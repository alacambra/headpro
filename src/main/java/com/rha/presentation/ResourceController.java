package com.rha.presentation;

import com.rha.control.CalendarEntriesGenerator;
import com.rha.control.CalendarPeriodsGenerator;
import com.rha.control.LocalDateConverter;
import com.rha.entity.PeriodTotal;
import com.rha.entity.PeriodWithValue;
import com.rha.entity.Step;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import javax.enterprise.event.Observes;
import static javax.enterprise.event.TransactionPhase.AFTER_SUCCESS;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.chart.BarChartModel;

public abstract class ResourceController<K, V extends PeriodWithValue> implements Serializable {

    @Inject
    transient Logger logger;

    @Inject
    CalendarPeriodsGenerator calendarPeriodsGenerator;

    @Inject
    transient CalendarEntriesGenerator calendarEntriesGenerator;

    List<LocalDate[]> periods;
    List<ResourcesRow<K, V>> resourcesRows;
    List<PeriodTotal> totalResources;
    BarChartModel resourcesGraph;
    LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    LocalDate endDate = LocalDate.now().plusMonths(3).with(TemporalAdjusters.lastDayOfMonth());
    Step step = Step.BIWEEK;

    public void loadResourcesForPeriod() {

        List<V> availableResources = getResourcesInPeriod();
        List<K> emptyKeys = getKeysWithoutValues();

        final Map<K, List<V>> resourcesByKey
                = availableResources.stream().collect(groupingBy(this::collectResourceByKey));

        emptyKeys.stream().forEach(servcie -> {
            resourcesByKey.putIfAbsent(servcie, new ArrayList<>());
        });

        resourcesRows = new ArrayList<>();

        if (periods == null) {
            loadPeriods();
        }

        resourcesRows = resourcesByKey.keySet().stream()
                .map(key -> generateRow(key, resourcesByKey))
                .collect(toList());
    }

    ResourcesRow<K, V> generateRow(K key, Map<K, List<V>> resourcesByKey) {
        Supplier<V> supplier = getResourceSupplierForKey(key);
        List<V> resources
                = calendarEntriesGenerator.getCalendarEntries(resourcesByKey.get(key), periods, supplier);
        ResourcesRow<K, V> row = new ResourcesRow<>(resources, key);
        row.setRowIsActive(rowIsActive(key));
        return row;
    }

    protected boolean rowIsActive(K key) {
        return false;
    }

    protected abstract List<V> getResourcesInPeriod();

    protected abstract List<K> getKeysWithoutValues();

    protected abstract K collectResourceByKey(V value);

    protected abstract Supplier<V> getResourceSupplierForKey(K key);

    private void loadPeriods() {
        periods = calendarPeriodsGenerator
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStep(step)
                .generatePeriods();
    }

    protected void resetValues() {
        resourcesRows = null;
        periods = null;
        totalResources = null;
        resourcesGraph = null;
    }

    public List<ResourcesRow<K, V>> getResourceRows() {
        if (resourcesRows == null) {
            loadResourcesForPeriod();
        }

        return resourcesRows;
    }

    public List<String> getPeriods() {

        if (periods == null) {
            loadPeriods();
        }

        if (step == Step.WEEK) {
            return periods.stream().map(period -> "CW" + Utils.getCalenderWeekOf(period[0]) + " (" + Utils.defaultDateFormat(period[0]) + ")").collect(toList());
        } else {
            return periods.stream().map(period -> Utils.defaultDateFormat(period[0])).collect(toList());
        }
    }

    protected abstract void updateOrCreateResource(List<V> resource);

    public void onCellEdit(CellEditEvent event) {

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        FacesContext context = FacesContext.getCurrentInstance();
        ResourcesRow entity
                = context.getApplication().evaluateExpressionGet(context, "#{resources}", ResourcesRow.class);

        if (newValue != null && !newValue.equals(oldValue)) {

            updateOrCreateResource(entity.getResources());

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);

            resourcesGraph = null;
            totalResources = null;
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cell not changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public BarChartModel getAreaModel() {

        if (resourcesGraph == null) {
            createResourcesGraph();
        }

        return resourcesGraph;
    }

    protected void createResourcesGraph() {
        resourcesGraph = new ResourcesGraph<K, V>()
                .setGraphTitle(getResourcesGraphTitle())
                .setPeriods(periods)
                .setResourcesRows(resourcesRows)
                .setTotalResources(totalResources)
                .setStep(step)
                .setLocale(Locale.GERMANY)
                .createResourcesGraph();
    }

    protected abstract String getResourcesGraphTitle();

    protected abstract List<PeriodTotal> getTotalResourcesInPeriod();

    public List<List<PeriodTotal>> getTotalResources() {

        if (totalResources == null) {
            List<PeriodTotal> values = getTotalResourcesInPeriod();

            if (periods == null) {
                loadPeriods();
            }

            totalResources = calendarEntriesGenerator.getCalendarEntries(values, periods, PeriodTotal::new);

        }

        List<List<PeriodTotal>> r = new ArrayList();
        r.add(totalResources);
        logger.log(Level.FINE, totalResources.toString());

        return r;
    }

    public void updateResources(@Observes(during = AFTER_SUCCESS) ProjectEvent projectEvent) {
        resetValues();
    }

    public void updateResources(@Observes(during = AFTER_SUCCESS) ServiceEvent serviceEvent) {
        resetValues();
    }

    public Date getStartDate() {
        return LocalDateConverter.toDate(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = LocalDateConverter.toLocalDate(startDate);
    }

    public Date getEndDate() {
        return LocalDateConverter.toDate(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = LocalDateConverter.toLocalDate(endDate);
    }

    public void dateChanged() {
        resetValues();
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public List<Step> getSteps() {
        return Arrays.asList(Step.values());
    }

    protected void ShowError(Exception e) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getClass().getSimpleName(), e.getMessage());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}

package com.rha.presentation;

import com.rha.control.CalendarEntriesGenerator;
import com.rha.entity.PeriodTotal;
import com.rha.entity.PeriodWithValue;
import com.rha.entity.Step;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
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
    transient CalendarEntriesGenerator calendarEntriesGenerator;

    List<ResourcesRow<K, V>> resourcesRows;
    List<PeriodTotal> totalResources;
    BarChartModel resourcesGraph;

    @Inject
    PeriodController periodController;

    public void loadResourcesForPeriod() {

        List<V> availableResources = getResourcesInPeriod();
        List<K> emptyKeys = getKeysWithoutValues();

        final Map<K, List<V>> resourcesByKey
                = availableResources.stream().collect(groupingBy(this::collectResourceByKey));

        emptyKeys.stream().forEach(servcie -> {
            resourcesByKey.putIfAbsent(servcie, new ArrayList<>());
        });

        resourcesRows = new ArrayList<>();

        resourcesRows = resourcesByKey.keySet().stream()
                .map(key -> generateRow(key, resourcesByKey))
                .collect(toList());
    }
    
    ResourcesRow<K, V> generateRow(K key, Map<K, List<V>> resourcesByKey) {
        Supplier<V> supplier = getResourceSupplierForKey(key);
        List<V> resources
                = calendarEntriesGenerator.getCalendarEntries(resourcesByKey.get(key), periodController.getPeriods(), supplier);
        ResourcesRow<K, V> row = new ResourcesRow<>(resources, key);
        row.setTitle(getKeyDisplayName().apply(key));
        row.setRowIsActive(rowIsActive(key));
        return row;
    }
    
    protected abstract Function<K, String> getKeyDisplayName();

    protected boolean rowIsActive(K key) {
        return false;
    }

    protected abstract List<V> getResourcesInPeriod();

    protected abstract List<K> getKeysWithoutValues();

    protected abstract K collectResourceByKey(V value);

    protected abstract Supplier<V> getResourceSupplierForKey(K key);

    protected void resetValues(@Observes PeriodChangedEvent event) {
        resourcesRows = null;
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

        if (periodController.getStep() == Step.WEEK) {
            return periodController.getPeriods().stream().map(period -> "CW" + Utils.getCalenderWeekOf(period[0]) + " (" + Utils.defaultDateFormat(period[0]) + ")").collect(toList());
        } else {
            return periodController.getPeriods().stream().map(period -> Utils.defaultDateFormat(period[0])).collect(toList());
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
            createResourcesChart();
        }

        return resourcesGraph;
    }

    protected void createResourcesChart() {
        resourcesGraph = new ResourcesChart<K, V>()
                .setGraphTitle(getResourcesGraphTitle())
                .setPeriods(periodController.getPeriods())
                .setResourcesRows(resourcesRows)
                .setTotalResources(totalResources)
                .setStep(periodController.getStep())
                .setLocale(Locale.GERMANY)
//                .setExetender("ext")
                .createResourcesGraph();
    }

    protected abstract String getResourcesGraphTitle();

    protected abstract List<PeriodTotal> getTotalResourcesInPeriod();

    public List<List<PeriodTotal>> getTotalResources() {

        if (totalResources == null) {
            List<PeriodTotal> values = getTotalResourcesInPeriod();
            totalResources = calendarEntriesGenerator.getCalendarEntries(values, periodController.getPeriods(), PeriodTotal::new);

        }

        List<List<PeriodTotal>> r = new ArrayList();
        r.add(totalResources);
        logger.log(Level.FINE, totalResources.toString());

        return r;
    }

    public void updateResources(@Observes(during = AFTER_SUCCESS) ProjectEvent projectEvent) {
        resetValues(null);
    }

    public void updateResources(@Observes(during = AFTER_SUCCESS) ServiceEvent serviceEvent) {
        resetValues(null);
    }

    public void dateChanged() {
        resetValues(null);
    }

    public List<Step> getSteps() {
        return Arrays.asList(Step.values());
    }

    protected void ShowError(Exception e) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getClass().getSimpleName(), e.getMessage());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}

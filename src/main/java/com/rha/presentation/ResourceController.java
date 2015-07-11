package com.rha.presentation;

import com.rha.boundary.AvailableResourceFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.control.CalendarEntriesGenerator;
import com.rha.control.CalendarPeriodsGenerator;
import com.rha.control.LocalDateConverter;
import com.rha.entity.BookedResource;
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
    AvailableResourceFacade availableResourceFacade;

    @Inject
    ServiceFacade serviceFacade;

    @Inject
    CalendarPeriodsGenerator calendarPeriodsGenerator;

    @Inject
    transient CalendarEntriesGenerator calendarEntriesGenerator;

    List<LocalDate[]> periods;
    List<ResourcesRow<K, V>> resourceRow;
    List<PeriodTotal> totalResources;
    BarChartModel barModel;
    LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    LocalDate endDate = LocalDate.now().plusMonths(3).with(TemporalAdjusters.lastDayOfMonth());
    Step step = Step.BIWEEK;

    public void loadAvailableResourcesForPeriod() {

        List<V> availableResources = getResourcesInPeriod();
        List<K> emptyKeys = getKeysWithoutValues();

        final Map<K, List<V>> resourcesByKey
                = availableResources.stream().collect(groupingBy(this::collectResourceByKey));

        emptyKeys.stream().forEach(servcie -> {
            resourcesByKey.putIfAbsent(servcie, new ArrayList<>());
        });

        resourceRow = new ArrayList<>();

        if (periods == null) {
            loadPeriods();
        }

        for (K key : resourcesByKey.keySet()) {

            Supplier<V> supplier = getResourceSupplierForKey(key);

            List<V> resources = 
                    calendarEntriesGenerator.getCalendarEntries(resourcesByKey.get(key), periods, supplier);

            resourceRow.add(new ResourcesRow(resources, key));
        }
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

    private void resetValues() {
        resourceRow = null;
        periods = null;
        totalResources = null;
        barModel = null;
    }

    public List<ResourcesRow<K, V>> getResourceRows() {
        if (resourceRow == null) {
            loadAvailableResourcesForPeriod();
        }

        return resourceRow;
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

    private LocalDate getDate(BookedResource br) {
        return br.getStartDate();
    }

    public void onCellEdit(CellEditEvent event) {

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        FacesContext context = FacesContext.getCurrentInstance();
        ResourcesRow entity
                = context.getApplication().evaluateExpressionGet(context, "#{availableResource}", ResourcesRow.class);

        if (newValue != null && !newValue.equals(oldValue)) {

            availableResourceFacade.updateOrCreateBookings(entity.getResources());

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);

            barModel = null;
            totalResources = null;
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cell not changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public BarChartModel getAreaModel() {

        if (barModel == null) {
            createAreaModel();
        }

        return barModel;
    }

    private void createAreaModel() {
//        barModel = new BarChartModel();
//        ChartSeries total = new ChartSeries();
//        total.setLabel("Estimation of required work resources");
//
//        int size = resourceRow.size() * periods.size();
//
//        if (size < 1200) {
//
//            resourceRow.stream().forEach(row -> {
//
//                ChartSeries chartSerie = new ChartSeries();
//                chartSerie.setLabel(row.getKey().getName());
//
//                row.getResources().stream().forEach(availableResource -> {
//                    
//                    String columnName = "";
//                    
//                    if(step == Step.WEEK){
//                        WeekFields fields = WeekFields.of(Locale.GERMANY);
//                        int kw = availableResource.getStartDate().get(fields.weekOfYear());
//                        columnName = "CW" + kw;
//                    }else{
//                        columnName = Utils.defaultDateFormat(availableResource.getStartDateAsDate());
//                    }
//                    long available = Optional.ofNullable(availableResource.getAvailable()).orElse(0L);
//                    Double d = 100D;
//                    chartSerie.set(columnName, available);
//                });
//
//                barModel.addSeries(chartSerie);
//            });
//        } else {
//            ChartSeries chartSerie = new ChartSeries();
//            chartSerie.setLabel("total");
//
//            int i = 0;
//            for (PeriodTotal value : totalResources) {
//                chartSerie.set(Utils.defaultDateFormat(value.getStartDateAsDate()), value.getTotal());
//            }
//            barModel.addSeries(chartSerie);
//        }
//
//        barModel.setTitle("Available resources");
//        barModel.setLegendPosition("ne");
//        barModel.setStacked(true);
//        barModel.setShowPointLabels(true);
//        barModel.setZoom(true);
//
//        Axis xAxis = new CategoryAxis("Period (" + step.name().toLowerCase() + ")");
//        xAxis.setTickAngle(90);
//
//        barModel.getAxes().put(AxisType.X, xAxis);
//        Axis yAxis = barModel.getAxis(AxisType.Y);
//
//        yAxis.setLabel("Resources (hours)");
//        yAxis.setMin(0);
    }

    public List<List<PeriodTotal>> getTotalAvailability() {

        if (totalResources == null) {
            List<PeriodTotal> values
                    = availableResourceFacade.getTotalAvailableResourcesInPeriod(startDate, endDate);

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
}

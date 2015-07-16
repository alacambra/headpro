package com.rha.presentation;

import com.rha.boundary.ResultsFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.control.CalendarEntriesGenerator;
import com.rha.control.CalendarPeriodsGenerator;
import com.rha.control.LocalDateConverter;
import com.rha.control.PeriodComparator;
import com.rha.control.WrappedMuttableValue;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.PeriodWithValue;
import com.rha.entity.RemainingResource;
import com.rha.entity.Service;
import com.rha.entity.Step;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.logging.Logger;
import static java.util.stream.Collectors.*;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;

@SessionScoped
@Named
public class ResultsController implements Serializable {

    @Inject
    transient Logger logger;

    @Inject
    transient ResultsFacade resultsFacade;

    @Inject
    ServiceFacade serviceFacade;

    @Inject
    CalendarPeriodsGenerator calendarPeriodsGenerator;

    @Inject
    transient CalendarEntriesGenerator calendarEntriesGenerator;

    List<LocalDate[]> periods;
    List<ResultRow> resultRows;
    List<PeriodTotal> totalBooking;
    BarChartModel barModel;
    LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    LocalDate endDate = LocalDate.now().plusMonths(3).with(TemporalAdjusters.lastDayOfMonth());
    Step step = Step.BIWEEK;
    private boolean disableCache = true;

    public void loadAvailableResourcesForPeriod() {

        Map<Service, List<PeriodWithValue>> remainingResources
                = resultsFacade.getWeighedRemainingResourcesByService(startDate, endDate);

        List<Service> emptyServices = serviceFacade.findAll();

        emptyServices.stream().forEach(servcie -> {
            remainingResources.putIfAbsent(servcie, new ArrayList<>());
        });

        resultRows = new ArrayList<>();

        if (periods == null) {
            loadPeriods();
        }

        for (Service service : remainingResources.keySet()) {

            Supplier<PeriodWithValue> supplier = () -> {
                RemainingResource ar = new RemainingResource();
                ar.setService(service);
                return ar;
            };

            BinaryOperator<PeriodWithValue> reducer = (p1, p2) -> {
                if (p1 == null) {
                    return p2;
                }
                p1.setValue(p1.getValue() + p2.getValue());
                return p1;
            };

            Map<LocalDate, PeriodWithValue> p = remainingResources.get(service).stream()
                    .collect(
                            groupingBy(
                                    PeriodWithValue::getStartDate,
                                    reducing(null, reducer)
                            )
                    );

            remainingResources.put(service, p.values().stream().sorted(new PeriodComparator()).collect(toList()));

            List<PeriodWithValue> resources = calendarEntriesGenerator
                    .getCalendarEntries(remainingResources.get(service), periods, supplier);

            resultRows.add(new ResultRow(resources, service));
        }
    }

    private void loadPeriods() {
        periods = calendarPeriodsGenerator
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStep(step)
                .generatePeriods();
    }

    private void resetValues() {
        resultRows = null;
        periods = null;
        totalBooking = null;
        barModel = null;
    }

    public List<ResultRow> getAvailableResourceRows() {
        if (resultRows == null) {
            loadAvailableResourcesForPeriod();
        }

        return resultRows;
    }

    public List<LocalDate> getPeriods() {

        if (periods == null) {
            loadPeriods();
        }

        return periods.stream().map(period -> period[0]).collect(toList());
    }

    private LocalDate getDate(BookedResource br) {
        return br.getStartDate();
    }

    public BarChartModel getAreaModel() {

        if (barModel == null || disableCache) {
            loadAvailableResourcesForPeriod();
            createAreaModel();
        }

        return barModel;
    }

    private void createAreaModel() {
        barModel = new BarChartModel();
        ChartSeries total = new ChartSeries();
        total.setLabel("Estimation of required work resources");

        int size = resultRows.size() * periods.size();
        
        WrappedMuttableValue<Float> max = new WrappedMuttableValue<>(0f);
        WrappedMuttableValue<Float> min = new WrappedMuttableValue<>(0f);
        

        if (size < 1200) {

            resultRows.stream().forEach(row -> {

                ChartSeries chartSerie = new ChartSeries();
                chartSerie.setLabel(row.getService().getName());

                row.getResources().stream().forEach(remainingresource -> {
                    float remainingResources = Optional.ofNullable(remainingresource.getValue()).orElse(0f);
                    if(remainingResources < min.get()) min.set(remainingResources);
                    if(remainingResources > max.get()) max.set(remainingResources);
                    
                    String columnName;
                     if(step == Step.WEEK){
                        WeekFields fields = WeekFields.of(Locale.GERMANY);
                        int kw = remainingresource.getStartDate().get(fields.weekOfYear());
                        columnName = "CW" + kw;
                    }else{
                        columnName = Utils.defaultDateFormat(LocalDateConverter.toDate(remainingresource.getStartDate()));
                    }
                    
                    chartSerie.set(columnName, remainingResources);
                });

                barModel.addSeries(chartSerie);
            });
        } else {
            ChartSeries chartSerie = new ChartSeries();
            chartSerie.setLabel("total");

            int i = 0;
            totalBooking.stream().forEach((booking) -> {
                chartSerie.set(booking.getStartDate(), booking.getTotal());
            });
            barModel.addSeries(chartSerie);
        }

        barModel.setTitle("Remaining resources");
        barModel.setLegendPosition("ne");
        barModel.setStacked(false);
        barModel.setShowPointLabels(true);
        barModel.setZoom(true);

        Axis xAxis = new CategoryAxis("Period (" + step.name().toLowerCase() + ")");
        xAxis.setTickAngle(0);

        barModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = barModel.getAxis(AxisType.Y);

        yAxis.setLabel("Resources (hours)");
        yAxis.setMin(Math.round(min.get() * 1.1));
        yAxis.setMax(Math.round(max.get() * 1.1));
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

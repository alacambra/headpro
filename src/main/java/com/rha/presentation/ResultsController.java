package com.rha.presentation;

import com.rha.boundary.AvailableResourceFacade;
import com.rha.boundary.RequiredResourceFacade;
import com.rha.boundary.ResultsFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.control.CalendarEntriesGenerator;
import com.rha.control.CalendarPeriodsGenerator;
import com.rha.control.LocalDateConverter;
import com.rha.control.PeriodComparator;
import com.rha.entity.AvailableResource;
import com.rha.entity.RequiredResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.PeriodWithValue;
import com.rha.entity.RemainingResource;
import com.rha.entity.Service;
import com.rha.entity.Step;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
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
    transient AvailableResourceFacade availableResourceFacade;

    @Inject
    transient RequiredResourceFacade bookedResourceFacade;

    @Inject
    transient ServiceFacade serviceFacade;

    @Inject
    transient CalendarPeriodsGenerator calendarPeriodsGenerator;

    @Inject
    transient CalendarEntriesGenerator calendarEntriesGenerator;

    @Inject
    PeriodController periodController;

    List<ResourcesRow<Service, PeriodWithValue>> resultRows;
    List<PeriodTotal> totalResources;
    BarChartModel resourcesChart;
    BarChartModel chart2;
    BarChartModel chartTotalDifference;

    public void loadAvailableVsRequiredResources() {

        List<AvailableResource> availableResources
                = availableResourceFacade.getAvailableResourcesInPeriod(periodController.getLocalStartDate(), periodController.getLocalEndDate());

        List<RequiredResource> bookedResources
                = bookedResourceFacade.getBookedResourcesInPeriod(periodController.getLocalStartDate(), periodController.getLocalEndDate());

        List<AvailableResource> res = calendarEntriesGenerator
                .getCalendarEntries(availableResources, periodController.getPeriods(), () -> {
                    AvailableResource ar = new AvailableResource();
                    ar.setPersisted(false);
                    return ar;
                });

        Map<LocalDate, Float> allAvailableResources = res.stream().collect(
                groupingBy(
                        PeriodWithValue::getStartDate,
                        mapping(PeriodWithValue::getValue,
                                reducing(0f, Float::sum))
                )
        );

        bookedResources = calendarEntriesGenerator
                .getCalendarEntries(bookedResources, periodController.getPeriods(), () -> {
                    RequiredResource ar = new RequiredResource();
                    ar.setPersisted(false);
                    return ar;
                });

        Map<LocalDate, Float> allBookedResources = bookedResources.stream().collect(
                groupingBy(
                        PeriodWithValue::getStartDate,
                        mapping(PeriodWithValue::getValue,
                                reducing(0f, Float::sum))
                )
        );

        chart2 = new BarChartModel();
        chart2.setLegendPosition("ne");
        chart2.setExtender("ext");
        ChartSeries available = new BarChartSeries();
        ChartSeries required = new BarChartSeries();

        available.setLabel("Available");
        required.setLabel("Required");

        periodController.getPeriods().stream().map(period -> period[0]).forEach(date -> {
            available.set(Utils.defaultDateFormat(
                    LocalDateConverter.toDate(date)),
                    allAvailableResources.get(date));

            required.set(Utils.defaultDateFormat(
                    LocalDateConverter.toDate(date)),
                    allBookedResources.get(date));
        });

        chart2.addSeries(available);
        chart2.addSeries(required);
        chart2.setTitle("Available vs required");
    }

    public void loadTotalDifferenceResources() {

        List<AvailableResource> availableResources
                = availableResourceFacade.getAvailableResourcesInPeriod(periodController.getLocalStartDate(), periodController.getLocalEndDate());

        List<RequiredResource> bookedResources
                = bookedResourceFacade.getBookedResourcesInPeriod(periodController.getLocalStartDate(), periodController.getLocalEndDate());

        List<AvailableResource> res = calendarEntriesGenerator
                .getCalendarEntries(availableResources, periodController.getPeriods(), () -> {
                    AvailableResource ar = new AvailableResource();
                    ar.setPersisted(false);
                    return ar;
                });

        Map<LocalDate, Float> allAvailableResources = res.stream().collect(
                groupingBy(
                        PeriodWithValue::getStartDate,
                        mapping(PeriodWithValue::getValue,
                                reducing(0f, Float::sum))
                )
        );

        bookedResources = calendarEntriesGenerator
                .getCalendarEntries(bookedResources, periodController.getPeriods(), () -> {
                    RequiredResource ar = new RequiredResource();
                    ar.setPersisted(false);
                    return ar;
                });

        Map<LocalDate, Float> allBookedResources = bookedResources.stream().collect(
                groupingBy(
                        PeriodWithValue::getStartDate,
                        mapping(PeriodWithValue::getValue,
                                reducing(0f, Float::sum))
                )
        );

        chartTotalDifference = new BarChartModel();
        chartTotalDifference.setTitle("Remaining resources");
        chartTotalDifference.setExtender("ext");
        ChartSeries remaining = new BarChartSeries();

        periodController.getPeriods().stream().map(period -> period[0]).forEach(date -> {
            remaining.set(Utils.defaultDateFormat(
                    LocalDateConverter.toDate(date)),
                    allAvailableResources.get(date) - allBookedResources.get(date));

        });

        chartTotalDifference.addSeries(remaining);
    }

    public BarChartModel getChart2() {
        if (chart2 == null) {
            loadAvailableVsRequiredResources();
        }
        return chart2;
    }

    public BarChartModel getChartTotalDifference() {
        if (chartTotalDifference == null) {
            loadTotalDifferenceResources();
        }
        return chartTotalDifference;
    }

    public void loadAvailableResourcesForPeriod() {

        Map<Service, List<PeriodWithValue>> remainingResources
                = resultsFacade.getWeighedRemainingResourcesByService(periodController.getLocalStartDate(), periodController.getLocalEndDate());

        List<Service> emptyServices = serviceFacade.findAll();

        emptyServices.stream().forEach(servcie -> {
            remainingResources.putIfAbsent(servcie, new ArrayList<>());
        });

        resultRows = new ArrayList<>();

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
                    .getCalendarEntries(remainingResources.get(service), periodController.getPeriods(), supplier);

            ResourcesRow resourcesRow = new ResourcesRow<>(resources, service);
            resourcesRow.setTitle(service.getName());
            resultRows.add(resourcesRow);
        }
    }

    private void resetValues(@Observes PeriodChangedEvent event) {
        resultRows = null;
        totalResources = null;
        resourcesChart = null;
        chart2 = null;
        chartTotalDifference = null;
    }

    public List<ResourcesRow<Service, PeriodWithValue>> getAvailableResourceRows() {
        if (resultRows == null) {
            loadAvailableResourcesForPeriod();
        }

        return resultRows;
    }

    public List<LocalDate> getPeriods() {
        return periodController.getPeriods().stream().map(period -> period[0]).collect(toList());
    }

    private LocalDate getDate(RequiredResource br) {
        return br.getStartDate();
    }

    public BarChartModel getAreaModel() {

        if (resourcesChart == null) {
            getChart3();
//            loadAvailableResourcesForPeriod();
//            createAreaModel();
//            resourcesChart = new ResourcesChart<Service, PeriodWithValue>()
//                    .setGraphTitle("Remaining resources")
//                    .setPeriods(periodController.getPeriods())
//                    .setResourcesRows(resultRows)
//                    .setTotalResources(totalResources)
//                    .setStep(step)
//                    .setLocale(Locale.GERMANY)
//                    .setExetender("ext")
//                    .createResourcesGraph();
//
//            resourcesChart.setStacked(false);
        }

        return resourcesChart;
    }

    public BarChartModel getAreaModel2() {

//        Map<Service, Float> res = resultsFacade.getWeighedRemainingResourcesByService2(periodController.getLocalStartDate(), periodController.getLocalEndDate());
//        resourcesChart = new BarChartModel();
//        
//        res.entrySet().forEach(r -> {
//            
//            ChartSeries chartSeries = new BarChartSeries();
//            chartSeries.setLabel(r.getKey().getName());
//            chart
//        });
        return resourcesChart;
    }

    private void getChart3() {

        resourcesChart = new BarChartModel();

        Map<Service, Map<LocalDate, Float>> resources
                = resultsFacade.getWeighedRemainingResourcesByService2(periodController.getLocalStartDate(), periodController.getLocalEndDate());

        resources.entrySet().forEach(serviceEntry -> {

            ChartSeries chartSeries = new BarChartSeries();
            periodController.getPeriods().stream().sorted((a, b) -> a[0].compareTo(b[0])).forEach(date -> chartSeries.set(date[0], 0));
            chartSeries.setLabel(serviceEntry.getKey().getName());
            Set<LocalDate> dates = periodController.getPeriods().stream().map(p -> p[0]).collect(Collectors.toSet());

            serviceEntry.getValue().entrySet().forEach(dateEntry -> {
                dates.remove(dateEntry.getKey());
                chartSeries.set(dateEntry.getKey(), dateEntry.getValue());
            });

            dates.stream().forEach(date -> {
                chartSeries.set(date, 0f);
            });

            resourcesChart.addSeries(chartSeries);
        });

        resourcesChart.setExtender("ext");
        resourcesChart.setTitle("Remaining resources per Project");
        resourcesChart.setLegendPosition("ne");
        resourcesChart.setStacked(true);
        resourcesChart.setShowPointLabels(true);

        Axis xAxis = new CategoryAxis("Period (" + periodController.getStep().name().toLowerCase() + ")");
        xAxis.setTickAngle(0);

        resourcesChart.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = resourcesChart.getAxis(AxisType.Y);

        yAxis.setLabel("Resources (hours)");

    }

    private void createAreaModel() {
        resourcesChart = new BarChartModel();
        int size = resultRows.size() * periodController.getPeriods().size();

        if (size < 1200) {

            resultRows.stream().forEach(row -> {

                ChartSeries chartSeriePositive = new ChartSeries();
                chartSeriePositive.setLabel(row.getKey().getName());

                row.getResources().stream().forEach(remainingresource -> {
                    float remainingResources = Optional.ofNullable(remainingresource.getValue()).orElse(0f);

                    String columnName;
                    if (periodController.getStep() == Step.WEEK) {
                        WeekFields fields = WeekFields.of(Locale.GERMANY);
                        int kw = remainingresource.getStartDate().get(fields.weekOfYear());
                        columnName = "CW" + kw;
                    } else {
                        columnName = Utils.defaultDateFormat(LocalDateConverter.toDate(remainingresource.getStartDate()));
                    }

                    chartSeriePositive.set(columnName, remainingResources);
                });

                resourcesChart.addSeries(chartSeriePositive);

            });
        } else {
            ChartSeries chartSerie = new ChartSeries();
            chartSerie.setLabel("total");

            totalResources.stream().forEach((booking) -> {
                chartSerie.set(booking.getStartDate(), booking.getTotal());
            });
            resourcesChart.addSeries(chartSerie);
        }

        resourcesChart.setExtender("ext");
        resourcesChart.setTitle("Remaining resources");
        resourcesChart.setLegendPosition("ne");
        resourcesChart.setStacked(true);
        resourcesChart.setShowPointLabels(true);

        Axis xAxis = new CategoryAxis("Period (" + periodController.getStep().name().toLowerCase() + ")");
        xAxis.setTickAngle(0);

        resourcesChart.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = resourcesChart.getAxis(AxisType.Y);

        yAxis.setLabel("Resources (hours)");
    }

    public List<Step> getSteps() {
        return Arrays.asList(Step.BIWEEK);
    }

}

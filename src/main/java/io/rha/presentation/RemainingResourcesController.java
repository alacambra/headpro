package io.rha.presentation;

import io.rha.boundary.AvailableResourceFacade;
import io.rha.boundary.RequiredResourceFacade;
import io.rha.boundary.RemainingResourcesFacade;
import io.rha.boundary.ServiceFacade;
import io.rha.control.CalendarEntriesGenerator;
import io.rha.control.CalendarPeriodsGenerator;
import io.rha.control.LocalDateConverter;
import io.rha.control.PeriodComparator;
import io.rha.entity.AvailableResource;
import io.rha.entity.RequiredResource;
import io.rha.entity.PeriodTotal;
import io.rha.entity.PeriodWithValue;
import io.rha.entity.RemainingResource;
import io.rha.entity.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class RemainingResourcesController implements Serializable {

    @Inject
    transient Logger logger;

    @Inject
    transient RemainingResourcesFacade remainingResourcesFacade;

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
    BarChartModel remainingByProjectChart;
    BarChartModel availableVsRequiredResourcesChart;
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

        //TODO: belongs to facade
        Map<LocalDate, Float> allBookedResources = bookedResources.stream().collect(
                groupingBy(
                        PeriodWithValue::getStartDate,
                        mapping(PeriodWithValue::getValue,
                                reducing(0f, Float::sum))
                )
        );

        availableVsRequiredResourcesChart = new BarChartModel();
        availableVsRequiredResourcesChart.setLegendPosition("ne");
        availableVsRequiredResourcesChart.setExtender("ext");
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

        availableVsRequiredResourcesChart.addSeries(available);
        availableVsRequiredResourcesChart.addSeries(required);
        availableVsRequiredResourcesChart.setTitle("Available vs required");
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

        //TODO: belongs to facade
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

        //TODO belongs to facade
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

    public BarChartModel getAvailableVsRequiredChart() {
        if (availableVsRequiredResourcesChart == null) {
            loadAvailableVsRequiredResources();
        }
        return availableVsRequiredResourcesChart;
    }

    public BarChartModel getChartTotalDifference() {
        if (chartTotalDifference == null) {
            loadTotalDifferenceResources();
        }
        return chartTotalDifference;
    }

    public void loadAvailableResourcesForPeriod() {

        Map<Service, List<PeriodWithValue>> remainingResources
                = remainingResourcesFacade.getWeighedRemainingResourcesByService(periodController.getLocalStartDate(), periodController.getLocalEndDate());

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

            //TODO belongs to facade
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
        remainingByProjectChart = null;
        availableVsRequiredResourcesChart = null;
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

    public BarChartModel getRemainingByProjectChart() {

        if (remainingByProjectChart == null) {
            loadRemainingByProjectChart();
        }

        return remainingByProjectChart;
    }

    private void loadRemainingByProjectChart() {

        remainingByProjectChart = new BarChartModel();

        Map<Service, Map<LocalDate, Float>> resources
                = remainingResourcesFacade.getWeighedRemainingResourcesByServiceAndDate(periodController.getLocalStartDate(), periodController.getLocalEndDate());

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

            remainingByProjectChart.addSeries(chartSeries);
        });

        remainingByProjectChart.setExtender("ext");
        remainingByProjectChart.setTitle("Remaining resources per Project");
        remainingByProjectChart.setLegendPosition("ne");
        remainingByProjectChart.setStacked(true);
        remainingByProjectChart.setShowPointLabels(true);

        Axis xAxis = new CategoryAxis("Period (" + periodController.getStep().name().toLowerCase() + ")");
        xAxis.setTickAngle(0);

        remainingByProjectChart.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = remainingByProjectChart.getAxis(AxisType.Y);

        yAxis.setLabel("Resources (hours)");

    }
}

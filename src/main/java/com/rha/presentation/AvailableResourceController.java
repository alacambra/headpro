package com.rha.presentation;

import com.rha.boundary.AvailableResourceFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.control.CalendarEntriesGenerator;
import com.rha.control.CalendarPeriodsGenerator;
import com.rha.control.LocalDateConverter;
import com.rha.entity.AvailableResource;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Service;
import com.rha.entity.Step;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;

@Named("arc")
@SessionScoped
public class AvailableResourceController implements Serializable {

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
    List<AvailableResourceRow> availableResourceRow;
    List<PeriodTotal> totalBooking;
    BarChartModel barModel;
    LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    LocalDate endDate = LocalDate.now().plusMonths(3).with(TemporalAdjusters.lastDayOfMonth());
    Step step = Step.BIWEEK;
    private boolean disableCache = false;

    public void loadAvailableResourcesForPeriod() {

        List<AvailableResource> availableResources
                = availableResourceFacade.getAvailableResourcesInPeriod(startDate, endDate);

        List<Service> emptyServices = serviceFacade.findAll();

        final Map<Service, List<AvailableResource>> resourcesByService
                = availableResources.stream().collect(groupingBy(ar -> ar.getService()));

        emptyServices.stream().forEach(servcie -> {
            resourcesByService.putIfAbsent(servcie, new ArrayList<>());
        });

        availableResourceRow = new ArrayList<>();

        if (periods == null) {
            loadPeriods();
        }

        for (Service service : resourcesByService.keySet()) {

            Supplier<AvailableResource> supplier = () -> {
                AvailableResource ar = new AvailableResource();
                ar.setPersisted(false);
                ar.setService(service);
                return ar;
            };

            List<AvailableResource> resources = calendarEntriesGenerator
                    .getCalendarEntries(resourcesByService.get(service), periods, supplier);

            availableResourceRow.add(new AvailableResourceRow(resources, service));
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
        availableResourceRow = null;
        periods = null;
        totalBooking = null;
        barModel = null;
    }

    public List<AvailableResourceRow> getAvailableResourceRows() {
        if (availableResourceRow == null || disableCache) {
            loadAvailableResourcesForPeriod();
        }

        return availableResourceRow;
    }

    public List<LocalDate> getPeriods() {

        if (periods == null || disableCache) {
            loadPeriods();
        }

        return periods.stream().map(period -> period[0]).collect(toList());
    }

    private LocalDate getDate(BookedResource br) {
        return br.getStartDate();
    }

    public void onCellEdit(CellEditEvent event) {

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        FacesContext context = FacesContext.getCurrentInstance();
        AvailableResourceRow entity = 
                context.getApplication().evaluateExpressionGet(context, "#{availableResource}", AvailableResourceRow.class);

        if (newValue != null && !newValue.equals(oldValue)) {

            availableResourceFacade.updateOrCreateBookings(entity.getResources());

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);

            barModel = null;
            totalBooking = null;
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cell not changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public BarChartModel getAreaModel() {

        if (barModel == null || disableCache) {
            createAreaModel();
        }

        return barModel;
    }

    private void createAreaModel() {
        barModel = new BarChartModel();
        ChartSeries total = new ChartSeries();
        total.setLabel("Estimation of required work resources");

        int size = availableResourceRow.size() * periods.size();

        if (size < 1200) {

            availableResourceRow.stream().forEach(row -> {

                ChartSeries chartSerie = new ChartSeries();
                chartSerie.setLabel(row.getService().getName());

                row.getResources().stream().forEach(b -> {
                    int position = Optional.ofNullable(b.getPosition()).orElse(chartSerie.getData().size());
                    long available = Optional.ofNullable(b.getAvailable()).orElse(0L);
                    chartSerie.set(position + 1, available);
                });

                barModel.addSeries(chartSerie);
            });
        } else {
            ChartSeries chartSerie = new ChartSeries();
            chartSerie.setLabel("total");

            int i = 0;
            for (PeriodTotal value : totalBooking) {
                chartSerie.set(value.getStartDate(), value.getTotal());
            }
            barModel.addSeries(chartSerie);
        }

        barModel.setTitle("Available resources");
        barModel.setLegendPosition("ne");
        barModel.setStacked(true);
        barModel.setShowPointLabels(true);
        barModel.setZoom(true);

        Axis xAxis = new CategoryAxis("Period (" + step.name().toLowerCase() + ")");
        xAxis.setTickAngle(90);

        barModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = barModel.getAxis(AxisType.Y);

        yAxis.setLabel("Resources (hours)");
        yAxis.setMin(0);
    }

    public List<List<PeriodTotal>> getTotalAvailability() {

        if (totalBooking == null || disableCache) {
            List<PeriodTotal> values
                    = availableResourceFacade.getTotalAvailableResourcesInPeriod(startDate, endDate);

            totalBooking = calendarEntriesGenerator.getCalendarEntries(values, periods, PeriodTotal::new);

        }

        List<List<PeriodTotal>> r = new ArrayList();
        r.add(totalBooking);
        logger.log(Level.FINE, totalBooking.toString());

        return r;
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

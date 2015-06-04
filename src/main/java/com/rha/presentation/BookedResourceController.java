package com.rha.presentation;

import com.rha.control.CalendarEntriesGenerator;
import com.rha.control.CalendarPeriodsGenerator;
import com.rha.boundary.BookedResourceFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.boundary.ProjectFacade;
import com.rha.control.LocalDateConverter;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Project;
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
import static java.util.stream.Collectors.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author alacambra
 */
@SessionScoped
@Named("brc")
public class BookedResourceController implements Serializable {

    @Inject
    transient Logger logger;

    @Inject
    BookedResourceFacade bookedResourceFacade;

    @Inject
    ProjectFacade projectFacade;

    @Inject
    ServiceFacade serviceFacade;

    @Inject
    CalendarPeriodsGenerator calendarPeriodsGenerator;

    @Inject
    transient CalendarEntriesGenerator calendarEntriesGenerator;

    List<LocalDate[]> periods;
    List<BookingRow> bookingRows;
    List<PeriodTotal> totalBooking;
    BarChartModel barModel;
    LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    LocalDate endDate = LocalDate.now().plusMonths(3).with(TemporalAdjusters.lastDayOfMonth());
    Step step = Step.BIWEEK;

    @ManagedProperty(value = "param.selectedService")
    Service currentService;

    @PostConstruct
    public void init() {
    }

    public void loadBookedResourcesForPeriod() {

        if (currentService == null) {
            throw new RuntimeException("No service given");
        }

        List<BookedResource> bookedResources
                = bookedResourceFacade.getBookedResourcesForServiceInPeriod(currentService, startDate, endDate);

        List<Project> emptyProjects = projectFacade.findAll();

        final Map<Project, List<BookedResource>> resourcesByProject
                = bookedResources.stream().collect(groupingBy(br -> br.getProject()));

        emptyProjects.stream().forEach(pr -> {
            resourcesByProject.putIfAbsent(pr, new ArrayList<>());
        });

        bookingRows = new ArrayList<>();

        if (periods == null) {
            loadPeriods();
        }

        for (Project project : resourcesByProject.keySet()) {

            Supplier<BookedResource> supplier = () -> {
                BookedResource br = new BookedResource();
                br.setPersisted(false);
                br.setProject(project);
                br.setService(currentService);
                return br;
            };

            List<BookedResource> resources = calendarEntriesGenerator
                    .getCalendarEntries(resourcesByProject.get(project), periods, supplier);

            bookingRows.add(new BookingRow(
                    project,
                    resources,
                    serviceFacade.find(1)));
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
        bookingRows = null;
        periods = null;
        totalBooking = null;
        barModel = null;
    }

    public List<BookingRow> getBookingRow() {
        if (bookingRows == null) {
            loadBookedResourcesForPeriod();
        }

        return bookingRows;
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

    public void onCellEdit(CellEditEvent event) {

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        FacesContext context = FacesContext.getCurrentInstance();
        BookingRow entity = context.getApplication().evaluateExpressionGet(context, "#{booking}", BookingRow.class);

        if (newValue != null && !newValue.equals(oldValue)) {

            bookedResourceFacade.updateOrCreateBookings(entity.getResources());

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

        if (barModel == null) {
            createAreaModel();
        }

        return barModel;
    }

    private void createAreaModel() {
        barModel = new BarChartModel();
        ChartSeries total = new ChartSeries();
        total.setLabel("Estimation of required work resources");

        int size = bookingRows.size() * periods.size();

        if (size < 1200) {

            bookingRows.stream().forEach(row -> {

                ChartSeries chartSerie = new ChartSeries();
                chartSerie.setLabel(row.getProject().getName());

                row.getResources().stream().forEach(b -> {
                    int position = Optional.ofNullable(b.getPosition()).orElse(chartSerie.getData().size());
                    long booked = Optional.ofNullable(b.getBooked()).orElse(0L);
                    chartSerie.set(position + 1, booked);
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

        barModel.setTitle("Resources booked for service " + currentService.getName());
        barModel.setLegendPosition("ne");
        barModel.setStacked(true);
        barModel.setShowPointLabels(true);
        barModel.setZoom(true);

        Axis xAxis = new CategoryAxis("Month");
        xAxis.setTickAngle(90);

        barModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = barModel.getAxis(AxisType.Y);

        yAxis.setLabel("Resources");
        yAxis.setMin(0);
    }

    public List<List<PeriodTotal>> getTotalBooking() {

        if (totalBooking == null) {
            List<PeriodTotal> values
                    = bookedResourceFacade.getTotalBookedResourcesForServiceInPeriod(currentService, startDate, endDate);

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

    public Service getCurrentService() {
        return currentService;
    }

    public void setCurrentService(Service currentService) {
        if(currentService.equals(this.currentService)){
            return;
        }
        
        this.currentService = currentService;
        resetValues();
    }
    
}

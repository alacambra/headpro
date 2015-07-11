package com.rha.presentation;

import com.rha.boundary.BookedResourceFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.boundary.ProjectFacade;
import com.rha.control.WrappedMuttableValue;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Project;
import com.rha.entity.Service;
import com.rha.entity.Step;
import java.io.Serializable;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;

@SessionScoped
@Named("brc")
public class BookedResourceController extends ResourceController<Project, BookedResource> implements Serializable {

    @Inject
    transient Logger logger;

    @Inject
    BookedResourceFacade bookedResourceFacade;

    @Inject
    ProjectFacade projectFacade;

    @Inject
    ServiceFacade serviceFacade;

    @ManagedProperty(value = "param.selectedService")
    Service currentService;

    @PostConstruct
    public void init() {
    }

    @Override
    protected List<BookedResource> getResourcesInPeriod() {
        return bookedResourceFacade.getBookedResourcesForServiceInPeriod(currentService, startDate, endDate);
    }

    @Override
    protected List<Project> getKeysWithoutValues() {
        return projectFacade.getProjectsWithoutBookedResources(startDate, endDate);
    }

    @Override
    protected Project collectResourceByKey(BookedResource value) {
        return value.getProject();
    }

    @Override
    protected Supplier<BookedResource> getResourceSupplierForKey(Project key) {
        return () -> {
            BookedResource br = new BookedResource();
            br.setPersisted(false);
            br.setProject(key);
            br.setService(currentService);
            return br;
        };
    }
    
    @Override
    protected boolean rowIsActive(Project project) {
        return (project.getStartLocalDate().isAfter(startDate.minusDays(1)) && project.getStartLocalDate().isBefore(endDate.plusDays(1)))
                || (project.getEndLocalDate().isAfter(startDate.minusDays(1)) && project.getEndLocalDate().isBefore(endDate.plusDays(1)))
                || (project.getStartLocalDate().isBefore(startDate.plusDays(1)) && project.getEndLocalDate().isAfter(endDate.minusDays(1)));
    }

    @Override
    protected void createAreaModel() {
        barModel = new BarChartModel();
        ChartSeries total = new ChartSeries();
        total.setLabel("Estimation of required work resources");

        int size = resourceRow.size() * periods.size();

        WrappedMuttableValue<Long> min = new WrappedMuttableValue<>(0L);
        WrappedMuttableValue<Long> max = new WrappedMuttableValue<>(0L);

        if (size < 1200) {

            resourceRow.stream().forEach(row -> {

                ChartSeries chartSerie = new ChartSeries();
                chartSerie.setLabel(row.getKey().getName());

                row.getResources().stream().forEach(bookedResource -> {

                    String columnName;
                    long booked = Optional.ofNullable(bookedResource.getBooked()).orElse(0L);

                    if (step == Step.WEEK) {
                        WeekFields fields = WeekFields.of(Locale.GERMANY);
                        int kw = bookedResource.getStartDate().get(fields.weekOfYear());
                        columnName = "CW" + kw;
                    } else {
                        columnName = Utils.defaultDateFormat(bookedResource.getStartDateAsDate());
                    }

                    Double d = 100D;
                    chartSerie.set(columnName, booked);
                });

                barModel.addSeries(chartSerie);
            });
        } else {
            ChartSeries chartSerie = new ChartSeries();
            chartSerie.setLabel("total");

            int i = 0;
            for (PeriodTotal value : totalResources) {
                chartSerie.set(value.getStartDate(), value.getTotal());
            }
            barModel.addSeries(chartSerie);
        }

        barModel.setTitle("Resources booked for service " + currentService.getName());
        barModel.setLegendPosition("ne");
        barModel.setStacked(true);
        barModel.setShowPointLabels(true);
        barModel.setZoom(true);

        Axis xAxis = new CategoryAxis("Period (" + step.name().toLowerCase() + ")");
        xAxis.setTickAngle(90);

        barModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = barModel.getAxis(AxisType.Y);

        yAxis.setLabel("Resources (hours)");
    }

    public Service getCurrentService() {
        return currentService;
    }

    public void setCurrentService(Service currentService) {
        if (currentService.equals(this.currentService)) {
            return;
        }

        this.currentService = currentService;
        resetValues();
    }

    public boolean somethingToShow() {
        return currentService != null && getResourceRows().size() > 0;
    }

    @Override
    protected void updateOrCreateResource(List<BookedResource> resources) {
        bookedResourceFacade.updateOrCreateBookings(resources);
    }

    @Override
    protected List<PeriodTotal> getTotalResourcesInPeriod() {
        return bookedResourceFacade.getTotalBookedResourcesForServiceInPeriod(currentService, startDate, endDate);
    }

}

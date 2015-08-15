package com.rha.presentation;

import com.rha.boundary.AvailableResourceFacade;
import com.rha.boundary.BookedResourceFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.boundary.ProjectFacade;
import com.rha.control.LocalDateConverter;
import com.rha.entity.AvailableResource;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Project;
import com.rha.entity.Service;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

@SessionScoped
@Named("brc")
public class BookedResourceController extends ResourceController<Project, BookedResource> implements Serializable {

    @Inject
    transient Logger logger;

    @Inject
    BookedResourceFacade bookedResourceFacade;

    @Inject
    AvailableResourceFacade availableResourceFacade;

    @Inject
    ProjectFacade projectFacade;

    @Inject
    ServiceFacade serviceFacade;

    @PostConstruct
    public void init() {
    }

    @Override
    protected List<BookedResource> getResourcesInPeriod() {

        return bookedResourceFacade.getBookedResourcesForServiceInPeriod(periodController.getActiveSerivice(), periodController.getLocalStartDate(), periodController.getLocalEndDate());
    }

    @Override
    protected void createResourcesChart() {
        List<AvailableResource> availableResources
                = availableResourceFacade.getAvailableResourcesOfServiceInPeriod(
                        periodController.getLocalStartDate(), periodController.getLocalEndDate(), periodController.getActiveSerivice()
                );

        Map<LocalDate, List<AvailableResource>> res
                = availableResources.stream().collect(Collectors.groupingBy(AvailableResource::getStartDate, Collectors.toList()));

        super.createResourcesChart();

        ChartSeries serie = new LineChartSeries();

        for (LocalDate[] period : periodController.getPeriods()) {

            LocalDate startDate = period[0];
            if (res.containsKey(startDate)) {
                AvailableResource ar = res.get(startDate).get(0);
                serie.set(getFormatedDate(ar.getStartDate()), ar.getValue());
            } else {
                serie.set(getFormatedDate(startDate), 0);
            }
        }

        serie.setLabel("available");
        resourcesGraph.setExtender("ext");
        resourcesGraph.setStacked(true);
        resourcesGraph.addSeries(serie);

    }

    private String getFormatedDate(LocalDate resource) {
        return Utils.defaultDateFormat(LocalDateConverter.toDate(resource));
    }

    @Override
    protected List<Project> getKeysWithoutValues() {
        return projectFacade.getProjectsWithoutBookedResources(periodController.getLocalStartDate(), periodController.getLocalEndDate());
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
            br.setService(periodController.getActiveSerivice());
            return br;
        };
    }

    @Override
    protected boolean rowIsActive(Project project) {
        return (project.getStartLocalDate().isAfter(periodController.getLocalStartDate().minusDays(1)) 
                && project.getStartLocalDate().isBefore(periodController.getLocalEndDate().plusDays(1)))
                || (project.getEndLocalDate().isAfter(periodController.getLocalStartDate().minusDays(1)) 
                && project.getEndLocalDate().isBefore(periodController.getLocalEndDate().plusDays(1)))
                || (project.getStartLocalDate().isBefore(periodController.getLocalStartDate().plusDays(1)) 
                && project.getEndLocalDate().isAfter(periodController.getLocalEndDate().minusDays(1)));
    }

    public Service getCurrentService() {
        return periodController.getActiveSerivice();
    }

    public boolean somethingToShow() {
        return periodController.getActiveSerivice() != null && getResourceRows().size() > 0;
    }

    @Override
    protected void updateOrCreateResource(List<BookedResource> resources) {
        bookedResourceFacade.updateOrCreateBookings(resources);
    }

    @Override
    protected List<PeriodTotal> getTotalResourcesInPeriod() {
        return bookedResourceFacade.getTotalBookedResourcesForServiceInPeriod(
                periodController.getActiveSerivice(), periodController.getLocalStartDate(), periodController.getLocalEndDate());
    }

    @Override
    protected String getResourcesGraphTitle() {
        return "Resources booked for service " + periodController.getActiveSerivice().getName();
    }

    @Override
    protected Function<Project, String> getKeyDisplayName() {
        return (Project p) -> p.getName();
    }

}

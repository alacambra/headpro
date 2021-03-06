package io.headpro.presentation;

import io.headpro.boundary.AvailableResourceFacade;
import io.headpro.boundary.RequiredResourceFacade;
import io.headpro.boundary.ServiceFacade;
import io.headpro.boundary.ProjectFacade;
import io.headpro.control.LocalDateConverter;
import io.headpro.entity.AvailableResource;
import io.headpro.entity.RequiredResource;
import io.headpro.entity.PeriodTotal;
import io.headpro.entity.Project;
import io.headpro.entity.Service;
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
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

@SessionScoped
@Named("brc")
public class RequiredResourceController extends ResourceController<Project, RequiredResource> implements Serializable {

    @Inject
    transient Logger logger;

    @Inject
    RequiredResourceFacade bookedResourceFacade;

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
    protected List<RequiredResource> getResourcesInPeriod() {

        return bookedResourceFacade.getBookedResourcesForServiceInPeriod(periodController.getActiveService(), periodController.getLocalStartDate(), periodController.getLocalEndDate());
    }

    @Override
    protected void createResourcesChart() {
        List<AvailableResource> availableResources
                = availableResourceFacade.getAvailableResourcesOfServiceInPeriod(
                        periodController.getLocalStartDate(), periodController.getLocalEndDate(), periodController.getActiveService()
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
    protected Project collectResourceByKey(RequiredResource value) {
        return value.getProject();
    }

    @Override
    protected Supplier<RequiredResource> getResourceSupplierForKey(Project key) {
        return () -> {
            RequiredResource br = new RequiredResource();
            br.setPersisted(false);
            br.setProject(key);
            br.setService(periodController.getActiveService());
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
        return periodController.getActiveService();
    }

    public boolean somethingToShow() {
        return periodController.getActiveService() != null && getResourceRows().size() > 0;
    }

    @Override
    protected void updateOrCreateResource(List<RequiredResource> resources) {
        bookedResourceFacade.updateOrCreateBookings(resources);
    }

    @Override
    protected List<PeriodTotal> getTotalResourcesInPeriod() {
        return bookedResourceFacade.getTotalBookedResourcesForServiceInPeriod(
                periodController.getActiveService(), periodController.getLocalStartDate(), periodController.getLocalEndDate());
    }

    @Override
    protected String getResourcesGraphTitle() {
        return "Resources booked for service " + periodController.getActiveService().getName();
    }

    @Override
    protected Function<Project, String> getKeyDisplayName() {
        return (Project p) -> p.getName();
    }

}

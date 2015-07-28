package com.rha.presentation;

import com.rha.boundary.BookedResourceFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.boundary.ProjectFacade;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Project;
import com.rha.entity.Service;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;

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

    @Override
    protected String getResourcesGraphTitle() {
        return "Resources booked for service " + currentService.getName();
    }

    @Override
    protected Function<Project, String> getKeyDisplayName() {
        return (Project p) -> p.getName();
    }

}

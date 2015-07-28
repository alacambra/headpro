package com.rha.presentation;

import com.rha.boundary.AvailableResourceFacade;
import com.rha.boundary.ServiceFacade;
import com.rha.entity.AvailableResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Service;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

@Named("arc")
@SessionScoped
public class AvailableResourceController extends ResourceController<Service, AvailableResource> implements Serializable {

    @Inject
    transient Logger logger;

    @Inject
    AvailableResourceFacade availableResourceFacade;

    @Inject
    ServiceFacade serviceFacade;

    @Override
    protected List<AvailableResource> getResourcesInPeriod() {
        return availableResourceFacade.getAvailableResourcesInPeriod(startDate, endDate);
    }

    @Override
    protected List<Service> getKeysWithoutValues() {
        return serviceFacade.findAll();
    }

    @Override
    protected Service collectResourceByKey(AvailableResource value) {
        return value.getService();
    }

    @Override
    protected Supplier<AvailableResource> getResourceSupplierForKey(Service key) {
        return  () -> {
                AvailableResource ar = new AvailableResource();
                ar.setPersisted(false);
                ar.setService(key);
                return ar;
            };
    }

    @Override
    protected List<PeriodTotal> getTotalResourcesInPeriod() {
        return availableResourceFacade.getTotalAvailableResourcesInPeriod(startDate, endDate);
    }

    @Override
    protected void updateOrCreateResource(List<AvailableResource> resources) {
        availableResourceFacade.updateOrCreateBookings(resources);
    }

    @Override
    protected String getResourcesGraphTitle() {
        return "Available resources";
    }

    @Override
    protected Function<Service, String> getKeyDisplayName() {
        return (Service s) -> s.getName();
    }
}

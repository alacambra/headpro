package io.rha.boundary;

import io.rha.control.LocalDateConverter;
import io.rha.entity.AvailableResource;
import io.rha.entity.PeriodTotal;
import io.rha.entity.Service;

import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alacambra
 */
@Stateless
public class AvailableResourceFacade extends AbstractFacade<AvailableResource> {

    @PersistenceContext
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AvailableResourceFacade() {
        super(AvailableResource.class);
    }

    public List<AvailableResource> getAvailableResourcesInPeriod(LocalDate startDate, LocalDate endDate) {

        List<AvailableResource> availableResources
                = em.createNamedQuery(AvailableResource.availabiltyInPeriod)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();

        return availableResources;
    }

    public List<AvailableResource> getAvailableResourcesOfServiceInPeriod(
            LocalDate startDate, LocalDate endDate, Service service) {

        List<AvailableResource> availableResources
                = em.createNamedQuery(AvailableResource.availabiltyOfServiceInPeriod)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .setParameter("service", service)
                .getResultList();

        return availableResources;
    }

    public List<PeriodTotal> getTotalAvailableResourcesInPeriod(LocalDate startDate, LocalDate endDate) {

        List<PeriodTotal> availableResources
                = em.createNamedQuery(AvailableResource.totalAvailabiltyInPeriod, PeriodTotal.class)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();

        return availableResources;
    }

    public void updateOrCreateBookings(List<AvailableResource> resources) {

        resources.stream().filter((resource) -> (resource.isPersisted() || resource.getAvailable() != 0))
                .forEach(ar -> {
                    if (ar.getId() == null) {
                        em.persist(ar);
                    } else {
                        em.merge(ar);
                    }

                    ar.setPersisted(true);
                });
    }
}

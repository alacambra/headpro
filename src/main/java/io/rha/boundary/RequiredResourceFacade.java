package io.rha.boundary;

import io.rha.control.LocalDateConverter;
import io.rha.entity.RequiredResource;
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
public class RequiredResourceFacade extends AbstractFacade<RequiredResource> {

    @PersistenceContext
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<RequiredResource> getBookedResourcesForServiceInPeriod(
            Service service, LocalDate startDate, LocalDate endDate) {

        List<RequiredResource> bookedResources
                = em.createNamedQuery(RequiredResource.forServiceInPeriod)
                .setParameter("service", service)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();

        return bookedResources;
    }

    public List<PeriodTotal> getTotalBookedResourcesForServiceInPeriod(
            Service service, LocalDate startDate, LocalDate endDate) {

        List<PeriodTotal> bookedResources
                = em.createNamedQuery(RequiredResource.totalForServiceInPeriod, PeriodTotal.class)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .setParameter("service", service)
                .getResultList();

        return bookedResources;
    }

    public RequiredResourceFacade() {
        super(RequiredResource.class);
    }

    public void updateOrCreateBookings(List<RequiredResource> resources) {

        resources.stream().filter((resource) -> (resource.isPersisted() || resource.getBooked() != 0))
                .forEach(br -> {
                    if (br.getId() == null) {
                        br.setId(em.merge(br).getId());
                    } else {
                        em.merge(br);
                    }
                    br.setPersisted(true);
                });
    }

    public List<RequiredResource> getBookedResourcesInPeriod(LocalDate startDate, LocalDate endDate) {
        List<RequiredResource> r
                = em.createNamedQuery(RequiredResource.bookedInPeriod)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();

        return r;
    }
}

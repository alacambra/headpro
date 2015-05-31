package com.rha.boundary;

import com.rha.control.LocalDateConverter;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Service;
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
public class BookedResourceFacade extends AbstractFacade<BookedResource> {

    @PersistenceContext(unitName = "rha")
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<BookedResource> getBookedResourcesFor(int projectId, int divisionId) {

        List<BookedResource> bookedResources
                = em.createNamedQuery(BookedResource.byProjectAndService, BookedResource.class)
                .setParameter("pid", projectId)
                .setParameter("sid", divisionId)
                .getResultList();

        return bookedResources;
    }

    public List<BookedResource> getBookedResourcesForDivision(int divisionId) {

        List<BookedResource> bookedResources
                = em.createNamedQuery(BookedResource.byService)
                .setParameter("sid", divisionId)
                .getResultList();

        return bookedResources;
    }

    public List<BookedResource> getBookedResourcesForService(
            Service service, LocalDate startDate, LocalDate endDate) {

        List<BookedResource> bookedResources
                = em.createNamedQuery(BookedResource.byServiceForPeriod)
                .setParameter("service", service)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();

        return bookedResources;
    }

    public List<Integer> getTotalBookedResourcesPerProjectForDivision(Service service) {

        List<Integer> bookedResources
                = em.createNamedQuery(BookedResource.totalByService)
                .setParameter("service", service)
                .getResultList();

        return bookedResources;
    }

    public List<PeriodTotal> getTotalBookedResourcesByServiceForPeriod(
            Service service, LocalDate startDate, LocalDate endDate) {

        List<PeriodTotal> bookedResources
                = em.createNamedQuery(BookedResource.totalByServiceForPeriod, PeriodTotal.class)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .setParameter("service", service)
                .getResultList();

        return bookedResources;
    }

    public BookedResourceFacade() {
        super(BookedResource.class);
    }

    public void updateOrCreateBookings(List<BookedResource> resources) {

        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).isPersisted() || resources.get(i).getBooked() != 0) {
                BookedResource br = resources.get(i);
                if (br.getId() == null) {
                    em.persist(br);
                } else {
                    em.merge(br);
                }
//                em.merge(br);
//                resources.set(i, br);
            }
        }

//        resources.stream()
//                .filter(r -> r.isPersisted() || r.getBooked() != 0)
//                .forEach(r -> {
//                    System.out.println("before: " + r.getId() + ":" + r.isPersisted() + ":" + r.getBooked() + ":" + r.hashCode());
//                    r = em.merge(r);
//                    r.setPersisted(true);
//                    System.out.println("after: " + r.getId() + ":" + r.isPersisted() + ":" + r.getBooked() + ":" + r.hashCode());
//                });
    }
}

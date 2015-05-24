package com.rha.boundary;

import com.rha.control.LocalDateConverter;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.*;
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
                = em.createNamedQuery(BookedResource.byProjectAndDivision, BookedResource.class)
                .setParameter("pid", projectId)
                .setParameter("did", divisionId)
                .getResultList();

        return bookedResources;
    }

    public List<BookedResource> getBookedResourcesForDivision(int divisionId) {

        List<BookedResource> bookedResources
                = em.createNamedQuery(BookedResource.byDivision)
                .setParameter("did", divisionId)
                .getResultList();

        return bookedResources;
    }

    public List<BookedResource> getBookedResourcesForDivision(
            int divisionId, LocalDate startDate, LocalDate endDate) {

        List<BookedResource> bookedResources
                = em.createNamedQuery(BookedResource.byDivisionForPeriod)
                //                .setParameter("did", divisionId)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();

        return bookedResources;
    }

    public List<Integer> getTotalBookedResourcesPerProjectForDivision(int divisionId) {

        List<Integer> bookedResources
                = em.createNamedQuery(BookedResource.totalByDivision)
                .setParameter("did", divisionId)
                .getResultList();

        return bookedResources;
    }

    public List<PeriodTotal> getTotalBookedResourcesByDivisionForPeriod(
            int divisionId, LocalDate startDate, LocalDate endDate) {

        List<PeriodTotal> bookedResources
                = em.createNamedQuery(BookedResource.totalByDivisionForPeriod, PeriodTotal.class)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();
        
//        Map<LocalDate, Optional<Long>> r = bookedResources.stream()
//                .collect(groupingBy(PeriodTotal::getDate,
//                                mapping(PeriodTotal::getTotal, reducing(Long::sum))));

        return bookedResources;
    }

    public BookedResourceFacade() {
        super(BookedResource.class);
    }

    public void updateOrCreateBookings(Collection<BookedResource> resources) {
        resources.stream()
                .filter(r -> r.isPersisted() || r.getBooked() != 0)
                .forEach(r -> em.merge(r));
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.control.LocalDateConverter;
import com.rha.entity.AvailableResource;
import com.rha.entity.PeriodTotal;
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

    @PersistenceContext(unitName = "rha")
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

    public List<PeriodTotal> getTotalAvailableResourcesInPeriod(LocalDate startDate, LocalDate endDate){

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

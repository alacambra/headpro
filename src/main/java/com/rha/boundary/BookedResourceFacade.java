/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.control.CalendarGenerator;
import com.rha.entity.BookedResource;
import com.rha.entity.Step;
import com.rha.entity.StepPeriod;
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
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<BookedResource> getBookedResourcesFor(int projectId, int divisionId) {

        List<BookedResource> bookedResources
                = em.createNamedQuery(
                        BookedResource.bookedResourceByProjectAndDivision, BookedResource.class)
                .setParameter("pid", projectId)
                .setParameter("did", divisionId)
                .getResultList();

        return bookedResources;
    }

    public List<BookedResource> getBookedResourcesForDivision(int divisionId) {

        List<BookedResource> bookedResources
                = em.createNamedQuery(BookedResource.bookedProjectResourcesByDivision)
                .setParameter("did", divisionId)
                .getResultList();

        return bookedResources;
    }
    
    public List<Integer> getTotslBookedResourcesPerProjectForDivision(int divisionId) {

        List<Integer> bookedResources
                = em.createNamedQuery(BookedResource.bookedTotalProjectResourcesByDivision)
                .setParameter("did", divisionId)
                .getResultList();

        return bookedResources;
    }

    public BookedResourceFacade() {
        super(BookedResource.class);
    }

    public List<StepPeriod> getPeriods() {
        return new CalendarGenerator(
                LocalDate.now(),
                LocalDate.now().plusYears(1), Step.MONTH)
                .getEntries();
    }
}

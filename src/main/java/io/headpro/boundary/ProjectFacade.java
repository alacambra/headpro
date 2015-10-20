/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.boundary;

import io.headpro.control.LocalDateConverter;
import io.headpro.entity.Project;
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
public class ProjectFacade extends AbstractFacade<Project> {

    @PersistenceContext
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Project> getProjectsWithoutBookedResources(LocalDate startDate, LocalDate endDate) {
        List<Project> emptyProjects = em
                .createNamedQuery(Project.projectsInPeriod, Project.class)
                .setParameter("startDate", LocalDateConverter.toDate(startDate))
                .setParameter("endDate", LocalDateConverter.toDate(endDate))
                .getResultList();

        return emptyProjects;
    }

    public ProjectFacade() {
        super(Project.class);
    }

}

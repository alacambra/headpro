/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.entity.Project;
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
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Project> getProjectsWithoutBookedResources(int did) {
        List<Project> emptyProjects = em
                .createNamedQuery(Project.emptyProjects, Project.class)
                .getResultList();

        return emptyProjects;
    }

    public ProjectFacade() {
        super(Project.class);
    }

}

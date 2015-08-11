/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.entity.Project;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import static org.hamcrest.core.Is.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class ProjectFacadeIT {
    EntityManager em;
    EntityTransaction tx;
    ProjectFacade cut;
    
    public ProjectFacadeIT() {
    }
    
    @Before
    public void setUp() {
        cut = new ProjectFacade();
        cut.em = Persistence.createEntityManagerFactory("it").createEntityManager();
        this.em = cut.em;
        this.tx = this.em.getTransaction();
        tx.begin();
    }

    @Test
    public void testGetProjectsWithoutBookedResources() throws Exception {
        
        Project project = new Project();
        project.setStartLocalDate(LocalDate.now());
        project.setEndLocalDate(LocalDate.now().plusMonths(1));
        project = em.merge(project);
        List<Project> pr = cut.getProjectsWithoutBookedResources(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        
        assertThat(pr.size(), is(1));
        
        
    }
    
}

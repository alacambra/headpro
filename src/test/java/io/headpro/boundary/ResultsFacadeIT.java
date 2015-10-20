/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.boundary;

import io.headpro.entity.AvailableResource;
import io.headpro.entity.RequiredResource;
import io.headpro.entity.Project;
import io.headpro.entity.Service;
import io.headpro.entity.Step;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class ResultsFacadeIT {

    RemainingResourcesFacade cut;
    EntityManager em;
    EntityTransaction tx;

    public ResultsFacadeIT() {
    }

    @Before
    public void setUp() {

        this.em = Persistence.createEntityManagerFactory("it").createEntityManager();

        RequiredResourceFacade bookedResourceFacade = new RequiredResourceFacade();
        bookedResourceFacade.em = em;

        AvailableResourceFacade availableResourceFacade = new AvailableResourceFacade();
        availableResourceFacade.em = em;

        cut = new RemainingResourcesFacade(availableResourceFacade, bookedResourceFacade);
        this.tx = this.em.getTransaction();
    }

    @Test
    public void testGetNetoRemainingResources() {
    }

    @Test
    @Ignore
    public void testGetWeighedRemainingResources() {
        
        tx.begin();

        LocalDate periodStartDate = LocalDate.of(2015, Month.JANUARY, 1);
        LocalDate periodEndDate = LocalDate.of(2015, Month.DECEMBER, 31);

        Service s1 = createService("s1");
        Service s2 = createService("s2");

        Project p1 = createProject("p1", periodStartDate, periodEndDate);
        Project p2 = createProject("p2", periodStartDate, periodEndDate);

        createBookedResource(p1, s1, periodStartDate);
        createBookedResource(p1, s1, periodStartDate);
        createBookedResource(p1, s2, periodStartDate);

        createBookedResource(p2, s1, periodStartDate);
        createBookedResource(p2, s1, periodStartDate);
        createBookedResource(p2, s2, periodStartDate);

        createAvailableResource(s1);
        createAvailableResource(s2);
        
        Map<Service, Map<LocalDate, Float>> r = cut.getWeighedRemainingResourcesByServiceAndDate(periodStartDate, periodEndDate);
        
        assertThat(r.size(), is(2));
        assertThat(r.get(s1).size(), is(1));
        assertThat(r.get(s2).size(), is(1));
        
        tx.commit();

    }

    private Project createProject(String name, LocalDate startDate, LocalDate endDate) {
        Project p = new Project();
        p.setName(name);
        p.setStartLocalDate(startDate);
        p.setEndLocalDate(endDate);
        p.setStep(Step.BIWEEK);
        p = em.merge(p);
        return p;
    }

    private Service createService(String name) {
        Service s = new Service();
        s.setName(name);
        s = em.merge(s);
        return s;
    }

    private RequiredResource createBookedResource(Project p, Service s, LocalDate startDate) {

        RequiredResource br = new RequiredResource();
        br.setStartDate(startDate);
        br.setEndDate(startDate.plusWeeks(2));
        br.setService(s);
        br.setProject(p);
        br.setValue(5f);
        br = em.merge(br);
        return br;
    }

    private AvailableResource createAvailableResource(Service s) {
        AvailableResource ar = new AvailableResource();
        ar.setValue(5f);
        ar.setService(s);
        return em.merge(ar);
    }

}

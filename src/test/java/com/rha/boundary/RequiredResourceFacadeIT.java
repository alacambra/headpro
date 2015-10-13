/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.entity.RequiredResource;
import com.rha.entity.Service;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Project;
import com.rha.entity.Step;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.hamcrest.core.Is;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class RequiredResourceFacadeIT {

    EntityManager em;
    EntityTransaction tx;
    RequiredResourceFacade cut;

    public RequiredResourceFacadeIT() {

    }

    @Before
    public void setUp() {
        cut = new RequiredResourceFacade();
        cut.em = Persistence.createEntityManagerFactory("it").createEntityManager();
        this.em = cut.em;
        this.tx = this.em.getTransaction();
        tx.begin();
    }

    @After
    public void tearDown() {
        tx.commit();
    }

    @Test
    @Ignore
    public void testGetBookedResourcesInPeriod() throws Exception {

        Service s = new Service();
        s.setName("testDivision");
        s = em.merge(s);

        Project p = new Project();
        p.setName("testProject");
        p.setStep(Step.MONTH);
        p = em.merge(p);

        RequiredResource br = new RequiredResource();
        br.setBooked(10f);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        br.setEndDate(LocalDate.of(2015, Month.MARCH, 30));
        br.setProject(p);

        cut.create(br);

        br = new RequiredResource();
        br.setBooked(10f);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.JULY, 1));
        br.setEndDate(LocalDate.of(2015, Month.JULY, 30));
        br.setProject(p);

        cut.create(br);

        List<RequiredResource> r
                = cut.getBookedResourcesInPeriod(LocalDate.of(2010, Month.MARCH, 30), LocalDate.of(2017, Month.MARCH, 30));

        assertThat(r.size(), is(2));
    }

    @Test
    public void testGetBookedResourcesForDivision_int() throws Exception {
    }

    @Test
    public void testGetBookedResourcesForDivision_3args() throws Exception {

        Service s = new Service();
        s.setName("testDivision");
        s = em.merge(s);

        Project p = new Project();
        p.setName("testProject");
        p.setStep(Step.MONTH);
        p = em.merge(p);

        RequiredResource br = new RequiredResource();
        br.setBooked(10f);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        br.setEndDate(LocalDate.of(2015, Month.MARCH, 30));
        br.setProject(p);

        cut.create(br);

        br = new RequiredResource();
        br.setBooked(10f);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.JULY, 1));
        br.setEndDate(LocalDate.of(2015, Month.JULY, 30));
        br.setProject(p);

        cut.create(br);

        List<RequiredResource> r = cut.getBookedResourcesForServiceInPeriod(s,
                LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.DECEMBER, 31));

        assertThat(r.size(), Is.is(2));

        r = cut.getBookedResourcesForServiceInPeriod(s,
                LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.MAY, 31));

        assertThat(r.size(), Is.is(1));
    }

    @Test
    @Ignore
    public void testGetTotalBookedResourcesPerProjectForDivision_int() throws Exception {
        Service s = new Service();
        s.setName("testDivision");
        s = em.merge(s);

        Project p = new Project();
        p.setName("testProject");
        p.setStep(Step.MONTH);
        p = em.merge(p);

        Project p2 = new Project();
        p2.setName("testProject");
        p2.setStep(Step.MONTH);
        p2 = em.merge(p2);

        RequiredResource br = new RequiredResource();
        br.setBooked(10f);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        br.setEndDate(LocalDate.of(2015, Month.MARCH, 30));
        br.setProject(p);

        cut.create(br);

        br = new RequiredResource();
        br.setBooked(15f);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        br.setEndDate(LocalDate.of(2015, Month.MARCH, 30));
        br.setProject(p2);

        cut.create(br);

        br = new RequiredResource();
        br.setBooked(30f);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.JULY, 1));
        br.setEndDate(LocalDate.of(2015, Month.JULY, 30));
        br.setProject(p);

        cut.create(br);

        List<PeriodTotal> r = cut.getTotalBookedResourcesForServiceInPeriod(s,
                LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.DECEMBER, 31));

        assertThat(r.size(), Is.is(2));
        assertThat(r.get(0).getTotal(), Is.is(25L));
        assertThat(r.get(0).getStartDate(), Is.is(LocalDate.of(2015, Month.MARCH, 1)));
        assertThat(r.get(1).getTotal(), Is.is(30L));
        assertThat(r.get(1).getStartDate(), Is.is(LocalDate.of(2015, Month.JULY, 1)));
    }

    @Test
    public void testGetTotalBookedResourcesByServiceInPeriod() {

        Service s = new Service();
        s.setName("testDivision");
        s = em.merge(s);

        Project p = new Project();
        p.setName("testProject");
        p.setStep(Step.MONTH);
        p = em.merge(p);

        RequiredResource br = new RequiredResource();
        br.setBooked(10f);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        br.setEndDate(LocalDate.of(2015, Month.MARCH, 30));
        br.setProject(p);

        em.persist(br);

        List<PeriodTotal> result = cut.getTotalBookedResourcesByServiceInPeriod(
                LocalDate.of(2015, Month.FEBRUARY, 1), LocalDate.of(2015, Month.FEBRUARY, 1).plusMonths(12));

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getTotal(), is(10F));
    }

    @Test
    public void testGetBookedResourcesForServiceInPeriod() throws Exception {
    }

    @Test
    public void testGetTotalBookedResourcesForServiceInPeriod() throws Exception {
    }

    @Test
    public void testUpdateOrCreateBookings() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
    }

    @Test
    public void testEdit() throws Exception {
    }

    @Test
    public void testRemove() throws Exception {
    }

    @Test
    public void testFind() throws Exception {
    }

    @Test
    public void testFindAll() throws Exception {
    }

    @Test
    public void testFindRange() throws Exception {
    }

    @Test
    public void testCount() throws Exception {
    }

}

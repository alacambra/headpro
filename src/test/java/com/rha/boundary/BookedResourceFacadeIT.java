/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.entity.BookedResource;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class BookedResourceFacadeIT {

    EntityManager em;
    EntityTransaction tx;
    BookedResourceFacade cut;

    public BookedResourceFacadeIT() {

    }

    @Before
    public void setUp() {
        cut = new BookedResourceFacade();
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
    public void testGetBookedResourcesFor() throws Exception {
    }

    @Test
    public void testGetBookedResourcesForDivision_int() throws Exception {
    }

    @Test
    public void testGetBookedResourcesForDivision_3args() throws Exception {

        Service s = new Service();
        s.setName("testDivision");
        em.merge(s);
        
        Project p = em.merge(new Project().setName("testProject").setStep(Step.MONTH));

        BookedResource br = new BookedResource();
        br.setBooked(10L);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        br.setEndDate(LocalDate.of(2015, Month.MARCH, 30));
        br.setProject(p);

        cut.create(br);

        br = new BookedResource();
        br.setBooked(10L);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.JULY, 1));
        br.setEndDate(LocalDate.of(2015, Month.JULY, 30));
        br.setProject(p);

        cut.create(br);

        List<BookedResource> r = cut.getBookedResourcesForService(s,
                LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.DECEMBER, 31));

        assertThat(r.size(), Is.is(2));

        r = cut.getBookedResourcesForService(s,
                LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.MAY, 31));

        assertThat(r.size(), Is.is(1));
    }

    @Test
    public void testGetTotalBookedResourcesPerProjectForDivision_int() throws Exception {
        Service s = new Service();
        s.setName("testDivision");
        em.merge(s);
        Project p = em.merge(new Project().setName("testProject").setStep(Step.MONTH));
        Project p2 = em.merge(new Project().setName("testProject").setStep(Step.MONTH));

        BookedResource br = new BookedResource();
        br.setBooked(10L);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        br.setEndDate(LocalDate.of(2015, Month.MARCH, 30));
        br.setProject(p);

        cut.create(br);

        br = new BookedResource();
        br.setBooked(15L);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        br.setEndDate(LocalDate.of(2015, Month.MARCH, 30));
        br.setProject(p2);

        cut.create(br);

        br = new BookedResource();
        br.setBooked(30L);
        br.setService(s);
        br.setStartDate(LocalDate.of(2015, Month.JULY, 1));
        br.setEndDate(LocalDate.of(2015, Month.JULY, 30));
        br.setProject(p);

        cut.create(br);

        List<PeriodTotal> r = cut.getTotalBookedResourcesByServiceForPeriod(s,
                LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.DECEMBER, 31));

        assertThat(r.size(), Is.is(2));
        assertThat(r.get(0).getTotal(), Is.is(25L));
        assertThat(r.get(0).getStartDate(), Is.is(LocalDate.of(2015, Month.MARCH, 1)));
        assertThat(r.get(1).getTotal(), Is.is(30L));
        assertThat(r.get(1).getStartDate(), Is.is(LocalDate.of(2015, Month.JULY, 1)));
    }

    @Test
    public void testGetTotalBookedResourcesPerProjectForDivision_3args() throws Exception {
    }

    @Test
    public void testUpdateOrCreateBookings() throws Exception {
    }

}

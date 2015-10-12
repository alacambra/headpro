/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.entity.AvailableResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.Service;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class AvailableResourceFacadeIT extends BaseTestIT{

    AvailableResourceFacade cut;

    public AvailableResourceFacadeIT() {
    }

    @Before
    public void setUp() {
        super.setUp();
        cut = new AvailableResourceFacade();
        cut.em = Persistence.createEntityManagerFactory("it").createEntityManager();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testGetAvailableResourcesInPeriod() throws Exception {
        tx.begin();
        Service s = createService();
        AvailableResource ar = new AvailableResource();
        ar.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        ar.setEndDate(LocalDate.of(2015, Month.MARCH, 1).plusMonths(1));
        ar.setService(s);
        em.merge(ar);

        ar = new AvailableResource();
        ar.setStartDate(LocalDate.of(2015, Month.DECEMBER, 1));
        ar.setEndDate(LocalDate.of(2015, Month.DECEMBER, 1).plusMonths(1));
        ar.setService(s);
        em.merge(ar);

        tx.commit();
        tx.begin();

        List<AvailableResource> result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.MARCH, 1), LocalDate.of(2015, Month.APRIL, 1));

        tx.commit();
        assertThat(result.size(), is(1));

        tx.begin();

        result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2018, Month.MARCH, 1), LocalDate.of(2017, Month.MARCH, 1));

        tx.commit();
        assertThat(result.size(), is(0));

        tx.begin();

        result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.MARCH, 1), LocalDate.of(2016, Month.MARCH, 1));

        tx.commit();
        assertThat(result.size(), is(2));
    }
    
    @Test
    public void testGetAvailableResourcesInPeriodBig() throws Exception {
        loadServiceTestTable();
        tx.begin();

        List<AvailableResource> result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.MARCH, 1), LocalDate.of(2016, Month.MARCH, 1));

        tx.commit();
        
        assertThat(result.size(), is(27));
    }

    @Test
    public void testGetAvailableResourcesInPeriodEmpty() throws Exception {
        loadServiceTestTable();
        tx.begin();

        List<AvailableResource> result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.MARCH, 1), LocalDate.of(2015, Month.APRIL, 30));

        tx.commit();

        assertThat(result.size(), is(0));
    }

    @Test
    public void testGetAvailableResourcesInPeriodShort() throws Exception {
        loadServiceTestTable();
        tx.begin();

        List<AvailableResource> result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.OCTOBER, 1), LocalDate.of(2015, Month.NOVEMBER, 1));

        tx.commit();

        assertThat(result.size(), is(20));
    }

    @Test
    public void testGetTotalAvailableResourcesInPeriodBig() throws Exception {
        loadServiceTestTable();
        tx.begin();

        List<PeriodTotal> result = cut.getTotalAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.MARCH, 1), LocalDate.of(2016, Month.MARCH, 1));

        tx.commit();

        assertThat(result.size(), is(4));

        List<Float> expectedTotal = Arrays.asList(75f,66f,60f,63.5f);

        for(int i = 0; i<4; i++){
            assertThat(result.get(i).getTotal(), is(expectedTotal.get(i)));
        }
    }

    @Test
    public void testGetTotalAvailableResourcesInPeriod() throws Exception {

        tx.begin();
        Service s = createService();
        AvailableResource ar = new AvailableResource();
        ar.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        ar.setEndDate(LocalDate.of(2015, Month.MARCH, 1).plusMonths(1));
        ar.setService(s);
        ar.setAvailable(50f);
        em.merge(ar);

        s = createService();
        ar = new AvailableResource();
        ar.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        ar.setEndDate(LocalDate.of(2015, Month.MARCH, 1).plusMonths(1));
        ar.setService(s);
        ar.setAvailable(5f);
        em.merge(ar);

        tx.commit();
        tx.begin();

        List<PeriodTotal> result = cut.getTotalAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.MARCH, 1), LocalDate.of(2017, Month.APRIL, 1));

        tx.commit();
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getTotal(), is(55F));

    }

    @Test
    public void testUpdateOrCreateBookings() throws Exception {

        tx.begin();
        Service s = createService();
        AvailableResource ar = new AvailableResource();
        ar.setStartDate(LocalDate.of(2015, Month.MARCH, 1));
        ar.setEndDate(LocalDate.of(2015, Month.MARCH, 1).plusMonths(1));
        ar.setService(s);
        ar.setPersisted(false);

        cut.updateOrCreateBookings(Arrays.asList(ar));

        List<AvailableResource> result
                = cut.getAvailableResourcesInPeriod(LocalDate.of(2010, Month.MARCH, 1), LocalDate.of(2017, Month.MARCH, 1));

        assertThat(ar.isPersisted(), is(false));
        assertThat(result.size(), is(0));

        ar.setAvailable(50f);

        cut.updateOrCreateBookings(Arrays.asList(ar));

        result = cut.getAvailableResourcesInPeriod(LocalDate.of(2010, Month.MARCH, 1), LocalDate.of(2017, Month.MARCH, 1));

        assertThat(ar.isPersisted(), is(true));
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(ar));
        assertThat(result.get(0).getAvailable(), is(50F));
        assertTrue(result.get(0) == ar);

        tx.commit();
        tx.begin();

        ar.setAvailable(10f);
        cut.updateOrCreateBookings(Arrays.asList(ar));

        tx.commit();
        assertThat(ar.isPersisted(), is(true));
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(ar));
        assertThat(result.get(0).getAvailable(), is(10F));
        assertTrue(result.get(0) == ar);
    }

    @Test
    public void testGetAvailableResourcesOfServiceInPeriod() {
        cut.getAvailableResourcesOfServiceInPeriod(LocalDate.now(), LocalDate.now(), createService());
    }

    private Service createService() {
        Service s = new Service();
        s.setName("testService");
        s = em.merge(s);
        return s;
    }

}

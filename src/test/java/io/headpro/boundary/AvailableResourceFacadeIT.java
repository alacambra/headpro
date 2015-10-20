/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.boundary;

import io.headpro.entity.AvailableResource;
import io.headpro.entity.PeriodTotal;
import io.headpro.entity.Service;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Persistence;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Ignore;
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
    public void testGetAvailableResourcesInPeriodSmall() throws Exception {
        loadAvailableServiceTestTable("ServiceTestTableSmall.csv");
        tx.begin();

        List<AvailableResource> result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.MARCH, 1), LocalDate.of(2016, Month.MARCH, 1));

        tx.commit();

        assertThat(result.size(), is(27));
    }

    @Test
    public void testGetAvailableResourcesInPeriodBig() throws Exception {
        loadAvailableServiceTestTable("ServiceTestTableBig.csv");
        tx.begin();

        List<AvailableResource> result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.JANUARY, 14));

        tx.commit();

        assertThat(result.size(), is(12));

        tx.begin();

        result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.JANUARY, 16));
        assertThat(result.size(), is(24));

        tx.commit();

        tx.begin();

        result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.JUNE, 30));

        tx.commit();

        assertThat(result.size(), is(2*12*6));

        tx.begin();

        result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.DECEMBER, 30));

        tx.commit();

        assertThat(result.size(), is(2*12*12));
    }

    @Test
    public void testGetAvailableResourcesInPeriodBigWithNulls() throws Exception {
        loadAvailableServiceTestTable("ServiceTestTableBigWithNulls.csv");
        tx.begin();

        List<AvailableResource> result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.JANUARY, 14));

        tx.commit();

        assertThat(result.size(), is(8));

        tx.begin();

        result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.JANUARY, 16));
        assertThat(result.size(), is(16));

        tx.commit();

        tx.begin();

        result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.JUNE, 30));

        tx.commit();

        assertThat(result.size(), is(104));

        tx.begin();

        result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.DECEMBER, 30));

        tx.commit();

        assertThat(result.size(), is(212));
    }



    @Test
    public void testGetAvailableResourcesInPeriodEmpty() throws Exception {
        loadAvailableServiceTestTable("ServiceTestTableSmall.csv");
        tx.begin();

        List<AvailableResource> result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.MARCH, 1), LocalDate.of(2015, Month.APRIL, 30));

        tx.commit();

        assertThat(result.size(), is(0));
    }

    @Test
    public void testGetAvailableResourcesInPeriodShort() throws Exception {
        loadAvailableServiceTestTable("ServiceTestTableSmall.csv");
        tx.begin();

        List<AvailableResource> result = cut.getAvailableResourcesInPeriod(
                LocalDate.of(2015, Month.OCTOBER, 1), LocalDate.of(2015, Month.NOVEMBER, 1));

        tx.commit();

        assertThat(result.size(), is(20));
    }

    @Test
    public void testGetTotalAvailableResourcesInPeriodSmall() throws Exception {
        loadAvailableServiceTestTable("ServiceTestTableSmall.csv");
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
    public void testGetTotalAvailableResourcesInPeriodBig() throws Exception {
        loadAvailableServiceTestTable("ServiceTestTableBig.csv");
        tx.begin();

        List<PeriodTotal> result = cut.getTotalAvailableResourcesInPeriod(
                LocalDate.of(2012, Month.MARCH, 1), LocalDate.of(2014, Month.MARCH, 1));

        tx.commit();

        assertThat(result.size(), is(24));

        List<Float> expectedTotal = Arrays.asList(
                25f, 25f, 25f, 25f, 26f, 26f, 26f, 26f,
                26f, 26f, 26f, 26f, 26f, 26f, 26f, 26f,
                26f, 26f, 26f, 26f, 26f, 26f, 26f, 26f
        );

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
    @Ignore
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

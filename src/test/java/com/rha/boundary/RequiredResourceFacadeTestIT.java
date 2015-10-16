package com.rha.boundary;

import com.rha.entity.*;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RequiredResourceFacadeTestIT extends BaseTestIT {

    RequiredResourceFacade cut;

    @Before
    public void setUp() {
        super.setUp();
        cut = new RequiredResourceFacade();
        cut.em = this.em;
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testGetBookedResourcesForServiceInPeriod() throws Exception {
        Service s1 = loadRequiredServiceTestTable("RequiredResourcesTestTableS1.csv", null);
        tx.begin();

        List<RequiredResource> r = cut.getBookedResourcesForServiceInPeriod(s1,
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.DECEMBER, 31));

        assertThat(r.size(), is(126));

        tx.commit();

        tx.begin();

        r = cut.getBookedResourcesForServiceInPeriod(s1,
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.JANUARY, 14));

        assertThat(r.size(), is(5));

        tx.commit();

        tx.begin();

        r = cut.getBookedResourcesForServiceInPeriod(s1,
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.JANUARY, 16));

        assertThat(r.size(), is(10));

        tx.commit();

        tx.begin();

        r = cut.getBookedResourcesForServiceInPeriod(s1,
                LocalDate.of(2013, Month.JANUARY, 16), LocalDate.of(2013, Month.JANUARY, 16));

        assertThat(r.size(), is(5));

        tx.commit();

        tx.begin();

        r = cut.getBookedResourcesForServiceInPeriod(s1,
                LocalDate.of(2013, Month.JANUARY, 15), LocalDate.of(2013, Month.JANUARY, 16));

        assertThat(r.size(), is(10));

        tx.commit();
    }

    @Test
    public void testGetTotalBookedResourcesForServiceInPeriod() throws Exception {

        List<Float> totals = new ArrayList<>();
        Service s1 = loadRequiredServiceTestTable("RequiredResourcesTestTableS1.csv", totals);

        List<PeriodTotal> r = cut.getTotalBookedResourcesForServiceInPeriod(s1,
                LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.DECEMBER, 31));

        assertThat(totals.size(), Is.is(24));
        assertThat(r.size(), Is.is(24));

        for(int i = 0; i<r.size(); i++){
            assertThat(r.get(i).getTotal(), is(totals.get(i)));
        }

    }

    @Test
    public void testUpdateOrCreateBookings() throws Exception {

    }

    @Test
    public void testGetBookedResourcesInPeriod() throws Exception {

    }
}
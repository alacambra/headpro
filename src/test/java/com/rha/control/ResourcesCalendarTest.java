/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import com.rha.entity.RequiredResource;
import com.rha.entity.Step;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class ResourcesCalendarTest {

    CalendarPeriodsGenerator cut;
    CalendarEntriesGenerator entriesGenerator;

    @Before
    public void setUp() {
        cut = new CalendarPeriodsGenerator();
        entriesGenerator = new CalendarEntriesGenerator();
        entriesGenerator.logger = Logger.getAnonymousLogger();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDailyCalendarEntriesGeneration() {
        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));

        cut.setStep(Step.DAY);

        assertThat(cut.generatePeriods().size(), Is.is(cut.getStartDate().lengthOfYear()));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));

        cut.setStep(Step.DAY);

        assertThat(cut.generatePeriods().size(), Is.is(2 * cut.getStartDate().lengthOfYear()));

        setUp();

        int expected = IntStream.rangeClosed(2012, 2016).boxed()
                .map(year -> LocalDate.ofYearDay(year, 1).lengthOfYear())
                .reduce(Integer::sum).get();

        cut.setStartDate(LocalDate.of(2012, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2016, Month.DECEMBER, 31));

        cut.setStep(Step.DAY);

        assertThat(cut.generatePeriods().size(), Is.is(expected));

    }

    @Test
    public void testBiweeklyCalendarEntriesGeneration() {
        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31))
                .setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(12 * 2));

        setUp();
        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2016, Month.DECEMBER, 31))
                .setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(2 * 12 * 2));

        setUp();
        cut.setStartDate(LocalDate.of(2015, Month.MARCH, 1))
                .setEndDate(LocalDate.of(2016, Month.MARCH, 31))
                .setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(2 + 12 * 2));

        setUp();
        cut.setStartDate(LocalDate.of(2015, Month.MARCH, 12))
                .setEndDate(LocalDate.of(2016, Month.MARCH, 18))
                .setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(2 + 12 * 2));

        setUp();
        cut.setStartDate(LocalDate.of(2016, Month.MARCH, 12))
                .setEndDate(LocalDate.of(2016, Month.MARCH, 18))
                .setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(2));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2014, Month.JANUARY, 4));

        cut.setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(1));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2014, Month.JANUARY, 17));

        cut.setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(2));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 15))
                .setEndDate(LocalDate.of(2014, Month.JANUARY, 17));

        cut.setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(2));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 19))
                .setEndDate(LocalDate.of(2014, Month.JANUARY, 20));

        cut.setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(1));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2016, Month.JANUARY, 1));

        cut.setStep(Step.BIWEEK);

        assertThat(cut.generatePeriods().size(), Is.is(2 * 2 * 12 + 1));
    }

    @Test
    public void testWeeklyCalendarEntriesGeneration() {
        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));

        cut.setStep(Step.WEEK);

        assertThat(cut.generatePeriods().size(), Is.is(53));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));

        cut.setStep(Step.WEEK);

        assertThat(cut.generatePeriods().size(), Is.is(2 * 53));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2014, Month.JANUARY, 1));

        cut.setStep(Step.WEEK);

        assertThat(cut.generatePeriods().size(), Is.is(1));

        setUp();
        cut.setStartDate(LocalDate.of(2015, Month.MARCH, 1))
                .setEndDate(LocalDate.of(2015, Month.JUNE, 30));

        cut.setStep(Step.WEEK);

        assertThat(cut.generatePeriods().size(), Is.is(19));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2016, Month.DECEMBER, 31));

        cut.setStep(Step.WEEK);

        WeekFields weekFields = WeekFields.of(Locale.GERMANY);

        int total = (LocalDate.ofYearDay(2014, 365).with(TemporalAdjusters.lastDayOfYear()).get(weekFields.weekOfYear()))
                + (LocalDate.ofYearDay(2015, 365).with(TemporalAdjusters.lastDayOfYear()).get(weekFields.weekOfYear()))
                + (LocalDate.ofYearDay(2016, 365).with(TemporalAdjusters.lastDayOfYear()).get(weekFields.weekOfYear()));

        assertThat(cut.generatePeriods().size(), Is.is(total));
    }

    @Test
    public void testMonthlyCalendarEntriesGeneration() {
        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));

        cut.setStep(Step.MONTH);

        assertThat(cut.generatePeriods().size(), Is.is(12));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));

        cut.setStep(Step.MONTH);

        assertThat(cut.generatePeriods().size(), Is.is(2 * 12));

        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2014, Month.JANUARY, 1));

        cut.setStep(Step.MONTH);

        assertThat(cut.generatePeriods().size(), Is.is(1));

        setUp();
        cut.setStartDate(LocalDate.of(2015, Month.MARCH, 1))
                .setEndDate(LocalDate.of(2015, Month.JUNE, 30));

        cut.setStep(Step.MONTH);

        assertThat(cut.generatePeriods().size(), Is.is(4));
    }

    @Test
    public void testMergeCalenderEntriesMonth() {

        List<RequiredResource> existentResources = new ArrayList<>();

        RequiredResource br = new RequiredResource();
        br.setStartDate(LocalDate.of(2015, Month.JANUARY, 1));

        existentResources.add(br);

        br = new RequiredResource();
        br.setStartDate(LocalDate.of(2015, Month.JUNE, 1));

        existentResources.add(br);

        List<LocalDate[]> periods = cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31))
                .setStep(Step.MONTH)
                .generatePeriods();

        List<RequiredResource> result = entriesGenerator.getCalendarEntries(existentResources, periods, () -> {
                    RequiredResource res = new RequiredResource();
                    res.setPersisted(false);
                    return res;
                });

        assertEquals(12, result.size());
        assertEquals(existentResources.get(0), result.get(0));
        assertEquals(existentResources.get(1), result.get(5));
    }

    @Test
    public void testMergeCalendarEntriesHalfYeyar() {

        List<RequiredResource> existentResources = new ArrayList<>();

        RequiredResource br = new RequiredResource();
        br.setStartDate(LocalDate.of(2015, Month.JUNE, 1));

        existentResources.add(br);

        List<LocalDate[]> periods = cut.setStartDate(LocalDate.of(2015, Month.FEBRUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.AUGUST, 31))
                .setStep(Step.MONTH)
                .generatePeriods();

        List<RequiredResource> result = entriesGenerator.getCalendarEntries(existentResources, periods, () -> {
                    RequiredResource res = new RequiredResource();
                    res.setPersisted(false);
                    return res;
                });

        assertEquals(7, result.size());
        assertTrue(result.contains(existentResources.get(0)));
        assertEquals(existentResources.get(0), result.get(4));
    }

    @Test
    public void testMergeCalendarEntriesWeek() {

        List<RequiredResource> existentResources = new ArrayList<>();

        RequiredResource br = new RequiredResource();
        br.setStartDate(LocalDate.of(2015, Month.FEBRUARY, 1));

        existentResources.add(br);

        br = new RequiredResource();
        br.setStartDate(LocalDate.of(2015, Month.JUNE, 1));

        existentResources.add(br);

        List<LocalDate[]> periods
                = cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31))
                .setStep(Step.MONTH)
                .generatePeriods();

        List<RequiredResource> result = entriesGenerator.getCalendarEntries(existentResources, periods, () -> {
                    RequiredResource res = new RequiredResource();
                    res.setPersisted(false);
                    return res;
                });

        assertEquals(12, result.size());
//        assertEquals(existentResources.get(0), result.get(0));
        assertEquals(existentResources.get(1), result.get(5));
    }

    @Test
    public void testWeeksMethods() {
        LocalDate date = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.GERMANY);

        System.out.println("results:");
        System.out.println(date.get(weekFields.weekBasedYear()));
        System.out.println(date.get(weekFields.weekOfYear()));
        System.out.println(date.get(weekFields.weekOfWeekBasedYear()));
        System.out.println(date.with(TemporalAdjusters.lastDayOfYear()).get(weekFields.weekOfYear()));

    }
}

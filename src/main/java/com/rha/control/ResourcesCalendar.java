package com.rha.control;

import com.rha.entity.BookedResource;
import com.rha.entity.Step;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author alacambra
 */
public class ResourcesCalendar {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<BookedResource> existentResources;
    private List<BookedResource> calenderEntries;
    private Step step;

    private LocalDate currentDate;

    public LocalDate getStartDate() {
        return startDate;
    }

    public ResourcesCalendar setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ResourcesCalendar setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public List<BookedResource> getExistentResources() {
        return existentResources;
    }

    public ResourcesCalendar setExistentResources(List<BookedResource> existentResources) {
        this.existentResources = existentResources.stream().sorted().collect(Collectors.toList());
        return this;
    }

    public List<BookedResource> getCalendarEntries() {

        generateEntries();

        if (existentResources == null) {
            return calenderEntries;
        }

        Iterator<BookedResource> it = existentResources.iterator();
        BookedResource pointer;

        if (it.hasNext()) {
            pointer = it.next();
        } else {
            return calenderEntries;
        }

        for (int i = 0; i < calenderEntries.size(); i++) {

            BookedResource br = calenderEntries.get(i);

            if (pointer != null && areEquals(br.getStartDate(), pointer.getStartDate())) {
                calenderEntries.set(i, pointer);

                if (it.hasNext()) {
                    pointer = it.next();
                } else {
                    break;
                }
            }
        }

        return calenderEntries;

    }

    public Step getStep() {
        return step;
    }

    public ResourcesCalendar setStep(Step step) {
        this.step = step;
        return this;
    }

    private int getDaysBetweenYears(int startYear, int endYear) {
        startYear += 1;
        endYear -= 1;

        if (startYear > endYear) {
            return 0;
        } else if (startYear == endYear) {
            return LocalDate.ofYearDay(startYear, 1).lengthOfYear();
        } else {

            final int sy = startYear;
            final int ey = endYear;

            return IntStream.rangeClosed(sy, ey).boxed()
                    .map(year -> LocalDate.ofYearDay(year, 1).lengthOfYear())
                    .reduce(Integer::sum).orElse(0);
        }

    }

    private int getWeeksBetweenYears(int startYear, int endYear, WeekFields weekFields) {
        startYear += 1;
        endYear -= 1;

        if (startYear > endYear) {
            return 0;
        } else if (startYear == endYear) {
            return LocalDate.ofYearDay(startYear, 1).get(weekFields.weekOfYear());
        } else {

            final int sy = startYear;
            final int ey = endYear;

            return IntStream.rangeClosed(sy, ey).boxed()
                    .map(year -> LocalDate.ofYearDay(year, 1)
                            .with(TemporalAdjusters.lastDayOfYear())
                            .get(weekFields.weekOfYear()))
                    .reduce(Integer::sum).orElse(0);
        }

    }

    private void generateEntries() {

        int totalEntries = 0;
        currentDate = startDate;

        if (step == Step.MONTH) {
            if (endDate.getYear() == startDate.getYear()) {
                totalEntries = endDate.getMonth().getValue() - startDate.getMonth().getValue() + 1;
            } else {
                totalEntries = endDate.getMonth().getValue()
                        + (13 - startDate.getMonth().getValue())
                        + (endDate.getYear() - startDate.getYear() - 1) * 12;
            }

        } else if (step == Step.DAY) {

            if (endDate.getYear() == startDate.getYear()) {
                totalEntries = endDate.getDayOfYear() - startDate.getDayOfYear() + 1;
            } else {
                totalEntries = endDate.getDayOfYear()
                        + getDaysBetweenYears(startDate.getYear(), endDate.getYear())
                        + (startDate.lengthOfYear() - startDate.getDayOfYear() + 1);
            }

        } else if (step == Step.WEEK) {

            WeekFields weekFields = WeekFields.of(Locale.GERMANY);

            if (endDate.getYear() == startDate.getYear()) {
                totalEntries
                        = endDate.get(weekFields.weekOfYear()) - startDate.get(weekFields.weekOfYear()) + 1;
            } else {
                totalEntries = endDate.get(weekFields.weekOfYear())
                        + getWeeksBetweenYears(startDate.getYear(), endDate.getYear(), weekFields)
                        + (startDate.with(TemporalAdjusters.lastDayOfYear())
                        .get(weekFields.weekOfYear())
                        - startDate.get(weekFields.weekOfYear()) + 1);
            }
        }

        calenderEntries = IntStream.range(0, totalEntries).boxed().map(i -> {

            LocalDate[] newPeriod = supplyNextPeriod();
            BookedResource b = new BookedResource();
            b.setStartDate(newPeriod[0]);
            b.setEndDate(newPeriod[1]);
            b.setPersisted(false);
            b.setBooked(0);
            b.setPosition(i);

            return b;

        }).collect(Collectors.toList());
    }

    private LocalDate[] supplyNextPeriod() {

        currentDate = currentDate.plusDays(1);

        if (step == Step.DAY) {

            return new LocalDate[]{currentDate, currentDate};

        } else if (step == Step.WEEK) {

            LocalDate startPeriodDate = currentDate;
            LocalDate endPeriodDate = currentDate.plusWeeks(1);
            currentDate = endPeriodDate;
            return new LocalDate[]{startPeriodDate, endPeriodDate};

        } else if (step == Step.MONTH) {

            LocalDate startPeriodDate = currentDate;
            LocalDate endPeriodDate = currentDate.plusMonths(1).minusDays(1);
            currentDate = endPeriodDate;
            return new LocalDate[]{startPeriodDate, endPeriodDate};

        }

        throw new RuntimeException("invalid step type");

    }

    private Boolean areEquals(LocalDate d1, LocalDate d2) {

        return d1.getDayOfMonth() == d1.getDayOfMonth()
                && d1.getMonth() == d2.getMonth()
                && d1.getYear() == d2.getYear();

    }
}

package com.rha.control;

import com.rha.entity.Step;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class CalendarPeriodsGenerator{

    private LocalDate startDate;
    private LocalDate endDate;
    private Step step;
    private LocalDate currentDate;

    public CalendarPeriodsGenerator() {
//        this.supplier = () -> {
//            BookedResource br = new BookedResource();
//            br.setPersisted(false);
//            return (T) br;
//        };
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public CalendarPeriodsGenerator setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    
    public CalendarPeriodsGenerator setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public Step getStep() {
        return step;
    }

    public CalendarPeriodsGenerator setStep(Step step) {
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

    public List<LocalDate[]> generatePeriods() {

        int totalEntries = 0;

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
        } else if (step == Step.BIWEEK) {

            int starDateOffset = 0;
            int endDateOffset = 0;

            if (startDate.getDayOfMonth() > 15) {
                startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), 16);
                starDateOffset = 1;
            } else {
                startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
                starDateOffset = 2;
            }

            if (endDate.getDayOfMonth() > 15) {
                endDate = LocalDate.of(endDate.getYear(), endDate.getMonth(),
                        endDate.getMonth().length(endDate.isLeapYear()));
                endDateOffset = 2;
            } else {
                endDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), 15);
                endDateOffset = 1;
            }

            if (endDate.getYear() == startDate.getYear()) {

                int totalMiddleMonths = endDate.getMonthValue() - startDate.getMonthValue() - 1;
                if (totalMiddleMonths < 0) {

                    totalMiddleMonths = 0;
                    totalEntries = starDateOffset == 1 || endDateOffset == 1 ? 1 : 2;

                } else {

                    totalEntries = starDateOffset + endDateOffset + totalMiddleMonths * 2;
                }

            } else {

                int stepsUntilYearEnd = (12 - startDate.getMonthValue()) * 2 + starDateOffset;
                int stepUntilYearBegin = (endDate.getMonthValue() - 1) * 2 + endDateOffset;
                int totalMiddleYears = endDate.getYear() - startDate.getYear() - 1;

                if (totalMiddleYears < 0) {
                    totalMiddleYears = 0;
                }

                totalEntries = stepUntilYearBegin + stepsUntilYearEnd + totalMiddleYears * 2 * 12;
            }
        }

        currentDate = startDate;
        List<LocalDate[]> generatedPeriods = new ArrayList<>();

        for (int i = 0; i < totalEntries; i++) {
              generatedPeriods.add(supplyNextPeriod());  
        }
        
        return generatedPeriods;
    }

    private LocalDate[] supplyNextPeriod() {

        if (step == Step.DAY) {
            currentDate = currentDate.plusDays(1);
            return new LocalDate[]{currentDate, currentDate};

        } else if (step == Step.WEEK) {

            LocalDate startPeriodDate = currentDate;
            LocalDate endPeriodDate = currentDate.plusWeeks(1);
            currentDate = endPeriodDate;
            currentDate = currentDate.plusDays(1);
            return new LocalDate[]{startPeriodDate, endPeriodDate};

        } else if (step == Step.MONTH) {

            LocalDate startPeriodDate = currentDate;
            LocalDate endPeriodDate = currentDate.plusMonths(1).minusDays(1);
            currentDate = endPeriodDate;
            currentDate = currentDate.plusDays(1);
            return new LocalDate[]{startPeriodDate, endPeriodDate};

        } else if (step == Step.BIWEEK) {

            LocalDate startPeriodDate = currentDate;
            LocalDate endPeriodDate = null;

            if (startPeriodDate.getDayOfMonth() == 1) {

                endPeriodDate = LocalDate.of(
                        startPeriodDate.getYear(),
                        startPeriodDate.getMonth(),
                        15);

            } else if (startPeriodDate.getDayOfMonth() == 16) {

                endPeriodDate = LocalDate.of(
                        startPeriodDate.getYear(),
                        startPeriodDate.getMonth(),
                        startPeriodDate.getMonth().length(startPeriodDate.isLeapYear()));

            } else {
                throw new RuntimeException("Invalid start period day for biweekly step");
            }

            currentDate = endPeriodDate;
            currentDate = currentDate.plusDays(1);
            return new LocalDate[]{startPeriodDate, endPeriodDate};

        }

        throw new RuntimeException("invalid step type");

    }
}

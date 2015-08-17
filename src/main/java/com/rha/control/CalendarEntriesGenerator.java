/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import com.rha.entity.PeriodWithValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class CalendarEntriesGenerator {

    @Inject
    Logger logger;

    /**
     *
     * @param <T>
     * @param periodicEntities: entities loaded from db
     * @param periods: periods to load
     * @param supplier: supplier to build the new desired objects
     * @return
     */
    public <T extends PeriodWithValue> List<T> getCalendarEntries2(
            List<T> periodicEntities, List<LocalDate[]> periods, Supplier<T> supplier) {

        List<T> generatedEntries = new ArrayList<>();

        Iterator<T> it = periodicEntities.iterator();
        T pointer = null;

        if (it.hasNext()) {
            pointer = it.next();

            if (pointer.getStartDate().isBefore(periods.get(0)[0])) {
                logger.log(Level.SEVERE, "pointer can not be before than first period: {0} : {1}", new Object[]{
                    pointer.getStartDate(), periods.get(0)[0]
                });
            }
        }

        int i = 0;
        for (LocalDate[] period : periods) {

            if (pointer != null && pointer.getStartDate().isBefore(period[0])) {

                logger.fine("pointer earlier than the period. Synchronising...");

                while (pointer != null && pointer.getStartDate().isBefore(period[0])) {
                    if (it.hasNext()) {
                        pointer = it.next();
                    } else {
                        pointer = null;
                    }
                }
            }

            if (pointer != null && areEquals(period[0], pointer.getStartDate())) {

                generatedEntries.add(pointer);

                if (it.hasNext()) {
                    pointer = it.next();
                } else {
                    pointer = null;
                }

            } else {
                T entity = generateEntity(supplier, period);
                generatedEntries.add(entity);
            }

            i++;
        }

        return generatedEntries;
    }

    public <T extends PeriodWithValue> List<T> getCalendarEntries(
            List<T> periodicEntities, List<LocalDate[]> allPeriods, Supplier<T> supplier) {

        logger.finer("all periods: " + allPeriods.size());
        logger.finer("total entities:" + periodicEntities.size());
        Map<LocalDate, LocalDate[]> dates = new HashMap<>();

        Set<LocalDate> periods = allPeriods.stream().map(p -> {
            dates.put(p[0], p);
            return p[0];
        }).collect(Collectors.toSet());

        periodicEntities.stream().map(p -> p.getStartDate()).forEach(date -> {
            if(!periods.contains(date)){
                logger.info("Invalid dte fetch. Date " +  LocalDateConverter.toDate(date));
                
            }
            periods.remove(date);
        });
        logger.finer("to introduce: " + periods.size());

        periods.stream().forEach(d -> periodicEntities.add(generateEntity(supplier, dates.get(d))));
        logger.finer("total after generation: " + periodicEntities.size());

        List<T> list = periodicEntities.stream().sorted((a, b) -> a.getStartDate().compareTo(b.getStartDate())).collect(Collectors.toList());
        logger.finer("total after reduction: " + list.size());
        return list;
    }

    private <T extends PeriodWithValue> T generateEntity(Supplier<T> supplier, LocalDate[] period) {
        T entity = supplier.get();
        entity.setPeriod(period);
        entity.setValue(0f);

        return entity;
    }

    private Boolean areEquals(LocalDate d1, LocalDate d2) {

        return d1.getDayOfMonth() == d2.getDayOfMonth()
                && d1.getMonth() == d2.getMonth()
                && d1.getYear() == d2.getYear();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import com.rha.entity.PeriodWithValue;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class CalendarEntriesGenerator implements Serializable{

    @Inject
    transient Logger logger;

    /**
     * For the given periodicEntities, generates periods to fulfill all calendar slots
     * @param <T> type of the periodic value
     * @param periodicEntities entities loaded from DB
     * @param allPeriods  periods to load
     * @param supplier a supplier to generate PeridosWithVAlue of type T
     * @return 
     */
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import com.rha.entity.PeriodWithValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
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
    public <T extends PeriodWithValue> List<T> getCalendarEntries(
            List<T> periodicEntities, List<LocalDate[]> periods, Supplier<T> supplier) {

        List<T> generatedEntries = new ArrayList<>();

        Iterator<T> it = periodicEntities.iterator();
        T pointer = null;

        if (it.hasNext()) {
            pointer = it.next();

            if (pointer.getStartDate().isBefore(periods.get(0)[0])) {
                throw new RuntimeException("pointer can not be before than first period");
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

                pointer.setPosition(i);
                generatedEntries.add(pointer);

                if (it.hasNext()) {
                    pointer = it.next();
                } else {
                    pointer = null;
                }

            } else {

                T entity = supplier.get();
                entity.setPeriod(period);
                entity.setPosition(i);
                entity.setValue(0L);

                generatedEntries.add(entity);
            }

            i++;
        }

        return generatedEntries;

    }

    private Boolean areEquals(LocalDate d1, LocalDate d2) {

        return d1.getDayOfMonth() == d2.getDayOfMonth()
                && d1.getMonth() == d2.getMonth()
                && d1.getYear() == d2.getYear();
    }
}

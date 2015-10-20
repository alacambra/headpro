/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.control;

import io.headpro.entity.PeriodWithValue;

/**
 *
 * @author alacambra
 */
public class PeriodComparator implements java.util.Comparator<PeriodWithValue> {

    @Override
    public int compare(PeriodWithValue o1, PeriodWithValue o2) {

        if (o1 == null || o2 == null) {
            throw new RuntimeException("values cannot be null");
        }

        if (o1.getStartDate() == null && o2.getStartDate() == null) {
            return 0;
        }

        if (o1.getStartDate() == null) {
            return -1;
        }

        if (o2.getStartDate() == null) {
            return 1;
        }

        if (o1.getStartDate().isAfter(o2.getStartDate())) {
            return 1;
        } else if (o1.getStartDate().isEqual(o2.getStartDate())) {
            return 0;
        } else {
            return -1;
        }
    }

}

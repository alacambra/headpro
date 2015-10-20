/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.rha.control;

import io.rha.entity.PeriodTotal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.core.Is.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class PeriodTotalsMergerIT {
    
    public PeriodTotalsMergerIT() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testReduce() {
        
        LocalDate date = LocalDate.now();
        
        PeriodTotal pt1 = new PeriodTotal().setStartDate(date).setTotal(5f);
        PeriodTotal pt2 = new PeriodTotal().setStartDate(date.plusMonths(1)).setTotal(10f);
        PeriodTotal pt3 = new PeriodTotal().setStartDate(date.plusMonths(2)).setTotal(25f);
        
        List<PeriodTotal> totals = PeriodTotalsMerger.reduce(Arrays.asList(pt1, pt2, pt3), Arrays.asList(pt1, pt2));
        
        assertThat(totals.size(), is(3));
        assertThat(totals.get(0).getTotal(), is(10f));
        assertThat(totals.get(1).getTotal(), is(20f));
        assertThat(totals.get(2).getTotal(), is(25f));
    }
    
}

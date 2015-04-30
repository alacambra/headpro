/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import com.rha.entity.Step;
import com.rha.entity.StepPeriod;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alacambra
 */
public class CalendarGenerator {
    
    LocalDate startDate;
    LocalDate endDate;
    Step step;
    LocalDate last;

    public CalendarGenerator(LocalDate startDate, LocalDate endDate, Step step) {
        
        this.startDate = 
                LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
        
        this.endDate = 
                LocalDate.of(
                        endDate.getYear(), 
                        endDate.getMonth(), 
                        endDate.lengthOfMonth());
        
        this.step = step;
        last = this.startDate;
                
    }
    
    public List<StepPeriod> getEntries(){
        
        List<StepPeriod> periods =  new ArrayList<>();
            
        while (last.compareTo(endDate) <= 0) {
            periods.add(supplyPeriod());            
        }
            
        return periods;
    }
    
    private StepPeriod supplyPeriod(){
        
        StepPeriod period =  new StepPeriod();
        LocalDate current = last;
        
        if(step ==  Step.DAY){     
            last = current;
            
        }else if(step == Step.WEEK){
            last =  current.plusWeeks(1);
            
        }else if(step == Step.MONTH){
            last = current.plusMonths(1).minusDays(1);
        }
        
        period.setStartLocalDate(current);
        period.setEndLocalDate(last);
        period.setStepType(step);
        
        last = last.plusDays(1);
        
        return period;
    }
    
    
    
}

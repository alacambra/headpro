/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.entity;

import java.time.LocalDate;

/**
 *
 * @author alacambra
 */
public interface PeriodWithValue {

    LocalDate getStartDate();
    LocalDate getEndDate();
    Float getValue();
    
    void setValue(Float o);
    void setPeriod(LocalDate[] period);
    

}

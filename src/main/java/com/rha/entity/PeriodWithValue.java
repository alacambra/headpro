/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.entity;

import java.time.LocalDate;

/**
 *
 * @author alacambra
 */
public interface PeriodWithValue {

    Integer getPosition();
    void setPosition(int position);

    LocalDate getStartDate();
    void setStartDate(LocalDate date);

    LocalDate getEndDate();
    void setEndDate(LocalDate date);
    
    void setValue(Object o);
    
    void setPeriod(LocalDate[] period);

//    Step getStep();
}

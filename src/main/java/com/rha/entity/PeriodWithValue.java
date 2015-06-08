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
    LocalDate getStartDate();
    LocalDate getEndDate();
    Long getValue();
    
    void setPosition(int position);
    void setValue(Long o);
    void setPeriod(LocalDate[] period);
    

}

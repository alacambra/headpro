/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author alacambra
 */
public class StepPeriod {
    
    LocalDate startLocalDate;
    LocalDate endLocalDate;
    Integer position;
    Step stepType;

    public LocalDate getStartLocalDate() {
        return startLocalDate;
    }

    public void setStartLocalDate(LocalDate startLocalDate) {
        this.startLocalDate = startLocalDate;
    }

    public LocalDate getEndLocalDate() {
        return endLocalDate;
    }

    public void setEndLocalDate(LocalDate endLocalDate) {
        this.endLocalDate = endLocalDate;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Step getStepType() {
        return stepType;
    }

    public void setStepType(Step stepType) {
        this.stepType = stepType;
    }

    @Override
    public String toString() {
        
        StringBuilder builder = new StringBuilder();
        builder.append(position)
                .append(":")
                .append(stepType)
                .append(":")
                .append(startLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .append(":")
                .append(endLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        return builder.toString();
    }
    
    
    
    
}

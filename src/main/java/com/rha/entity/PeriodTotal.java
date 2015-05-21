/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.entity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author alacambra
 */
public class PeriodTotal {
    LocalDate date;
    long total;

    public PeriodTotal(Date date, Long total) {
        this.date = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.total = total;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getTotal() {
        return total;
    }    
}

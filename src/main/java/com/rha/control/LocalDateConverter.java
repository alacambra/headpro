/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author alacambra
 */
public class LocalDateConverter {
    
    public static Date toDate(LocalDate date){
        return toDate(date, ZoneId.systemDefault());
    }
    
    public static Date toDate(LocalDate date, ZoneId zoneId){
        Instant instant = date.atStartOfDay().atZone(zoneId).toInstant();
        return Date.from(instant);
    }
    
    public static LocalDate toLocalDate(Date date){
        return toLocalDate(date, ZoneId.systemDefault());
    }
    
    public static LocalDate toLocalDate(Date date, ZoneId zoneId){
        return date.toInstant().atZone(zoneId).toLocalDate();
    }
}

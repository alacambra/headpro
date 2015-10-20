/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.control;

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
        return toLocalDate(new Date(date.getTime()), ZoneId.systemDefault());
    }
    
    public static LocalDate toLocalDate(Date date, ZoneId zoneId){
        if(date instanceof java.sql.Date) throw new RuntimeException("sql date not accepted");
        return date.toInstant().atZone(zoneId).toLocalDate();
    }
    
    public static LocalDate toLocalDate(java.sql.Date date){
        return toLocalDate(date, ZoneId.systemDefault());
    }
    
    public static LocalDate toLocalDate(java.sql.Date date, ZoneId zoneId){
        return toLocalDate(new Date(date.getTime()), zoneId);
    }
}

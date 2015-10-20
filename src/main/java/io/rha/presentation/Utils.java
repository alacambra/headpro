/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.rha.presentation;

import io.rha.control.LocalDateConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author alacambra
 */
public class Utils {

    public static String defaultDateFormat(LocalDate date) {
        return defaultDateFormat(LocalDateConverter.toDate(date));
    }

    public static String defaultDateFormat(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    public static int getCalenderWeekOf(LocalDate date) {
        WeekFields fields = WeekFields.of(Locale.GERMANY);
        int kw = date.get(fields.weekOfYear());
        return kw;                
    }
}

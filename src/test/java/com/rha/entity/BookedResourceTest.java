/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.entity;

import org.hamcrest.core.Is;
import static org.hamcrest.core.Is.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class BookedResourceTest {

    BookedResource cut;

    public BookedResourceTest() {
    }

    @Before
    public void setUp() {
        cut = new BookedResource();
    }

    @Test
    public void testGetPrettifiedBooked() {
        cut.setBooked(1L);
        String pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("0.01"));
        
        cut.setBooked(9L);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("0.09"));

        cut.setBooked(10L);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("0.10"));
        
        cut.setBooked(110L);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("1.10"));
        
        cut.setBooked(23110L);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("231.10"));
        
        cut.setBooked(23115L);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("231.15"));
        
        cut.setBooked(230L);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("2.30"));
    }

    @Test
    public void testSetPrettifiedBooked() {
        cut.setPrettifiedBooked("0.1");
        assertThat(cut.getBooked(), is(10L));
        
        cut.setPrettifiedBooked("0.9");
        assertThat(cut.getBooked(), is(90L));
        
        cut.setPrettifiedBooked("0.10");
        assertThat(cut.getBooked(), is(10L));
        
        cut.setPrettifiedBooked("0.14");
        assertThat(cut.getBooked(), is(14L));
        
        cut.setPrettifiedBooked("22.14");
        assertThat(cut.getBooked(), is(2214L));
        
        cut.setPrettifiedBooked("14");
        assertThat(cut.getBooked(), is(1400L));
        
        cut.setPrettifiedBooked("1476.87645");
        assertThat(cut.getBooked(), is(147687L));
        assertThat(cut.getPrettifiedBooked(), is("1476.87"));
    }

}

/*
 * To change this license header, choose ficense Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.entity;

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
        cut.setBooked(1f);
        String pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("0.01"));
        
        cut.setBooked(9f);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("0.09"));

        cut.setBooked(10f);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("0.10"));
        
        cut.setBooked(110f);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("1.10"));
        
        cut.setBooked(23110f);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("231.10"));
        
        cut.setBooked(23115f);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("231.15"));
        
        cut.setBooked(230f);
        pretty = cut.getPrettifiedBooked();

        assertThat(pretty, is("2.30"));
    }

    @Test
    public void testSetPrettifiedBooked() {
        cut.setPrettifiedBooked("0.1");
        assertThat(cut.getBooked(), is(10f));
        
        cut.setPrettifiedBooked("0.9");
        assertThat(cut.getBooked(), is(90f));
        
        cut.setPrettifiedBooked("0");
        assertThat(cut.getBooked(), is(0f));
        
        cut.setPrettifiedBooked("0.10");
        assertThat(cut.getBooked(), is(10f));
        
        cut.setPrettifiedBooked("0.14");
        assertThat(cut.getBooked(), is(14f));
        
        cut.setPrettifiedBooked("22.14");
        assertThat(cut.getBooked(), is(2214f));
        
        cut.setPrettifiedBooked("14");
        assertThat(cut.getBooked(), is(1400f));
        
        cut.setPrettifiedBooked("1476.87645");
        assertThat(cut.getBooked(), is(147687f));
        assertThat(cut.getPrettifiedBooked(), is("1476.87"));
    }

}

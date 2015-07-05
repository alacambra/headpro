/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author alacambra
 */
public class BookedResourceControllerIT {

    BookedResourceController cut;

    EntityManager em;
    EntityTransaction tx;

    public BookedResourceControllerIT() {
        cut = new BookedResourceController();
    }

    @Before
    public void setUp() {
        this.em = Persistence.createEntityManagerFactory("it").createEntityManager();
        this.tx = this.em.getTransaction();
        tx.begin();
    }

    @After
    public void tearDown() {
        tx.commit();
    }

    @Test
    public void testLoadBookedResourcesForPeriod() {
    }

    @Test
    public void testGetBookingRow() {
        
        cut.getTotalBooking();
    }

    @Test
    public void testGetPeriods() {
    }

    @Test
    public void testOnCellEdit() {
    }

    @Test
    public void testGetAreaModel() {
    }

    @Test
    public void testGetTotalBooking() {
    }
}

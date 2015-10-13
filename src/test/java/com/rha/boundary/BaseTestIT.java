/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.airhacks.enhydrator.Pump;
import com.airhacks.enhydrator.in.*;
import com.airhacks.enhydrator.in.Column;
import com.airhacks.enhydrator.out.LogSink;
import com.airhacks.enhydrator.transform.Memory;
import com.airhacks.enhydrator.transform.SkipFirstRow;
import com.rha.entity.AvailableResource;
import com.rha.entity.Service;
import com.rha.presentation.AvailableResourceController;
import org.junit.After;
import org.junit.Before;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author alacambra
 */
public class BaseTestIT {

    final static String INPUT = "./src/test/resources/";
    EntityManager em;
    EntityTransaction tx;
    AvailableResourceController cut;

    @Before
    public void setUp() {

        this.em = Persistence.createEntityManagerFactory("it").createEntityManager();
        this.tx = this.em.getTransaction();
        clearTables();
    }

    @After
    public void tearDown() {
        clearTables();
    }

    private void clearTables() {
        try {

            tx.begin();

            Query q1 = em.createNativeQuery("DELETE FROM AvailableResource");
            Query q2 = em.createNativeQuery("DELETE FROM RequiredResource");
            Query q3 = em.createNativeQuery("DELETE FROM Project");
            Query q4 = em.createNativeQuery("DELETE FROM Service");

            q1.executeUpdate();
            q2.executeUpdate();
            q3.executeUpdate();
            q4.executeUpdate();

            tx.commit();
        } catch (SecurityException | IllegalStateException | RollbackException e) {
            e.printStackTrace();
        }
    }

    public void loadServiceTestTable(String testFileName) {
        tx.begin();
        Source source = new CSVFileSource(INPUT + testFileName, ",", "utf-8", true);
        VirtualSinkSource output = new VirtualSinkSource();
        Pump pump = new Pump.Engine()
                .from(source)
                .startWith(new SkipFirstRow())
                .to(new LogSink()).to(output).build();
        Memory memory = pump.start();
        List<Row> rows = output.getRows();
        int totalArs = 0;
        for (Row row : rows) {

            if (row.getColumnByIndex(0).getValue().equals("Total")) {
                continue;
            }

            Service service = persistService(row);
            assertThat(service.getName(), is(row.getColumnByIndex(0).getValue()));
            assertThat(service.getId(), notNullValue());

            for (int i = 1; i < row.getColumnNames().size(); i++) {
                Column c = row.getColumnByIndex(i);
                String dateAsString = c.getName().toString();

                LocalDate startDate = LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                LocalDate endDate = startDate.getDayOfMonth() == 1 ? startDate.plusDays(14)
                        : startDate.plusDays(startDate.lengthOfMonth() - 16);

                AvailableResource availableResource = new AvailableResource();
                availableResource.setPeriod(new LocalDate[]{startDate, endDate});
                availableResource.setService(service);
                if (c.getValue() == null) continue;
                availableResource.setValue(Float.parseFloat((String) c.getValue()));
                em.merge(availableResource);
                totalArs++;
            }
        }
        tx.commit();
        System.out.println(totalArs);
    }

    private Service persistService(Row row) {

        Service service = new Service();
        String serviceName = "";
        serviceName = (String) row.getColumnByIndex(0).getValue();
        service.setName(serviceName);
        service = em.merge(service);

        return service;
    }
}
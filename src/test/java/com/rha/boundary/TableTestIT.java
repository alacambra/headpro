/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.airhacks.enhydrator.Pump;
import com.airhacks.enhydrator.in.CSVFileSource;
import com.airhacks.enhydrator.in.Row;
import com.airhacks.enhydrator.in.Source;
import com.airhacks.enhydrator.in.VirtualSinkSource;
import com.airhacks.enhydrator.out.LogSink;
import com.airhacks.enhydrator.transform.Memory;
import com.airhacks.enhydrator.transform.SkipFirstRow;
import com.rha.entity.Service;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author alacambra
 */
public class TableTestIT {

    final static String INPUT = "./src/test/resources/";
    EntityManager em;
    EntityTransaction tx;

    @Before
    public void setUp() {
        this.em = Persistence.createEntityManagerFactory("it").createEntityManager();
        this.tx = this.em.getTransaction();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void test() {
        Source source = new CSVFileSource(INPUT + "ServiceTestTable.csv", ",", "utf-8", true);
        VirtualSinkSource output = new VirtualSinkSource();
        Pump pump = new Pump.Engine()
                .from(source)
                .startWith(new SkipFirstRow())
                .to(new LogSink()).to(output).build();
        Memory memory = pump.start();
        List<Row> rows = output.getRows();

        rows.stream().forEach(row -> {
            Service s = persistService(row);
            assertThat(s.getName(), is(row.getColumnByIndex(0).getValue()));
            assertThat(s.getId(), notNullValue());
        });
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

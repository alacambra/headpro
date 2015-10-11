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
import java.util.List;
import org.junit.Test;

/**
 *
 * @author alacambra
 */
public class TableTestIT {

     final static String INPUT = "./src/test/resources/";
    
    @Test
    public void test() {
        Source source = new CSVFileSource(INPUT + "TestTable.csv", ",", "utf-8", true);
        VirtualSinkSource sinkSource = new VirtualSinkSource();
        Pump pump = new Pump.Engine().from(source).to(new LogSink()).to(sinkSource).build();
        Memory memory = pump.start();
        List<Row> rows = sinkSource.getRows();
        rows.stream().forEach(System.out::println);
    }
}

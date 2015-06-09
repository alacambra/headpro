/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import com.rha.entity.BookedResource;
import com.rha.entity.Service;
import com.rha.entity.Project;
import com.rha.entity.Step;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alacambra
 */
@Stateless
public class DataImport {

    @PersistenceContext
    EntityManager em;

    List<Service> services = new ArrayList<>();
    Service service;
    Integer factor;
    Map<String, Project> projects = new HashMap<>();
    int i = 1;

    public void loadData() {

        Service service = new Service();
        service.setName("java");
        service = em.merge(service);
        services.add(service);

        service = new Service();
        service.setName("docker");
        service = em.merge(service);
        services.add(service);

        service = new Service();
        service.setName("aws");
        service = em.merge(service);
        services.add(service);

        service = new Service();
        service.setName("angular");
        service = em.merge(service);
        services.add(service);

        service = new Service();
        service.setName("foundation");
        service = em.merge(service);
        services.add(service);

        services.stream().forEach(s -> {
            try {
                Stream<String> lines = Files.lines(
                        Paths.get("/Users/albertlacambra1/git/rha/src/main/resources/",
                                "servicedata.csv"));

                this.service = s;
                factor = new Random().nextInt(4);

                lines.map(l -> l.split(",")).forEach(this::insertRow);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    void insertRow(String... row) {

        LocalDate currentDate = LocalDate.of(2015, Month.JANUARY, 1);

        String projectName = row[0];

        Project p;
        
        if (projects.containsKey(projectName)) {
            p = projects.get(projectName);
        } else {
            p = new Project();
//            p.setId(i);
            p.setAbscence(10);
            p.setName(projectName);
            p.setProbability(90);
            p.setStep(Step.WEEK);

            p = em.merge(p);
            projects.put(projectName, p);
//            System.out.println(++i);    
        }

        for (int i = 0; i < row.length - 1; i++) {

            LocalDate[] period = supplyNextPeriod(currentDate);

            LocalDate startPeriodDate = period[0];
            LocalDate endPeriodDate = period[1];
            currentDate = period[2];

            BookedResource br = new BookedResource();
            String v = "".equals(row[i + 1]) ? "0" : row[i + 1];
            br.setBooked(Math.round(factor * Double.parseDouble(v)));
            br.setService(service);
            br.setProject(p);
            br.setStartDate(startPeriodDate);
            br.setEndDate(endPeriodDate);
            em.persist(br);
        }
    }

    private LocalDate[] supplyNextPeriod(LocalDate currentDate) {
        LocalDate startPeriodDate = currentDate;
        LocalDate endPeriodDate = null;

        if (startPeriodDate.getDayOfMonth() == 1) {

            endPeriodDate = LocalDate.of(
                    startPeriodDate.getYear(),
                    startPeriodDate.getMonth(),
                    15);

        } else if (startPeriodDate.getDayOfMonth() == 16) {

            endPeriodDate = LocalDate.of(
                    startPeriodDate.getYear(),
                    startPeriodDate.getMonth(),
                    startPeriodDate.getMonth().length(startPeriodDate.isLeapYear()));

        } else {
            throw new RuntimeException("Invalid start period day for biweekly step " + startPeriodDate);
        }

        currentDate = endPeriodDate;
        currentDate = currentDate.plusDays(1);
        return new LocalDate[]{startPeriodDate, endPeriodDate, currentDate};

    }
}

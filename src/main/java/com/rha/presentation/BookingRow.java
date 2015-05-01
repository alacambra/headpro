/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import java.util.List;

/**
 *
 * @author alacambra
 */
public class BookingRow {
    
    String project;
    List<Integer> resources;

    public BookingRow(String project, List<Integer> resources) {
        this.project = project;
        this.resources = resources;
    }
    
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List<Integer> getResources() {
        return resources;
    }

    public void setResources(List<Integer> resources) {
        this.resources = resources;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.entity.Project;
import com.rha.presentation.util.JsfUtil;

/**
 *
 * @author alacambra
 */
public class ProjectEvent {
 
    Project project;
    JsfUtil.PersistAction action;

    public ProjectEvent(Project project, JsfUtil.PersistAction action) {
        this.project = project;
        this.action = action;
    }

    public Project getProject() {
        return project;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.control.DataImport;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author alacambra
 */

@RequestScoped
@Named
public class TasksController {

    @Inject
    DataImport dataImport;

    public void startImport() {
        dataImport.loadData();
    }

}

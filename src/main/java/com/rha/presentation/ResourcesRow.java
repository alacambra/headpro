/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.entity.PeriodWithValue;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ResourcesRow<K, T extends PeriodWithValue> implements Serializable{

    K key;
    List<T> columns;
    String title;
    
    @Inject
    transient Logger logger;
    private boolean rowIsActive;

    public ResourcesRow(List<T> resources, K key) {
        this.columns = resources;
        this.key = key;
    }

    public List<T> getColumns() {
        return columns;
    }

    public K getKey() {
        return key;
    }
    
    public boolean isRowIsActive() {
        return rowIsActive;
    }

    public void setRowIsActive(boolean rowIsActive) {
        this.rowIsActive = rowIsActive;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResourcesRow) {
            final ResourcesRow other = (ResourcesRow) obj;
            return key.equals(other.getKey());
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(key).append(columns).toHashCode();
    }
}

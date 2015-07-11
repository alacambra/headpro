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
    List<T> resources;
    
    @Inject
    transient Logger logger;
    private boolean rowIsActive;

    public ResourcesRow(List<T> resources, K service) {
        this.resources = resources;
        this.key = service;
    }

    public List<T> getResources() {
        return resources;
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
        return new HashCodeBuilder().append(key).append(resources).toHashCode();
    }
}

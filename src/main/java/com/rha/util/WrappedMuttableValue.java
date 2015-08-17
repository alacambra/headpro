/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.util;

/**
 *
 * @author alacambra
 */
public class WrappedMuttableValue<T> {
    
    T value;

    public WrappedMuttableValue(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        if(value == null)
            throw new RuntimeException("Value cannot vbe null");
        this.value = value;
    }
}

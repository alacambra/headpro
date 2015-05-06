/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author alacambra
 */
@RequestScoped
@Named
public class DummyController {
    public List<Integer> getColumns(){
        return IntStream.range(0, 13).boxed().collect(Collectors.toList());
    }
}

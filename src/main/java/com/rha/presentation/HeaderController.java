/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.security.Principal;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;

@SessionScoped
@Named
public class HeaderController implements Serializable {

    KeycloakPrincipal<KeycloakSecurityContext> principal;

    public Principal getPrincipal() {

        if (principal == null) {
            principal = (KeycloakPrincipal<KeycloakSecurityContext>) FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        }
        return principal;
    }

    public String getUser() {
        return principal.getKeycloakSecurityContext().getToken().getEmail();
    }

    public void removePrincipal() {
        this.principal = null;
    }
}

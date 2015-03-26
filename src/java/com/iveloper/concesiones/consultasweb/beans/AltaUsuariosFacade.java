/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.concesiones.consultasweb.beans;

import com.iveloper.concesiones.consultasweb.entities.AltaUsuarios;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author alexbonilla
 */
@Stateless
public class AltaUsuariosFacade extends AbstractFacade<AltaUsuarios> {
    @PersistenceContext(unitName = "altausuariosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AltaUsuariosFacade() {
        super(AltaUsuarios.class);
    }
    
}

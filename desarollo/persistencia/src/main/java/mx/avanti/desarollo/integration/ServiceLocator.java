/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.avanti.desarollo.integration;

import jakarta.persistence.EntityManager;
import mx.avanti.desarollo.dao.*;
import mx.avanti.desarollo.persistence.HibernateUtil;


/**
 *
 * @author total
 */
public class ServiceLocator {

    private static ClienteDAO ClienteDAO;

    private static EntityManager getEntityManager(){
        return HibernateUtil.getEntityManager();
    }

    /**
     * se crea la instancia para Cliente DAO si esta no existe
     */
    public static ClienteDAO getInstanceClienteDAO(){
        if(ClienteDAO == null){
            ClienteDAO = new ClienteDAO(getEntityManager());
            return ClienteDAO;
        } else{
            return ClienteDAO;
        }
    }
    
}

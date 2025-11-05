/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.avanti.desarollo.integration;

import jakarta.persistence.EntityManager;
import mx.avanti.desarollo.dao.*;
import mx.avanti.desarollo.persistence.HibernateUtil;
import mx.desarollo.entity.Producto;

/**
 * Proveedor central de DAOs y EntityManagers.
 * Se asegura de crear un EntityManager NUEVO en cada llamada,
 * evitando problemas de caché de primer nivel.
 */
public class ServiceLocator {

    private ServiceLocator() {} // Evita instancias

    /** Devuelve un nuevo EntityManager cada vez */
    public static EntityManager getEntityManager() {
        return HibernateUtil.getEntityManager();
    }

    /** 🔹 Devuelve un DAO con un EntityManager nuevo */
    public static ClienteDAO getInstanceClienteDAO() {
        return new ClienteDAO(getEntityManager());
    }

    public static ClaseDAO getInstanceClaseDAO() {
        return new ClaseDAO(getEntityManager());
    }

    public static ProductoDAO getInstanceProductoDAO() {
        return new ProductoDAO(getEntityManager());
    }
    /**
     * se crea la instancia de usuarioDAO si esta no existe
     */
    /*
    public static UsuarioDAO getInstanceUsuarioDAO(){
        if(usuarioDAO == null){
            usuarioDAO = new UsuarioDAO(getEntityManager());
            return usuarioDAO;
        } else{
            return usuarioDAO;
        }
    }

     */

}

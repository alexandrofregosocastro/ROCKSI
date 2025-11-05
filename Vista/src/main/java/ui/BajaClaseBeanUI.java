package ui;

import helper.ClaseHelper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("bajaClaseBeanUI")
@SessionScoped
public class BajaClaseBeanUI implements Serializable {
    private String idClase;
    private final ClaseHelper claseHelper = new ClaseHelper();

    public void eliminarClase(){
        System.out.println("recibido: " + idClase);
        try{
            boolean eliminado = claseHelper.eliminarClase(idClase);//Se usa boolean para verificar si se elimino o no

            if(eliminado){
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Baja Exitosa", "Clase eliminado correctamente."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "No encontrado", "No existe ninguna clase con ese ID."));
            }


        }catch(Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar", e.getMessage()));
        }
    }

    public String getIdClase() { return idClase; }
    public void setIdClase(String idClase) { this.idClase = idClase; }


}

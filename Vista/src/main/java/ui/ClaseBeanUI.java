package ui;

import helper.ClaseHelper;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import mx.desarollo.entity.Clase;

@Named("claseBeanUI")
@ViewScoped
public class ClaseBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ClaseHelper claseHelper = new ClaseHelper();

    private List<Clase> listaClases;      // lista filtrada que expone la UI
    private List<Clase> originalClases;   // copia completa
    private String filtro;                // texto del filtro

    public ClaseBeanUI() {
        this.listaClases = new ArrayList<>();
        this.originalClases = new ArrayList<>();
        this.filtro = "";
    }

    @PostConstruct
    public void init() {
        cargarClases();
    }


     //carga todas las clases desde la capa de negocio utilizando ClaseHelper
    public void cargarClases() {
        try {
            List<Clase> obtenidas = claseHelper.listarClases(); // debe implementar listarClases()
            if (obtenidas == null) {
                obtenidas = new ArrayList<>();
            }
            // copia defensiva para evitar ConcurrentModification durante render
            this.originalClases = new ArrayList<>(obtenidas);
            this.listaClases = new ArrayList<>(obtenidas);
        } catch (Exception e) {
            // En caso de error, dejamos listas vacías y escribimos a stderr (puedes cambiar por logger)
            System.err.println("Error al cargar clases: " + e.getMessage());
            e.printStackTrace();
            this.originalClases = new ArrayList<>();
            this.listaClases = new ArrayList<>();
        }
    }

    //este metodo sirve para la busqueda por nombres o id
    public void filtrar() {
        if (filtro == null || filtro.trim().isEmpty()) {
            this.listaClases = new ArrayList<>(this.originalClases);
            return;
        }

        final String q = filtro.trim().toLowerCase(Locale.ROOT);

        try {
            this.listaClases = this.originalClases.stream()
                    .filter(c -> {
                        // Nombre
                        if (c.getNombre() != null && c.getNombre().toLowerCase(Locale.ROOT).contains(q)) {
                            return true;
                        }
                        // Maestro
                        if (c.getMaestro() != null && c.getMaestro().toLowerCase(Locale.ROOT).contains(q)) {
                            return true;
                        }
                        // compara el ID
                        try {
                            String id = null;
                            try {
                                id = (String) c.getClass().getMethod("getIdItem").invoke(c);
                            } catch (NoSuchMethodException ex) {
                                try {
                                    id = (String) c.getClass().getMethod("getIdClase").invoke(c);
                                } catch (NoSuchMethodException ex2) {
                                }
                            }
                            if (id != null && id.toLowerCase(Locale.ROOT).contains(q)) {
                                return true;
                            }
                        } catch (Exception ex) {
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error filtrando clases: " + e.getMessage());
            e.printStackTrace();
            this.listaClases = new ArrayList<>(this.originalClases);
        }
    }

    //metodo para hacer uso de boton actualizar tabla
    public void recargar() {
        cargarClases();
    }

    // --- Getters y Setters ---

    public List<Clase> getListaClases() {
        return listaClases;
    }

    public void setListaClases(List<Clase> listaClases) {
        this.listaClases = listaClases;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}

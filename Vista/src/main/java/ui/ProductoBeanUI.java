package ui;

import helper.ClaseHelper;
import helper.ProductoHelper;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.desarollo.entity.Producto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Named("productoBeanUI")
@ViewScoped
public class ProductoBeanUI implements Serializable {

    private static final long serialVersionUID = 1L;
    private final ProductoHelper productoHelper = new ProductoHelper();

    private List<Producto> listaProductos;      // lista filtrada que expone la UI
    private List<Producto> originalProductos;   // copia completa
    private String filtro;         // texto del filtro
    private Producto productoSeleccionado;

    public ProductoBeanUI() {
        this.listaProductos = new ArrayList<>();
        this.originalProductos = new ArrayList<>();
    }

    @PostConstruct
    public void init() { cargarProductos();
    }


    //carga todas los productos desde la capa de negocio utilizando ProductoHelper
    public void cargarProductos() {
        try {
            List<Producto> obtenidas = productoHelper.listarProductos(); // debe implementar listarProductos()
            if (obtenidas == null) {
                obtenidas = new ArrayList<>();
            }
            // copia defensiva para evitar ConcurrentModification durante render
            this.originalProductos = new ArrayList<>(obtenidas);
            this.listaProductos = new ArrayList<>(obtenidas);
        } catch (Exception e) {
            // En caso de error, dejamos listas vacías y escribimos a stderr (puedes cambiar por logger)
            e.printStackTrace();
            this.originalProductos = new ArrayList<>();
            this.listaProductos = new ArrayList<>();
        }
    }

    //este metodo sirve para la busqueda por nombres o id
    public void filtrarPorId() {
        try {
            if (filtro == null || filtro.isEmpty()) {
                listaProductos = productoHelper.listarProductos();
            } else {
                Producto p = productoHelper.obtenerProducto(filtro);
                listaProductos = new ArrayList<>();
                if (p != null) {
                    listaProductos.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //metodo para hacer uso de boton actualizar tabla
    public void recargar() {
        cargarProductos();
    }

    // --- Getters y Setters ---

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaClases(List<Producto> lista) {
        this.listaProductos = lista;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Producto getProductoSeleccionado() {return productoSeleccionado;}

    public void setProductoSeleccionado(Producto productoSeleccionado) {this.productoSeleccionado = productoSeleccionado;}

}

package com.mycompany.nativo.servicios;

import com.mycompany.nativo.repositorios.ProductoDAO;
import com.mycompany.nativo.repositorios.ProductoDAOImpl;
import com.mycompany.nativo.modelos.Producto;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.ZoneId;

public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAOImpl();
    }
 
    public boolean registrarProducto(Producto producto) {
        if (producto.getNombre().trim().isEmpty() || producto.getPrecio() <= 0 || producto.getProveedorId() <= 0) {
            System.err.println("Error de Negocio: Datos esenciales incompletos (Nombre, Precio o Proveedor ID).");
            return false;
        }
        
        if (producto.getStockMinimo() < 0) {
             producto.setStockMinimo(10);  
        }
        
        try {
            productoDAO.insertar(producto);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Fallo al registrar producto en capa de servicio: " + e.getMessage());
            return false;
        }
    }
     
    public boolean actualizarProducto(Producto producto) {
        if (producto.getId() <= 0) return false;
        if (producto.getNombre().trim().isEmpty() || producto.getPrecio() <= 0 || producto.getProveedorId() <= 0) {
            System.err.println("Error de Negocio: Datos esenciales incompletos.");
            return false;
        }
        
        try {
            productoDAO.actualizar(producto);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Fallo al actualizar producto en capa de servicio: " + e.getMessage());
            return false;
        }
    }
    
 
    public boolean eliminarProducto(int id) {
        if (id <= 0) return false;
        try {
            return productoDAO.eliminar(id);
        } catch (RuntimeException e) {
            System.err.println("Error de Servicio: No se pudo eliminar el producto ID " + id + ". Posiblemente está asociado a una venta.");
            return false;
        }
    }
    
 
    public Producto buscarProductoPorId(int id) {
        if (id <= 0) return null;
        return productoDAO.obtenerPorId(id);
    }
 
    public List<Producto> obtenerTodos() {
        return productoDAO.obtenerTodos();
    }
    
 
    public List<Producto> buscarProductosPorCriterio(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return productoDAO.obtenerTodos();
        }
        return productoDAO.buscarPorCriterio(criterio.trim());
    }

    public List<Producto> obtenerStockBajo() {
        return productoDAO.obtenerTodos().stream()
                .filter(p -> p.getStock() <= p.getStockMinimo())  
                .collect(Collectors.toList());
    }

    public List<Producto> obtenerProximosAVencer() {
    LocalDate limite = LocalDate.now().plusDays(60);
    
    return productoDAO.obtenerTodos().stream()
            .filter(p -> p.getFechaVencimiento() != null)
            .filter(p -> {
                LocalDate fechaVencimiento = p.getFechaVencimiento().toLocalDate();
                return fechaVencimiento.isBefore(limite) || fechaVencimiento.isEqual(limite);
            })
            .collect(Collectors.toList());
    }
}   
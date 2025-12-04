package com.mycompany.nativo.servicios;

import java.util.List;
import com.mycompany.nativo.repositorios.ProveedorDAO;
import com.mycompany.nativo.repositorios.ProveedorDAOImpl;
import com.mycompany.nativo.modelos.Proveedor;

public class ProveedorService {

    private final ProveedorDAO proveedorDAO;
    
    public ProveedorService() {
        this.proveedorDAO = new ProveedorDAOImpl();
    }

    public boolean registrarNuevoProveedor(Proveedor proveedor) {
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            System.err.println("Error de Negocio: El nombre del proveedor no puede estar vacío.");
            return false;
        }
        
        if (proveedor.getRuc() != null && proveedor.getRuc().length() != 11) {
            System.err.println("Error de Negocio: El RUC debe tener 11 dígitos.");
            return false;
        }
        if (proveedor.getRuc() != null && proveedorDAO.obtenerPorRuc(proveedor.getRuc()) != null) {
            System.err.println("Error de Negocio: Ya existe un proveedor con ese RUC.");
            return false;
        }
        
        try {
            proveedorDAO.insertar(proveedor);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Fallo al registrar proveedor en capa de servicio: " + e.getMessage());
            return false;
        }
    }

    public List<Proveedor> listarTodosLosProveedores() {
        return proveedorDAO.obtenerTodos();
    }
    
    public boolean modificarProveedor(Proveedor proveedor) {
        if (proveedor.getId() <= 0) return false;
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) return false;
        
        try {
            proveedorDAO.actualizar(proveedor);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Fallo al actualizar proveedor en capa de servicio: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarProveedor(int id) {
        if (id <= 0) return false;
        try {
            return proveedorDAO.eliminar(id);
        } catch (RuntimeException e) {
            System.err.println("Error de Servicio: No se pudo eliminar el proveedor ID " + id + ". Posiblemente tiene productos asociados (Violación FK).");
            return false;
        }
    }
    
    public Proveedor buscarProveedorPorId(int id) {
        return proveedorDAO.obtenerPorId(id);
    }
    
    public List<Proveedor> buscarProveedoresPorCriterio(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return proveedorDAO.obtenerTodos();
        }
        return proveedorDAO.buscarPorCriterio(criterio.trim());
    }
  
    public Proveedor obtenerPorNombre(String nombre) {
        return proveedorDAO.obtenerPorNombre(nombre);
    }
}
package com.mycompany.nativo.repositorios;
import com.mycompany.nativo.modelos.Proveedor;
import java.util.List;

public interface ProveedorDAO {
    void insertar(Proveedor proveedor);
    void actualizar(Proveedor proveedor);
    boolean eliminar(int id);
    Proveedor obtenerPorId(int id);
    Proveedor obtenerPorRuc(String ruc); 
    List<Proveedor> obtenerTodos();
    List<Proveedor> buscarPorCriterio(String criterio);
    Proveedor obtenerPorNombre(String nombre);
}
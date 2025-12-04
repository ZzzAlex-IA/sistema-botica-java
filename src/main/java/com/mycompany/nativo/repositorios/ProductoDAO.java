package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.Producto;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public interface ProductoDAO {
    void insertar(Producto producto);
    void actualizar(Producto producto);
    boolean eliminar(int id);
    Producto obtenerPorId(int id);
    List<Producto> obtenerTodos();
    void actualizarStockTransaccional(Producto producto, Connection conn) throws SQLException;
    List<Producto> buscarPorCriterio(String criterio); 
}
package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.DetalleVenta;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public interface DetalleVentaDAO {
    void insertarDetalle(DetalleVenta detalle, Connection conn) throws SQLException; 
    List<DetalleVenta> obtenerDetallesPorVenta(int ventaId);
    List<com.mycompany.nativo.modelos.DetalleVenta> obtenerTodos();
}
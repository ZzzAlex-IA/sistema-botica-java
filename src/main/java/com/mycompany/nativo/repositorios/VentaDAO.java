package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.Venta;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import com.mycompany.nativo.modelos.DetalleVenta;
import java.util.Map;

public interface VentaDAO {

    void insertarVenta(Venta venta, Connection conn) throws SQLException; 
    void insertarDetalleVenta(DetalleVenta detalle, Connection conn) throws SQLException;
 
    Venta obtenerPorId(int id);
    List<Venta> obtenerTodos();
    void eliminar(int id) throws SQLException;
 
    List<Venta> obtenerVentasPorUsuario(int usuarioId);   
    List<Map<String, Object>> obtenerTopProductosVendidos();   
    
}
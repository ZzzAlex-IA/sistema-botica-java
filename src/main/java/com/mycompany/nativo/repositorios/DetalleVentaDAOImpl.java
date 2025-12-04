package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.ConexionDB;
import com.mycompany.nativo.modelos.DetalleVenta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaDAOImpl implements DetalleVentaDAO {
    
    @Override
    public void insertarDetalle(DetalleVenta detalle, Connection conn) throws SQLException {
        String sql = "INSERT INTO detalle_venta (venta_id, producto_id, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detalle.getVentaId());
            ps.setInt(2, detalle.getProductoId());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getSubtotal()); 

            ps.executeUpdate();
        }
    }
    
    @Override
    public List<DetalleVenta> obtenerDetallesPorVenta(int ventaId) {

        return new ArrayList<>(); 
    }

    @Override
    public List<com.mycompany.nativo.modelos.DetalleVenta> obtenerTodos() {
        List<com.mycompany.nativo.modelos.DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT id, venta_id, producto_id, cantidad, subtotal FROM detalle_venta";

        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                com.mycompany.nativo.modelos.DetalleVenta d = new com.mycompany.nativo.modelos.DetalleVenta();
                d.setId(rs.getInt("id"));
                d.setVentaId(rs.getInt("venta_id"));
                d.setProductoId(rs.getInt("producto_id"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setSubtotal(rs.getDouble("subtotal"));
                detalles.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los detalles de venta: " + e.getMessage());
        }
        return detalles;
    }
}
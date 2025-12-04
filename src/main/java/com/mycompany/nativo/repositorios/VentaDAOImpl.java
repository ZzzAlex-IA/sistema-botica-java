package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.Venta;
import com.mycompany.nativo.ConexionDB;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class VentaDAOImpl implements VentaDAO {
    
    private Venta mapearVenta(ResultSet rs) throws SQLException {
        return new Venta(
            rs.getInt("id"),
            rs.getDate("fecha"),
            rs.getInt("usuario_id"),
            rs.getString("cliente_documento"), 
            rs.getString("metodo_pago"),     
            rs.getDouble("monto_total")
        );
    }

    @Override
    public void insertarVenta(Venta venta, java.sql.Connection conn) throws java.sql.SQLException {
        String sql = "INSERT INTO venta (fecha, usuario_id, cliente_documento, metodo_pago, monto_total) VALUES (?, ?, ?, ?, ?)";

        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setDate(1, venta.getFecha()); 
            ps.setInt(2, venta.getUsuarioId());
            ps.setString(3, venta.getClienteDocumento()); 
            ps.setString(4, venta.getMetodoPago());     
            ps.setDouble(5, venta.getMontoTotal()); 

            ps.executeUpdate(); 

            try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    venta.setId(rs.getInt(1)); 
                } else {
                    throw new java.sql.SQLException("Error de inserción. La PK no se generó.");
                }
            }
        }
    }

    @Override
    public void insertarDetalleVenta(com.mycompany.nativo.modelos.DetalleVenta detalle, Connection conn) throws SQLException {
   
        String sql = "INSERT INTO detalle_venta (venta_id, producto_id, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detalle.getVentaId());
            ps.setInt(2, detalle.getProductoId());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getSubtotal()); 

            ps.executeUpdate();
        }
    }
    
 
    @Override
    public Venta obtenerPorId(int id) {
        Venta venta = null;
        String sql = "SELECT id, fecha, usuario_id, cliente_documento, metodo_pago, monto_total FROM venta WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);   

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    venta = mapearVenta(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener venta por ID: " + e.getMessage());
        }
        return venta;
    }

    @Override
    public List<Venta> obtenerTodos() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, fecha, usuario_id, cliente_documento, metodo_pago, monto_total FROM venta ORDER BY fecha DESC";

        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Error al obtener todas las ventas: " + e.getMessage());
        }
        return ventas;
    }
    
    @Override
    public void eliminar(int id) throws java.sql.SQLException {
        String sql = "DELETE FROM venta WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar venta ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Fallo al eliminar venta.", e);
        }
    }
 
    @Override
    public List<Venta> obtenerVentasPorUsuario(int usuarioId) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, fecha, usuario_id, cliente_documento, metodo_pago, monto_total FROM venta WHERE usuario_id = ? ORDER BY fecha DESC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al obtener ventas por usuario: " + e.getMessage());
        }
        return ventas;
    }
 
    @Override
    public List<Map<String, Object>> obtenerTopProductosVendidos() {
        List<Map<String, Object>> topProductos = new ArrayList<>();
        String sql = "SELECT dv.producto_id, p.nombre, SUM(dv.cantidad) AS cantidadTotal " +
                     "FROM detalle_venta dv JOIN producto p ON dv.producto_id = p.id " +
                     "GROUP BY dv.producto_id, p.nombre ORDER BY cantidadTotal DESC LIMIT 10";

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
               
            conn = ConexionDB.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(sql);

           while (rs.next()) {
                Map<String, Object> item = new HashMap<>(); 

                item.put("nombreProducto", rs.getString("nombre")); 
                item.put("cantidadTotal", rs.getInt("cantidadTotal"));
                topProductos.add(item);

                System.out.println("DEBUG DAO: Fila de producto más vendido encontrada."); 
            }

            System.out.println("DEBUG DAO: Filas totales encontradas: " + topProductos.size());
        } catch (SQLException e) {
            System.err.println("Error al obtener Top Productos Vendidos: " + e.getMessage());
            throw new RuntimeException("Error en la consulta de Top Productos Vendidos. Revisar log.", e); 
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (st != null) st.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return topProductos;
    }
}
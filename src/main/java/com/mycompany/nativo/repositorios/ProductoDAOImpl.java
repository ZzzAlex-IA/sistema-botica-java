package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.Producto;
import com.mycompany.nativo.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    private final Connection conexion;

    public ProductoDAOImpl() {
        try {
            this.conexion = ConexionDB.getConnection();
        } catch (SQLException e) {
            System.err.println("Error al inicializar ProductoDAO: " + e.getMessage());
            throw new RuntimeException("Fallo al inicializar la conexión para ProductoDAO.", e);
        }
    }
    

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getDouble("precio"), 
            rs.getInt("stock"),
            rs.getInt("stock_minimo"), // Debe coincidir con la BD (lo recreaste)
            rs.getDate("fecha_vencimiento"), 
            rs.getInt("proveedor_id")
        );
    }

    @Override
    public void insertar(Producto producto) {
        String sql = "INSERT INTO producto (nombre, precio, stock, stock_minimo, fecha_vencimiento, proveedor_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio()); 
            ps.setInt(3, producto.getStock());
            ps.setInt(4, producto.getStockMinimo()); 
            ps.setDate(5, producto.getFechaVencimiento()); 
            ps.setInt(6, producto.getProveedorId());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al insertar producto: " + e.getMessage());
            throw new RuntimeException(" Fallo al insertar producto.", e);
        }
    }
    
    @Override
    public void actualizar(Producto producto) {
        String sql = "UPDATE producto SET nombre = ?, precio = ?, stock = ?, stock_minimo = ?, fecha_vencimiento = ?, proveedor_id = ? WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio()); 
            ps.setInt(3, producto.getStock());
            ps.setInt(4, producto.getStockMinimo()); 
            ps.setDate(5, producto.getFechaVencimiento()); 
            ps.setInt(6, producto.getProveedorId());
            ps.setInt(7, producto.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            throw new RuntimeException("Fallo al actualizar producto.", e);
        }
    }
    
    @Override
    public Producto obtenerPorId(int id) {
        Producto producto = null;
        String sql = "SELECT id, nombre, precio, stock, stock_minimo, fecha_vencimiento, proveedor_id FROM producto WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto = mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
        }
        return producto;
    }
    
    @Override
    public boolean eliminar(int id) {    
        String sql = "DELETE FROM producto WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate(); 
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            throw new RuntimeException("Fallo al eliminar producto.", e);
        }
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, stock, stock_minimo, fecha_vencimiento, proveedor_id FROM producto";

        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Error al obtener todos los productos: " + e.getMessage());
        }
        return productos;
    }

    
    @Override
    public void actualizarStockTransaccional(Producto producto, java.sql.Connection conn) throws java.sql.SQLException {
        // Usado en la transacción de venta (RF26)
        String sql = "UPDATE producto SET stock = ? WHERE id = ?";

        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, producto.getStock());
            ps.setInt(2, producto.getId());
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new SQLException("Fallo al actualizar stock dentro de la transacción.", e);
        }
    }
    
    @Override
    public List<Producto> buscarPorCriterio(String criterio) {
        List<Producto> productos = new ArrayList<>();

        String sql = "SELECT id, nombre, precio, stock, stock_minimo, fecha_vencimiento, proveedor_id FROM producto WHERE nombre LIKE ? OR CAST(id AS CHAR) LIKE ? ORDER BY nombre";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String patron = "%" + criterio + "%";
            ps.setString(1, patron); 
            ps.setString(2, patron); 
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por nombre/ID: " + e.getMessage());
        }
        return productos;
    }
}
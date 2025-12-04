package com.mycompany.nativo.repositorios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.mycompany.nativo.ConexionDB;
import com.mycompany.nativo.modelos.Proveedor;

public class ProveedorDAOImpl implements ProveedorDAO {

    private final Connection conexion;

    public ProveedorDAOImpl() {
        try {
            this.conexion = ConexionDB.getConnection();
        } catch (SQLException e) {
            System.err.println(" Error al inicializar ProveedorDAO: No se pudo obtener la conexión.");
            throw new RuntimeException("Fallo al inicializar la conexión para ProveedorDAO.", e);
        }
    }
    
    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setRuc(rs.getString("ruc")); 
        p.setTelefono(rs.getString("telefono"));
        p.setDireccion(rs.getString("direccion"));
        return p;
    }

    @Override
    public void insertar(Proveedor proveedor) {
  
        String sql = "INSERT INTO proveedor (nombre, ruc, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getRuc()); 
            ps.setString(3, proveedor.getTelefono());
            ps.setString(4, proveedor.getDireccion());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    proveedor.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al insertar proveedor: " + e.getMessage());
            throw new RuntimeException("Fallo al insertar proveedor.", e);
        }
    }
    
    @Override
    public void actualizar(Proveedor proveedor) {

        String sql = "UPDATE proveedor SET nombre = ?, ruc = ?, telefono = ?, direccion = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getRuc());
            ps.setString(3, proveedor.getTelefono()); 
            ps.setString(4, proveedor.getDireccion());

            ps.setInt(5, proveedor.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(" Error al actualizar proveedor: " + e.getMessage());
            throw new RuntimeException(" Fallo al actualizar proveedor.", e);
        }
    }

    @Override
    public Proveedor obtenerPorId(int id) {
        Proveedor proveedor = null;

        String sql = "SELECT id, nombre, ruc, telefono, direccion FROM proveedor WHERE id = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    proveedor = mapearProveedor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al obtener proveedor por ID: " + e.getMessage());
        }
        return proveedor;
    }

    @Override
    public Proveedor obtenerPorRuc(String ruc) {
        Proveedor proveedor = null;
        String sql = "SELECT id, nombre, ruc, telefono, direccion FROM proveedor WHERE ruc = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, ruc);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    proveedor = mapearProveedor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al obtener proveedor por RUC: " + e.getMessage());
        }
        return proveedor;
    }


    @Override
    public boolean eliminar(int id) {    
        String sql = "DELETE FROM proveedor WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate(); 
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println(" Error al eliminar proveedor: " + e.getMessage());
            throw new RuntimeException(" Fallo al eliminar proveedor.", e);
        }
    }
    @Override
    public List<Proveedor> obtenerTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id, nombre, ruc, telefono, direccion FROM proveedor";
        
        try (Statement st = conexion.createStatement(); 
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
        } catch (SQLException e) {
            System.err.println(" Error al obtener proveedores: " + e.getMessage());
        }
        return proveedores;
    }


    @Override
    public List<Proveedor> buscarPorCriterio(String criterio) {
        List<Proveedor> proveedores = new ArrayList<>();

        String sql = "SELECT id, nombre, ruc, telefono, direccion FROM proveedor WHERE " +
                     "nombre LIKE ? OR ruc LIKE ? OR CAST(id AS CHAR) LIKE ?"; 

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String patron = "%" + criterio + "%";
            ps.setString(1, patron); 
            ps.setString(2, patron); 
            ps.setString(3, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    proveedores.add(mapearProveedor(rs)); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar proveedores por criterio: " + e.getMessage());
        }
        return proveedores;
    }


    @Override
    public Proveedor obtenerPorNombre(String nombre) {
        Proveedor proveedor = null;
        String sql = "SELECT id, nombre, ruc, telefono, direccion FROM proveedor WHERE nombre = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
            
                    proveedor = mapearProveedor(rs); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proveedor por Nombre: " + e.getMessage());
        }
        return proveedor;
    }
}
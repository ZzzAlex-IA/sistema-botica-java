package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.Cliente;
import com.mycompany.nativo.ConexionDB; // Importar la clase de conexión principal
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {
    
    // Método privado para obtener la conexión
    private Connection getConnection() throws SQLException {
        return ConexionDB.getConnection(); // Usar la clase de conexión central
    }
    
    // Método Helper para mapear el ResultSet al objeto Cliente
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setDocumento(rs.getString("documento"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setTelefono(rs.getString("telefono"));
        return cliente;
    }
    
    @Override
    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO cliente (documento, nombre, telefono) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, cliente.getDocumento());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getTelefono());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        cliente.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            throw new RuntimeException("Fallo al insertar cliente.", e);
        }
        return false;
    }
    
    @Override
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET documento = ?, nombre = ?, telefono = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getDocumento());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getTelefono());
            stmt.setInt(4, cliente.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            throw new RuntimeException("Fallo al actualizar cliente.", e);
        }
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM cliente WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            throw new RuntimeException("Fallo al eliminar cliente.", e);
        }
    }
    
    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT id, documento, nombre, telefono FROM cliente WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por ID: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public Cliente buscarPorDocumento(String documento) {
        String sql = "SELECT id, documento, nombre, telefono FROM cliente WHERE documento = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, documento);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por documento: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, documento, nombre, telefono FROM cliente ORDER BY nombre";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
        return clientes;
    }
    @Override
    public List<Cliente> buscarPorCriterio(String criterio) {
        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT id, documento, nombre, telefono FROM cliente WHERE " +
                     "documento LIKE ? OR nombre LIKE ? ORDER BY nombre"; 

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String patron = "%" + criterio + "%";
            ps.setString(1, patron); 
            ps.setString(2, patron); 

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setDocumento(rs.getString("documento"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setTelefono(rs.getString("telefono"));
                    clientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por criterio: " + e.getMessage());
        }
        return clientes;
    }
}
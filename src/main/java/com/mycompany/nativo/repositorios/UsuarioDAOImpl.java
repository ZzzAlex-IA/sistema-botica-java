package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.Usuario;
import com.mycompany.nativo.ConexionDB;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class UsuarioDAOImpl implements UsuarioDAO {

    private final Connection conexion;

    public UsuarioDAOImpl() {
        try {
            this.conexion = ConexionDB.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar la base de datos para UsuarioDAO.", e);
        }
    }
 
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nombre"),    
            rs.getString("dni"),       
            rs.getString("email"),
            rs.getString("password"),
            rs.getInt("cargo_id"),
            rs.getString("estado")   
        );
    }
    
    @Override
    public void insertar(Usuario usuario) {
  
        String sql = "INSERT INTO usuario (nombre, dni, email, password, cargo_id, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getDni()); 
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getPassword()); 
            ps.setInt(5, usuario.getCargoId());
            ps.setString(6, usuario.getEstado()); 
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            throw new RuntimeException("Fallo al insertar usuario.", e);
        }
    }

    @Override
    public Usuario obtenerPorId(int id) {
        Usuario usuario = null;
         String sql = "SELECT id, nombre, dni, email, password, cargo_id, estado FROM usuario WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }
        return usuario;
    }
    
    @Override
    public Usuario obtenerPorEmailYPassword(String email, String password) {
        Usuario usuario = null;
        String sql = "SELECT id, nombre, dni, email, password, cargo_id, estado FROM usuario WHERE email = ? AND password = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
        }
        return usuario;
    }
    
    @Override
    public Usuario obtenerPorDni(String dni) {
        Usuario usuario = null;
        String sql = "SELECT id, nombre, dni, email, password, cargo_id, estado FROM usuario WHERE dni = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por DNI: " + e.getMessage());
        }
        return usuario;
    }
    
    @Override
    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre = ?, dni = ?, email = ?, password = ?, cargo_id = ?, estado = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getDni());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getPassword());  
            ps.setInt(5, usuario.getCargoId());
            ps.setString(6, usuario.getEstado()); 
            ps.setInt(7, usuario.getId());

            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            throw new RuntimeException("Fallo al actualizar usuario.", e);
        }
    }  
    
    @Override
    public boolean eliminar(int id) {    
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate(); 
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            throw new RuntimeException("Fallo al eliminar usuario.", e);
        }
    }
    
    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, dni, email, password, cargo_id, estado FROM usuario";

        try (Statement st = conexion.createStatement(); 
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los usuarios: " + e.getMessage());
        }
        return usuarios;
    }
 
    @Override
    public List<Usuario> buscarPorCriterio(String criterio) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, dni, email, password, cargo_id, estado FROM usuario WHERE " +
                     "nombre LIKE ? OR dni LIKE ? OR CAST(id AS CHAR) LIKE ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            String patron = "%" + criterio + "%";
            ps.setString(1, patron);  
            ps.setString(2, patron);  
            ps.setString(3, patron);    

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));  
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios por criterio: " + e.getMessage());
        }
        return usuarios;
    }


}
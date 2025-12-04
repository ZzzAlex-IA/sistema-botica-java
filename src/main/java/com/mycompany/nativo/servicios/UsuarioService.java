package com.mycompany.nativo.servicios;

import java.util.List;
import com.mycompany.nativo.modelos.Usuario;
import com.mycompany.nativo.repositorios.UsuarioDAO;
import com.mycompany.nativo.repositorios.UsuarioDAOImpl;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }
    
    public Usuario login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return null;    
        }
        return usuarioDAO.obtenerPorEmailYPassword(email, password);
    }
    
    public boolean registrar(Usuario usuario) {
      
        if (usuario.getEmail().trim().isEmpty() || 
            usuario.getPassword().trim().isEmpty() || 
            usuario.getCargoId() <= 0 || 
            usuario.getNombre().trim().isEmpty() ||
            usuario.getDni() == null || 
            usuario.getDni().trim().isEmpty()) 
        {
            System.err.println("Error de Negocio: Faltan datos obligatorios (Email, Nombre, Password, Cargo, o DNI).");
            return false;
        }

        String dni = usuario.getDni().trim();

        if (dni.length() != 8 || !dni.matches("\\d+")) {  
            System.err.println("Error de Negocio: El DNI debe tener exactamente 8 dígitos numéricos.");
            return false;
        }

        if (usuarioDAO.obtenerPorDni(dni) != null) {
            System.err.println("Error de Negocio: Ya existe un usuario con el DNI: " + dni);
            return false;
        }

        try {
            usuarioDAO.insertar(usuario);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Fallo al registrar usuario en capa de servicio: " + e.getMessage());
            return false;
        }

    }
    
    public Usuario buscarUsuarioPorId(int id) {
        return usuarioDAO.obtenerPorId(id);
    }
    
    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario.getId() <= 0) return false;
        try {
            usuarioDAO.actualizar(usuario);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Fallo al actualizar usuario en capa de servicio: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarUsuario(int id) {
        if (id <= 0) return false;
        try {
            return usuarioDAO.eliminar(id);
        } catch (RuntimeException e) {
            System.err.println("Error de Servicio: No se pudo eliminar el usuario ID " + id + ". Podría tener ventas asociadas.");
            return false;
        }
    }
    
    public List<Usuario> obtenerTodos() {
        return usuarioDAO.obtenerTodos();
    }
    
    public List<Usuario> buscarUsuariosPorCriterio(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return usuarioDAO.obtenerTodos();
        }
        return usuarioDAO.buscarPorCriterio(criterio.trim());
    }
}
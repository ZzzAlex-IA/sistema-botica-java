package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.Usuario;
import java.util.List;

public interface UsuarioDAO {
    void insertar(Usuario usuario);
    void actualizar(Usuario usuario);
    boolean eliminar(int id);
    Usuario obtenerPorId(int id);
    Usuario obtenerPorEmailYPassword(String email, String password); 
    Usuario obtenerPorDni(String dni); 
    List<Usuario> obtenerTodos();
    List<Usuario> buscarPorCriterio(String criterio);
    
}
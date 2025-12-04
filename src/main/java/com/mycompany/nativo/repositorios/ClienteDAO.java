package com.mycompany.nativo.repositorios;
import com.mycompany.nativo.modelos.Cliente;
import java.util.List;

public interface ClienteDAO {
    boolean insertar(Cliente cliente);
    boolean actualizar(Cliente cliente);
    boolean eliminar(int id);
    Cliente buscarPorId(int id);
    Cliente buscarPorDocumento(String documento);
    List<Cliente> listarTodos();
    List<Cliente> buscarPorCriterio(String criterio);
}
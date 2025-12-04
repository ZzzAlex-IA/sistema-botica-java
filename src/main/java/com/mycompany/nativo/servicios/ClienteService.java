package com.mycompany.nativo.servicios;

import com.mycompany.nativo.modelos.Cliente;
import com.mycompany.nativo.repositorios.ClienteDAO;
import com.mycompany.nativo.repositorios.ClienteDAOImpl;
import java.util.List;

public class ClienteService {
    
    private final ClienteDAO clienteDAO;
    
    public ClienteService() {
        this.clienteDAO = new ClienteDAOImpl();
    }
    
    public boolean registrarCliente(Cliente cliente) {
        if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
            System.err.println("El documento (DNI/RUC) es obligatorio.");
            return false;
        }
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            System.err.println("El nombre o razón social es obligatorio.");
            return false;
        }
        
        if (cliente.getDocumento().length() == 8 && !cliente.getDocumento().matches("\\d{8}")) {
            System.err.println("DNI inválido. Debe tener 8 dígitos numéricos.");
            return false;
        }
        
        if (clienteDAO.buscarPorDocumento(cliente.getDocumento()) != null) {
            System.err.println("Ya existe un cliente con ese documento.");
            return false;
        }
        
        try {
            return clienteDAO.insertar(cliente);
        } catch (RuntimeException e) {
            System.err.println("Fallo al registrar cliente en capa de servicio: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarCliente(Cliente cliente) {
        if (cliente.getId() <= 0) return false;
        try {
            return clienteDAO.actualizar(cliente);
        } catch (RuntimeException e) {
            System.err.println("Fallo al actualizar cliente: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarCliente(int id) {
        return clienteDAO.eliminar(id);
    }
    
    public Cliente buscarPorId(int id) {
        return clienteDAO.buscarPorId(id);
    }
    
    public Cliente buscarPorDocumento(String documento) {
        return clienteDAO.buscarPorDocumento(documento);
    }
    
    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }
    
     public List<Cliente> buscarClientesPorCriterio(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return clienteDAO.listarTodos();
        }
        return clienteDAO.buscarPorCriterio(criterio.trim());
    }
}
package com.mycompany.nativo.servicios;

import com.mycompany.nativo.repositorios.CargoDAO;
import com.mycompany.nativo.repositorios.CargoDAOImpl;
import com.mycompany.nativo.modelos.Cargo;
import java.util.List;

public class CargoService {

    private final CargoDAO cargoDAO;

    public CargoService() {
        this.cargoDAO = new CargoDAOImpl();
    }

    public boolean registrarNuevoCargo(Cargo cargo) {
        if (cargo.getRol() == null || cargo.getRol().trim().isEmpty()) {
            System.err.println("Error de Negocio: El nombre del rol no puede estar vacío.");
            return false;
        }
        try {
            cargoDAO.insertar(cargo);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Fallo al registrar cargo en capa de servicio: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarCargo(Cargo cargo) {
        if (cargo == null || cargo.getRol() == null || cargo.getRol().trim().isEmpty()) {
            System.err.println("Error de Negocio: Los datos del cargo a modificar son inválidos.");
            return false;
        }
        try {
            cargoDAO.actualizar(cargo);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Error en el servicio al actualizar cargo: " + e.getMessage());
            return false;
        }
    }

    public Cargo buscarPorId(int id) {
        return cargoDAO.obtenerPorId(id);
    }
    
    public List<Cargo> listarTodosLosCargos() {
        return cargoDAO.obtenerTodos();
    }
    
    public boolean eliminarCargo(int id) {
        try {
            cargoDAO.eliminar(id);
            return true;
        } catch (RuntimeException e) {
            System.err.println("Error de Servicio: No se pudo eliminar el cargo ID " + id + ". Posiblemente tiene usuarios asociados.");
            return false;
        }
    }
}
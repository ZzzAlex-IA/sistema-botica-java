package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.Cargo;
import java.util.List;

public interface CargoDAO {
    void insertar(Cargo cargo);
    void actualizar(Cargo cargo);
    void eliminar(int id);
    Cargo obtenerPorId(int id);
    List<Cargo> obtenerTodos();
}
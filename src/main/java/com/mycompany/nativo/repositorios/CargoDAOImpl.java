package com.mycompany.nativo.repositorios;

import com.mycompany.nativo.modelos.Cargo;
import com.mycompany.nativo.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargoDAOImpl implements CargoDAO {

    private final Connection conexion;

    public CargoDAOImpl() {
        try {
            this.conexion = ConexionDB.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar la base de datos para CargoDAO.", e);
        }
    }

    @Override
    public void insertar(Cargo cargo) {
        String sql = "INSERT INTO cargo (rol) VALUES (?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, cargo.getRol());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cargo.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar cargo: " + e.getMessage());
            throw new RuntimeException("Fallo al insertar cargo.", e);
        }
    }
    
    @Override
    public Cargo obtenerPorId(int id) {
        Cargo cargo = null;
        String sql = "SELECT id, rol FROM cargo WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cargo = new Cargo(rs.getInt("id"), rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cargo por ID: " + e.getMessage());
        }
        return cargo;
    }

    
    @Override
    public void actualizar(Cargo cargo) {
        String sql = "UPDATE cargo SET rol = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cargo.getRol());
            ps.setInt(2, cargo.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar cargo: " + e.getMessage());
            throw new RuntimeException("Fallo al actualizar cargo.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM cargo WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar cargo: " + e.getMessage());
            throw new RuntimeException("Fallo al eliminar cargo.", e);
        }
    }

    @Override
    public List<Cargo> obtenerTodos() {
        List<Cargo> cargos = new ArrayList<>();
        String sql = "SELECT id, rol FROM cargo";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                cargos.add(new Cargo(rs.getInt("id"), rs.getString("rol")));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los cargos: " + e.getMessage());
        }
        return cargos;
    }
}
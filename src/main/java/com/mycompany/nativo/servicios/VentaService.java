package com.mycompany.nativo.servicios;

import com.mycompany.nativo.ConexionDB;
import com.mycompany.nativo.modelos.Venta;
import com.mycompany.nativo.modelos.DetalleVenta;
import com.mycompany.nativo.modelos.Producto;
import com.mycompany.nativo.repositorios.VentaDAO;
import com.mycompany.nativo.repositorios.ProductoDAO; 
import com.mycompany.nativo.repositorios.DetalleVentaDAO;
import com.mycompany.nativo.repositorios.ProductoDAOImpl;  

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class VentaService {
    
    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;  
    private final DetalleVentaDAO detalleVentaDAO; 
 
    public VentaService(VentaDAO ventaDAO, ProductoDAO productoDAO, DetalleVentaDAO detalleVentaDAO) {
        this.ventaDAO = ventaDAO;
        this.productoDAO = productoDAO; 
        this.detalleVentaDAO = detalleVentaDAO; 
    }
    
    public boolean procesarVenta(Venta venta, List<DetalleVenta> detalles) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);  

            ventaDAO.insertarVenta(venta, conn);

            for (DetalleVenta detalle : detalles) {
                detalle.setVentaId(venta.getId());

                Producto producto = productoDAO.obtenerPorId(detalle.getProductoId());
                if (producto == null) {
                    throw new RuntimeException("Producto ID " + detalle.getProductoId() + " no encontrado.");
                }
                if (producto.getStock() < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
                }

                double subtotal = producto.getPrecio() * detalle.getCantidad();
                detalle.setSubtotal(subtotal);

                detalleVentaDAO.insertarDetalle(detalle, conn);

                producto.setStock(producto.getStock() - detalle.getCantidad());
                productoDAO.actualizarStockTransaccional(producto, conn);
            }

            conn.commit();
            return true;

        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    public List<Venta> obtenerTodos() {
        return ventaDAO.obtenerTodos();
    }
    

    public List<Venta> obtenerVentasPorUsuario(int usuarioId) {
        return ventaDAO.obtenerVentasPorUsuario(usuarioId);
    }
    public List<Map<String, Object>> obtenerTopProductos() {
        return ventaDAO.obtenerTopProductosVendidos();
    }
}       
package com.mycompany.nativo.modelos;

import java.sql.Date;

public class Producto {

    private int id;
    private String nombre;
    private double precio;
    private int stock;
    private int stockMinimo; 
    private Date fechaVencimiento;
    private int proveedorId;

    public Producto() {}

    public Producto(int id, String nombre, double precio, int stock, int stockMinimo, Date fechaVencimiento, int proveedorId) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.fechaVencimiento = fechaVencimiento;
        this.proveedorId = proveedorId;
    }

    public Producto(String nombre, double precio, int stock, int stockMinimo, Date fechaVencimiento, int proveedorId) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.fechaVencimiento = fechaVencimiento;
        this.proveedorId = proveedorId;
    }

    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    
    public double getPrecio() { 
        return precio; 
    }
    public void setPrecio(double precio) { 
        this.precio = precio; 
    }
    
    public int getStock() { 
        return stock; 
    }
    public void setStock(int stock) { 
        this.stock = stock; 
    }
    
    public int getStockMinimo() { 
        return stockMinimo; 
    }
    public void setStockMinimo(int stockMinimo) { 
        this.stockMinimo = stockMinimo; 
    }
    
    public Date getFechaVencimiento() { 
        return fechaVencimiento; 
    }
    public void setFechaVencimiento(Date fechaVencimiento) { 
        this.fechaVencimiento = fechaVencimiento; 
    }
    
    public int getProveedorId() { 
        return proveedorId; 
    }
    public void setProveedorId(int proveedorId) { 
        this.proveedorId = proveedorId; 
    }
}
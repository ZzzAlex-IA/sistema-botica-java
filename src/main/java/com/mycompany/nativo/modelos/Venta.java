package com.mycompany.nativo.modelos;

import java.sql.Date;

public class Venta {
    
    private int id;
    private java.sql.Date fecha;
    private int usuarioId;    
    private String clienteDocumento; 
    private String metodoPago;     
    private double montoTotal; 

    public Venta() {}

    public Venta(Date fecha, int usuarioId, String clienteDocumento, String metodoPago, double montoTotal) {
        this.fecha = fecha;
        this.usuarioId = usuarioId;
        this.clienteDocumento = clienteDocumento;
        this.metodoPago = metodoPago;
        this.montoTotal = montoTotal;
    }
    
    public Venta(int id, Date fecha, int usuarioId, String clienteDocumento, String metodoPago, double montoTotal) {
        this(fecha, usuarioId, clienteDocumento, metodoPago, montoTotal);
        this.id = id;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getClienteDocumento() { return clienteDocumento; }
    public void setClienteDocumento(String clienteDocumento) { this.clienteDocumento = clienteDocumento; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }
}
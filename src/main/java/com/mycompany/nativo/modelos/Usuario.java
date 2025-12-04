package com.mycompany.nativo.modelos;

public class Usuario {
    
    private int id;
    private String nombre; 
    private String dni; 
    private String email;
    private String password;    
    private int cargoId;       
    private String estado; 

    public Usuario() {}
    
    public Usuario(String nombre, String dni, String email, String password, int cargoId, String estado) {
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.cargoId = cargoId;
        this.estado = estado;
    }
    
    public Usuario(int id, String nombre, String dni, String email, String password, int cargoId, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.cargoId = cargoId;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getCargoId() { return cargoId; }
    public void setCargoId(int cargoId) { this.cargoId = cargoId; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
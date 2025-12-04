
package Views;

import com.mycompany.nativo.modelos.Cargo;
import com.mycompany.nativo.modelos.Usuario;
import com.mycompany.nativo.servicios.CargoService;
import com.mycompany.nativo.servicios.UsuarioService;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.mycompany.nativo.modelos.Proveedor;     
import com.mycompany.nativo.servicios.ProveedorService;    
import com.mycompany.nativo.modelos.Producto;  
import com.mycompany.nativo.servicios.ProductoService;
import java.sql.Date; 
import java.util.List;  

public class AdminPestañas extends javax.swing.JPanel {
 

    private final UsuarioService usuarioService = new UsuarioService();
    private final CargoService cargoService = new CargoService();
    private final ProveedorService proveedorService = new ProveedorService(); 
    private final ProductoService productoService = new ProductoService(); 
    private boolean modoEdicion = false;
    private boolean modoEdicionProveedor = false; 
    private boolean modoEdicionProducto = false; 
    private int idUsuarioSeleccionado = -1; 
    private int idProveedorSeleccionado = -1;
    private int idProductoSeleccionado = -1;
 
    public AdminPestañas() {
        initComponents();
        cargarCargosEnComboBox();
        cargarTablaUsuarios();
        cargarTablaProveedores();
        cargarProveedoresEnComboBox(); 
        
        cmbEstado.setModel(new DefaultComboBoxModel<>(new String[]{"ACTIVO", "INACTIVO"}));
      
        cargarProveedoresEnComboBox(); 
        cargarTablaProductos(null);    
    }
    private void cargarProveedoresEnComboBox() {  
        try {
            List<Proveedor> proveedores = proveedorService.listarTodosLosProveedores();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

            for (Proveedor p : proveedores) {
                model.addElement(p.getNombre()); 
            }

            cmbProveedorProducto.setModel(model); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores para Productos.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

     
    private void cargarTablaProductos(List<Producto> listaFiltrada) {   
        if (listaFiltrada == null) {
            listaFiltrada = productoService.obtenerTodos();
        }

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Precio");
        model.addColumn("Stock");
        model.addColumn("Vencimiento");
        model.addColumn("Proveedor"); 

        try {
            for (Producto p : listaFiltrada) {
                Proveedor proveedor = proveedorService.buscarProveedorPorId(p.getProveedorId());
                String nombreProveedor = (proveedor != null) ? proveedor.getNombre() : "Desconocido";

                model.addRow(new Object[]{
                    p.getId(), 
                    p.getNombre(), 
                    p.getPrecio(), 
                    p.getStock(), 
                    p.getFechaVencimiento(), 
                    nombreProveedor
                });
            }
            tblProductos.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al listar productos: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

 
    private void limpiarCamposProducto() {                  
        txtNombreProducto.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        // Asumiendo JTextField para fecha
        txtFechaVencimiento.setText(""); 

        if (cmbProveedorProducto.getItemCount() > 0) cmbProveedorProducto.setSelectedIndex(0);

        modoEdicionProducto = false;
        idProductoSeleccionado = -1;
        btnGuardarProducto.setText("GUARDAR"); 
    }
    
    private void cargarTablaProveedores() {
    DefaultTableModel model = new DefaultTableModel();
    
    model.addColumn("ID");
    model.addColumn("Nombre");
    model.addColumn("RUC");
    model.addColumn("Teléfono");
    model.addColumn("Dirección");
    
    try {
        List<Proveedor> proveedores = proveedorService.listarTodosLosProveedores();
        
        for (Proveedor p : proveedores) {
            model.addRow(new Object[]{
                p.getId(), 
                p.getNombre(), 
                p.getRuc(), 
                p.getTelefono(), 
                p.getDireccion()
            });
        }
        tblProveedores.setModel(model);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al listar proveedores: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
    }
}

    private void limpiarCamposProveedor() {

        txtPorvedor.setText("");
        txtRuc.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");

        modoEdicionProveedor = false;
        idProveedorSeleccionado = -1;
        btnGuardarProveedor.setText("GUARDAR"); 
    }

    private void cargarCargosEnComboBox() {
        try {
            List<Cargo> cargos = cargoService.listarTodosLosCargos();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            
            for (Cargo cargo : cargos) {
                model.addElement(cargo.getRol());
            }
            cmbCargo.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los cargos: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }
    
 
    private void cargarTablaUsuarios() {
        DefaultTableModel model = new DefaultTableModel();
        
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("DNI");
        model.addColumn("Email");
        model.addColumn("Cargo");
        model.addColumn("Estado");
        
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodos();
            
            for (Usuario u : usuarios) {
                // Para obtener el nombre del cargo, buscamos el Cargo por ID
                Cargo cargo = cargoService.buscarPorId(u.getCargoId());
                String nombreCargo = (cargo != null) ? cargo.getRol() : "Desconocido";
                
                model.addRow(new Object[]{
                    u.getId(), 
                    u.getNombre(), 
                    u.getDni(), 
                    u.getEmail(), 
                    nombreCargo, 
                    u.getEstado()
                });
            }
            tblUsuarios.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al listar usuarios: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void limpiarCampos() {
        txtNombre.setText("");
        txtDni.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        cmbCargo.setSelectedIndex(0); 
        cmbEstado.setSelectedIndex(0); 
        modoEdicion = false;
        idUsuarioSeleccionado = -1;
        btnGuardar.setText("GUARDAR");
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtPorvedor = new javax.swing.JTextField();
        txtRuc = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        btnGuardarProveedor = new javax.swing.JButton();
        btnLimpiarProveedor = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtBuscarProveedor = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProveedores = new javax.swing.JTable();
        btnBuscarProveedor = new javax.swing.JButton();
        btnEditarProveedor = new javax.swing.JButton();
        btnEliminarProveedor = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtDni = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        cmbCargo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cmbEstado = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        btnEditar = new javax.swing.JButton();
        btnElimar = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtNombreProducto = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        txtStock = new javax.swing.JTextField();
        txtFechaVencimiento = new javax.swing.JTextField();
        cmbProveedorProducto = new javax.swing.JComboBox<>();
        btnGuardarProducto = new javax.swing.JButton();
        btnLimpiarProducto = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        btnStockBajo = new javax.swing.JButton();
        btnProxVencer = new javax.swing.JButton();
        btnEditarProducto = new javax.swing.JButton();
        btnEliminarProducto = new javax.swing.JButton();
        btnBuscarProducto = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(197, 223, 223));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registrar Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("Nombre Completo :");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("Ruc :");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("Tlelefono :");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("Direccion :");

        btnGuardarProveedor.setBackground(new java.awt.Color(255, 135, 66));
        btnGuardarProveedor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardarProveedor.setText("Guardar");
        btnGuardarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProveedorActionPerformed(evt);
            }
        });

        btnLimpiarProveedor.setBackground(new java.awt.Color(26, 32, 40));
        btnLimpiarProveedor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiarProveedor.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiarProveedor.setText("Limpiar");
        btnLimpiarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarProveedorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(txtRuc, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPorvedor)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDireccion))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnGuardarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                        .addComponent(btnLimpiarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(53, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPorvedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtRuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel11)
                .addGap(14, 14, 14)
                .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        jPanel6.setBackground(new java.awt.Color(153, 153, 153));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "lista de Provedores", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Buscar :");

        tblProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "RUC", "Teléfono", "Dirección"
            }
        ));
        jScrollPane2.setViewportView(tblProveedores);

        btnBuscarProveedor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscarProveedor.setText("Buscar");
        btnBuscarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProveedorActionPerformed(evt);
            }
        });

        btnEditarProveedor.setBackground(new java.awt.Color(26, 32, 40));
        btnEditarProveedor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEditarProveedor.setForeground(new java.awt.Color(255, 255, 255));
        btnEditarProveedor.setText("Editar");
        btnEditarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarProveedorActionPerformed(evt);
            }
        });

        btnEliminarProveedor.setBackground(new java.awt.Color(26, 32, 40));
        btnEliminarProveedor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminarProveedor.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarProveedor.setText("Eliminar");
        btnEliminarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProveedorActionPerformed(evt);
            }
        });

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-buscar-30.png"))); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel14)
                        .addGap(46, 46, 46)
                        .addComponent(txtBuscarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20)
                        .addGap(23, 23, 23)
                        .addComponent(btnBuscarProveedor))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(btnEditarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(btnEliminarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBuscarProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(txtBuscarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEditarProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(btnEliminarProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Registrar Provedores", jPanel1);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registrar Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Nombre Completo :");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("DNI :");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Email :");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Contraseña");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Cargo :");

        cmbCargo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText("Estado");

        cmbEstado.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACTIVO", "INACTIVO" }));

        btnGuardar.setBackground(new java.awt.Color(26, 32, 40));
        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnLimpiar.setBackground(new java.awt.Color(255, 135, 66));
        btnLimpiar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel1)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtNombre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel7))
                            .addGap(27, 27, 27)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cmbCargo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmbEstado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cmbCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registrar Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Buscar :");

        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        tblUsuarios.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "DNI", "Cargo", "Estado"
            }
        ));
        jScrollPane1.setViewportView(tblUsuarios);

        btnEditar.setBackground(new java.awt.Color(26, 32, 40));
        btnEditar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnEditar.setForeground(new java.awt.Color(255, 255, 255));
        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnElimar.setBackground(new java.awt.Color(255, 135, 66));
        btnElimar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnElimar.setText("Eliminar");
        btnElimar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnElimarActionPerformed(evt);
            }
        });

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-buscar-30.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel6)
                .addGap(68, 68, 68)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(btnElimar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addComponent(jLabel19)))
                .addGap(52, 52, 52)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnElimar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(74, 74, 74))
        );

        jTabbedPane2.addTab("Registrar Usuarios", jPanel5);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registrar Productos ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setText("Nombre de Productos :");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setText("Costo :");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Stock :");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setText("Fecha de Vencimiento");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setText("Provedor");

        btnGuardarProducto.setBackground(new java.awt.Color(26, 32, 40));
        btnGuardarProducto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarProducto.setText("Guardar");
        btnGuardarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProductoActionPerformed(evt);
            }
        });

        btnLimpiarProducto.setBackground(new java.awt.Color(255, 135, 66));
        btnLimpiarProducto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLimpiarProducto.setText("Limpiar");
        btnLimpiarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel17)
                                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                                .addComponent(txtNombreProducto)
                                .addGroup(jPanel8Layout.createSequentialGroup()
                                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel15)
                                        .addComponent(jLabel13))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtPrecio)
                                        .addComponent(txtStock)))
                                .addComponent(txtFechaVencimiento))
                            .addComponent(cmbProveedorProducto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGuardarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLimpiarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(57, 57, 57))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFechaVencimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbProveedorProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLimpiarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(btnGuardarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lista de Productos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setText("Buscar :");

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Precio", "Stock", "Vencimiento", "Provedor"
            }
        ));
        jScrollPane3.setViewportView(tblProductos);

        btnStockBajo.setBackground(new java.awt.Color(26, 32, 40));
        btnStockBajo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnStockBajo.setForeground(new java.awt.Color(255, 255, 255));
        btnStockBajo.setText("Estock Bajo");
        btnStockBajo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStockBajoActionPerformed(evt);
            }
        });

        btnProxVencer.setBackground(new java.awt.Color(26, 32, 40));
        btnProxVencer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnProxVencer.setForeground(new java.awt.Color(255, 255, 255));
        btnProxVencer.setText("Proximo en vencer");
        btnProxVencer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProxVencerActionPerformed(evt);
            }
        });

        btnEditarProducto.setBackground(new java.awt.Color(26, 32, 40));
        btnEditarProducto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEditarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnEditarProducto.setText("Editar");
        btnEditarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarProductoActionPerformed(evt);
            }
        });

        btnEliminarProducto.setBackground(new java.awt.Color(255, 135, 66));
        btnEliminarProducto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminarProducto.setText("Eliminar");
        btnEliminarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProductoActionPerformed(evt);
            }
        });

        btnBuscarProducto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscarProducto.setText("Buscar");
        btnBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductoActionPerformed(evt);
            }
        });

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-buscar-30.png"))); // NOI18N

        jLabel22.setText("Informacion Adicional :");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(btnEditarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 571, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(44, 44, 44)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnStockBajo, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProxVencer))
                .addGap(56, 56, 56))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18))
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBuscarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(33, 33, 33)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnProxVencer, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addGap(12, 12, 12)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStockBajo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Registrar Porductos", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        String nombre = txtNombre.getText();
        String dni = txtDni.getText();
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());
        String estado = cmbEstado.getSelectedItem().toString();
        
        String nombreCargoSeleccionado = cmbCargo.getSelectedItem().toString();
        
        int cargoId = cmbCargo.getSelectedIndex() + 1; 
        
        if (nombre.isEmpty() || dni.isEmpty() || email.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Debe completar Nombre, DNI y Email.", "Advertencia", JOptionPane.WARNING_MESSAGE);
             return;
        }
        if (modoEdicion) {
            Usuario usuarioAEditar = usuarioService.buscarUsuarioPorId(idUsuarioSeleccionado);
            if (usuarioAEditar == null) {
                JOptionPane.showMessageDialog(this, "Error: Usuario a editar no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                limpiarCampos();
                return;
            }
            
            usuarioAEditar.setNombre(nombre);
            usuarioAEditar.setDni(dni);
            usuarioAEditar.setEmail(email);
            if (!password.isEmpty()) usuarioAEditar.setPassword(password);
            usuarioAEditar.setCargoId(cargoId);
            usuarioAEditar.setEstado(estado);
            
            boolean exito = usuarioService.actualizarUsuario(usuarioAEditar);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Usuario actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Error al actualizar. Revise DNI duplicado u otros datos.", "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        } 

        else {
            if (password.isEmpty()) { 
                 JOptionPane.showMessageDialog(this, "La contraseña es obligatoria para un nuevo usuario.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            
            Usuario nuevoUsuario = new Usuario(nombre, dni, email, password, cargoId, estado);
            boolean exito = usuarioService.registrar(nuevoUsuario);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Usuario registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar. DNI/Email duplicado o formato incorrecto (RF11).", "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        limpiarCampos();
        cargarTablaUsuarios();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
       limpiarCampos();   
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
             
        int filaSeleccionada = tblUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fila para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        idUsuarioSeleccionado = (int) tblUsuarios.getValueAt(filaSeleccionada, 0);
        
        Usuario usuarioAEditar = usuarioService.buscarUsuarioPorId(idUsuarioSeleccionado);
        
        if (usuarioAEditar != null) {

            txtNombre.setText(usuarioAEditar.getNombre());
            txtDni.setText(usuarioAEditar.getDni());
            txtEmail.setText(usuarioAEditar.getEmail());
            txtPassword.setText(""); 

            cmbEstado.setSelectedItem(usuarioAEditar.getEstado());

            Cargo cargo = cargoService.buscarPorId(usuarioAEditar.getCargoId());
            if (cargo != null) {
                cmbCargo.setSelectedItem(cargo.getRol());
            }
            modoEdicion = true;
            btnGuardar.setText("ACTUALIZAR");
            
        } else {
             JOptionPane.showMessageDialog(this, "Error: No se encontró el usuario en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnElimarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnElimarActionPerformed

        int filaSeleccionada = tblUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fila para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tblUsuarios.getValueAt(filaSeleccionada, 0);
        Object[] botones = {"Sí", "No"};
        int confirmacion = JOptionPane.showOptionDialog(
            this,
            "¿Está seguro de eliminar el usuario ID: " + id + "?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            botones, 
            botones[1] 
        );
        if (confirmacion == 0) {
            boolean exito = usuarioService.eliminarUsuario(id);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTablaUsuarios(); 
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar. Puede tener ventas asociadas.", "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        } 
    }//GEN-LAST:event_btnElimarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
     String criterio = txtBuscar.getText().trim();

        if (criterio.isEmpty()) {
            cargarTablaUsuarios();
            return;
        }

        try {

            List<Usuario> usuariosEncontrados = usuarioService.buscarUsuariosPorCriterio(criterio);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Nombre");
            model.addColumn("DNI");
            model.addColumn("Email");
            model.addColumn("Cargo");
            model.addColumn("Estado");

            for (Usuario u : usuariosEncontrados) {
                Cargo cargo = cargoService.buscarPorId(u.getCargoId());
                String nombreCargo = (cargo != null) ? cargo.getRol() : "Desconocido";

                model.addRow(new Object[]{
                    u.getId(), 
                    u.getNombre(), 
                    u.getDni(), 
                    u.getEmail(), 
                    nombreCargo, 
                    u.getEstado()
                });
            }
            tblUsuarios.setModel(model);
            if (usuariosEncontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron resultados para el criterio: " + criterio, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la búsqueda: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnGuardarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProveedorActionPerformed
        String nombre = txtPorvedor.getText().trim();
        String ruc = txtRuc.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty() || ruc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El Nombre/Razón Social y el RUC son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Proveedor proveedor;
        boolean exito;

        if (modoEdicionProveedor) {

            proveedor = proveedorService.buscarProveedorPorId(idProveedorSeleccionado);
            if (proveedor == null) {
                JOptionPane.showMessageDialog(this, "Error: Proveedor a editar no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                limpiarCamposProveedor();
                return;
            }
    
            proveedor.setNombre(nombre);
            proveedor.setRuc(ruc);
            proveedor.setTelefono(telefono);
            proveedor.setDireccion(direccion);

            exito = proveedorService.modificarProveedor(proveedor);

        } else {

            proveedor = new Proveedor(nombre, ruc, telefono, direccion);
            exito = proveedorService.registrarNuevoProveedor(proveedor); 
        }

 
        if (exito) {
            JOptionPane.showMessageDialog(this, "Proveedor " + (modoEdicionProveedor ? "actualizado" : "registrado") + " con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            cargarTablaProveedores(); 
            cargarProveedoresEnComboBox(); 

        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar. Revise RUC duplicado o formato (debe ser 11 dígitos).", "Error de Lógica", JOptionPane.ERROR_MESSAGE);
        }

        limpiarCamposProveedor();
    }//GEN-LAST:event_btnGuardarProveedorActionPerformed

    private void btnLimpiarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarProveedorActionPerformed
       
        txtPorvedor.setText("");
        txtRuc.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");

        modoEdicionProveedor = false;
        idProveedorSeleccionado = -1;
        btnGuardarProveedor.setText("GUARDAR");
    }//GEN-LAST:event_btnLimpiarProveedorActionPerformed

    private void btnEditarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarProveedorActionPerformed
       int filaSeleccionada = tblProveedores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fila para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }


        idProveedorSeleccionado = (int) tblProveedores.getValueAt(filaSeleccionada, 0);


        Proveedor proveedorAEditar = proveedorService.buscarProveedorPorId(idProveedorSeleccionado);

        if (proveedorAEditar != null) {

            txtPorvedor.setText(proveedorAEditar.getNombre());
            txtRuc.setText(proveedorAEditar.getRuc());
            txtTelefono.setText(proveedorAEditar.getTelefono());
            txtDireccion.setText(proveedorAEditar.getDireccion());

            modoEdicionProveedor = true;
            btnGuardarProveedor.setText("ACTUALIZAR");

        } else {
             JOptionPane.showMessageDialog(this, "Error: No se encontró el proveedor en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditarProveedorActionPerformed

    private void btnBuscarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProveedorActionPerformed
         String criterio = txtBuscarProveedor.getText().trim();
        if (criterio.isEmpty()) {
            cargarTablaProveedores();
            return;
        }

        try {
            List<Proveedor> proveedoresEncontrados = proveedorService.buscarProveedoresPorCriterio(criterio);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Nombre");
            model.addColumn("RUC");
            model.addColumn("Teléfono");
            model.addColumn("Dirección");

            for (Proveedor p : proveedoresEncontrados) {
                model.addRow(new Object[]{
                    p.getId(), 
                    p.getNombre(), 
                    p.getRuc(), 
                    p.getTelefono(), 
                    p.getDireccion()
                });
            }

            tblProveedores.setModel(model);

            if (proveedoresEncontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron proveedores para el criterio: " + criterio, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la búsqueda de proveedores: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnBuscarProveedorActionPerformed

    private void btnEliminarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProveedorActionPerformed
        int filaSeleccionada = tblProveedores.getSelectedRow();
        if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Debe seleccionar una fila para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
        }

        int id = (int) tblProveedores.getValueAt(filaSeleccionada, 0);

 
         Object[] botones = {"Sí", "No"};

        int confirmacion = JOptionPane.showOptionDialog(
            this,
            "¿Está seguro de eliminar el proveedor ID: " + id + "?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            botones,  
            botones[1]  
        );
 
        if (confirmacion == 0) {
            boolean exito = proveedorService.eliminarProveedor(id);
        
            if (exito) {
            JOptionPane.showMessageDialog(this, " Proveedor eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaProveedores(); 
            } else {
            JOptionPane.showMessageDialog(this, " Error al eliminar. Debe eliminar primero los productos asociados a este proveedor.", "Error de BD", JOptionPane.ERROR_MESSAGE);
            } 
        }
    }//GEN-LAST:event_btnEliminarProveedorActionPerformed
    
    private void btnLimpiarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarProductoActionPerformed
        limpiarCamposProducto();
    }//GEN-LAST:event_btnLimpiarProductoActionPerformed

    private void btnGuardarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProductoActionPerformed
        try {
            String nombre = txtNombreProducto.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String fechaTexto = txtFechaVencimiento.getText().trim();

            int stockMinimoFijo = 10; 

            java.sql.Date fechaVencimiento = null;
            if (!fechaTexto.isEmpty()) {
                fechaVencimiento = java.sql.Date.valueOf(fechaTexto); 
            }

            String nombreProveedor = cmbProveedorProducto.getSelectedItem().toString();
            Proveedor proveedorSeleccionado = proveedorService.obtenerPorNombre(nombreProveedor); 
            int proveedorId = (proveedorSeleccionado != null) ? proveedorSeleccionado.getId() : -1;

            if (nombre.isEmpty() || proveedorId == -1 || precio <= 0 || stock < 0 || fechaVencimiento == null) {
                JOptionPane.showMessageDialog(this, "Debe completar todos los campos válidos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Producto producto;
            boolean exito;
            
            if (modoEdicionProducto) {
                producto = productoService.buscarProductoPorId(idProductoSeleccionado);
                if (producto == null) throw new RuntimeException("Producto no encontrado para editar.");

                producto.setNombre(nombre);
                producto.setPrecio(precio);
                producto.setStock(stock);
                producto.setStockMinimo(stockMinimoFijo); 
                producto.setFechaVencimiento(fechaVencimiento);
                producto.setProveedorId(proveedorId);
                exito = productoService.actualizarProducto(producto);
            } else {

                producto = new Producto(nombre, precio, stock, stockMinimoFijo, fechaVencimiento, proveedorId);
                exito = productoService.registrarProducto(producto);
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, "Producto " + (modoEdicionProducto ? "actualizado" : "registrado") + " con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCamposProducto();
                cargarTablaProductos(null);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar. No se pudo guardar el producto.", "Error de BD/Lógica", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Precio y Stock deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: La fecha debe ser válida y en formato YYYY-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnGuardarProductoActionPerformed

    private void btnEditarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarProductoActionPerformed
      int filaSeleccionada = tblProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fila para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        idProductoSeleccionado = (int) tblProductos.getValueAt(filaSeleccionada, 0);
        Producto productoAEditar = productoService.buscarProductoPorId(idProductoSeleccionado);

        if (productoAEditar != null) {
            txtNombreProducto.setText(productoAEditar.getNombre());
            txtPrecio.setText(String.valueOf(productoAEditar.getPrecio()));
            txtStock.setText(String.valueOf(productoAEditar.getStock()));

            // Cargar Fecha de Vencimiento
            if (productoAEditar.getFechaVencimiento() != null) {
                txtFechaVencimiento.setText(productoAEditar.getFechaVencimiento().toString());
            } else {
                txtFechaVencimiento.setText("");
            }

            // Cargar Proveedor al ComboBox
            Proveedor proveedor = proveedorService.buscarProveedorPorId(productoAEditar.getProveedorId());
            if (proveedor != null) {
                cmbProveedorProducto.setSelectedItem(proveedor.getNombre());
            }

            // Cambiar el modo a EDICIÓN
            modoEdicionProducto = true;
            btnGuardarProducto.setText("ACTUALIZAR");

        } else {
             JOptionPane.showMessageDialog(this, "Error: No se encontró el producto en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditarProductoActionPerformed

    private void btnEliminarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProductoActionPerformed

        int filaSeleccionada = tblProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fila para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tblProductos.getValueAt(filaSeleccionada, 0);
        Object[] botones = {"Sí", "No"};
        int confirmacion = JOptionPane.showOptionDialog(
            this,
            "¿Está seguro de eliminar el producto ID: " + id + "?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            botones,
            botones[1] 
        );
        if (confirmacion == 0) {
            boolean exito = productoService.eliminarProducto(id);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Producto eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTablaProductos(null); 
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar. El producto podría estar asociado a ventas anteriores.", "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEliminarProductoActionPerformed

    private void btnBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductoActionPerformed
        
        String criterio = jTextField5.getText().trim(); 
        if (criterio.isEmpty()) {
            cargarTablaProductos(null); 
            return;
        }

        try {
            List<Producto> productosEncontrados = productoService.buscarProductosPorCriterio(criterio);
            cargarTablaProductos(productosEncontrados); 

            if (productosEncontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron productos para el criterio: " + criterio, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la búsqueda de productos: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnBuscarProductoActionPerformed

    private void btnStockBajoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStockBajoActionPerformed
        
        List<Producto> stockBajo = productoService.obtenerStockBajo();
        cargarTablaProductos(stockBajo);
        if (stockBajo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos con Stock Bajo (<= 10).", "Alerta de Inventario", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Se han filtrado " + stockBajo.size() + " productos con Stock Bajo.", "Alerta de Inventario", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnStockBajoActionPerformed

    private void btnProxVencerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProxVencerActionPerformed

        List<Producto> proximos = productoService.obtenerProximosAVencer();
        cargarTablaProductos(proximos);
        if (proximos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos próximos a vencer en los próximos 60 días.", "Alerta de Caducidad", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Se han filtrado " + proximos.size() + " productos próximos a vencer.", "Alerta de Caducidad", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnProxVencerActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscarProducto;
    private javax.swing.JButton btnBuscarProveedor;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEditarProducto;
    private javax.swing.JButton btnEditarProveedor;
    private javax.swing.JButton btnElimar;
    private javax.swing.JButton btnEliminarProducto;
    private javax.swing.JButton btnEliminarProveedor;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnGuardarProducto;
    private javax.swing.JButton btnGuardarProveedor;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnLimpiarProducto;
    private javax.swing.JButton btnLimpiarProveedor;
    private javax.swing.JButton btnProxVencer;
    private javax.swing.JButton btnStockBajo;
    private javax.swing.JComboBox<String> cmbCargo;
    private javax.swing.JComboBox<String> cmbEstado;
    private javax.swing.JComboBox<String> cmbProveedorProducto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTable tblProveedores;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtBuscarProveedor;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFechaVencimiento;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNombreProducto;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPorvedor;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtRuc;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}

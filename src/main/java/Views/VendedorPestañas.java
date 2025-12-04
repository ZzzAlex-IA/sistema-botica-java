
package Views;
import com.mycompany.nativo.modelos.Cliente;
import com.mycompany.nativo.modelos.DetalleVenta;
import com.mycompany.nativo.modelos.Producto;
import com.mycompany.nativo.modelos.Venta;
import com.mycompany.nativo.modelos.Usuario;
import com.mycompany.nativo.servicios.ClienteService;
import com.mycompany.nativo.servicios.ProductoService;
import com.mycompany.nativo.servicios.VentaService;
import com.mycompany.nativo.servicios.UsuarioService;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.time.LocalDate;


public class VendedorPestañas extends javax.swing.JPanel {
    
    
    private final ClienteService clienteService = new ClienteService();
    private final ProductoService productoService = new ProductoService(); 
    private final UsuarioService usuarioService = new UsuarioService();
    private final VentaService ventaService;
    private boolean modoEdicionCliente = false;
    private int idClienteSeleccionado = -1;
    
    
    private final List<DetalleVenta> carrito = new ArrayList<>(); 
    private final double IGV_RATE = 0.18; 
    private final int ID_USUARIO_VENDEDOR = 2; 

 
    public VendedorPestañas(VentaService vService) {
        initComponents();
        this.ventaService = vService;
        
        cargarTablaClientes(null);
        inicializarVentas();
        
    
    }
    
    private void cargarTablaSeleccionarCliente() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nombre");
        model.addColumn("DNI");

        List<Cliente> listaClientes = clienteService.listarTodos();

        for (Cliente c : listaClientes) {
            model.addRow(new Object[]{
                c.getNombre(),
                c.getDocumento()
            });
        }

        tblSeleccionarCliente.setModel(model);

        tblSeleccionarCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tblSeleccionarCliente.getSelectedRow();
                if (fila != -1) {
                    String nombre = (String) tblSeleccionarCliente.getValueAt(fila, 0);
                    String dni = (String) tblSeleccionarCliente.getValueAt(fila, 1);

                    txtNombreClienteSeleccionado.setText(nombre);
                    txtDniClienteSeleccionado.setText(dni);
                }
            }
        });
    }
    //------------------------------------------------------------------------------------
    private void mostrarBoleta(Venta venta, List<DetalleVenta> detalles, String nombreVendedor, String nombreCliente) {
        StringBuilder boleta = new StringBuilder();

        boleta.append("================================\n");
        boleta.append("       BOTICA MYC FARMA       \n");
        boleta.append("================================\n");
        boleta.append("Fecha: ").append(venta.getFecha()).append("\n");
        boleta.append("Vendedor: ").append(nombreVendedor).append("\n");
        boleta.append("Cliente: ").append(nombreCliente).append("\n");
        boleta.append("DNI/RUC: ").append(venta.getClienteDocumento()).append("\n");
        boleta.append("--------------------------------\n");
        boleta.append(String.format("%-20s %-5s %-10s\n", "Producto", "Cant", "Subtotal"));
        boleta.append("--------------------------------\n");

        for (DetalleVenta dv : detalles) {
 
            Producto p = productoService.buscarProductoPorId(dv.getProductoId());
            String nombreProd = (p != null) ? p.getNombre() : "Desconocido";

 
            if (nombreProd.length() > 18) nombreProd = nombreProd.substring(0, 18) + "..";

            boleta.append(String.format("%-20s %-5d S/ %-8.2f\n", 
                    nombreProd, dv.getCantidad(), dv.getSubtotal()));
        }

        boleta.append("--------------------------------\n");
        boleta.append(String.format("TOTAL A PAGAR:      S/ %.2f\n", venta.getMontoTotal()));
        boleta.append("================================\n");
        boleta.append("     ¡Gracias por su compra!    \n");

 
        JOptionPane.showMessageDialog(this, boleta.toString(), "Boleta de Venta Electrónica", JOptionPane.INFORMATION_MESSAGE);
    }
    private void inicializarVentas() {
 
        cmbMetodoPago.setModel(new DefaultComboBoxModel<>(new String[]{"Efectivo", "Tarjeta", "Yape", "Transferencia"}));
        cargarVendedoresEnComboBox(); 

        cargarTablaProductosVenta(null);
        cargarTablaSeleccionarCliente();  
        actualizarTablaCarrito();

        txtNombreClienteSeleccionado.setEditable(false);
        txtDniClienteSeleccionado.setEditable(false);

        txtDniClienteSeleccionado.setText("0"); 
        txtNombreClienteSeleccionado.setText("Consumidor Final");
        
    }
    private void cargarVendedoresEnComboBox() {
        try {
            int vendedorCargoId = 3;  
            List<Usuario> usuarios = usuarioService.obtenerTodos();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

            for (Usuario u : usuarios) {
                if (u.getCargoId() == vendedorCargoId) {
                    model.addElement(u.getNombre()); 
                }
            }
            cmbSeleccionarVendedor.setModel(model); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar vendedores.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void cargarTablaProductosVenta(List<Producto> listaFiltrada) {
        
        if (listaFiltrada == null) {
            listaFiltrada = productoService.obtenerTodos();
        }

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Precio");
        model.addColumn("Stock");
        model.addColumn("Vencimiento");

        try {
            for (Producto p : listaFiltrada) {
                model.addRow(new Object[]{
                    p.getId(), 
                    p.getNombre(), 
                    p.getPrecio(), 
                    p.getStock(), 
                    p.getFechaVencimiento()
                });
            }
            tblProductosVenta.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al listar productos para Venta: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void actualizarTablaCarrito() {
        
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID Prod.");
        model.addColumn("Producto");
        model.addColumn("Precio Unit.");
        model.addColumn("Cantidad");
        model.addColumn("Subtotal");

        double subtotalGlobal = 0.0;

        for (DetalleVenta dv : carrito) {
            Producto p = productoService.buscarProductoPorId(dv.getProductoId());
            String nombreProducto = (p != null) ? p.getNombre() : "Producto Eliminado";

            model.addRow(new Object[]{
                dv.getProductoId(),
                nombreProducto,
                dv.getSubtotal() / dv.getCantidad(), 
                dv.getCantidad(),
                dv.getSubtotal()
            });
            subtotalGlobal += dv.getSubtotal();
        }

        tblCarrito.setModel(model);
 
        double igvCalculado = subtotalGlobal * IGV_RATE;
        double totalPagar = subtotalGlobal + igvCalculado;
 
        tblSubtotalVenta.setText(String.format("Subtotal: S/ %.2f", subtotalGlobal));
        lblGVVenta.setText(String.format("IGV (18%%): S/ %.2f", igvCalculado));
        txtTotalPagar.setText(String.format("Total a Pagar: S/ %.2f", totalPagar)); 
    }
    
    private void limpiarCamposCliente() {
        
        txtDniClienteRegistro.setText(""); 
        txtNombreClienteRegistro.setText("");
        txtTelefonoClienteRegistro.setText("");

        txtDniClienteRegistro.setEditable(true);
        modoEdicionCliente = false;
        idClienteSeleccionado = -1;
        btnGuardarClienteRegistro.setText("GUARDAR");
    }
 
    private int obtenerIdVendedor(String nombre) {
        
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre)) {
                return u.getId();
            }
        }
        return -1;  
    }
    private void limpiarVenta() {
        carrito.clear();  
        actualizarTablaCarrito();  
        txtBuscarVenta.setText("");
        txtCantidadVenta.setText("");
  
        txtDniClienteSeleccionado.setText("0");
        txtNombreClienteSeleccionado.setText("Consumidor Final");
 
        tblSubtotalVenta.setText("Subtotal: S/ 0.00");
        lblGVVenta.setText("IGV (18%): S/ 0.00");
        txtTotalPagar.setText("Total a Pagar: S/ 0.00");
    }
    
    private void cargarTablaClientes(List<Cliente> listaFiltrada) {

        if (listaFiltrada == null) {
            listaFiltrada = clienteService.listarTodos();
        }

        DefaultTableModel model = new DefaultTableModel();
 
        model.addColumn("ID");
        model.addColumn("DNI/RUC"); 
        model.addColumn("Nombre");
        model.addColumn("Teléfono");

        try {
            for (Cliente c : listaFiltrada) {
                model.addRow(new Object[]{
                    c.getId(), 
                    c.getDocumento(), 
                    c.getNombre(), 
                    c.getTelefono()
                });
            }
 
            tblClientes.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al listar clientes: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }


     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtDniClienteRegistro = new javax.swing.JTextField();
        txtTelefonoClienteRegistro = new javax.swing.JTextField();
        txtNombreClienteRegistro = new javax.swing.JTextField();
        btnGuardarClienteRegistro = new javax.swing.JButton();
        btnLimpiarClienteRegistro = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtBuscarClienteTabla = new javax.swing.JTextField();
        btnBuscarClienteTabla = new javax.swing.JButton();
        btnEditarCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        PanelCompras = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSeleccionarCliente = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProductosVenta = new javax.swing.JTable();
        txtBuscarVenta = new javax.swing.JTextField();
        btnAnadirAlCarrito = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtCantidadVenta = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCarrito = new javax.swing.JTable();
        btnQuitarProducto = new javax.swing.JButton();
        cmbSeleccionarVendedor = new javax.swing.JComboBox<>();
        btnBuscarClienteTablas = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        cmbMetodoPago = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        lblGVVenta = new javax.swing.JLabel();
        tblSubtotalVenta = new javax.swing.JLabel();
        txtTotalPagar = new javax.swing.JLabel();
        btnFinalizarVenta = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtNombreClienteSeleccionado = new javax.swing.JTextField();
        txtDniClienteSeleccionado = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registrar Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("Nombre :");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("Telefono :");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("DNI :");

        btnGuardarClienteRegistro.setBackground(new java.awt.Color(26, 32, 40));
        btnGuardarClienteRegistro.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuardarClienteRegistro.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarClienteRegistro.setText("Guardar");
        btnGuardarClienteRegistro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarClienteRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarClienteRegistroActionPerformed(evt);
            }
        });

        btnLimpiarClienteRegistro.setBackground(new java.awt.Color(255, 135, 66));
        btnLimpiarClienteRegistro.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnLimpiarClienteRegistro.setText("Limpiar");
        btnLimpiarClienteRegistro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTelefonoClienteRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(txtDniClienteRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(txtNombreClienteRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(btnGuardarClienteRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimpiarClienteRegistro)))
                        .addContainerGap(43, Short.MAX_VALUE))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDniClienteRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNombreClienteRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(txtTelefonoClienteRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnLimpiarClienteRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardarClienteRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 250, 390));

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lista de Clientes ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "DNI", "Nombre", "Telefono"
            }
        ));
        jScrollPane1.setViewportView(tblClientes);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Buscar :");

        btnBuscarClienteTabla.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnBuscarClienteTabla.setText("Buscar");
        btnBuscarClienteTabla.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBuscarClienteTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteTablaActionPerformed(evt);
            }
        });

        btnEditarCliente.setBackground(new java.awt.Color(26, 32, 40));
        btnEditarCliente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEditarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnEditarCliente.setText("Editar");
        btnEditarCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarClienteActionPerformed(evt);
            }
        });

        btnEliminarCliente.setBackground(new java.awt.Color(255, 135, 66));
        btnEliminarCliente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminarCliente.setText("Eliminar");
        btnEliminarCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-buscar-30.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(btnEditarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarClienteTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(btnBuscarClienteTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(63, 63, 63))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(txtBuscarClienteTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnBuscarClienteTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEditarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(btnEliminarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, 620, 400));

        jPanel2.setBackground(new java.awt.Color(0, 204, 51));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 40, 20));

        jTabbedPane1.addTab("Registrar Clientes", jPanel1);

        PanelCompras.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Seleccionar Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        tblSeleccionarCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Nombre ", "DNI"
            }
        ));
        jScrollPane2.setViewportView(tblSeleccionarCliente);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Seleccionar Productos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Buscar :");

        tblProductosVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Precio", "Stock", "Vencimiento"
            }
        ));
        jScrollPane4.setViewportView(tblProductosVenta);

        btnAnadirAlCarrito.setBackground(new java.awt.Color(26, 32, 40));
        btnAnadirAlCarrito.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAnadirAlCarrito.setForeground(new java.awt.Color(255, 255, 255));
        btnAnadirAlCarrito.setText("Agregar Compra");
        btnAnadirAlCarrito.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAnadirAlCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnadirAlCarritoActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Cantidad : ");

        tblCarrito.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Producto", "Precio", "Cantidad", "Subtotal"
            }
        ));
        jScrollPane3.setViewportView(tblCarrito);

        btnQuitarProducto.setBackground(new java.awt.Color(26, 32, 40));
        btnQuitarProducto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQuitarProducto.setForeground(new java.awt.Color(255, 255, 255));
        btnQuitarProducto.setText("Quitar Producto");
        btnQuitarProducto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuitarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarProductoActionPerformed(evt);
            }
        });

        cmbSeleccionarVendedor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbSeleccionarVendedor.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selecionar Vendedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        btnBuscarClienteTablas.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBuscarClienteTablas.setText("Buscar");
        btnBuscarClienteTablas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBuscarClienteTablas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteTablasActionPerformed(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-buscar-30.png"))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-carrito-de-compras-30.png"))); // NOI18N
        jLabel12.setText("Carrito : ");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(39, 39, 39)
                        .addComponent(txtBuscarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscarClienteTablas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(41, 41, 41)
                        .addComponent(cmbSeleccionarVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 670, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtCantidadVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(81, 81, 81)
                                        .addComponent(btnAnadirAlCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(btnQuitarProducto)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(txtBuscarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7)))
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbSeleccionarVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBuscarClienteTablas, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtCantidadVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAnadirAlCarrito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(btnQuitarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Comprar :", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        cmbMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo ", "Yape ", "Targeta" }));

        jLabel6.setText("Metodo Pago");

        lblGVVenta.setText("IGV (18%): S/ 0.00");

        tblSubtotalVenta.setText("Subtotal: S/ 0.00");

        txtTotalPagar.setText("Total a Pagar: S/ 0.00");

        btnFinalizarVenta.setBackground(new java.awt.Color(255, 135, 66));
        btnFinalizarVenta.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnFinalizarVenta.setText("REALIZAR VENTA");
        btnFinalizarVenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFinalizarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(cmbMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGVVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tblSubtotalVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(btnFinalizarVenta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblGVVenta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblSubtotalVenta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotalPagar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFinalizarVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Datos Cliente Seleccionado");

        txtNombreClienteSeleccionado.setActionCommand("<Not Set>");

        jPanel3.setBackground(new java.awt.Color(51, 51, 255));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelComprasLayout = new javax.swing.GroupLayout(PanelCompras);
        PanelCompras.setLayout(PanelComprasLayout);
        PanelComprasLayout.setHorizontalGroup(
            PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelComprasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PanelComprasLayout.createSequentialGroup()
                        .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(PanelComprasLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel2)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(PanelComprasLayout.createSequentialGroup()
                        .addComponent(txtNombreClienteSeleccionado)
                        .addGap(18, 18, 18)
                        .addComponent(txtDniClienteSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 721, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
            .addGroup(PanelComprasLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        PanelComprasLayout.setVerticalGroup(
            PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelComprasLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelComprasLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDniClienteSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombreClienteSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Compras", PanelCompras);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1010, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1010, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnQuitarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarProductoActionPerformed
        
        int filaSeleccionada = tblCarrito.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del carrito", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idProducto = (int) tblCarrito.getValueAt(filaSeleccionada, 0);
        carrito.removeIf(dv -> dv.getProductoId() == idProducto);
        actualizarTablaCarrito();
        JOptionPane.showMessageDialog(this, "Producto eliminado del carrito", "Éxito", JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_btnQuitarProductoActionPerformed

    private void btnAnadirAlCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnadirAlCarritoActionPerformed
 
        int filaSeleccionada = tblProductosVenta.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
        }

        String cantidadTexto = txtCantidadVenta.getText().trim();
        if (cantidadTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idProducto = (int) tblProductosVenta.getValueAt(filaSeleccionada, 0);
        Producto producto = productoService.buscarProductoPorId(idProducto);

        if (producto == null) {
            JOptionPane.showMessageDialog(this, " Error: Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidad > producto.getStock()) {
            JOptionPane.showMessageDialog(this, " Stock insuficiente. Disponible: " + producto.getStock(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean productoExiste = false;
        for (DetalleVenta dv : carrito) {
            if (dv.getProductoId() == idProducto) {
                int nuevaCantidad = dv.getCantidad() + cantidad;
                if (nuevaCantidad > producto.getStock()) {
                    JOptionPane.showMessageDialog(this, "Stock insuficiente. Disponible: " + producto.getStock(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dv.setCantidad(nuevaCantidad);
                dv.setSubtotal(nuevaCantidad * producto.getPrecio());
                productoExiste = true;
                break;
            }
        }

        if (!productoExiste) {
            DetalleVenta nuevoDetalle = new DetalleVenta();
            nuevoDetalle.setProductoId(idProducto);
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setSubtotal(cantidad * producto.getPrecio());
            carrito.add(nuevoDetalle);
        }
        actualizarTablaCarrito();
        txtCantidadVenta.setText("");
        JOptionPane.showMessageDialog(this, "Producto agregado al carrito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnAnadirAlCarritoActionPerformed

    private void btnGuardarClienteRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarClienteRegistroActionPerformed
       
        String documento = txtDniClienteRegistro.getText().trim();
        String nombre = txtNombreClienteRegistro.getText().trim();
        String telefono = txtTelefonoClienteRegistro.getText().trim(); 

        if (documento.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El Documento y Nombre son campos obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = new Cliente(documento, nombre, telefono);
        boolean exito;

        if (modoEdicionCliente) {

            cliente.setId(idClienteSeleccionado); 
            exito = clienteService.actualizarCliente(cliente);

            txtDniClienteRegistro.setEditable(true); 

        } else {

            exito = clienteService.registrarCliente(cliente);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this, "Cliente " + (modoEdicionCliente ? "actualizado" : "registrado") + " con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            modoEdicionCliente = false;
            btnGuardarClienteRegistro.setText("GUARDAR");

        } else {

            JOptionPane.showMessageDialog(this, "Error: Revise si el DNI/RUC ya existe o si el formato es incorrecto.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
        }

        limpiarCamposCliente();
        cargarTablaClientes(null);
        cargarTablaSeleccionarCliente(); 
    }//GEN-LAST:event_btnGuardarClienteRegistroActionPerformed

    private void btnBuscarClienteTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteTablaActionPerformed
        
        String criterio = txtBuscarClienteTabla.getText().trim(); 
    
        if (criterio.isEmpty()) {
            cargarTablaClientes(null); 
            return;
        }

        try {
            List<Cliente> clientesEncontrados = clienteService.buscarClientesPorCriterio(criterio);

            cargarTablaClientes(clientesEncontrados); 

            if (clientesEncontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron clientes para el criterio: " + criterio, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la búsqueda de clientes: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnBuscarClienteTablaActionPerformed

    private void btnEditarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarClienteActionPerformed
        int filaSeleccionada = tblClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente de la lista para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        idClienteSeleccionado = (int) tblClientes.getValueAt(filaSeleccionada, 0);
        String documento = (String) tblClientes.getValueAt(filaSeleccionada, 1);


        Cliente clienteAEditar = clienteService.buscarPorDocumento(documento);

        if (clienteAEditar != null) {
            txtDniClienteRegistro.setText(clienteAEditar.getDocumento());
            txtNombreClienteRegistro.setText(clienteAEditar.getNombre());
            txtTelefonoClienteRegistro.setText(clienteAEditar.getTelefono());

            txtDniClienteRegistro.setEditable(false); 

            modoEdicionCliente = true;
            btnGuardarClienteRegistro.setText("ACTUALIZAR");

        } else {
             JOptionPane.showMessageDialog(this, "Error: Cliente no encontrado en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditarClienteActionPerformed

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed

        int filaSeleccionada = tblClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tblClientes.getValueAt(filaSeleccionada, 0);
        String documento = (String) tblClientes.getValueAt(filaSeleccionada, 1);
        Object[] botones = {"Sí", "No"};

        int confirmacion = JOptionPane.showOptionDialog(
            this,
            "¿Está seguro de eliminar al cliente " + documento + "?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,   
            botones, 
            botones[1] 
        );
        if (confirmacion == 0) {
            boolean exito = clienteService.eliminarCliente(id);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTablaClientes(null); 
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar. El cliente tiene ventas registradas.", "Error de BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void btnFinalizarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarVentaActionPerformed
        try {
            if (carrito.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String dniCliente = txtDniClienteSeleccionado.getText();
            String nombreVendedor = (String) cmbSeleccionarVendedor.getSelectedItem();
            String nombreCliente = txtNombreClienteSeleccionado.getText(); // Capturamos nombre cliente para la boleta

            if (dniCliente.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idVendedor = obtenerIdVendedor(nombreVendedor);
            if (idVendedor == -1) {
                JOptionPane.showMessageDialog(this, "Error al identificar vendedor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double montoTotal = 0.0;
            for (DetalleVenta dv : carrito) {
                montoTotal += dv.getSubtotal();
            }
            double totalConIGV = montoTotal + (montoTotal * IGV_RATE);

            Venta venta = new Venta();
            venta.setFecha(java.sql.Date.valueOf(LocalDate.now())); 
            venta.setUsuarioId(idVendedor);
            venta.setClienteDocumento(dniCliente);
            venta.setMetodoPago(cmbMetodoPago.getSelectedItem().toString());
            venta.setMontoTotal(totalConIGV);

            boolean exito = ventaService.procesarVenta(venta, carrito);

            if (exito) {
                mostrarBoleta(venta, carrito, nombreVendedor, nombreCliente);

                limpiarVenta();
                cargarTablaProductosVenta(null);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en BD.\nRevise la consola para detalles.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "ERROR CRÍTICO: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnFinalizarVentaActionPerformed

    private void btnBuscarClienteTablasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteTablasActionPerformed

        String criterio = txtBuscarVenta.getText().trim(); 
        if (criterio.isEmpty()) {
            cargarTablaProductosVenta(null); 
            return;
        }
        try {
            List<Producto> productosEncontrados = productoService.buscarProductosPorCriterio(criterio);
            cargarTablaProductosVenta(productosEncontrados); 

            if (productosEncontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron productos para: " + criterio, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al buscar productos: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnBuscarClienteTablasActionPerformed
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelCompras;
    private javax.swing.JButton btnAnadirAlCarrito;
    private javax.swing.JButton btnBuscarClienteTabla;
    private javax.swing.JButton btnBuscarClienteTablas;
    private javax.swing.JButton btnEditarCliente;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnFinalizarVenta;
    private javax.swing.JButton btnGuardarClienteRegistro;
    private javax.swing.JButton btnLimpiarClienteRegistro;
    private javax.swing.JButton btnQuitarProducto;
    private javax.swing.JComboBox<String> cmbMetodoPago;
    private javax.swing.JComboBox<String> cmbSeleccionarVendedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblGVVenta;
    private javax.swing.JTable tblCarrito;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTable tblProductosVenta;
    private javax.swing.JTable tblSeleccionarCliente;
    private javax.swing.JLabel tblSubtotalVenta;
    private javax.swing.JTextField txtBuscarClienteTabla;
    private javax.swing.JTextField txtBuscarVenta;
    private javax.swing.JTextField txtCantidadVenta;
    private javax.swing.JTextField txtDniClienteRegistro;
    private javax.swing.JTextField txtDniClienteSeleccionado;
    private javax.swing.JTextField txtNombreClienteRegistro;
    private javax.swing.JTextField txtNombreClienteSeleccionado;
    private javax.swing.JTextField txtTelefonoClienteRegistro;
    private javax.swing.JLabel txtTotalPagar;
    // End of variables declaration//GEN-END:variables
}

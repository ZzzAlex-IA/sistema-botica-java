package Views;

import com.mycompany.nativo.modelos.Usuario;
import com.mycompany.nativo.modelos.Venta;
import com.mycompany.nativo.servicios.UsuarioService;
import com.mycompany.nativo.servicios.VentaService;
import com.mycompany.nativo.repositorios.VentaDAOImpl;
import com.mycompany.nativo.repositorios.ProductoDAOImpl;
import com.mycompany.nativo.repositorios.DetalleVentaDAOImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mycompany.nativo.servicios.ProveedorService;
import com.mycompany.nativo.servicios.ProductoService;
import com.mycompany.nativo.repositorios.DetalleVentaDAOImpl;

public class JefePestañas extends javax.swing.JPanel {
    private final VentaService ventaService;
    private final UsuarioService usuarioService;
    
    private final int ID_CARGO_VENDEDOR = 3;

    private final ProductoService productoService = new ProductoService();
    private final ProveedorService proveedorService = new ProveedorService();
    private final DetalleVentaDAOImpl detalleVentaDAO = new DetalleVentaDAOImpl();


    public JefePestañas() {
        this.ventaService = new VentaService(new VentaDAOImpl(), new ProductoDAOImpl(), new DetalleVentaDAOImpl());
        this.usuarioService = new UsuarioService();
        initComponents();
        
        cargarVendedoresEnCombo(); 
        
        filtrarVentasGeneral("HOY");
        System.out.println("JefePestañas inicializado correctamente"); 

        jTabbedPane2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                int index = jTabbedPane2.getSelectedIndex();
                
                switch (index) {
                    case 0: 
                        cargarTablaProductos();
                        break;
                    case 1: 
                        cargarTablaProveedores();
                        break;
                    case 2: 
                        cargarTablaUsuarios();
                        break;
                    case 3: 
                        cargarTablaVentas();
                        break;
                    case 4: 
                        cargarTablaDetalleVenta();
                        break;
                }
            }
        });
        cargarTablaProductos();
    }

    private void filtrarVentasGeneral(String criterio) {
        List<Venta> todas = ventaService.obtenerTodos();
        List<Venta> filtradas = todas;
        LocalDate hoy = LocalDate.now();

        try {
            switch (criterio) {
                case "HOY":
                    filtradas = todas.stream()
                            .filter(v -> v.getFecha().toLocalDate().isEqual(hoy))
                            .collect(Collectors.toList());
                    break;
                case "SEMANA":

                    filtradas = todas.stream()
                            .filter(v -> !v.getFecha().toLocalDate().isBefore(hoy.minusDays(7)))
                            .collect(Collectors.toList());
                    break;
                case "MES":
 
                    filtradas = todas.stream()
                            .filter(v -> v.getFecha().toLocalDate().getMonth() == hoy.getMonth() && 
                                         v.getFecha().toLocalDate().getYear() == hoy.getYear())
                            .collect(Collectors.toList());
                    break;
                case "FECHA":

                     String fechaTexto = txtFechaFiltro.getText().trim();
                     if(!fechaTexto.isEmpty()) {
                         LocalDate fechaBusq = LocalDate.parse(fechaTexto);
                         filtradas = todas.stream()
                                 .filter(v -> v.getFecha().toLocalDate().isEqual(fechaBusq))
                                 .collect(Collectors.toList());
                     }
                     break;
                case "TODOS":
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Use: AAAA-MM-DD (Ej: 2025-11-28)");
            return;
        }

        llenarTablaGeneral(filtradas);

        calcularTotal(filtradas);
    }

    private void calcularTotal(List<Venta> lista) {
        double total = 0.0;
        for (Venta v : lista) {
            total += v.getMontoTotal();
        }
        lblTotalVentas.setText(String.format("%.2f", total));
    }

    private void llenarTablaGeneral(List<Venta> lista) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Fecha");
        model.addColumn("Vendedor");
        model.addColumn("Cliente");
        model.addColumn("Monto S/");

        for (Venta v : lista) {
            Usuario u = usuarioService.buscarUsuarioPorId(v.getUsuarioId());
            String nombreVendedor = (u != null) ? u.getNombre() : "Desconocido";
            
            model.addRow(new Object[]{
                v.getId(),
                v.getFecha(),
                nombreVendedor,
                v.getClienteDocumento(), 
                String.format("%.2f", v.getMontoTotal())
            });
        }
        tblVentasGeneral.setModel(model);
    }

    private void cargarVendedoresEnCombo() {
        cmbVendedores.removeAllItems();
        cmbVendedores.addItem("Seleccione Vendedor...");
        
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        for (Usuario u : usuarios) {
            if (u.getCargoId() == ID_CARGO_VENDEDOR) {
                cmbVendedores.addItem(u.getNombre());
            }
        }
    }

    private void cargarVentasPorVendedorSeleccionado() {
        String nombreSeleccionado = (String) cmbVendedores.getSelectedItem();

        if (nombreSeleccionado == null || nombreSeleccionado.equals("Seleccione Vendedor...")) {
            tblAvanzada.setModel(new DefaultTableModel()); 
            lblVendedor.setText("TOTAL: S/ 0.00"); 
            return;
        }

        int idUsuario = -1;
        for(Usuario u : usuarioService.obtenerTodos()) {
            if(u.getNombre().equals(nombreSeleccionado)) {
                idUsuario = u.getId();
                break;
            }
        }

        if (idUsuario != -1) {
            List<Venta> ventas = ventaService.obtenerVentasPorUsuario(idUsuario);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID Venta");
            model.addColumn("Fecha");
            model.addColumn("Total Vendido");
            model.addColumn("Cliente");

            double totalVendedor = 0.0; 

            for (Venta v : ventas) {
                totalVendedor += v.getMontoTotal(); 
                model.addRow(new Object[]{
                    v.getId(),
                    v.getFecha(),
                    String.format("%.2f", v.getMontoTotal()),
                    v.getClienteDocumento()
                });
            }

            tblAvanzada.setModel(model);
            lblVendedor.setText("TOTAL: S/ " + String.format("%.2f", totalVendedor)); 
        }
    }



    private void cargarTopProductos() {
        try {
            List<Map<String, Object>> top = ventaService.obtenerTopProductos();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Nombre Producto");
            model.addColumn("Cantidad Vendida"); 


            if (top.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "No se encontraron productos más vendidos.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Map<String, Object> fila : top) {
                    model.addRow(new Object[]{ 
                        String.valueOf(fila.get("nombreProducto")), 
                        String.valueOf(fila.get("cantidadTotal")) 
                    });
                }
            }

            tblTopProductos.setModel(model); 

        } catch (Exception e) {
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "ERROR: Fallo al cargar el reporte Top Productos: " + e.getMessage(), "Error de Vista", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void cargarTablaProductos() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Precio");
        model.addColumn("Stock");
        model.addColumn("Fecha Vencimiento");
        model.addColumn("ID Proveedor");

        try {
            List<com.mycompany.nativo.modelos.Producto> productos = productoService.obtenerTodos(); 
            for (com.mycompany.nativo.modelos.Producto p : productos) {
                model.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), String.valueOf(p.getFechaVencimiento()), p.getProveedorId()
                });
            }
            tblProductos.setModel(model); 
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
    }

    private void cargarTablaProveedores() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("RUC");
        model.addColumn("Teléfono");
        model.addColumn("Dirección");

        try {
            List<com.mycompany.nativo.modelos.Proveedor> proveedores = proveedorService.listarTodosLosProveedores();
            for (com.mycompany.nativo.modelos.Proveedor p : proveedores) {
                model.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getRuc(), p.getTelefono(), p.getDireccion()
                });
            }
            tblProveedores.setModel(model); 
        } catch (Exception e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
        }
    }
 
    private void cargarTablaUsuarios() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("DNI");
        model.addColumn("Cargo ID");
        model.addColumn("Estado");

        try {
            List<Usuario> usuarios = usuarioService.obtenerTodos();
            for (Usuario u : usuarios) {
                model.addRow(new Object[]{
                    u.getId(), u.getNombre(), u.getDni(), u.getCargoId(), u.getEstado()
                });
            }
            tblUsuarios.setModel(model); 
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
    }

 
    private void cargarTablaVentas() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Fecha");
        model.addColumn("Usuario ID");
        model.addColumn("Cliente Documento");
        model.addColumn("Método Pago");
        model.addColumn("Monto Total");

        try {
            List<Venta> ventas = ventaService.obtenerTodos();
            for (Venta v : ventas) {
                model.addRow(new Object[]{
                    v.getId(), v.getFecha(), v.getUsuarioId(), v.getClienteDocumento(), v.getMetodoPago(), v.getMontoTotal()
                });
            }
            tblVentas.setModel(model); 
        } catch (Exception e) {
            System.err.println("Error al cargar ventas: " + e.getMessage());
        }
    }

    private void cargarTablaDetalleVenta() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Venta ID");
        model.addColumn("Producto ID");
        model.addColumn("Cantidad");
        model.addColumn("Subtotal");

        try {
            List<com.mycompany.nativo.modelos.DetalleVenta> detalles = detalleVentaDAO.obtenerTodos(); 
            for (com.mycompany.nativo.modelos.DetalleVenta d : detalles) {
                model.addRow(new Object[]{
                    d.getId(), d.getVentaId(), d.getProductoId(), d.getCantidad(), d.getSubtotal()
                });
            }
            tblDetalleVenta.setModel(model); 
        } catch (Exception e) {
            System.err.println("Error al cargar detalle venta: " + e.getMessage());
        }
    }
 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFechaFiltro = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        btnVerTodo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVentasGeneral = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnHoy = new javax.swing.JButton();
        btnSemana = new javax.swing.JButton();
        btnMes = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblTotalVentas = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnTopProductos = new javax.swing.JButton();
        cmbVendedores = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAvanzada = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        lblVendedor = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTopProductos = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblProveedores = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblVentas = new javax.swing.JTable();
        jPanel14 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblDetalleVenta = new javax.swing.JTable();
        jPanel15 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(197, 223, 223));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles de ventas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        jLabel1.setText("Filtrar por fecha : año/mes/dia");

        btnFiltrar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnVerTodo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnVerTodo.setText("Ver todo");
        btnVerTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerTodoActionPerformed(evt);
            }
        });

        tblVentasGeneral.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Fecha", "Vendedor", "Cliente", "Monto s/"
            }
        ));
        jScrollPane1.setViewportView(tblVentasGeneral);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Reportes de venta :", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        btnHoy.setBackground(new java.awt.Color(255, 135, 66));
        btnHoy.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnHoy.setText("Hoy");
        btnHoy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHoy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoyActionPerformed(evt);
            }
        });

        btnSemana.setBackground(new java.awt.Color(26, 32, 40));
        btnSemana.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnSemana.setForeground(new java.awt.Color(255, 255, 255));
        btnSemana.setText("Esta Semana");
        btnSemana.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSemana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSemanaActionPerformed(evt);
            }
        });

        btnMes.setBackground(new java.awt.Color(26, 32, 40));
        btnMes.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnMes.setForeground(new java.awt.Color(255, 255, 255));
        btnMes.setText("Este Mes ");
        btnMes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnHoy, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSemana, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMes, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHoy, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSemana, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMes, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText("TOTAL :  S/");

        lblTotalVentas.setText("0.00");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-buscar-30.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(42, 42, 42)
                            .addComponent(lblTotalVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(btnFiltrar)
                            .addGap(33, 33, 33)
                            .addComponent(btnVerTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFechaFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtFechaFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(btnVerTodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblTotalVentas))
                .addGap(38, 38, 38)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 450, 480));

        jPanel6.setBackground(new java.awt.Color(153, 153, 153));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles avanzado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        btnTopProductos.setBackground(new java.awt.Color(26, 32, 40));
        btnTopProductos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTopProductos.setForeground(new java.awt.Color(255, 255, 255));
        btnTopProductos.setText("Top Productos mas vendidos");
        btnTopProductos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTopProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopProductosActionPerformed(evt);
            }
        });

        cmbVendedores.setBorder(javax.swing.BorderFactory.createTitledBorder("Seleccionar Vendedor"));
        cmbVendedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbVendedoresActionPerformed(evt);
            }
        });

        tblAvanzada.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblAvanzada);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Total venta : ");

        lblVendedor.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblVendedor.setText("0.00");

        tblTopProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Nombre Producto", "Cantidad Venta"
            }
        ));
        jScrollPane3.setViewportView(tblTopProductos);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbVendedores, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(28, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btnTopProductos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblVendedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(71, 71, 71))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(cmbVendedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTopProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(lblVendedor))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 10, 480, 480));

        jTabbedPane1.addTab("Reportes de Ventas", jPanel1);

        jTabbedPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Precio", "Stock", "Stock Minimo", "Fecha Vencimiento", "ID provedor"
            }
        ));
        jScrollPane4.setViewportView(tblProductos);

        jPanel3.setBackground(new java.awt.Color(255, 153, 0));

        jLabel5.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel5.setText("P");

        jLabel6.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel6.setText("R");

        jLabel7.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel7.setText("O");

        jLabel8.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel8.setText("D");

        jLabel9.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel9.setText("U");

        jLabel10.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel10.setText("C");

        jLabel11.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel11.setText("T");

        jLabel12.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel12.setText("O");

        jLabel13.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel13.setText("S");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 870, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 153, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(86, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Producto", jPanel7);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

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
        jScrollPane5.setViewportView(tblProveedores);

        jPanel12.setBackground(java.awt.Color.green);

        jLabel14.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel14.setText("P");

        jLabel15.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel15.setText("R");

        jLabel16.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel16.setText("O");

        jLabel17.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel17.setText("v");

        jLabel18.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel18.setText("e");

        jLabel19.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel19.setText("d");

        jLabel20.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel20.setText("o");

        jLabel21.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel21.setText("r");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 907, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(116, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Provedor", jPanel8);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

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
        jScrollPane6.setViewportView(tblUsuarios);

        jPanel13.setBackground(java.awt.Color.blue);

        jLabel22.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel22.setText("U");

        jLabel23.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel23.setText("S");

        jLabel24.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel24.setText("U");

        jLabel25.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel25.setText("A");

        jLabel26.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel26.setText("R");

        jLabel27.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel27.setText("I");

        jLabel28.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel28.setText("o");

        jLabel29.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel29.setText("S");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 925, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(90, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(121, 121, 121))
        );

        jTabbedPane2.addTab("Usuarios", jPanel9);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        tblVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Fecha", "Usuario_id", "Cliente_documento", "Metodo_pago", "Monto_total"
            }
        ));
        jScrollPane7.setViewportView(tblVentas);

        jPanel14.setBackground(java.awt.Color.yellow);

        jLabel30.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel30.setText("V");

        jLabel31.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel31.setText("E");

        jLabel32.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel32.setText("N");

        jLabel33.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel33.setText("T");

        jLabel34.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel34.setText("A");

        jLabel35.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel35.setText("S");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel35)
                .addContainerGap(85, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 869, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(123, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(87, 87, 87))
        );

        jTabbedPane2.addTab("Venta", jPanel10);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        tblDetalleVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Venta_id", "Producto_id", "Cantidad", "Subtotal"
            }
        ));
        jScrollPane8.setViewportView(tblDetalleVenta);

        jPanel15.setBackground(java.awt.Color.red);

        jLabel36.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel36.setText("D");

        jLabel37.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel37.setText("e");

        jLabel38.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel38.setText("t");

        jLabel39.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel39.setText("a");

        jLabel40.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel40.setText("l");

        jLabel41.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel41.setText("l");

        jLabel42.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel42.setText("e");

        jLabel43.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        jLabel43.setText("s");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38)
                    .addComponent(jLabel39)
                    .addComponent(jLabel40)
                    .addComponent(jLabel41)
                    .addComponent(jLabel42)
                    .addComponent(jLabel43))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel43)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 916, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(87, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(74, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Detalle Venta", jPanel11);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        jTabbedPane1.addTab("Informacion General", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbVendedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbVendedoresActionPerformed
        cargarVentasPorVendedorSeleccionado();
    }//GEN-LAST:event_cmbVendedoresActionPerformed

    private void btnTopProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopProductosActionPerformed
        cargarTopProductos();
    }//GEN-LAST:event_btnTopProductosActionPerformed

    private void btnMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMesActionPerformed
        filtrarVentasGeneral("MES");
    }//GEN-LAST:event_btnMesActionPerformed

    private void btnSemanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSemanaActionPerformed
        filtrarVentasGeneral("SEMANA");
    }//GEN-LAST:event_btnSemanaActionPerformed

    private void btnHoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoyActionPerformed
        filtrarVentasGeneral("HOY");

    }//GEN-LAST:event_btnHoyActionPerformed

    private void btnVerTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerTodoActionPerformed
        filtrarVentasGeneral("TODOS");
        txtFechaFiltro.setText("");
    }//GEN-LAST:event_btnVerTodoActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        filtrarVentasGeneral("FECHA");
    }//GEN-LAST:event_btnFiltrarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnHoy;
    private javax.swing.JButton btnMes;
    private javax.swing.JButton btnSemana;
    private javax.swing.JButton btnTopProductos;
    private javax.swing.JButton btnVerTodo;
    private javax.swing.JComboBox<String> cmbVendedores;
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
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
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
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblTotalVentas;
    private javax.swing.JLabel lblVendedor;
    private javax.swing.JTable tblAvanzada;
    private javax.swing.JTable tblDetalleVenta;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTable tblProveedores;
    private javax.swing.JTable tblTopProductos;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTable tblVentas;
    private javax.swing.JTable tblVentasGeneral;
    private javax.swing.JTextField txtFechaFiltro;
    // End of variables declaration//GEN-END:variables
}

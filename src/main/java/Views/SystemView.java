package Views;
import Views.VendedorPestañas;
import Views.JefePestañas;
import Views.AdminPestañas;
import Controllers.SettingsControllers;
import java.awt.BorderLayout;
import javax.swing.JFrame;  
import javax.swing.JPanel;


import com.mycompany.nativo.servicios.VentaService;
import com.mycompany.nativo.repositorios.VentaDAO;
import com.mycompany.nativo.repositorios.VentaDAOImpl;
import com.mycompany.nativo.repositorios.ProductoDAO;
import com.mycompany.nativo.repositorios.ProductoDAOImpl;

import com.mycompany.nativo.repositorios.DetalleVentaDAOImpl;
import com.mycompany.nativo.repositorios.ProductoDAOImpl;
import com.mycompany.nativo.repositorios.VentaDAOImpl;
import com.mycompany.nativo.servicios.VentaService;
public class SystemView extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SystemView.class.getName());

    private VentaService ventaService;
    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;
    
    public SystemView() {
        
        initComponents();
   
        setSize(1208, 680);  
        setResizable(false);  
        setTitle("Panel de Administracion");  
        setLocationRelativeTo(null);  
        SettingsControllers setting = new SettingsControllers(this);
        contenedorPestañas.setLayout(new BorderLayout());
        System.out.println("🟢 SystemView iniciado");
        System.out.println("🟢 contenedorPestañas tamaño: " + contenedorPestañas.getWidth() + "x" + contenedorPestañas.getHeight());
 
        VentaDAOImpl ventaDao = new VentaDAOImpl();
        ProductoDAOImpl productoDao = new ProductoDAOImpl();
        DetalleVentaDAOImpl detalleDao = new DetalleVentaDAOImpl();
 
        this.ventaService = new VentaService(ventaDao, productoDao, detalleDao);
 
        this.repaint();
     
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Menu = new javax.swing.JPanel();
        jPanelVendedor = new javax.swing.JPanel();
        jLabelVendedor = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanelAdmin = new javax.swing.JPanel();
        jLabelAdmin = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanelJefe = new javax.swing.JPanel();
        jLabelJefe = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        Logo = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        Cabecera = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        contenedorPestañas = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Menu.setBackground(new java.awt.Color(26, 32, 40));
        Menu.setForeground(new java.awt.Color(255, 255, 255));
        Menu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelVendedor.setBackground(new java.awt.Color(26, 32, 40));
        jPanelVendedor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanelVendedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                colores(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                Color_salida(evt);
            }
        });

        jLabelVendedor.setBackground(new java.awt.Color(26, 32, 40));
        jLabelVendedor.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelVendedor.setForeground(new java.awt.Color(255, 255, 255));
        jLabelVendedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-medicamentos-64 (2).png"))); // NOI18N
        jLabelVendedor.setText("  Vendedor");
        jLabelVendedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ClickVendedor(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(0, 150, 136));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelVendedorLayout = new javax.swing.GroupLayout(jPanelVendedor);
        jPanelVendedor.setLayout(jPanelVendedorLayout);
        jPanelVendedorLayout.setHorizontalGroup(
            jPanelVendedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelVendedorLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelVendedor, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
        );
        jPanelVendedorLayout.setVerticalGroup(
            jPanelVendedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelVendedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelVendedorLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        Menu.add(jPanelVendedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 200, 60));

        jPanelAdmin.setBackground(new java.awt.Color(26, 32, 40));
        jPanelAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanelAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                color_admin_entra(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                color_admin_salida(evt);
            }
        });

        jLabelAdmin.setBackground(new java.awt.Color(26, 32, 40));
        jLabelAdmin.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelAdmin.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-administrador-66.png"))); // NOI18N
        jLabelAdmin.setText("Administrador");
        jLabelAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clickAdmin(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(0, 150, 136));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelAdminLayout = new javax.swing.GroupLayout(jPanelAdmin);
        jPanelAdmin.setLayout(jPanelAdminLayout);
        jPanelAdminLayout.setHorizontalGroup(
            jPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAdminLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelAdminLayout.setVerticalGroup(
            jPanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Menu.add(jPanelAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 200, 60));

        jPanelJefe.setBackground(new java.awt.Color(26, 32, 40));
        jPanelJefe.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanelJefe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                color_jefe_entrada(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                color_jefe_salida(evt);
            }
        });

        jLabelJefe.setBackground(new java.awt.Color(26, 32, 40));
        jLabelJefe.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelJefe.setForeground(new java.awt.Color(255, 255, 255));
        jLabelJefe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icons8-jefe-64.png"))); // NOI18N
        jLabelJefe.setText("   Jefe");
        jLabelJefe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clickJefe(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(0, 150, 136));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelJefeLayout = new javax.swing.GroupLayout(jPanelJefe);
        jPanelJefe.setLayout(jPanelJefeLayout);
        jPanelJefeLayout.setHorizontalGroup(
            jPanelJefeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelJefeLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelJefe, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelJefeLayout.setVerticalGroup(
            jPanelJefeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelJefeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabelJefe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Menu.add(jPanelJefe, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 325, 200, 60));

        getContentPane().add(Menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 200, 580));

        Logo.setBackground(new java.awt.Color(26, 32, 40));
        Logo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Captura de pantalla 2025-11-26 121358.png"))); // NOI18N
        Logo.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 160, 70));

        getContentPane().add(Logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 100));
        Logo.getAccessibleContext().setAccessibleName("");

        Cabecera.setBackground(new java.awt.Color(0, 150, 136));
        Cabecera.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("Botica Myc Farma");
        Cabecera.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));

        getContentPane().add(Cabecera, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 1010, 100));

        contenedorPestañas.setBackground(new java.awt.Color(255, 255, 255));
        contenedorPestañas.setForeground(new java.awt.Color(255, 255, 255));
        contenedorPestañas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Engravers MT", 2, 48)); // NOI18N
        jLabel3.setText("MyC Farma");
        contenedorPestañas.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 180, 430, 90));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI Light", 0, 14)); // NOI18N
        jLabel4.setText("\"Tu botica digital de confianza. Aquí podrás gestionar ventas, controlar stock, registrar proveedores y");
        contenedorPestañas.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 320, 630, 30));

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI Light", 0, 14)); // NOI18N
        jLabel5.setText("mantener el seguimiento de tus productos en tiempo real.\"");
        contenedorPestañas.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 360, 370, 20));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Light", 0, 14)); // NOI18N
        jLabel6.setText("\"Trabajamos para brindarte una experiencia rápida, ordenada y segura en la administración de tu botica.\"");
        contenedorPestañas.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 400, 660, 30));

        getContentPane().add(contenedorPestañas, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 1000, 560));

        pack();
    }// </editor-fold>//GEN-END:initComponents
 

    private void colores(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_colores
      
        jPanelVendedor.setBackground(new java.awt.Color(0, 150, 136));      
    }//GEN-LAST:event_colores

    private void Color_salida(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Color_salida
      
        jPanelVendedor.setBackground(new java.awt.Color(26,32,40)); 
    }//GEN-LAST:event_Color_salida

    private void color_admin_entra(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_color_admin_entra
         
        jPanelAdmin.setBackground(new java.awt.Color(0, 150, 136));       
    }//GEN-LAST:event_color_admin_entra

    private void color_admin_salida(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_color_admin_salida
       
        jPanelAdmin.setBackground(new java.awt.Color(26,32,40));    
    }//GEN-LAST:event_color_admin_salida

    private void color_jefe_entrada(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_color_jefe_entrada
         
        jPanelJefe.setBackground(new java.awt.Color(0, 150, 136));        
    }//GEN-LAST:event_color_jefe_entrada

    private void color_jefe_salida(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_color_jefe_salida
         
        jPanelJefe.setBackground(new java.awt.Color(26,32,40));        
    }//GEN-LAST:event_color_jefe_salida
 
    public void mostrarPanelAdmin() {
         System.out.println("🔵 cargarPanelAdmin() llamado");
        AdminPestañas panel = new AdminPestañas();
        System.out.println("🔵 AdminPestañas creado: " + (panel != null));
        mostrarPanel(panel);
    }

    public void mostrarPanelJefe() {
        System.out.println("🔵 cargarPanelJefe() llamado");
        JefePestañas panel = new JefePestañas();
        System.out.println("🔵 JefePestañas creado: " + (panel != null));
        mostrarPanel(panel);

    }
 
    public void cargarPanelAdmin() {
        mostrarPanel(new AdminPestañas());
    }

 
    public void cargarPanelJefe() {
        mostrarPanel(new JefePestañas());
    }
 
    public void cargarPanelVendedor() {
            mostrarPanel(new VendedorPestañas(ventaService));
    }
 
 
    private void ClickVendedor(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ClickVendedor
  
        mostrarPanel(new VendedorPestañas(ventaService));
    }//GEN-LAST:event_ClickVendedor

    private void clickAdmin(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clickAdmin
        
        LoginView login = new LoginView(this, "admin");
        login.setVisible(true);
    }//GEN-LAST:event_clickAdmin

    private void clickJefe(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clickJefe
       LoginView login = new LoginView(this, "jefe");
       login.setVisible(true);
    }//GEN-LAST:event_clickJefe
 
    public void mostrarPanel(JPanel panel) {
   
        System.out.println("mostrarPanel() llamado");
        System.out.println("Panel recibido: " + panel.getClass().getName());
        System.out.println("contenedorPestañas es null? " + (contenedorPestañas == null));
        System.out.println("Tamaño contenedor: " + contenedorPestañas.getWidth() + "x" + contenedorPestañas.getHeight());

        panel.setSize(contenedorPestañas.getWidth(), contenedorPestañas.getHeight());
        panel.setLocation(0, 0);

        contenedorPestañas.removeAll();
        contenedorPestañas.add(panel, BorderLayout.CENTER); 
        contenedorPestañas.revalidate();
        contenedorPestañas.repaint();

        System.out.println("Panel agregado al contenedor");

    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new SystemView().setVisible(true));
    }

    
    
    
    
    
    
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Cabecera;
    private javax.swing.JPanel Logo;
    private javax.swing.JPanel Menu;
    public javax.swing.JPanel contenedorPestañas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabelAdmin;
    public javax.swing.JLabel jLabelJefe;
    public javax.swing.JLabel jLabelVendedor;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    public javax.swing.JPanel jPanelAdmin;
    public javax.swing.JPanel jPanelJefe;
    public javax.swing.JPanel jPanelVendedor;
    // End of variables declaration//GEN-END:variables

   
   
}


package Views;
import Views.AdminPestañas;
import Views.JefePestañas;
import Views.VendedorPestañas;
import Main.Main;
import javax.swing.JOptionPane;
import Views.SystemView; 
import javax.swing.JFrame;

public class LoginView extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginView.class.getName());
    private SystemView sistemaPrincipal;
    private String tipoUsuario;
    
    public LoginView() {
        initComponents();
        setSize(930,420);
        setResizable(false);
        setTitle("Ingresar al sistema");
        setLocationRelativeTo(null);
        this.repaint();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
   
    
    }
    public LoginView(SystemView sistema, String tipo) {
       this();
       this.sistemaPrincipal = sistema;
       this.tipoUsuario = tipo;
       setTitle("Login " + tipo.toUpperCase());

       System.out.println("🔷 LoginView creado");
       System.out.println("🔷 Tipo de usuario: " + tipo);
       System.out.println("🔷 Sistema principal es null? " + (sistema == null));



    }
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txt_userName = new javax.swing.JTextField();
        txt_pasword = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btn_enter = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PANEL DE ADMINISTRACIÓN ");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 153, 51));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("USUARIO");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 178, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Password");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 242, 90, -1));

        txt_userName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jPanel1.add(txt_userName, new org.netbeans.lib.awtextra.AbsoluteConstraints(209, 177, 134, -1));
        jPanel1.add(txt_pasword, new org.netbeans.lib.awtextra.AbsoluteConstraints(209, 244, 134, -1));

        jButton1.setText("jButton1");
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(209, 433, -1, -1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("INICIAR SESION ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(99, 33, -1, -1));

        btn_enter.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btn_enter.setText("INGRESAR");
        btn_enter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_enter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_enterActionPerformed(evt);
            }
        });
        jPanel1.add(btn_enter, new org.netbeans.lib.awtextra.AbsoluteConstraints(209, 315, -1, 46));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 0, 490, 420));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Captura de pantalla 2025-11-26 113308.png"))); // NOI18N
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, 420));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, 420));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_enterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_enterActionPerformed
    String usuario = txt_userName.getText().trim();
    String clave = String.valueOf(txt_pasword.getPassword());
    
    if (sistemaPrincipal == null) {
        JOptionPane.showMessageDialog(this,
                "Error: No se pudo conectar con el sistema principal",
                "Error del Sistema",
                JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    if (usuario.isEmpty() || clave.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Por favor, complete todos los campos",
                "Campos Vacios",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    if ("admin".equalsIgnoreCase(tipoUsuario)) {
        if (usuario.equals("admin") && clave.equals("1234")) {

            sistemaPrincipal.cargarPanelAdmin();
            this.dispose();
        } else {
          
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos",
                    "Error de Autenticacion",
                    JOptionPane.ERROR_MESSAGE);
        }
        return;
    }
    

    if ("jefe".equalsIgnoreCase(tipoUsuario)) {
        if (usuario.equals("jefe") && clave.equals("1234")) {
  
            sistemaPrincipal.cargarPanelJefe();
            this.dispose();
        } else {
   
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos",
                    "Error de Autenticacion",
                    JOptionPane.ERROR_MESSAGE);
        }
        return;
    }
  
    JOptionPane.showMessageDialog(this,
            "Tipo de usuario no reconocido: " + tipoUsuario,
            "Error del Sistema",
            JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_btn_enterActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new LoginView().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btn_enter;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    public javax.swing.JPasswordField txt_pasword;
    public javax.swing.JTextField txt_userName;
    // End of variables declaration//GEN-END:variables
}


import dbc.connect;
import java.awt.Color;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.mindrot.jbcrypt.BCrypt;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author kasso
 */
public class manageuser extends javax.swing.JFrame {

    private int appuser = 0;

    public manageuser() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private boolean validateFields(String formtype) {
        if (formtype.equals("edit") && (txtname.getText().equals("") || txtmob.getText().equals("") || txtemail.getText().equals("") || txtadd.getText().equals(""))) {
            return true; // Should return true if fields are empty
        } else if (formtype.equals("new") && (txtname.getText().equals("") || txtmob.getText().equals("") || txtemail.getText().equals("") || txtadd.getText().equals("") || pas.getText().equals(""))) {
            return true; // Should return true if fields are empty
        } else {
            return false;
        }
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtname = new javax.swing.JTextField();
        txtmob = new javax.swing.JTextField();
        txtemail = new javax.swing.JTextField();
        txtadd = new javax.swing.JTextField();
        combo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        btnsave = new javax.swing.JButton();
        btnupd = new javax.swing.JButton();
        btnrst = new javax.swing.JButton();
        btnclose = new javax.swing.JButton();
        pas = new javax.swing.JPasswordField();
        jLabel9 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Schadow BT", 1, 36)); // NOI18N
        jLabel1.setText("Manage User");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 6, -1, -1));

        jTable1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Mobile Number", "Email", "Address", "Role", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 68, 440, 526));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Name");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Mobile Number");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Email");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Address");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Status");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 380, -1, -1));

        txtname.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtname, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 170, 180, -1));

        txtmob.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtmob, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, 180, -1));

        txtemail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 250, 180, -1));

        txtadd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtadd, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 290, 180, -1));

        combo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));
        getContentPane().add(combo, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 380, 110, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Password");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 330, -1, -1));

        btnsave.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnsave.setForeground(new java.awt.Color(0, 102, 51));
        btnsave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        btnsave.setText("Save");
        btnsave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnsave, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 440, -1, -1));

        btnupd.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnupd.setForeground(new java.awt.Color(0, 102, 51));
        btnupd.setText("Update");
        btnupd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnupdActionPerformed(evt);
            }
        });
        getContentPane().add(btnupd, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 440, -1, -1));

        btnrst.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnrst.setForeground(new java.awt.Color(0, 102, 51));
        btnrst.setText("Reset");
        btnrst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrstActionPerformed(evt);
            }
        });
        getContentPane().add(btnrst, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 440, -1, -1));

        btnclose.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnclose.setForeground(new java.awt.Color(255, 51, 51));
        btnclose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        btnclose.setText("Close");
        btnclose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncloseActionPerformed(evt);
            }
        });
        getContentPane().add(btnclose, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 440, -1, -1));

        pas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        pas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasActionPerformed(evt);
            }
        });
        getContentPane().add(pas, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 330, 180, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Role");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 380, -1, -1));

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "User", "Admin" }));
        jComboBox1.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                jComboBox1ComponentAdded(evt);
            }
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                jComboBox1ComponentRemoved(evt);
            }
        });
        getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 380, 110, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/All_page_Background.png"))); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        DefaultTableModel df = (DefaultTableModel) jTable1.getModel();
        try {
            Connection con = connect.dbConnect();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from user");
            while (rs.next()) {
                df.addRow(new Object[]{rs.getString("u_id"), rs.getString("name"), rs.getString("pnumber"), rs.getString("email"), rs.getString("address"), rs.getString("userrole"), rs.getString("status")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        btnupd.setEnabled(false);
    }//GEN-LAST:event_formComponentShown

    private void btnsaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsaveActionPerformed
        // TODO add your handling code here:
        String role = (String) jComboBox1.getSelectedItem();
        String name = txtname.getText();
        String mobilenum = txtmob.getText();
        String email = txtemail.getText();
        String password = pas.getText();
        String address = txtadd.getText();
        String status = (String) combo.getSelectedItem();

        if (validateFields("new")) {
            JOptionPane.showMessageDialog(null, "All fields are required!");
            return;
        }

        // Role validation
        if (!role.equals("Admin") && !role.equals("Manager") && !role.equals("User")) {
            JOptionPane.showMessageDialog(null, "Invalid role selected!");
            return;
        }

        try {
            Connection con = connect.dbConnect();

            // Check for duplicate email
            String query = "SELECT COUNT(*) FROM user WHERE email = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "This email is already registered!");
                return;
            }
            // Insert user into the database
            String hashedPassword = hashPassword(password);
            pst = con.prepareStatement("INSERT INTO user (userrole, name, pnumber, email, password, address, status) VALUES (?, ?, ?, ?, ?, ?, ?)");
            pst.setString(1, role);
            pst.setString(2, name);
            pst.setString(3, mobilenum);
            pst.setString(4, email);
            pst.setString(5, hashedPassword);
            pst.setString(6, address);
            pst.setString(7, status);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "User added successfully!");
            setVisible(false);
            new manageuser().setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnsaveActionPerformed

    private void pasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pasActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int index = jTable1.getSelectedRow();
        TableModel model = jTable1.getModel();

        if (index != -1) {
            appuser = Integer.parseInt(model.getValueAt(index, 0).toString()); // Set user ID
            txtname.setText(model.getValueAt(index, 1).toString());
            txtmob.setText(model.getValueAt(index, 2).toString());
            txtemail.setText(model.getValueAt(index, 3).toString());
            txtadd.setText(model.getValueAt(index, 4).toString());
            jComboBox1.setSelectedItem(model.getValueAt(index, 5).toString());
            combo.setSelectedItem(model.getValueAt(index, 6).toString());
            pas.setEditable(false); // Disable password editing
            pas.setBackground(Color.DARK_GRAY);
            btnsave.setEnabled(false);
            btnupd.setEnabled(true); // Enable update button
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnupdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnupdActionPerformed
        // TODO add your handling code here:
        String name = txtname.getText();
        String mobilenum = txtmob.getText();
        String email = txtemail.getText();
        String address = txtadd.getText();
        String role = (String) jComboBox1.getSelectedItem();
        String status = (String) combo.getSelectedItem();

        if (validateFields("edit")) {
            JOptionPane.showMessageDialog(null, "All fields are required!");
            return;
        }

        // Check if the user ID (appuser) is set
        if (appuser == 0) {
            JOptionPane.showMessageDialog(null, "No user selected for update!");
            return;
        }

        try {
            // Update the database
            Connection con = connect.dbConnect();
            PreparedStatement pst = con.prepareStatement(
                    "UPDATE user SET userrole=?, name=?, pnumber=?, email=?, address=?, status=? WHERE u_id=?"
            );
            pst.setString(1, role);
            pst.setString(2, name);
            pst.setString(3, mobilenum);
            pst.setString(4, email);
            pst.setString(5, address);
            pst.setString(6, status);
            pst.setInt(7, appuser);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "User updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update user. Please try again.");
            }

            // Refresh the UI
            setVisible(false);
            new manageuser().setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnupdActionPerformed

    private void btnrstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrstActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new manageuser().setVisible(true);
    }//GEN-LAST:event_btnrstActionPerformed

    private void btncloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncloseActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_btncloseActionPerformed

    private void jComboBox1ComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_jComboBox1ComponentAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ComponentAdded

    private void jComboBox1ComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_jComboBox1ComponentRemoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ComponentRemoved

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(manageuser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(manageuser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(manageuser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(manageuser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new manageuser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnclose;
    private javax.swing.JButton btnrst;
    private javax.swing.JButton btnsave;
    private javax.swing.JButton btnupd;
    private javax.swing.JComboBox<String> combo;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPasswordField pas;
    private javax.swing.JTextField txtadd;
    private javax.swing.JTextField txtemail;
    private javax.swing.JTextField txtmob;
    private javax.swing.JTextField txtname;
    // End of variables declaration//GEN-END:variables
}

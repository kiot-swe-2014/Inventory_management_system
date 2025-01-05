
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dbc.connect;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author kasso
 */
public class viewOrder extends javax.swing.JFrame {

    /**
     * Creates new form viewOrder
     */
    public viewOrder() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void fetchOrders(String statusFilter, int customerId) {
        try {
            DefaultTableModel orderModel = (DefaultTableModel) ordersTable.getModel();
            orderModel.setRowCount(0); // Clear the table

            Connection con = connect.dbConnect();
            String query = "SELECT * FROM orderdetails WHERE cus_id = ?";

            if (!statusFilter.equals("All")) {
                query += " AND status = ?";
            }

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, customerId);

            if (!statusFilter.equals("All")) {
                pst.setString(2, statusFilter);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                orderModel.addRow(new Object[]{
                    rs.getInt("order_id"),
                    rs.getDate("order_date"),
                    rs.getDouble("totpaid"),
                    rs.getString("status"),
                    rs.getDate("finalized_date") != null ? rs.getDate("finalized_date").toString() : "N/A"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching orders: " + e.getMessage());
        }
    }

    private void finalizeOrder() {
        try {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select an order to finalize.");
                return;
            }

            String status = (String) ordersTable.getValueAt(selectedRow, 3);
            if (!status.equals("Pending")) {
                JOptionPane.showMessageDialog(null, "Only pending orders can be finalized.");
                return;
            }

            int orderId = (int) ordersTable.getValueAt(selectedRow, 0);

            Connection con = connect.dbConnect();

            // Update order status
            PreparedStatement updateOrder = con.prepareStatement("UPDATE orderdetails SET status = 'Completed', finalized_date = ? WHERE order_id = ?");
            updateOrder.setString(1, java.time.LocalDate.now().toString());
            updateOrder.setInt(2, orderId);
            updateOrder.executeUpdate();

            JOptionPane.showMessageDialog(null, "Order finalized successfully!");
            int selectedCustomer = customerTable.getSelectedRow();
            if (selectedCustomer != -1) {
                int customerId = (int) customerTable.getValueAt(selectedCustomer, 0);
                fetchOrders((String) statusFilterDropdown.getSelectedItem(), customerId);
            }

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error finalizing order: " + e.getMessage());
        }
    }

    private void generateReport() {
        try {
            Connection con = connect.dbConnect();
            PreparedStatement pst = con.prepareStatement(
                    "SELECT c.name AS customer_name, c.email, o.unique_id, o.order_date, o.totpaid "
                    + "FROM orderdetails o "
                    + "INNER JOIN customer c ON o.cus_id = c.cus_id WHERE o.status = 'Completed'"
            );
            ResultSet rs = pst.executeQuery();

            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdir();
            }

            File outputFile = new File(reportsDir, "Completed_Orders_Report.pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();

            // Add Title
            document.add(new Paragraph("                                                               Completed Orders Report\n"));
            document.add(new Paragraph("****************************************************************************************************************************************"));

            // Create Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            // Add Table Headers
            table.addCell(createHeaderCell("Customer Name"));
            table.addCell(createHeaderCell("Email"));
            table.addCell(createHeaderCell("Order ID"));
            table.addCell(createHeaderCell("Order Date"));
            table.addCell(createHeaderCell("Total Paid"));

            // Populate Table Data
            while (rs.next()) {
                table.addCell(rs.getString("customer_name"));
                table.addCell(rs.getString("email"));
                table.addCell(rs.getString("unique_id"));
                table.addCell(rs.getDate("order_date").toString());
                table.addCell(String.valueOf(rs.getDouble("totpaid")));
            }

            document.add(table);
            document.add(new Paragraph("****************************************************************************************************************************************"));
            document.add(new Paragraph("Generated By Inventory Management System"));
            document.close();

            JOptionPane.showMessageDialog(null, "Report generated successfully! Check " + outputFile.getAbsolutePath());

        } catch (DocumentException | FileNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error generating report: " + e.getMessage());
        }
    }

    private PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBackgroundColor(new BaseColor(255, 204, 51));
        cell.setBorderWidth(1);
        return cell;
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
        customerTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        ordersTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        statusFilterDropdown = new javax.swing.JComboBox<>();
        generateReportButton = new javax.swing.JButton();
        cancelOrderButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("View Orders");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(317, 28, -1, -1));

        customerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Mobile Number", "Email"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                customerTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(customerTable);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 126, 340, 384));

        ordersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Date", "Total Paid", "Status", "Finalized Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        ordersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ordersTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(ordersTable);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(368, 126, 470, 384));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Customer List");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(173, 94, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Order List");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 90, -1, -1));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 51, 51));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 530, -1, -1));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Finalize Order");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 530, -1, -1));

        statusFilterDropdown.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        statusFilterDropdown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Pending", "Completed" }));
        statusFilterDropdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusFilterDropdownActionPerformed(evt);
            }
        });
        getContentPane().add(statusFilterDropdown, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 530, -1, -1));

        generateReportButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        generateReportButton.setText("Generate Report");
        generateReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateReportButtonActionPerformed(evt);
            }
        });
        getContentPane().add(generateReportButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 530, -1, -1));

        cancelOrderButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelOrderButton.setText("Cancel Order");
        cancelOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelOrderButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cancelOrderButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 530, -1, -1));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/All_page_Background.png"))); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void customerTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_customerTableMouseClicked
        // TODO add your handling code here:
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int customerId = (int) customerTable.getValueAt(selectedRow, 0);
            fetchOrders((String) statusFilterDropdown.getSelectedItem(), customerId);
        }
    }//GEN-LAST:event_customerTableMouseClicked

    private void ordersTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ordersTableMouseClicked

    }//GEN-LAST:event_ordersTableMouseClicked

    private void statusFilterDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusFilterDropdownActionPerformed
        // TODO add your handling code here:
        int selectedCustomer = customerTable.getSelectedRow();
        if (selectedCustomer != -1) {
            int customerId = (int) customerTable.getValueAt(selectedCustomer, 0);
            fetchOrders((String) statusFilterDropdown.getSelectedItem(), customerId);
        }
    }//GEN-LAST:event_statusFilterDropdownActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        finalizeOrder();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void generateReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateReportButtonActionPerformed
        // TODO add your handling code here:
        generateReport();
    }//GEN-LAST:event_generateReportButtonActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        try {
            DefaultTableModel customerModel = (DefaultTableModel) customerTable.getModel();
            customerModel.setRowCount(0); // Clear existing rows

            Connection con = connect.dbConnect();
            PreparedStatement pst = con.prepareStatement("SELECT cus_id, name, pnumber, email FROM customer");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                customerModel.addRow(new Object[]{
                    rs.getInt("cus_id"),
                    rs.getString("name"),
                    rs.getString("pnumber"),
                    rs.getString("email")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching customers: " + e.getMessage());
        }
    }//GEN-LAST:event_formComponentShown

    private void cancelOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelOrderButtonActionPerformed
        // TODO add your handling code here:
        try {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select an order to cancel.");
                return;
            }

            String status = (String) ordersTable.getValueAt(selectedRow, 3);
            if (!status.equals("Pending")) {
                JOptionPane.showMessageDialog(null, "Only pending orders can be canceled.");
                return;
            }

            int orderId = (int) ordersTable.getValueAt(selectedRow, 0);

            Connection con = connect.dbConnect();

            // Update order status to Cancelled
            PreparedStatement updateOrder = con.prepareStatement("UPDATE orderdetails SET status = 'Cancelled' WHERE order_id = ?");
            updateOrder.setInt(1, orderId);
            updateOrder.executeUpdate();

            JOptionPane.showMessageDialog(null, "Order canceled successfully!");
            int selectedCustomer = customerTable.getSelectedRow();
            if (selectedCustomer != -1) {
                int customerId = (int) customerTable.getValueAt(selectedCustomer, 0);
                fetchOrders((String) statusFilterDropdown.getSelectedItem(), customerId);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error canceling order: " + e.getMessage());
        }
    }//GEN-LAST:event_cancelOrderButtonActionPerformed

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
            java.util.logging.Logger.getLogger(viewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(viewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(viewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(viewOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new viewOrder().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelOrderButton;
    private javax.swing.JTable customerTable;
    private javax.swing.JButton generateReportButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable ordersTable;
    private javax.swing.JComboBox<String> statusFilterDropdown;
    // End of variables declaration//GEN-END:variables
}

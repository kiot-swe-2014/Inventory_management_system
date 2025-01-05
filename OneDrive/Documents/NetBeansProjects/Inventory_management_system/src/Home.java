
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author kasso
 */
public class Home extends javax.swing.JFrame {

    /**
     * Creates new form Home
     */
    public Home() {
        initComponents();
    }

    public Home(String role) {
        initComponents();
        setLocationRelativeTo(null);
        configureUIBasedOnRole(role);
        addPieChart();
        addBarChart();
        loadTopSpendingCustomers("");
    }

    private void addPieChart() {
        try {
            Connection con = dbc.connect.dbConnect();
            DefaultPieDataset dataset = new DefaultPieDataset();

            // Fetch order distribution by status
            PreparedStatement statusStmt = con.prepareStatement(
                    "SELECT status, COUNT(*) AS count FROM orderdetails GROUP BY status"
            );
            ResultSet rs = statusStmt.executeQuery();

            while (rs.next()) {
                dataset.setValue(rs.getString("status"), Integer.valueOf(rs.getInt("count")));
            }
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Order Status Distribution",
                    dataset,
                    true,
                    true,
                    false
            );
            ChartPanel chartPanel = new ChartPanel(pieChart);
            jPanel1.removeAll();
            jPanel1.add(chartPanel, BorderLayout.CENTER);
            jPanel1.validate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error creating pie chart: " + e.getMessage());
        }
    }

    private void addBarChart() {
        try {
            Connection con = dbc.connect.dbConnect();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Fetch revenue by month
            PreparedStatement revenueStmt = con.prepareStatement(
                    "SELECT MONTH(order_date) AS month, SUM(totpaid) AS revenue "
                    + "FROM orderdetails WHERE status = 'Completed' "
                    + "GROUP BY MONTH(order_date)"
            );
            ResultSet rs = revenueStmt.executeQuery();

            while (rs.next()) {
                String month = "Month " + rs.getInt("month");
                double revenue = rs.getDouble("revenue");
                dataset.addValue(revenue, "Revenue", month);
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Monthly Revenue Trend",
                    "Month",
                    "Revenue ($)",
                    dataset
            );

            ChartPanel chartPanel = new ChartPanel(barChart);
            jPanel2.removeAll();
            jPanel2.add(chartPanel, BorderLayout.CENTER);
            jPanel2.validate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error creating bar chart: " + e.getMessage());
        }
    }

    private void loadTopSpendingCustomers(String filter) {
        try {
            Connection con = dbc.connect.dbConnect();
            String query = "SELECT c.name, SUM(o.totpaid) AS total_spent "
                    + "FROM orderdetails o "
                    + "JOIN customer c ON o.cus_id = c.cus_id "
                    + "WHERE c.name LIKE ? "
                    + "GROUP BY c.name "
                    + "ORDER BY total_spent DESC "
                    + "LIMIT 10";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, filter.isEmpty() ? "%" : "%" + filter + "%");
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = new DefaultTableModel(new String[]{"Customer Name", "Total Spent"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("name"), rs.getDouble("total_spent")});
            }

            tblTopCustomers.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading top customers: " + e.getMessage());
        }
    }

    private void configureUIBasedOnRole(String role) {
        btnuser.setVisible(role.equals("Admin")); // Only Admin can manage users
    }

    private void refreshMetrics() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Connection con = dbc.connect.dbConnect();

                // Fetch Total Orders
                PreparedStatement totalOrdersStmt = con.prepareStatement("SELECT COUNT(*) AS total FROM orderdetails");
                ResultSet rsTotalOrders = totalOrdersStmt.executeQuery();
                if (rsTotalOrders.next()) {
                    lblTotalOrders.setText("Total Orders: " + rsTotalOrders.getInt("total"));
                }

                // Fetch Completed Orders
                PreparedStatement completedOrdersStmt = con.prepareStatement("SELECT COUNT(*) AS completed FROM orderdetails WHERE status = 'Completed'");
                ResultSet rsCompletedOrders = completedOrdersStmt.executeQuery();
                if (rsCompletedOrders.next()) {
                    lblCompletedOrders.setText("Completed Orders: " + rsCompletedOrders.getInt("completed"));
                }

                // Fetch Pending Orders
                PreparedStatement pendingOrdersStmt = con.prepareStatement("SELECT COUNT(*) AS pending FROM orderdetails WHERE status = 'Pending'");
                ResultSet rsPendingOrders = pendingOrdersStmt.executeQuery();
                if (rsPendingOrders.next()) {
                    lblPendingOrders.setText("Pending Orders: " + rsPendingOrders.getInt("pending"));
                }

                // Fetch Total Revenue
                PreparedStatement totalRevenueStmt = con.prepareStatement("SELECT SUM(totpaid) AS revenue FROM orderdetails WHERE status = 'Completed'");
                ResultSet rsTotalRevenue = totalRevenueStmt.executeQuery();
                if (rsTotalRevenue.next()) {
                    lblTotalRevenue.setText("Total Revenue: $" + rsTotalRevenue.getDouble("revenue"));
                }

                return null;
            }

            @Override
            protected void done() {
                // Update charts after metrics are refreshed
                addPieChart();
                addBarChart();
            }
        };
        worker.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnuser = new javax.swing.JButton();
        btncat = new javax.swing.JButton();
        btnpro = new javax.swing.JButton();
        btncus = new javax.swing.JButton();
        btnor = new javax.swing.JButton();
        btnvw = new javax.swing.JButton();
        btnlo = new javax.swing.JButton();
        lblTotalRevenue = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lblCompletedOrders = new javax.swing.JLabel();
        lblPendingOrders = new javax.swing.JLabel();
        lblTotalOrders = new javax.swing.JLabel();
        tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        pnlFilter = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCustomerFilter = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        tblTopCustomers = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnuser.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnuser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Users.png"))); // NOI18N
        btnuser.setText("User");
        btnuser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnuserActionPerformed(evt);
            }
        });
        getContentPane().add(btnuser, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, -1, -1));

        btncat.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btncat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/category.png"))); // NOI18N
        btncat.setText("Category");
        btncat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncatActionPerformed(evt);
            }
        });
        getContentPane().add(btncat, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, -1, -1));

        btnpro.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnpro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/product.png"))); // NOI18N
        btnpro.setText("Product");
        btnpro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnproActionPerformed(evt);
            }
        });
        getContentPane().add(btnpro, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, -1, -1));

        btncus.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btncus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/customers.png"))); // NOI18N
        btncus.setText("Customer");
        btncus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncusActionPerformed(evt);
            }
        });
        getContentPane().add(btncus, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 20, -1, -1));

        btnor.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Orders.png"))); // NOI18N
        btnor.setText("Order");
        btnor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnorActionPerformed(evt);
            }
        });
        getContentPane().add(btnor, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 20, -1, -1));

        btnvw.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnvw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/View-orders.png"))); // NOI18N
        btnvw.setText("View order");
        btnvw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnvwActionPerformed(evt);
            }
        });
        getContentPane().add(btnvw, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 20, -1, -1));

        btnlo.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        btnlo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Exit.png"))); // NOI18N
        btnlo.setText("Logout");
        btnlo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnloActionPerformed(evt);
            }
        });
        getContentPane().add(btnlo, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 20, -1, -1));

        lblTotalRevenue.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTotalRevenue.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalRevenue.setText("Total Revenue: Loading...");
        getContentPane().add(lblTotalRevenue, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 110, -1, -1));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Refresh Metrics");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 110, -1, -1));

        lblCompletedOrders.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblCompletedOrders.setForeground(new java.awt.Color(255, 255, 255));
        lblCompletedOrders.setText("Completed Orders: Loading...");
        getContentPane().add(lblCompletedOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 110, -1, -1));

        lblPendingOrders.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblPendingOrders.setForeground(new java.awt.Color(255, 255, 255));
        lblPendingOrders.setText("Pending Orders: Loading...");
        getContentPane().add(lblPendingOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 110, -1, -1));

        lblTotalOrders.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTotalOrders.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalOrders.setText("Total Orders: Loading...");
        getContentPane().add(lblTotalOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, -1, -1));

        tabbedPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(204, 255, 255))); // NOI18N
        tabbedPane.addTab("Piechart", jPanel1);
        tabbedPane.addTab("Barchart", jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());

        pnlFilter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Filter by Customer Name:");
        pnlFilter.add(jLabel2);

        txtCustomerFilter.setColumns(20);
        txtCustomerFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtCustomerFilter.setToolTipText("");
        pnlFilter.add(txtCustomerFilter);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setText("Search");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        pnlFilter.add(jButton2);

        jPanel3.add(pnlFilter, java.awt.BorderLayout.NORTH);

        tblTopCustomers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Customer Name", "Total Spent"
            }
        ));
        scrollPane.setViewportView(tblTopCustomers);

        jPanel3.add(scrollPane, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Top Spending Customers", jPanel3);

        getContentPane().add(tabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 1210, 530));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/home_background.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnuserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnuserActionPerformed
        // TODO add your handling code here:
        new manageuser().setVisible(true);
    }//GEN-LAST:event_btnuserActionPerformed

    private void btnloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnloActionPerformed
        // TODO add your handling code here:
        int a = JOptionPane.showConfirmDialog(null, "Do you want to Logout?", "Select", JOptionPane.YES_NO_OPTION);
        if (a == JOptionPane.YES_OPTION) {
            setVisible(false);
            new Login().setVisible(true);
        }
    }//GEN-LAST:event_btnloActionPerformed

    private void btncatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncatActionPerformed
        // TODO add your handling code here:
        new manageCategory().setVisible(true);
    }//GEN-LAST:event_btncatActionPerformed

    private void btnproActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnproActionPerformed
        // TODO add your handling code here:
        new manageProduct().setVisible(true);
    }//GEN-LAST:event_btnproActionPerformed

    private void btncusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncusActionPerformed
        // TODO add your handling code here:
        new manageCustomer().setVisible(true);
    }//GEN-LAST:event_btncusActionPerformed

    private void btnorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnorActionPerformed
        // TODO add your handling code here:
        new manageOrder().setVisible(true);
    }//GEN-LAST:event_btnorActionPerformed

    private void btnvwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnvwActionPerformed
        // TODO add your handling code here:
        new viewOrder().setVisible(true);
    }//GEN-LAST:event_btnvwActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        refreshMetrics();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        loadTopSpendingCustomers(txtCustomerFilter.getText());
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btncat;
    private javax.swing.JButton btncus;
    private javax.swing.JButton btnlo;
    private javax.swing.JButton btnor;
    private javax.swing.JButton btnpro;
    private javax.swing.JButton btnuser;
    private javax.swing.JButton btnvw;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblCompletedOrders;
    private javax.swing.JLabel lblPendingOrders;
    private javax.swing.JLabel lblTotalOrders;
    private javax.swing.JLabel lblTotalRevenue;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tblTopCustomers;
    private javax.swing.JTextField txtCustomerFilter;
    // End of variables declaration//GEN-END:variables
}


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dbc.connect;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author kasso
 */
public class manageProduct extends javax.swing.JFrame {

    private int apppro = 0;
    private int totalquant = 0;
    private static final int LOW_STOCK_THRESHOLD = 10;

    /**
     * Creates new form manageProduct
     */
    public manageProduct() {
        initComponents();
        setLocationRelativeTo(null);
        getAllCategory();
        fetchProducts();
        checkLowStock();
    }

    private void getAllCategory() {
        try {
            Connection con = connect.dbConnect();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM category");
            ResultSet rs = pst.executeQuery();
            categoryFilter.addItem("All Categories");
            comboc.removeAllItems();
            while (rs.next()) {
                categoryFilter.addItem(rs.getString("cat_id") + "-" + rs.getString("name"));
                comboc.addItem(rs.getString("cat_id") + "-" + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void fetchProducts() {
        DefaultTableModel df = (DefaultTableModel) productTable.getModel();
        df.setRowCount(0); // Clear table for new data

        String searchQuery = searchProduct.getText().trim();
        String categoryFilterValue = (String) categoryFilter.getSelectedItem();
        String minPriceValue = minPrice.getText().trim();
        String maxPriceValue = maxPrice.getText().trim();

        // Build the SQL query with dynamic filters
        StringBuilder query = new StringBuilder(
                "SELECT p.pr_id, p.name, p.quantity, p.price, p.description, c.name AS category_name "
                + "FROM product p "
                + "INNER JOIN category c ON p.cat_id = c.cat_id "
                + "WHERE 1=1"
        );

        if (!searchQuery.isEmpty()) {
            query.append(" AND (p.name LIKE ? OR c.name LIKE ?)");
        }

        String[] categoryId = null;
        if (categoryFilterValue != null && !categoryFilterValue.equals("All Categories")) {
            categoryId = categoryFilterValue.split("-");
            query.append(" AND p.cat_id = ?");
        }

        if (!minPriceValue.isEmpty()) {
            query.append(" AND p.price >= ?");
        }

        if (!maxPriceValue.isEmpty()) {
            query.append(" AND p.price <= ?");
        }

        query.append(" ORDER BY p.name ASC"); // Sort products alphabetically by name

        try ( Connection con = connect.dbConnect()) {
            PreparedStatement pst = con.prepareStatement(query.toString());

            // Populate dynamic query parameters
            int paramIndex = 1;
            if (!searchQuery.isEmpty()) {
                pst.setString(paramIndex++, "%" + searchQuery + "%");
                pst.setString(paramIndex++, "%" + searchQuery + "%");
            }
            if (categoryId != null) {
                pst.setInt(paramIndex++, Integer.parseInt(categoryId[0]));
            }
            if (!minPriceValue.isEmpty()) {
                pst.setDouble(paramIndex++, Double.parseDouble(minPriceValue));
            }
            if (!maxPriceValue.isEmpty()) {
                pst.setDouble(paramIndex++, Double.parseDouble(maxPriceValue));
            }

            // Execute query and populate the table
            ResultSet rs = pst.executeQuery();
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                df.addRow(new Object[]{
                    rs.getInt("pr_id"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getString("description"),
                    rs.getString("category_name")
                });
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(null, "No products match the selected filters.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid numeric value entered. Please check the price fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while fetching products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateFields(String formtype) {
        if (formtype.equals("edit") && !txtname.getText().equals("") && !txtds.getText().equals("") && !txtpr.getText().equals("")) {
            return false;
        } else if (formtype.equals("new") && !txtname.getText().equals("") && !txtquant.getText().equals("") && !txtds.getText().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void checkLowStock() {
        try {
            Connection con = connect.dbConnect();
            PreparedStatement pst = con.prepareStatement("SELECT name, quantity FROM product WHERE quantity < ?");
            pst.setInt(1, LOW_STOCK_THRESHOLD);
            ResultSet rs = pst.executeQuery();

            StringBuilder lowStockItems = new StringBuilder();
            while (rs.next()) {
                lowStockItems.append("Product: ").append(rs.getString("name"))
                        .append(", Quantity: ").append(rs.getInt("quantity"))
                        .append("\n");
            }

            if (lowStockItems.length() > 0) {
                JOptionPane.showMessageDialog(null, "Low Stock Alert:\n" + lowStockItems.toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error checking low stock: " + e.getMessage());
        }
    }

    private void importCSV() {
        try {
            File file = new File("import.csv");
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "CSV file not found!");
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            Connection con = connect.dbConnect();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                PreparedStatement pst = con.prepareStatement("INSERT INTO product (name, quantity, price, description, cat_id) VALUES (?, ?, ?, ?, ?)");
                pst.setString(1, values[0]);
                pst.setInt(2, Integer.parseInt(values[1]));
                pst.setDouble(3, Double.parseDouble(values[2]));
                pst.setString(4, values[3]);
                pst.setInt(5, Integer.parseInt(values[4]));
                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "CSV data imported successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error importing CSV: " + e.getMessage());
        }
    }

    private void exportCSV() {
        try {
            FileWriter fw = new FileWriter("export.csv");
            PrintWriter pw = new PrintWriter(fw);
            DefaultTableModel df = (DefaultTableModel) productTable.getModel();

            for (int i = 0; i < df.getRowCount(); i++) {
                for (int j = 1; j < df.getColumnCount(); j++) { // Skip ID column
                    pw.print(df.getValueAt(i, j).toString());
                    if (j < df.getColumnCount() - 1) {
                        pw.print(",");
                    }
                }
                pw.println();
            }

            pw.close();
            fw.close();
            JOptionPane.showMessageDialog(null, "CSV exported successfully!");
        } catch (HeadlessException | IOException e) {
            JOptionPane.showMessageDialog(null, "Error exporting CSV: " + e.getMessage());
        }
    }

    private void generateReport() throws FileNotFoundException, DocumentException {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Product_Report.pdf"));
            document.open();

            // Add title
            document.add(new Paragraph("Product Inventory Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("\n"));

            // Create table
            PdfPTable table = new PdfPTable(5); // 5 columns: Name, Quantity, Price, Description, Category
            table.setWidthPercentage(100);

            // Add table headers
            table.addCell("Name");
            table.addCell("Quantity");
            table.addCell("Price");
            table.addCell("Description");
            table.addCell("Category");

            DefaultTableModel df = (DefaultTableModel) productTable.getModel();
            for (int i = 0; i < df.getRowCount(); i++) {
                table.addCell(df.getValueAt(i, 1).toString()); // Name
                table.addCell(df.getValueAt(i, 2).toString()); // Quantity
                table.addCell(df.getValueAt(i, 3).toString()); // Price
                table.addCell(df.getValueAt(i, 4).toString()); // Description
                table.addCell(df.getValueAt(i, 5).toString()); // Category
            }

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(null, "Report generated successfully! Check Product_Report.pdf.");
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Error generating report: " + e.getMessage());
        }
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
        productTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtname = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtds = new javax.swing.JTextField();
        txtpr = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        btnrst = new javax.swing.JButton();
        btnup = new javax.swing.JButton();
        btnsave = new javax.swing.JButton();
        comboc = new javax.swing.JComboBox<>();
        lblquant = new javax.swing.JLabel();
        txtquant = new javax.swing.JTextField();
        searchProduct = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        categoryFilter = new javax.swing.JComboBox<>();
        minPrice = new javax.swing.JTextField();
        maxPrice = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        reportButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("Manage Product");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(307, 15, -1, -1));

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Quantity", "Price", "Description", "Category"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(productTable);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 70, 460, 400));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Name");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 160, -1, -1));

        txtname.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtname, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 157, 222, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Description");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 269, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Price");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 313, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Categrory");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 362, -1, -1));

        txtds.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtds, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 266, 222, -1));

        txtpr.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtpr, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 310, 222, -1));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close.png"))); // NOI18N
        jButton4.setText("Close");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(297, 429, -1, -1));

        btnrst.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnrst.setText("Reset");
        btnrst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrstActionPerformed(evt);
            }
        });
        getContentPane().add(btnrst, new org.netbeans.lib.awtextra.AbsoluteConstraints(213, 429, -1, -1));

        btnup.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnup.setText("Update");
        btnup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnupActionPerformed(evt);
            }
        });
        getContentPane().add(btnup, new org.netbeans.lib.awtextra.AbsoluteConstraints(124, 429, -1, -1));

        btnsave.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnsave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        btnsave.setText("Save");
        btnsave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsaveActionPerformed(evt);
            }
        });
        getContentPane().add(btnsave, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 429, -1, -1));

        comboc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(comboc, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 359, 222, -1));

        lblquant.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblquant.setText("Quantity");
        getContentPane().add(lblquant, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 214, -1, -1));

        txtquant.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        getContentPane().add(txtquant, new org.netbeans.lib.awtextra.AbsoluteConstraints(151, 211, 222, -1));

        searchProduct.setToolTipText("Search by name or category");
        getContentPane().add(searchProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 510, 220, 30));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 510, 90, 30));

        categoryFilter.setToolTipText("Filter by category");
        getContentPane().add(categoryFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 480, 110, -1));

        minPrice.setToolTipText("Min Price");
        getContentPane().add(minPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 480, 100, -1));

        maxPrice.setToolTipText("Max Price");
        getContentPane().add(maxPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 480, 100, -1));

        jLabel7.setText("Min Price");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 480, -1, 20));

        jLabel8.setText("Max Price");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 480, -1, 20));

        reportButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        reportButton.setText("Generate Report");
        reportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportButtonActionPerformed(evt);
            }
        });
        getContentPane().add(reportButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 480, 150, 30));

        importButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        importButton.setText("Import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });
        getContentPane().add(importButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 480, 90, 30));

        exportButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        getContentPane().add(exportButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 480, 90, 30));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/All_page_Background.png"))); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnrstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrstActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        new manageProduct().setVisible(true);
    }//GEN-LAST:event_btnrstActionPerformed

    private void btnupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnupActionPerformed
        // TODO add your handling code here:
        String name = txtname.getText();
        String quantity = txtquant.getText();
        String description = txtds.getText();
        String price = txtpr.getText();
        String category = (String) comboc.getSelectedItem();
        String categoryId[] = category.split("-", 0);
        if (validateFields("edit")) {
            JOptionPane.showMessageDialog(null, "All fields are required!");
        } else {
            try {
                if (!quantity.equals("")) {
                    totalquant = totalquant + Integer.parseInt(quantity);
                }
                Connection con = connect.dbConnect();
                PreparedStatement pst = con.prepareStatement("update product set name=?,quantity=?,price=?,description=?,cat_id=? where pr_id=?");
                pst.setString(1, name);
                pst.setInt(2, totalquant);
                pst.setString(3, price);
                pst.setString(4, description);
                pst.setInt(5, Integer.parseInt(categoryId[0]));
                pst.setInt(6, apppro);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Product Updated successfully!");
                setVisible(false);
                new manageProduct().setVisible(true);

            } catch (HeadlessException | NumberFormatException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
            btnsave.setEnabled(false);
            btnup.setEnabled(true);
        }
    }//GEN-LAST:event_btnupActionPerformed

    private void btnsaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsaveActionPerformed
        // TODO add your handling code here:
        String name = txtname.getText();
        String quantity = txtquant.getText();
        String description = txtds.getText();
        String price = txtpr.getText();
        String category = (String) comboc.getSelectedItem();
        String categoryId[] = category.split("-", 0);
        if (validateFields("new")) {
            JOptionPane.showMessageDialog(null, "All fields are required!");
        } else {
            try {
                Connection con = connect.dbConnect();
                PreparedStatement pst = con.prepareStatement("insert into product (name,quantity,price,description,cat_id) values(?,?,?,?,?)");
                pst.setString(1, name);
                pst.setString(2, quantity);
                pst.setString(3, price);
                pst.setString(4, description);
                pst.setInt(5, Integer.parseInt(categoryId[0]));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Product added successfully!");
                setVisible(false);
                new manageProduct().setVisible(true);

            } catch (HeadlessException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_btnsaveActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void productTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productTableMouseClicked
        // TODO add your handling code here:
        int selectedRow = productTable.getSelectedRow(); // Get selected row index

        // Validate row selection
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No row selected. Please select a valid product.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) productTable.getModel();

        try {
            // Fetch Product ID
            String productId = model.getValueAt(selectedRow, 0).toString();
            apppro = Integer.parseInt(productId);

            // Fetch Product Name
            String productName = model.getValueAt(selectedRow, 1).toString();
            txtname.setText(productName);

            // Fetch Quantity
            String quantity = model.getValueAt(selectedRow, 2).toString();
            totalquant = Integer.parseInt(quantity);
            lblquant.setText("Add quantity"); // Reset label

            // Fetch Price
            String price = model.getValueAt(selectedRow, 3).toString();
            txtpr.setText(price);

            // Fetch Description
            String description = model.getValueAt(selectedRow, 4).toString();
            txtds.setText(description);

            // Fetch Category
            String categoryName = model.getValueAt(selectedRow, 5).toString();
            comboc.addItem(categoryName);

            // Populate Category ComboBox
            Connection con = connect.dbConnect();
            PreparedStatement pst = con.prepareStatement("SELECT cat_id, name FROM category WHERE name != ?");
            pst.setString(1, categoryName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                comboc.addItem(rs.getString("cat_id") + "-" + rs.getString("name"));
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid numeric data in the table. Please check the selected row.", "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching category data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Update Button States
        btnsave.setEnabled(false); // Disable Save button
        btnup.setEnabled(true);
    }//GEN-LAST:event_productTableMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        fetchProducts();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void reportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportButtonActionPerformed
        try {
            // TODO add your handling code here:
            generateReport();
        } catch (FileNotFoundException | DocumentException ex) {
            Logger.getLogger(manageProduct.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_reportButtonActionPerformed

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
        // TODO add your handling code here:
        importCSV();
    }//GEN-LAST:event_importButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        // TODO add your handling code here:
        exportCSV();
    }//GEN-LAST:event_exportButtonActionPerformed

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
            java.util.logging.Logger.getLogger(manageProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(manageProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(manageProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(manageProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new manageProduct().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnrst;
    private javax.swing.JButton btnsave;
    private javax.swing.JButton btnup;
    private javax.swing.JComboBox<String> categoryFilter;
    private javax.swing.JComboBox<String> comboc;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton importButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblquant;
    private javax.swing.JTextField maxPrice;
    private javax.swing.JTextField minPrice;
    private javax.swing.JTable productTable;
    private javax.swing.JButton reportButton;
    private javax.swing.JTextField searchProduct;
    private javax.swing.JTextField txtds;
    private javax.swing.JTextField txtname;
    private javax.swing.JTextField txtpr;
    private javax.swing.JTextField txtquant;
    // End of variables declaration//GEN-END:variables
}

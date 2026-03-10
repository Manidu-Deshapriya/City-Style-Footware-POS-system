package com.citystyle.ui;

import com.citystyle.dao.*;
import com.citystyle.model.User;
import com.citystyle.model.Shoe;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * BaseDashboard serves as a parent class (Inheritance) for specific dashboards.
 * It encapsulates shared UI styles, color palettes, and DAO instances.
 */
public abstract class BaseDashboard extends JFrame {
    protected User currentUser;
    protected JPanel contentPanel;
    protected CardLayout cardLayout;

    // Shared DAO instances
    protected ShoeDAO shoeDAO = new ShoeDAO();
    protected ReturnDAO returnDAO = new ReturnDAO();
    protected UserDAO userDAO = new UserDAO();
    protected MetaDAO metaDAO = new MetaDAO();
    protected SaleDAO saleDAO = new SaleDAO();

    // Shared Color Palette & Fonts
    protected final Color SIDEBAR_COLOR = new Color(33, 47, 61); // Darker
    protected final Color ACCENT_COLOR = new Color(41, 128, 185); // Stronger Blue
    protected final Color BG_COLOR = new Color(240, 243, 244);
    protected final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    protected final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);

    // Dropdowns for refreshing
    protected JComboBox<String> cbInventoryCat;
    protected JComboBox<String> cbInventoryBrand;
    protected JComboBox<String> cbInventoryColor;
    protected JComboBox<String> cbPOSCat;
    protected JComboBox<String> cbPOSBrand;
    protected JComboBox<String> cbPOSColor;
    protected JTextField txtDiscount;
    protected JButton btnReqDiscount;
    protected JLabel lblDiscountStatus;

    // Sidebar State Management
    protected java.util.Map<String, JButton> sidebarButtons = new java.util.HashMap<>();
    protected String activeCard = "";

    // Discount Approval State
    protected DiscountRequestDAO drDAO = new DiscountRequestDAO();
    protected int currentDiscountRequestId = -1;
    protected boolean isDiscountApproved = false;
    protected double approvedDiscountValue = 0.0;

    public BaseDashboard(User user, String title) {
        this.currentUser = user;
        setTitle(title + " - " + user.getUsername());
        // Set default size for "Normal" (Restored) state
        setSize(1280, 720);
        setLocationRelativeTo(null);
        // Start maximized (Full Screen effect)
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    protected abstract JPanel createSidebar();

    protected JButton createSidebarButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(240, 50));
        btn.setBackground(SIDEBAR_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(MAIN_FONT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (cardName != null) {
            sidebarButtons.put(cardName, btn);
            btn.addActionListener(e -> {
                cardLayout.show(contentPanel, cardName);
                activeCard = cardName;
                // Update all buttons
                sidebarButtons.forEach((k, b) -> b.setBackground(SIDEBAR_COLOR));
                btn.setBackground(ACCENT_COLOR);
            });
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!activeCard.equals(cardName)) {
                    btn.setBackground(ACCENT_COLOR);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!activeCard.equals(cardName)) {
                    btn.setBackground(SIDEBAR_COLOR);
                }
            }
        });
        return btn;
    }

    protected JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        searchPanel.setPreferredSize(new Dimension(200, 0));

        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.insets = new Insets(5, 0, 2, 0);
        sgbc.gridx = 0;
        int vRow = 0;

        JTextField txtSearch = new JTextField(12);
        cbInventoryCat = new JComboBox<>(new String[] { "All Categories" });
        cbInventoryBrand = new JComboBox<>(new String[] { "All Brands" });
        cbInventoryColor = new JComboBox<>(new String[] { "All Colors" });
        JTextField txtMinSize = new JTextField(5);
        JTextField txtMaxSize = new JTextField(5);
        JTextField txtMinPrice = new JTextField(5);
        JTextField txtMaxPrice = new JTextField(5);
        JButton btnSearch = new JButton("Search Products");
        btnSearch.setBackground(ACCENT_COLOR);
        btnSearch.setForeground(Color.WHITE);
        JButton btnClear = new JButton("Clear All");

        refreshMetadataDropdowns(cbInventoryCat, cbInventoryBrand, cbInventoryColor, true);

        searchPanel.add(new JLabel("Model Name:"), gbcAt(0, vRow++, sgbc));
        searchPanel.add(txtSearch, gbcAt(0, vRow++, sgbc));
        searchPanel.add(new JLabel("Category:"), gbcAt(0, vRow++, sgbc));
        searchPanel.add(cbInventoryCat, gbcAt(0, vRow++, sgbc));
        searchPanel.add(new JLabel("Brand:"), gbcAt(0, vRow++, sgbc));
        searchPanel.add(cbInventoryBrand, gbcAt(0, vRow++, sgbc));
        searchPanel.add(new JLabel("Color:"), gbcAt(0, vRow++, sgbc));
        searchPanel.add(cbInventoryColor, gbcAt(0, vRow++, sgbc));

        searchPanel.add(new JLabel("Size (Model Search):"), gbcAt(0, vRow++, sgbc));
        searchPanel.add(txtMinSize, gbcAt(0, vRow++, sgbc));

        searchPanel.add(new JLabel("Price Range (Min - Max):"), gbcAt(0, vRow++, sgbc));
        JPanel priceP = new JPanel(new GridLayout(1, 2, 5, 0));
        priceP.setOpaque(false);
        priceP.add(txtMinPrice);
        priceP.add(txtMaxPrice);
        searchPanel.add(priceP, gbcAt(0, vRow++, sgbc));

        JSlider minPriceSlider = new JSlider(0, 90000, 0);
        minPriceSlider.setOpaque(false);
        minPriceSlider.addChangeListener(e -> txtMinPrice.setText(String.valueOf(minPriceSlider.getValue())));
        searchPanel.add(new JLabel("Min Price Slider:"), gbcAt(0, vRow++, sgbc));
        searchPanel.add(minPriceSlider, gbcAt(0, vRow++, sgbc));

        JSlider maxPriceSlider = new JSlider(0, 90000, 90000);
        maxPriceSlider.setOpaque(false);
        maxPriceSlider.addChangeListener(e -> txtMaxPrice.setText(String.valueOf(maxPriceSlider.getValue())));
        searchPanel.add(new JLabel("Max Price Slider:"), gbcAt(0, vRow++, sgbc));
        searchPanel.add(maxPriceSlider, gbcAt(0, vRow++, sgbc));

        sgbc.insets = new Insets(20, 0, 5, 0);
        searchPanel.add(btnSearch, gbcAt(0, vRow++, sgbc));
        sgbc.insets = new Insets(5, 0, 5, 0);
        searchPanel.add(btnClear, gbcAt(0, vRow++, sgbc));
        sgbc.weighty = 1.0;
        searchPanel.add(Box.createVerticalGlue(), gbcAt(0, vRow++, sgbc));

        setupValidation(txtSearch, new JTextField(), txtMinSize, txtMinPrice, txtMaxPrice, panel);
        panel.add(searchPanel, BorderLayout.WEST);

        String[] cols = { "ID", "Category", "Model", "Brand", "Color", "Size", "Supplier", "Base Price",
                "Current Price", "Stock" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    if (column == 1) { // Category Column
                        String category = (String) getModel().getValueAt(row, 1);
                        c.setBackground(getCategoryColor(category));
                    } else if (column == 2) { // Model Column
                        String modelName = (String) getModel().getValueAt(row, 2);
                        c.setBackground(getModelColor(modelName));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.setShowGrid(true); // Enable grid lines
        table.setGridColor(Color.LIGHT_GRAY);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        Runnable refreshAction = () -> {
            model.setRowCount(0);
            int minS = -1, maxS = -1;
            double minP = -1, maxP = -1;
            try {
                if (!txtMinSize.getText().isEmpty()) {
                    int s = Integer.parseInt(txtMinSize.getText());
                    minS = s;
                    maxS = s;
                }
                if (!txtMinPrice.getText().isEmpty())
                    minP = Double.parseDouble(txtMinPrice.getText());
                if (!txtMaxPrice.getText().isEmpty())
                    maxP = Double.parseDouble(txtMaxPrice.getText());
            } catch (Exception ex) {
            }

            List<Shoe> shoes = shoeDAO.searchByAll(txtSearch.getText(),
                    cbInventoryCat.getSelectedIndex() == 0 ? "All" : (String) cbInventoryCat.getSelectedItem(),
                    cbInventoryBrand.getSelectedIndex() == 0 ? "All" : (String) cbInventoryBrand.getSelectedItem(),
                    cbInventoryColor.getSelectedIndex() == 0 ? "All" : (String) cbInventoryColor.getSelectedItem(),
                    null, minS, maxS, minP, maxP);
            for (Shoe s : shoes) {
                String status = "";
                if (s.getStock() <= 0)
                    status = " (Out of Stock)";
                else if (s.getStock() < 5)
                    status = " (Low Stock)"; // Simple threshold check

                String basePriceDisplay = String.format("Rs. %.2f", s.getPrice());
                String currentPriceDisplay = String.format("Rs. %.2f", s.getPrice());

                // Fix: Show updated price if promotional price is different from base price
                if (s.getPromotionalPrice() > 0 && Math.abs(s.getPromotionalPrice() - s.getPrice()) > 0.01) {
                    String tag = s.getPromotionalPrice() < s.getPrice() ? " (PROMO)" : " (NEW)";
                    currentPriceDisplay = String.format("Rs. %.2f%s", s.getPromotionalPrice(), tag);
                }

                String sizeRange;
                if (s.getMinSize() == s.getMaxSize()) {
                    sizeRange = String.valueOf(s.getMinSize());
                } else {
                    sizeRange = s.getMinSize() + " - " + s.getMaxSize();
                }

                if (s.getSize() != null && !s.getSize().isEmpty() && !s.getSize().equals(sizeRange)) {
                    // If specific size string exists and is different (e.g. "L", "XL" or different
                    // format)
                    sizeRange = s.getSize();
                }

                model.addRow(new Object[] { s.getShoeId(), s.getCategoryName(), s.getModelName(), s.getBrandName(),
                        s.getColor(), sizeRange, s.getSupplierName(), basePriceDisplay,
                        currentPriceDisplay, s.getStock() + status });
            }
            // Sort by Category is handled by SQL now.
        };

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh All");
        JButton btnAdd = new JButton("Add New Shoe");
        JButton btnUpdate = new JButton("Update Selected");
        JButton btnDelete = new JButton("Delete");

        actions.add(btnRefresh);
        actions.add(btnAdd);
        actions.add(btnUpdate);
        if (currentUser.getRole().equals("Manager")) {
            actions.add(btnDelete);
            JButton btnBulkPrice = new JButton("Bulk Price Update");
            btnBulkPrice.addActionListener(e -> showBulkPriceDialog(refreshAction));
            actions.add(btnBulkPrice);
        }
        panel.add(actions, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> refreshAction.run());
        btnClear.addActionListener(e ->

        {
            txtSearch.setText("");
            cbInventoryCat.setSelectedIndex(0);
            cbInventoryBrand.setSelectedIndex(0);
            cbInventoryColor.setSelectedIndex(0);
            txtMinSize.setText("");
            txtMaxSize.setText("");
            txtMinPrice.setText("");
            txtMaxPrice.setText("");
            refreshAction.run();
        });
        btnRefresh.addActionListener(e -> refreshAction.run());
        btnAdd.addActionListener(e -> {
            showShoeDialog(null);
            // Clear filters so the new shoe is visible in the list
            txtSearch.setText("");
            cbInventoryCat.setSelectedIndex(0);
            cbInventoryBrand.setSelectedIndex(0);
            refreshAction.run();
        });
        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                com.citystyle.model.Shoe s = shoeDAO.getAllShoes().stream().filter(x -> x.getShoeId() == id).findFirst()
                        .orElse(null);
                showShoeDialog(s);
                refreshAction.run();
            }
        });
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "Delete this shoe?", "Confirm",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    shoeDAO.deleteShoe(id);
                    refreshAction.run();
                }
            }
        });

        refreshAction.run();

        // Real-time refreshing: Update inventory & metadata every 10 seconds
        Timer refreshTimer = new Timer(10000, e -> {
            refreshAction.run();
            refreshMetadataDropdowns(cbInventoryCat, cbInventoryBrand, cbInventoryColor, true);
        });
        refreshTimer.start();

        return panel;
    }

    // Helper method to assign consistent colors to categories
    // Helper method to assign consistent colors to categories
    private Color getCategoryColor(String category) {
        if (category == null)
            return Color.WHITE;

        // More Vibrant & Distinct Palette
        switch (category.toLowerCase().trim()) {
            case "sneakers":
                return new Color(255, 105, 180); // Hot Pink
            case "boots":
                return new Color(135, 206, 250); // Light Sky Blue
            case "formal":
                return new Color(46, 204, 113); // Emerald Green
            case "sandals":
                return new Color(241, 196, 15); // Sunflower Yellow
            case "sports":
                return new Color(231, 76, 60); // Alizarin Red
            case "casual":
                return new Color(26, 188, 156); // Turquoise
            case "heels":
                return new Color(155, 89, 182); // Amethyst Purple
            case "kids":
                return new Color(52, 152, 219); // Peter River Blue
            case "loafers":
                return new Color(230, 126, 34); // Carrot Orange
            case "slippers":
                return new Color(243, 156, 18); // Orange
            case "school shoes":
                return new Color(52, 73, 94); // Dark Blue Grey
            case "canvas":
                return new Color(149, 165, 166); // Concrete Grey
            default:
                // Generate a consistent pastel color based on string hash
                int hash = category.hashCode();
                int r = (hash & 0xFF) % 150 + 100;
                int g = ((hash >> 8) & 0xFF) % 150 + 100;
                int b = ((hash >> 16) & 0xFF) % 150 + 100;
                return new Color(r, g, b);
        }
    }

    // New Helper: Generate consistent colors for Models
    private Color getModelColor(String modelName) {
        if (modelName == null)
            return Color.WHITE;
        int hash = modelName.hashCode();
        // Generate lighter/pastel shades suitable for text readability if needed,
        // or vibrant if used as background. Here: Light Pastel.
        int r = (hash & 0xFF) % 127 + 128; // 128-255
        int g = ((hash >> 8) & 0xFF) % 127 + 128;
        int b = ((hash >> 16) & 0xFF) % 127 + 128;
        return new Color(r, g, b);
    }

    protected void refreshMetadataDropdowns(JComboBox<String> cbCat, JComboBox<String> cbBrand,
            JComboBox<String> cbColor, boolean includeAll) {
        if (cbCat == null || cbBrand == null)
            return;
        String selectedCat = (String) cbCat.getSelectedItem();
        String selectedBrand = (String) cbBrand.getSelectedItem();
        String selectedColor = cbColor != null ? (String) cbColor.getSelectedItem() : null;

        cbCat.removeAllItems();
        cbBrand.removeAllItems();
        if (cbColor != null)
            cbColor.removeAllItems();

        if (includeAll) {
            cbCat.addItem("All Categories");
            cbBrand.addItem("All Brands");
            if (cbColor != null)
                cbColor.addItem("All Colors");
        }

        metaDAO.getCategories().forEach(cbCat::addItem);
        metaDAO.getBrands().forEach(cbBrand::addItem);
        if (cbColor != null)
            metaDAO.getColors().forEach(cbColor::addItem);

        cbCat.setSelectedItem(selectedCat);
        cbBrand.setSelectedItem(selectedBrand);
        if (cbColor != null && selectedColor != null)
            cbColor.setSelectedItem(selectedColor);
    }

    protected void refreshAllUIMetadata() {
        refreshMetadataDropdowns(cbInventoryCat, cbInventoryBrand, cbInventoryColor, true);
        refreshMetadataDropdowns(cbPOSCat, cbPOSBrand, cbPOSColor, true);
    }

    protected void showShoeDialog(Shoe shoe) {
        JDialog dialog = new JDialog(this, shoe == null ? "Add Shoe" : "Update Shoe", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtModel = new JTextField(shoe != null ? shoe.getModelName() : "");
        JTextField txtMinS = new JTextField(shoe != null ? String.valueOf(shoe.getMinSize()) : "0");
        JTextField txtMaxS = new JTextField(shoe != null ? String.valueOf(shoe.getMaxSize()) : "0");
        JTextField txtColor = new JTextField(shoe != null ? shoe.getColor() : "");
        JTextField txtStock = new JTextField(shoe != null ? String.valueOf(shoe.getStock()) : "");
        JTextField txtPrice = new JTextField(shoe != null ? String.valueOf(shoe.getPrice()) : "");
        JTextField txtPromo = new JTextField(shoe != null ? String.valueOf(shoe.getPromotionalPrice()) : "");
        JComboBox<String> cbCat = new JComboBox<>();
        metaDAO.getCategories().forEach(cbCat::addItem);
        JComboBox<String> cbBrand = new JComboBox<>();
        metaDAO.getBrands().forEach(cbBrand::addItem);

        if (shoe != null) {
            cbCat.setSelectedItem(shoe.getCategoryName()); // Need to add categoryName to Shoe model if missing, or
                                                           // handle by ID
            cbBrand.setSelectedItem(shoe.getBrandName());
        }

        int r = 0;
        dialog.add(new JLabel("Model:"), gbcAt(0, r, gbc));
        dialog.add(txtModel, gbcAt(1, r++, gbc));
        dialog.add(new JLabel("Category:"), gbcAt(0, r, gbc));
        dialog.add(cbCat, gbcAt(1, r++, gbc));
        dialog.add(new JLabel("Brand:"), gbcAt(0, r, gbc));
        dialog.add(cbBrand, gbcAt(1, r++, gbc));

        dialog.add(new JLabel("Size :"), gbcAt(0, r, gbc));
        JPanel sizeP = new JPanel(new GridLayout(1, 2, 5, 0));
        sizeP.setOpaque(false);
        sizeP.add(txtMinS);
        sizeP.add(txtMaxS);
        dialog.add(sizeP, gbcAt(1, r++, gbc));

        dialog.add(new JLabel("Color:"), gbcAt(0, r, gbc));
        dialog.add(txtColor, gbcAt(1, r++, gbc));
        dialog.add(new JLabel("Stock:"), gbcAt(0, r, gbc));
        dialog.add(txtStock, gbcAt(1, r++, gbc));
        dialog.add(new JLabel("Price:"), gbcAt(0, r, gbc));
        dialog.add(txtPrice, gbcAt(1, r++, gbc));
        dialog.add(new JLabel("Promo Price:"), gbcAt(0, r, gbc));
        dialog.add(txtPromo, gbcAt(1, r++, gbc));

        JTextField txtThreshold = new JTextField("5");
        // Removed txtSupplier field initialization as we use ComboBox now

        JComboBox<String> cbSupplier = new JComboBox<>();
        metaDAO.getSuppliers().forEach(cbSupplier::addItem);
        if (shoe != null && shoe.getSupplierName() != null) {
            cbSupplier.setSelectedItem(shoe.getSupplierName());
        }

        setupValidation(txtModel, txtColor, txtMinS, txtMaxS, txtStock, txtPrice, txtPromo, dialog);
        setupValidation(new JTextField(), new JTextField(), new JTextField(), new JTextField(), txtThreshold,
                new JTextField(), new JTextField(), dialog);

        if (currentUser.getRole().equals("Manager")) {
            dialog.add(new JLabel("Reorder Threshold:"), gbcAt(0, r, gbc));
            dialog.add(txtThreshold, gbcAt(1, r++, gbc));
            dialog.add(new JLabel("Supplier:"), gbcAt(0, r, gbc));
            dialog.add(cbSupplier, gbcAt(1, r++, gbc));
        }

        JButton btnSave = new JButton("Save");
        dialog.add(btnSave, gbcAt(1, r, gbc));
        btnSave.addActionListener(e -> {
            try {
                String generatedSize = txtMinS.getText() + " - " + txtMaxS.getText();
                int brandId = metaDAO.getBrandIdByName((String) cbBrand.getSelectedItem());
                int catId = metaDAO.getCategoryIdByName((String) cbCat.getSelectedItem());

                String selectedSupplier = (String) cbSupplier.getSelectedItem();
                int supplierId = 1; // Default
                if (selectedSupplier != null) {
                    supplierId = metaDAO.getSupplierIdByName(selectedSupplier);
                    if (supplierId == -1) {
                        // Create if not exists (optional, or just error)
                        // For now assuming existing from dropdown
                        supplierId = 1;
                    }
                }

                if (shoe == null)
                    shoeDAO.addShoe(txtModel.getText(), brandId, catId,
                            generatedSize, Integer.parseInt(txtMinS.getText()),
                            Integer.parseInt(txtMaxS.getText()),
                            txtColor.getText(), Integer.parseInt(txtStock.getText()),
                            Double.parseDouble(txtPrice.getText()), supplierId);
                else
                    shoeDAO.updateShoe(shoe.getShoeId(), txtModel.getText(), brandId, catId, generatedSize,
                            Integer.parseInt(txtMinS.getText()), Integer.parseInt(txtMaxS.getText()),
                            txtColor.getText(), Integer.parseInt(txtStock.getText()),
                            Double.parseDouble(txtPrice.getText()),
                            Double.parseDouble(txtPromo.getText()), supplierId);
                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Failed to save shoe: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    protected JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Sales Terminal (POS)");
        title.setFont(TITLE_FONT);
        header.add(title, BorderLayout.WEST);

        JPanel posSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        posSearch.setBackground(Color.WHITE);
        posSearch.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JTextField txtPOSSearch = new JTextField(12);
        cbPOSCat = new JComboBox<>(new String[] { "All Categories" });
        cbPOSBrand = new JComboBox<>(new String[] { "All Brands" });
        cbPOSColor = new JComboBox<>(new String[] { "All Colors" });
        refreshMetadataDropdowns(cbPOSCat, cbPOSBrand, cbPOSColor, true);
        JButton btnPOSSearch = new JButton("Filter Products");
        btnPOSSearch.setBackground(ACCENT_COLOR);
        btnPOSSearch.setForeground(Color.WHITE);

        posSearch.add(new JLabel("Model:"));
        posSearch.add(txtPOSSearch);
        posSearch.add(new JLabel("Category:"));
        posSearch.add(cbPOSCat);
        posSearch.add(new JLabel("Brand:"));
        posSearch.add(cbPOSBrand);
        posSearch.add(new JLabel("Color:"));
        posSearch.add(cbPOSColor);
        posSearch.add(btnPOSSearch);

        header.add(posSearch, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        JPanel pos = new JPanel(new GridLayout(1, 2, 20, 0));
        pos.setBackground(BG_COLOR);

        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Select Product (Grouped by Model)"), BorderLayout.NORTH);

        // Update columns to show Grouped Info
        String[] sCols = { "Model", "Brand", "Category", "Size Range", "Price Range", "Total Stock" };
        DefaultTableModel sModel = new DefaultTableModel(sCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable sTable = new JTable(sModel);
        sTable.setRowHeight(40);
        sTable.setShowGrid(true);
        sTable.setGridColor(Color.LIGHT_GRAY);
        left.add(new JScrollPane(sTable), BorderLayout.CENTER);
        JButton btnAddCart = new JButton("Select Variant & Add to Cart");
        btnAddCart.setBackground(new Color(52, 152, 219));
        btnAddCart.setForeground(Color.WHITE);
        left.add(btnAddCart, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel("Cart Contents"), BorderLayout.NORTH);
        String[] cCols = { "ID", "Model", "Color", "Size", "Qty", "Price", "Total" };
        DefaultTableModel cModel = new DefaultTableModel(cCols, 0);
        JTable cTable = new JTable(cModel);
        cTable.setRowHeight(40);
        cTable.setShowGrid(true);
        cTable.setGridColor(Color.LIGHT_GRAY);
        right.add(new JScrollPane(cTable), BorderLayout.CENTER);

        JPanel customerP = new JPanel(new BorderLayout(5, 5));
        customerP.setOpaque(false);
        JTextField txtCustomerName = new JTextField();
        customerP.add(new JLabel("Customer Name:"), BorderLayout.WEST);
        customerP.add(txtCustomerName, BorderLayout.CENTER);

        JPanel rightBottom = new JPanel(new GridLayout(6, 1, 5, 5));
        JLabel lblTotal = new JLabel("Total: Rs. 0.00", JLabel.RIGHT);
        lblTotal.setFont(TITLE_FONT);

        JPanel discountP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        discountP.setOpaque(false);
        txtDiscount = new JTextField("0", 5);
        lblDiscountStatus = new JLabel("");
        btnReqDiscount = new JButton("Request");
        JButton btnViewReq = new JButton("Requests");

        discountP.add(new JLabel("Discount (%):"));
        discountP.add(txtDiscount);

        if (currentUser.getRole().equals("Cashier")) {
            discountP.add(btnReqDiscount);
            discountP.add(lblDiscountStatus);
        } else {
            discountP.add(btnViewReq);
        }

        JLabel lblFinalTotal = new JLabel("Final Total: Rs. 0.00", JLabel.RIGHT);
        lblFinalTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblFinalTotal.setForeground(new Color(192, 57, 43));

        JButton btnRemoveCart = new JButton("Remove Selected");
        JButton btnPay = new JButton("Process Payment");
        btnPay.setBackground(new Color(46, 204, 113));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));

        rightBottom.add(customerP);
        rightBottom.add(lblTotal);
        rightBottom.add(discountP);
        rightBottom.add(lblFinalTotal);
        rightBottom.add(btnRemoveCart);
        rightBottom.add(btnPay);
        right.add(rightBottom, BorderLayout.SOUTH);

        Runnable updateTotals = () -> {
            double total = 0;
            for (int i = 0; i < cModel.getRowCount(); i++)
                total += (double) cModel.getValueAt(i, 6);
            lblTotal.setText(String.format("Total: Rs. %.2f", total));

            try {
                double discountPct = Double.parseDouble(txtDiscount.getText());
                double finalTotal = total * (1 - (discountPct / 100.0));
                lblFinalTotal.setText(String.format("Final Total: Rs. %.2f", finalTotal));
            } catch (Exception ex) {
                lblFinalTotal.setText(String.format("Final Total: Rs. %.2f", total));
            }
        };

        txtDiscount.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateTotals.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateTotals.run();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateTotals.run();
            }
        });

        if (currentUser.getRole().equals("Cashier")) {
            // Polling
            javax.swing.Timer statusTimer = new javax.swing.Timer(2000, e -> {
                if (currentDiscountRequestId != -1 && !isDiscountApproved) {
                    String status = drDAO.checkStatus(currentDiscountRequestId);
                    if ("Approved".equals(status)) {
                        isDiscountApproved = true;
                        lblDiscountStatus.setText("Approved");
                        lblDiscountStatus.setForeground(Color.GREEN);
                        txtDiscount.setEnabled(false);
                        btnReqDiscount.setEnabled(false);
                        updateTotals.run();
                    } else if ("Rejected".equals(status)) {
                        lblDiscountStatus.setText("Rejected");
                        lblDiscountStatus.setForeground(Color.RED);
                        currentDiscountRequestId = -1;
                        btnReqDiscount.setEnabled(true);
                    }
                }
            });
            statusTimer.start();
            btnReqDiscount.addActionListener(e -> {
                try {
                    double d = Double.parseDouble(txtDiscount.getText());
                    if (d > 0) {
                        // Build Items Summary
                        StringBuilder summary = new StringBuilder();
                        for (int i = 0; i < cModel.getRowCount(); i++) {
                            summary.append(cModel.getValueAt(i, 1)) // Model Name
                                    .append(" (")
                                    .append(cModel.getValueAt(i, 2)) // Color
                                    .append(", sz ")
                                    .append(cModel.getValueAt(i, 3)) // Size
                                    .append(") x")
                                    .append(cModel.getValueAt(i, 4)) // Qty
                                    .append("; ");
                        }
                        currentDiscountRequestId = drDAO.requestDiscount(currentUser.getUserId(), d,
                                summary.toString());
                        if (currentDiscountRequestId != -1) {
                            lblDiscountStatus.setText("Pending (Req ID: " + currentDiscountRequestId + ")");
                            lblDiscountStatus.setForeground(Color.ORANGE);
                            btnReqDiscount.setEnabled(false);
                        }
                    }
                } catch (Exception ex) {
                }
            });
        }

        btnViewReq.addActionListener(e -> showDiscountRequestsDialog());

        txtDiscount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (currentUser.getRole().equals("Manager") || isDiscountApproved)
                    updateTotals.run();
            }
        });

        pos.add(left);
        pos.add(right);
        panel.add(pos, BorderLayout.CENTER);

        // Store grouped shoes for access by Add Button
        final java.util.Map<String, java.util.List<Shoe>> groupedShoes = new java.util.HashMap<>();

        Runnable refreshProducts = () -> {
            sModel.setRowCount(0);
            groupedShoes.clear();

            List<Shoe> allResults = shoeDAO.searchByAll(txtPOSSearch.getText(),
                    cbPOSCat.getSelectedIndex() == 0 ? "All" : (String) cbPOSCat.getSelectedItem(),
                    cbPOSBrand.getSelectedIndex() == 0 ? "All" : (String) cbPOSBrand.getSelectedItem(),
                    cbPOSColor.getSelectedIndex() == 0 ? "All" : (String) cbPOSColor.getSelectedItem(), null, -1,
                    -1, -1, -1);

            // Group by Model Name
            for (Shoe s : allResults) {
                groupedShoes.computeIfAbsent(s.getModelName(), k -> new java.util.ArrayList<>()).add(s);
            }

            for (String modelName : groupedShoes.keySet()) {
                List<Shoe> variants = groupedShoes.get(modelName);
                if (variants.isEmpty())
                    continue;
                Shoe first = variants.get(0);
                int totalStock = variants.stream().mapToInt(Shoe::getStock).sum();
                double minP = variants.stream().mapToDouble(Shoe::getEffectivePrice).min().orElse(0);
                double maxP = variants.stream().mapToDouble(Shoe::getEffectivePrice).max().orElse(0);

                String priceDisplay = (minP == maxP) ? "Rs. " + minP : "Rs. " + minP + " - " + maxP;

                int overallMinSize = variants.stream().mapToInt(Shoe::getMinSize).min().orElse(0);
                int overallMaxSize = variants.stream().mapToInt(Shoe::getMaxSize).max().orElse(0);
                String sizeDisplay = overallMinSize == overallMaxSize ? String.valueOf(overallMinSize)
                        : overallMinSize + " - " + overallMaxSize;

                sModel.addRow(new Object[] {
                        first.getModelName(),
                        first.getBrandName(),
                        first.getCategoryName(),
                        sizeDisplay,
                        priceDisplay,
                        totalStock
                });
            }
        };

        btnPOSSearch.addActionListener(e -> refreshProducts.run());

        btnAddCart.addActionListener(e -> {
            int r = sTable.getSelectedRow();
            if (r != -1) {
                String modelName = (String) sModel.getValueAt(r, 0);
                List<Shoe> variants = groupedShoes.get(modelName);
                if (variants == null || variants.isEmpty())
                    return;

                // Create Custom Dialog for Variant Selection
                JDialog d = new JDialog(this, "Select Size & Color: " + modelName, true);
                d.setSize(400, 450);
                d.setLayout(new GridBagLayout());
                d.setLocationRelativeTo(this);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // Extract Unique Colors
                java.util.Set<String> colors = new java.util.HashSet<>();
                variants.forEach(s -> colors.add(s.getColor()));
                JComboBox<String> cbColor = new JComboBox<>(colors.toArray(new String[0]));

                // Size Dropdown (Updated based on color)
                // Size Input Field (Replaced Dropdown)
                JTextField txtSize = new JTextField(5);

                // Stock Info Display
                JTextArea txtStockInfo = new JTextArea(5, 20);
                txtStockInfo.setEditable(false);
                txtStockInfo.setLineWrap(true);
                txtStockInfo.setWrapStyleWord(true);
                txtStockInfo.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollStock = new JScrollPane(txtStockInfo);

                Runnable updateStockDisplay = () -> {
                    String selColor = (String) cbColor.getSelectedItem();
                    if (selColor == null) {
                        txtStockInfo.setText("");
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    variants.stream()
                            .filter(s -> s.getColor().equalsIgnoreCase(selColor))
                            .forEach(s -> {
                                String sizeStr;
                                if (s.getMinSize() > 0 && s.getMaxSize() > s.getMinSize()) {
                                    sizeStr = s.getMinSize() + "-" + s.getMaxSize();
                                } else {
                                    sizeStr = (s.getSize() != null && !s.getSize().isEmpty()) ? s.getSize()
                                            : String.valueOf(s.getMinSize());
                                }
                                sb.append("Size ").append(sizeStr).append(": ").append(s.getStock()).append(" left\n");
                            });
                    txtStockInfo.setText(sb.toString());
                };

                cbColor.addActionListener(ev -> updateStockDisplay.run());

                // Populate Color Dropdown
                if (cbColor.getItemCount() > 0) {
                    cbColor.setSelectedIndex(0);
                    updateStockDisplay.run();
                }

                d.add(new JLabel("Select Color:"), gbcAt(0, 0, gbc));
                d.add(cbColor, gbcAt(1, 0, gbc));
                d.add(new JLabel("Enter Size:"), gbcAt(0, 1, gbc));
                d.add(txtSize, gbcAt(1, 1, gbc));

                JTextField txtQty = new JTextField("1", 5);
                d.add(new JLabel("Quantity:"), gbcAt(0, 2, gbc));
                d.add(txtQty, gbcAt(1, 2, gbc));

                JButton btnConfirm = new JButton("Add to Cart");
                btnConfirm.setBackground(ACCENT_COLOR);
                btnConfirm.setForeground(Color.WHITE);
                d.add(btnConfirm, gbcAt(1, 3, gbc));

                d.add(new JLabel("Available Stock:"), gbcAt(0, 4, gbc));

                GridBagConstraints gbcStock = new GridBagConstraints();
                gbcStock.gridx = 0;
                gbcStock.gridy = 5;
                gbcStock.gridwidth = 2;
                gbcStock.fill = GridBagConstraints.BOTH;
                gbcStock.weightx = 1.0;
                gbcStock.weighty = 1.0;
                gbcStock.insets = new Insets(5, 10, 10, 10);
                d.add(scrollStock, gbcStock);

                btnConfirm.addActionListener(ev -> {
                    String selColor = (String) cbColor.getSelectedItem();
                    String selSize = txtSize.getText().trim();

                    if (selSize.isEmpty()) {
                        JOptionPane.showMessageDialog(d, "Please enter a size.");
                        return;
                    }

                    int qty = 0;
                    try {
                        qty = Integer.parseInt(txtQty.getText());
                        if (qty <= 0)
                            throw new NumberFormatException();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(d, "Please enter a valid quantity > 0");
                        return;
                    }

                    // Find the exact shoe object or the range object covering this size
                    Shoe selectedShoe = null;
                    for (Shoe s : variants) {
                        if (s.getColor().equalsIgnoreCase(selColor)) {
                            // Check specific size match
                            if (s.getSize() != null && s.getSize().equalsIgnoreCase(selSize)) {
                                selectedShoe = s;
                                break;
                            }
                            // Check range match
                            if (s.getMinSize() > 0 && s.getMaxSize() >= s.getMinSize()) {
                                try {
                                    int sizeInt = Integer.parseInt(selSize);
                                    if (sizeInt >= s.getMinSize() && sizeInt <= s.getMaxSize()) {
                                        selectedShoe = s;
                                        break;
                                    }
                                } catch (Exception ex) {
                                    // Not an integer size, ignore range check
                                }
                            }
                        }
                    }

                    if (selectedShoe == null) {
                        JOptionPane.showMessageDialog(d,
                                "Size " + selSize + " is not available for " + selColor + " in this model.");
                        return;
                    }

                    if (selectedShoe.getStock() <= 0) {
                        JOptionPane.showMessageDialog(d, "Out of Stock!");
                        return;
                    }

                    if (qty > selectedShoe.getStock()) {
                        JOptionPane.showMessageDialog(d,
                                "Only " + selectedShoe.getStock() + " items available in stock.");
                        return;
                    }

                    // Check if already in cart
                    boolean found = false;
                    for (int i = 0; i < cModel.getRowCount(); i++) {
                        // Check ID match AND Color match AND specific Size match
                        if ((int) cModel.getValueAt(i, 0) == selectedShoe.getShoeId() &&
                                cModel.getValueAt(i, 2).equals(selColor) &&
                                cModel.getValueAt(i, 3).equals(selSize)) {

                            int currentQty = (int) cModel.getValueAt(i, 4);
                            if (currentQty + qty > selectedShoe.getStock()) {
                                JOptionPane.showMessageDialog(d, "Total quantity exceeds stock limit!");
                                return;
                            }
                            cModel.setValueAt(currentQty + qty, i, 4);
                            cModel.setValueAt((currentQty + qty) * selectedShoe.getEffectivePrice(), i, 6);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        cModel.addRow(new Object[] {
                                selectedShoe.getShoeId(),
                                selectedShoe.getModelName(),
                                selectedShoe.getColor(),
                                selSize, // Use the SPECIFIC size entered by user
                                qty,
                                selectedShoe.getEffectivePrice(),
                                selectedShoe.getEffectivePrice() * qty
                        });
                    }
                    updateTotals.run();
                    d.dispose();
                });

                d.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a model first.");
            }
        });

        btnRemoveCart.addActionListener(e -> {
            int r = cTable.getSelectedRow();
            if (r != -1) {
                cModel.removeRow(r);
                updateTotals.run();
            }
        });

        btnPay.addActionListener(e -> {
            if (cModel.getRowCount() == 0)
                return;

            String customerName = txtCustomerName.getText().trim();
            if (customerName.isEmpty())
                customerName = "Walk-in Customer";

            java.util.List<com.citystyle.model.CartItem> items = new java.util.ArrayList<>();
            double total = 0;
            for (int i = 0; i < cModel.getRowCount(); i++) {
                int id = (int) cModel.getValueAt(i, 0);
                String name = (String) cModel.getValueAt(i, 1);
                String color = (String) cModel.getValueAt(i, 2);
                String size = (String) cModel.getValueAt(i, 3);
                int qty = (int) cModel.getValueAt(i, 4);
                double price = (double) cModel.getValueAt(i, 5);
                items.add(new com.citystyle.model.CartItem(id, name, qty, price, size, color));
                total += (double) cModel.getValueAt(i, 6);
            }
            try {
                double discountPct = 0;
                try {
                    discountPct = Double.parseDouble(txtDiscount.getText());
                } catch (Exception ex) {
                }

                if (discountPct > 0 && currentUser.getRole().equals("Cashier") && !isDiscountApproved) {
                    JOptionPane.showMessageDialog(this,
                            "Discount of " + discountPct + "% must be approved by an Admin.",
                            "Approval Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double finalTotal = total * (1 - (discountPct / 100.0));

                saleDAO.processSale(currentUser.getUserId(), customerName, items, finalTotal);

                StringBuilder sb = new StringBuilder();
                sb.append("Sale Processed Successfully!\n");
                sb.append("Customer: ").append(customerName).append("\n");
                sb.append("Total: Rs. ").append(String.format("%.2f", finalTotal)).append("\n\n");
                sb.append("Next Customer, please!");

                JOptionPane.showMessageDialog(this, sb.toString());

                cModel.setRowCount(0);
                txtDiscount.setText("0");
                txtCustomerName.setText("");
                isDiscountApproved = false;
                currentDiscountRequestId = -1;
                txtDiscount.setEnabled(true);
                if (btnReqDiscount != null)
                    btnReqDiscount.setEnabled(true);
                if (lblDiscountStatus != null)
                    lblDiscountStatus.setText("");
                updateTotals.run();
                refreshProducts.run();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Checkout Failed!\nError: " + ex.getMessage() + "\nCheck if database schema is updated.");
            }
        });

        refreshProducts.run();

        // Real-time refreshing for POS section
        Timer posRefreshTimer = new Timer(10000, e -> {
            // Only refresh if search fields are empty to avoid interrupting user typing
            if (txtPOSSearch.getText().isEmpty() && cbPOSCat.getSelectedIndex() == 0
                    && cbPOSBrand.getSelectedIndex() == 0) {
                refreshProducts.run();
                refreshMetadataDropdowns(cbPOSCat, cbPOSBrand, cbPOSColor, true);
            }
        });
        posRefreshTimer.start();

        return panel;
    }

    protected JPanel createReturnsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Returns & Inventory Recovery");
        title.setFont(TITLE_FONT);
        panel.add(title, BorderLayout.NORTH);

        // Split Pane to show Returns and Recent Sales (so they know what ID to use!)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setOpaque(false);

        // Top: Return Requests
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(new JLabel("Active Return Requests"), BorderLayout.NORTH);
        String[] cols = { "RID", "Item ID", "Sale ID", "Model", "Reason", "Status", "Date", "Admin" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(40);
        top.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: Recent Sales Items (Lookup)
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(new JLabel("Lookup: Recent Sales Items (Use 'Item ID' for returns)"), BorderLayout.NORTH);
        String[] sCols = { "Item ID", "Sale ID", "Model", "Brand", "Color", "Size", "Customer", "Qty", "Price Paid" };
        DefaultTableModel sModel = new DefaultTableModel(sCols, 0);
        JTable sTable = new JTable(sModel);
        sTable.setRowHeight(40);
        bottom.add(new JScrollPane(sTable), BorderLayout.CENTER);

        splitPane.setTopComponent(top);
        splitPane.setBottomComponent(bottom);
        panel.add(splitPane, BorderLayout.CENTER);

        // Container for all footer actions
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);

        // Row 1: Return Creation (Accessible to everyone)
        JPanel creationActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        creationActions.setOpaque(false);
        JButton btnRefresh = new JButton("Refresh Data");
        JTextField txtSaleItemId = new JTextField(5);
        JTextField txtReason = new JTextField(15);
        JButton btnCreateReturn = new JButton("Process New Return");
        btnCreateReturn.setBackground(ACCENT_COLOR);
        btnCreateReturn.setForeground(Color.WHITE);

        creationActions.add(btnRefresh);
        creationActions.add(new JLabel(" Enter Item ID:"));
        creationActions.add(txtSaleItemId);
        creationActions.add(new JLabel(" Reason:"));
        creationActions.add(txtReason);
        creationActions.add(btnCreateReturn);
        footerPanel.add(creationActions);

        // Row 2: Management Actions (Managers Only)
        if (currentUser.getRole().equals("Manager")) {
            JPanel mgmtActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
            mgmtActions.setOpaque(false);

            JButton btnApprove = new JButton("Approve & Restock Selected");
            btnApprove.setBackground(new Color(46, 204, 113));
            btnApprove.setForeground(Color.WHITE);
            mgmtActions.add(btnApprove);
            btnApprove.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    returnDAO.approveReturn((int) table.getValueAt(row, 0), currentUser.getUserId());
                    btnRefresh.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a return request to approve.");
                }
            });

            JButton btnDeleteReturn = new JButton("Delete Return Record");
            btnDeleteReturn.setBackground(new Color(231, 76, 60));
            btnDeleteReturn.setForeground(Color.WHITE);
            mgmtActions.add(btnDeleteReturn);
            btnDeleteReturn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int id = (int) table.getValueAt(row, 0);
                    if (JOptionPane.showConfirmDialog(this, "Delete this return record?", "Confirm",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        returnDAO.deleteReturn(id);
                        btnRefresh.doClick();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a return record to delete.");
                }
            });

            JButton btnDeleteItem = new JButton("Delete Sale Item from History");
            btnDeleteItem.setBackground(new Color(192, 57, 43));
            btnDeleteItem.setForeground(Color.WHITE);
            mgmtActions.add(btnDeleteItem);
            btnDeleteItem.addActionListener(e -> {
                int row = sTable.getSelectedRow();
                if (row != -1) {
                    int itemId = Integer.parseInt((String) sModel.getValueAt(row, 0));
                    if (JOptionPane.showConfirmDialog(this,
                            "Permanently delete this Sale Item (ID: " + itemId + ") from history?", "Confirm",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        saleDAO.deleteSaleItem(itemId);
                        btnRefresh.doClick();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Please select an item from the bottom lookup table to delete.");
                }
            });
            footerPanel.add(mgmtActions);
        }
        panel.add(footerPanel, BorderLayout.SOUTH);

        // Input Validation
        txtSaleItemId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });

        Runnable refresh = () -> {
            model.setRowCount(0);
            returnDAO.getAllReturns().forEach(r -> model.addRow(new Object[] {
                    r.getReturnId(), r.getSaleItemId(), r.getSaleId(), r.getModelName(),
                    r.getReason(), r.getStatus(), r.getReturnDate(), r.getApprovedBy()
            }));

            sModel.setRowCount(0);
            returnDAO.getRecentSalesItems().forEach(sModel::addRow);
        };

        btnRefresh.addActionListener(e -> refresh.run());
        btnCreateReturn.addActionListener(e -> {
            try {
                if (txtSaleItemId.getText().isEmpty())
                    throw new Exception("Please enter an Item ID from the lookup table.");
                returnDAO.createReturn(Integer.parseInt(txtSaleItemId.getText()), txtReason.getText());
                JOptionPane.showMessageDialog(this, "Return request created and pending approval!");
                txtSaleItemId.setText("");
                txtReason.setText("");
                refresh.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Process Failed: " + ex.getMessage());
            }
        });
        refresh.run();

        // Real-time refresh for returns lookup
        Timer returnTimer = new Timer(10000, e -> refresh.run());
        returnTimer.start();

        return panel;
    }

    private void setupValidation(JTextField tSearch, JTextField tColor, JTextField tSize, JTextField tMin,
            JTextField tMax, Component parent) {
        java.awt.event.KeyAdapter numOnly = new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '.') {
                    JOptionPane.showMessageDialog(parent, "numbers only");
                    e.consume();
                }
            }
        };
        // Removed generic text-only validation as it blocks valid alphanumeric inputs
        // (e.g. Model names)
        tSize.addKeyListener(numOnly);
        tMin.addKeyListener(numOnly);
        tMax.addKeyListener(numOnly);
    }

    private void setupValidation(JTextField m, JTextField c, JTextField sMin, JTextField sMax, JTextField st,
            JTextField p,
            JTextField pr, Component parent) {
        java.awt.event.KeyAdapter numOnly = new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) && e.getKeyChar() != '.') {
                    JOptionPane.showMessageDialog(parent, "numbers only");
                    e.consume();
                }
            }
        };
        // Removed generic text-only validation
        sMin.addKeyListener(numOnly);
        sMax.addKeyListener(numOnly);
        st.addKeyListener(numOnly);
        p.addKeyListener(numOnly);
        pr.addKeyListener(numOnly);
    }

    private void showBulkPriceDialog(Runnable refreshAction) {
        JDialog d = new JDialog(this, "Bulk Price Update (Promotional)", true);
        d.setSize(350, 250);
        d.setLayout(new GridBagLayout());
        d.setLocationRelativeTo(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cbCat = new JComboBox<>();
        metaDAO.getCategories().forEach(cbCat::addItem);
        JTextField txtPercent = new JTextField("10", 5);

        JButton btnIncrease = new JButton("Apply % Increase");
        btnIncrease.setBackground(new Color(46, 204, 113));
        btnIncrease.setForeground(Color.WHITE);

        JButton btnDecrease = new JButton("Apply % Decrease (Discount)");
        btnDecrease.setBackground(new Color(231, 76, 60));
        btnDecrease.setForeground(Color.WHITE);

        int row = 0;
        d.add(new JLabel("Target Category:"), gbcAt(0, row, gbc));
        d.add(cbCat, gbcAt(1, row++, gbc));

        d.add(new JLabel("Percentage Value (%):"), gbcAt(0, row, gbc));
        d.add(txtPercent, gbcAt(1, row++, gbc));

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        btnPanel.add(btnIncrease);
        btnPanel.add(btnDecrease);

        gbc.gridwidth = 2;
        d.add(btnPanel, gbcAt(0, row++, gbc));

        java.util.function.BiConsumer<String, Double> applyUpdate = (cat, pct) -> {
            try {
                shoeDAO.bulkUpdatePrice(cat, pct);
                String mode = pct >= 0 ? "Increased" : "Decreased";
                JOptionPane.showMessageDialog(d,
                        "Current (Promo) Prices " + mode + " by " + Math.abs(pct) + "% for " + cat);
                d.dispose();
                refreshAction.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage());
            }
        };

        btnIncrease.addActionListener(e -> {
            try {
                double val = Double.parseDouble(txtPercent.getText());
                applyUpdate.accept((String) cbCat.getSelectedItem(), val);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Invalid number");
            }
        });

        btnDecrease.addActionListener(e -> {
            try {
                double val = Double.parseDouble(txtPercent.getText());
                applyUpdate.accept((String) cbCat.getSelectedItem(), -val);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Invalid number");
            }
        });

        d.setVisible(true);
    }

    protected GridBagConstraints gbcAt(int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }

    protected void logout() {
        new LoginFrame().setVisible(true);
        dispose();
    }

    private void showDiscountRequestsDialog() {
        JDialog d = new JDialog(this, "Discount Requests", true);
        d.setSize(600, 400);
        d.setLocationRelativeTo(this);

        String[] cols = { "ID", "Cashier", "Percentage", "Time", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        d.add(new JScrollPane(table));

        JPanel p = new JPanel();
        JButton btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject");
        JButton btnRefresh = new JButton("Refresh");
        p.add(btnApprove);
        p.add(btnReject);
        p.add(btnRefresh);
        d.add(p, BorderLayout.SOUTH);

        Runnable load = () -> {
            model.setRowCount(0);
            for (DiscountRequestDAO.RequestData r : drDAO.getPendingRequests()) {
                model.addRow(new Object[] { r.id, r.cashierName, r.percentage, r.time, "Pending" });
            }
        };
        load.run();

        btnRefresh.addActionListener(e -> load.run());

        btnApprove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                drDAO.updateStatus(id, "Approved");
                load.run();
            }
        });

        btnReject.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                drDAO.updateStatus(id, "Rejected");
                load.run();
            }
        });

        d.setVisible(true);
    }
}
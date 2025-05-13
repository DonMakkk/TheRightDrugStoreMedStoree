package com.mycompany.therightdrugstoremedstoree;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class KioskSystem extends JFrame {
    private static final double DISCOUNT_RATE = 0.20;

    static class Product {
        String name;
        double price;
        ImageIcon image;
        boolean isRx;

        Product(String name, double price, ImageIcon image, boolean isRx) {
            this.name = name;
            this.price = price;
            this.image = image;
            this.isRx = isRx;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Product)) return false;
            Product product = (Product) o;
            return Objects.equals(name, product.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    static class CartEntry {
        Product product;
        int quantity;

        CartEntry(Product product) {
            this.product = product;
            this.quantity = 1;
        }
    }

    private final List<Product> brandedProducts = new ArrayList<>();
    private final List<Product> genericProducts = new ArrayList<>();
    private final List<Product> rxProducts = new ArrayList<>();

    private final Map<Product, CartEntry> cart = new LinkedHashMap<>();

    private JPanel productsPanel;
    private JCheckBox seniorCheckbox;
    private JCheckBox pwdCheckbox;
    private JComboBox<String> categoryCombo;
    private JLabel cartStatusLabel;

    public KioskSystem() {
        setTitle("Pharmacy Kiosk System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(600, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Pharmacy Kiosk", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Top controls panel with category & discounts
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 15, 0, 15);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel categoryLabel = new JLabel("Select Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        topPanel.add(categoryLabel, gbc);

        categoryCombo = new JComboBox<>(new String[]{"Branded", "Generic", "Rx"});
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        topPanel.add(categoryCombo, gbc);

        seniorCheckbox = new JCheckBox("Senior Citizen Discount");
        seniorCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 2;
        topPanel.add(seniorCheckbox, gbc);

        pwdCheckbox = new JCheckBox("PWD Discount");
        pwdCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 3;
        topPanel.add(pwdCheckbox, gbc);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(header, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        productsPanel = new JPanel();
        productsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for buttons and cart status
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        GridBagConstraints bgbc = new GridBagConstraints();
        bgbc.insets = new Insets(0, 15, 0, 15);
        bgbc.anchor = GridBagConstraints.CENTER;

        JButton viewCartBtn = new JButton("View Cart");
        viewCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bgbc.gridx = 0;
        bottomPanel.add(viewCartBtn, bgbc);

        cartStatusLabel = new JLabel("Cart is empty");
        cartStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bgbc.gridx = 1;
        bottomPanel.add(cartStatusLabel, bgbc);

        add(bottomPanel, BorderLayout.SOUTH);

        populateProducts();

        categoryCombo.addActionListener(e -> refreshProducts());
        seniorCheckbox.addItemListener(e -> refreshProducts());
        pwdCheckbox.addItemListener(e -> refreshProducts());

        viewCartBtn.addActionListener(e -> showCartDialog());

        refreshProducts();
    }

    private void populateProducts() {
        brandedProducts.clear();
        genericProducts.clear();
        rxProducts.clear();

        // 15 branded products with fallback icons
        brandedProducts.add(new Product("Panadol Extra", 120.00, createSampleIcon("P", new Color(0x1E90FF)), false));
        brandedProducts.add(new Product("Neozep", 130.00, createSampleIcon("N", new Color(0x0077CC)), false));
        brandedProducts.add(new Product("Solmux", 115.00, createSampleIcon("S", new Color(0x005FA3)), false));
        brandedProducts.add(new Product("Biogesic", 140.00, createSampleIcon("B", new Color(0x00457C)), false));
        brandedProducts.add(new Product("Decolgen", 125.50, createSampleIcon("D", new Color(0x003357)), false));
        brandedProducts.add(new Product("Neurobion", 135.00, createSampleIcon("Ne", new Color(0x00253F)), false));
        brandedProducts.add(new Product("Ceelin", 128.00, createSampleIcon("C", new Color(0x001A2A)), false));
        brandedProducts.add(new Product("Solmux Forte", 138.00, createSampleIcon("SF", new Color(0x001318)), false));
        brandedProducts.add(new Product("Tuseran", 145.00, createSampleIcon("T", new Color(0x000D11)), false));
        brandedProducts.add(new Product("Vicks VapoRub", 150.00, createSampleIcon("V", new Color(0x000708)), false));
        brandedProducts.add(new Product("Voltaren", 155.00, createSampleIcon("Vo", new Color(0x000404)), false));
        brandedProducts.add(new Product("Tuseran Forte", 160.00, createSampleIcon("TF", new Color(0x000202)), false));
        brandedProducts.add(new Product("Redoxon", 165.00, createSampleIcon("R", new Color(0x000101)), false));
        brandedProducts.add(new Product("Biolax", 170.00, createSampleIcon("Bi", new Color(0x000000)), false));
        brandedProducts.add(new Product("Alaxan", 175.00, createSampleIcon("A", new Color(0x001122)), false));

        // 15 generic products with fallback icons
        genericProducts.add(new Product("Paracetamol", 55.00, createSampleIcon("Pa", new Color(0x228B22)), false));
        genericProducts.add(new Product("Ascorbic Acid", 58.00, createSampleIcon("Aa", new Color(0x2E8B57)), false));
        genericProducts.add(new Product("Ibuprofen", 60.00, createSampleIcon("I", new Color(0x3CB371)), false));
        genericProducts.add(new Product("Cetirizine", 62.00, createSampleIcon("Ce", new Color(0x66CDAA)), false));
        genericProducts.add(new Product("Loperamide", 65.00, createSampleIcon("Lo", new Color(0x8FBC8F)), false));
        genericProducts.add(new Product("Metformin", 67.00, createSampleIcon("Me", new Color(0x90EE90)), false));
        genericProducts.add(new Product("Omeprazole", 70.00, createSampleIcon("O", new Color(0x98FB98)), false));
        genericProducts.add(new Product("Amoxicillin", 72.00, createSampleIcon("Am", new Color(0x00FF7F)), false));
        genericProducts.add(new Product("Simvastatin", 75.00, createSampleIcon("Si", new Color(0x3CB371)), false));
        genericProducts.add(new Product("Furosemide", 77.00, createSampleIcon("Fu", new Color(0x2E8B57)), false));
        genericProducts.add(new Product("Atenolol", 80.00, createSampleIcon("At", new Color(0x006400)), false));
        genericProducts.add(new Product("Ciprofloxacin", 82.00, createSampleIcon("Ci", new Color(0x008000)), false));
        genericProducts.add(new Product("Clindamycin", 85.00, createSampleIcon("Cl", new Color(0x228B22)), false));
        genericProducts.add(new Product("Hydrochlorothiazide", 87.00, createSampleIcon("Hy", new Color(0x32CD32)), false));
        genericProducts.add(new Product("Prednisone", 90.00, createSampleIcon("Pr", new Color(0x7CFC00)), false));

        // 15 Rx products with fallback icons
        rxProducts.add(new Product("Amoxicillin RX", 155.00, createSampleIcon("AR", new Color(0xB22222)), true));
        rxProducts.add(new Product("Ibuprofen RX", 160.00, createSampleIcon("IR", new Color(0x8B0000)), true));
        rxProducts.add(new Product("Metformin RX", 165.00, createSampleIcon("MR", new Color(0xA52A2A)), true));
        rxProducts.add(new Product("Atorvastatin RX", 170.00, createSampleIcon("AR", new Color(0xFF0000)), true));
        rxProducts.add(new Product("Lisinopril RX", 175.00, createSampleIcon("LR", new Color(0xDC143C)), true));
        rxProducts.add(new Product("Omeprazole RX", 180.00, createSampleIcon("OR", new Color(0xB03060)), true));
        rxProducts.add(new Product("Simvastatin RX", 185.00, createSampleIcon("SR", new Color(0xC71585)), true));
        rxProducts.add(new Product("Hydrochlorothiazide RX", 190.00, createSampleIcon("HR", new Color(0xDB7093)), true));
        rxProducts.add(new Product("Prednisone RX", 195.00, createSampleIcon("PR", new Color(0xFF69B4)), true));
        rxProducts.add(new Product("Clindamycin RX", 200.00, createSampleIcon("CR", new Color(0xFF1493)), true));
        rxProducts.add(new Product("Azithromycin RX", 205.00, createSampleIcon("AR", new Color(0xFF6347)), true));
        rxProducts.add(new Product("Levofloxacin RX", 210.00, createSampleIcon("LR", new Color(0xFF4500)), true));
        rxProducts.add(new Product("Doxycycline RX", 215.00, createSampleIcon("DR", new Color(0xE9967A)), true));
        rxProducts.add(new Product("Metronidazole RX", 220.00, createSampleIcon("MR", new Color(0xFA8072)), true));
        rxProducts.add(new Product("Ciprofloxacin RX", 225.00, createSampleIcon("CR", new Color(0xF08080)), true));
    }

    private ImageIcon createSampleIcon(String text, Color bgColor) {
        int width = 120;
        int height = 120;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, width, height, 20, 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2 - 10);

        g2.dispose();
        return new ImageIcon(image);
    }

    private void refreshProducts() {
        productsPanel.removeAll();

        List<Product> currentList;
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        if ("Branded".equals(selectedCategory)) currentList = brandedProducts;
        else if ("Generic".equals(selectedCategory)) currentList = genericProducts;
        else currentList = rxProducts;

        boolean senior = seniorCheckbox.isSelected();
        boolean pwd = pwdCheckbox.isSelected();
        boolean hasDiscount = senior || pwd;

        // Limit to first 15 items exactly if list is longer, else use all
        List<Product> displayList = currentList.size() > 15 ? currentList.subList(0, 15) : new ArrayList<>(currentList);

        for (Product p : displayList) {
            JPanel pPanel = new JPanel();
            pPanel.setPreferredSize(new Dimension(220, 260));
            pPanel.setLayout(new BorderLayout());
            pPanel.setBackground(Color.WHITE);
            pPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xBBBBBB), 1, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));

            JLabel imgLabel = new JLabel(p.image);
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setPreferredSize(new Dimension(220, 140));
            pPanel.add(imgLabel, BorderLayout.NORTH);

            JLabel nameLabel = new JLabel("<html><div style='text-align: center;'>" + p.name + "</div></html>", SwingConstants.CENTER);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            nameLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
            pPanel.add(nameLabel, BorderLayout.CENTER);

            double priceToShow = p.price;
            String priceText;
            if (hasDiscount) {
                priceToShow = p.price * (1 - DISCOUNT_RATE);
                priceText = String.format("<html><div style='text-align: center;'>Price: <s>₱%.2f</s><br><span style='color: #CC0000;'>₱%.2f (Discounted)</span></div></html>", p.price, priceToShow);
            } else {
                priceText = String.format("Price: ₱%.2f", p.price);
            }
            JLabel priceLabel = new JLabel(priceText, SwingConstants.CENTER);
            priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            priceLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            pPanel.add(priceLabel, BorderLayout.SOUTH);

            pPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            pPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    addToCart(p);
                }
            });

            productsPanel.add(pPanel);
        }

        productsPanel.setLayout(new GridLayout(0, 4, 20, 20)); // 4 items per row
        productsPanel.revalidate();
        productsPanel.repaint();

        updateCartStatus();
    }

    private void addToCart(Product product) {
        cart.compute(product, (k, v) -> {
            if (v == null) return new CartEntry(product);
            v.quantity++;
            return v;
        });
        JOptionPane.showMessageDialog(this,
                product.name + " added to cart!",
                "Added",
                JOptionPane.INFORMATION_MESSAGE);
        updateCartStatus();
    }

    private void updateCartStatus() {
        int totalItems = cart.values().stream().mapToInt(e -> e.quantity).sum();
        cartStatusLabel.setText("Cart: " + totalItems + " item" + (totalItems != 1 ? "s" : ""));
    }

    private void showCartDialog() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Your cart is empty.",
                    "Cart",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(480, 350));

        String[] columnNames = {"Product", "Unit Price", "Quantity", "Subtotal", "Remove"};
        Object[][] data = new Object[cart.size()][5];
        boolean hasDiscount = seniorCheckbox.isSelected() || pwdCheckbox.isSelected();

        List<CartEntry> entries = new ArrayList<>(cart.values());

        for (int i = 0; i < entries.size(); i++) {
            CartEntry entry = entries.get(i);
            double unitPrice = entry.product.price;
            if (hasDiscount) unitPrice *= (1 - DISCOUNT_RATE);
            double subtotal = unitPrice * entry.quantity;
            data[i][0] = entry.product.name;
            data[i][1] = String.format("₱%.2f", unitPrice);
            data[i][2] = entry.quantity;
            data[i][3] = String.format("₱%.2f", subtotal);
            data[i][4] = "Remove";
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        JTable table = new JTable(model);
        table.getColumn("Remove").setCellRenderer(new ButtonRenderer());
        table.getColumn("Remove").setCellEditor(new ButtonEditor(new JCheckBox(), this, entries, model, cart));

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        JButton orderBtn = new JButton("Place Order");
        orderBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JPanel btnPanel = new JPanel();
        btnPanel.add(orderBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Cart Contents", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        orderBtn.addActionListener(e -> {
            dialog.dispose();
            showReceiptDialog();
        });

        dialog.setVisible(true);
    }

    private void showReceiptDialog() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Your cart is empty.",
                    "Receipt",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        boolean senior = seniorCheckbox.isSelected();
        boolean pwd = pwdCheckbox.isSelected();
        boolean hasDiscount = senior || pwd;

        boolean hasRx = cart.values().stream().anyMatch(entry -> entry.product.isRx);

        StringBuilder sb = new StringBuilder("<html><h2>Receipt</h2><table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        sb.append("<tr><th>Product</th><th>Unit Price</th><th>Quantity</th><th>Subtotal</th></tr>");

        double total = 0;
        for (CartEntry entry : cart.values()) {
            double unitPrice = entry.product.price;
            if (hasDiscount) unitPrice *= (1 - DISCOUNT_RATE);
            double subtotal = unitPrice * entry.quantity;
            total += subtotal;
            sb.append("<tr>")
                    .append("<td>").append(entry.product.name).append("</td>")
                    .append("<td>₱").append(String.format("%.2f", unitPrice)).append("</td>")
                    .append("<td>").append(entry.quantity).append("</td>")
                    .append("<td>₱").append(String.format("%.2f", subtotal)).append("</td>")
                    .append("</tr>");
        }
        sb.append("<tr><td colspan='3' style='text-align:right; font-weight:bold;'>Total</td><td style='font-weight:bold;'>₱")
                .append(String.format("%.2f", total))
                .append("</td></tr>");

        if (hasDiscount) {
            sb.append("<tr><td colspan='4' style='color: #CC0000; text-align:center;'>Discount applied: 20% off</td></tr>");
        }

        if (hasRx) {
            sb.append("<tr><td colspan='4' style='color: #CC0000; text-align:center; font-weight:bold; padding-top: 10px;'>")
              .append("Notice: For RX products, please show the doctor's prescription during payment.")
              .append("</td></tr>");
        }

        sb.append("</table></html>");

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(sb.toString());
        textPane.setEditable(false);
        textPane.setPreferredSize(new Dimension(550, 400));
        textPane.setBackground(null);
        textPane.setBorder(null);

        JOptionPane.showMessageDialog(this, new JScrollPane(textPane), "Receipt", JOptionPane.PLAIN_MESSAGE);

        cart.clear();
        updateCartStatus();
        refreshProducts();
    }

    static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        ButtonRenderer() {
            setOpaque(true);
            setText("Remove");
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.RED.darker());
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private final KioskSystem kiosk;
        private List<CartEntry> entries;
        private DefaultTableModel model;
        private Map<Product, CartEntry> cart;
        private int selectedRow;

        ButtonEditor(JCheckBox checkBox, KioskSystem kiosk, List<CartEntry> entries, DefaultTableModel model, Map<Product, CartEntry> cart) {
            super(checkBox);
            this.kiosk = kiosk;
            this.entries = entries;
            this.model = model;
            this.cart = cart;
            button = new JButton("Remove");
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setForeground(Color.RED.darker());
            button.setOpaque(true);

            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                if (selectedRow >= 0 && selectedRow < entries.size()) {
                    CartEntry entry = entries.get(selectedRow);
                    if (entry.quantity > 1) {
                        entry.quantity--;
                    } else {
                        cart.remove(entry.product);
                        entries.remove(selectedRow);
                    }
                    model.removeRow(selectedRow);
                    kiosk.updateCartStatus();
                    kiosk.refreshProducts();
                }
            }
            clicked = false;
            return "Remove";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            KioskSystem kioskSystem = new KioskSystem();
            kioskSystem.setVisible(true);
        });
    }
}

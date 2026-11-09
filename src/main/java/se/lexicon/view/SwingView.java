package se.lexicon.view;

import se.lexicon.controller.VendingController;
import se.lexicon.model.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Modern graphical <b>View</b> (dark theme, product cards, coin buttons, live balance display).
 * Like {@link ConsoleView} it contains no business rules and never touches the model: button clicks
 * are forwarded to the {@link VendingController}, which calls back the {@code show...} methods.
 * The view keeps the last products and balance it was given so it can redraw itself
 * (e.g. enable/disable buttons).
 */
public class SwingView implements VendingView {

    // ---- Colour palette ----------------------------------------------------------------------
    private static final Color BG = new Color(0x12, 0x14, 0x1C);
    private static final Color PANEL = new Color(0x1C, 0x20, 0x2B);
    private static final Color CARD = new Color(0x24, 0x29, 0x37);
    private static final Color TEXT = new Color(0xEE, 0xF1, 0xF7);
    private static final Color MUTED = new Color(0x8D, 0x96, 0xAA);
    private static final Color ACCENT = new Color(0x4F, 0x8C, 0xFF);
    private static final Color OK = new Color(0x3C, 0xD0, 0x8A);
    private static final Color ERROR = new Color(0xFF, 0x6B, 0x6B);
    private static final Color DISABLED = new Color(0x3A, 0x40, 0x50);

    private static final int[] COINS = {1, 2, 5, 10, 20, 50};

    private VendingController controller;
    /** Last data received from the controller; used to redraw the window. */
    private List<Product> products = List.of();
    private int balance;

    private final JLabel balanceLabel = new JLabel();
    private final JLabel messageLabel = new JLabel(" ");
    /** Enabled only while there is a balance to return. */
    private RoundButton returnButton;
    private final JPanel productGrid = new JPanel(new GridLayout(0, 3, 16, 16));

    /** {@inheritDoc} */
    @Override
    public void setController(VendingController controller) {
        this.controller = controller;
    }

    /** {@inheritDoc} Builds and shows the window on the Swing event thread; returns immediately. */
    @Override
    public void start() {
        SwingUtilities.invokeLater(() -> {
            buildWindow();
            controller.onShowProducts(); // ask the controller for the initial data
        });
    }

    /** {@inheritDoc} */
    @Override
    public void showProducts(List<Product> products) {
        this.products = products;
        refresh();
    }

    /** {@inheritDoc} */
    @Override
    public void showBalance(int balance) {
        this.balance = balance;
        refresh();
    }

    /** {@inheritDoc} Shown in the payment panel, coloured by type. */
    @Override
    public void showMessage(String message, MessageType type) {
        Color color = switch (type) {
            case SUCCESS -> OK;
            case ERROR -> ERROR;
            case INFO -> MUTED;
        };
        messageLabel.setForeground(color);
        messageLabel.setText("<html><div style='width:190px'>" + escape(message).replace("\n", "<br>") + "</div></html>");
    }

    /** Creates the window: title bar area, product grid on the left, payment panel on the right. */
    private void buildWindow() {
        JFrame frame = new JFrame("Lexicon Vending Machine");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        root.add(buildHeader(), BorderLayout.NORTH);

        productGrid.setOpaque(false);
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(productGrid, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(gridWrapper);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        root.add(buildPaymentPanel(), BorderLayout.EAST);

        refresh();
        frame.setContentPane(root);
        frame.setSize(1080, 640);
        frame.setMinimumSize(new Dimension(900, 520));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /** @return the title and subtitle shown at the top of the window */
    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = label("Lexicon Vending Machine", 28, Font.BOLD, TEXT);
        JLabel subtitle = label("Insert coins, then pick a product", 14, Font.PLAIN, MUTED);
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    /** @return the right-hand panel: balance display, coin buttons, return-change button, messages */
    private JComponent buildPaymentPanel() {
        RoundPanel panel = new RoundPanel(PANEL, 24);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        panel.setPreferredSize(new Dimension(300, 0));

        panel.add(left(label("BALANCE", 12, Font.BOLD, MUTED)));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 44));
        balanceLabel.setForeground(OK);
        panel.add(left(balanceLabel));
        panel.add(Box.createVerticalStrut(24));

        panel.add(left(label("INSERT COIN (kr)", 12, Font.BOLD, MUTED)));
        panel.add(Box.createVerticalStrut(10));
        JPanel coins = new JPanel(new GridLayout(2, 3, 10, 10));
        coins.setOpaque(false);
        coins.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        for (int coin : COINS) {
            RoundButton b = new RoundButton(String.valueOf(coin), CARD, 18);
            b.setFont(new Font("Segoe UI", Font.BOLD, 18));
            b.addActionListener(e -> controller.onInsertCoin(coin));
            coins.add(b);
        }
        panel.add(left(coins));
        panel.add(Box.createVerticalStrut(20));

        returnButton = new RoundButton("Return change", ACCENT, 18);
        returnButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        returnButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        returnButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        returnButton.addActionListener(e -> controller.onReturnChange());
        panel.add(returnButton);
        panel.add(Box.createVerticalStrut(20));

        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(MUTED);
        panel.add(left(messageLabel));
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    /** Redraws the balance and rebuilds all product cards from the last data the controller gave us. */
    private void refresh() {
        balanceLabel.setText(balance + " kr");
        returnButton.setEnabled(balance > 0);
        productGrid.removeAll();
        for (Product p : products) {
            productGrid.add(buildCard(p));
        }
        productGrid.revalidate();
        productGrid.repaint();
    }

    /**
     * Builds one product card: type badge, name, detail, price, stock and a Buy button
     * (disabled when sold out).
     *
     * @param p the product to show
     * @return the card component
     */
    private JComponent buildCard(Product p) {
        String type = p.getType();
        boolean inStock = p.isInStock();

        RoundPanel card = new RoundPanel(CARD, 20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel badge = label(type.toUpperCase() + "   #" + p.getId(), 11, Font.BOLD, accentFor(type));
        card.add(left(badge));
        card.add(Box.createVerticalStrut(8));
        card.add(left(label(p.getName(), 20, Font.BOLD, TEXT)));
        card.add(left(label(p.getDetail(), 13, Font.PLAIN, MUTED)));
        card.add(Box.createVerticalStrut(12));
        card.add(left(label(p.getPrice() + " kr", 24, Font.BOLD, TEXT)));
        card.add(left(label(inStock ? "In stock: " + p.getQuantity() : "Sold out", 12, Font.PLAIN,
                inStock ? MUTED : ERROR)));
        card.add(Box.createVerticalStrut(12));

        // Buy is enabled when the item is in stock AND some money has been inserted. If the balance is
        // too low, clicking it shows the machine's "Insufficient balance" message instead of buying.
        boolean canBuy = inStock && balance > 0;
        String buyText = !inStock ? "Sold out" : canBuy ? "Buy" : "Insert coin";
        RoundButton buy = new RoundButton(buyText, canBuy ? accentFor(type) : DISABLED, 14);
        buy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        buy.setEnabled(canBuy);
        buy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        buy.setAlignmentX(Component.LEFT_ALIGNMENT);
        buy.addActionListener(e -> controller.onSelectProduct(p.getId()));
        card.add(buy);
        return card;
    }

    // ---- Small helpers -----------------------------------------------------------------------

    /** @return a badge/button colour for a product type */
    private static Color accentFor(String type) {
        return switch (type) {
            case "Snack" -> new Color(0xF2, 0x99, 0x3D);
            case "Beverage" -> ACCENT;
            case "Fruit" -> new Color(0x3C, 0xB4, 0x6E);
            default -> MUTED;
        };
    }

    /** @return text with HTML special characters escaped */
    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** @return a label with the given size, weight and colour */
    private static JLabel label(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        return l;
    }

    /** @return the component left-aligned so it lines up inside a vertical BoxLayout */
    private static <T extends JComponent> T left(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    // ---- Custom-painted components -----------------------------------------------------------

    /** A panel with a rounded, filled background. */
    private static class RoundPanel extends JPanel {
        private final Color fill;
        private final int arc;

        RoundPanel(Color fill, int arc) {
            this.fill = fill;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** A flat, rounded button with a hover highlight. */
    private static class RoundButton extends JButton {
        private final Color base;
        private final int arc;
        private boolean hover;

        RoundButton(String text, Color base, int arc) {
            super(text);
            this.base = base;
            this.arc = arc;
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = isEnabled() ? base : DISABLED;
            if (!isEnabled()) {
                setForeground(new Color(0xA0, 0xA7, 0xB8)); // dimmed text on disabled buttons
            } else {
                setForeground(Color.WHITE);
            }
            if (isEnabled() && hover) {
                c = base.brighter();
            }
            if (isEnabled() && getModel().isPressed()) {
                c = base.darker();
            }
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

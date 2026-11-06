/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package oop_project;

/**
 *
 * @author rennz
 */
public class OOP_Project {

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/*
 * Basketball Court Reservation System
 * NetBeans-ready Java Swing starter project.
 *
 * Demo admin login:
 * Username: admin
 * Password: admin123
 *
 * Note: This is a local desktop prototype. For a real deployment, use a
 * database, hashed passwords, server-side access control, and secure file storage.
 */
public class BasketballReservationSystem extends JFrame {
    private static final String DATA_FILE = "basketball_reservations.dat";
    private static final String PROOF_DIR = "payment_proofs";
    private static Store store = loadStore();

    private User currentUser;
    private CardLayout cards;
    private JPanel root;

    public BasketballReservationSystem() {
        setTitle("Basketball Court Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null);
        showLogin();
    }

    // -------------------- LOGIN / SIGN UP --------------------

    private void showLogin() {
        cards = new CardLayout();
        root = new JPanel(cards);
        root.add(buildLoginPanel(), "login");
        root.add(buildSignupPanel(), "signup");
        setContentPane(root);
        cards.show(root, "login");
        revalidate();
        repaint();
    }

    private JPanel buildLoginPanel() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(new Color(20, 32, 48));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(new EmptyBorder(30, 36, 30, 36));
        box.setPreferredSize(new Dimension(390, 410));

        JLabel title = heading("COURT RESERVATION", 23);
        JLabel subtitle = new JLabel("Sign in to book your basketball court");
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        styleField(username, "Username");
        styleField(password, "Password");

        JButton login = primaryButton("LOGIN");
        JButton signup = new JButton("Create a user account");
        signup.setAlignmentX(Component.CENTER_ALIGNMENT);
        signup.setBorderPainted(false);
        signup.setContentAreaFilled(false);
        signup.setForeground(new Color(25, 105, 180));

        box.add(title);
        box.add(Box.createVerticalStrut(8));
        box.add(subtitle);
        box.add(Box.createVerticalStrut(28));
        box.add(leftLabel("Username"));
        box.add(username);
        box.add(Box.createVerticalStrut(13));
        box.add(leftLabel("Password"));
        box.add(password);
        box.add(Box.createVerticalStrut(24));
        box.add(login);
        box.add(Box.createVerticalStrut(12));
        box.add(signup);

        login.addActionListener(e -> {
            String u = username.getText().trim();
            String p = new String(password.getPassword());
            User found = store.users.get(u);
            if (found == null || !found.password.equals(p)) {
                JOptionPane.showMessageDialog(this, "Incorrect username or password.", "Login failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentUser = found;
            if (found.admin) showAdminDashboard();
            else showUserDashboard();
        });
        signup.addActionListener(e -> cards.show(root, "signup"));

        page.add(box);
        return page;
    }

    private JPanel buildSignupPanel() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(new Color(20, 32, 48));
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(new EmptyBorder(25, 36, 25, 36));
        box.setPreferredSize(new Dimension(410, 480));

        JLabel title = heading("CREATE ACCOUNT", 23);
        JTextField name = new JTextField();
        JTextField username = new JTextField();
        JTextField contact = new JTextField();
        JPasswordField password = new JPasswordField();
        styleField(name, "Full name");
        styleField(username, "Username");
        styleField(contact, "Contact number");
        styleField(password, "Password");

        JButton create = primaryButton("SIGN UP");
        JButton back = new JButton("Back to login");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setForeground(new Color(25, 105, 180));

        box.add(title);
        box.add(Box.createVerticalStrut(18));
        addField(box, "Full name", name);
        addField(box, "Username", username);
        addField(box, "Contact number", contact);
        addField(box, "Password", password);
        box.add(Box.createVerticalStrut(16));
        box.add(create);
        box.add(Box.createVerticalStrut(8));
        box.add(back);

        create.addActionListener(e -> {
            String n = name.getText().trim();
            String u = username.getText().trim();
            String c = contact.getText().trim();
            String p = new String(password.getPassword());
            if (n.isEmpty() || u.isEmpty() || c.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please complete all fields.");
                return;
            }
            if (u.equalsIgnoreCase("admin") || store.users.containsKey(u)) {
                JOptionPane.showMessageDialog(this, "That username is already taken.");
                return;
            }
            store.users.put(u, new User(n, u, c, p, false));
            saveStore();
            JOptionPane.showMessageDialog(this, "Account created. You can now log in.");
            cards.show(root, "login");
        });
        back.addActionListener(e -> cards.show(root, "login"));
        page.add(box);
        return page;
    }

    // -------------------- USER DASHBOARD --------------------

    private void showUserDashboard() {
        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBorder(new EmptyBorder(18, 20, 18, 20));
        page.setBackground(new Color(244, 247, 250));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = heading("Basketball Court", 25);
        JLabel welcome = new JLabel("Welcome, " + currentUser.fullName + "   ");
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> { currentUser = null; showLogin(); });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(welcome);
        right.add(logout);
        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Make a Reservation", buildBookingPanel());
        tabs.addTab("My Reservations", buildMyReservationsPanel());

        page.add(top, BorderLayout.NORTH);
        page.add(tabs, BorderLayout.CENTER);
        setContentPane(page);
        revalidate();
        repaint();
    }

    private JPanel buildBookingPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(new EmptyBorder(22, 25, 22, 25));

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(500, 440));

        JLabel title = heading("Reserve your time", 21);
        JLabel note = new JLabel("Choose a date and an available time slot.");
        note.setForeground(Color.GRAY);
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        styleField(dateField, "Date (yyyy-MM-dd)");
        JComboBox<String> timeBox = new JComboBox<>();
        timeBox.setFont(new Font("SansSerif", Font.PLAIN, 15));
        timeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JLabel availability = new JLabel(" ");
        availability.setForeground(new Color(25, 125, 70));
        JTextField hours = new JTextField("1");
        styleField(hours, "Duration in hours");
        JLabel rate = new JLabel("Court rate: ₱500 per hour");
        rate.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel payment = new JLabel("Payment method: GCash");
        payment.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel instructions = new JLabel("<html>Send payment to the court's official GCash number,<br/>then upload your payment screenshot below.</html>");
        instructions.setForeground(Color.DARK_GRAY);
        JTextField proofPath = new JTextField();
        proofPath.setEditable(false);
        styleField(proofPath, "No proof selected");
        JButton chooseProof = new JButton("Choose payment proof image");
        JButton reserve = primaryButton("SUBMIT RESERVATION");

        Runnable refreshSlots = () -> {
            timeBox.removeAllItems();
            String date = dateField.getText().trim();
            for (String slot : getTimeSlots()) {
                if (isSlotAvailable(date, slot)) timeBox.addItem(slot);
            }
            availability.setText(timeBox.getItemCount() == 0
                    ? "No available slots for this date." : timeBox.getItemCount() + " time slot(s) available");
        };
        refreshSlots.run();
        dateField.addActionListener(e -> refreshSlots.run());
        dateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) { refreshSlots.run(); }
        });

        final File[] selectedProof = {null};
        chooseProof.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select GCash payment screenshot");
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedProof[0] = chooser.getSelectedFile();
                proofPath.setText(selectedProof[0].getAbsolutePath());
            }
        });

        reserve.addActionListener(e -> {
            String date = dateField.getText().trim();
            String time = (String) timeBox.getSelectedItem();
            int duration;
            try {
                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) throw new Exception();
                java.time.LocalDate.parse(date);
                duration = Integer.parseInt(hours.getText().trim());
                if (duration < 1 || duration > 4) throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid date (yyyy-MM-dd) and duration from 1 to 4 hours.");
                return;
            }
            if (time == null) {
                JOptionPane.showMessageDialog(this, "There are no available time slots on this date.");
                return;
            }
            if (selectedProof[0] == null || !selectedProof[0].isFile()) {
                JOptionPane.showMessageDialog(this, "Please upload your GCash payment proof first.");
                return;
            }
            if (!isRangeAvailable(date, time, duration)) {
                JOptionPane.showMessageDialog(this, "That time overlaps with an existing reservation. Please choose another slot or duration.");
                refreshSlots.run();
                return;
            }
            try {
                Files.createDirectories(Paths.get(PROOF_DIR));
                String ext = getExtension(selectedProof[0].getName());
                String proofName = "proof_" + System.currentTimeMillis() + "." + ext;
                Path destination = Paths.get(PROOF_DIR, proofName);
                Files.copy(selectedProof[0].toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                Reservation r = new Reservation();
                r.id = store.nextReservationId++;
                r.username = currentUser.username;
                r.fullName = currentUser.fullName;
                r.contact = currentUser.contact;
                r.date = date;
                r.time = time;
                r.duration = duration;
                r.amount = duration * 500;
                r.proofPath = destination.toString();
                r.status = "Pending approval";
                store.reservations.add(r);
                saveStore();
                JOptionPane.showMessageDialog(this, "Reservation submitted!\nYour selected time is now unavailable while the admin checks your payment.");
                showUserDashboard();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not save the payment proof: " + ex.getMessage());
            }
        });

        form.add(title);
        form.add(Box.createVerticalStrut(8));
        form.add(note);
        form.add(Box.createVerticalStrut(20));
        addField(form, "Reservation date", dateField);
        addField(form, "Available time slots", timeBox);
        form.add(availability);
        form.add(Box.createVerticalStrut(12));
        addField(form, "Duration (1–4 hours)", hours);
        form.add(Box.createVerticalStrut(6));
        form.add(rate);
        form.add(Box.createVerticalStrut(12));
        form.add(payment);
        form.add(Box.createVerticalStrut(8));
        form.add(instructions);
        form.add(Box.createVerticalStrut(10));
        form.add(proofPath);
        form.add(Box.createVerticalStrut(8));
        form.add(chooseProof);
        form.add(Box.createVerticalStrut(15));
        form.add(reserve);
        outer.add(form);
        return outer;
    }

    private JPanel buildMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        String[] columns = {"ID", "Date", "Time", "Hours", "Amount", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Reservation r : store.reservations) {
            if (r.username.equals(currentUser.username)) {
                model.addRow(new Object[]{r.id, r.date, r.time, r.duration, peso(r.amount), r.status});
            }
        }
        JTable table = new JTable(model);
        table.setRowHeight(27);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel note = new JLabel("Pending reservations also block that time slot until the admin rejects them.");
        note.setForeground(Color.GRAY);
        panel.add(note, BorderLayout.SOUTH);
        return panel;
    }

    // -------------------- ADMIN DASHBOARD --------------------

    private void showAdminDashboard() {
        JPanel page = new JPanel(new BorderLayout(12, 12));
        page.setBorder(new EmptyBorder(18, 20, 18, 20));
        page.setBackground(new Color(244, 247, 250));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(heading("ADMIN DASHBOARD", 25), BorderLayout.WEST);
        JButton logout = new JButton("Log out");
        logout.addActionListener(e -> { currentUser = null; showLogin(); });
        top.add(logout, BorderLayout.EAST);

        String[] columns = {"ID", "Customer", "Contact", "Date", "Time", "Hours", "Amount", "Payment Proof", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Reservation r : store.reservations) {
            model.addRow(new Object[]{r.id, r.fullName, r.contact, r.date, r.time, r.duration, peso(r.amount), r.proofPath, r.status});
        }
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {45, 140, 105, 95, 100, 55, 85, 250, 120};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(Color.WHITE);
        JButton viewProof = new JButton("View Payment Proof");
        JButton approve = primaryButton("Approve Reservation");
        JButton reject = new JButton("Reject / Release Slot");
        JButton refresh = new JButton("Refresh");
        JLabel hint = new JLabel("Select a reservation row first.");
        actions.add(viewProof);
        actions.add(approve);
        actions.add(reject);
        actions.add(refresh);
        actions.add(hint);

        Runnable refreshAction = () -> showAdminDashboard();
        refresh.addActionListener(e -> refreshAction.run());

        viewProof.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a reservation first."); return; }
            Reservation r = findReservation((int) model.getValueAt(row, 0));
            if (r == null) return;
            File file = new File(r.proofPath);
            if (!file.exists()) { JOptionPane.showMessageDialog(this, "Payment proof file not found:\n" + r.proofPath); return; }
            showImagePreview(file);
        });

        approve.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a reservation first."); return; }
            Reservation r = findReservation((int) model.getValueAt(row, 0));
            if (r == null) return;
            if (r.status.equals("Rejected")) {
                JOptionPane.showMessageDialog(this, "Rejected reservations cannot be approved. Ask the user to book again.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Approve reservation #" + r.id + " after checking the GCash proof?", "Confirm approval", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                r.status = "Approved";
                saveStore();
                showAdminDashboard();
            }
        });

        reject.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a reservation first."); return; }
            Reservation r = findReservation((int) model.getValueAt(row, 0));
            if (r == null) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Reject reservation #" + r.id + " and make its time available again?", "Confirm rejection", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                r.status = "Rejected";
                saveStore();
                showAdminDashboard();
            }
        });

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBackground(Color.WHITE);
        center.setBorder(new EmptyBorder(10, 10, 10, 10));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);
        page.add(top, BorderLayout.NORTH);
        page.add(center, BorderLayout.CENTER);
        setContentPane(page);
        revalidate();
        repaint();
    }

    private void showImagePreview(File file) {
        try {
            ImageIcon original = new ImageIcon(file.getAbsolutePath());
            Image image = original.getImage();
            int maxW = 750, maxH = 550;
            double scale = Math.min((double) maxW / original.getIconWidth(), (double) maxH / original.getIconHeight());
            if (scale > 1) scale = 1;
            Image scaled = image.getScaledInstance(Math.max(1, (int)(original.getIconWidth() * scale)),
                    Math.max(1, (int)(original.getIconHeight() * scale)), Image.SCALE_SMOOTH);
            JOptionPane.showMessageDialog(this, new JLabel(new ImageIcon(scaled)), "Payment Proof: " + file.getName(), JOptionPane.PLAIN_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not display image: " + ex.getMessage());
        }
    }

    // -------------------- HELPERS / DATA --------------------

    private static String[] getTimeSlots() {
        return new String[]{"8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM",
                "11:00 AM - 12:00 PM", "1:00 PM - 2:00 PM", "2:00 PM - 3:00 PM",
                "3:00 PM - 4:00 PM", "4:00 PM - 5:00 PM", "5:00 PM - 6:00 PM",
                "6:00 PM - 7:00 PM", "7:00 PM - 8:00 PM", "8:00 PM - 9:00 PM"};
    }

    private static boolean isSlotAvailable(String date, String time) {
        return isRangeAvailable(date, time, 1);
    }

    private static boolean isRangeAvailable(String date, String time, int duration) {
        if (date == null || time == null) return false;
        int requestedStart = Arrays.asList(getTimeSlots()).indexOf(time);
        if (requestedStart < 0 || requestedStart + duration > getTimeSlots().length) return false;
        for (Reservation r : store.reservations) {
            // Pending and approved reservations block every hour in their duration.
            // Rejected reservations release the slot.
            if (r.date.equals(date) && !r.status.equals("Rejected")) {
                int existingStart = Arrays.asList(getTimeSlots()).indexOf(r.time);
                if (existingStart >= 0) {
                    int existingEnd = existingStart + Math.max(1, r.duration);
                    int requestedEnd = requestedStart + duration;
                    if (requestedStart < existingEnd && existingStart < requestedEnd) return false;
                }
            }
        }
        return true;
    }

    private static Reservation findReservation(int id) {
        for (Reservation r : store.reservations) if (r.id == id) return r;
        return null;
    }

    private static String[] getTimeSlotsSafe() { return getTimeSlots(); }

    private static Store loadStore() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                Store loaded = (Store) in.readObject();
                if (!loaded.users.containsKey("admin")) loaded.users.put("admin", new User("Administrator", "admin", "-", "admin123", true));
                return loaded;
            } catch (Exception e) {
                System.err.println("Could not load saved data; starting fresh: " + e.getMessage());
            }
        }
        Store fresh = new Store();
        fresh.users.put("admin", new User("Administrator", "admin", "-", "admin123", true));
        return fresh;
    }

    private static void saveStore() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(store);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not save application data: " + e.getMessage());
        }
    }

    private static String getExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot < 0) return "img";
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String peso(int amount) { return "₱" + amount; }

    private JLabel heading(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        label.setForeground(new Color(25, 45, 68));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel leftLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }

    private void styleField(JComponent field, String tooltip) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(300, 38));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setToolTipText(tooltip);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void addField(JPanel panel, String label, JComponent field) {
        JLabel l = leftLabel(label);
        panel.add(l);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(11));
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(25, 105, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setPreferredSize(new Dimension(300, 42));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private static class User implements Serializable {
        private static final long serialVersionUID = 1L;
        String fullName, username, contact, password;
        boolean admin;
        User(String fullName, String username, String contact, String password, boolean admin) {
            this.fullName = fullName; this.username = username; this.contact = contact;
            this.password = password; this.admin = admin;
        }
    }

    private static class Reservation implements Serializable {
        private static final long serialVersionUID = 1L;
        int id, duration, amount;
        String username, fullName, contact, date, time, proofPath, status;
    }

    private static class Store implements Serializable {
        private static final long serialVersionUID = 1L;
        Map<String, User> users = new LinkedHashMap<>();
        List<Reservation> reservations = new ArrayList<>();
        int nextReservationId = 1001;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new BasketballReservationSystem().setVisible(true));
    }
}

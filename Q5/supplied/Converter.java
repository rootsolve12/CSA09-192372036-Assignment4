import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Converter extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target =
            new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter() {
        super("Currency Converter");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR")); add(amount); add(target);
        add(convertButton); add(result);
        ActionListener handler = e -> convert();
        convertButton.addActionListener(handler);
        amount.addActionListener(handler);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    private void convert() {
        try {
            double value = Double.parseDouble(amount.getText().trim());
            if (!Double.isFinite(value) || value < 0) throw new NumberFormatException();
            result.setText(String.format(java.util.Locale.ROOT, "%.2f", value * rate((String) target.getSelectedItem())));
        } catch (NumberFormatException e) {
            result.setText("Invalid amount");
        }
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter().setVisible(true));
    }
}

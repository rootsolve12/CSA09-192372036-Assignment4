import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
public class LambdaConverter extends JFrame {
 final JTextField amount=new JTextField(10);
 final JComboBox<String> from=new JComboBox<>(new String[]{"INR","USD","EUR"}),to=new JComboBox<>(new String[]{"USD","EUR","INR"});
 final JLabel result=new JLabel("Enter an amount");
 public LambdaConverter() {
  super("Q5 Lambda before");setLayout(new FlowLayout());
  JButton convertButton=new JButton("Convert");add(amount);add(from);add(to);add(convertButton);add(result);
  ActionListener handler=e->convert();
  convertButton.addActionListener(handler);
  setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e){dispose();}});pack();setMinimumSize(new Dimension(720,190));setSize(720,190);setLocationByPlatform(true);
 }
 private static double rate(String currency){return switch(currency){case "USD"->0.012;case "EUR"->0.011;default->1.0;};}
 static double convertValue(double value,String source,String target) {
  if(!Double.isFinite(value)||value<0)throw new NumberFormatException("Invalid amount");
  // Form the small rate ratio first, avoiding overflow in an intermediate INR value.
  double converted=value*(rate(target)/rate(source));
  if(!Double.isFinite(converted))throw new NumberFormatException("Converted amount is out of range");
  return converted;
 }
 void convert() {
  String message;
  try{double value=Double.parseDouble(amount.getText().trim());double converted=convertValue(value,(String)from.getSelectedItem(),(String)to.getSelectedItem());message=String.format(Locale.ROOT,"%.2f %s",converted,to.getSelectedItem());}
  catch(NumberFormatException ex){message="Invalid amount";}
  result.setText(message);
 }
 public static void main(String[] args){SwingUtilities.invokeLater(()->new LambdaConverter().setVisible(true));}
}

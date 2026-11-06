import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
public class LambdaConverter extends JFrame {
 final JTextField amount=new JTextField(10);
 final JComboBox<String> from=new JComboBox<>(new String[]{"INR","USD","EUR"}),to=new JComboBox<>(new String[]{"USD","EUR","INR"});
 final JLabel result=new JLabel("Enter an amount");
 public LambdaConverter() {
  super("Q5 Lambda after");setLayout(new FlowLayout());
  JButton convertButton=new JButton("Convert");add(amount);add(from);add(to);add(convertButton);add(result);
  ActionListener handler=e->convert();
  convertButton.addActionListener(handler);
  amount.addActionListener(handler);
  setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e){dispose();}});pack();setMinimumSize(new Dimension(720,190));setSize(720,190);setLocationByPlatform(true);
 }
 private static double rate(String currency){return switch(currency){case "USD"->0.012;case "EUR"->0.011;default->1.0;};}
 void convert() {
  String message;
  try{double value=Double.parseDouble(amount.getText().trim());if(!Double.isFinite(value)||value<0)throw new NumberFormatException();message=String.format(Locale.ROOT,"%.2f %s",value/rate((String)from.getSelectedItem())*rate((String)to.getSelectedItem()),to.getSelectedItem());}
  catch(NumberFormatException ex){message="Invalid amount";}
  result.setText(message);
  JOptionPane.showMessageDialog(this,message,"Conversion",JOptionPane.INFORMATION_MESSAGE);
 }
 public static void main(String[] args){SwingUtilities.invokeLater(()->new LambdaConverter().setVisible(true));}
}

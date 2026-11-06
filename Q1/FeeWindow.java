import javax.swing.*;
import java.awt.*;
public class FeeWindow extends JFrame {
 final JTextField regNo=new JTextField(12),amount=new JTextField(10);
 final JButton pay=new JButton("Pay"),clear=new JButton("Clear"),receipt=new JButton("Receipt");
 final JLabel status=new JLabel("Enter register number and amount");
 private final FeeService service;private FeeService.Payment last;private boolean busy;
 public FeeWindow(FeeService service) {
  super("Q1 Fee Payment");this.service=service;setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  add(Ui.row(new JLabel("Register"),regNo,new JLabel("INR"),amount),BorderLayout.NORTH);
  add(Ui.row(pay,clear,receipt),BorderLayout.CENTER);add(status,BorderLayout.SOUTH);
  pay.addActionListener(e->pay());amount.addActionListener(e->pay());
  clear.addActionListener(e->{regNo.setText("");amount.setText("");status.setText("Cleared");});
  receipt.addActionListener(e->JOptionPane.showMessageDialog(this,last==null?"No completed payment":last.toString(),"Receipt",JOptionPane.INFORMATION_MESSAGE));
  Ui.close(this,()->{if(!busy)dispose();else status.setText("Payment is in progress; wait for its result");});pack();setMinimumSize(new Dimension(720,180));setSize(720,180);setLocationByPlatform(true);
 }
 void pay() {
  if(busy)return;String register=regNo.getText().trim(),text=amount.getText();
  try{FeeService.validate(register,text);}catch(IllegalArgumentException ex){status.setText(ex.getMessage());return;}
  busy=true;pay.setEnabled(false);clear.setEnabled(false);regNo.setEnabled(false);amount.setEnabled(false);status.setText("Saving payment...");
  Ui.work(()->service.pay(register,text),payment->{last=payment;finish("Paid INR "+payment.amount()+"; receipt available");},ex->finish("Payment failed: "+ex.getMessage()));
 }
 void finish(String message){busy=false;pay.setEnabled(true);clear.setEnabled(true);regNo.setEnabled(true);amount.setEnabled(true);status.setText(message);}
 public static void main(String[] args){Ui.launch(()->new FeeWindow(new FeeService()).setVisible(true));}
}

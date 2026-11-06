import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class AppointmentDesk extends JFrame {
 final JTextField patient=new JTextField(12);
 final JComboBox<String> department=new JComboBox<>(new String[]{"Cardiology","Orthopaedics","General"});
 final JComboBox<String> doctor=new JComboBox<>(),slot=new JComboBox<>(new String[]{"09:00","09:30","10:00"});
 final JButton book=new JButton("Book appointment");final JLabel status=new JLabel("Ready");
 final Map<String,java.util.List<String>> doctors=Map.of("Cardiology",java.util.List.of("Dr Anitha","Dr Ravi"),"Orthopaedics",java.util.List.of("Dr Kumar"),"General",java.util.List.of("Dr Meena","Dr Priya"));
 final Set<String> occupied=new HashSet<>();boolean dirty,busy;
 public AppointmentDesk() {
  super("Q2 Outpatient Appointment Desk");setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  JPanel form=new JPanel(new GridLayout(4,2,10,10));
  form.add(new JLabel("Patient ID"));form.add(patient);form.add(new JLabel("Department"));form.add(department);form.add(new JLabel("Doctor"));form.add(doctor);form.add(new JLabel("Time slot (today)"));form.add(slot);
  add(form,BorderLayout.CENTER);JPanel footer=new JPanel(new BorderLayout());footer.add(Ui.row(book),BorderLayout.NORTH);footer.add(status,BorderLayout.SOUTH);add(footer,BorderLayout.SOUTH);
  department.addActionListener(e->{refreshDoctors();dirty=true;});doctor.addActionListener(e->dirty=true);slot.addActionListener(e->dirty=true);
  patient.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){public void insertUpdate(javax.swing.event.DocumentEvent e){dirty=true;}public void removeUpdate(javax.swing.event.DocumentEvent e){dirty=true;}public void changedUpdate(javax.swing.event.DocumentEvent e){dirty=true;}});
  book.addActionListener(e->book());
  addWindowListener(new WindowAdapter(){
   @Override public void windowOpened(WindowEvent e){System.out.println("windowOpened");}
   @Override public void windowActivated(WindowEvent e){System.out.println("windowActivated");}
   @Override public void windowClosing(WindowEvent e){System.out.println("windowClosing");if(busy){status.setText("Wait for booking to complete");return;}if(!dirty||JOptionPane.showConfirmDialog(AppointmentDesk.this,"Discard the booking in progress?","Close",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)dispose();}
   @Override public void windowDeactivated(WindowEvent e){System.out.println("windowDeactivated");}
   @Override public void windowClosed(WindowEvent e){System.out.println("windowClosed");}
  });refreshDoctors();dirty=false;pack();setSize(620,330);setLocationByPlatform(true);
 }
 void refreshDoctors(){doctor.removeAllItems();for(String d:doctors.getOrDefault((String)department.getSelectedItem(),java.util.List.of()))doctor.addItem(d);doctor.setEnabled(doctor.getItemCount()>0);}
 void book(){
  if(busy)return;String id=patient.getText().trim();
  if(!id.matches("P[0-9]{4}")){status.setText("Patient ID must be P followed by 4 digits");return;}
  if(doctor.getSelectedItem()==null){status.setText("Choose a doctor");return;}
  String key=doctor.getSelectedItem()+" "+slot.getSelectedItem();
  if(!occupied.add(key)){status.setText("That doctor and slot are already booked");return;}
  dirty=false;status.setText("Booked "+id+" with "+key);System.out.println(status.getText());
 }
 public static void main(String[] args){Ui.launch(()->new AppointmentDesk().setVisible(true));}
}

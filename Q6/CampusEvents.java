import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
public class CampusEvents extends JFrame {
 final RegistrationService service;final CardLayout cards=new CardLayout();final JPanel screens=new JPanel(cards);
 final JTextField student=new JTextField(15),name=new JTextField(20);
 final JComboBox<String> event=new JComboBox<>(new String[]{"Music","Dance","Drama"}),filter=new JComboBox<>(new String[]{"All events","Music","Dance","Drama"});
 final JButton register=new JButton("Register"),load=new JButton("Filter / Reload"),prev=new JButton("Prev"),next=new JButton("Next"),export=new JButton("Export CSV");
 final JLabel status=new JLabel("Ready"),heartbeat=new JLabel("EDT ticks: 0");
 final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Student ID","Name","Event"},0){@Override public boolean isCellEditable(int r,int c){return false;}};
 final JTable table=new JTable(model);final Timer timer;int offset,selectedFilter,ticks;boolean busy,hasNext;
 static final int PAGE_SIZE=50;
 public CampusEvents(RegistrationService service){
  super("Q6 Campus Event Registration");this.service=service;setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  JMenuBar bar=new JMenuBar();JMenu view=new JMenu("View"),file=new JMenu("File");
  JMenuItem form=new JMenuItem("Registration form"),list=new JMenuItem("Committee table"),save=new JMenuItem("Export selected filter"),exit=new JMenuItem("Exit");
  form.addActionListener(e->cards.show(screens,"form"));list.addActionListener(e->{cards.show(screens,"list");if(!busy)reload(0,filter.getSelectedIndex());});
  save.addActionListener(e->export());exit.addActionListener(e->close());view.add(form);view.add(list);file.add(save);file.add(exit);bar.add(file);bar.add(view);setJMenuBar(bar);
  JPanel fields=new JPanel(new GridLayout(3,2,10,14));fields.add(new JLabel("Student ID"));fields.add(student);fields.add(new JLabel("Student name"));fields.add(name);fields.add(new JLabel("Event"));fields.add(event);
  JPanel registration=new JPanel(new BorderLayout());registration.add(fields,BorderLayout.NORTH);registration.add(Ui.row(register,new JLabel("One registration per student per event")),BorderLayout.CENTER);
  JPanel committee=new JPanel(new BorderLayout());committee.add(Ui.row(new JLabel("Event filter"),filter,load),BorderLayout.NORTH);committee.add(new JScrollPane(table),BorderLayout.CENTER);committee.add(Ui.row(prev,next,export),BorderLayout.SOUTH);
  screens.add(registration,"form");screens.add(committee,"list");add(screens,BorderLayout.CENTER);add(Ui.row(status,heartbeat),BorderLayout.SOUTH);
  register.addActionListener(e->register());load.addActionListener(e->reload(0,filter.getSelectedIndex()));prev.addActionListener(e->reload(Math.max(0,offset-PAGE_SIZE),selectedFilter));next.addActionListener(e->reload(offset+PAGE_SIZE,selectedFilter));export.addActionListener(e->export());Ui.close(this,this::close);
  timer=new Timer(100,e->{ticks++;heartbeat.setText("EDT ticks: "+ticks);});timer.start();setSize(900,520);setMinimumSize(new Dimension(760,400));setLocationByPlatform(true);updateControls();
 }
 void close(){if(busy){status.setText("Operation running; close after completion");return;}timer.stop();dispose();}
 void updateControls(){register.setEnabled(!busy);load.setEnabled(!busy);filter.setEnabled(!busy);prev.setEnabled(!busy&&offset>0);next.setEnabled(!busy&&hasNext);export.setEnabled(!busy);}
 void begin(String text){busy=true;status.setText(text);updateControls();}
 void finish(String text){busy=false;status.setText(text);updateControls();}
 void failure(Exception ex){finish("Database/file error: "+ex.getMessage());System.err.println(ex);}
 void register(){
  if(busy)return;String id=student.getText().trim(),person=name.getText().trim();int ev=event.getSelectedIndex()+1;
  try{RegistrationService.validate(id,person,ev);}catch(IllegalArgumentException ex){status.setText(ex.getMessage());return;}
  begin("Registering...");Ui.work(()->service.register(id,person,ev),this::finish,this::failure);
 }
 void reload(int newOffset,int ev){
  if(busy)return;begin("Loading registrations...");
  Ui.work(()->service.page(ev,newOffset,PAGE_SIZE+1),rows->{
   offset=newOffset;selectedFilter=ev;hasNext=rows.size()>PAGE_SIZE;model.setRowCount(0);
   for(Registration r:rows.subList(0,Math.min(PAGE_SIZE,rows.size())))model.addRow(new Object[]{r.id(),r.studentId(),r.name(),r.event()});
   finish("Page "+(offset/PAGE_SIZE+1)+"; "+model.getRowCount()+" displayed rows");
  },this::failure);
 }
 void export(){
  if(busy)return;JFileChooser chooser=new JFileChooser();chooser.setSelectedFile(new java.io.File("registrations.csv"));
  if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
  Path target=chooser.getSelectedFile().toPath();
  if(java.nio.file.Files.exists(target)&&JOptionPane.showConfirmDialog(this,"Replace existing file?","Export",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
  int ev=filter.getSelectedIndex();begin("Exporting selected filter...");Ui.work(()->service.export(ev,target),count->finish("Exported "+count+" rows"),this::failure);
 }
 public static void main(String[] args){Ui.launch(()->new CampusEvents(new RegistrationService(new JdbcRegistrationDao())).setVisible(true));}
}

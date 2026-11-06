import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
public class GuiEvidence {
 static Robot robot;static PrintWriter log;
 static <T>T edt(Callable<T> c)throws Exception{FutureTask<T> f=new FutureTask<>(c);EventQueue.invokeAndWait(f);return f.get();}
 static void act(Runnable r)throws Exception{edt(()->{r.run();return null;});}
 static void check(boolean ok,String text){if(!ok)throw new AssertionError(text);log.println("PASS: "+text);log.flush();}
 static void pause(){robot.delay(350);}
 static void capture(Window w,String path)throws Exception{pause();Rectangle b=edt(w::getBounds);ImageIO.write(robot.createScreenCapture(b),"png",Path.of(path).toFile());log.println("SCREENSHOT "+path);log.flush();}
 static void show(Window w)throws Exception{act(()->{w.setLocation(120,100);w.setVisible(true);w.toFront();});pause();}
 static void waitUntil(Callable<Boolean> condition)throws Exception{long end=System.nanoTime()+TimeUnit.SECONDS.toNanos(20);while(!edt(condition)){if(System.nanoTime()>end)throw new AssertionError("Async operation timed out");robot.delay(50);}pause();}
 static void click(Frame w,int x,int y)throws Exception{Point p=edt(w::getLocationOnScreen);robot.mouseMove(p.x+x,p.y+y);robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);pause();}
 static void minimize(Frame w)throws Exception{act(()->w.setState(Frame.ICONIFIED));robot.delay(500);act(()->{w.setState(Frame.NORMAL);w.toFront();});pause();}
 static void startLog(String q)throws Exception{log=new PrintWriter(Files.newBufferedWriter(Path.of(q,"results/gui-tests.txt")));log.println("Actual GUI execution: OpenJDK "+System.getProperty("java.version")+"; "+java.time.Instant.now());}
 static void q1()throws Exception{
  startLog("Q1");FeeWindow w=edt(()->new FeeWindow(new FeeService()));show(w);
  act(()->{w.regNo.setText("192372036");w.amount.setText("abc");w.pay.doClick();});check(edt(()->w.status.getText().startsWith("Amount must")),"Invalid amount rejected before database access");capture(w,"Q1/screenshots/invalid.png");
  act(()->{w.amount.setText("1500.00");w.pay.doClick();});waitUntil(()->w.pay.isEnabled());check(edt(()->w.status.getText().startsWith("Paid INR 1500.00")),"Valid payment saved with receipt");capture(w,"Q1/screenshots/paid.png");
  act(w::dispose);log.close();
 }
 static void q2()throws Exception{
  startLog("Q2");AppointmentDesk w=edt(AppointmentDesk::new);show(w);
  act(()->w.department.setSelectedItem("Orthopaedics"));check(edt(()->w.doctor.getItemCount()==1&&w.doctor.getItemAt(0).equals("Dr Kumar")),"Department event refreshes dependent doctor list");
  act(()->{w.patient.setText("bad");w.book.doClick();});check(edt(()->w.status.getText().startsWith("Patient ID must")),"Invalid patient rejected");capture(w,"Q2/screenshots/invalid.png");
  act(()->{w.patient.setText("P1001");w.book.doClick();});check(edt(()->w.status.getText().startsWith("Booked P1001")),"Valid appointment confirmed");capture(w,"Q2/screenshots/booked.png");
  act(()->w.book.doClick());check(edt(()->w.status.getText().contains("already booked")),"Occupied doctor slot rejected");capture(w,"Q2/screenshots/occupied.png");
  act(()->w.patient.setText("P1002"));
  act(()->{javax.swing.Timer t=new javax.swing.Timer(700,e->{for(Window x:Window.getWindows())if(x instanceof JDialog dialog&&x.isVisible()){try{ImageIO.write(robot.createScreenCapture(w.getBounds().union(x.getBounds())),"png",Path.of("Q2/screenshots/close-confirmation.png").toFile());}catch(Exception ex){throw new RuntimeException(ex);}for(Component child:dialog.getContentPane().getComponents())if(child instanceof JOptionPane pane)pane.setValue(JOptionPane.NO_OPTION);}});t.setRepeats(false);t.start();});
  act(()->w.dispatchEvent(new WindowEvent(w,WindowEvent.WINDOW_CLOSING)));check(edt(w::isDisplayable),"No at close confirmation retains unfinished booking");
  act(w::dispose);log.close();
 }
 static void q3()throws Exception{
  startLog("Q3");LibrarySearch w=edt(LibrarySearch::new);show(w);int oldH=0,oldResults=0,oldSearch=0,oldButtons=0;
  for(int[] size:new int[][]{{650,350},{850,550},{1100,750}}){act(()->w.setSize(size[0],size[1]));pause();int h=edt(()->w.getHeight()),rh=edt(()->w.scroll.getHeight()),sh=edt(()->w.searchRow.getHeight()),bh=edt(()->w.buttonRow.getHeight());
   log.println("SIZE "+size[0]+"x"+size[1]+" results_height="+rh+" search_height="+sh+" buttons_height="+bh);
   if(oldH!=0)check(rh-oldResults==h-oldH&&sh==oldSearch&&bh==oldButtons,"Results absorb height increase; control row heights remain fixed");
   oldH=h;oldResults=rh;oldSearch=sh;oldButtons=bh;capture(w,"Q3/screenshots/size-"+size[0]+"x"+size[1]+".png");
  }
  act(()->{w.category.setSelectedItem("Computing");w.search.doClick();});check(edt(()->w.matches.size()==8),"Category filter returns 8 computing books");act(()->{w.results.setSelectedIndex(0);w.reserve.doClick();});check(edt(()->w.status.getText().startsWith("Reserved:")),"Selected result can be reserved");capture(w,"Q3/screenshots/reserved.png");act(w::dispose);log.close();
 }
 static void q4()throws Exception{
  startLog("Q4");DrawPad w=edt(DrawPad::new);show(w);click(w,100,100);click(w,200,130);click(w,280,180);capture(w,"Q4/screenshots/before-minimize.png");minimize(w);capture(w,"Q4/screenshots/after-restore.png");
  Field f=DrawPad.class.getDeclaredField("points");f.setAccessible(true);check(edt(()->((java.util.List<?>)f.get(w)).size()==3),"Three mouse events stored three points");Point p=edt(w::getLocationOnScreen);check((robot.getPixelColor(p.x+100,p.y+100).getRGB()&0xffffff)==0,"Stored dot survives real minimize and restore");act(w::dispose);
  ImmediatePad bad=edt(ImmediatePad::new);show(bad);click(bad,100,100);capture(bad,"Q4/screenshots/immediate-before.png");minimize(bad);capture(bad,"Q4/screenshots/immediate-after.png");Point bp=edt(bad::getLocationOnScreen);check((robot.getPixelColor(bp.x+100,bp.y+100).getRGB()&0xffffff)!=0,"Immediate-mode dot disappears on restore");act(bad::dispose);
  try(URLClassLoader loader=new URLClassLoader(new URL[]{Path.of("build/Q4/NoOverride").toUri().toURL()},ClassLoader.getPlatformClassLoader())){
   Frame wrong=edt(()->(Frame)loader.loadClass("DrawPad").getConstructor().newInstance());show(wrong);click(wrong,100,100);Field s=wrong.getClass().getDeclaredField("status");s.setAccessible(true);check(edt(()->((Label)s.get(wrong)).getText().equals("Clicks: 0")),"Misspelled method without Override compiles but receives no click callback");capture(wrong,"Q4/screenshots/misspelled.png");act(wrong::dispose);
  }log.close();
 }
 static Object field(Object w,String name)throws Exception{Field f=w.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(w);}
 static void q5(String version)throws Exception{
  startLog("Q5");for(String style:new String[]{"Frame","Named","Anonymous","Lambda"}){
   JFrame w=edt(()->(JFrame)Class.forName(style+"Converter").getConstructor().newInstance());show(w);JTextField amount=(JTextField)field(w,"amount");JLabel result=(JLabel)field(w,"result");
   if(version.equals("after"))dialogTimer(w,"Q5/screenshots/"+style+"-valid-dialog.png");
   act(()->{amount.setText("1000");if(version.equals("after"))amount.postActionEvent();else for(Component c:w.getContentPane().getComponents())if(c instanceof JButton b)b.doClick();});
   check(edt(()->result.getText().equals("12.00 USD")),style+" "+version+" valid conversion "+(version.equals("after")?"by Enter with dialog":"by button"));
   if(version.equals("before"))capture(w,"Q5/screenshots/"+style+"-before.png");
   if(version.equals("after")){dialogTimer(w,"Q5/screenshots/"+style+"-invalid-dialog.png");act(()->{amount.setText("abc");amount.postActionEvent();});check(edt(()->result.getText().equals("Invalid amount")),style+" invalid amount feedback");}
   act(w::dispose);
  }log.close();
 }
 static void dialogTimer(JFrame owner,String file)throws Exception{
  act(()->{javax.swing.Timer timer=new javax.swing.Timer(700,e->{try{
   for(Window x:Window.getWindows())if(x instanceof JDialog&&x.isVisible()){Rectangle b=owner.getBounds().union(x.getBounds());ImageIO.write(robot.createScreenCapture(b),"png",Path.of(file).toFile());x.dispose();}
  }catch(Exception ex){throw new RuntimeException(ex);}});timer.setRepeats(false);timer.start();});
 }
 static void q6()throws Exception{
  startLog("Q6");RegistrationService service=new RegistrationService(new JdbcRegistrationDao());CampusEvents w=edt(()->new CampusEvents(service));show(w);
  act(()->{w.student.setText("bad");w.name.setText("B Hitesh Roy");w.register.doClick();});check(edt(()->w.status.getText().contains("9 digits")),"Invalid ID rejected");capture(w,"Q6/screenshots/invalid.png");
  String id="1"+String.format("%08d",Math.floorMod(System.nanoTime(),100000000));log.println("Fixture student_id="+id);
  int before=edt(()->w.ticks);act(()->{w.student.setText(id);w.register.doClick();});waitUntil(()->!w.busy);check(edt(()->w.status.getText().startsWith("Registered ")),"Normal registration stored in MySQL");check(edt(()->w.ticks)-before>=5,"EDT timer advances during injected 1200ms database latency");capture(w,"Q6/screenshots/normal.png");
  act(()->w.register.doClick());waitUntil(()->!w.busy);check(edt(()->w.status.getText().equals("Already registered for this event")),"Database unique key rejects duplicate registration");capture(w,"Q6/screenshots/duplicate.png");
  act(()->{w.cards.show(w.screens,"list");w.filter.setSelectedIndex(1);w.reload(0,1);});waitUntil(()->!w.busy);check(edt(()->w.model.getRowCount()>=1),"Music filter loads persisted registrations");capture(w,"Q6/screenshots/filter.png");
  long exported=service.export(1,Path.of("Q6/results/registrations.csv"));check(exported>=1,"Streaming CSV export contains selected Music registrations");
  ExecutorService pool=Executors.newFixedThreadPool(2);try{
   Future<String> a=pool.submit(()->service.register(id,"B Hitesh Roy",3)),b=pool.submit(()->service.register(id,"B Hitesh Roy",3));java.util.List<String> values=java.util.List.of(a.get(),b.get());check(values.stream().filter(x->x.startsWith("Registered ")).count()==1&&values.stream().filter(x->x.startsWith("Already")).count()==1,"Concurrent submissions yield exactly one insert and one duplicate");
  }finally{pool.shutdown();}
  act(w::dispose);log.close();
 }
 public static void main(String[] args)throws Exception{
  robot=new Robot();robot.setAutoDelay(60);
  try{switch(args[0]){case "Q1"->q1();case "Q2"->q2();case "Q3"->q3();case "Q4"->q4();case "Q5"->q5(args[1]);case "Q6"->q6();}System.out.println("GUI tests passed: "+Arrays.toString(args));}
  catch(Throwable e){if(log!=null){e.printStackTrace(log);log.flush();}throw e;}finally{for(Window w:Window.getWindows())w.dispose();}System.exit(0);
 }
}

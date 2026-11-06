import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.sql.SQLException;
public class EdgeCaseChecks {
 static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 static void invalid(Method m,double value,String source,String target)throws Exception {
  try{m.invoke(null,value,source,target);throw new AssertionError("Accepted invalid conversion");}
  catch(InvocationTargetException e){check(e.getCause() instanceof NumberFormatException,"Wrong conversion exception: "+e.getCause());}
 }
 static void converters()throws Exception {
  StringBuilder log=new StringBuilder("Focused conversion regression checks\n");
  for(String version:List.of("before","after"))try(URLClassLoader loader=new URLClassLoader(new URL[]{Path.of("build/Q5",version).toUri().toURL()},ClassLoader.getPlatformClassLoader())){
   for(String style:List.of("Frame","Named","Anonymous","Lambda")){
    Method m=loader.loadClass(style+"Converter").getDeclaredMethod("convertValue",double.class,String.class,String.class);m.setAccessible(true);
    check((double)m.invoke(null,1000.0,"INR","USD")==12.0,"Normal conversion changed");
    check((double)m.invoke(null,1e308,"USD","USD")==1e308,"Same-currency intermediate overflow");
    double cross=(double)m.invoke(null,1e308,"USD","EUR");check(Double.isFinite(cross)&&Math.abs(cross/1e308-0.011/0.012)<1e-15,"Finite cross conversion overflowed");
    check((double)m.invoke(null,0.0,"EUR","USD")==0,"Zero conversion");
    invalid(m,Double.MAX_VALUE,"EUR","INR");invalid(m,Double.NaN,"INR","USD");invalid(m,Double.POSITIVE_INFINITY,"USD","EUR");invalid(m,-1,"INR","USD");
    log.append("PASS: "+version+" "+style+": normal, huge same/cross currency, zero, true overflow, NaN, infinity, negative\n");
   }
  }
  Files.writeString(Path.of("Q5/results/edge-case-tests.txt"),log.toString());System.out.print(log);
 }
 static class RecordingDao implements RegistrationDao {
  int calls;String id,name;int event;
  public void insert(String id,String name,int event){calls++;this.id=id;this.name=name;this.event=event;}
  public List<Registration> page(int e,int o,int l){return List.of();}
  public long export(int e,Path p){return 0;}
 }
 static void registrations()throws Exception {
  RecordingDao dao=new RecordingDao();RegistrationService service=new RegistrationService(dao);StringBuilder log=new StringBuilder("Focused registration regression checks\n");
  for(String[] input:new String[][]{{null,"Valid Name"},{"192372036",null},{null,null},{" ","Valid Name"},{"192372036"," "}}){
   try{service.register(input[0],input[1],1);throw new AssertionError("Accepted invalid registration");}
   catch(IllegalArgumentException expected){check(dao.calls==0,"Invalid input reached DAO");log.append("PASS: invalid/null input rejected by validation before DAO\n");}
  }
  check(service.register(" 192372036 "," B Hitesh Roy ",1).startsWith("Registered "),"Valid registration rejected");
  check(dao.calls==1&&dao.id.equals("192372036")&&dao.name.equals("B Hitesh Roy")&&dao.event==1,"Trimming or valid DAO call changed");
  log.append("PASS: valid padded input trimmed and inserted once\n");Files.writeString(Path.of("Q6/results/edge-case-tests.txt"),log.toString());System.out.print(log);
 }
 public static void main(String[] args)throws Exception {converters();registrations();}
}

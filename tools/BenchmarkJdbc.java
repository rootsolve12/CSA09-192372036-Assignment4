import java.sql.*;
import java.nio.file.*;
import java.util.*;
public class BenchmarkJdbc {
 public static void main(String[] args)throws Exception{
  String rootPassword=System.getenv().getOrDefault("ASSIGNMENT_ADMIN_PASSWORD",System.getenv().getOrDefault("ASSIGNMENT_DB_PASSWORD",""));
  try(Connection c=DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/assignment4",System.getenv().getOrDefault("ASSIGNMENT_ADMIN_USER","root"),rootPassword);Statement s=c.createStatement()){
   s.execute("CREATE TABLE IF NOT EXISTS benchmark_registrations (id BIGINT PRIMARY KEY,student_id VARCHAR(9),student_name VARCHAR(80),event_id INT,INDEX event_page(event_id,id))");
   try(ResultSet rs=s.executeQuery("SELECT COUNT(*) FROM benchmark_registrations")){rs.next();if(rs.getInt(1)!=100000){s.executeUpdate("DELETE FROM benchmark_registrations");c.setAutoCommit(false);try(PreparedStatement insert=c.prepareStatement("INSERT INTO benchmark_registrations VALUES(?,?,?,?)")){
    for(int i=0;i<100000;i++){insert.setLong(1,i+1);insert.setString(2,String.format(Locale.ROOT,"%09d",i));insert.setString(3,"Benchmark Student "+i);insert.setInt(4,i%3+1);insert.addBatch();if(i%1000==999)insert.executeBatch();}c.commit();}c.setAutoCommit(true);}}
  }
  StringBuilder csv=new StringBuilder("approach,run,elapsed_ns,retained_rows,retained_string_characters\n");
  try(Connection c=Db.open()){
   for(String mode:new String[]{"all","first_page","last_page"})for(int run=0;run<=5;run++){
    String sql="SELECT id,student_id,student_name,event_id FROM benchmark_registrations ORDER BY id"+(mode.equals("all")?"":" LIMIT 51 OFFSET "+(mode.equals("first_page")?0:99950));
    long start=System.nanoTime();ArrayList<Registration> list=new ArrayList<>();
    try(Statement s=c.createStatement();ResultSet rs=s.executeQuery(sql)){while(rs.next())list.add(new Registration(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getInt(4),"Event"));}
    long elapsed=System.nanoTime()-start,chars=list.stream().mapToLong(r->r.studentId().length()+r.name().length()+r.event().length()).sum();
    if(run>0)csv.append(mode+","+run+","+elapsed+","+list.size()+","+chars+"\n");
   }
  }
  Files.writeString(Path.of("Q6/results/loading-measurements.csv"),csv.toString());System.out.println(csv);
 }
}

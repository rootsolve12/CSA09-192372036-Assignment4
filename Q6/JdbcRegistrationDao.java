import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class JdbcRegistrationDao implements RegistrationDao {
 private static void delay() {
  int ms=Integer.parseInt(System.getenv().getOrDefault("ASSIGNMENT_TEST_DELAY_MS","0"));
  if(ms>0)try{Thread.sleep(ms);}catch(InterruptedException e){Thread.currentThread().interrupt();throw new IllegalStateException("Interrupted",e);}
 }
 public void insert(String student,String name,int event) throws SQLException {
  delay();
  try(Connection c=Db.open();PreparedStatement s=c.prepareStatement("INSERT INTO registrations(student_id,student_name,event_id) VALUES(?,?,?)")) {
   s.setString(1,student);s.setString(2,name);s.setInt(3,event);s.executeUpdate();
  }
 }
 private PreparedStatement query(Connection c,int event,String suffix) throws SQLException {
  PreparedStatement s=c.prepareStatement("SELECT r.id,r.student_id,r.student_name,r.event_id,e.name FROM registrations r JOIN events e ON e.id=r.event_id "+(event==0?"":"WHERE r.event_id=? ")+"ORDER BY r.id "+suffix);
  if(event!=0)s.setInt(1,event);return s;
 }
 static Registration row(ResultSet rs)throws SQLException{return new Registration(rs.getLong(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getString(5));}
 public java.util.List<Registration> page(int event,int offset,int limit)throws SQLException {
  if(event<0||event>3||offset<0||limit<1)throw new IllegalArgumentException("Invalid page");delay();
  try(Connection c=Db.open();PreparedStatement s=query(c,event,"LIMIT ? OFFSET ?")) {
   int n=event==0?1:2;s.setInt(n,limit);s.setInt(n+1,offset);
   try(ResultSet rs=s.executeQuery()){java.util.List<Registration> rows=new ArrayList<>();while(rs.next())rows.add(row(rs));return rows;}
  }
 }
 static String csv(String text){
  // Prevent spreadsheet formula interpretation when an exported name is opened in Excel.
  if(text.matches("^[=+@\\-].*"))text="'"+text;
  return "\""+text.replace("\"","\"\"")+"\"";
 }
 public long export(int event,Path target)throws SQLException,IOException {
  Path absolute=target.toAbsolutePath();Path temp=Files.createTempFile(absolute.getParent(),"registrations-",".tmp");boolean moved=false;long count=0;
  try {
   try(Connection c=Db.open();PreparedStatement s=query(c,event,"")) {
    s.setFetchSize(Integer.MIN_VALUE); // Connector/J forward-only streaming result set.
    try(ResultSet rs=s.executeQuery();BufferedWriter w=Files.newBufferedWriter(temp)) {
     w.write("id,student_id,name,event\n");while(rs.next()){Registration r=row(rs);w.write(r.id()+","+csv(r.studentId())+","+csv(r.name())+","+csv(r.event())+"\n");count++;}
    }
   }
   Files.move(temp,absolute,StandardCopyOption.REPLACE_EXISTING);moved=true;return count;
  }finally{if(!moved)Files.deleteIfExists(temp);}
 }
}

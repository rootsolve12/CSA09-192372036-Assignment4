import java.sql.SQLException;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
public class RegistrationService {
 private final RegistrationDao dao;
 public RegistrationService(RegistrationDao dao){this.dao=dao;}
 public static void validate(String id,String name,int event) {
  if(id==null||!id.matches("[0-9]{9}"))throw new IllegalArgumentException("Student ID must contain exactly 9 digits");
  if(name==null||name.isBlank()||name.length()>80||name.chars().anyMatch(Character::isISOControl))throw new IllegalArgumentException("Name must contain 1 to 80 printable characters");
  if(event<1||event>3)throw new IllegalArgumentException("Choose an available event");
 }
 public String register(String id,String name,int event)throws SQLException {
  id=id.trim();name=name.trim();validate(id,name,event);
  try{dao.insert(id,name,event);return "Registered "+id+" successfully";}
  catch(SQLException e){if(e.getErrorCode()==1062)return "Already registered for this event";throw e;}
 }
 public List<Registration> page(int event,int offset,int limit)throws SQLException{return dao.page(event,offset,limit);}
 public long export(int event,Path file)throws SQLException,IOException{return dao.export(event,file);}
}

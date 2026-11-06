import java.sql.SQLException;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
public interface RegistrationDao {
 void insert(String student,String name,int event) throws SQLException;
 List<Registration> page(int event,int offset,int limit) throws SQLException;
 long export(int event,Path path) throws SQLException,IOException;
}

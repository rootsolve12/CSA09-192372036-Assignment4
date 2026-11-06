import java.sql.*;
public final class Db {
 private Db() {}
 public static Connection open() throws SQLException {
  String url=System.getenv().getOrDefault("ASSIGNMENT_DB_URL","jdbc:mysql://127.0.0.1:3307/assignment4?connectTimeout=5000&socketTimeout=15000");
  return DriverManager.getConnection(url,System.getenv().getOrDefault("ASSIGNMENT_DB_USER","assignment"),System.getenv().getOrDefault("ASSIGNMENT_DB_PASSWORD",""));
 }
}

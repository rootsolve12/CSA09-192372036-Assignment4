import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;
public class FeeService {
 public record Payment(String receipt,String register,BigDecimal amount) {}
 public static BigDecimal validate(String register,String text) {
  if(register==null || !register.matches("[0-9]{9}"))throw new IllegalArgumentException("Enter a 9-digit register number");
  try {
   BigDecimal amount=new BigDecimal(text.trim());
   if(amount.signum()<=0 || amount.compareTo(new BigDecimal("1000000"))>0 || amount.scale()>2)throw new NumberFormatException();
   return amount.setScale(2);
  }catch(RuntimeException e){throw new IllegalArgumentException("Amount must be positive, at most 1000000, with up to 2 decimals");}
 }
 public Payment pay(String register,String text) throws SQLException {
  BigDecimal amount=validate(register,text);String receipt=UUID.randomUUID().toString();
  // Each attempt is one database insert. No fabricated bank-confirmation delay.
  try(Connection c=Db.open();PreparedStatement s=c.prepareStatement("INSERT INTO fees(receipt,register_no,amount) VALUES(?,?,?)")) {
   s.setString(1,receipt);s.setString(2,register);s.setBigDecimal(3,amount);s.executeUpdate();
  }
  return new Payment(receipt,register,amount);
 }
}

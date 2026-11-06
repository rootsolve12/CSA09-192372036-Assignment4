import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
public class SuppliedConverterEvidence {
 public static void main(String[] args)throws Exception{
  Robot r=new Robot();Converter[] ref=new Converter[1];SwingUtilities.invokeAndWait(()->{ref[0]=new Converter();ref[0].setLocation(120,100);ref[0].setVisible(true);});Converter w=ref[0];
  Field a=Converter.class.getDeclaredField("amount"),v=Converter.class.getDeclaredField("result");a.setAccessible(true);v.setAccessible(true);JTextField amount=(JTextField)a.get(w);JLabel result=(JLabel)v.get(w);
  StringBuilder log=new StringBuilder();
  for(String input:new String[]{"1000","abc","NaN","-1"}){
   SwingUtilities.invokeAndWait(()->{amount.setText(input);amount.postActionEvent();});r.delay(400);
   String expected=input.equals("1000")?"12.00":"Invalid amount";if(!result.getText().equals(expected))throw new AssertionError(input);log.append("PASS: input=").append(input).append(" output=").append(result.getText()).append('\n');
   if(input.equals("1000")||input.equals("abc"))ImageIO.write(r.createScreenCapture(w.getBounds()),"png",Path.of("Q5/screenshots/supplied-"+(input.equals("1000")?"valid":"invalid")+".png").toFile());
  }
  Files.writeString(Path.of("Q5/results/supplied-tests.txt"),log.toString());SwingUtilities.invokeAndWait(w::dispose);System.exit(0);
 }
}

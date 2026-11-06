import java.awt.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.util.*;
public class TerminalEvidence {
 public static void main(String[] args)throws Exception{
  Robot robot=new Robot();
  for(int n=1;n<=6;n++){
   String q="Q"+n;System.out.print("\033[2J\033[H");
   System.out.println(q+" | B Hitesh Roy | 192372036 | Actual workspace compilation");
   System.out.println("Compiler OpenJDK 21.0.13; Java desktop execution 21.0.12.1; MySQL 8.4.13");
   Path source=Path.of(q,switch(n){case 1->"FeeWindow.java";case 2->"AppointmentDesk.java";case 3->"LibrarySearch.java";case 4->"DrawPad.java";case 5->"supplied/Converter.java";default->"CampusEvents.java";});
   System.out.println("Source excerpt: "+source);java.util.List<String> lines=Files.readAllLines(source);for(int i=0;i<Math.min(8,lines.size());i++)System.out.printf("%2d: %s%n",i+1,lines.get(i));
   System.out.println("$ python3 tools/compile.py "+n);System.out.flush();
   Process p=new ProcessBuilder("python3","tools/compile.py",""+n).inheritIO().start();if(p.waitFor()!=0)throw new AssertionError(q);
   for(String line:Files.readAllLines(Path.of(q,"results/compiler.txt")))if(line.startsWith("Exit=")||line.contains("error:")||line.contains("does not override"))System.out.println(line);
   System.out.println("Full commands and diagnostics: "+q+"/results/compiler.txt");System.out.flush();robot.delay(700);
   ImageIO.write(robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),"png",Path.of(q,"screenshots/compiler.png").toFile());
  }
  System.out.println("Captured all six real compiler screens");
 }
}

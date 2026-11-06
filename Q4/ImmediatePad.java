import java.awt.*;
import java.awt.event.*;
public class ImmediatePad extends Frame {
 int count;final Label status=new Label("Clicks: 0");
 public ImmediatePad(){super("Immediate drawing defect");add(status,BorderLayout.SOUTH);addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){count++;status.setText("Clicks: "+count);Graphics g=getGraphics();try{g.fillOval(e.getX()-3,e.getY()-3,6,6);}finally{g.dispose();}}});Ui.close(this,this::dispose);setSize(400,300);}
 public static void main(String[] args){EventQueue.invokeLater(()->new ImmediatePad().setVisible(true));}
}

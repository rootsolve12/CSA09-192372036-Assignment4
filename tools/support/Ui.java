import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
public final class Ui {
 private Ui() {}
 public static void close(Window window,Runnable action) {
  window.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e){action.run();}});
 }
 public static JPanel row(Component... items) {
  JPanel panel=new JPanel(new FlowLayout(FlowLayout.LEFT,10,8));for(Component c:items)panel.add(c);return panel;
 }
 public static <T> void work(Callable<T> task,Consumer<T> success,Consumer<Exception> failure) {
  new SwingWorker<T,Void>() {
   protected T doInBackground() throws Exception{return task.call();}
   protected void done(){try{success.accept(get());}catch(Exception e){Throwable c=e.getCause();failure.accept(c instanceof Exception?(Exception)c:e);}}
  }.execute();
 }
 public static void launch(Runnable task){SwingUtilities.invokeLater(task);}
}

import javax.swing.*;
import java.awt.*;
import java.util.*;
public class LibrarySearch extends JFrame {
 record Book(String title,String category){}
 final JTextField query=new JTextField(20);final JComboBox<String> category=new JComboBox<>(new String[]{"All","Computing","Science","Literature"});
 final JButton search=new JButton("Search"),prev=new JButton("Prev"),next=new JButton("Next"),reserve=new JButton("Reserve");
 final DefaultListModel<String> rows=new DefaultListModel<>();final JList<String> results=new JList<>(rows);final JLabel status=new JLabel("Ready");
 final JPanel searchRow,buttonRow;final JScrollPane scroll;final java.util.List<Book> catalog=new ArrayList<>();java.util.List<Book> matches;int page;
 public LibrarySearch(){
  super("Q3 Campus Library Search");setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  for(int i=1;i<=24;i++)catalog.add(new Book("Library Volume "+i,new String[]{"Computing","Science","Literature"}[(i-1)%3]));
  JPanel north=new JPanel(new BorderLayout());north.add(new JLabel("Campus Library",SwingConstants.CENTER),BorderLayout.NORTH);
  searchRow=Ui.row(new JLabel("Title"),query,category,search);north.add(searchRow,BorderLayout.SOUTH);add(north,BorderLayout.NORTH);
  scroll=new JScrollPane(results);add(scroll,BorderLayout.CENTER);
  JPanel south=new JPanel(new BorderLayout());buttonRow=Ui.row(prev,next,reserve);south.add(buttonRow,BorderLayout.NORTH);south.add(status,BorderLayout.SOUTH);add(south,BorderLayout.SOUTH);
  search.addActionListener(e->search());query.addActionListener(e->search());prev.addActionListener(e->{page--;showPage();});next.addActionListener(e->{page++;showPage();});reserve.addActionListener(e->{String selected=results.getSelectedValue();status.setText(selected==null?"Select a book to reserve":"Reserved: "+selected);});
  setMinimumSize(new Dimension(650,350));setSize(760,500);setLocationByPlatform(true);search();
 }
 void search(){String text=query.getText().trim().toLowerCase(Locale.ROOT);String cat=(String)category.getSelectedItem();matches=catalog.stream().filter(b->b.title.toLowerCase(Locale.ROOT).contains(text)&&("All".equals(cat)||b.category.equals(cat))).toList();page=0;showPage();}
 void showPage(){rows.clear();for(Book b:matches.subList(page*6,Math.min(matches.size(),page*6+6)))rows.addElement(b.title+" / "+b.category);prev.setEnabled(page>0);next.setEnabled((page+1)*6<matches.size());status.setText(matches.size()+" matches; page "+(page+1));}
 public static void main(String[] args){Ui.launch(()->new LibrarySearch().setVisible(true));}
}

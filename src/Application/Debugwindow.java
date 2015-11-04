package Application;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;


public class Debugwindow {

	private JFrame frame = new JFrame();
	private DefaultListModel list1 = new DefaultListModel();
	private JList list_1 = new JList(list1);
	private DefaultListModel list2 = new DefaultListModel();
	private JList list_2 = new JList(list2);
	private DefaultListModel list3 = new DefaultListModel();
	private JList list_3 = new JList(list3);
	private DefaultListModel list4 = new DefaultListModel();
	private JList list_4 = new JList(list4);
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel label4;
	
	public Debugwindow() {
		frame.getContentPane().setLayout(null);
		
		label1 = new JLabel("List 1");
		label1.setBounds(59, 11, 46, 14);
		frame.getContentPane().add(label1);
		
		label2 = new JLabel("List 2");
		label2.setBounds(333, 11, 46, 14);
		frame.getContentPane().add(label2);
		
		label3 = new JLabel("List 3");
		label3.setBounds(59, 256, 46, 14);
		frame.getContentPane().add(label3);
		
		label4 = new JLabel("List 4");
		label4.setBounds(333, 256, 46, 14);
		frame.getContentPane().add(label4);
		
		list_1.setBounds(10, 36, 136, 200);
		frame.getContentPane().add(list_1);
		
		list_2.setBounds(288, 36, 136, 200);
		frame.getContentPane().add(list_2);
		
		list_3.setBounds(10, 281, 136, 200);
		frame.getContentPane().add(list_3);
		
		list_4.setBounds(288, 281, 136, 200);
		frame.getContentPane().add(list_4);
		
		
		frame.setSize(450, 530);
		frame.setLocation(1315, 255);
		frame.setVisible(true);
		
	}
	
	public void addTolist1() {
	}
	
	public void addTolist2() {
	}
	
	public void addTolist3() {
	}
	
	public void addTolist4() {
	}
	
	public void removeWindow() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}

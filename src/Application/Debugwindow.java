package Application;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;


public class Debugwindow {

	private JFrame frame = new JFrame();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_3;
	private JTextField textField_2;
	
	public Debugwindow() {
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 35, 104, 152);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(35, 11, 46, 14);
		frame.getContentPane().add(lblNewLabel);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(186, 35, 104, 152);
		frame.getContentPane().add(textField_1);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(186, 241, 104, 152);
		frame.getContentPane().add(textField_3);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(10, 241, 104, 152);
		frame.getContentPane().add(textField_2);
		
		JLabel label = new JLabel("New label");
		label.setBounds(212, 11, 46, 14);
		frame.getContentPane().add(label);
		
		JLabel label_1 = new JLabel("New label");
		label_1.setBounds(212, 216, 46, 14);
		frame.getContentPane().add(label_1);
		
		JLabel label_2 = new JLabel("New label");
		label_2.setBounds(35, 216, 46, 14);
		frame.getContentPane().add(label_2);
		
		
		
	}
}

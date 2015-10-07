import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;


public class WindowTest {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    WindowTest window = new WindowTest();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public WindowTest() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 550, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
        JTextArea textArea = new JTextArea();
        textArea.setBounds(10, 38, 200, 120);
        frame.getContentPane().add(textArea);
        
        JTextArea textArea_1 = new JTextArea();
        textArea_1.setBounds(317, 38, 200, 120);
        frame.getContentPane().add(textArea_1);
        
        JTextArea textArea_2 = new JTextArea();
        textArea_2.setBounds(10, 281, 200, 120);
        frame.getContentPane().add(textArea_2);
        
        JTextArea textArea_3 = new JTextArea();
        textArea_3.setBounds(317, 281, 200, 120);
        frame.getContentPane().add(textArea_3);
        
        JLabel lblNewLabel = new JLabel("Some label");
        lblNewLabel.setBounds(10, 13, 60, 20);
        frame.getContentPane().add(lblNewLabel);
        
        JLabel label = new JLabel("Some label");
        label.setBounds(317, 13, 60, 20);
        frame.getContentPane().add(label);
        
        JLabel label_1 = new JLabel("Some label");
        label_1.setBounds(10, 250, 60, 20);
        frame.getContentPane().add(label_1);
        
        JLabel label_2 = new JLabel("Some label");
        label_2.setBounds(317, 250, 60, 20);
        frame.getContentPane().add(label_2);
        
        JCheckBox chckbxNewCheckBox = new JCheckBox("Some check box");
        chckbxNewCheckBox.setBounds(76, 249, 110, 23);
        frame.getContentPane().add(chckbxNewCheckBox);
        
        JCheckBox checkBox = new JCheckBox("Some check box");
        checkBox.setBounds(383, 249, 110, 23);
        frame.getContentPane().add(checkBox);
        
        JRadioButton rdbtnNewRadioButton = new JRadioButton("Some radio button");
        rdbtnNewRadioButton.setBounds(76, 8, 125, 23);
        frame.getContentPane().add(rdbtnNewRadioButton);
        
        JToggleButton tglbtnNewToggleButton = new JToggleButton("Some toggle button");
        tglbtnNewToggleButton.setBounds(372, 12, 145, 23);
        frame.getContentPane().add(tglbtnNewToggleButton);
    }
}

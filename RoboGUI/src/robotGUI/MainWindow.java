package robotGUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.Color;

//drawing on panels
import java.awt.Graphics;
import java.awt.Graphics2D;

public class MainWindow{
	
	private JFrame frame;
	private JTextField txtSomeTextHere;
	private JCheckBox chckbxControlSomething;
	private JPanel overheadViewPanel;

	//
	//Launch the application.
	//
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//
	// Create the application.
	//
	public MainWindow() {
		initialize();
	}

	//
	// Initialize the contents of the frame.
	//
	private void initialize() {
		frame = new JFrame();
		frame.setBackground(Color.GRAY);
		frame.setBounds(100, 100, 1000, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel lblHello = new JLabel("Hello, welcome to robot world");
		frame.getContentPane().add(lblHello, BorderLayout.NORTH);
		
		txtSomeTextHere = new JTextField();
		txtSomeTextHere.setText("Some TExt here");
		frame.getContentPane().add(txtSomeTextHere, BorderLayout.SOUTH);
		txtSomeTextHere.setColumns(10);
		
		JRadioButton rdbtnControlSomething = new JRadioButton("Control something");
		frame.getContentPane().add(rdbtnControlSomething, BorderLayout.EAST);
		
		chckbxControlSomething = new JCheckBox("Control Something");
		frame.getContentPane().add(chckbxControlSomething, BorderLayout.WEST);
		
		overheadViewPanel = new overheadView();
		overheadViewPanel.setBackground(Color.BLACK);
		frame.getContentPane().add(overheadViewPanel, BorderLayout.CENTER);
	}
	
	void setupMainWindow(){
	
	}

}

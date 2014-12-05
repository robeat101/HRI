package robotGUI;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.Timer;

import robotGenericValues.StandardValues;

import java.awt.Color;
import java.awt.Dimension;
//drawing on panels
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class MainWindow{
	
	private JFrame frame;
	private JTextField txtSomeTextHere;
	private JCheckBox chckbxControlSomething;
	private overheadView overheadViewPanel;
	private long time = 0;
    private Timer timer = new Timer(StandardValues.DELTA_TIME_INTERPOLATE, new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
        	time++;
        	int interpolationsPerTimeStep = StandardValues.DELTA_TIME/StandardValues.DELTA_TIME_INTERPOLATE;
        	int remainder = (int)(time%(long)interpolationsPerTimeStep);
        	if (remainder!=0){
        		overheadViewPanel.interpolate((float)remainder/interpolationsPerTimeStep);
        	}else{
//        	System.out.println("UPDATE SIMULATION:");
        		overheadViewPanel.update();
        	}
        }
    });
	
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
		timer.start();
	}

	//
	// Initialize the contents of the frame.
	//
	private void initialize() {
		int side = 900;
		frame = new JFrame();
		frame.setBackground(Color.GRAY);
		frame.setBounds(0,0,side, side);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JProgressBar rechargeIndicator = new JProgressBar(JProgressBar.VERTICAL, 0, side);
		rechargeIndicator.setValue(rechargeIndicator.getMaximum());
		frame.getContentPane().add(rechargeIndicator, BorderLayout.EAST);
		
		/*
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
		*/
		overheadViewPanel = new overheadView();
		overheadViewPanel.setBackground(Color.BLACK);		
		frame.getContentPane().add(overheadViewPanel, BorderLayout.CENTER);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		overheadViewPanel.setUserIndicator(rechargeIndicator);

	}
	
}

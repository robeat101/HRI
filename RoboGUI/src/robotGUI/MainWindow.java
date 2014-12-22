package robotGUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

public class MainWindow{
	
	private JFrame frame;
	private overheadView overheadViewPanel;
//	private Timer timer = new Timer(StandardValues.DELTA_TIME_INTERPOLATE, new AbstractAction() {
//        @Override
//        public void actionPerformed(ActionEvent ae) {
//        	time++;
//        	int interpolationsPerTimeStep = StandardValues.DELTA_TIME/StandardValues.DELTA_TIME_INTERPOLATE;
//        	int remainder = (int)(time%(long)interpolationsPerTimeStep);
//        	if (remainder!=0){
//        		overheadViewPanel.interpolate((float)remainder/interpolationsPerTimeStep);
//        	}else{
////        	System.out.println("UPDATE SIMULATION:");
//        		overheadViewPanel.update();
//        	}
//        }
//    });
	
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
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.out.println("Shutting down");
				RobotWorld.getRobotWolrd().writeFinalLog();
				DataLogger.getDataLogger().saveLog();
			}
		}));
	}

	//
	// Create the application.
	//
	public MainWindow() {
		initialize();
		SimTimer.makeTheTimer(overheadViewPanel);
	}

	//
	// Initialize the contents of the frame.
	//
	private void initialize() {
		int side = 800;

		frame = new JFrame();
		frame.setBackground(Color.GRAY);
		frame.setBounds(0,0,side, side);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JProgressBar rechargeIndicator = new JProgressBar(JProgressBar.VERTICAL, 0, side);
		rechargeIndicator.setValue(rechargeIndicator.getMaximum());
		frame.getContentPane().add(rechargeIndicator, BorderLayout.EAST);
		
		ScoreLabel scoreIndicator = new ScoreLabel();
		frame.getContentPane().add(scoreIndicator, BorderLayout.NORTH);
				
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
		overheadViewPanel.setBackground(Color.WHITE);		
		frame.getContentPane().add(overheadViewPanel, BorderLayout.CENTER);
		
		frame.setResizable(true);
		frame.pack();
		frame.setVisible(true);

		overheadViewPanel.setUserIndicators(rechargeIndicator, scoreIndicator);
		
		//change cursor
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image mouseImage = toolkit.getImage("images/wrench.png");
		Cursor cursor = toolkit.createCustomCursor(mouseImage, new Point(frame.getX(), frame.getY()), "img");
		frame.setCursor(cursor);
	}
	
}

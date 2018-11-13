import java.util.Map;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.Choice;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.io.IOException;
import javax.swing.JToggleButton;

public class GameScreen {

	private JFrame frame;
	private JButton btnNewGame;
	private JButton btnSave;
	private JPanel panel;
	private JPanel textPanel;
	private JTextField topscoreField;
	private JTextField scoreField;
	private Choice choice;
	private JToggleButton tglbtnAutomatic;
		
	private Model model;
		
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new GameScreen();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public GameScreen() throws IOException {
		model = new Model(this);
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setFocusable(true);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(300,300));
		
		textPanel = new JPanel();
		frame.getContentPane().add(textPanel, BorderLayout.SOUTH);
		textPanel.setLayout(new BorderLayout(0, 0));
		
		scoreField = new JTextField();
		textPanel.add(scoreField, BorderLayout.CENTER);
		scoreField.setEditable(false);
		scoreField.setFocusable(false);
		
		topscoreField = new JTextField();
		textPanel.add(topscoreField, BorderLayout.EAST);
		topscoreField.setColumns(10);
		topscoreField.setEditable(false);
		topscoreField.setFocusable(false);
		
		frame.pack();

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		menuBar.setFocusable(false);
		
		btnNewGame = new JButton("New Game");
		menuBar.add(btnNewGame);
		btnNewGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.newGame(Integer.parseInt(choice.getSelectedItem()));
			}
		});
		
		btnSave = new JButton("Save Score");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.saveTopScore();
			}
		});
		menuBar.add(btnSave);
		
		tglbtnAutomatic = new JToggleButton("Automatic");
		tglbtnAutomatic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnAutomatic.getText().equals("Automatic")){
					tglbtnAutomatic.setText("Manual");
				} else if(tglbtnAutomatic.getText().equals("Manual")){
					tglbtnAutomatic.setText("Automatic");
				} else {
					System.out.println("Error changing autoController!");
				}
				model.switchAutoController();
			}
		});
		menuBar.add(tglbtnAutomatic);
		
		choice = new Choice();
		menuBar.add(choice);
		choice.add("2");
		choice.add("3");
		choice.add("4");
		
		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				Direction direction;
				if(e.getKeyCode() == KeyEvent.VK_UP){
					direction = Direction.UP;
					model.directionPressed(direction);
				} else if(e.getKeyCode() == KeyEvent.VK_DOWN){
					direction = Direction.DOWN;
					model.directionPressed(direction);
				} else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					direction = Direction.RIGHT;
					model.directionPressed(direction);
				} else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					direction = Direction.LEFT;
					model.directionPressed(direction);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		};
				
		frame.addKeyListener(keyListener);
		panel.addKeyListener(keyListener);
		menuBar.addKeyListener(keyListener);
		btnNewGame.addKeyListener(keyListener);
		btnSave.addKeyListener(keyListener);
		choice.addKeyListener(keyListener);

	}
		
	public void update(Map<Integer, Map<Integer, MyButton>> fieldsMap, int score, boolean finished, boolean newRecord) {
		// Update view
		panel.removeAll();
		panel.setLayout(new GridLayout(fieldsMap.size(), fieldsMap.size(), 1, 1));
		for(Map<Integer,MyButton> map : fieldsMap.values()){
			for(MyButton button : map.values()){
				panel.add(button);
			}
		}
		panel.revalidate();
		// Update score
		scoreField.setText("Score: " + score);
		// Update finished popup if necessairy
		if(finished){
			if(newRecord){
				JOptionPane.showMessageDialog(frame, "You reached a score of " + score + ".\n" + "That's a new record, congratulations!", "Game Over!", JOptionPane.PLAIN_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(frame, "You reached a score of " + score + ".", "Game Over!", JOptionPane.PLAIN_MESSAGE);
			}
		}

	}
	
	public void displayTopscore(int topscore){
		topscoreField.setText("Topscore: " + topscore);
	}
	
}

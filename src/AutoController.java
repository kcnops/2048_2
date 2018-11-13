import java.util.Random;

public class AutoController implements Runnable {

	private Model model;
	
	private boolean running = false;
	

	public AutoController(Model model){
		this.model = model;
	}
	
	public void run() {
		while(running){
				System.out.println("Running...");
				try {
				
					// CODE
					//randomDirection();
				
					Thread.currentThread().sleep(1000);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
	
	public synchronized void start(){
		System.out.println("Started.");
		running = true;
		new Thread(this).start();
	}
	
	public void stop(){
		System.out.println("Stopped.");
		running = false;
	}
	
	
	/**********************
	 * Automation Methods *
	 **********************/

	private void alwaysDown(){
		model.directionPressed(Direction.DOWN);
	}
	
	private void randomDirection(){
		Direction[] directions = Direction.values();
		Random random = new Random();
		model.directionPressed(directions[random.nextInt(directions.length)]);
	}
	
}

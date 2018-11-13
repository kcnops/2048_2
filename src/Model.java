import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Model {

	private GameScreen gameScreen;
	private TopscoreDB topscoreDB;
	private AutoController autoController;
	
	private static final double chanceForFour = 0.15;
	
	private Map<Integer,Map<Integer,MyButton>> fieldsMap;

	private int score;
	
	private boolean automatic = false;
		
	public Model(GameScreen gameScreen) throws IOException{
		this.gameScreen = gameScreen;
		topscoreDB = new TopscoreDB();
	}
	
	public Model() throws IOException{
		topscoreDB = new TopscoreDB();
	}
	
	public void newGame(int fieldSize){
		score = 0;
		fieldsMap = new HashMap<Integer,Map<Integer,MyButton>>();
		for(int i=1; i<=fieldSize; i++){
			HashMap<Integer, MyButton> tempMap = new HashMap<Integer,MyButton>();
			for(int j=1; j<=fieldSize; j++){
				MyButton tempField = new MyButton(i,j);
				tempField.setBackground(Color.WHITE);
				tempField.setFocusable(false);
				tempMap.put(j, tempField);
			}
			fieldsMap.put(i, tempMap);
		}
		addRandom();
		addRandom();
		if(gameScreen != null){
			gameScreen.update(fieldsMap, score, false, false);
			gameScreen.displayTopscore(topscoreDB.getTopscore(fieldsMap.size()));			
		}
	}
	
	public void directionPressed(Direction direction) {
		Map<Integer,Map<Integer,MyButton>> currentFieldsMap = new HashMap<Integer,Map<Integer,MyButton>>(fieldsMap);
		Map<Integer,Map<Integer,MyButton>> newFieldsMap = move(currentFieldsMap, direction);
		if(newFieldsMap != null){
			addRandom();
			boolean finished = false;
			boolean newRecord = false;
			if((finished = isFinished()) == true){
				if(score > topscoreDB.getTopscore(fieldsMap.size())){
					newRecord = true;
					saveTopScore();
				}
			}
			if(gameScreen != null){
				gameScreen.update(fieldsMap, score, finished, newRecord);
			}
		}		
	}
	
	/*
	 * @return	The new map after the move made according to the direction given.
	 * 			Null, if no move has been made.
	 */
	public Map<Integer,Map<Integer,MyButton>> move(Map<Integer,Map<Integer,MyButton>> fields, Direction direction){
		List<MyButton> buttonsToStartWith = new ArrayList<MyButton>();
		if(direction == Direction.DOWN){
			buttonsToStartWith.addAll(fields.get(fields.size()).values());
		} else if (direction == Direction.UP) {
			buttonsToStartWith.addAll(fields.get(1).values());
		} else if (direction == Direction.LEFT) {
			for(int i = 1; i<=fields.size(); i++){
				buttonsToStartWith.add(fields.get(i).get(1));
			}
		} else if (direction == Direction.RIGHT){
			for(int i = 1; i<=fields.size(); i++){
				buttonsToStartWith.add(fields.get(i).get(fields.size()));
			}
		}
		// Do doubles
		Boolean anyMove = false;
		for(MyButton button : buttonsToStartWith){
			Map<Integer,Map<Integer,MyButton>> newMap = findDouble(fields, button, direction, null);
			if(newMap != null){
				fields = newMap;
				if(!anyMove){
					anyMove = true;
				}
			}
		}
		// Shift numbers
		Boolean changed = true;
		while(changed){
			changed = false;
			for(Map<Integer,MyButton> map : fields.values()){
				for(MyButton button : map.values()){
					int value = button.getValue();
					if(value == 0){
						MyButton previousButton = getPreviousField(fields, button, direction);
						if(previousButton != null){
							int previousValue = previousButton.getValue();
							if(previousValue > 0){
								button.setValue(previousValue);
								previousButton.setValue(0);
								changed = true;
								anyMove = true;
							}
						}
					}
				}
			}
		}
		if(anyMove){
			return fields;
		}
		return null;
	}
	
	public void saveTopScore() {
		topscoreDB.newTopscore(fieldsMap.keySet().size(), score);
	}
	
	public void switchAutoController() {
		if(automatic){
			autoController.stop();
			automatic = false;
		} else {
			automatic = true;
			if(autoController == null){
				autoController = new AutoController(this);
			}
			autoController.start();
		}
	}
	
	private void addRandom(){
		Random ran = new Random();
		while(true){
			Object[] fieldsMapValues = fieldsMap.values().toArray();
			Map<Integer,MyButton> tempMap = (Map<Integer, MyButton>)fieldsMapValues[ran.nextInt(fieldsMapValues.length)];
			Object[] tempMapValues = tempMap.values().toArray();
			MyButton field = (MyButton) tempMapValues[ran.nextInt(tempMapValues.length)];
			if(field.getValue() == 0){
				double number = ran.nextDouble();
				if(number < chanceForFour){
					field.setValue(4);
				} else {
					field.setValue(2);					
				}
				return;
			}
		}
	}

	/*
	 * @param result	The result of the method, carried through recursion.
	 * 					Initialised to null and only changed to the active fields map when a double has been found.
	 * @return	The new map after a double has been found for the given button and direction.
	 * 			Null, if no double has been found.
	 */
	private Map<Integer,Map<Integer,MyButton>> findDouble(Map<Integer,Map<Integer,MyButton>> fields, MyButton button, Direction direction, Map<Integer,Map<Integer,MyButton>> result){
		if(button == null){return result;}
		MyButton previousButton = getPreviousField(fields, button, direction);
		if(previousButton == null){return result;}
		int value = button.getValue();
		if(value == 0){
			return findDouble(fields, previousButton, direction, result);
		} else {
			int previousValue = previousButton.getValue();
			if(previousValue == value){
				multiply(button);
				previousButton.setValue(0);
				result = fields;
				return findDouble(fields, getPreviousField(fields, previousButton, direction), direction, result);
			}
			else if(previousValue != 0){
				return findDouble(fields, previousButton, direction, result);
			}
			else if(previousValue == 0){
				previousButton = getPreviousField(fields, previousButton, direction);
				while(previousButton != null){
					previousValue = previousButton.getValue();
					if(previousValue == value){
						multiply(button);
						previousButton.setValue(0);
						result = fields;
						return findDouble(fields, getPreviousField(fields, previousButton, direction),direction, result);
					}
					else if(previousValue != 0){
						return findDouble(fields, previousButton, direction, result);
					}
					previousButton = getPreviousField(fields, previousButton, direction);
				}
				return result;
			}
		}
		return result;
	}
	
	private MyButton getPreviousField(Map<Integer,Map<Integer,MyButton>> fields, MyButton button, Direction direction){
		int x = button.getX();
		int y = button.getY();
		if(direction == Direction.DOWN){
			if(x == 1){ return null;}
			else {x--;}
			}
		else if(direction == Direction.UP){
			if(x == fields.size()) {return null;}
			else {x++;}
		}
		else if(direction == Direction.RIGHT){
			if(y == 1) {return null;}
			else {y--;}
		}
		else if(direction == Direction.LEFT){
			if(y == fields.size()) {return null;}
			else {y++;}
		}
		return fields.get(x).get(y);
	}
	
	private void multiply(MyButton button){
		button.multiply();
		score += button.getValue();
	}

	private Boolean isFinished(){
		for(Map<Integer,MyButton> map1 : fieldsMap.values()){
			for(MyButton button1 : map1.values()){
				if(button1.getValue() == 0){
					return false;
				}
				for(Map<Integer,MyButton> map2 : fieldsMap.values()){
					for(MyButton button2 : map2.values()){
						if(button1.getValue() == button2.getValue()){
							if((Math.abs(button1.getX() - button2.getX()) + Math.abs(button1.getY() - button2.getY())) == 1){
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	// Old unused methods
	
	private MyButton getNextButton(MyButton button, Direction direction){
		int x = button.getX();
		int y = button.getY();
		if(direction == Direction.DOWN){
			if(x == fieldsMap.size()){ return null;}
			else {x++;}
			}
		else if(direction == Direction.UP){
			if(x == 1) {return null;}
			else {x--;}
		}
		else if(direction == Direction.RIGHT){
			if(y == fieldsMap.size()) {return null;}
			else {y++;}
		}
		else if(direction == Direction.LEFT){
			if(y == 1) {return null;}
			else {y--;}
		}
		return fieldsMap.get(x).get(y);
	}

	private ArrayList<MyButton> getFields(){
		ArrayList<MyButton> fields = new ArrayList<MyButton>();
		for(Map<Integer,MyButton> map : fieldsMap.values()){
			for(MyButton button : map.values()){
				fields.add(button);
			}
		}
		return fields;
	}


}

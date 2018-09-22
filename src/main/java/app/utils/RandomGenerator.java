package app.utils;
import java.util.Random;

public class RandomGenerator {

	private Random randomGen;
	private char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G',  'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	
	public RandomGenerator() {
		this.randomGen = new Random();
	}
	
	public String generateAlphaNumericCode(int numberOfLetters, int from, int to) {
		return generateRandomWord(numberOfLetters)+Integer.toString(generateRandomIndex(from, to));
	}

	private String generateRandomWord(int numberOfLetters) {
		String w = "";
		
		for(int i=0; i<numberOfLetters; i++) {
			w = w+generateRandomLetter();
		}
		
		return w;
	}
	
	private char generateRandomLetter() {
		return letters[generateRandomIndex(0, letters.length)];
	}
	
	private int generateRandomIndex(int from, int to) {
		return randomGen.nextInt(to)+from;
	}
}


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Scanner;

public class FileManage {
	char newLineChar = 10;
	int lines = 1;
	int singleComments = 0;
	int blockComments = 0;
	int blockLines = 0;
	int todos = 0;
	boolean previousComment = false;
	boolean inAdjacentComment = false;
	boolean firstNonSpace =true;
	enum Expectations {
		STRING1,
		STRING2,
		MULTISTRING1,
		MULTISTRING2,
		SINGLECOMMENT,
		BLOCKCOMMENT,
		NONE
	};
	HashMap < String,Expectations > expects;
	Language l;
	BufferedReader br;
	File f;

	//function for when the reader is currently in a single comment
	void inSingleCommentRead(String start) throws IOException {

		//checks block comments for languages that don't have specific identifiers
		if (previousComment && !inAdjacentComment) {
			blockComments++;
			blockLines += 2;
			singleComments--;
			inAdjacentComment = true;
		} else if (inAdjacentComment) {
			blockLines++;
		} else {
			singleComments++;
		}

		lines++;

		
		String x = start + br.readLine();
		
		//Checks how many todos are in the string
		for(int i = 0;i< x.length()-6;i++) {
			if(!Character.isLetterOrDigit(x.charAt(i)) && x.substring(i+1, i+5).equals("TODO")
					&& !Character.isLetterOrDigit(x.charAt(i+5))){
				todos++;
			}
		}
		if(x.length() >= 5 && !Character.isLetterOrDigit(x.charAt(x.length()-5))
				&&x.substring(x.length()-4).equals("TODO")) {
			todos++;
		}
		if(x.length() >= 5 && !Character.isLetterOrDigit(x.charAt(4))
				&&x.substring(0,4).equals("TODO")) {
			todos++;
		}else if (x.length() == 4 &&x.substring(0,4).equals("TODO")) {
			todos++;
		}
		
		//Checks for block comment potential
		if (l.blockCommentStart.equals("") && firstNonSpace) {
			previousComment = true;
		}
		
		firstNonSpace = true;
	}

	//function for when the reader is currently in a block comment
	void inBlockCommentRead(String exit, String start) throws IOException {
		blockComments++;
		blockLines++;
		String current = "";
		String begin = start;
		String todoCheck = "      ";
		char x;
		while (!current.equals(exit)) {
			if (begin.length() > 0) {
				x = begin.charAt(0);
				begin = begin.substring(1);
			} else {
				x = (char) br.read();
			}
			if (x == newLineChar) {
				lines++;
				blockLines++;
			}
			
			todoCheck += x;
			if(todoCheck.length()>6) {
				todoCheck = todoCheck.substring(1);
			}
			
			if(!Character.isLetterOrDigit(todoCheck.charAt(0)) && todoCheck.substring(1,5).equals("TODO")
					&& !Character.isLetterOrDigit(todoCheck.charAt(5))){
				todos++;
			}
			
			current += x;
			if (current.length() <= exit.length() && current.equals(exit.substring(0, current.length()))) {
				if (current.equals(exit)) {
					current = "";
					if(!Character.isLetterOrDigit(todoCheck.charAt(1)) && todoCheck.substring(2,6).equals("TODO")){
						todos++;
					}
					return;
				}
			} else {
				current = "";
			}
		}
	}

	//function for when reader is in a string
	void inStringRead(String exit, char esc, String start) throws IOException {
		String begin = start;
		String current = "";
		char x;
		boolean escape;
		while (!current.equals(exit)) {
			if (begin.length() > 0) {
				x = begin.charAt(0);
				begin = begin.substring(1);
			} else {
				x = (char) br.read();
			}
			escape = (x == esc);
			if (escape) {
				x = (char) br.read();
			}
			if (x == newLineChar) {
				lines++;
			}
			current += x;
			if (!escape && current.length() <= exit.length() && current.equals(exit.substring(0, current.length()))) {
				if (current.equals(exit)) {
					current = "";
					return;
				}
			} else {
				current = "";
			}
		}
	}

	//function for when reader is in generic text
	void general(File f) throws IOException {
		br = new BufferedReader(new FileReader(f));
		String current = "";
		Expectations expect = Expectations.NONE; // expect is used in case key symbols are parts of others			
		boolean ongoing = false;
		char x;
		while (br.ready()) {
			ongoing = false;
			x = (char) br.read();
			if (x == newLineChar) {
				lines++;
				previousComment = false;
				inAdjacentComment = false;
				firstNonSpace = true;
			}
			current += x;
			for (String i: expects.keySet()) {
				if (check1(i, current)) {
					ongoing = true;
					expect = expects.get(i);
				}

			}
			if (check2(l.blockCommentStart, current)) {
				if (expect== Expectations.BLOCKCOMMENT ) {
					inBlockCommentRead(l.blockCommentEnd, current.substring(l.blockCommentStart.length()));
					expect = Expectations.NONE;
					current = "";
				}
			}
			if (check2(l.singleCommentStart, current)) {
				if (expect == Expectations.SINGLECOMMENT) {
					inSingleCommentRead(current.substring(l.singleCommentStart.length()));
					expect = Expectations.NONE;
					current = "";
				}
			}
			//checks all cases of strings
			for (String i: expects.keySet()) {
				if (expects.get(i) == Expectations.STRING1 || 
						expects.get(i) == Expectations.STRING2||
						expects.get(i) == Expectations.MULTISTRING1||
						expects.get(i) == Expectations.MULTISTRING2) {
					if (check2(i, current)) {
						if (expect == expects.get(i)) {
							inStringRead(i, l.stringEscape, current.substring(i.length()));
							expect = Expectations.NONE;
							current = "";
						}
					}
				}
			}
			
			if (current.trim().length() > l.singleCommentStart.length() || !l.singleCommentStart.substring(0, current.trim().length()).equals(current.trim())) {
				previousComment = false;
				inAdjacentComment = false;
				firstNonSpace = false;
			}
			if (!ongoing) {
				expect = Expectations.NONE;
				current = "";
			}
		}
		//cleans up any remaining characters left unused
		if (!current.equals("")) {
			lines++;
			if (expect == Expectations.BLOCKCOMMENT) {
				inBlockCommentRead(l.blockCommentEnd, current.substring(l.blockCommentStart.length()));
			}else if (expect == Expectations.SINGLECOMMENT) {
				inSingleCommentRead(current.substring(l.singleCommentStart.length()));
			}
		}
		br.close();
		output();
	}

	void output() {

		System.out.println("Total # of lines: " + lines);
		System.out.println("Total # of comment lines: " + (singleComments + blockLines));
		System.out.println("Total # of single line comments: " + singleComments);
		System.out.println("Total # of comment lines within block comments: " + blockLines);
		System.out.println("Total # of block line comments: " + blockComments);
		System.out.println("Total # of TODO's: " + todos);

	}

	//check1 checks if the current string is part of a key string
	boolean check1(String goal, String current) {
		return (!goal.equals("") && current.length() <= goal.length() && current.length() > 0 && current.equals(goal.substring(0, current.length())));
	}

	//check2 checks if the current string contains the entirety of a key string
	boolean check2(String x, String current) {
		return (!x.equals("") && current.length() > x.length() && current.substring(0, x.length()).equals(x));
	}

	//function that begins the check
	void runner() throws IOException {

		boolean success = false;
		String x = f.getName();
		if (!x.contains(".")) {
			System.out.println("I'm sorry, that does not appear to be a file");
			return;
		}
		 x = x.substring(x.lastIndexOf(46));
		
		
		
		String singleComm = null;
		String blockCommStart = null;;
		String blockCommEnd = null; 
		String stringSymbol1 = null;
		String stringSymbol2 = null;
		String multiStringSymbol1 = null;
		String multiStringSymbol2 = null;
		char escape = 0;

		File language = new File("src/languages.cfg");
		
		BufferedReader languageRead = new BufferedReader(new FileReader(language));
		//reads language information, if available
		while (languageRead.ready()) {
			String currentLine = languageRead.readLine();
			if (currentLine.equals("extention: " + x)) {
				singleComm = languageRead.readLine();
				blockCommStart = languageRead.readLine();
				blockCommEnd = languageRead.readLine();
				stringSymbol1 = languageRead.readLine();
				stringSymbol2 = languageRead.readLine();
				escape = languageRead.readLine().charAt(0);
				multiStringSymbol1 = languageRead.readLine();
				multiStringSymbol2 = languageRead.readLine();
				success = true;
			}
		}
		languageRead.close();
		if (!success) {
			System.out.println("I'm sorry, that is not currently a supported language");
			System.out.println("If you want, you can enter the info in \"languages.cfg\"");
			return;
		}
		l = new Language(x, singleComm, blockCommStart, blockCommEnd, stringSymbol1, stringSymbol2, escape, multiStringSymbol1, multiStringSymbol2);
		expects = new HashMap < String,	Expectations > ();
		expects.put(singleComm, Expectations.SINGLECOMMENT);
		expects.put(blockCommStart, Expectations.BLOCKCOMMENT);
		expects.put(stringSymbol1, Expectations.STRING1);
		expects.put(stringSymbol2, Expectations.STRING2);
		expects.put(multiStringSymbol1, Expectations.MULTISTRING1);
		expects.put(multiStringSymbol2, Expectations.MULTISTRING2);

		general(f);

	}

	FileManage(File f) throws IOException {
		this.f = f;
		runner();
	}
	public static void main(String[] args) throws IOException {
		Scanner input = new Scanner(System.in);
		String fileName = input.nextLine();
		input.close();
		FileManage fm = new FileManage(new File(fileName));
	}
}

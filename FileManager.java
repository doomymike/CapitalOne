import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileWriter;

public class FileManager {
	static char newLineChar = 10;
	static int lines =1;
	static int singleComments = 0;
	static int blockComments = 0;
	static int todos = 0;

	static void inSingleCommentRead(BufferedReader br, String start) throws IOException {
		//todo check
		String x = start + br.readLine();
		if((x.trim().length() >= 4)&&x.trim().substring(0, 4).equals("TODO")){
			todos++;
			lines++;
			//System.out.println(x.trim().substring(4));
		}else {
			//System.out.println(x);
			lines++;
			singleComments++;
		}
	}
	static void inBlockCommentRead(BufferedReader br, String exit, String start) throws IOException {
		blockComments ++;
		String current = "";
		String begin = start;
		char x;
		while(!current.equals(exit)) {
			if(begin.length()>0) {
				x = begin.charAt(0);
				begin = begin.substring(1);
			}else {
				x = (char)br.read();
			}
			if (x == newLineChar) {
				lines++;
				blockComments++;
			}
			current+= x;
			if(current.length()<=exit.length() && 
					current.equals(exit.substring(0, current.length()))) {
				if(current.equals(exit)) {
					current ="";
					return;
				}
			}else {
				//System.out.print(current);
				current = "";
			}
		}
	}

	static void inStringRead(BufferedReader br, String exit,char except,String start) throws IOException {
		String current = "";
		String begin = start;
		char x;
		boolean exception;
		while(!current.equals(exit)) {
			if(begin.length()>0) {
				x = begin.charAt(0);
				begin = begin.substring(1);
			}else {
				x = (char)br.read();
			}
			exception = (x == except);
			if(exception) {
				x = (char)br.read();
			}
			current+= x;
			if(!exception && current.length()<=exit.length() && 
					current.equals(exit.substring(0, current.length()))) {
				if(current.equals(exit)) {
					current ="";
					return;
				}
			}else {
				//System.out.print(current);
				current = "";
			}

		}
	}
	
	static void inMultiStringRead(BufferedReader br, String exit,char except, String start) throws IOException {
		String begin = start;
		String current = "";
		char x;
		boolean exception;
		while(!current.equals(exit)) {
			if(begin.length()>0) {
				x = begin.charAt(0);
				begin = begin.substring(1);
			}else {
				x = (char)br.read();
			}
			
			exception = (x == except);
			if(exception) {
				x = (char)br.read();
			}
			if(x == newLineChar) {
				lines++;
			}
			current+= x;
			if(!exception && current.length()<=exit.length() && 
					current.equals(exit.substring(0, current.length()))) {
				if(current.equals(exit)) {
					current ="";
					return;
				}
			}else {
				//System.out.print(current);
				current = "";
			}

		}
	}

	static void general(File f, Language l) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(f));
		String current = "";
		String expect = ""; // expect is used in case key symbols are parts of others
		boolean ongoing = false;
		char x;
		while(input.ready()) {
			ongoing = false;
			x = (char)input.read();
			if (x == newLineChar) {
				lines++;
			}
			current+=x;
			
			if(!l.blockCommentStart.equals("none")
					&&current.length()<=l.blockCommentStart.length() && current.length()>0 &&
					current.equals(l.blockCommentStart.substring(0, current.length()))) {
				if (current.equals( l.blockCommentStart)) {
					expect = "block comment";
				}
				ongoing = true;
				
			}
			if(!l.blockCommentStart.equals("none") && 
					current.length()>l.blockCommentStart.length() &&
					current.substring(0, l.blockCommentStart.length()).equals(l.blockCommentStart)){
				if(expect.equals("block comment")) {
					inBlockCommentRead(input,l.blockCommentEnd,current.substring(l.blockCommentStart.length()));
					expect ="";
					current="";
				}
			}
			if(!l.singleCommentStart.equals("none") //part of single comment start
					&&current.length()<=l.singleCommentStart.length() && current.length()>0 &&
					current.equals(l.singleCommentStart.substring(0, current.length()))) {

				if (current.equals( l.singleCommentStart)) {
					expect = "single comment";
				}
				ongoing = true;
			}
			if(!l.singleCommentStart.equals("none") && 
					current.length()>l.singleCommentStart.length() &&
					current.substring(0, l.singleCommentStart.length()).equals(l.singleCommentStart)){
				if(expect.equals("single comment")) {
					inSingleCommentRead(input,current.substring(l.singleCommentStart.length()));
					expect ="";
					current="";
				}
				
			} 
			if(!l.stringSymbol1.equals("none")
					&&current.length()<=l.stringSymbol1.length() && current.length()>0 &&
					current.equals(l.stringSymbol1.substring(0, current.length()))) {
				if (current.equals( l.stringSymbol1)) {
					expect = "string";
				}
				ongoing = true;
			}
			if(!l.stringSymbol1.equals("none") && 
					current.length()>l.stringSymbol1.length() &&
					current.substring(0, l.stringSymbol1.length()).equals(l.stringSymbol1)){
				if(expect.equals("string")) {
					inStringRead(input,l.stringSymbol1,l.stringEscape,
							current.substring(l.stringSymbol1.length()));
					expect ="";
					current="";
				}
			}
			if(!ongoing) {
				//System.out.print(current);
				current = "";
				expect = "";
				
			}
		}

		//System.out.print(current);
		if(!current.equals("")) {
			lines++;
		}
		//System.out.println();
		//System.out.println();
		System.out.println("Total # of lines: " + lines);
		System.out.println("Total # of comment lines: " + (singleComments+blockComments));
		System.out.println("Total # of single line comments: "+singleComments);
		System.out.println("Total # of comment lines within block comments: "+blockComments);
		System.out.println("Total # of TODO's: " + todos);
		input.close();
	}

	//
	static void runner(File f) throws IOException {

		boolean success = false;

		String x = f.getName().substring(f.getName().lastIndexOf(46));

		String singleComm = null;
		String blockCommStart = null;
		String blockCommEnd = null;
		String stringStart = null;
		String stringEnd = null;
		String multiStringStart = null;
		String multiStringEnd = null;
		char exception = 0;
		
		File language = new File("files/languages.txt");
		
		BufferedReader languageRead = new BufferedReader(new FileReader(language));
		while(languageRead.ready()) {
			String currentLine = languageRead.readLine();
			if (currentLine.equals("extention: "+x)) {
				singleComm = languageRead.readLine(); //new line character too?
				blockCommStart = languageRead.readLine();
				blockCommEnd = languageRead.readLine();
				stringStart = languageRead.readLine();
				stringEnd = languageRead.readLine();
				exception = languageRead.readLine().charAt(0);
				multiStringStart = languageRead.readLine();
				multiStringEnd = languageRead.readLine();
				success = true;
			}
		}
		
		languageRead.close();
		
		if(!success) { //gets information from user if written in unknown language
			FileWriter fw = new FileWriter(language,true);
			Scanner input = new Scanner(System.in);
			System.out.println("Apologies, but that file type is not currently supported");
			System.out.println("Please input information regarding this language");
			System.out.println("If the language does not support any of these features, "
					+ "please type in \"none\" ");
			System.out.println("Please enter the string that starts a sigle line comment");
			singleComm = input.next();
			System.out.println("Please enter the string that starts a block comment");
			blockCommStart = input.next();
			System.out.println("Please enter the string that ends a block comment");
			blockCommEnd = input.next();
			System.out.println("Please enter the string that starts a string");
			stringStart = input.next();
			System.out.println("Please enter the string that ends a string");
			stringEnd = input.next();
			System.out.println("Please enter the string that begins a multi line string");
			multiStringStart = input.next();
			System.out.println("Please enter the string that ends a multi line string");
			multiStringEnd = input.next();
			System.out.println("Please enter the exception character "
					+ "(how you would print a string excape in a string)");
			exception = input.next().charAt(0);
			input.close();
			
			fw.write("extention: "+x+"\n");
			fw.write(singleComm+"\n");
			fw.write(blockCommStart+"\n");
			fw.write(blockCommEnd+"\n");
			fw.write(stringStart+"\n");
			fw.write(stringEnd+"\n");
			fw.write(exception+"\n");
			fw.write(multiStringStart+"\n");
			fw.write(multiStringEnd+"\n");
			
			fw.close();
			
			
		}
			
			Language unknown = new Language(x,singleComm,blockCommStart,blockCommEnd,
					stringStart,stringEnd,exception, multiStringStart,multiStringEnd);
			general(f,unknown);
		
	}


	public static void main(String args[]) throws IOException {
		File file = new File("files/level1.txt"); //REPLACE THIS FILE
		runner(file);
	}
}

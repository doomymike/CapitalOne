/**
 * 
 * @author Michael Oren
 * class for keeping the key expressions of the given language
 */
public class Language {
	String extention;
	String singleCommentStart;
	String blockCommentStart;
	String blockCommentEnd;
	String stringSymbol1;
	String stringSymbol2;
	String multiStringSymbol1;
	String multiStringSymbol2;
	char stringEscape;
	Language(String extention,String singleCommentStart,String blockCommentStart,
			String blockCommentEnd,String stringSym1,String stringSym2,char stringEscape,
			String multiStringSym1, String multiStringSym2){
		this.extention = extention;
		this.singleCommentStart = singleCommentStart;
		this.blockCommentStart = blockCommentStart;
		this.blockCommentEnd = blockCommentEnd;
		this.stringSymbol1 = stringSym1;
		this.stringSymbol2 = stringSym2;
		this.multiStringSymbol1= multiStringSym1;
		this.multiStringSymbol2= multiStringSym2;
		this.stringEscape = stringEscape;
		
	}
}

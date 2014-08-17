package lab4;

/* 
 * Class for exceptions which generated by automaton.
 * Can shows in what state was exception and last processed symbol.
 */
public class CSVParseException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;

	CSVParseException(String reason, Byte state, char lastSymb){
		message = reason + " State of automaton=" + state.toString() + " Last processed symbol=" + lastSymb;
	}
	
	CSVParseException(String reason){
		message = reason;
	}
	
	public String getMessage(){
		return message;
	}
}

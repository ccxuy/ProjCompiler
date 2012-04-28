/**
 * 
 */
package com.compiler;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrew
 *
 */
public class Grammar {
	public static final boolean CTRL_CASE_SENSITIVE = false;
	public static final String SYMBOL_EXP_IMPLY = "(->)|→";
	public static final String SYMBOL_EXP_OR = "|";
	public static final String SYMBOL_EXP_EPS = "(eps)|(EPS)|ε";
	public static final String SYMBOL_EPS = "ε";
	public static final String SYMBOL_DOLLAR = "$";
	public static final String[] SYMBOL_EXP_SPECIAL = {"\\*",
		"\\+","\\{","\\}","\\(","\\)","\\);","\\[","\\]","\\|\\|"}; 
	static String[] terminals_exp = {"[I|i]d","\\d+" //num
			,"[B|b]asic","[I|i]f","[E|e]lse","[W|w]hile","[D|d]o","[B|b]reak"
			,";","[B|b]lock","\\|\\|","&&","==","!=","<=",">=","<",">","\\+","-","\\*",
			"/","!","=",",","\\{","\\}","\\(","\\)","real","true","false"};
	/*static String[] terminals_caseSense = {"id","\\d" //num
		,"basic","if","else","while","do","break"
		,";","block","\\|\\|","&&","==","!=","<=",">=","<",">","\\+","-","\\*",
		"/","!","\\{","\\}","real","true","false"};
	static String[] terminals_src = {"id","\\d" //num
		,"basic","if","else","while","do","break"
		,";","block","\\|\\|","&&","==","!=","<=",">=","<",">","\\+","-","\\*",
		"/","!","\\{","\\}","real","true","false"};
	String getTerminalsByIndex(int i){
		return terminals_src[i];
	}*/

	private Log log = new Log("Grammar",true);// Allow print log infomation
	
	private String nonTerminal;
	private LinkedList<String> expression = new LinkedList<String>();
	
	
	public Grammar() {
		super();
	}

	public Grammar(String grammar) throws GrammarErrorException {
		super();
		this.nonTerminal = getNonterminalFromString(grammar);
		this.expression = getExpressionFromString(grammar);
	}

	public Grammar(String nonTerminal, LinkedList<String> terminal) {
		super();
		this.nonTerminal = nonTerminal;
		this.expression = terminal;
	}
	
	String getNonterminalFromString(String s) throws GrammarErrorException{
		//Pattern p = Pattern.compile("(?<=^|\n).+?(?=->)",Pattern.DOTALL);
		//Pattern p = Pattern.compile("(?<=^|\n).+?(?=->)");
		Pattern p = Pattern.compile("(?<=^\\s*+).+?(?=\\s*("+SYMBOL_EXP_IMPLY+"))");
		Matcher m = p.matcher(s);
		log.debugInfo(" -> getNonterminalFromString:");
		if( m.find() ){
			String res = m.group();
			log.debugInfo("    "+res);
			return res;
		}
		throw new GrammarErrorException("getNonterminalFromString");
	}
	
	LinkedList<String> getExpressionFromString(String s) throws GrammarErrorException{
		//Pattern p = Pattern.compile("(?<=(->)|\\|).+?(?=$|\n|\\|)",Pattern.DOTALL);
		Pattern p = Pattern.compile("(?<=("+Grammar.SYMBOL_EXP_IMPLY+"|\\|)\\s*+).*?(?=\\s*($|\\n|(?<!\\|)\\|(?!\\|)))",Pattern.DOTALL);
		Matcher m = p.matcher(s);
		log.debugInfo(" -> getExpressionFromString :");
		LinkedList<String> res = new LinkedList<String>();
		while( m.find() ){
			res.add( m.group() );
		}
		log.debugInfo("    "+res);
		
		if(res.isEmpty()){
			throw new GrammarErrorException("getExpressionFromString");
		}else{
			return res;
		}
	}
	
	public String getNonTerminal() {
		return nonTerminal;
	}

	public LinkedList<String> getExpression() {
		return expression;
	}

	public static String getRegExpByString(String s){
		for(String exp:SYMBOL_EXP_SPECIAL){
			if(s.matches(exp)){
				return exp;
			}
		}
		return s;
	}
	
	@Override
	public String toString() {
		return "Grammar [nonTerminal=" + nonTerminal + ", expression="
				+ expression + "]";
	}

	public class GrammarErrorException extends Exception{

		public GrammarErrorException() {}

		public GrammarErrorException(String s) {
			super(s);
		}

		@Override
		public String getMessage() {
			return "GrammarErrorException : "+super.getMessage();
		}

		@Override
		public String toString() {
			return "GrammarErrorException : "+super.toString();
		}
	}
}

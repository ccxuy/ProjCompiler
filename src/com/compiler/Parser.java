package com.compiler;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.compiler.Grammar.GrammarErrorException;

public class Parser {
	Log log = new Log("Parser", true);
	LinkedList<Grammar> grammars = new LinkedList<Grammar>();
	HashSet<String> terminals = new HashSet<String>();
	HashSet<NonTerminalValue> nonTerminals = new HashSet<NonTerminalValue>();
	String[][] parsingTable;
	/*public class Expression{
		public String nonTerminal;
		public String terminal;
		public Expression(String nonTerminal, String terminal) {
			super();
			this.nonTerminal = nonTerminal;
			this.terminal = terminal;
		}
		@Override
		public String toString() {
			return "Expression [nonTerminal=" + nonTerminal + ", terminal="
					+ terminal + "]";
		}
	}*/
	
	public void parse(String s) throws GrammarErrorException{
		//[DONE] divide lines and grammars
		LinkedList<String> lines = lineSeperater(s);
		for (String l : lines) {
			grammars.add(new Grammar(l));
		}
		//[TODO] convert to LL1
		//doConvertGrammarToLL1();
		
		//[DONE] compute each individual non-terminal
		this.nonTerminals.clear();
		for(Grammar g:this.grammars){
			this.nonTerminals.add( new NonTerminalValue(g.getNonTerminal()) );
		}
		//[DONE] compute each individual terminal
		for(Grammar g:this.grammars){
			for(String exp:g.getExpression()){
				this.terminals.addAll(getIndividualTerminals(exp));
			}
		}
		//this.terminals.add(Grammar.SYMBOL_DOLLAR);
		//log.debugInfo(" >this.terminals "+this.terminals);
		doComputeFirstAndFollow();
		log.debugInfo(" >> nonTerminals:\n"+this.nonTerminals);
		//log.debugInfo("\n\n====================\n\n");
		printFirstSet();

		printFollowSet();

		log.debugInfo(" >> terminals:\n"+this.terminals);
		log.debugInfo(" >> grammars:\n"+this.grammars);
		log.debugInfo("\n\n====================\n\n");
		doConstructParsingTable();
		//[TODO]duplicated entry
		printParsingTable();
	}

	private void doConvertGrammarToLL1() {
		boolean newChange = false;
		//do{
		LinkedList<Grammar> gList = new LinkedList<Grammar>();
		for(Grammar g:this.grammars){
			LinkedList<String> prod1 = new LinkedList<String>();
			LinkedList<String> prod2 = new LinkedList<String>();
			LinkedList<String> expression = g.getExpression();

			for(String production : expression){
				if( production.matches("^\\s*"+g.getNonTerminal()+".*") ){
					String e = production.replaceAll("^\\s*"+g.getNonTerminal(), "");
					prod1.add(e+" "+g.getNonTerminal()+"'");
				}else{
					prod2.add(production+" "+g.getNonTerminal()+"'");
				}
			}
			//Exist left recursion
			Grammar g1;
			Grammar g2;
			if( prod1.size()>0 ){
				prod1.add(Grammar.SYMBOL_EPS);
				g1 = new Grammar(g.getNonTerminal(), prod2);
				g2 = new Grammar(g.getNonTerminal()+"'", prod1);
				gList.add(g1);
				gList.add(g2);
			}else{
				gList.add(g);
			}
		}
		this.grammars = gList;
		gList = null;
		for(Grammar g : this.grammars)
			System.out.println(g);
		//}while(newChange == false);
	}

	private void doComputeFirstAndFollow() {
		//[DONE] compute first and follow for each non-terminal
		boolean newChange = true;
		while(newChange){
			newChange = false;
			for(Grammar g:this.grammars){
				if( true == doFirst(getNonTerminalValueByNonTerminal(g.getNonTerminal())
						,getExpressionByNonTerminal(g.getNonTerminal())) ){
					newChange=true;
				}
			}
		}
		
		//Add $ for the first item
		if(this.grammars.toArray().length>0){
			NonTerminalValue nt = getNonTerminalValueByNonTerminal( 
					((Grammar)this.grammars.toArray()[0]).getNonTerminal() );
			nt.addFollow(Grammar.SYMBOL_DOLLAR);
		}else{
			log.err("Add $ for the first item FAIL, no grammar exist!");
		}
		newChange = true;
		while(newChange){
			newChange = false;
			for(NonTerminalValue ntv:this.nonTerminals){
				newChange = doFollow(ntv,getExpressionByNonTerminal(ntv.getNonTerminal()));
			}
		}
	}

	private void printFollowSet() {
		log.debugInfo("========== Follow ==========");
		for(Grammar g:this.grammars){
			log.debugInfo("Follow("+g.getNonTerminal()+")={"+getNonTerminalValueByNonTerminal(g.getNonTerminal()).getFollow()+"}");
		}
	}

	private void printFirstSet() {
		log.debugInfo("========== First ==========");
		for(Grammar g:this.grammars){
			log.debugInfo("First("+g.getNonTerminal()+")={"+getNonTerminalValueByNonTerminal(g.getNonTerminal()).getFirst()+"}");
		}
	}

	private void printParsingTable() {
		System.out.println("==========PARSING TABLE=========");
		for(int ty=0;ty<parsingTable.length;ty++){
			System.out.print(parsingTable[ty][0]+"\t|\t ");
			for(int tx=0;tx<parsingTable[ty].length;tx++){
				if(null != parsingTable[ty][tx]){
					if(tx == parsingTable[ty].length)
						System.out.print(parsingTable[ty][tx]);
					else
						System.out.print(parsingTable[ty][tx]+" , ");
						
				}else{
					System.out.print(" , ");
				}
			}
			System.out.println();
		}
		/*System.out.println("\n------------");
		for(String[] sy:parsingTable){
			for(String si:sy){
				if(si!=null)
					System.out.print(si);
				else
					System.out.print("\t");
			}
			System.out.println();
		}*/
	}

	private void doConstructParsingTable() {
		//[TODO] construct parsing table
		//for title use and $
		LinkedList<String> pTableHeader = new LinkedList<String>();
		LinkedList<NonTerminalValue> nt = new LinkedList<NonTerminalValue>();
		pTableHeader.addAll(terminals);
		pTableHeader.add("$");
		nt.addAll(nonTerminals);
		String tabs = "";
		int max_x = pTableHeader.size()+1;
		int max_y = nt.size()+1;
		int x=0,y=0,i=0;
		parsingTable = new String[max_y][max_x];
		parsingTable[0][0] = tabs;
		for(x=1;x<max_x;x++){
			parsingTable[0][x]=tabs+pTableHeader.get(x-1);
			//log.debugInfo(parsingTable[0][x]);
		}
		/*for(y=0;y<max_y;y++){
			parsingTable[y][0]=tabs+nt.get(y);
		}*/
		log.debugInfo("table size"+max_y+"*"+max_x+"  grammar size>"+this.grammars.size());
		y=0;
		for(Grammar g:this.grammars){
			x=0;
			y++;
			parsingTable[y][x]=g.getNonTerminal();
			NonTerminalValue ntv = getNonTerminalValueByNonTerminal(g.getNonTerminal());
			//[DONE]for each first
			for(String f:ntv.getFirst() ){
				i = searchIndexByString(f,pTableHeader);
				parsingTable[y][i] = tabs;
				//[FIXME] different expression for different nonterminals
				for(String es:g.getExpression()){
					parsingTable[y][i] = parsingTable[y][i]+es;
				}
			}
			//[TODO]for each follow
			//[FIXME] Expression derives eps instead of only eps nulable
			if(ntv.isNullable())
				for(String f:ntv.getFollow() ){
					i = searchIndexByString(f,pTableHeader);
					parsingTable[y][i] = tabs;
					//[FIXME] different expression for different nonterminals
					for(String es:g.getExpression()){
						parsingTable[y][i] = parsingTable[y][i]+es;
					}
				}
		}
	}
	
	private int searchIndexByString(String s,LinkedList<String> l){
		for(int index=0;index<l.size();index++){
			if(s.equals(l.get(index))){
				//log.debugInfo("searchIndexByString>"+index+" "+s+" == "+l.get(index));
				return index;
			}
		}
		log.err("searchIndexByString FAIL, string not found : "+s+" in "+l);
		return 0;
	}

	/**
	 * @param ntv
	 * @param expressionByNonTerminal
	 * @return
	 */
	private boolean doFollow(NonTerminalValue ntv,
			LinkedList<String> expressions) {
		log.debugInfo(" >>> [Before]doFollow> NonTerminalValue:"+ntv+"\texpressions:"+expressions);
		boolean newChange = false;
		for(String exp:expressions){
			if( true == doFollow(ntv, exp)){
				newChange = true;
			}
		}
		log.debugInfo(" >>> [After]doFollow> NonTerminalValue:"+ntv+"\texpressions:"+expressions);
		return newChange;
	}

	/**
	 * @param ntv
	 * @param exp
	 * @return
	 */
	private boolean doFollow(NonTerminalValue ntv, String exp) {
		log.debugInfo("\tdoFollow :　\n\tProduction: "+ntv.getNonTerminal()+" -> "+exp);
		// [DONE] add first(after nonterminal)
		boolean newChange = false;
		String subExp = exp.toString();
		NonTerminalValue nt;
		String f = getFirstFromGrammarExpression(subExp);
		subExp = subExp.replaceFirst(Grammar.getRegExpByString(f), "");
		log.debugInfo("\t ==>getFirstFrom: subExp>"+subExp+" f>"+f);
		// while not empty string
		int iwc = 0;
		while(f!=null&&false == subExp.matches("^\\s*$")){
			log.debugInfo(" >> subExp: "+subExp+"  f: "+f);
			if(subExp.matches("^\\s*$"))
				break;
			// A -> aBb 
			NonTerminalValue b, beta;
			if( null != (b=getNonTerminalValueByNonTerminal(f)) ){
				f = getFirstFromGrammarExpression(subExp);
				if( null != (beta=getNonTerminalValueByNonTerminal(f)) ){
					//Add not eps
					log.debugInfo("\tAdd First("+beta.getNonTerminal()+")= "+beta.getFirst()+" to Follow("+b.getNonTerminal()+")");
					for(String s:beta.getFirst()){
						if(false == s.matches(Grammar.SYMBOL_EXP_EPS)){
							if( true == b.addFollow(s)){
								newChange = true;
							}
						}
					}
					subExp = subExp.replaceFirst(beta.getNonTerminal(), "");
				}else{
					if( true == b.addFollow(f)){
						newChange = true;
					}
				}
			}
			subExp = subExp.replaceFirst(Grammar.getRegExpByString(f), "");
			f = getFirstFromGrammarExpression(subExp);
		}
		subExp = exp.toString();
		while(false == subExp.matches("^\\s*$")){
			String tail = getTailFromExpression(subExp);
			if( tail!=null && null != (nt=getNonTerminalValueByNonTerminal(tail)) ){
				if( true == nt.addFollow(ntv.getFollow()) ){
					newChange = true;
				}
				if(nt.isNullable()){
					subExp = subExp.replaceFirst(tail+"\\s*$", "");
				}else{
					break;
				}
			}else{
				break;
			}
		}
		
		return newChange;
	}

	/**
	 * @param exp
	 * @return
	 */
	protected String getTailFromExpression(String exp) {
		Pattern p = Pattern.compile("((\\b[\\w']+\\b)|([^\\w'\\s]+))(?=\\s*)$");//simple tested
		Matcher m = p.matcher(exp);
		log.debugInfo(" -> getTailFromExpression :");
		if( m.find() ){
			String res = m.group();
			log.debugInfo("    "+res);
			return res;
		}
		//[QUEST] should return eps?
		log.debugInfo(" getTailFromExpression return null : should return $?");
		return null;
	}

	/**
	 * @param ntv NonTerminalValue
	 * @param expressions Expression
	 * @return true if new value input into first set
	 */
	private boolean doFirst(NonTerminalValue ntv, LinkedList<String> expressions) {
		log.debugInfo("  >doFirst: ntv>"+ntv+" exp>"+expressions);
		boolean newChange = false;
		for(String exp:expressions){
			log.debugInfo("\t[Before]doFirst : ntv>"+ntv+" exp>"+exp);
			if( true == doFirst(ntv, exp)){
				newChange = true;
			}
			log.debugInfo("\t[After]doFirst : ntv>"+ntv+" exp>"+exp);
		}
		return newChange;
	}
	
	/**
	 * @param ntv NonTerminalValue
	 * @param exp Expression
	 * @return true if new value input into first set
	 */
	private boolean doFirst(NonTerminalValue ntv, String exp) {
		//[FIXME] can't do first() after eps
		boolean newChange = false;
		String firstSymbol = getFirstFromGrammarExpression(exp);
		// [DONE] If first is itself, return false
		if(exp.equals(ntv.getNonTerminal())){
			return newChange;
		}
		
		// [DONE] If terminal add
		if (this.terminals.contains(firstSymbol)) {
			if(true == ntv.addFirst(firstSymbol)){
				log.debugInfo("\tAdd therminal -> addFirst:"+firstSymbol);
				newChange = true;
			}
			// [DONE] If imply to eps, add eps.
		} else if (firstSymbol.matches(Grammar.SYMBOL_EXP_EPS)) {
			if(true == ntv.addFirst(firstSymbol)){
				log.debugInfo("\tAdd eps -> addFirst:"+firstSymbol);
				newChange = true;
			}
			//if non-terminal
		} else {
			// [DONE] Ignore eps from head, add first(after).
			String subExp = exp;
			boolean nullable = false;
			do {
				log.debugInfo("\tCheck Nullable to dereritive > "+subExp);
				//The one need to check nullable
				NonTerminalValue nt = 
					getNonTerminalValueByNonTerminal( 
							getFirstFromGrammarExpression(subExp) );
				//if next element is terminal stop check nullable
				if(null==nt){
					break;
				}
				//Nullable
				nullable = nt.isNullable();
				/*for(String s:nt.getFirst()){
					if(true == s.matches(Grammar.SYMBOL_EXP_EPS)){
						nullable = true;
					}
				}*/
				if(nullable){
					// [DONE] If all eps, add eps.
					//If nothing left to continue first
					if(subExp.matches("^\\s+?$")){
						if(true == ntv.addFirst(Grammar.SYMBOL_EPS)){
							log.debugInfo("\t\tadd first("+Grammar.SYMBOL_EPS+")");
							newChange = true;
						}
						break;
					}
					// [DONE] If eps, delete the first non-terminal and try again.
					subExp = subExp.replaceFirst(nt.getNonTerminal(), "");
					
				}else{
					// [DONE] If non-terminal and not eps, first(after).
					log.debugInfo("\t\t[add eps first(after)]add first("+nt.getNonTerminal()+")="+nt.getFirst());
					if(true == ntv.addFirst(nt.getFirst())){
						newChange = true;
					}
					break;
				}
				
			} while (false==subExp.matches("^\\s*$"));
			
		}

		return newChange;
	}

	/**
	 * @param nonTerminal
	 * @return
	 */
	protected NonTerminalValue getNonTerminalValueByNonTerminal(
			String nonTerminal) {
		for(NonTerminalValue nt:this.nonTerminals){
			if(nonTerminal.equals(nt.getNonTerminal())){
				return nt;
			}
		}
		log.debugInfo("\t\tgetNonTerminalValueByNonTerminal : NonTerminalValue not found! nonTerminal="+nonTerminal);
		return null;
	}

	/**
	 * @param exp
	 * @return
	 */
	protected String getFirstFromGrammarExpression(String exp) {
		Pattern p = Pattern.compile("(?<=^|(\\s|\\b))(\\b[\\w']+\\b)|([^\\w'\\s]+)");//verified
		Matcher m = p.matcher(exp);
		if( m.find() ){
			String res = m.group();
			//log.debugInfo(" -> getFirstFromGrammarExpression :"+res);
			return res;
		}
		//[QUEST] should return eps?
		log.debugInfo(" getFirstFromGrammarExpression return null : should return eps? exp="+exp);
		return null;
	}

	protected LinkedList<String> getExpressionByNonTerminal(String nonTerminal){
		for(Grammar g:this.grammars){
			if(g.getNonTerminal().equals(nonTerminal)){
				return g.getExpression();
			}
		}
		//[XXX] Need to implement exception here
		log.err(" getExpressionByNonTerminal : nonterminal not found!");
		return null;
	}
	
	/**
	 * @warning no "|" "->" !!
	 * @param exp
	 * @return
	 */
	protected Collection<? extends String> getIndividualTerminals(String exp) {
		// [DONE] getIndividualTerminals
		HashSet<String> res = new HashSet<String>();
		Pattern p = Pattern.compile("(?<=^|(\\s|\\b))(\\b[\\w']+\\b)|([^\\w'\\s]+)");//verified
		Matcher m = p.matcher(exp);
		log.debugInfo(" -> getIndividualTerminals :");
		while( m.find() ){
			String terminal = m.group();
			//if not non-terminal
			if(null == getNonTerminalValueByNonTerminal(terminal)){
				res.add(terminal);
			}
		}
		log.debugInfo("    "+res);
		return res;
	}

	LinkedList<String> lineSeperater(String input){
		//[QUEST]
		// Pattern p = Pattern.compile("(?<=^|\n).+?(?=$|\n)");

		Pattern p = Pattern.compile("(?<=^|\n).+?(?=$|\n)",Pattern.DOTALL);
		Matcher m = p.matcher(input);
		log.debugInfo(" -> lineSeperater : input>"+input);
		LinkedList<String> res = new LinkedList<String>();
		while( m.find() ){
			res.add(m.group());
			log.debugInfo("    "+res);
		}
		return res;
	}
	
	void getExpArrayFromString(String s){
		Pattern p = Pattern.compile("(?<=^|\n).+?(?=->)",Pattern.DOTALL);
		Matcher m = p.matcher(s);
		log.debugInfo(" -> lineSeperater:");
		while( m.find() ){
			String res = m.group();
			log.debugInfo("    "+res);
		}
	}
	
	void getTerminalsFromString(String s){
		//Pattern p = Pattern.compile("(?<=(->)|\\|).+?(?=$|\n|\\|)",Pattern.DOTALL);
		Pattern p = Pattern.compile("(?<=(->)|\\|).+?(?=$|\n|\\|)",Pattern.DOTALL);
		Matcher m = p.matcher(s);
		log.debugInfo(" -> lineSeperater:");
		while( m.find() ){
			String res = m.group();
			log.debugInfo("    "+res);
		}
	}

}

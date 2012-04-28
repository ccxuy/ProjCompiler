/**
 * 
 */
package com.compiler;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrew
 *
 */
public class Scanner {
	boolean printDebugInfo = false;
	Log log_scanner = new Log("Scanner", printDebugInfo);
	Stack<String> tokenStack = new Stack<String>();
	
	public Scanner() {
		this.tokenStack = new Stack<String>();
	}
	public void scan(String s){
		scan(new StringBuffer(s));
	}
	public void scan(StringBuffer s){
		int count = 0;
		if(s==null){
			log_scanner.err("<Scan> s==null!");
			throw new NullPointerException("<Scan> s==null!");
		}
		while(s.length()>0){
			log_scanner.debugInfo("Scan No."+ ++count +"  >"+s);
			//Remove space and other special symbol from beginning
			while(s.length()>0 
					&& (s.charAt(0)==' ' || s.charAt(0)=='\n' || s.charAt(0)=='\r' || s.charAt(0)=='\t')){
				s.deleteCharAt(0);
			}
			//Recognize first token from grammar and put into token stack
			String token = takeOutToken(s);
			if(token != null){
				tokenStack.add(token);
			}else{
				break;
			}
		}
		//print token stack
		for(String t:tokenStack){
			System.out.print(" <"+t+"> ");
		}
		System.out.println();
	}

	public String takeOutToken(StringBuffer src) {
		String sourceTmp = "err";
		boolean foundToken = false;
		String token = " err token";
		for(String token_exp:Grammar.terminals_exp){
			if( (token=getTokenFromString(src, "^"+token_exp)) !=null){
				foundToken = true;
				String[] outSplit=src.toString().split("^"+token_exp);
				if(0==outSplit.length){// if no element left
					sourceTmp="";
				}else if(1==outSplit.length){//if it is the last element
					sourceTmp=outSplit[0];
				}else if(2==outSplit.length){
					sourceTmp=outSplit[1];
				}
				break;
			}
		}
		//Check if found token
		if(foundToken == true){
			log_scanner.debugInfo("find token: "+token);
			src.replace(0, src.length(), sourceTmp);//Put remaining part of source into s
			log_scanner.debugInfo("Output source: "+src.toString());
			return token;
		//If can't find token print err and exception
		}else{
			log_scanner.err("Invalid input or grammar, token not found!");
			log_scanner.err("Input: "+src);
			return null;
		}
	}
	String getTokenFromString(StringBuffer sb, String exp){
		String token;
		Pattern p = Pattern.compile(exp);
		Matcher m = p.matcher(sb);
		if( m.find() ){
			token = m.group();
			//log_scanner.in(" $getTokenFromString use:\n"+exp+"\n from \n"+sb+"\n <token>: "+token);
			return token;
		}else{
			return null;
		}
	}
}

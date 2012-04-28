/**
 * 
 */
package com.compiler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.compiler.Grammar.GrammarErrorException;

/**
 * @author Andrew
 *
 */
public class main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String s1 = "{Basic id;If (id==5) id=65;Else id=75;While (id<=1000 && id>10)  Id=id*2;}";
		String s2 = "{Basic id1,id2;If (id1==5) id1=65;Else id2=75;Id3=id1+5*6+id2+id1;Id4=true && false && 3<5;}";
	   	Scanner sc = new Scanner();
	   	//sc.scan(s2);
	   	Pattern p = Pattern.compile( "(?<=("+Grammar.SYMBOL_EXP_IMPLY+"|\\|)\\s*+).*?(?=\\s*($|\\n|(\\b\\|\\b)))" );
		Matcher m = p.matcher("program -> block \n block -> {decls stmts}||fd|d");
		while( m.find() ){
			String res = m.group();
			//Log.in(" $getTokenFromString use:\n"+exp+"\n from \n"+sb+"\n <token>: "+token);
			System.out.println("=============");
			//System.out.println(res);
		}
		String parseInfo = "program → block \n" +
				"block → {decls stmts}\n" +
				"decls → decls decl | ε\n" +
				"decl → type id;\n" +
				"type → type[num]|basic\n" +
				"stmts → stmts stmt| ε\n" +
				"stmt → loc=bool;" +
				"         |    if (bool) stmt" +
				"         |    if (bool) stmt else stmt" +
				"         |    while (bool) stmt" +
				"         |    do stmt while (bool);"+
				"		|    break;" +
				"		|    block\n" +
				"		loc → loc[bool] | id\n" +
				"		bool  → bool || join | join\n" +
				"join  → join && equality | equality\n" +
				"equality  → equality == rel | equality!=rel | rel\n" +
				"rel → expr < expr | expr <= expr | expr >= expr | expr > expr | expr \n" +
				"expr → expr+term | expr-term |term\n" +
				"term → term * unary |term/unary|unary\n" +
				"unary → !unary| -unary | factor\n" +
				"factor→ (bool) | loc | num | real | true | false\n";
		Parser parser = new Parser();
		parser.parse(parseInfo);
		//System.out.println("{decls stmts}".replaceFirst("{", ""));
	}
	void openFile() throws Exception{
		File f = new File("D:\\Workspace\\ProjCompiler\\grammar");
		FileInputStream fis = new FileInputStream(f);
		//BufferedInputStream bis = new BufferedInputStream(new InputStreamReader(fis));
	}

}

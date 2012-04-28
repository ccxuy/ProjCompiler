/**
 * 
 */
package com.compiler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author Andrew
 * 
 */
public class NonTerminalValue {
	private String nonTerminal;
	private HashSet<String> first = new HashSet<String>();
	private HashSet<String> follow = new HashSet<String>();

	public NonTerminalValue() {
		super();
	}

	/**
	 * @param nonTerminal2
	 */
	public NonTerminalValue(String nonTerminal) {
		this.nonTerminal = nonTerminal;
	}

	public NonTerminalValue(String nonTerminal, HashSet<String> first,
			HashSet<String> follow) {
		super();
		this.nonTerminal = nonTerminal;
		this.first = first;
		this.follow = follow;
	}

	public String getNonTerminal() {
		return nonTerminal;
	}

	public HashSet<String> getFirst() {
		return first;
	}

	public boolean addFirst(String firstNT) {
		if (first.contains(firstNT)) {
			return false;
		} else {
			first.add(firstNT);
			return true;
		}
	}

	/**
	 * @param first
	 * @return
	 */
	public boolean addFirst(Collection<? extends String> first) {
		boolean newChange = false;
		for(String s:first){
			if(true == addFirst(s)){
				newChange=true;
			}
		}
		return newChange;
	}
	
	public HashSet<String> getFollow() {
		return follow;
	}

	public boolean addFollow(String followNT) {
		if (follow.contains(followNT)) {
			return false;
		} else {
			follow.add(followNT);
			return true;
		}
	}
	
	/**
	 * @param follow
	 * @return
	 */
	public boolean addFollow(Collection<? extends String> follow) {
		boolean newChange = false;
		for(String s:follow){
			if(true == addFollow(s)){
				newChange=true;
			}
		}
		return newChange;
	}

	public boolean isNullable() {
		for (String s : this.first) {
			if (s.matches(Grammar.SYMBOL_EXP_EPS))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "NonTerminalValue [nonTerminal=" + nonTerminal + ", first="
				+ first + ", follow=" + follow + "]";
	}


}

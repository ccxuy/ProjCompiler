package com.compiler;

public class Log {
	public final static boolean PrtSc = true;
	private String title;
	private int msgLevel = 0;
	private boolean printTitle = false;
	private boolean PrtSc_Subcontrol = true;
	
	
	public Log(boolean prtSc_Subcontrol) {
		super();
		PrtSc_Subcontrol = prtSc_Subcontrol;
	}
	public Log(String name, boolean prtSc_Subcontrol) {
		super();
		this.title = name;
		PrtSc_Subcontrol = prtSc_Subcontrol;
	}
	public Log(String name, int msgLevel, boolean printTitle,
			boolean prtSc_Subcontrol) {
		super();
		this.title = name;
		this.msgLevel = msgLevel;
		this.printTitle = printTitle;
		PrtSc_Subcontrol = prtSc_Subcontrol;
	}
	public void debugInfo(String s){
		if(printTitle){
			for(int i=0;i<msgLevel;i++)System.out.print("\t");
			System.out.println(" [ "+title+" ] : ");
		}
		if(PrtSc && PrtSc_Subcontrol){
			for(int i=0;i<msgLevel;i++)System.out.print("\t");
			System.out.println(s);
		}
	}
	public void err(String s){
		if(PrtSc && PrtSc_Subcontrol){
			for(int i=0;i<msgLevel;i++)System.out.print("\t");
			System.err.println(s);
		}
	}

}

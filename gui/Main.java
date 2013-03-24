package gui;
import java.util.ArrayList;
import java.util.HashSet;

import mir.MIR_logger;


public class Main {
	public static void main(String[] args) {
		MirGui mir = new MirGui(600,500);
		MIR_logger.setLogger(mir);
		
		mir.setText("/home/ana/FEUP/COMP2/mir/teste5.mir");
		mir.setRegisters("lolololl\nasd");
	}
}

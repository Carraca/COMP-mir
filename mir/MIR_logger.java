package mir;

import java.text.SimpleDateFormat;
import java.util.Date;

import gui.MirGui;

public class MIR_logger {
	static MirGui gui_logger;
	static SimpleDateFormat hour_format = new SimpleDateFormat("[HH:mm:ss]");
	
	public static void setLogger(MirGui mirgui) {
		gui_logger = mirgui;
	}
	
	public static String make_message(String msg) {
		Date dateNow = new Date();
		 
		StringBuilder hourNow = new StringBuilder( hour_format.format( dateNow ) );
		return hourNow + " " + msg;
		
	}
	
	public static void log(String msg) {
		String full_log_msg = make_message(msg);
		gui_logger.log(full_log_msg);
		System.out.println(full_log_msg);
	}

	public static void log(String string, long start_time, long end_time) {
		log(string + " [" + new Long(end_time - start_time).toString() + "ms]");
		
	}
}

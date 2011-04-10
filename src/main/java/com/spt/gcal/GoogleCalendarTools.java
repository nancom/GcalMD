package com.spt.gcal;

import com.spt.gcal.CalendarUtil;
import java.util.List;

public class GoogleCalendarTools{
public static void main(String args[]){
	if(args.length < 4){
		System.out.println("Usage :java -jar GoogleCal.jar {gmail} {password} {start date}[yyyy-MM-dd] {end date}[yyyy-MM-dd]");
	}else{
		CalendarUtil cu = new CalendarUtil(args[0],args[1]);
		List<String> result = cu.calculateMDByCustomerAndModule(cu.getDataBetweenDate(args[2],args[3]));
		for(String res:result){
			System.out.println(res);
		}
	}

}
}


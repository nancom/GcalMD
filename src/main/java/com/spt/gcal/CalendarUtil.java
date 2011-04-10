package com.spt.gcal;

/**
 * Hello google Calendar world!
 *
 */
import com.google.gdata.client.Query;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.calendar.WebContent;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.Recurrence;
import com.google.gdata.data.extensions.Reminder;
import com.google.gdata.data.extensions.Reminder.Method;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.lang.Double;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Date;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spt.gcal.domain.Job;

public class CalendarUtil{

    private static Logger logger = LoggerFactory.getLogger(CalendarUtil.class);

	private String email;
	private String password;
    
	private static final String METAFEED_URL_BASE = "https://www.google.com/calendar/feeds/";

	// The string to add to the user's metafeedUrl to access the event feed for
	// their primary calendar.
	private static final String EVENT_FEED_URL_SUFFIX = "/public/full";

	// The URL for the metafeed of the specified user.
	// (e.g. http://www.google.com/feeds/calendar/jdoe@gmail.com)
	private static URL metafeedUrl = null;

	// The URL for the event feed of the specified user's primary calendar.
	// (e.g. http://www.googe.com/feeds/calendar/jdoe@gmail.com/private/full)
	private static URL eventFeedUrl = null;
	
	private CalendarService service = null;
	
	public CalendarUtil(String mail,String password){
	       this.email = mail;
		   this.password = password;
		   
		   service = new CalendarService("nancom-calendar");
           
		   try{   
				metafeedUrl = new URL(METAFEED_URL_BASE + this.email);
				eventFeedUrl = new URL(METAFEED_URL_BASE + this.email + EVENT_FEED_URL_SUFFIX);
		        
				service.setUserCredentials(this.email, this.password);
		   } catch (Exception e) {
				logger.error(e.getMessage());
			}

	}
	
	public void setCredential(String mail,String password){
	       this.email = mail;
		   this.password = password;
	}
	


	public void printUserCalendars(){
		
		try{


			// Send the request and receive the response:
	    	CalendarFeed resultFeed = (CalendarFeed)service.getFeed(metafeedUrl, CalendarFeed.class);
			logger.debug("Your calendars:");
			logger.debug("===============================");
			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEntry entry = (CalendarEntry)resultFeed.getEntries().get(i);
				logger.debug("{}",entry.getTitle().getPlainText());
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}

	    logger.debug("=============================");
	}
	
    public List getDataBetweenDate(String startDate,String endDate){
		List<Job> dataList = new ArrayList<Job>();
        List<Date> dateList = new ArrayList<Date>();

		dateList = this.getDateBetween(startDate,endDate);
        
		String startTime = "";
		String endTime = "";
		for(Date date:dateList){
			startTime = this.convertToGoogleDateFormat(date);
            startTime = startTime.split("T")[0]+"T00:00:00";
            endTime = startTime.split("T")[0]+"T23:59:59";
			List<Job> buff = dateRangeQuery(startTime,endTime);
			logger.debug("result :{}",buff);
			for(Job job:buff){
				dataList.add(job);
			}
		}

		return dataList;
	
	}

    public List dateRangeQuery(String startTime, String endTime){
		List<Job> dataList = new ArrayList<Job>();

		try{
		CalendarQuery myQuery = new CalendarQuery(eventFeedUrl);
		myQuery.setMinimumStartTime(DateTime.parseDateTime(startTime));
		myQuery.setMaximumStartTime(DateTime.parseDateTime(endTime));
		// Send the request and receive the response:
		
		CalendarEventFeed resultFeed = service.query(myQuery,CalendarEventFeed.class);

	    logger.debug("Events from {} to {} ",startTime.toString(),endTime.toString());
	    logger.debug("============================================");
		
		 for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEventEntry entry = resultFeed.getEntries().get(i);
		    	
			String head = entry.getTitle().getPlainText();
            
			logger.debug("{} : {}",head.toUpperCase().indexOf("PERSONAL"),head);
			
			if(head.toUpperCase().indexOf("PERSONAL") < 0 && head.toUpperCase().indexOf(":") >= 0){
				for(When time:entry.getTimes()){
					logger.debug("{} - {}",time.getStartTime(),time.getEndTime());
					Job job = new Job();
					job.setStartDate(new Date(time.getStartTime().getValue()));
					job.setEndDate(new Date(time.getEndTime().getValue()));
					job.setCustomer(head.split(",")[0].trim());
					job.setModule(head.split(",")[1].split(":")[0].trim());
					job.setDetail(head.split(":")[1].trim());
					dataList.add(job);
				}
			}
		 } 
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		return dataList;
	}

	public String convertToGoogleDateFormat(Date input){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result = new String();
		logger.debug("Date Time:{}",input);
		
		result = dateFormat.format(input);
		logger.debug(result.split(" ")[0]+"T"+result.split(" ")[1]);
		return result.split(" ")[0]+"T"+result.split(" ")[1];
				
	}

    public List<Date> getDateBetween(String startDate , String endDate){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar sDate = Calendar.getInstance(); 
        Calendar eDate = Calendar.getInstance();
	    List<Date> resDate = new ArrayList<Date>();

        try{		
			sDate.setTime(dateFormat.parse(startDate));
			eDate.setTime(dateFormat.parse(endDate));

        	sDate.clear(Calendar.MILLISECOND);
			sDate.clear(Calendar.SECOND);
	    	sDate.clear(Calendar.MINUTE);
	    	sDate.clear(Calendar.HOUR_OF_DAY);
												    
			eDate.clear(Calendar.MILLISECOND);
			eDate.clear(Calendar.SECOND);
			eDate.clear(Calendar.MINUTE);
	    	eDate.clear(Calendar.HOUR_OF_DAY);
		
			logger.debug(" Start Date :{} - End Date :{}",sDate.getTime(),eDate.getTime());
            if(sDate.equals(eDate)){
				resDate.add(sDate.getTime());
			}else{
				resDate.add(sDate.getTime());
	    		while ( sDate.before(eDate) ) {    
					Calendar cDate = Calendar.getInstance();
	           		sDate.add(Calendar.DATE, 1);
                    cDate.setTime(sDate.getTime());
					resDate.add(cDate.getTime());
	    		}
			}
        	logger.debug("Distance of Day :{} ",resDate.size());

				
		}catch(java.text.ParseException pe){
			logger.error(pe.getMessage());
		}
			
	    return resDate;
	}
	
	public double calculateMDByCustomer(String customer,List<Job> jobs){
		
		long md = 0L;
		
		if(jobs == null || jobs.size() == 0){
			return 0;
		}else{
			for(Job job:jobs){
				if(customer.equalsIgnoreCase(job.getCustomer().trim())){
					logger.debug("Date :{} - Time {}",job.getStartDate(),job.getEndDate().getTime() - job.getStartDate().getTime());
					md += (job.getEndDate().getTime() - job.getStartDate().getTime());
				}
			}
		
		}

        logger.debug(" MD of {} =  {} ",customer,this.calMD(md));
		return this.calMD(md);
	
	}
    
   public Set getCustomerAndModule(List<Job> jobs){
   		Set res = new HashSet();
		for(Job job:jobs){
			
			if(res.contains(job.getCustomer().trim()+"#"+job.getModule().trim())){
				logger.debug("!!Already Has Data !!");
			}else{
				res.add(job.getCustomer().trim()+"#"+job.getModule().trim());
			}
		}
		logger.debug("Customer and module :{}",res);
		return res;
   
   }

   public List<String> calculateMDByCustomerAndModule(List<Job> jobs){
   	   List<String> result = new ArrayList<String>();	
	   Set customerModule = this.getCustomerAndModule(jobs);
		Iterator iter = customerModule.iterator();
		String customerMod = "";
        long md = 0L;  
		
		
		while(iter.hasNext()){
			customerMod = (String)iter.next();
			for(Job job:jobs){
				if(customerMod.trim().equalsIgnoreCase(job.getCustomer().trim()+"#"+job.getModule().trim())){
					md += (job.getEndDate().getTime() - job.getStartDate().getTime());
				}
			}
			logger.debug("Customer Module:{} - MD=[{}]",customerMod,this.calMD(md));
			result.add(customerMod.split("#")[0]+","+customerMod.split("#")[1]+":"+this.calMD(md));
			md = 0L;
		}
		logger.debug("{}",result);
		return result;
   }

   public double calMD(long millisec){
   		double md = 0.0D;
		md = (double)millisec/(28800000L);
		logger.debug("MD={} {}",md,(double)millisec/(28800000L));
		return md;
   }
}

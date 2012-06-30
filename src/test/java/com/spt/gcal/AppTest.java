package com.spt.gcal;


import com.spt.gcal.CalendarUtil;
import com.spt.gcal.domain.Job;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.data.DateTime;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Unit test for simple App.
 */
public class AppTest {
    private static Logger logger = LoggerFactory.getLogger(AppTest.class);
    CalendarUtil cu;
    private String user = "";
	private String password = "";

    public AppTest() {
        logger.debug("username:{} password:####",System.getProperty("username"));
        cu = new CalendarUtil(user.length()!=0?user:System.getProperty("username")+"@gmail.com", password.length()!=0?password:System.getProperty("password"));
    }
    
    @Before
    public void setUp() {
        logger.info("-= Setup =-");					        
    }

    @After
    public void tearDown() {
        logger.info("-= TearDown =-");
    }

    @Test
    public void testApp() {
        cu.printUserCalendars();
        assertTrue(true);
    }

    @Test
    public void testConvertToGoogleDateFormat() {
        String base = "2011-08-08T00:00:00";
        String result = new String();

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, 2011);
        c.set(Calendar.MONTH, 7);
        c.set(Calendar.DATE, 8);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        
        logger.debug("Date Time:{}", c.getTime());

        result = cu.convertToGoogleDateFormat(c.getTime());
        assertEquals(base, result);
    }	
	
    @Test
    public void testgetDateBetween() {
        String startDate = "2011-08-01";
        String endDate = "2011-08-31";
        int base = 31;

        assertEquals(base, cu.getDateBetween(startDate, endDate).size());

        startDate = "2011-08-01";
        endDate = "2011-08-31";

        assertEquals(31, cu.getDateBetween(startDate, endDate).size());
    }

    @Test 
    public void testGetDataBetweenDate() {
        int base = 5;
        String startDate = "2011-06-01";
        String endDate = "2011-06-30";
        logger.debug("Data size : {}",cu.getDataBetweenDate(startDate, endDate).size()); 
        assertEquals(base, cu.getDataBetweenDate(startDate, endDate).size());
	
    }	
	
    @Test
    public void testGetEvent() {
        List<Job> result = cu.dateRangeQuery("2011-04-08T00:00:00",
                "2011-04-08T23:59:59");

        if (result != null && result.size() > 0) {
            Collections.sort(result, Job.SequenceComparator);
        }				        
        assertNotNull(result);

        for (Job job:result) {
            logger.debug("{}", job);
        }
    }
    
    @Test
    public void testCalculateMDBycustomer() {
        String customer = "BIGC";
        String startDate = "2011-03-01";
        String endDate = "2011-03-30";

        assertEquals(14.8125,
                cu.calculateMDByCustomer(customer,
                cu.getDataBetweenDate(startDate, endDate)),
                0);
    }
    
    @Test
    public void testGetCustomerAndModule() {
        String startDate = "2011-02-01";
        String endDate = "2011-02-28"; 	
		
        List<Job> jobs = cu.getDataBetweenDate(startDate, endDate);
		
        Set customerModule = cu.getCustomerAndModule(jobs);	

        assertEquals(5, customerModule.size());
    }

    @Test
    public void testCalculateMDByCustomerAndModule() {
        String startDate = "2011-02-01";
        String endDate = "2011-02-28";

        List<Job> jobs = cu.getDataBetweenDate(startDate, endDate);

        List<String> base = new ArrayList<String>();
        List<String> result = new ArrayList<String>();

        base.add("MP,HR:1.5");
        base.add("SPT,MNG:2.625");
        base.add("MP,CMIS:2.5");
        base.add("RMS,RPTGN:2.875");
        base.add("CTEP,TSS:3.375");
	
        result = cu.calculateMDByCustomerAndModule(jobs);
        assertEquals(base, result);

    }

    @Test
    public void testCalMD() {
        logger.debug("{}", cu.calMD(43200000L));
        assertTrue(true);
    }
}
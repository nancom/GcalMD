package com.spt.gcal.domain;

import java.util.Comparator;
import java.util.Date;

public class Job implements Comparable<Job>{
	
	private Date startDate;
	private Date endDate;
	private String customer;
	private String module;
	private String detail;
    
    public void setStartDate(Date startDate){
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}

	public void setCustomer(String customer){
		this.customer = customer;
	}

	public void setModule(String module){
		this.module = module;
	}

	public void setDetail(String detail){
		this.detail = detail;
	}

	public Date getStartDate(){
		return this.startDate;
	}

	public Date getEndDate(){
		return this.endDate;
	}

	public String getCustomer(){
		return this.customer;
	}

	public String getModule(){
		return this.module;
	}

	public String getDetail(){
		return this.detail;
	}

	@Override	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[Customer :").append(this.customer).append(",");
		sb.append("Module :").append(this.module).append(",");
		sb.append("Start Date :").append(this.startDate).append(",");
		sb.append("End Date :").append(this.endDate).append(",");
		sb.append("Detail :").append(this.detail).append("]");
        return sb.toString();
	}
	
	@Override
	public int compareTo(Job o) throws ClassCastException{
		if (!(o instanceof Job)){
			throw new ClassCastException("A ReportParameter object expected.");
		}
		return this.startDate.compareTo(((Job) o).getStartDate());
	}

	public static Comparator SequenceComparator = new Comparator() {
		public int compare(Object job, Object anotherJob) {
			return ((Job)job).getStartDate().compareTo(((Job) anotherJob).getStartDate());
		}
	};

}

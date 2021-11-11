package com.loggerreader;
 

import org.springframework.stereotype.Component;

@Component
public class LogAnalysis {
	
	//take start and finish objects and calculate   timestamp
	 public Event calculateDuration(TransactionReader startTimestamp, TransactionReader finisTtimestamp) {
	        Long duration =  startTimestamp.getTimestamp() - startTimestamp.getTimestamp();
	        boolean isAlert = duration > 4;
	        return new Event(startTimestamp.getId(), duration, startTimestamp.getType(), startTimestamp.getHost(), isAlert);
	    }

}

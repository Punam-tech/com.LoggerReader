package com.loggerreader.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loggerreader.Event;
import com.loggerreader.LogAnalysis;
import com.loggerreader.TransactionReader;
import com.loggerreader.db.DBOperation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class Main implements CommandLineRunner{
	
	private static Logger log = LoggerFactory.getLogger(Main.class);
    private final ObjectMapper objectMapper;
    private final Connection connection;
    private final LogAnalysis anaLog;

    private Map<String, TransactionReader> startedMap = new ConcurrentHashMap<>();
    private Map<String, TransactionReader> finishedMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        log.info(" Application Starting in Process ... ");
        SpringApplication.run(Main.class, args);
       
    }

    @Autowired
    public Main(ObjectMapper objectMapper, Connection connection, LogAnalysis anaLog) {
        this.objectMapper = objectMapper;
        this.connection = connection;
        this.anaLog = anaLog;
    }

    @Override
    public void run(String... args) throws IOException {
        if (args.length != 1 || args[0].isEmpty()) {
            throw new InvalidParameterException("Please Enter logFile Path ::");
        }

        String filePath = args[0];

        log.info("Open file {} for processing", filePath);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            bufferedReader.lines().forEach(this::groupByState);
            processData(startedMap.keySet());

            log.info("Finished processing file {}, closing...", filePath);
        } catch (IOException e) {
            log.error("Failure reading the file, exiting...", e);
            throw e;
        }
    }

     
    private void groupByState(String json) {
        try {
            
          TransactionReader tr = Optional.ofNullable(objectMapper.readValue(json, TransactionReader.class)).orElseThrow(() -> new NullPointerException("Failed to convert json"));

          if (tr.getState().equals("STARTED")) {
                startedMap.put(tr.getId(), tr);
            } else {
                finishedMap.put(tr.getId(), tr);
            }
        } catch (IOException e) {
            log.error("Failure processing json {} ", json);
        }
    }

    /**
     * Takes event ids and finds corresponding start and finish events if found saves resulting event and its duration
     *
     * @param ids
     */
    private void processData(Set<String> ids) {
        try(DBOperation dbevent = new DBOperation(connection)) {
            for (String id : ids) {
            	TransactionReader startTimestamp = startedMap.get(id);
            	TransactionReader finishTimestamp = finishedMap.get(id);
                if (startTimestamp!= null && finishTimestamp != null) {
                    log.info("Converting eventDTO to event...");
                    Event event = anaLog.calculateDuration(startTimestamp, finishTimestamp);

                    log.info("Saving {}", event.toString());
                    dbevent.InsertRecordsIntoTable(event);
                } else {
                    log.error("Log {} ids is missing start or finish event, skipping...", id);
                }
            }
        }
    }

}

package com.rafalcendrowski.AccountApplication.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class LoggerConfig {

    private final Logger secLogger = LogManager.getLogger("sec-logger");

    @Bean
    public Logger securityEventLogger() {
        return secLogger;
    }

    public static StringMapMessage getEventLogMap(String subject, String object, String action, String path) {
        String subjectStr = subject.isBlank() ? "Anonymous" : subject;
        return new StringMapMessage(Map.of("subject", subjectStr, "object", object,
                "action", action, "path", path));
    }
}

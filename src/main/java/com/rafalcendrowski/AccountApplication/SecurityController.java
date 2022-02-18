package com.rafalcendrowski.AccountApplication;


import com.rafalcendrowski.AccountApplication.logging.ConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
public class SecurityController {


    @GetMapping("/events")
    public List<Map<String, Object>> getEvents() throws SQLException {
        List<Map<String, Object>> eventList = new ArrayList<>();
        ResultSet resultSet = ConnectionFactory.getConnection()
                .createStatement()
                .executeQuery("SELECT * FROM event_log");
        while(resultSet.next()) {
            Map<String, Object> event = Map.of("id",resultSet.getInt("id"),
                    "date", resultSet.getTimestamp("date"), "subject", resultSet.getString("subject"),
                    "object", resultSet.getString("object"), "action", resultSet.getString("action"),
                    "path", resultSet.getString("path"));
            eventList.add(event);
        }
        return eventList;
    }
}

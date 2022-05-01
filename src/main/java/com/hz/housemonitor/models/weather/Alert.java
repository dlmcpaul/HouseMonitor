package com.hz.housemonitor.models.weather;

import lombok.Data;

import java.util.List;

@Data
public class Alert {
    private String sender_name;
    private String event;
    private long start;
    private long end;
    private String description;
    private List<String> tags;
}

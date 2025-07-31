package com.github.konstantyn111.crashapi.entity.announcement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    private String id;
    private String title;
    private LocalDateTime lastUpdate;
    private Long lastUpdateBy;
}

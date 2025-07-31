package com.github.konstantyn111.crashapi.entity.announcement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementText {
    private Long itemId;
    private String content;
}

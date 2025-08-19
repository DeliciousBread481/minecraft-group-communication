package com.github.konstantyn111.crashapi.entity.solution;

import com.github.konstantyn111.crashapi.entity.user.User;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private String id;
    private String name;
    private String icon;
    private String description;
    private String color;
    private User createdBy;
    private LocalDateTime createdAt;
    private String CreatedByUsername;
}

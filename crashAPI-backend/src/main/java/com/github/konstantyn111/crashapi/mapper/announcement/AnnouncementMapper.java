package com.github.konstantyn111.crashapi.mapper.announcement;

import com.github.konstantyn111.crashapi.entity.announcement.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper {
    Announcement findById(String id);
    void insert(Announcement announcement);
    void update(Announcement announcement);
}

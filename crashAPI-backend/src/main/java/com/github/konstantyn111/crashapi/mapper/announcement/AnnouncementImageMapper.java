package com.github.konstantyn111.crashapi.mapper.announcement;

import com.github.konstantyn111.crashapi.entity.announcement.AnnouncementImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementImageMapper {
    void insert(AnnouncementImage imageItem);
    void update(AnnouncementImage imageItem);
    AnnouncementImage findByItemId(Long itemId);
    void delete(Long itemId);
}

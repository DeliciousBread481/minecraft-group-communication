package com.github.konstantyn111.crashapi.mapper.announcement;

import com.github.konstantyn111.crashapi.entity.announcement.AnnouncementText;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementTextMapper {
    void insert(AnnouncementText textItem);
    void update(AnnouncementText textItem);
    AnnouncementText findByItemId(Long itemId);
    void delete(Long itemId);
}

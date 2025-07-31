package com.github.konstantyn111.crashapi.mapper.announcement;

import com.github.konstantyn111.crashapi.entity.announcement.AnnouncementItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnnouncementItemMapper {
    List<AnnouncementItem> findByCategoryIdOrdered(String categoryId);
    AnnouncementItem findById(Long id);
    void insert(AnnouncementItem item);
    void update(AnnouncementItem item);
    void delete(Long id);
    int countByCategoryId(String categoryId);
}

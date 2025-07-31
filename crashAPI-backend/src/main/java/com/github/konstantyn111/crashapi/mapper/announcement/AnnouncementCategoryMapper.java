package com.github.konstantyn111.crashapi.mapper.announcement;

import com.github.konstantyn111.crashapi.entity.announcement.AnnouncementCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnnouncementCategoryMapper {
    List<AnnouncementCategory> findAllOrderBySortOrder();
    AnnouncementCategory findById(String id);
    void insert(AnnouncementCategory category);
    void update(AnnouncementCategory category);
    void delete(String id);
    int countAll();
}

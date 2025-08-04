package com.github.konstantyn111.crashapi.mapper.user;

import com.github.konstantyn111.crashapi.entity.user.AdminApplication;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 管理员申请数据访问接口
 * <p>
 * 提供管理员权限申请记录的增删改查操作。
 * </p>
 */
public interface AdminApplicationMapper {

    /**
     * 插入管理员申请记录
     * <p>
     * 创建新的管理员权限申请记录，自动生成主键ID。
     * </p>
     *
     * @param application 要插入的管理员申请实体
     */
    @Insert("INSERT INTO admin_applications (user_id, status, reason, processor_id, created_at, processed_at) " +
            "VALUES (#{userId}, #{status}, #{reason}, #{processorId}, #{createdAt}, #{processedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AdminApplication application);

    /**
     * 检查用户是否有待处理的申请
     * <p>
     * 查询指定用户是否存在状态为PENDING的申请记录。
     * </p>
     *
     * @param userId 要检查的用户ID
     * @return 存在待处理申请返回true，否则返回false
     */
    @Select("SELECT COUNT(*) > 0 FROM admin_applications " +
            "WHERE user_id = #{userId} AND status = 'PENDING'")
    boolean hasPendingApplication(Long userId);

    /**
     * 根据ID查询管理员申请
     * <p>
     * 通过主键ID查询管理员申请记录。
     * </p>
     *
     * @param id 管理员申请ID
     * @return 包含管理员申请实体的Optional对象
     */
    @Select("SELECT * FROM admin_applications WHERE id = #{id}")
    Optional<AdminApplication> findById(Long id);

    /**
     * 分页查询待处理申请（含用户信息）
     * <p>
     * 查询状态为PENDING的申请记录，并关联申请人基本信息。
     * </p>
     *
     * @param offset 查询偏移量
     * @param limit 每页记录数
     * @return 管理员申请实体列表（含用户信息）
     */
    @Select("SELECT aa.*, u.username, u.email, u.nickname " +
            "FROM admin_applications aa " +
            "JOIN users u ON aa.user_id = u.id " +
            "WHERE aa.status = 'PENDING' " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<AdminApplication> findPendingApplicationsWithUserInfo(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计待处理申请数量
     * <p>
     * 查询状态为PENDING的申请记录总数。
     * </p>
     *
     * @return 待处理申请数量
     */
    @Select("SELECT COUNT(*) FROM admin_applications WHERE status = 'PENDING'")
    long countPendingApplications();

    /**
     * 更新管理员申请记录
     * <p>
     * 修改申请状态、处理信息和时间戳。
     * </p>
     *
     * @param application 包含更新字段的管理员申请实体
     */
    @Update("UPDATE admin_applications SET status=#{status}, reason=#{reason}, " +
            "processor_id=#{processorId}, processed_at=#{processedAt} " +
            "WHERE id=#{id}")
    void update(AdminApplication application);
}
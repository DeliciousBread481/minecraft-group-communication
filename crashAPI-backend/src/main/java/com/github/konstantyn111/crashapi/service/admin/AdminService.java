package com.github.konstantyn111.crashapi.service.admin;

import com.github.konstantyn111.crashapi.dto.user.UserInfo;
import com.github.konstantyn111.crashapi.dto.solution.SolutionCreateDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionDTO;
import com.github.konstantyn111.crashapi.dto.solution.SolutionUpdateDTO;
import com.github.konstantyn111.crashapi.entity.user.User;
import com.github.konstantyn111.crashapi.entity.solution.Category;
import com.github.konstantyn111.crashapi.entity.solution.Solution;
import com.github.konstantyn111.crashapi.exception.BusinessException;
import com.github.konstantyn111.crashapi.exception.ErrorCode;
import com.github.konstantyn111.crashapi.mapper.user.UserMapper;
import com.github.konstantyn111.crashapi.mapper.solution.CategoryMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionImageMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionMapper;
import com.github.konstantyn111.crashapi.mapper.solution.SolutionStepMapper;
import com.github.konstantyn111.crashapi.util.*;
import com.github.konstantyn111.crashapi.util.admin.*;
import com.github.konstantyn111.crashapi.util.user.UserConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserMapper userMapper;
    private final SolutionMapper solutionMapper;
    private final SolutionStepMapper solutionStepMapper;
    private final SolutionImageMapper solutionImageMapper;
    private final CategoryMapper categoryMapper;
    private final SolutionUpdateUtil solutionUpdateUtil;

    /**
     * <p> ---- 管理员接口 ---- <p/>
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息DTO
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public RestResponse<UserInfo> getUserInfoById(Long userId) {
        try {
            SecurityUtils.validateAdminPermissions(userMapper);
            User user = ValidationUtils.validateUserExists(userMapper, userId, "");
            return RestResponse.success(UserConvertUtil.convertToUserInfo(user), "获取用户信息成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取用户信息失败: " + ex.getMessage());
        }
    }

    /**
     * <p> ---- 管理员接口 ---- <p/>
     * 撤销用户令牌
     * @param username 用户名
     * @return API响应结果
     */
    @Transactional
    public RestResponse<Void> revokeToken(String username) {
        Optional<User> userOptional = userMapper.findByUsername(username);
        if (userOptional.isPresent()) {
            userMapper.updateRefreshToken(
                    userOptional.get().getId(),
                    null,
                    null
            );
            return RestResponse.success(null, "令牌已撤销");
        }
        throw new BusinessException(ErrorCode.USER_NOT_FOUND,
                HttpStatus.NOT_FOUND,
                "用户不存在");
    }

    /**
     * <p> ---- 管理员接口 ---- <p/>
     * 创建解决方案
     * @param createDTO 创建数据
     * @return 创建的解决方案DTO
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public RestResponse<SolutionDTO> createSolution(SolutionCreateDTO createDTO) {
        try {
            User currentAdmin = SecurityUtils.validateAdminPermissions(userMapper);

            Category category = ValidationUtils.validateCategoryExists(
                    categoryMapper, createDTO.getCategoryId());

            Solution solution = new Solution();
            solution.setId(SolutionUtils.generateSolutionId());
            solution.setCategoryId(createDTO.getCategoryId());
            solution.setTitle(createDTO.getTitle());
            solution.setDifficulty(createDTO.getDifficulty());
            solution.setVersion(createDTO.getVersion());
            solution.setDescription(createDTO.getDescription());
            solution.setNotes(createDTO.getNotes());
            solution.setStatus(SolutionStatus.DRAFT);
            solution.setCreatedBy(currentAdmin.getId());
            solution.setCreatedAt(LocalDateTime.now());
            solution.setUpdatedAt(LocalDateTime.now());

            solutionMapper.insert(solution);

            solutionUpdateUtil.createSolutionStepsAndImages(
                    solution,
                    createDTO,
                    solutionStepMapper,
                    solutionImageMapper
            );

            SolutionDTO dto = SolutionConvertUtil.convertToSolutionDTO(solution);
            return RestResponse.success(dto, "解决方案创建成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "创建解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * <p> ---- 管理员接口 ---- <p/>
     * 更新解决方案
     * @param solutionId 解决方案ID
     * @param updateDTO 更新数据
     * @return 更新后的解决方案DTO
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public RestResponse<SolutionDTO> updateSolution(String solutionId, SolutionUpdateDTO updateDTO) {
        try {
            User currentAdmin = SecurityUtils.validateAdminPermissions(userMapper);
            Solution solution = ValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            ValidationUtils.validateSolutionOwnership(solution, currentAdmin, "修改");

            if (!SolutionStatus.DRAFT.equals(solution.getStatus()) &&
                    !SolutionStatus.PUBLISHED.equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "只能修改草稿或已发布状态的解决方案");
            }

            solutionUpdateUtil.updateSolutionCore(
                    solution,
                    updateDTO,
                    solutionMapper,
                    solutionStepMapper,
                    solutionImageMapper
            );

            SolutionDTO dto = SolutionConvertUtil.convertToSolutionDTO(solution);
            return RestResponse.success(dto, "解决方案更新成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "更新解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * <p> ---- 管理员接口 ---- <p/>
     * 删除解决方案
     * @param solutionId 解决方案ID
     * @return API响应结果
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public RestResponse<Void> deleteSolution(String solutionId) {
        try {
            User currentAdmin = SecurityUtils.validateAdminPermissions(userMapper);
            Solution solution = ValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            ValidationUtils.validateSolutionOwnership(solution, currentAdmin, "删除");

            if (!SolutionStatus.DRAFT.equals(solution.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_SOLUTION_STATUS,
                        HttpStatus.BAD_REQUEST,
                        "只能删除草稿状态的解决方案");
            }

            solutionStepMapper.deleteBySolutionId(solutionId);
            solutionImageMapper.deleteBySolutionId(solutionId);
            solutionMapper.delete(solutionId);

            return RestResponse.success("解决方案删除成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "删除解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * <p> ---- 管理员接口 ---- <p/>
     * 撤回已发布解决方案
     * @param solutionId 解决方案ID
     * @return API响应结果
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public RestResponse<Void> withdrawSolution(String solutionId) {
        try {
            User currentAdmin = SecurityUtils.validateAdminPermissions(userMapper);
            Solution solution = ValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            ValidationUtils.validateSolutionOwnership(solution, currentAdmin, "撤回");

            SolutionStateUtils.transitionState(solution, SolutionStateUtils.SolutionStateAction.WITHDRAW, null);
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已撤回为草稿状态");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "撤回解决方案失败: " + ex.getMessage());
        }
    }

    /**
     * <p> ---- 管理员接口 ---- <p/>
     * 提交解决方案审核
     * @param solutionId 解决方案ID
     * @return API响应结果
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public RestResponse<Void> submitSolutionForReview(String solutionId) {
        try {
            User currentAdmin = SecurityUtils.validateAdminPermissions(userMapper);
            Solution solution = ValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            ValidationUtils.validateSolutionOwnership(solution, currentAdmin, "提交");

            SolutionStateUtils.transitionState(solution, SolutionStateUtils.SolutionStateAction.SUBMIT_FOR_REVIEW, null);
            solutionMapper.update(solution);

            return RestResponse.success("解决方案已提交审核");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "提交解决方案审核失败: " + ex.getMessage());
        }
    }

    /**
     * <p> ---- 管理员接口 ---- <p/>
     * 分页获取管理员创建的解决方案
     * @param pageable 分页参数
     * @param status 解决方案状态/可以为空
     * @return 解决方案分页数据
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public RestResponse<Page<SolutionDTO>> getMySolutions(Pageable pageable, String status) {
        try {
            User currentAdmin = SecurityUtils.validateAdminPermissions(userMapper);

            List<Solution> solutions = solutionMapper.findByCreator(
                    currentAdmin.getId(),
                    status,
                    pageable.getPageSize(),
                    (int) pageable.getOffset()
            );

            long total = solutionMapper.countByCreator(currentAdmin.getId(), status);

            List<SolutionDTO> dtos = SolutionConvertUtil.convertToSolutionDTOList(solutions);
            Page<SolutionDTO> page = new PageImpl<>(dtos, pageable, total);
            return RestResponse.success(page, "获取解决方案列表成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案列表失败: " + ex.getMessage());
        }
    }

    /**
     * <p> ---- 管理员接口 ---- <p/>
     * 根据ID获取解决方案详情
     * @param solutionId 解决方案ID
     * @return 解决方案DTO
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public RestResponse<SolutionDTO> getSolutionById(String solutionId) {
        try {
            SecurityUtils.validateAdminPermissions(userMapper);
            Solution solution = ValidationUtils.validateSolutionExists(solutionMapper, solutionId);

            SolutionDTO dto = SolutionConvertUtil.convertToSolutionDTO(solution);
            return RestResponse.success(dto, "获取解决方案成功");
        } catch (BusinessException ex) {
            return RestResponse.fail(ex);
        } catch (Exception ex) {
            return RestResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "获取解决方案失败: " + ex.getMessage());
        }
    }
}
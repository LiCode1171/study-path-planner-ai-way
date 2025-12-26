package io.renren.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.student.dao.StudyPlanDao;
import io.renren.modules.student.dto.DashboardDTO;
import io.renren.modules.student.dto.StudyPlanDTO;
import io.renren.modules.student.dto.StudyPlanItemDTO;
import io.renren.modules.student.entity.*;
import io.renren.modules.student.service.*;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习计划表 Service 实现类
 * <p>
 * 核心算法设计：
 * 1. 复习优先策略：每日优先分配复习任务，遵循艾宾浩斯遗忘曲线
 * 2. 动态复习机制：复习时长=原知识点时长/2，不超过每日时长的50%，每日复习总量不超过60%
 * 3. 智能任务拆分：每日学习时长≥5小时启用60%阈值机制，新知识点单次分配不超过阈值
 * 4. 科目交替轮转：根据科目掌握程度计算权重，避免长时间学习同一科目造成疲劳
 * 5. 跨天任务管理：自动追踪未完成任务，作为高优先级在下一次轮转到该科目时继续分配
 * </p>
 *
 * @author Haili 695536881@qq.com and deepseek and kimi
 * @since 1.0.0 2025-11-23
 */
@Service
public class StudyPlanServiceImpl extends CrudServiceImpl<StudyPlanDao, StudyPlanEntity, StudyPlanDTO> implements StudyPlanService {

    private static final Logger logger = LoggerFactory.getLogger(StudyPlanServiceImpl.class);

    /** 复习间隔天数（间隔重复算法：3天, 7天, 14天, 30天） */
    private static final int[] REVIEW_INTERVALS = {3, 7, 14, 30};
    /** 每日复习时间最大占比阈值（60%），超过则推迟到明天 */
    private static final double DAILY_REVIEW_RATIO = 0.6;
    /** 单个复习任务最大时长占比（50%），防止单个大任务占据过多时间 */
    private static final double SINGLE_REVIEW_MAX_RATIO = 0.5;
    /** 新知识点单次分配时长阈值（60%），用于拆分大任务 */
    private static final double NEW_TASK_THRESHOLD_RATIO = 0.6;
    /** 启用阈值机制的每日工时基准（5小时），少于5小时则不强制拆分 */
    private static final int THRESHOLD_ENABLE_HOURS = 5;

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserSubjectLevelService userSubjectLevelService;
    @Autowired
    private StudyPlanItemService studyPlanItemService;
    @Autowired
    private KnowledgePointService knowledgePointService;

    /**
     * 复习任务内部类
     * 用于在生成计划时临时存储复习任务信息
     * 核心方法 calculateReviewHours 根据每日可用时间动态计算复习时长
     */
    @Setter @Getter
    private static class ReviewTask {
        private Long knowledgePointId;    // 知识点ID
        private String subjectName;       // 科目名称
        private int originalHours;        // 原始学习时长（用于计算复习时长）
        private int finishDay;           // 学习完成的天数索引
        private int reviewNumber;         // 第几次复习（1-4）

        /**
         * 计算复习时长
         * 规则：原知识点时长的一半，向上取整，但不超过每日学习时间的50%，至少1小时
         *
         * @param dailyHours 每日可用学习时长
         * @return 计算后的复习时长
         */
        public int calculateReviewHours(int dailyHours) {
            int halfTime = (int) Math.ceil(originalHours / 2.0);
            int maxReview = (int) (dailyHours * SINGLE_REVIEW_MAX_RATIO);
            return Math.max(1, Math.min(halfTime, maxReview));
        }
    }

    /**
     * 跨天任务状态管理类
     * 用于跟踪跨天学习任务的进度和状态
     * 核心方法 isCompleted 判断任务是否全部完成
     */
    @Getter
    private static class CrossDayTask {
        private Long knowledgePointId;    // 知识点ID
        private String subjectName;       // 科目名称
        private int originalHours;        // 任务总时长
        private int remainingHours;       // 剩余未分配时长
        private int allocatedHours;       // 已分配时长
        private int currentPart;          // 当前是第几部分（从0开始计数，显示时可能+1）
        private int finishDay = -1;           // 完成学习的那一天（用于计算复习日期，初始未完成）
        private int estimatedTotalParts = 1; // 预估总部分数（仅作参考，实际根据动态分配决定）

        public CrossDayTask(Long knowledgePointId, String subjectName, int originalHours) {
            this.knowledgePointId = knowledgePointId;
            this.subjectName = subjectName;
            this.originalHours = originalHours;
            this.remainingHours = originalHours;
            this.allocatedHours = 0;
            this.currentPart = 0;
            logger.debug("创建跨天任务: {} - {}小时，预计{}天完成", subjectName, originalHours, this.estimatedTotalParts);
        }

        /**
         * 记录一次时间分配
         * 更新剩余时长、已分配时长和部分计数
         * 如果还有剩余时间，增加预估总部分数
         */
        public void addAllocation(int hours) {
            this.remainingHours -= hours;
            this.allocatedHours += hours;
            this.currentPart++;
            if (this.remainingHours > 0) this.estimatedTotalParts++;
        }

        /**
         * 在任务首次完成时设置完成日
         * @param currentDay 当前分配的天数
         * @return 是否完成
         */
        public boolean isCompleted(int currentDay) {
            if (this.remainingHours <= 0 && this.finishDay == -1) {
                this.finishDay = currentDay; // ✅ 设置完成日
                return true;
            }
            return this.remainingHours <= 0;
        }

        public boolean isCompleted() { return this.remainingHours <= 0; }
    }

    /**
     * 分配统计结果记录类
     * 用于 avoid lambda 编译错误和流式计算性能浪费
     * 在 allocateReviews 方法中返回统计结果，避免事后统计
     */
    private record AllocationStats(int addedCount, int addedDuration, int updatedUsedHours) {}

    /**
     * 构建查询条件
     * @param params 查询参数
     * @return QueryWrapper
     */
    @Override
    public QueryWrapper<StudyPlanEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");
        QueryWrapper<StudyPlanEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(id), "id", id);
        return wrapper;
    }

    /**
     * 生成学习计划主入口
     * <p>
     * 处理流程：
     * 1. 获取用户画像和考试类别
     * 2. 检查是否已存在学习计划
     * 3. 若存在则删除旧计划项并更新，否则创建新计划
     * 4. 调用 generatePlanItems 生成详细计划项
     * </p>
     * @param userId 用户ID
     * @return 生成的学习计划实体
     */
    @Override
    public StudyPlanEntity generatePlan(Long userId) {
        UserProfileEntity profile = userProfileService.getByUserId(userId);
        if (profile == null) throw new RuntimeException("用户画像不存在");
        List<KnowledgePointEntity> relevantPoints = getKnowledgePointsByExamCategory(profile.getExamCategory());
        Map<String, List<KnowledgePointEntity>> pointsBySubject = groupPointsBySubject(relevantPoints);
        StudyPlanEntity existingPlan = getByUserId(userId);
        if (existingPlan != null) {
            deleteOldPlanItems(existingPlan.getId());
            StudyPlanDTO planDTO = updateStudyPlanDTO(existingPlan, profile, pointsBySubject);
            this.update(planDTO);
            logger.info("更新学习计划，用户ID: {}", userId);
            generatePlanItems(planDTO, profile, userSubjectLevelService.getByUserId(userId));
            return baseDao.selectById(planDTO.getId());
        } else {
            StudyPlanDTO planDTO = createStudyPlanDTO(profile, pointsBySubject);
            this.save(planDTO);
            logger.info("创建学习计划，用户ID: {}", userId);
            generatePlanItems(planDTO, profile, userSubjectLevelService.getByUserId(userId));
            return baseDao.selectById(planDTO.getId());
        }
    }

    /**
     * 根据用户ID查询学习计划
     * @param userId 用户ID
     * @return 学习计划实体，或 null
     */
    public StudyPlanEntity getByUserId(Long userId) {
        QueryWrapper<StudyPlanEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return baseDao.selectOne(wrapper);
    }

    /**
     * 创建新的学习计划DTO
     * @param profile 用户画像
     * @param pointsBySubject 按科目分组的知识点
     * @return 新创建的学习计划DTO
     */
    private StudyPlanDTO createStudyPlanDTO(UserProfileEntity profile, Map<String, List<KnowledgePointEntity>> pointsBySubject) {
        StudyPlanDTO planDTO = new StudyPlanDTO();
        planDTO.setUserId(profile.getUserId());
        planDTO.setPlanName(profile.getTargetSchool() + "专升本备考计划");
        planDTO.setDailyHours(profile.getDailyStudyTime());
        planDTO.setStartDate(new Date());
        setupPlanDates(planDTO, profile.getPreparationMonths());
        planDTO.setStatus(1);
        planDTO.setTotalSubjects(pointsBySubject.size());
        planDTO.setCompletedSubjects(0);
        planDTO.setTotalReviewDays(7);
        planDTO.setOverallProgress(BigDecimal.valueOf(0.0));
        return planDTO;
    }

    /**
     * 更新现有学习计划DTO
     * @param existingPlan 现有计划实体
     * @param profile 用户画像
     * @param pointsBySubject 按科目分组的知识点
     * @return 更新后的学习计划DTO
     */
    private StudyPlanDTO updateStudyPlanDTO(StudyPlanEntity existingPlan, UserProfileEntity profile, Map<String, List<KnowledgePointEntity>> pointsBySubject) {
        StudyPlanDTO planDTO = new StudyPlanDTO();
        planDTO.setId(existingPlan.getId());
        planDTO.setUserId(profile.getUserId());
        planDTO.setPlanName(profile.getTargetSchool() + "专升本备考计划(更新)");
        planDTO.setDailyHours(profile.getDailyStudyTime());
        planDTO.setStartDate(new Date());
        setupPlanDates(planDTO, profile.getPreparationMonths());
        planDTO.setStatus(1);
        planDTO.setTotalSubjects(pointsBySubject.size());
        planDTO.setCompletedSubjects(0);
        planDTO.setTotalReviewDays(7);
        planDTO.setOverallProgress(BigDecimal.valueOf(0.0));
        return planDTO;
    }

    /**
     * 设置计划的日期（开始、结束、总天数）
     * @param planDTO 计划DTO
     * @param months 备考月份数
     */
    private void setupPlanDates(StudyPlanDTO planDTO, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, months);
        planDTO.setEndDate(calendar.getTime());
        long diffTime = planDTO.getEndDate().getTime() - planDTO.getStartDate().getTime();
        long totalDays = diffTime / (1000 * 60 * 60 * 24) + 1;
        planDTO.setTotalDays((int) totalDays);
    }

    /**
     * 删除旧计划项
     * @param planId 计划ID
     */
    private void deleteOldPlanItems(Long planId) {
        if (planId != null) {
            studyPlanItemService.deleteByPlanId(planId);
        }
    }

    /**
     * 生成学习计划项主方法
     * <p>
     * 核心算法流程：
     * 1. 获取用户相关知识点
     * 2. 构建科目水平映射（用于确定科目学习优先级）
     * 3. 按科目分组知识点（为科目交替学习做准备）
     * 4. 调用 generatePlanWithSubjectRotation 生成带科目交替和动态复习的学习计划
     * 5. 批量保存学习计划项
     * </p>
     * @param plan 计划基础信息
     * @param profile 用户画像
     * @param subjectLevels 用户科目水平列表
     */
    private void generatePlanItems(StudyPlanDTO plan, UserProfileEntity profile, List<UserSubjectLevelEntity> subjectLevels) {
        logger.info("开始生成个性化学习计划项（含动态复习），用户ID: {}", profile.getUserId());
        List<KnowledgePointEntity> relevantPoints = getKnowledgePointsByExamCategory(profile.getExamCategory());
        logger.info("获取用户招考类别的相关知识点，数量: {}", relevantPoints.size());
        Map<String, Integer> subjectLevelMap = buildSubjectLevelMap(subjectLevels);
        Map<String, List<KnowledgePointEntity>> pointsBySubject = groupPointsBySubject(relevantPoints);
        List<StudyPlanItemDTO> planItems = generatePlanWithSubjectRotation(pointsBySubject, subjectLevelMap, plan, profile);
        savePlanItems(planItems);
        logger.info("学习计划项生成完成，总任务数: {}", planItems.size());
    }

    /**
     * 核心算法：生成带科目交替和动态复习的学习计划
     * <p>
     * 算法特点：
     * - 科目交替学习，避免疲劳
     * - 动态生成复习任务（间隔重复算法）
     * - 智能任务拆分（60%阈值，仅当每日学习时长≥5小时）
     * - 复习任务优先分配
     * </p>
     *
     * @param pointsBySubject 按科目分组的知识点
     * @param subjectLevels 用户科目水平（用于计算权重）
     * @param plan 计划基础信息
     * @param profile 用户画像
     * @return 生成的计划项列表
     */
    private List<StudyPlanItemDTO> generatePlanWithSubjectRotation(
            Map<String, List<KnowledgePointEntity>> pointsBySubject,
            Map<String, Integer> subjectLevels,
            StudyPlanDTO plan,
            UserProfileEntity profile) {

        List<StudyPlanItemDTO> resultItems = new ArrayList<>();
        int totalDays = plan.getTotalDays();
        int dailyHours = plan.getDailyHours();
        logger.info("开始生成学习计划（含复习），用户ID: {}, 每日学习时间: {}小时, 总天数: {}",
                profile.getUserId(), dailyHours, totalDays);

        // 1. 初始化数据结构

        // 1.1 剩余知识点队列：按难度排序，由易到难
        Map<String, List<KnowledgePointEntity>> remainingPoints = new HashMap<>();
        pointsBySubject.forEach((k, v) -> {
            v.sort(Comparator.comparingInt(KnowledgePointEntity::getDifficulty));
            remainingPoints.put(k, new ArrayList<>(v));
            logger.info("科目 {} 有 {} 个知识点，总时长: {}小时",
                    k, v.size(),
                    v.stream().mapToInt(p -> p.getEstimatedHours() != null ? p.getEstimatedHours() : 2).sum());
        });

        // 1.2 跨天任务缓存 (Subject -> Task)：每个科目同一时间只能有一个正在进行中的跨天任务
        Map<String, CrossDayTask> activeCrossDayTasks = new HashMap<>();

        // 1.3 复习队列 (DayIndex -> List<Task>)：存储未来需要复习的任务
        Map<Integer, List<ReviewTask>> reviewQueue = new TreeMap<>();

        // 1.4 科目轮转序列：根据权重生成的科目列表，用于实现交替学习
        List<String> subjectRotation = createSubjectRotation(calculateSubjectWeights(subjectLevels));
        logger.info("科目轮转序列: {}", subjectRotation);

        int rotationIndex = 0;
        int totalPointsAllocated = 0;
        int totalHoursAllocated = 0;
        int totalReviewHoursAllocated = 0;

        // 2. 按天遍历生成 (Main Loop)
        for (int currentDay = 1; currentDay <= totalDays; currentDay++) {
            // 终止条件：没有剩余知识点、没有进行中的任务、也没有未来的复习任务
            if (!hasRemainingPoints(remainingPoints) && activeCrossDayTasks.isEmpty() && !hasFutureReviews(reviewQueue, currentDay)) {
                logger.info("✅ 所有知识点及复习任务已分配完毕，提前结束，当前天数: {}", currentDay);
                break;
            }

            // 当天初始状态
            int daySortOrder = 1;
            int dayUsedHours = 0;
            int dayReviewHours = 0;
            int dayReviewCount = 0;
            int dayPointsCount = 0;
            boolean useThreshold = dailyHours >= THRESHOLD_ENABLE_HOURS;
            logger.debug("开始分配第{}天，可用时间: {}小时，使用阈值: {}", currentDay, dailyHours, useThreshold);

            // 3. 步骤一：分配复习任务（优先级最高）
            // ✅ 优化：直接返回统计结果，无需事后流式计算（解决lambda编译错误和性能问题）
            AllocationStats reviewStats = allocateReviews(currentDay, dailyHours, reviewQueue, resultItems,
                    plan.getId(), dayUsedHours, dayReviewHours, daySortOrder);
            daySortOrder += reviewStats.addedCount();
            dayReviewCount = reviewStats.addedCount();
            dayReviewHours = reviewStats.addedDuration();
            dayUsedHours = reviewStats.updatedUsedHours();
            totalReviewHoursAllocated += reviewStats.addedDuration();

            // 4. 步骤二：分配新知识点任务（科目交替轮转）
            while (dayUsedHours < dailyHours) {
                boolean assignedInThisLoop = false;

                // 遍历一轮科目轮转序列
                for (int i = 0; i < subjectRotation.size(); i++) {
                    if (dayUsedHours >= dailyHours) break;

                    String subject = subjectRotation.get((rotationIndex + i) % subjectRotation.size());
                    rotationIndex = (rotationIndex + 1) % subjectRotation.size(); // 指针永远后移，保证交替

                    // 情况4.1：该科目存在未完成的跨天任务（优先继续分配）
                    if (activeCrossDayTasks.containsKey(subject)) {
                        CrossDayTask task = activeCrossDayTasks.get(subject);
                        logger.debug("🔄 检测到跨天任务后续部分: {} - {} (第{}部分/总{}部分)，继续分配",
                                subject, task.getKnowledgePointId(),
                                task.getCurrentPart() + 1, task.getEstimatedTotalParts());

                        int allocated = allocateTaskTime(task, currentDay, dayUsedHours, dailyHours,
                                plan.getId(), daySortOrder++, resultItems);
                        if (allocated == 0) continue;

                        dayUsedHours += allocated;
                        totalHoursAllocated += allocated;
                        assignedInThisLoop = true;
                        dayPointsCount++;
                        totalPointsAllocated++;

                        // 仅当任务最终完成时才生成复习任务
                        if (task.isCompleted(currentDay)) {
                            logger.debug("✅ 跨天任务完成: {} - 知识点ID:{}，从Map中移除", subject, task.getKnowledgePointId());
                            scheduleReviews(task, reviewQueue, totalDays,currentDay);
                            activeCrossDayTasks.remove(subject);
                        }
                        break;
                    }
                    // 情况4.2：该科目有待学新知识点
                    else if (remainingPoints.containsKey(subject) && !remainingPoints.get(subject).isEmpty()) {
                        KnowledgePointEntity kp = remainingPoints.get(subject).get(0);
                        int originalHours = kp.getEstimatedHours() != null ? kp.getEstimatedHours() : 2;
                        logger.debug("尝试分配知识点: {} - {} ({}小时)，当天已用: {}小时",
                                subject, kp.getPointName(), originalHours, dayUsedHours);

                        // 创建新跨天任务
                        CrossDayTask task = new CrossDayTask(kp.getId(), subject, originalHours);
                        int allocated = allocateTaskTime(task, currentDay, dayUsedHours, dailyHours,
                                plan.getId(), daySortOrder++, resultItems);
                        if (allocated == 0) continue;

                        // 从待分配列表移除
                        remainingPoints.get(subject).remove(0);
                        dayUsedHours += allocated;
                        totalHoursAllocated += allocated;
                        assignedInThisLoop = true;
                        dayPointsCount++;
                        totalPointsAllocated++;

                        // 判断任务是否一次性完成
                        if (task.isCompleted(currentDay)) {
                            logger.debug("✅ 正常分配/一次性完成: {} - {} ({}小时)", subject, kp.getPointName(), allocated);
                            scheduleReviews(task, reviewQueue, totalDays,currentDay);
                        } else {
                            // 未完成，加入跨天任务缓存
                            logger.info("📊 任务需要跨天/拆分: {} - {} (原{}小时)，今天分配{}小时，剩余{}小时",
                                    subject, kp.getPointName(), originalHours, allocated, task.getRemainingHours());
                            activeCrossDayTasks.put(subject, task);
                        }
                        break;
                    }
                }

                // 防御性退出：如果遍历了一整圈科目都没分配到任务（例如所有科目都学完了），强制退出防止死循环
                if (!assignedInThisLoop) break;
            }

            // 日志：记录当天的分配情况
            if (dayPointsCount > 0 || dayReviewCount > 0) {
                logger.info("第{}天分配完成: {}个新知识点，{}个复习任务，{}小时（学习:{}，复习:{}）",
                        currentDay, dayPointsCount, dayReviewCount, dayUsedHours,
                        (dayUsedHours - dayReviewHours), dayReviewHours);
            } else {
                logger.error("❌ 第{}天无法分配任何任务，检查剩余知识点是否为空", currentDay);
            }
        }

        // 5. 最终统计日志
        logger.info("学习计划生成完成: 共{}天，分配{}个新知识点（{}小时），复习任务（{}小时），剩余知识点数: {}",
                resultItems.stream().mapToInt(StudyPlanItemDTO::getDayIndex).max().orElse(0),
                totalPointsAllocated, totalHoursAllocated, totalReviewHoursAllocated,
                remainingPoints.values().stream().mapToInt(List::size).sum());

        return resultItems;
    }

    /**
     * 分配当天的复习任务
     * <p>
     * 逻辑：
     * 1. 检查今日复习总占比是否超过60%
     * 2. 检查今日总时长是否已满
     * 3. 若满足条件则分配，否则推迟到下一天
     * </p>
     *
     * @return AllocationStats 统计结果（新增数量、总时长、更新后的已用时间）
     */
    private AllocationStats allocateReviews(int currentDay, int dailyHours, Map<Integer, List<ReviewTask>> reviewQueue,
                                            List<StudyPlanItemDTO> resultItems, Long planId, int dayUsedHours,
                                            int dayReviewHours, int startSortOrder) {
        List<ReviewTask> todayReviews = reviewQueue.get(currentDay);
        if (todayReviews == null || todayReviews.isEmpty()) {
            return new AllocationStats(0, 0, dayUsedHours);
        }

        Iterator<ReviewTask> iterator = todayReviews.iterator();
        int count = 0;
        int totalDuration = 0;
        int currentUsedHours = dayUsedHours;
        int currentReviewHours = dayReviewHours;
        int sortOrder = startSortOrder;

        while (iterator.hasNext()) {
            ReviewTask review = iterator.next();
            int duration = review.calculateReviewHours(dailyHours);

            // 复习总时长 > 60% ?
            if (currentReviewHours + duration > dailyHours * DAILY_REVIEW_RATIO) {
                logger.debug("⏭️ 复习任务超过60%限制，推迟到明天第{}天: {} - 知识点{} ({}小时)",
                        currentDay + 1, review.getSubjectName(), review.getKnowledgePointId(), duration);
                deferReviewToNextDay(review, currentDay, reviewQueue);
                iterator.remove();
                continue;
            }

            // 总时长 > 100% ?
            if (currentUsedHours + duration > dailyHours) {
                logger.debug("⏭️ 每日时间已满，复习任务推迟到明天第{}天: {} - 知识点{} ({}小时)",
                        currentDay + 1, review.getSubjectName(), review.getKnowledgePointId(), duration);
                deferReviewToNextDay(review, currentDay, reviewQueue);
                iterator.remove();
                continue;
            }

            // 创建复习计划项
            StudyPlanItemDTO item = new StudyPlanItemDTO();
            item.setPlanId(planId);
            item.setDayIndex(currentDay);
            item.setSort(sortOrder++);
            item.setKnowledgePointId(review.getKnowledgePointId());
            item.setSubjectName(review.getSubjectName());
            item.setIsReview(1);
            item.setSplitPart(1);
            item.setTotalParts(1);
            item.setSplitHours(duration);
            item.setCompleted(0);
            resultItems.add(item);

            currentUsedHours += duration;
            currentReviewHours += duration;
            totalDuration += duration;
            count++;
            iterator.remove();

            logger.debug("✅ 分配复习任务: {} - 知识点{} ({}小时)，第{}次复习",
                    review.getSubjectName(), review.getKnowledgePointId(),
                    duration, review.getReviewNumber());
        }

        return new AllocationStats(count, totalDuration, currentUsedHours);
    }

    /**
     * 将复习任务推迟到第二天
     */
    private void deferReviewToNextDay(ReviewTask task, int currentDay, Map<Integer, List<ReviewTask>> reviewQueue) {
        reviewQueue.computeIfAbsent(currentDay + 1, k -> new ArrayList<>()).add(task);
    }

    /**
     * 核心分配逻辑：分配单个学习任务的时间（统一入口）
     * <p>
     * 实现了阈值拆分、时间填充等核心逻辑
     * </p>
     *
     * @return 本次实际分配的小时数
     */
    private int allocateTaskTime(CrossDayTask task, int currentDay, int dayUsedHours, int dailyHours,
                                 Long planId, int sortOrder, List<StudyPlanItemDTO> resultItems) {
        int remainingTime = dailyHours - dayUsedHours;
        if (remainingTime <= 0) return 0;

        int maxAllow;
        // 当每日时长 > 5小时时，启用60%阈值限制
        if (dailyHours >= THRESHOLD_ENABLE_HOURS) {
            int threshold = (int) Math.floor(dailyHours * NEW_TASK_THRESHOLD_RATIO);
            maxAllow = Math.min(threshold, remainingTime); // 既不能超过今日剩余时间，也不能超过60%的大任务限制
        } else {
            // 不启用阈值：尽量填满今日剩余时间
            maxAllow = remainingTime;
        }

        // 实际分配 = min(任务剩余时长, 允许分配时长)
        int actual = Math.min(task.getRemainingHours(), maxAllow);
        if (actual <= 0) return 0;

        // 更新任务状态
        task.addAllocation(actual);

        // 创建计划项
        StudyPlanItemDTO item = new StudyPlanItemDTO();
        item.setPlanId(planId);
        item.setDayIndex(currentDay);
        item.setSort(sortOrder);
        item.setKnowledgePointId(task.getKnowledgePointId());
        item.setSubjectName(task.getSubjectName());
        item.setIsReview(0);
        item.setSplitHours(actual);
        item.setSplitPart(task.getCurrentPart());
        item.setTotalParts(task.isCompleted() ? task.getCurrentPart() : task.getEstimatedTotalParts());
        item.setCompleted(0);
        resultItems.add(item);

        logger.debug("分配详情: {} - 部分{}/{} - 本次{}小时 - 剩余{}小时",
                task.getSubjectName(), task.getCurrentPart(),
                item.getTotalParts(), actual, task.getRemainingHours());
        return actual;
    }

    /**
     * 安排未来的复习计划
     * <p>
     * 根据遗忘曲线，在知识点学习完成后的固定间隔安排复习任务
     * 仅在 CrossDayTask.isCompleted() 为 true 时调用
     * </p>
     */
    private void scheduleReviews(CrossDayTask task, Map<Integer, List<ReviewTask>> reviewQueue, int totalDays, int currentDay) {
        if (!task.isCompleted()) return;
        for (int interval : REVIEW_INTERVALS) {
            // 注意：调用前必须确保 task.isCompleted() == true
            int reviewDay = task.getFinishDay() + interval;

            // 计算复习次数
            int ReviewNumber = interval == 3 ? 1 : (interval == 7 ? 2 : (interval == 14 ? 3 : 4));
            // 如果复习日期超过计划总天数，则跳过
            if (reviewDay > totalDays) {
                logger.debug("复习日期{}超过计划总天数{}，跳过第{}次复习", reviewDay, totalDays, ReviewNumber);
                continue;
            }

            ReviewTask review = new ReviewTask();
            review.setKnowledgePointId(task.getKnowledgePointId());
            review.setSubjectName(task.getSubjectName());
            review.setOriginalHours(task.getOriginalHours());
            review.setFinishDay(task.getFinishDay());
            review.setReviewNumber(ReviewNumber);

            // 避免同一天重复复习同一个知识点
            List<ReviewTask> dayList = reviewQueue.computeIfAbsent(reviewDay, k -> new ArrayList<>());
            if (dayList.stream().anyMatch(r -> r.getKnowledgePointId().equals(task.getKnowledgePointId()))) {
                logger.debug("复习任务已存在: {} - 知识点{} - 第{}天，跳过", review.getSubjectName(), review.getKnowledgePointId(), reviewDay);
                continue;
            }

            dayList.add(review);
            logger.debug("加入复习队列: {} - 知识点{} - 第{}天（学习后{}天）- 第{}次复习",
                    review.getSubjectName(), review.getKnowledgePointId(),
                    reviewDay, interval, review.getReviewNumber());
        }
    }

    /**
     * 检查是否还有未来的复习任务
     * @param reviewQueue 复习队列
     * @param currentDay 当前天数
     * @return 是否有未来复习任务
     */
    private boolean hasFutureReviews(Map<Integer, List<ReviewTask>> reviewQueue, int currentDay) {
        return reviewQueue.keySet().stream().anyMatch(day -> day >= currentDay);
    }

    /**
     * 获取用户招考类别的相关知识点
     * @param examCategory 招考类别
     * @return 知识点列表
     */
    private List<KnowledgePointEntity> getKnowledgePointsByExamCategory(String examCategory) {
        return knowledgePointService.listByExamCategory(examCategory);
    }

    /**
     * 构建科目水平映射（用于确定科目学习优先级）
     * @param subjectLevels 用户科目水平列表
     * @return 科目 -> 水平等级的Map
     */
    private Map<String, Integer> buildSubjectLevelMap(List<UserSubjectLevelEntity> subjectLevels) {
        Map<String, Integer> levelMap = new HashMap<>();
        if (subjectLevels != null) {
            for (UserSubjectLevelEntity subjectLevel : subjectLevels) {
                levelMap.put(subjectLevel.getSubjectName(), subjectLevel.getLevel());
            }
        }
        return levelMap;
    }

    /**
     * 按科目分组知识点
     * @param points 知识点列表
     * @return 科目 -> 知识点列表的Map
     */
    private Map<String, List<KnowledgePointEntity>> groupPointsBySubject(List<KnowledgePointEntity> points) {
        return points.stream().collect(Collectors.groupingBy(KnowledgePointEntity::getSubjectName));
    }

    /**
     * 计算科目权重（弱科权重更高）
     * <p>
     * 算法：水平越低（数字小），权重越高（6 - level）
     * 然后归一化处理，使权重之和为1
     * </p>
     * @param subjectLevels 科目水平Map
     * @return 科目 -> 权重的Map
     */
    private Map<String, Double> calculateSubjectWeights(Map<String, Integer> subjectLevels) {
        Map<String, Double> weights = new HashMap<>();
        double totalWeight = 0;
        for (Map.Entry<String, Integer> entry : subjectLevels.entrySet()) {
            // 水平越低，权重越高（6 - level），默认level为3
            double weight = 6.0 - (entry.getValue() != null ? entry.getValue() : 3);
            weights.put(entry.getKey(), weight);
            totalWeight += weight;
        }
        if (weights.isEmpty()) return weights;

        // 归一化
        final double finalTotal = totalWeight;
        weights.replaceAll((k, v) -> v / finalTotal);
        return weights;
    }

    /**
     * 创建科目轮转序列
     * <p>
     * 算法：根据权重确定每个科目在轮转序列中的出现次数，然后随机打乱
     * </p>
     * @param subjectWeights 科目权重Map
     * @return 科目轮转序列列表
     */
    private List<String> createSubjectRotation(Map<String, Double> subjectWeights) {
        List<String> rotation = new ArrayList<>();
        if (subjectWeights.isEmpty()) return rotation;

        subjectWeights.forEach((subject, weight) -> {
            // 权重越大，出现次数越多（至少1次）
            int count = (int) Math.max(1, Math.round(weight * 10));
            for (int i = 0; i < count; i++) rotation.add(subject);
        });

        // 随机打乱以避免固定模式
        Collections.shuffle(rotation);
        return rotation;
    }

    /**
     * 检查是否还有剩余的知识点
     * @param remainingPoints 剩余知识点Map
     * @return 是否还有剩余
     */
    private boolean hasRemainingPoints(Map<String, List<KnowledgePointEntity>> remainingPoints) {
        return remainingPoints.values().stream().anyMatch(list -> !list.isEmpty());
    }

    /**
     * 批量保存学习计划项（分批处理避免性能问题）
     * @param planItems 计划项列表
     */
    private void savePlanItems(List<StudyPlanItemDTO> planItems) {
        if (planItems == null || planItems.isEmpty()) {
            logger.info("没有学习计划项需要保存");
            return;
        }

        // 每批保存100条，避免单次操作数据量过大
        int batchSize = 100;
        for (int i = 0; i < planItems.size(); i += batchSize) {
            int end = Math.min(i + batchSize, planItems.size());
            List<StudyPlanItemDTO> batch = planItems.subList(i, end);
            studyPlanItemService.saveBatch(batch);
            logger.debug("保存第 {} 批学习计划项，数量: {}", (i / batchSize) + 1, batch.size());
        }
        logger.info("成功保存 {} 个学习计划项", planItems.size());
    }

    /**
     * 获取用户Dashboard数据
     * <p>
     * 返回包括：计划名称、总天数、已完成/总任务数、剩余天数、整体进度、今日任务、所有任务等
     * </p>
     * @param userId 用户ID
     * @return Dashboard数据封装
     */
    @Override
    public DashboardDTO getDashboardData(Long userId) {
        logger.info("开始获取用户Dashboard数据，用户ID: {}", userId);

        // 1. 获取用户学习计划
        StudyPlanEntity plan = getByUserId(userId);
        if (plan == null) {
            logger.warn("用户 {} 没有学习计划", userId);
            return new DashboardDTO(); // 返回空对象而不是null，前端可识别无数据状态
        }

        // 2. 获取计划项
        List<StudyPlanItemEntity> planItems = studyPlanItemService.getByPlanId(plan.getId());
        logger.info("获取到 {} 个计划项", planItems.size());

        // 3. 计算统计数据（已完成包括正常完成1和补交完成2）
        int completedTasks = (int) planItems.stream().filter(item -> item.getCompleted() == 1 || item.getCompleted() == 2).count();
        int totalTasks = planItems.size();
        int remainingDays = calculateRemainingDays(plan);
        int overallProgress = calculateOverallProgress(completedTasks, totalTasks);

        // 4. 获取今日任务
        List<StudyPlanItemDTO> todayTasks = getTodayTasks(planItems, plan);
        logger.info("今日任务数量: {}", todayTasks.size());

        // 5. 转换所有计划项为DTO
        List<StudyPlanItemDTO> allTasks = planItems.stream().map(this::convertToDTO).collect(Collectors.toList());

        // 6. 封装DashboardDTO
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setPlanName(plan.getPlanName());
        dashboard.setTotalDays(plan.getTotalDays());
        dashboard.setCompletedTasks(completedTasks);
        dashboard.setTotalTasks(totalTasks);
        dashboard.setRemainingDays(remainingDays);
        dashboard.setOverallProgress(overallProgress);
        dashboard.setTodayTasks(todayTasks);
        dashboard.setAllTasks(allTasks);
        dashboard.setStartDate(plan.getStartDate());
        dashboard.setEndDate(plan.getEndDate());

        logger.info("Dashboard数据生成完成: 计划名称={}, 总天数={}, 已完成任务={}, 总任务={}, 剩余天数={}, 整体进度={}%",
                dashboard.getPlanName(), dashboard.getTotalDays(), dashboard.getCompletedTasks(),
                dashboard.getTotalTasks(), dashboard.getRemainingDays(), dashboard.getOverallProgress());

        return dashboard;
    }

    /**
     * 将实体转换为DTO（减少重复代码）
     */
    private StudyPlanItemDTO convertToDTO(StudyPlanItemEntity item) {
        StudyPlanItemDTO dto = new StudyPlanItemDTO();
        dto.setId(item.getId());
        dto.setPlanId(item.getPlanId());
        dto.setKnowledgePointId(item.getKnowledgePointId());
        dto.setDayIndex(item.getDayIndex());
        dto.setSort(item.getSort());
        dto.setCompleted(item.getCompleted());
        dto.setCompletionDate(item.getCompletionDate());
        dto.setSubjectName(item.getSubjectName());
        dto.setIsReview(item.getIsReview());
        dto.setSplitPart(item.getSplitPart());
        dto.setTotalParts(item.getTotalParts());
        dto.setSplitHours(item.getSplitHours());
        return dto;
    }

    /**
     * 计算剩余天数
     * @param plan 学习计划实体
     * @return 剩余天数
     */
    private int calculateRemainingDays(StudyPlanEntity plan) {
        if (plan.getStartDate() == null || plan.getEndDate() == null) {
            logger.warn("计划开始或结束日期为空，无法计算剩余天数");
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = plan.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (today.isAfter(endDate)) {
            logger.info("当前日期 {} 已超过计划结束日期 {}", today, endDate);
            return 0;
        }

        long daysBetween = ChronoUnit.DAYS.between(today, endDate);
        logger.debug("计算剩余天数: 今天={}, 结束日期={}, 剩余={}天", today, endDate, daysBetween);
        return (int) daysBetween;
    }

    /**
     * 获取今日任务
     * @param allItems 所有计划项
     * @param plan 学习计划
     * @return 今日任务DTO列表
     */
    private List<StudyPlanItemDTO> getTodayTasks(List<StudyPlanItemEntity> allItems, StudyPlanEntity plan) {
        if (allItems == null || allItems.isEmpty() || plan.getStartDate() == null) {
            logger.warn("计划项为空或开始日期为空，无法获取今日任务");
            return new ArrayList<>();
        }

        // 计算今天是第几天（第1天是startDate当天）
        LocalDate startDate = plan.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(startDate, today);
        int todayDayIndex = (int) daysBetween + 1;

        logger.debug("计算今日任务: 开始日期={}, 今天={}, 第{}天", startDate, today, todayDayIndex);

        // 过滤并转换今日任务
        return allItems.stream()
                .filter(item -> item.getDayIndex() != null && item.getDayIndex() == todayDayIndex)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 计算整体进度
     * 已完成任务包括正常完成（1）和补交完成（2）
     * @param completedTasks 已完成任务数
     * @param totalTasks 总任务数
     * @return 进度百分比
     */
    private int calculateOverallProgress(int completedTasks, int totalTasks) {
        if (totalTasks == 0) {
            logger.debug("总任务数为0，进度为0%");
            return 0;
        }
        int progress = (int) Math.round((completedTasks * 100.0) / totalTasks);
        logger.debug("计算整体进度: 已完成={}, 总任务={}, 进度={}%", completedTasks, totalTasks, progress);
        return progress;
    }
}
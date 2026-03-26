package com.jq.utils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TimeCostUtils {

    /**
     * 统计代码块耗时（毫秒）
     * @param taskName 任务名称（用于日志区分）
     * @param runnable 要执行的代码块
     */
   public static void calculateCost(String taskName, Runnable runnable) {
        // 1. 记录开始时间（纳秒转毫秒，保留精度）
       long start = System.nanoTime();
        try {
            // 2. 执行目标代码（Redis查询/Session校验）
           runnable.run();
        } finally {
            // 3. 计算耗时（纳秒 -> 毫秒，保留 2 位小数）
           long costNs = System.nanoTime() - start;
            double costMs = costNs / 1_000_000.0;
            // 4. 打印/记录耗时（生产环境建议用日志框架，便于监控）
           log.info("【耗时统计】{} 耗时：{} ms", taskName, String.format("%.2f", costMs));
        }
    }
}

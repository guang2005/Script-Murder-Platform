package com.jq.dto;

import lombok.Data;

import java.util.List;

/**
 * 滚动查询结果类
 * 用于存储分页查询的结果数据，包含数据列表、最小时间和偏移量
 */
@Data
public class ScrollResult {
    // 数据列表，存储查询结果的数据集合
    private List<?> list;
    // 最小时间，用于分页查询的时间点
    private Long minTime;
    // 偏移量，用于分页查询的偏移位置
    private Integer offset;
}

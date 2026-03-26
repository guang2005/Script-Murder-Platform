package com.jq.dto;

import lombok.Data;

/**
 * 用户数据传输对象(Data Transfer Object)
 * 用于在系统各层之间传递用户相关信息
 * 使用@Data注解自动生成getter、setter、toString等方法
 */
@Data
public class UserDTO {
    private Long id;
    private String nickName;
    private String icon;
}

package com.jq.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 登录表单数据传输对象(DTO)类
 * 使用@Data和@Builder注解简化代码，提供getter、setter、builder等功能
 */
@Data
@Builder
public class LoginFormDTO {
    // 手机号码字段，用于用户登录验证
    private String phone;
    // 验证码字段，用于短信验证登录
    private String code;
    // 密码字段，用于账号密码登录方式
    private String password;
}

package com.jq.utils;

import com.jq.dto.UserDTO;

/**
 * UserHolder类用于在当前线程中存储UserDTO对象
 * 使用ThreadLocal实现线程间的数据隔离，确保每个线程只能访问到自己的UserDTO实例
 */
public class UserHolder {
    // 使用ThreadLocal存储UserDTO对象，确保线程安全
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    /**
     * 保存UserDTO对象到当前线程中
     * @param user 需要保存的UserDTO对象
     */
    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    /**
     * 从当前线程中获取UserDTO对象
     * @return 当前线程中存储的UserDTO对象，如果没有则返回null
     */
    public static UserDTO getUser(){
        return tl.get();
    }

    /**
     * 移除当前线程中的UserDTO对象
     * 通常在请求处理完成后调用，防止内存泄漏
     */
    public static void removeUser(){
        tl.remove();
    }
}

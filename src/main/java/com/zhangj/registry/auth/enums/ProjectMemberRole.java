package com.zhangj.registry.auth.enums;

/**
 * @author zhangjun
 * @description
 * @date 2018/3/13
 */
public enum ProjectMemberRole {

    MASTER(1,"Master"),
    DEVELOPER(2,"Developer"),
    GUEST(3,"Guest");
    private Integer value;
    private String name;
    ProjectMemberRole(Integer value,String name){
        this.value=value;
        this.name=name;
    }
    public Integer value(){
        return this.value;
    }
    @Override
    public String toString(){
        return this.name;
    }
}

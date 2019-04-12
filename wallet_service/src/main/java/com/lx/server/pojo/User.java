package com.lx.server.pojo;

import java.util.Date;

import com.lx.server.pojo.AbstractObject;

/**
 * 【用户表】持久化对象 数据库表：t_user
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-04-12 20:03:00
 *
 */
public class User extends AbstractObject {

    public static final long serialVersionUID = 1L;

    // id
    private String id;
    // 昵称
    private String nickname;
    // 创建时间
    private Date createTime;

    /** 获取 id 属性 */
    public String getId() {
        return id;
    }

    /** 设置 id 属性 */
    public void setId(String id) {
        this.id = id;
    }

    /** 获取 昵称 属性 */
    public String getNickname() {
        return nickname;
    }

    /** 设置 昵称 属性 */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /** 获取 创建时间 属性 */
    public Date getCreateTime() {
        return createTime;
    }

    /** 设置 创建时间 属性 */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("User");
        sb.append("{id=").append(id);
        sb.append(", nickname=").append(nickname);
        sb.append(", createTime=").append(createTime);
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof User) {
            User user = (User) obj;
            if (this.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        String pkStr = "" + this.getId();
        return pkStr.hashCode();
    }

}

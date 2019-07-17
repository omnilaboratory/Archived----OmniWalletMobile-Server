package com.lx.server.pojo;

import java.math.BigDecimal;
import java.util.Date;

import com.lx.server.pojo.AbstractObject;

/**
 * 【某人某个时刻的某个地址某个资产的价值记录】持久化对象 数据库表：t_log_user_asset
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-07-16 16:01:28
 *
 */
public class LogUserAsset extends AbstractObject {

    public static final long serialVersionUID = 1L;

    // Id
    private Long id;
    // 用户id
    private String userId;
    // 地址
    private String addr;
    // 资产id
    private Long assetId;
    // 数量
    private BigDecimal amount;
    // 写入时间
    private Date createTime;

    /** 获取 Id 属性 */
    public Long getId() {
        return id;
    }

    /** 设置 Id 属性 */
    public void setId(Long id) {
        this.id = id;
    }

    /** 获取 用户id 属性 */
    public String getUserId() {
        return userId;
    }

    /** 设置 用户id 属性 */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** 获取 地址 属性 */
    public String getAddr() {
        return addr;
    }

    /** 设置 地址 属性 */
    public void setAddr(String addr) {
        this.addr = addr;
    }

    /** 获取 资产id 属性 */
    public Long getAssetId() {
        return assetId;
    }

    /** 设置 资产id 属性 */
    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    /** 获取 数量 属性 */
    public BigDecimal getAmount() {
        return amount;
    }

    /** 设置 数量 属性 */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /** 获取 写入时间 属性 */
    public Date getCreateTime() {
        return createTime;
    }

    /** 设置 写入时间 属性 */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LogUserAsset");
        sb.append("{id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", addr=").append(addr);
        sb.append(", assetId=").append(assetId);
        sb.append(", amount=").append(amount);
        sb.append(", createTime=").append(createTime);
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LogUserAsset) {
            LogUserAsset logUserAsset = (LogUserAsset) obj;
            if (this.getId().equals(logUserAsset.getId())) {
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

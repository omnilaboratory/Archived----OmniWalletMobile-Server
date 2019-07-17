package com.lx.server.pojo;

import java.math.BigDecimal;
import java.util.Date;

import com.lx.server.pojo.AbstractObject;

/**
 * 【某个时刻的价格信息】持久化对象 数据库表：t_log_user_asset_price
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-07-16 16:01:30
 *
 */
public class LogUserAssetPrice extends AbstractObject {

    public static final long serialVersionUID = 1L;

    // Id
    private Long id;
    // 资产id
    private Long assetId;
    // 人民币价格
    private BigDecimal priceCny;
    // 美元价格
    private BigDecimal priceUsd;
    // 欧元价格
    private BigDecimal priceEur;
    // 此时的价格信息json
    private String priceInfo;
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

    /** 获取 资产id 属性 */
    public Long getAssetId() {
        return assetId;
    }

    /** 设置 资产id 属性 */
    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    /** 获取 人民币价格 属性 */
    public BigDecimal getPriceCny() {
        return priceCny;
    }

    /** 设置 人民币价格 属性 */
    public void setPriceCny(BigDecimal priceCny) {
        this.priceCny = priceCny;
    }

    /** 获取 美元价格 属性 */
    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    /** 设置 美元价格 属性 */
    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    /** 获取 欧元价格 属性 */
    public BigDecimal getPriceEur() {
        return priceEur;
    }

    /** 设置 欧元价格 属性 */
    public void setPriceEur(BigDecimal priceEur) {
        this.priceEur = priceEur;
    }

    /** 获取 此时的价格信息json 属性 */
    public String getPriceInfo() {
        return priceInfo;
    }

    /** 设置 此时的价格信息json 属性 */
    public void setPriceInfo(String priceInfo) {
        this.priceInfo = priceInfo;
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
        sb.append("LogUserAssetPrice");
        sb.append("{id=").append(id);
        sb.append(", assetId=").append(assetId);
        sb.append(", priceCny=").append(priceCny);
        sb.append(", priceUsd=").append(priceUsd);
        sb.append(", priceEur=").append(priceEur);
        sb.append(", priceInfo=").append(priceInfo);
        sb.append(", createTime=").append(createTime);
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LogUserAssetPrice) {
            LogUserAssetPrice logUserAssetPrice = (LogUserAssetPrice) obj;
            if (this.getId().equals(logUserAssetPrice.getId())) {
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

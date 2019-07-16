package com.lx.server.pojo;

import java.math.BigDecimal;
import java.util.Date;

import com.lx.server.pojo.AbstractObject;

/**
 * 【某人在某个地址的某个资产在什么时候发起了一笔转出到某个地址的操作日志】持久化对象 数据库表：t_log_transaction
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-07-16 11:37:56
 *
 */
public class LogTransaction extends AbstractObject {

    public static final long serialVersionUID = 1L;

    // 
    private Integer id;
    // 助记词生成的用户id
    private String userId;
    // 转账地址
    private String fromAddr;
    // 目标地址
    private String toAddr;
    // 资产id
    private Integer assetId;
    // 金额
    private BigDecimal amount;
    // 矿工费
    private BigDecimal fee;
    // 交易id
    private String txid;
    // 
    private Date createTime;

    /** 获取  属性 */
    public Integer getId() {
        return id;
    }

    /** 设置  属性 */
    public void setId(Integer id) {
        this.id = id;
    }

    /** 获取 助记词生成的用户id 属性 */
    public String getUserId() {
        return userId;
    }

    /** 设置 助记词生成的用户id 属性 */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** 获取 转账地址 属性 */
    public String getFromAddr() {
        return fromAddr;
    }

    /** 设置 转账地址 属性 */
    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }

    /** 获取 目标地址 属性 */
    public String getToAddr() {
        return toAddr;
    }

    /** 设置 目标地址 属性 */
    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }

    /** 获取 资产id 属性 */
    public Integer getAssetId() {
        return assetId;
    }

    /** 设置 资产id 属性 */
    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    /** 获取 金额 属性 */
    public BigDecimal getAmount() {
        return amount;
    }

    /** 设置 金额 属性 */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /** 获取 矿工费 属性 */
    public BigDecimal getFee() {
        return fee;
    }

    /** 设置 矿工费 属性 */
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    /** 获取 交易id 属性 */
    public String getTxid() {
        return txid;
    }

    /** 设置 交易id 属性 */
    public void setTxid(String txid) {
        this.txid = txid;
    }

    /** 获取  属性 */
    public Date getCreateTime() {
        return createTime;
    }

    /** 设置  属性 */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LogTransaction");
        sb.append("{id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", fromAddr=").append(fromAddr);
        sb.append(", toAddr=").append(toAddr);
        sb.append(", assetId=").append(assetId);
        sb.append(", amount=").append(amount);
        sb.append(", fee=").append(fee);
        sb.append(", txid=").append(txid);
        sb.append(", createTime=").append(createTime);
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LogTransaction) {
            LogTransaction logTransaction = (LogTransaction) obj;
            if (this.getId().equals(logTransaction.getId())) {
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

package com.lx.server.pojo;

import java.util.Date;

import com.lx.server.pojo.AbstractObject;

/**
 * 【用户钱包地址的资产】持久化对象 数据库表：t_wallet_asset
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-04-13 23:59:48
 *
 */
public class WalletAsset extends AbstractObject {

    public static final long serialVersionUID = 1L;

    // id
    private Integer id;
    // t_wallet_address id
    private Integer addressId;
    // 支持类型 0 btc币，1omni资产
    private Byte assetType;
    // 对应的omni的支持id
    private Integer assetId;
    // 创建时间
    private Date createTime;
    // t_user_client id
    private String userId;

    /** 获取 id 属性 */
    public Integer getId() {
        return id;
    }

    /** 设置 id 属性 */
    public void setId(Integer id) {
        this.id = id;
    }

    /** 获取 t_wallet_address id 属性 */
    public Integer getAddressId() {
        return addressId;
    }

    /** 设置 t_wallet_address id 属性 */
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    /** 获取 支持类型 0 btc币，1omni资产 属性 */
    public Byte getAssetType() {
        return assetType;
    }

    /** 设置 支持类型 0 btc币，1omni资产 属性 */
    public void setAssetType(Byte assetType) {
        this.assetType = assetType;
    }

    /** 获取 对应的omni的支持id 属性 */
    public Integer getAssetId() {
        return assetId;
    }

    /** 设置 对应的omni的支持id 属性 */
    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    /** 获取 创建时间 属性 */
    public Date getCreateTime() {
        return createTime;
    }

    /** 设置 创建时间 属性 */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /** 获取 t_user_client id 属性 */
    public String getUserId() {
        return userId;
    }

    /** 设置 t_user_client id 属性 */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("WalletAsset");
        sb.append("{id=").append(id);
        sb.append(", addressId=").append(addressId);
        sb.append(", assetType=").append(assetType);
        sb.append(", assetId=").append(assetId);
        sb.append(", createTime=").append(createTime);
        sb.append(", userId=").append(userId);
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof WalletAsset) {
            WalletAsset walletAsset = (WalletAsset) obj;
            if (this.getId().equals(walletAsset.getId())) {
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

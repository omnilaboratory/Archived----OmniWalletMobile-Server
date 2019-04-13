package com.lx.server.enums;

import org.springframework.util.StringUtils;

/**
 * 数据库表名
 *
 */
public enum EnumDBTableName {
	
	t_default_type_table("t_default_type_table"),
	t_user("t_user"),
	;
	
	public final String value;
	
	EnumDBTableName(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	public static EnumDBTableName getValue(String tableName) {
		if (StringUtils.isEmpty(tableName)==true) {
			return null;
		}
		for (EnumDBTableName examType : EnumDBTableName.values()) {
            if (examType.value.equals(tableName)) {
                return examType;
            }
        }
        return null;
	}
}

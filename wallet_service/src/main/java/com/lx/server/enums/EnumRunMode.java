package com.lx.server.enums;

/**
 * 运行模式
 * pro test dev
 *
 */
public enum EnumRunMode {
	
	error("error"),
	pro("pro"),
	test("test"),
	dev("dev"),
	;
	
	public String value;
	
	EnumRunMode(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	public static EnumRunMode getValue(String name) {
		for (EnumRunMode examType : EnumRunMode.values()) {
            if (examType.value.equals(name)) {
                return examType;
            }
        }
        return error;
	}
}

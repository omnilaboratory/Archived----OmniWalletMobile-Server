package com.lx.server.enums;

/**
 * kafka topic
 * @author ZhuGuojun
 *
 */
public enum EnumKafkaTopic {

	DefaultTopic("defaultTopic"),
	UserTopic("userTopic"),
	;
	
	public String value;
	
	EnumKafkaTopic(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	public static EnumKafkaTopic getValue(String name) {
		for (EnumKafkaTopic examType : EnumKafkaTopic.values()) {
            if (examType.value.equals(name)) {
                return examType;
            }
        }
        return DefaultTopic;
	}
}

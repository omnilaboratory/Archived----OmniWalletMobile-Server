package com.lx.server.enums;

import com.lx.server.config.GlobalConfig;

/**
 * 文件保存的路径
 * @author ZhuGuojun
 *
 */
public enum EnumFolderURI {

	Default_Folder("/image/wallet/"+GlobalConfig.runMode+"/default/"),
	Image_userface("/image/wallet/"+GlobalConfig.runMode+"/userface/"),
	appPack("/image/wallet/"+GlobalConfig.runMode+"/appPack/"),
	coinImage("/image/wallet/"+GlobalConfig.runMode+"/coinImage/"),
	;
	
	public String value;
	
	EnumFolderURI(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	public static EnumFolderURI getValue(String name) {
		for (EnumFolderURI examType : EnumFolderURI.values()) {
            if (examType.value.equals(name)) {
                return examType;
            }
        }
        return Default_Folder;
	}
	
	
	public static EnumFolderURI getEnumByType(Integer type) {
		switch (type) {
		case 0:
			return Image_userface;
		}
		return Default_Folder;
	}
}

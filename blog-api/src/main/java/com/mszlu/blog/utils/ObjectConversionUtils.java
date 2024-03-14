package com.mszlu.blog.utils;

import com.mszlu.blog.dao.pojo.*;
import com.mszlu.blog.vo.*;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ObjectConversionUtils {

    // 将List<SysUser>转化为List<SysUserVo>
    public static List<SysUserVo> copyList(List<SysUser> sysUserList) {
        List<SysUserVo> sysUserVoList = new ArrayList<>();
        for (SysUser sysUser : sysUserList) {
            sysUserVoList.add(copy(sysUser));
        }
        return sysUserVoList;
    }
    // 将sysUser转化为SysUserVo
    public static SysUserVo copy(SysUser sysUser) {
        SysUserVo sysUserVo = new SysUserVo();
        BeanUtils.copyProperties(sysUser,sysUserVo);
        return sysUserVo;
    }

}

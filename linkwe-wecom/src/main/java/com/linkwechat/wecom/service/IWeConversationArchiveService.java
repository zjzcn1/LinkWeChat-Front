package com.linkwechat.wecom.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

/**
 * @author sxw
 * @description 会话存档业务接口
 * @date 2020/12/19 13:59
 **/
public interface IWeConversationArchiveService {
    /**
     * 根据用户ID 获取对应内部联系人列表
     *
     * @param userId
     * @return
     */
    PageInfo<JSONObject> getInternalContactList(String userId, int pageSize, int pageNum);
}

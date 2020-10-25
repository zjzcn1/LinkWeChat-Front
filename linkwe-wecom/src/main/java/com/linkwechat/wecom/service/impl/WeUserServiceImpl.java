package com.linkwechat.wecom.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linkwechat.common.constant.Constants;
import com.linkwechat.common.constant.WeConstans;
import com.linkwechat.common.exception.wecom.WeComException;
import com.linkwechat.framework.web.domain.server.Sys;
import com.linkwechat.wecom.client.WeDepartMentClient;
import com.linkwechat.wecom.client.WeUserClient;
import com.linkwechat.wecom.domain.WeDepartment;
import com.linkwechat.wecom.domain.WeUser;
import com.linkwechat.wecom.domain.dto.LeaveWeUserListsDto;
import com.linkwechat.wecom.domain.dto.WeDepartMentDto;
import com.linkwechat.wecom.domain.dto.WeUserDto;
import com.linkwechat.wecom.domain.dto.WeUserListDto;
import com.linkwechat.wecom.domain.vo.WeLeaveUserInfoAllocateVo;
import com.linkwechat.wecom.domain.vo.WeLeaveUserVo;
import com.linkwechat.wecom.mapper.WeUserMapper;
import com.linkwechat.wecom.service.IWeCustomerService;
import com.linkwechat.wecom.service.IWeDepartmentService;
import com.linkwechat.wecom.service.IWeGroupService;
import com.linkwechat.wecom.service.IWeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 通讯录相关客户Service业务层处理
 * 
 * @author ruoyi
 * @date 2020-08-31
 */
@Service
public class WeUserServiceImpl extends ServiceImpl<WeUserMapper,WeUser> implements IWeUserService
{
    @Autowired
    private WeUserMapper weUserMapper;


    @Autowired
    private WeUserClient weUserClient;


    @Autowired
    private IWeCustomerService iWeCustomerService;


    @Autowired
    private IWeGroupService iWeGroupService;


    @Autowired
    private IWeDepartmentService iWeDepartmentService;

    /**
     * 查询通讯录相关客户
     *
     * @param id 通讯录相关客户ID
     * @return 通讯录相关客户
     */
    @Override
    public WeUser selectWeUserById(Long id)
    {
        return weUserMapper.selectWeUserById(id);
    }

    /**
     * 查询通讯录相关客户列表
     * 
     * @param weUser 通讯录相关客户
     * @return 通讯录相关客户
     */
    @Override
    public List<WeUser> selectWeUserList(WeUser weUser)
    {
        return weUserMapper.selectWeUserList(weUser);
    }

    /**
     * 新增通讯录相关客户
     * 
     * @param weUser 通讯录相关客户
     * @return 结果
     */
    @Override
    @Transactional
    public void insertWeUser(WeUser weUser)
    {


        if(this.save(weUser)){
            weUserClient.createUser(
                    weUser.transformWeUserDto()
            );
        }

    }

    /**
     * 修改通讯录相关客户
     * 
     * @param weUser 通讯录相关客户
     * @return 结果
     */
    @Override
    @Transactional
    public void updateWeUser(WeUser weUser)
    {

        if(this.updateById(weUser)){
            weUserClient.updateUser(
                    weUser.transformWeUserDto()
            );
        }
    }




    /**
     *  启用或禁用用户
     * @param weUser
     * @return
     */
    @Override
    public void startOrStop(WeUser weUser) {
        this.updateWeUser(weUser);
    }


    /**
     * 离职未分配员工
     * @param weLeaveUserVo
     * @return
     */
    @Override
    public List<WeLeaveUserVo> leaveNoAllocateUserList(WeLeaveUserVo weLeaveUserVo) {
        return this.weUserMapper.leaveNoAllocateUserList(weLeaveUserVo);
    }


    /**
     * 离职已分配员工
     * @param weLeaveUserVo
     * @return
     */
    @Override
    public List<WeLeaveUserVo> leaveAllocateUserList(WeLeaveUserVo weLeaveUserVo) {
        return null;
    }


    /**
     * 分配离职员工相关数据
     * @param weLeaveUserInfoAllocateVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allocateLeaveUserAboutData(WeLeaveUserInfoAllocateVo weLeaveUserInfoAllocateVo) {

        try {
            //分配客户
            iWeCustomerService.allocateWeCustomer(weLeaveUserInfoAllocateVo);

            //分配群组
            iWeGroupService.allocateWeGroup(weLeaveUserInfoAllocateVo);
        }catch (Exception e){
            throw new WeComException(e.getMessage());
        }

    }


    /**
     * 同步成员
     */
    @Override
    @Transactional
    public void synchWeUser(){

        //同步部门
        iWeDepartmentService.synchWeDepartment();

        List<WeUser> weUsers
                = weUserClient.list(WeConstans.WE_ROOT_DEPARMENT_ID, WeConstans.DEPARTMENT_SUB_WEUSER).getWeUsers();
        if(CollectionUtil.isNotEmpty(weUsers)){

            this.saveOrUpdateBatch(weUsers);
        }

    }


    /**
     * 删除用户
     * @param ids
     */
    @Override
    @Transactional
    public void deleteUser(String[] ids) {

        List<WeUser> weUsers=new ArrayList<>();
        CollectionUtil.newArrayList(ids).stream().forEach(k->{
            weUsers.add(
                    WeUser.builder()
                            .userId(k)
                            .enable(WeConstans.WE_USER_IS_LEAVE)
                            .dimissionTime(new Date())
                            .build()
            );
        });

        if(this.updateBatchById(weUsers)){
            weUsers.stream().forEach(k->{
                weUserClient.deleteUserByUserId(k.getUserId());
            });
        }

    }


}

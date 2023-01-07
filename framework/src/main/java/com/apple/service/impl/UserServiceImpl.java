package com.apple.service.impl;

import com.apple.constants.SystemConstants;
import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddUserDto;
import com.apple.domain.entity.Role;
import com.apple.domain.entity.User;
import com.apple.domain.entity.UserRole;
import com.apple.domain.vo.*;
import com.apple.enums.AppHttpCodeEnum;
import com.apple.exception.SystemException;
import com.apple.mapper.RoleMapper;
import com.apple.mapper.UserMapper;
import com.apple.mapper.UserRoleMapper;
import com.apple.service.UserRoleService;
import com.apple.service.UserService;
import com.apple.utils.BeanCopyUtils;
import com.apple.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult register(User user) {
        //对数据进行非空判断
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        //对数据进行是否存在的判断
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        //...
        //对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
    }

    /**
     * 分页列表
     * @param pageNum
     * @param pageSize
     * @param phonenumber
     * @param status
     * @return
     */
    @Override
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize,String userName, String phonenumber, String status
                                       ) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(userName),User::getUserName,userName);
        lambdaQueryWrapper.eq(StringUtils.hasText(phonenumber),User::getPhonenumber,phonenumber);
        lambdaQueryWrapper.eq(StringUtils.hasText(status),User::getStatus,status);

        Page<User> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,lambdaQueryWrapper);
        List<UserListVo> userListVos = BeanCopyUtils.copyBeanList(page.getRecords(), UserListVo.class);
        PageVo pageVo = new PageVo(userListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 添加用户
     * @param addUserDto
     * @return
     */
    @Override
    public ResponseResult addUser(AddUserDto addUserDto) {
        //用户名不能为空
        if(addUserDto.getUserName() == null){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        //用户名不能重复
        if(userNameExist(addUserDto.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        //手机号不能重复
        if(addUserDto.getPhonenumber() == null){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_NOT_NULL);
        }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getPhonenumber, addUserDto.getPhonenumber());
        List<User> users1 = getBaseMapper().selectList(userLambdaQueryWrapper);
        if(users1.size() > 0){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        //邮箱不能重复
        if(addUserDto.getEmail() == null){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,addUserDto.getEmail());
        List<User> users2 = getBaseMapper().selectList(queryWrapper);
        if(users2.size() > 0){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        //密码不能为空
        if(addUserDto.getPassword() == null){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }

        //对密码进行加密
        User user = BeanCopyUtils.copyBean(addUserDto, User.class);
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //存入数据库
        save(user);

        //添加用户-角色关联
        for (Long roleId : addUserDto.getRoleIds()) {
            userRoleService.save(new UserRole(user.getId(),roleId));
        }
        return ResponseResult.okResult();
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ResponseResult deleteUser(Long id) {
        getBaseMapper().deleteById(id);
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole::getUserId,id);
        userRoleService.getBaseMapper().delete(lambdaQueryWrapper);
        return ResponseResult.okResult();
    }

    /**
     * 查询用户-角色信息
     * @param id
     * @return
     */
    @Override
    public ResponseResult<UserVo> getUser(Long id) {
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserRole::getUserId,id);
        List<UserRole> userRoles = userRoleService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Role> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Role::getStatus, SystemConstants.STATUS_NORMAL);
        List<Role> list = roleMapper.selectList(lambdaQueryWrapper1);

        User user = getBaseMapper().selectById(id);
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);

        UserVo userVo = new UserVo(roleIds,list,userInfoVo);
        return ResponseResult.okResult(userVo);
    }

    /**
     * 修改用户信息
     * @param userVos
     * @return
     */
    @Override
    @Transactional
    public ResponseResult updateUser(UserVos userVos) {
        User user = BeanCopyUtils.copyBean(userVos, User.class);
        updateById(user);
        //删除原来的用户-角色信息
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(UserRole::getUserId,user.getId());
        userRoleService.getBaseMapper().delete(lambdaQueryWrapper1);
        //添加用户-角色信息
        for (Long roleId : userVos.getRoleIds()) {
            userRoleService.save(new UserRole(user.getId(),roleId));
        }

        return ResponseResult.okResult();
    }

    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName,nickName);
        return count(queryWrapper) > 0;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        return count(queryWrapper) > 0;
    }
}

package com.yupi.yupicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupicturebackend.common.PageRequest;
import com.yupi.yupicturebackend.constant.UserConstant;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.model.dto.user.UserQueryRequest;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.enums.UserRoleEnum;
import com.yupi.yupicturebackend.model.vo.LoginUserVO;
import com.yupi.yupicturebackend.model.vo.UserVO;
import com.yupi.yupicturebackend.service.UserService;
import com.yupi.yupicturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-03-19 22:07:53
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1 校验参数
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }
        //2 检查用户账号是否和数据库中已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号重复");
        }
        //3 密码一定要加密
        String encryptedPassword = getEncryptedPassword(userPassword);
        //4 插入数据到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveRole = this.save(user);
        if (!saveRole){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户注册失败");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1 校验参数
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号错误");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码错误");
        }
        //2 对用户传递的密码进行加密
        String encryptedPassword = getEncryptedPassword(userPassword);
        //3 查询数据库中的用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptedPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        //不存在，抛异常
        if (user == null){
            log.error("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或者账号密码错误");
        }
        //4 保存用户登录状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    @Override
    public String getEncryptedPassword(String userPassword){
        //加盐，混淆
        final String SALT = "yupi";
        return DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        //判断是否已经登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //先在数据库中查询，非必要不要从缓存中查询（追求性能，就可以直接返回上述结果）
        Long userId=currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取脱敏的登录用户信息
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取脱敏的用户信息
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏的用户信息列表
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (userList == null){
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        //判断是否已经登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"未登录");
        }
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest){
        if (userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }
}





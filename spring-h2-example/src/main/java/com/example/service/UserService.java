package com.example.service;

import com.example.common.ObjectUtils;
import com.example.entity.User;
import com.example.pojo.vo.PageResult;
import com.example.pojo.vo.user.UserVO;
import com.example.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fdrama
 * date 2023年05月08日 14:05
 */
@Service
@Slf4j
public class UserService {

    @Resource
    private UserRepository userRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public List<UserVO> queryAll() {
        return convert2UserVoList(userRepository.findAll());
    }

    public UserVO findOne(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("user not found"));
        return convert2UserVo(user);
    }

    public UserVO update(UserVO userVO) {
        if (userRepository.existsById(userVO.getId())) {

            User save = userRepository.save(convert2User(userVO));
            return convert2UserVo(save);
        } else {
            throw new IllegalArgumentException("user not found");
        }
    }

    public void delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("user not found");
        }
    }

    public UserVO add(UserVO userVO) {
        User save = userRepository.save(convert2User(userVO));
        return convert2UserVo(save);
    }

    public UserVO patch(UserVO userVO) {
        if (userRepository.existsById(userVO.getId())) {
            User record = userRepository.findById(userVO.getId()).get();
            ObjectUtils.update(record, userVO);
            User save = userRepository.save(record);
            return convert2UserVo(save);
        } else {
            throw new IllegalArgumentException("user not found");
        }
    }

    public PageResult<UserVO> pageQuery(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<User> users = userRepository.findAll(pageable);
        List<User> content = users.getContent();
        long totalElements = users.getTotalElements();
        return new PageResult.Builder<UserVO>().total(totalElements).currentPage(pageNum).pageSize(pageSize).data(convert2UserVoList(content)).buildForSuccess();
    }

    private User convert2User(UserVO userVO) {
        User user = new User();
        BeanUtils.copyProperties(userVO, user);
        return user;
    }

    private UserVO convert2UserVo(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    private List<UserVO> convert2UserVoList(List<User> users) {
        List<UserVO> vos = new ArrayList<>(users.size());
        for (User user : users) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            vos.add(userVO);
        }
        return vos;
    }
}

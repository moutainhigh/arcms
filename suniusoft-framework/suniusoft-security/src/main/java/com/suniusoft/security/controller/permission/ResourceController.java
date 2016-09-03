package com.suniusoft.security.controller.permission;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.suniusoft.common.utils.JSONUtils;
import com.suniusoft.security.biz.domain.generation.permission.Resource;
import com.suniusoft.security.biz.domain.generation.permission.Role;
import com.suniusoft.security.biz.domain.generation.permission.User;
import com.suniusoft.security.biz.service.permission.ResourceService;
import com.suniusoft.security.biz.service.permission.SecurityManageService;
import com.suniusoft.security.biz.service.permission.SecurityUserService;
import com.suniusoft.security.biz.service.permission.UserRoleService;
import com.suniusoft.security.controller.BaseController;
import com.suniusoft.security.interceptor.constant.LoginConstant;
import com.suniusoft.security.vo.ResourceVO;
import com.suniusoft.security.vo.UserVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 *   
 *  @ProjectName: icard 
 *  @Description: <p>
 * </p>
 *  @author yuyuchi  yuyc@suniusoft.com
 *  @date 2015/10/31  
 */
@Controller
public class ResourceController extends BaseController {

    private static final Logger logger = Logger.getLogger(BaseController.class);
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SecurityUserService securityUserService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SecurityManageService securityManageService;

    @RequestMapping(value = "/resourceManage")
    public ModelAndView index(ModelMap modelMap) {


        return new ModelAndView("permission/resourceList", modelMap);
    }

    @RequestMapping(value = "/admin/resourceTreeList")
    public void showTree(HttpServletRequest reqeust,
                         HttpServletResponse response) throws IOException {

        Map<String, Object> dataMap = Maps.newHashMap();

        response.setCharacterEncoding("utf-8");
        String id = reqeust.getParameter("resourceId");

        String s = "";
        List<Resource> list = new ArrayList<Resource>();

        if (id == null) {

            list = resourceService.getChildResources(0l);


        } else {

            list = resourceService.getChildResources(Long.valueOf(id));


        }

        List<ResourceVO> list1 = new ArrayList<ResourceVO>();

        for (Resource r : list) {

            long isd = r.getId();
            long pid = r.getParentResourceId();
            String name = r.getName();
            String parent = r.getIsParent() == true ? "true" : "false";
            ResourceVO res = new ResourceVO();
            res.setId(isd);
            res.setName(name);
            res.setPid(pid);
            res.setIsParent(r.getIsParent());
            res.setResourceId(r.getResourceId());
            list1.add(res);
        }
        s = JSONUtils.toJSONStr(list1);
        response.getWriter().print(s);


    }


    @RequestMapping(value = "/admin/resourceList")
    public Map<String, Object> showResource(HttpServletRequest reqeust, ResourceVO resourceVO, Integer pageNum, Integer length) throws IOException {
        {

            Map<String, Object> dataMap = Maps.newHashMap();
            String id = reqeust.getParameter("id");

            if (id == null || "".equals(id)) {


                resourceVO.setResourceId(0l);
            }else{
                resourceVO.setResourceId(Long.valueOf(id));

            }
            PageInfo pageInfo = resourceService.queryResource(resourceVO, pageNum, length);

            if (pageInfo != null) {
                dataMap.put("recordsFiltered", pageInfo.getTotal());
                dataMap.put("recordsTotal", pageInfo.getTotal());
                dataMap.put("data", pageInfo.getList());
            }

            return dataMap;
        }


    }

    @RequestMapping(value = "/admin/editResource")
    public Map<String, Object> editUser(ResourceVO resourceVO) {

        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put(ERROR_CODE_KEY, ErrorCode.ERROR);

        try {


            if (resourceVO.getType().equals("menu")) {
                resourceVO.setIsParent(true);
            } else
                resourceVO.setIsParent(false);
            if (resourceVO.getParentResourceId() == null)
                resourceVO.setParentResourceId(0l);
            boolean code = resourceService.insertOrUpdateResource(resourceVO);


            if (code) {
                dataMap.put(ERROR_CODE_KEY, ErrorCode.SUCCESS);
                dataMap.put(ERROR_MESSAGE_KEY, "操作成功");
            } else {
                dataMap.put(ERROR_MESSAGE_KEY, "操作失败");
            }
        } catch (Exception e) {
            logger.error("UserController editUser occur error", e);
            dataMap.put(ERROR_MESSAGE_KEY, "操作异常");
        }

        return dataMap;
    }

    @RequestMapping(value = "/admin/delResource")
    public Map<String, Object> changePass(Long id) {

        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put(ERROR_CODE_KEY, ErrorCode.ERROR);

        if (resourceService.deleteResource(id)) {
            dataMap.put(ERROR_CODE_KEY, ErrorCode.SUCCESS);
            dataMap.put(ERROR_MESSAGE_KEY, "删除成功");
        }

        return dataMap;
    }


    @RequestMapping(value = "/admin/getResource")
    public Map<String, Object> getResource(ResourceVO resourceVO) {

        Map<String, Object> dataMap = Maps.newHashMap();

        Resource resource = resourceService.getResourceById(resourceVO);

        dataMap.put("resource", resource);

        return dataMap;
    }


}

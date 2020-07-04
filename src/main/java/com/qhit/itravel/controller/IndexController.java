package com.qhit.itravel.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qhit.itravel.dao.RouteDao;
import com.qhit.itravel.entity.*;
import com.qhit.itravel.service.CategoryService;
import com.qhit.itravel.service.UserService;
import com.qhit.itravel.utils.CpachaUtil;
import com.qhit.itravel.utils.JSONUtils;
import com.qhit.itravel.utils.RedisUtil;
import com.qhit.itravel.utils.ResultInfo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/front")
public class IndexController {
@Autowired
private CategoryService categoryService;

    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private RouteDao routeDao;
    @Autowired
    private UserService userService;

    @RequestMapping("/index")
    public String toIndex(Model model){
        //看一下redis里有没有缓存，如果有就取缓存，如果没有就去查数据库
        //数据库里查出来 再往缓存里写一份
        help(model);

        return "index";
    }

    //分页查询 国内游，酒店啥的
    @GetMapping("/getRouteList")
    public String getRouteList(@RequestParam("cid") Integer cid,Model model,
                               @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "5")Integer pageSize){
        //1启动分页
        PageHelper.startPage(pageNum,pageSize);
        //2查找所有数据
        List<Route> routes = routeDao.getRouteList(cid);
        //3构建pageInfo并返回
        PageInfo<Route> pageInfo = new PageInfo<>(routes);
        //4通过model把数据传回去
        model.addAttribute("pageInfo",pageInfo);
        //处理头部分类信息缓存
        help(model);

        return "route_list";

    }

    //详情
    @GetMapping("/routedetail")
    public String routeDetail(@RequestParam("rid") Long rid,Model model){
        Route route = routeDao.getById(rid);
        model.addAttribute("route",route);

       //商家信息
        Seller seller = routeDao.getSeller(route.getSid());
        model.addAttribute("seller",seller);

        //根据当前路线rid，查询路线大小图信息
        List<RouteImg> list=routeDao.getRouteImgsByRid(route.getRid());
        model.addAttribute("imglist",list);
        //处理头部分类信息缓存
        help(model);

        return "route_detail";
    }


    //头部分类信息缓存的封装方法
    private  void help(Model model){
        //看一下redis里有没有缓存数据，如果有取缓存，如果没有则去查数据库，
        // 从数据库里查出来别忘了往缓存里写一份
        List<Object> categoriesList = redisUtil.rangeList("categoriesList", 0, -1);
        if (categoriesList==null || categoriesList.size()==0){
            // 把分类数据带过去
            List<Category> list = categoryService.queryAll();
            redisUtil.lSet("categoriesList",list);
            redisUtil.expire("categoriesList",60*60*60);
            model.addAttribute("categories",list);
        }else {
            model.addAttribute("categories",categoriesList.get(0));
            System.out.println(categoriesList.get(0));
        }
    }



//----------------------------------下面是旅游的验证码，注册，登录-----------------------------------------
    //处理验证码
    @RequestMapping(value = "/checkCode",method = RequestMethod.GET)
    public void get(HttpServletResponse response, HttpServletRequest request){
        CpachaUtil cpachaUtil = new CpachaUtil(4, 120, 40);
        String vCode = cpachaUtil.generatorVCode();
        //存储到session作用域里面
        request.getSession().setAttribute("vCode",vCode);

        BufferedImage bufferedImage = cpachaUtil.generatorRotateVCodeImage(vCode, true);
        try {
            ImageIO.write(bufferedImage,"gif",response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    //鼠标失去事件，判断用户名存不存在
    @RequestMapping(value = "isUsername",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String isUsername(@RequestParam(value = "username") String username){
        ResultInfo info = new ResultInfo();
        User user= userService.getname(username);
        if (user==null){
            info.setFlag(false);
        }else {
            info.setFlag(true);
        }

        return JSONUtils.getJson(info);


    }

    //登录
    @RequestMapping(value = "/loginnn",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String login(@RequestParam("username")String username, @RequestParam("password")String password, HttpServletRequest request, @RequestParam("check")String check,Model model){
        ResultInfo resultInfo = new ResultInfo();

        String vCode = (String) request.getSession().getAttribute("vCode");
        if (StringUtils.equalsIgnoreCase(vCode,check)){
            User user = userService.getname(username);
            if (user==null){
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("用户名错误");
            }else {
                user = userService.login(username,password);
                if (user==null){
                    resultInfo.setFlag(false);
                    resultInfo.setErrorMsg("密码错误");
                }else {
                    resultInfo.setFlag(true);
                    resultInfo.setErrorMsg("登录成功");
                    request.getSession().setAttribute("loginUser",user);
                }
            }

        }else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码不正确");
        }



        // 从数据库里查出来别忘了往缓存里写一份
        List<Object> categoriesList = redisUtil.rangeList("categoriesList", 0, -1);
        if (categoriesList==null || categoriesList.size()==0){
            // 把分类数据带过去
            List<Category> list = categoryService.queryAll();
            redisUtil.lSet("categoriesList",list);
            redisUtil.expire("categoriesList",60*60*60);
            model.addAttribute("categories",list);
        }else {
            model.addAttribute("categories",categoriesList.get(0));
            System.out.println(categoriesList.get(0));
        }
        return JSONUtils.getJson(resultInfo);

    }

    //-------------------------------------------------------------------------------------

}

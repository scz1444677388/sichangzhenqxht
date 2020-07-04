package com.qhit.itravel.service.impl;

import com.qhit.itravel.dao.FavoriteDao;
import com.qhit.itravel.entity.Favorite;
import com.qhit.itravel.entity.Route;
import com.qhit.itravel.dao.RouteDao;
import com.qhit.itravel.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

/**
 * (Route)表服务实现类
 *
 * @author makejava
 * @since 2020-04-15 17:47:02
 */
@Service("routeService")
public class RouteServiceImpl implements RouteService {
    @Resource
    private RouteDao routeDao;
    @Autowired
    private FavoriteDao favoriteDao;

    /**
     * 通过ID查询单条数据
     *
     * @param rid 主键
     * @return 实例对象
     */
/*
    @Override
    public Route queryById(Integer rid) {
        return this.routeDao.queryById(rid);
    }
*/

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
  /*  @Override
    public List<Route> queryAllByLimit(int offset, int limit) {
        return this.routeDao.queryAllByLimit(offset, limit);
    }
*/
    /**
     * 新增数据
     *
     * @param route 实例对象
     * @return 实例对象
     */
/*    @Override
    public Route insert(Route route) {
        this.routeDao.insert(route);
        return route;
    }*/

    /**
     * 修改数据
     *
     * @param route 实例对象
     * @return 实例对象
     */
    @Override
    public Route update(Route route) {
        this.routeDao.update(route);
        //return this.queryById(route.getRid());
        return null;
    }

    /**
     * 通过主键删除数据
     *
     * @param rid 主键
     * @return 是否成功
     */
   /* @Override
    public boolean deleteById(Integer rid) {
        return this.routeDao.deleteById(rid) > 0;
    }*/


    @Override
    @Transactional//开启事务同时成功，或者同时失败
    public void saveFavorite(Long rid, Integer uid) {
        //操作路线表
        routeDao.updateFavorite(rid);
        //构建Favorite  entity实体类
        Favorite favorite = new Favorite();
        //通过intValue把long型转成整型
        favorite.setRid(rid.intValue());
        favorite.setUid(uid);
        favorite.setDate(new Date());//当前时间放进去

        favoriteDao.insert(favorite);
    }
}
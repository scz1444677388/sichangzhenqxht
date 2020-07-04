package com.qhit.itravel.dao;

import java.util.List;
import java.util.Map;

import com.qhit.itravel.entity.RouteImg;
import com.qhit.itravel.entity.Seller;
import org.apache.ibatis.annotations.*;

import com.qhit.itravel.entity.Route;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RouteDao {

    @Select("select * from route t where t.rid = #{id}")
    Route getById(Long id);

    @Delete("delete from route where rid = #{id}")
    int delete(Long id);

    int update(Route route);
    
    @Options(useGeneratedKeys = true, keyProperty = "rid")
    @Insert("insert into route(rid, rname, price, routeIntroduce, rflag, rdate, isThemeTour, count, cid, rimage, sid, sourceId) values(#{rid}, #{rname}, #{price}, #{routeIntroduce}, #{rflag}, #{rdate}, #{isThemeTour}, #{count}, #{cid}, #{rimage}, #{sid}, #{sourceId})")
    int save(Route route);

    int count(@Param("params") Map<String, Object> params);

    List<Route> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Select("select * from route t where t.cid = #{cid}")
    List<Route> getRouteList(Integer cid);
        //查询商家信息,详情那里
    @Select("select s.* from seller s where s.sid = #{sid}")
    Seller getSeller(Integer sid);

    //查询全部的旅游路线  导出表格用
    @Select("select * from route")
    List<Route> queryAll();

    //处理收藏，点击收藏次数加一
    @Update("update route set count = count + 1 where rid=#{rid}")
    void updateFavorite(Long rid);
    //
    @Select("select r.* from route_img r where r.rid = #{rid}")
    List<RouteImg> getRouteImgsByRid(Integer rid);
}

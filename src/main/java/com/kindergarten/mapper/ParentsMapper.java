package com.kindergarten.mapper;


import com.kindergarten.bean.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface ParentsMapper {

    public Parents login(String tel);
    public String FindRole(int roleid);
    public int updatePwd(@Param("tel") String tel,@Param("pwd") String password);

    //校园资讯
    public List<CampusInfo> CampusInfo(@Param("kinderId") int kinderId, @Param("curPage") int curPage, @Param("pageSize") int pageSize);
    public int CampusInfoCount(@Param("kinderId") int kinderId);

    //平台资讯
    public List<PlatformInfo> PlatformInfo(@Param("curPage") int curPage, @Param("pageSize") int pageSize);
    public int PlatformInfoCount();

    //登陆后选择哪个孩子的管理
    public List<Students> SerchStudent(int parentId);
    //查看体检信息
    public List<Examination> SearchBabyHeath(@Param("studentid")int studentId,@Param("curPage") int curPage, @Param("pageSize") int pageSize);
    public int SearchBabyHeathCount(int studentId);
    //查看校园监控信息
    public List<Monitor>SearchVideo(@Param("studentid")int studentid,@Param("curPage") int curPage, @Param("pageSize") int pageSize);
    public int SearchVideoCount(int studentId);

    //查膳食表
    public List<Meal> SearchMeal(@Param("studentid")int studentid, @Param("curPage") int curPage, @Param("pageSize") int pageSize);
    public int SearchMealCount(int studentid);

    //作业列表
    public List<Workrelease> StudentWork(@Param("studentid")int studentid,@Param("curPage") int curPage, @Param("pageSize") int pageSize);
    public int StudentWorkCount(@Param("studentid")int studentid);
    public List<HashMap<String,Object>>WorkResult(@Param("studentid")int studentid);

    //查英文名，用于班级分类文件
    public String SearchEnglishClassName(int studentid);
    //上传文件判断。是否首次上传？还是重新提交
    public int IsNewWork(@Param("workid")String workid,@Param("studentid") String studentid);
    //重新提交
    public int UpdateWork(@Param("url")String url, @Param("parentid")int parentid, @Param("parentname")String parentname, @Param("workid")String workid, @Param("studentid")String studentid);
    //首次提交
    public int UploadWork(@Param("url")String url, @Param("parentid")int parentid, @Param("parentname")String parentname, @Param("workid")String workid, @Param("studentid")String studentid,@Param("studentname")String studentname,@Param("cid")int cid);
    //查找孩子属于哪个班级的，用于作业上传需要
    public int SearchStudentClass(int studentId);
    //通过文件ID找作业文件路径
    public String SearchTeacherWork(int workid);
}

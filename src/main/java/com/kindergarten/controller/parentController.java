package com.kindergarten.controller;


import com.alibaba.fastjson.JSON;
import com.kindergarten.bean.*;
import com.kindergarten.mapper.ParentsMapper;
import com.kindergarten.service.ParentService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RequestMapping("/pt")
@Controller
public class parentController {
     @Autowired
     ParentService parentService;
     @Autowired
     ParentsMapper parentsMapper;


    @RequestMapping(value = "/login")
    @ResponseBody
    public String Login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tel = request.getParameter("tel");
        String password = request.getParameter("password");
        String sessionCode = (String) request.getSession().getAttribute("vCode");
        String paramCode=request.getParameter("code");
        System.out.println(paramCode);
        System.out.println(sessionCode);

        request.getSession().removeAttribute("vcode"); //错了就换新的验证码：

        if (paramCode.equalsIgnoreCase(sessionCode)){
        Parents parents=parentService.login(tel);
        if (parents!=null) {
            if (password.equals(parents.getParentsPwd())){

                if (parents.getParentsStatus()==2){
                    return "登陆失败,账户被封禁！";
                }else {
                    String roleName = parentsMapper.FindRole(parents.getRoleID());
                    parents.setRoleName(roleName);
                    request.getSession().setAttribute("parents",parents);

                    //查该家长的孩子list
                    List<Students> studentlist=parentService.studentsList(parents.getParentsId());
                    request.getSession().setAttribute("MyChild",studentlist);
                    return "success";
                }

            }else {

                return "账号或密码错误";
            }

        }else {
            return "无此账号";
        }
        }else {
            return "验证码错误";
        }
    }

    @RequestMapping(value = "/UpdatePwd")
    @ResponseBody
    public String UpdatePwd(String oldpwd,String newpwd,HttpServletRequest request) throws ServletException, IOException {

        Parents parents= (Parents) request.getSession().getAttribute("parents");
        if (oldpwd.equals(parents.getParentsPwd())){

            int a=parentsMapper.updatePwd(parents.getParentsTel(),newpwd);
            if (a>0){
                parents.setParentsPwd(newpwd);
                request.getSession().setAttribute("parents",parents);
                return "success";
            }else {
                return "error";
            }

        }else {

            return "error";
        }
    }

    //校园资讯
    @RequestMapping(value = "/SchoolInfo")
    @ResponseBody
    public String SchoolInfo(HttpServletRequest request) throws ServletException, IOException {
        int curPage;
        if(request.getParameter("curPage")!=null){
            curPage = Integer.parseInt(request.getParameter("curPage"));
        }else{
            curPage = 1;
        }
        int pageSize=Integer.parseInt(request.getParameter("pageSize"));
//        Students students= (Students) request.getSession().getAttribute("students");
        String studentID= (String) request.getSession().getAttribute("studentID");
//        int studentID= (int)

        LayuiData<CampusInfo> layuiData=parentService.CampusInfo(Integer.parseInt(studentID),curPage,pageSize);
        System.out.println(JSON.toJSONString(layuiData.getData()));
        return JSON.toJSONString(layuiData);
    }

    //平台资讯
    @RequestMapping(value = "/PlatformInfo")
    @ResponseBody
    public String PlatformInfo(HttpServletRequest request) throws ServletException, IOException {
        int curPage;
        if(request.getParameter("curPage")!=null){
            curPage = Integer.parseInt(request.getParameter("curPage"));
        }else{
            curPage = 1;
        }
        int pageSize=Integer.parseInt(request.getParameter("pageSize"));
        LayuiData<PlatformInfo> layuiData=parentService.PlatformInfo(curPage,pageSize);
        System.out.println(JSON.toJSONString(layuiData.getData()));
        return JSON.toJSONString(layuiData);
    }


    //下拉拥有的孩子列表
    @RequestMapping(value = "/MyChild")
    @ResponseBody
    public String MyChild(HttpServletRequest request) throws ServletException, IOException {

        System.out.println("进来了");
       List<Students> students= (List<Students>) request.getSession().getAttribute("MyChild");
        return JSON.toJSONString(students);
    }

    @RequestMapping(value = "/confirmKid")
    @ResponseBody
    public String confirmKid(String studentID,String studentName,HttpServletRequest request) throws ServletException, IOException {
        System.out.println("选中的孩子ID:"+studentID);
        System.out.println("选中的孩子姓名:"+studentName);
        request.getSession().setAttribute("studentID",studentID);
        request.getSession().setAttribute("studentName",studentName);


        return "OK";
    }

    @RequestMapping(value = "/babyhealth")
    @ResponseBody
    public String babyhealth(HttpServletRequest request) throws ServletException, IOException {
        int curPage;
        if(request.getParameter("curPage")!=null){
            curPage = Integer.parseInt(request.getParameter("curPage"));
        }else{
            curPage = 1;
        }
        int pageSize=Integer.parseInt(request.getParameter("pageSize"));

        String studentid= (String) request.getSession().getAttribute("studentID");
        LayuiData<Examination> examinations=parentService.examination(Integer.parseInt(studentid),curPage,pageSize);

        return JSON.toJSONString(examinations);
    }

    @RequestMapping(value = "/schoolVideo")
    @ResponseBody
    public String schoolVideo(HttpServletRequest request) throws ServletException, IOException {
        int curPage;
        if(request.getParameter("curPage")!=null){
            curPage = Integer.parseInt(request.getParameter("curPage"));
        }else{
            curPage = 1;
        }
        int pageSize=Integer.parseInt(request.getParameter("pageSize"));

        String studentid= (String) request.getSession().getAttribute("studentID");
        LayuiData<Monitor> examinations=parentService.monitors(Integer.parseInt(studentid),curPage,pageSize);

        return JSON.toJSONString(examinations);
    }

    @RequestMapping(value = "/searchmeal")
//    @ResponseBody
    public String searchmeal(HttpServletRequest request) throws ServletException, IOException {
        String studentid= (String) request.getSession().getAttribute("studentID");

        int curPage;
        if(request.getParameter("curPage")!=null){
            curPage = Integer.parseInt(request.getParameter("curPage"));
        }else{
            curPage = 1;
        }


        PageBean<Meal> pageBean=parentService.meals(Integer.parseInt(studentid),curPage,3);

//        return JSON.toJSONString(examinations);
        request.setAttribute("meals",pageBean);
        System.out.println(JSON.toJSONString(pageBean));
        return "/partent/meal.jsp";
    }

    @RequestMapping(value = "/homework")
    @ResponseBody
    public String homework(HttpServletRequest request) throws ServletException, IOException {
        int curPage;
        if(request.getParameter("curPage")!=null){
            curPage = Integer.parseInt(request.getParameter("curPage"));
        }else{
            curPage = 1;
        }
        int pageSize=Integer.parseInt(request.getParameter("pageSize"));

        String studentid= (String) request.getSession().getAttribute("studentID");
        LayuiData<Workrelease> workreleaseLayuiData=parentService.studentWork(Integer.parseInt(studentid),curPage,pageSize);

        return JSON.toJSONString(workreleaseLayuiData);
    }

    @RequestMapping(value = "/uploadHomeWork")
    @ResponseBody
    public Object uploadHomeWork(HttpServletRequest request, HttpServletResponse response, MultipartFile file, String fileName,String releaseid) {
        String studentid= (String) request.getSession().getAttribute("studentID");
        String studentName= (String) request.getSession().getAttribute("studentName");
        int cid=parentsMapper.SearchStudentClass(Integer.parseInt(studentid));

        String EnglishClassName= parentsMapper.SearchEnglishClassName(Integer.parseInt(studentid));
        Parents parents= (Parents) request.getSession().getAttribute("parents");

        System.out.println("fileName=" + file.getOriginalFilename());
        try {
            //获取文件名
            String originalName = file.getOriginalFilename();
            //扩展名
            String prefix = originalName.substring(originalName.lastIndexOf(".") + 1);
            Date date = new Date();
            //使用UUID+后缀名保存文件名，防止中文乱码问题,防止文件名重复
            String uuid = UUID.randomUUID() + "";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = simpleDateFormat.format(date);
            String savePath = request.getSession().getServletContext().getRealPath("/upload/");
            //要保存的问题件路径和名称
            String projectPath = savePath + EnglishClassName + File.separator + uuid + "." + prefix;

//            String projectPath = savePath + dateStr + File.separator + uuid + "." + prefix;

            System.out.println("projectPath==" + projectPath);
            File files = new File(projectPath);
            //打印查看上传路径
            if (!files.getParentFile().exists()) {//判断目录是否存在
                System.out.println("files11111=" + files.getPath());
                files.getParentFile().mkdirs();
            }
            //判断是否首次上传文件？
            int flag=parentsMapper.IsNewWork(releaseid,studentid);
            if (flag>0){
                //有数据，只需要更新文件路径+上传新的文件+上传时间
                SimpleDateFormat NewTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                file.transferTo(files); // 将接收的文件保存到指定文件中
                int IsSuccess=parentsMapper.UpdateWork(projectPath,parents.getParentsId(),parents.getParentsName(),releaseid,studentid);
                System.out.println("flag>0更新状态:"+IsSuccess);
            }else{
                //上传完成后，插入新数据
                file.transferTo(files); // 将接收的文件保存到指定文件中
                int newwork=parentsMapper.UploadWork(projectPath,parents.getParentsId(),parents.getParentsName(),releaseid,studentid,studentName,cid);
                System.out.println("newwork>0更新状态:"+newwork);
            }


            System.out.println(projectPath);
            LayuiData layuiData = new LayuiData();
            layuiData.setCode(0);
            layuiData.setMsg("上传成功");
            return JSON.toJSONString(layuiData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "/download")
    @ResponseBody
    public String download(HttpServletRequest request, HttpServletResponse response,int id) throws ServletException, IOException {
              System.out.println(id);
              String Url=parentsMapper.SearchTeacherWork(id);


            File file = new File(Url);
            String fileName=file.getName();
            // 浏览器以utf-8进行编码
            fileName = URLEncoder.encode(fileName, "utf-8");

            if (!file.exists()){

                return "文件不存在";
            }else {
//                   进行下载
                response.setContentType("text/html;charset=utf-8");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

                FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(reader);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(bytes)) > 0) {
                    response.getOutputStream().write(bytes, 0, len);
                }
                inputStream.close();
                return "success";

            }

        }


}

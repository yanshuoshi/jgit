package com.yss.jgit.module;

import com.yss.jgit.bean.GitBean;
import com.yss.jgit.util.GitUtil;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuoshi.Yan
 * @description:
 * @date 2020-12-31 10:42
 **/
@RestController
@RequestMapping("/git")
public class TestModule {

    @Value("${git.githome}")
    private String gitHome;
    @Value("${git.localhome}")
    private String localHome;
    @Value("${git.sshhome}")
    private String sshHome;
    @Value("${git.username}")
    private String username;
    @Value("${git.password}")
    private String password;

    /**
     * 创建git
     *
     * @param
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:53
     */
    @GetMapping("/create")
    public String CreateGit(@RequestParam("name")String name){
        GitUtil gitUtil = new GitUtil();
        //初始化GitBean
        GitBean gitBean = gitUtil.gitBeanBuilder(name,gitHome,localHome,sshHome,username,password);
        gitUtil.createGit(gitBean);
        return "奥里给";
    }

    /**
     * 修改git中内容并提交
     *
     * @param
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:53
     */
    @GetMapping("/update")
    public String UpdateGit(@RequestParam("name")String name){
        GitUtil gitUtil = new GitUtil();
        //初始化GitBean
        GitBean gitBean = gitUtil.gitBeanBuilder(name,gitHome,localHome,sshHome,username,password);
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("name","name1");
        map.put("number","1");
        list.add(map);
        map.put("name","name2");
        map.put("number","2");
        list.add(map);
        try {
            gitUtil.changGit(gitBean,list);
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "奥里给";
    }
}

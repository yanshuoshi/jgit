package com.yss.jgit.bean;


import lombok.Data;

@Data
public class GitBean {
    private String name;

    private String localhome;
    private String githome;
    private String sshhome;


    private String username;
    private String password;


    public String getLocalPath() {
        return localhome + "/" + name;

    }

    public String getGitPath() {
        return githome + "/" + name + ".git";
    }

    public String getSshPath() {
        return username + "@" + sshhome + ":" + System.getProperty("user.dir") + githome + "/" + name + ".git";
    }


}

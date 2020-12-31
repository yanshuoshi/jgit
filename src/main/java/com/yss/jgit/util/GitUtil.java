package com.yss.jgit.util;


import com.alibaba.fastjson.JSONObject;
import com.yss.jgit.bean.GitBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author:Shuoshi.Yan
 * @date: 2020/12/31 10:23
 */
@Slf4j
public class GitUtil {


    /**
     * 新建GitBean
     *
     * @param:
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:24
     */
    public void createGit(GitBean git) {
        try {
            git = createProject(git);
            git = createLocal(git);
            git = createLog(git);
            git = commit_push(git);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("create git error{} ;git_bean:{}", e, git);
        }
        log.info("git create over " + git.getSshPath());
    }

    /**
     * 新疆项目
     *
     * @param:
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:25
     */
    public GitBean createProject(GitBean git) throws GitAPIException, IOException {
        String githome = git.getGithome();
        String name = git.getName();
        log.debug("name:{};git_home:{};", name, githome);
        if (Strings.isBlank(githome)) {
            return git;
        }
        if (Strings.isBlank(name)) {
            return git;
        }
        File gitfile = new File(System.getProperty("user.dir") + git.getGitPath());
        if (!gitfile.exists()) {
            gitfile.mkdirs();
        }
        log.debug("git_path:{};git_file:{}", git.getGitPath(), gitfile);

        if (!gitfile.delete()) {
            throw new IOException("Could not delete temporary file " + git.getGitPath());
        }
        log.info("start:{},{}", "git build", gitfile);
        Git mygit = Git.init()
                .setBare(true)
                .setGitDir(gitfile)
                .setDirectory(gitfile)
                .call();
        log.info("end:", "git build");
        return git;
    }

    /**
     * 创建本地副本
     *
     * @param:
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:25
     */
    public GitBean createLocal(GitBean git) throws GitAPIException, IOException {

        File localfile = new File(System.getProperty("user.dir") + git.getLocalPath());
        if (!localfile.exists()) {
            localfile.mkdirs();
        }
        File gitfile = new File(System.getProperty("user.dir") + git.getGitPath());
        if (!gitfile.exists()) {
            gitfile.mkdirs();
        }
        log.info("local:{};git:{};", localfile, gitfile);
        FileUtils.deleteFiles(localfile);
        localfile.mkdir();
        // then clone
        log.info("start:Cloning from {} to {}", gitfile, localfile);
        Git result = Git.cloneRepository()
                .setURI(gitfile.getPath())
                .setDirectory(localfile)
                .setCredentialsProvider(allowHosts)
                .call();
        log.info("end:Cloning ");
        return git;
    }

    /**
     * 加入日志 创建ReadMe
     *
     * @param:
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:25
     */
    public GitBean createLog(GitBean git) throws IOException {
        String path = System.getProperty("user.dir") + git.getLocalPath();
        String target = path + "/ReadMe.md";
        File file = new File(target);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileUtils.append01(file, new Date().toString());
        FileUtils.append01(file, "create log");
        return git;
    }

    /**
     * 提交
     *
     * @param:
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:26
     */
    public GitBean commit_push(GitBean git) throws IOException, GitAPIException {
        File local = new File(System.getProperty("user.dir") + git.getLocalPath());
        if (!local.exists()) {
            local.mkdirs();
        }
        log.info("start:commit_push  from {} ", local);
        Git g = Git.open(local);
        g.add().addFilepattern(".").call();
        g.commit().setAll(true).setMessage("message").call();
        Iterator it = g.push().call().iterator();
        while (it.hasNext()) {
            log.info("commit:{}", it.next());
        }
        return git;
    }

    /**
     * 变更提交项目
     *
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:26
     */
    public void changGit(GitBean git, List<Map<String,Object>> devices) throws GitAPIException, IOException {
        git = createLocal(git);
        git = copyJson(git, devices);
        git = commit_push(git);
    }

    /**
     * copy 配置信息
     *
     * @param:
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:26
     */
    public GitBean copyJson(GitBean git, List<Map<String,Object>> devices) {

        String path = System.getProperty("user.dir") + git.getLocalPath();
        String target = path + "/config/";
        File config_file = new File(target);
        if (config_file.exists()) {
            FileUtils.deleteFiles(config_file);
            config_file.mkdir();
        } else {
            config_file.mkdir();
        }
        for (Map<String,Object> src : devices) {
            String name = "" + src.get("name");
            String config_name = target + "/" + name + ".json";
            File target_file = new File(config_name);
            try {
                target_file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("start:copy config json from {} to {}", src, target_file);
            FileUtils.append01(target_file, JSONObject.toJSONString(src));
        }
        return git;
    }


    // this is necessary when the remote host does not have a valid certificate, ideally we would install the certificate in the JVM
    // instead of this unsecure workaround!
    /* 自动点击同意  */
    /**
     * 为处理有些服务器git提交之后会出现选择Y/N确认信息
     *
     * @param
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:32
     */
    CredentialsProvider allowHosts = new CredentialsProvider() {
        @Override
        public boolean supports(CredentialItem... items) {
            for (CredentialItem item : items) {
                if ((item instanceof CredentialItem.YesNoType)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
            for (CredentialItem item : items) {
                if (item instanceof CredentialItem.YesNoType) {
                    ((CredentialItem.YesNoType) item).setValue(true);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isInteractive() {
            return false;
        }
    };

    /**
     * 新建GitBean
     *
     * @author:Shuoshi.Yan
     * @date: 2020/12/31 10:27
     */
    public GitBean gitBeanBuilder(String name,
                                  String gitHome,
                                  String localHome,
                                  String sshHome,
                                  String username,
                                  String password) {
        log.debug("gitHome:{};localHome:{};sshHome:{};username:{};password:{}", gitHome, localHome, sshHome, username, password);
        GitBean git = new GitBean();
        git.setGithome(gitHome);
        git.setLocalhome(localHome);
        git.setSshhome(sshHome);
        git.setUsername(username);
        git.setPassword(password);
        git.setName(name);
        return git;
    }
}

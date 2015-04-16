package com.winterfarmer.virgo.base.service;

import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yangtianhang on 15-4-15.
 */
public class DefaultHttpServiceLog implements HttpService.AccessLog {
    public void accessLog(long time, String method, int status, int len,
                          String url, String post, String ret) {
        if (post != null && post.length() > 200) {
            post = post.substring(0, 200);
            post.replaceAll("\n", "");
            post = post + "...";
        }
        if (ret != null) {
            ret = ret.trim();
            ret = ret.replaceAll("\n", "");
        }
        if (!StringUtils.isBlank(post) && url.startsWith("http://ilogin.sina.com.cn")) {
            post = replacePwd(post);
        }

        VirgoLogger.httpInfo("[HTTPCLI] {} {} {} {} {} {} {}", time, method, status, len, url,
                StringUtils.isEmpty(post) ? "-" : post, StringUtils.isEmpty(ret) ? "-" : ret);
    }

    static String replacePwd(String text) {
        text = text.replaceFirst("pw=[^&]*", "pw=***");
        return text;
    }
}

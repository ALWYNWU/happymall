package com.happymall.authserver.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Yilong
 * @date 2022-10-18 4:15 a.m.
 * @description
 */
@Data
public class SocialUserDetails {
    private String access_token;
    public String login;
    public String id;
    public String node_id;
    public String avatar_url;
    public String gravatar_id;
    public String url;
    public String html_url;
    public String followers_url;
    public String following_url;
    public String gists_url;
    public String starred_url;
    public String subscriptions_url;
    public String organizations_url;
    public String repos_url;
    public String events_url;
    public String received_events_url;
    public String type;
    public boolean site_admin;
    public String name;
    public String company;
    public String blog;
    public String location;
    public Object email;
    public Object hireable;
    public Object bio;
    public Object twitter_username;
    public int public_repos;
    public int public_gists;
    public int followers;
    public int following;
    public Date created_at;
    public Date updated_at;
}

package com.example.project.app;

public class ServerInterface {
    public static final String HTTP_POST = "POST";

    // 서버
    public static final String site = "https://ssoss8866.cafe24.com";
    public static final String domain = site + "/_android/core";

    public static final String domain_ssl = domain;
    public static final String exec_ssl = domain_ssl + "/CheckedEmail.php";

    public static final String name = domain_ssl + "/CheckedName.php";

    public static final String regist = domain_ssl + "/regist.php";
    public static final String emailLogin = domain_ssl + "/emailLogin.php";
    public static final String ProductItem = domain_ssl + "/ProductItem.php";
    public static final String SaveReview = domain_ssl + "/SaveReview.php";
    public static final String reviewlist = domain_ssl + "/reviewlist.php";
    public static final String postsave =  domain_ssl +"/postsave.php";
    public static final String rmReview =  domain_ssl +"/rmReview.php";

    public static final String likeView = domain_ssl + "/likeView.php";
    public static final String Like = domain_ssl + "/Like.php";
    public static final String DeleteLike = domain_ssl + "/DeleteLike.php";

    public static final String Delete= domain_ssl + "/Delete.php";
    public static final String dd= domain_ssl + "/dd.php";
}

package com.github.composemusic.route.login.pwdlogin

sealed class PwdLoginStatus(val msg: String) {
    object LoginEmpty : PwdLoginStatus("账号和密码不能为空!")
    object LoginSuccess : PwdLoginStatus("登录成功!")
    object LoginFailed : PwdLoginStatus("登录失败,账号或密码错误!")
    object ForgetPassword : PwdLoginStatus("忘记密码!")
    data class NetworkFailed(val message: String) : PwdLoginStatus(message)
}
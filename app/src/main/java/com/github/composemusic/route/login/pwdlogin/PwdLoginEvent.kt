package com.github.composemusic.route.login.pwdlogin

sealed class PwdLoginEvent {
    data class ChangeUserName(val username:String):PwdLoginEvent()
    data class ChangePassword(val password:String):PwdLoginEvent()
    data class ChangePwdStatus(val isShowPwd:Boolean):PwdLoginEvent()
    object PwdLogin : PwdLoginEvent()
    object ForgetPassword:PwdLoginEvent()
}
package me.ghui.v2er.util;

import android.app.Activity;
import android.content.Context;

import es.dmoral.prefs.Prefs;
import me.ghui.toolbox.android.Check;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.network.bean.UserInfo;

/**
 * Created by ghui on 30/04/2017.
 */

public class UserUtils {

    private static final String USER_INFO_KEY = Constants.PACKAGE_NAME + "user_info_key";
    private static final String IS_PRO_KEY = Constants.PACKAGE_NAME + "is_pro";


    public static UserInfo getUserInfo() {
        String json = Prefs.with(App.get()).read(USER_INFO_KEY);
        if (Check.notEmpty(json)) {
            return APIService.gson().fromJson(json, UserInfo.class);
        }
        return null;
    }

    public static String getUserName() {
        UserInfo userInfo = getUserInfo();
        if (userInfo == null) return "";
        else return userInfo.getUserName();
    }

    public static boolean isLogin() {
        return Check.notEmpty(Prefs.with(App.get()).read(USER_INFO_KEY));
    }

    public static void saveLogin(UserInfo userInfo) {
        if (userInfo == null || Check.isEmpty(userInfo.getUserName())) return;
        String json = APIService.gson().toJson(userInfo);
        Prefs.with(App.get()).write(USER_INFO_KEY, json);
    }

    public static void clearLogin() {
        //1. crear cookie
        APIService.cookieJar().clearCookie();
        //2. crear userInfo
        Prefs.with(App.get()).remove(USER_INFO_KEY);
    }

    public static boolean notLoginAndProcessToLogin(boolean finishCurrentPage, Context context) {
        if (!isLogin()) {
            Voast.show("登录后才能进行此操作");
            Navigator.from(context).to(LoginActivity.class).start();
            if (context instanceof Activity && finishCurrentPage) {
                ((Activity) context).finish();
            }
            return true;
        }
        return false;
    }

    public static void savePro(boolean isPro){
        // TODO: 2018/6/29 is it save
        Pref.save(IS_PRO_KEY, isPro);
    }

    public static boolean isPro(){
        return Pref.readBool(IS_PRO_KEY);
    }

}

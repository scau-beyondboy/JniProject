package beyondboy.scau.com.jniproject;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.proguard.ab;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2016-01-30
 * Time: 09:23
 */
public class MyApplication extends Application
{
    private static MyApplication sMyApplication;
    //多线程安全返回单例
    public static MyApplication getInstance()
    {
        if (sMyApplication == null)
        {
            synchronized (MyApplication.class)
            {
                if (sMyApplication == null)
                {
                    sMyApplication = new MyApplication();
                }
            }
        }
        return sMyApplication;
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        sMyApplication=this;
        CrashReport.initCrashReport(this, "900019200", false);
        LeakCanary.install(this);
        ab.a=true;
        CrashReport.isDebug=true;
    }
}

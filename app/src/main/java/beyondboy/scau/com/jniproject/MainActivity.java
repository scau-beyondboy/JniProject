package beyondboy.scau.com.jniproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();
    private EditText et_wav;
    private EditText et_mp3;
    private ProgressDialog pd;
    private String mp3name;
    private String wavname;
    private int size;

    public static native void initIDs();
    private AlertDialog.Builder infop;
    private Button dlete;
    private Button info;
    private int size1;
    private long size2;
    public native void convertmp3(String war, String mp3);
    public FutureTask<Void> work;
    public native void stop();
    public native void start();

    public native String getLameVersion();

    static
    {
        System.loadLibrary("lame");
        initIDs();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_wav = (EditText) this.findViewById(R.id.editText1);
        et_mp3 = (EditText) this.findViewById(R.id.editText2);
        dlete=(Button)this.findViewById(R.id.button3);
        info=(Button)this.findViewById(R.id.button4);
        pd = new ProgressDialog(this);
        //pd.setIndeterminate(true);
        infop=new AlertDialog.Builder(this);
        dlete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                File file=new File(et_mp3.getText().toString().trim());
                if(file!=null&&file.exists())
                {
                    file.delete();
                }
            }
        });
        info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String wavname = et_wav.getText().toString().trim();
                File file1 = new File(wavname);
                 size1 = (int) file1.length() / 1024;
                File file = new File(et_mp3.getText().toString().trim());
                if (file != null && file.exists())
                {
                    size2 = file.length() / 1024;
                } else
                {
                    size2 = 0;
                }
                infop.setMessage(String.format("wav大小： %dK\nmp3大小：%dk", size1, size2));
                infop.show();
            }
        });
        pd.setMessage("转换中....");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //停止线程
                stop();
                Log.i(TAG, "停掉线程");
                File file = new File(et_mp3.getText().toString().trim());
                if (file != null && file.exists())
                {
                    file.delete();
                }
                work = null;
            }
        });
        mp3name = et_mp3.getText().toString().trim();
         wavname = et_wav.getText().toString().trim();
        File file = new File(wavname);
         size = (int) file.length();
        pd.setMax(size); // 设置进度条的最大值
        System.out.println("文件大小 " + size);
        if ("".equals(mp3name) || "".equals(wavname))
        {
            Toast.makeText(this, "路径不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //convertmp3(wavname,mp3name);

    }


    public void convert(View view)
    {
       // pd.setProgress(0);
       // pd.setMax(size);
        pd.setProgress(0);
        Log.i(TAG, "current:" + pd.getProgress() + "   Max:" + pd.getMax());
        pd.show();
        pd.setProgress(0);
        pd.setMax(size);
        start();
        //pd.setCancelable(false);
        Log.i(TAG, "执行显示");
        new MyThread(wavname,mp3name,this).start();
    }
    //防止内存泄漏
    static class MyThread extends Thread
    {
        String wavname, mp3name;
        WeakReference<MainActivity> mMainActivityWeakReference;

        MyThread(String wavname, String mp3name, MainActivity mainActivity)
        {
            this.wavname = wavname;
            this.mp3name = mp3name;
            this.mMainActivityWeakReference = new WeakReference<MainActivity>(mainActivity);
        }
        @Override
        public void run()
        {
            MainActivity mainActivity = mMainActivityWeakReference.get();
            if (mainActivity != null)
            {
                mainActivity.convertmp3(wavname, mp3name);
               // mainActivity.pd.setProgress(0);
                //mainActivity.pd.hide();
            }
        }
    }

    public void setConvertProgress(final int progress)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                pd.setProgress(progress);
            }
        });
    }


    public void getversion(View view)
    {
        String version = getLameVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }
}

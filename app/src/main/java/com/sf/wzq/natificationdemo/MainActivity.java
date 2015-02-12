package com.sf.wzq.natificationdemo;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * 关于Notification的使用：
 * <ul>
 *     <li>普通样式</li>
 *     <li>进度条样式</li>
 *     <li>bigView样式</li>
 *     <li>自定义样式</li>
 * </ul>
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private NotificationCompat.Builder builder;
    private int NOTIFY_ID;
    private NotificationManager nm;
    private PendingIntent pendingIntent;
    private Intent notifySpecialIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化数据
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);
        notifySpecialIntent = new Intent(this, NotifySpecialActivity.class);
        // to match some attributes in the AndroidManifest.xml
        notifySpecialIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent = PendingIntent.getActivity(this, (int) SystemClock.uptimeMillis(), notifySpecialIntent, PendingIntent.FLAG_ONE_SHOT);
        /**
         * 以下是一些通用属性设置
         */
        builder.setAutoCancel(true);//true:点击后消失 TODO
        builder.setOngoing(false);//true:不能滑动删除
        builder.setPriority(NotificationCompat.PRIORITY_MAX);//优先级
        builder.setTicker("Status Bar 提示语");//Status Bar 上的提示语
//        builder.setSound(Uri.parse("file:///mnt/sdcard/cat.mp3"));//存储卡上的音乐
//        builder.setSound(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.cat));//工程中的音乐
//        builder.setDefaults(Notification.DEFAULT_ALL);//铃声、震动、闪光，为系统默认

        /**
         * 设置完毕
         */
        // 注册监听,点击不同的TextView，发送不同的Notification
        registerListener();
    }

    private void registerListener() {
        TextView tv_sendNotification = (TextView) findViewById(R.id.tv_sendNotification);
        tv_sendNotification.setOnClickListener(this);
        TextView tv_sendNotification_progress = (TextView) findViewById(R.id.tv_sendNotification_progress);
        tv_sendNotification_progress.setOnClickListener(this);
        TextView tv_sendNotification_bigView = (TextView) findViewById(R.id.tv_sendNotification_bigView);
        tv_sendNotification_bigView.setOnClickListener(this);
        TextView tv_sendNotification_customView = (TextView) findViewById(R.id.tv_sendNotification_customView);
        tv_sendNotification_customView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sendNotification:
                // 发出通知_普通样式
                NOTIFY_ID = 1;
                createNotification();
                break;
            case R.id.tv_sendNotification_progress:
                // 发出通知_进度条样式
                NOTIFY_ID = 2;
                createNotification_Progress();
                break;
            case R.id.tv_sendNotification_bigView:
                // 发出通知_bigView样式:2个手指下滑/上滑，分别展开/关闭bigView
                NOTIFY_ID = 3;
                createNotification_bigView();
                break;
            case R.id.tv_sendNotification_customView:
                // 发出通知_自定义样式
                NOTIFY_ID = 4;
                createNotification_customView();
                break;
        }
    }

    private void createNotification_customView() {
        RemoteViews rv = new RemoteViews(getPackageName(),R.layout.remote);
        rv.setTextViewText(R.id.tv_customView,"changed in method");
        rv.setOnClickPendingIntent(R.id.tv_customView,pendingIntent);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContent(rv);
        nm.notify(NOTIFY_ID,builder.build());
    }

    /**
     * Android4.1之后，谷歌引入了一种新的样式，叫做Big View,效果就是相对于传统的Notification,它的显示区域更大，显示的内容也更多一些。关于Big View，谷歌支持了三种模式，分别是：
     * Big picture style 和 Big text style 还有 Inbox style
     */
    private void createNotification_bigView() {
        builder.setContentTitle("bigView title")
                .setContentText("bigView content")
                .setSmallIcon(R.drawable.ic_launcher);
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.largeicon));
        builder.setStyle(style);
        builder.addAction(R.drawable.ic_launcher,"action",pendingIntent);
        nm.notify(NOTIFY_ID, builder.build());
    }

    /**
     * 创建一个进度条Notification
     */
    private void createNotification_Progress() {
        builder.setContentTitle("Download Title")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_launcher);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 15; i += 5) {
                    // 具体的进度条
//                    builder.setProgress(20, i, false);
                    // 连续模糊的进度条
                    builder.setProgress(0, 0, true);
                    nm.notify(NOTIFY_ID, builder.build());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 模拟下载结束
                builder.setProgress(0, 0, false);
                builder.addAction(R.drawable.ic_launcher,"action",pendingIntent);
                nm.notify(NOTIFY_ID, builder.build());
            }
        }).start();
    }

    /**
     * 创建一个普通的Notification
     */
    private void createNotification() {
        builder = new NotificationCompat.Builder(this);
        // 必须的
        builder.setContentTitle("my_content_title");
        builder.setContentText("my_content_text");
        builder.setSmallIcon(R.drawable.ic_launcher);
        // 可选的
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.largeicon));
        builder.setNumber(12);
        builder.setWhen(System.currentTimeMillis());
        // 手机最上端的Status Bar上面，闪现的一段提示语
        builder.setTicker("this is ticker");
        // 优先级
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        // 监听Notification的销毁
        registerDeleteListener();
        // 顺序返回 ：NotifyRegularActivity-->OtherActivity-->MainActivity
        //        registerOnClickListener();
        // 打开 NotifyRegularActivity only and return to your recent task
        registerOnClickListener2();

    }

    /**
     * 为Notification设置点击监听.
     */
    private void registerOnClickListener2() {
        builder.setContentIntent(pendingIntent);
        builder.addAction(R.drawable.ic_launcher,"action",pendingIntent);
        nm.notify(NOTIFY_ID, builder.build());
    }

    /**
     * 为Notification设置点击监听.
     * NotifyRegularActivity-->OtherActivity-->MainActivity
     * <ul>
     * <li>FLAG_CANCEL_CURRENT：如果构建的PendingIntent已经存在，则取消前一个，重新构建一个。</li>
     * <li>FLAG_NO_CREATE：如果前一个PendingIntent已经不存在了，将不再构建它。</li>
     * <li>FLAG_ONE_SHOT：表明这里构建的PendingIntent只能使用一次。</li>
     * <li>FLAG_UPDATE_CURRENT：如果构建的PendingIntent已经存在，那么系统将不会重复创建，只是把之前不同的传值替换掉。</li>
     * <ul/>
     */
    private void registerOnClickListener() {
        Intent notifyIntent = new Intent(this, NotifyRegularActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotifyRegularActivity.class);
        stackBuilder.addNextIntent(notifyIntent);
        PendingIntent pi = stackBuilder.getPendingIntent((int) SystemClock.uptimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
    }

    /**
     * 为Notification设置删除监听，此处开启了一个服务
     */
    private void registerDeleteListener() {
        Intent deleteIntent = new Intent(this, DeleteService.class);
        int deleteCode = (int) System.currentTimeMillis();
        PendingIntent deletePendingIntent = PendingIntent.getService(this, deleteCode, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setDeleteIntent(deletePendingIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

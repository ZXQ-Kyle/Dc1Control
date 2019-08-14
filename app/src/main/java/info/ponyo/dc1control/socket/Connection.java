package info.ponyo.dc1control.socket;


import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * channel的管理
 *
 * @author gxy
 */

public class Connection {
    private static Connection connection = new Connection();

    private Connection() {
        sendMessageScheduleThread = new ScheduledThreadPoolExecutor(1,
                new DefaultThreadFactory("sendMessageScheduleThread"));
        sendMessageScheduleThread.scheduleWithFixedDelay(new SendTask(), 0, DEFAULT_TIME, TimeUnit.MILLISECONDS);
    }

    public static Connection getInstance() {
        return connection;
    }

    public void setChannel(Channel channel) {
        close();
        this.channel = channel;
    }

    //周期消息发送间隔时间（ms）
    private final static int DEFAULT_TIME = 100;
    private Channel channel;

    // 消息队列
    private final LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private static ScheduledExecutorService sendMessageScheduleThread;

    public void appendMsgToQueue(String msg) {
        try {
            messageQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 发送心跳
    public void appendIdleMessage() {
        appendMsgToQueue("-");
    }

    // 定时任务发送消息给服务器端
    private class SendTask implements Runnable {
        @Override
        public void run() {
            if (isActive()) {
                try {
                    String message = messageQueue.take();
                    channel.writeAndFlush(message + "\n");
                    Log.i("send", "run(SendTask.java:68)" + message);
                } catch (InterruptedException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() {
        if (channel != null) {
            channel.close();
            channel = null;
        }
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }
}

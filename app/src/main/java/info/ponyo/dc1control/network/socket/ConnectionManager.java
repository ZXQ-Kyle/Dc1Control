package info.ponyo.dc1control.network.socket;


import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import info.ponyo.dc1control.bean.Dc1Bean;
import info.ponyo.dc1control.bean.PlanBean;
import info.ponyo.dc1control.util.Const;
import info.ponyo.dc1control.util.Event;
import info.ponyo.dc1control.util.SpManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;


public class ConnectionManager {
    private static ConnectionManager connectionManager = new ConnectionManager();

    private ExecutorService threadPool;
    private final Connection conn = Connection.getInstance();
    private Bootstrap b;

    public static ConnectionManager getInstance() {
        return connectionManager;
    }

    private ConnectionManager() {
        threadPool = Executors.newFixedThreadPool(5);
        EventLoopGroup group = new NioEventLoopGroup();
        b = new Bootstrap()
                .group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new NewChannelInitializer());
    }

    /**
     * 用于记录连接当前连接，只有当前没有连接的时候才能允许再连接
     */
    private AtomicInteger connectCount = new AtomicInteger(0);

    public void start() {
        threadPool.execute(() -> {
            Log.i("ConnectionManager", "start(ConnectionManager.java:71)" + connectCount.get());
            // 同步发起连接
            if (connectCount.get() == 0) {
                connectCount.incrementAndGet();
                Log.i("ConnectionManager", "start(ConnectionManager.java:75)");
                connect();
            }
        });
    }

    public void connect() {
        Log.i("ConnectionManager", "connect(ConnectionManager.java:84)" + conn.isActive());
        if (conn.isActive()) {
            return;
        }
        String host = SpManager.getString(Const.KEY_HOST, "192.168.1.1");
        int port = SpManager.getInt(Const.KEY_PORT, 8800);
        ChannelFuture channelFuture = b.connect(host, port);
        // 添加连接监听
        channelFuture.addListener((ChannelFuture future) -> {
            if (!future.isSuccess()) {
                connectCount.set(0);
                EventBus.getDefault().post(new Event().setCode(Event.CODE_CONNECT_ERROR));
            }
        });
    }

    /**
     * 重置连接，断开服务器并连接
     */
    public void reset() {
        Connection.getInstance().close();
        connectCount.set(0);
        start();
    }

    private class NewChannelInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
            ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024 * 1024, Delimiters.lineDelimiter()));
//            ch.pipeline().addLast(new LineBasedFrameDecoder(1024 * 1024, true, false));
            ch.pipeline().addLast(new IdleStateHandler(15, 15, 15));
            ch.pipeline().addLast("handler", new TcpClientHandler());
        }
    }

    private class TcpClientHandler extends SimpleChannelInboundHandler<String> {
        private final Gson gson = new Gson();

        // 读取服务端发来的消息
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            Log.i("TcpClientHandler", "channelRead0(TcpClientHandler.java:123)" + msg);
            dispatchMsg(conn, msg);
        }

        // 设备连接
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            if (ctx.channel() != null) {
                conn.setChannel(ctx.channel());
            }
            super.channelActive(ctx);
        }

        // 设备断开
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            //掉线的情况的下的重新连接
            connect();
            super.channelInactive(ctx);
        }

        // 心跳机制
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleState state = ((IdleStateEvent) evt).state();
                if (state == IdleState.WRITER_IDLE) {
                    conn.appendIdleMessage();
                }
            }
            super.userEventTriggered(ctx, evt);
        }

        // 捕获运行时的异常情况
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            ctx.close();
        }

        private void dispatchMsg(Connection conn, String msg) {
            threadPool.execute(() -> {
                if (TextUtils.isEmpty(msg) || msg.startsWith("-")) {
                    return;
                }
                String[] split = msg.split(" ", 2);
                if (split.length != 2) {
                    return;
                }
                switch (split[0]) {
                    case "queryDevice": {
                        try {
                            Type type = new TypeToken<ArrayList<Dc1Bean>>() {
                            }.getType();
                            ArrayList<Dc1Bean> dc1BeanArrayList = gson.fromJson(split[1], type);
                            EventBus.getDefault().post(new Event().setCode(Event.CODE_DEVICE_LIST).setData(dc1BeanArrayList));
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "queryPlan": {
                        try {
                            Type type = new TypeToken<ArrayList<PlanBean>>() {
                            }.getType();
                            ArrayList<PlanBean> planBeans = gson.fromJson(split[1], type);
                            EventBus.getDefault().post(new Event().setCode(Event.CODE_PLAN_LIST).setData(planBeans));
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "planChanged": {
                        String planId = split[1].replace("\n", "");
                        EventBus.getDefault().post(new Event().setCode(Event.CODE_PLAN_CHANGED).setData(planId));
                        break;
                    }
                    case "tip": {
//                        EventBus.getDefault().post(new Event().setCode(Event.CODE_MESSAGE).setData(split[1]));
                        break;
                    }
                    default: {
                        break;
                    }
                }
            });
        }
    }
}

package info.ponyo.dc1control.socket;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import info.ponyo.dc1control.bean.Dc1Bean;
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
import io.netty.handler.codec.LineBasedFrameDecoder;
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

    private OnReceiveData mListener;

    public ConnectionManager setListener(OnReceiveData listener) {
        this.mListener = listener;
        return this;
    }

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

    private void connect() {
        Log.i("ConnectionManager", "connect(ConnectionManager.java:82)" + conn.isActive());
        if (conn.isActive()) {
            return;
        }
        ChannelFuture channelFuture = b.connect("122.112.246.230", 40079);
//        ChannelFuture channelFuture = b.connect("192.168.50.50", 8800);
        // 添加连接监听
        channelFuture.addListener((ChannelFuture future) -> {
            if (!future.isSuccess()) {
                connectCount.set(0);
            }
            Log.i("ConnectionManager", "connect(ConnectionManager.java:92)" + future.isSuccess());
        });
    }

    private class NewChannelInitializer extends ChannelInitializer<Channel> {
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
            ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024 * 1024));
            ch.pipeline().addLast(new IdleStateHandler(15, 15, 15));
            ch.pipeline().addLast("handler", new TcpClientHandler());
        }
    }

    private class TcpClientHandler extends SimpleChannelInboundHandler<String> {
        private final Gson gson = new Gson();

        // 读取服务端发来的消息
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            dispatchMsg(conn, msg);
        }

        // 设备连接
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Log.i("TcpClientHandler", "channelActive(TcpClientHandler.java:109)");
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
                Type type = new TypeToken<ArrayList<Dc1Bean>>() {
                }.getType();
                Log.i("TcpClientHandler", "dispatchMsg(TcpClientHandler.java:157)" + msg);
                if (!msg.startsWith("[")) {
                    return;
                }
                ArrayList<Dc1Bean> dc1BeanArrayList = gson.fromJson(msg, type);
                if (mListener != null) {
                    Log.i("TcpClientHandler", "dispatchMsg(TcpClientHandler.java:148)" + dc1BeanArrayList);
                    mListener.onReceive(dc1BeanArrayList);
                }
            });
        }
    }
}

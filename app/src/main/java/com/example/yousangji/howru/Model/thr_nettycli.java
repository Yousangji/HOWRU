package com.example.yousangji.howru.Model;

import android.os.Handler;
import android.util.Log;

import com.example.yousangji.howru.Controller.hdr_nettycli;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class thr_nettycli extends Thread{

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "52.78.169.32");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    private final String host;
    private final int port;
    ChannelFuture f;
    Thread_send thread_sendmsg;
    private Channel mChannel;
    private obj_chatmsg msgobj;
    private Handler mhandler;

    public thr_nettycli(String host, int port, Handler handler){
        this.host="192.168.1.101";
        this.port=8007;
        this.mhandler=handler;


    }

    public void run() {

            try {

                final SslContext sslContext;

                if (SSL) {
                    sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                } else {
                    sslContext = null;
                }


                EventLoopGroup bossGroup = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(bossGroup)
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline p = ch.pipeline();
                                    if (sslContext != null) {
                                        p.addLast(sslContext.newHandler(ch.alloc(), HOST, PORT));
                                    }
                                    p.addLast(new LoggingHandler(LogLevel.INFO));
                                    p.addLast(new hdr_nettycli(mhandler));
                                    p.addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8));
                                   // p.addLast("ping",new IdleStateHandler(0,5,0));
                                   // p.addLast("heartbeat",new hdr_heartbeat());
                                }
                            });
                    // Start the Client
                    f = b.connect(HOST, PORT);
                    mChannel = f.sync().channel();
                    Log.d("mytag", "setsocket");

                    //Wait until the connection is closed
                    f.channel().closeFuture().sync();
            /*
            //java command
            String command;
            Scanner scan=new Scanner(System.in);

            JSONObject jsonmsg=new JSONObject();


            System.out.println("방번호 입력: ");
            command=scan.nextLine();
            jsonmsg.put("rmnum", command);

            System.out.println("닉네임 입력: ");
            command=scan.nextLine();
            jsonmsg.put("nickname", command);

            jsonmsg.put("state", "0");

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){

                if(jsonmsg.get("state").equals("0")){
                    jsonmsg.put("msg", "");
                }else{
                    String msg=in.readLine();
                    jsonmsg.put("msg",msg);
                }

                String sendMessage=jsonmsg.toString();

                System.out.println("로그 : sendmessage : "+ sendMessage);

                // String sendMessage=in.readLine() ;
                ByteBuf messageBuffer = Unpooled.buffer();
                messageBuffer.writeBytes(sendMessage.getBytes());
                f.channel().writeAndFlush(messageBuffer);

                jsonmsg.put("state", "3");
            }*/

                } catch (Exception e) {
                    Log.d("mytag", "[Exception] netty setting ");
                    e.printStackTrace();
                } finally {

                    //Shut down the event loop the terminal all threads.
                    bossGroup.shutdownGracefully();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    public void sendMsg(String data) {
/*
        thread_sendmsg = new Thread_send(f.channel(), data);
        thread_sendmsg.start();
*/
        if(mChannel.isOpen()) {
            Log.d("mytag",data);
            mChannel.writeAndFlush(data);
            //mChannel.read();
        }else{
            Log.d("mytag","notopen");
        }
    }

    public void closesocket(){
        f.channel().close();
    }





    /*********** inner thread classes **************/
    public class Thread_send extends Thread {
        private Channel channel;
        private String data;

        public Thread_send(Channel cf, String d) {
            this.channel = cf;
            this.data = d;
        }

        public void run() {
            try {
                // 데이터 전송.
                channel.write(data.getBytes());
            } catch (Exception e1) {

            }
        }
    }
}

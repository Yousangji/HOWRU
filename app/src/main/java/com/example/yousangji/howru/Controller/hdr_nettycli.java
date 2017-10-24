package com.example.yousangji.howru.Controller;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class hdr_nettycli extends ChannelInboundHandlerAdapter {

    //private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(echoclienthandler.class);
    //private final ByteBuf firstMessage;
    private Handler m_handler;

    public hdr_nettycli(Handler handler){
        this.m_handler=handler;
        /*
        firstMessage  = Unpooled.buffer(echoclient.SIZE);


        for(int i =0 ; i< firstMessage.capacity();i++){
            firstMessage.writeByte((byte)i);
        }*/
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // LOG.info("전송한 문자열 {}"+firstMessage.toString());
        //String sendMessage = "Hello, Netty!";


    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String readMessage = ((ByteBuf)msg).toString(CharsetUtil.UTF_8);
        Log.d("mytag",readMessage);

        JSONObject jsonObj = new JSONObject(readMessage);
        // 메시지 얻어오기
        Message handlermsg = m_handler.obtainMessage();
        // 메시지 ID 설정
        handlermsg.what = 1;
        // 메시지 정보 설정3 (Object 형식)
        handlermsg.obj = readMessage;
        m_handler.sendMessage(handlermsg);

        switch(jsonObj.getString("state")){
            //0. 접속멤버추가
            //1. 방종료
            //2. 방나가기
            //3. 메시지전송
            case "0":
                Log.d("channelread",jsonObj.get("nickname")+"님이 입장하셨습니다.");
                break;
            case "1":
                Log.d("channelread","스트리밍이 종료되었습니다 방을 나갑니다");
                ctx.close();
                break;

            case "2":
                Log.d("channelread",jsonObj.get("nickname")+"님이 퇴장하셨습니다.");
                break;

            case "3":
                Log.d("channelread",jsonObj.get("nickname") +": "+jsonObj.get("msg"));
                break;
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /*
    private void readmsg(SelectionKey key) throws Exception{

        // ByteBuffer를 생성한다.
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = 0;
        // 요청한 클라이언트의 소켓채널로부터 데이터를 읽어들인다.
        read = sc.read(buffer);
        buffer.flip();
        String data = new String();
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        data = decoder.decode(buffer).toString();
        // 메시지 얻어오기
        Message msg = m_handler.obtainMessage();
        // 메시지 ID 설정 msg.what = 1;
        // 메시지 정보 설정3 (Object 형식)
        msg.obj = data;
        m_handler.sendMessage(msg);
        // 버퍼 메모리를 해제한다.
        clearBuffer(buffer);

    }*/
}

package com.zepinos.chat.server.Netty.Server.Handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zepinos.chat.server.Service.LoginService;
import com.zepinos.chat.server.Service.MessageService;
import io.netty.channel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("jsonHandler")
@ChannelHandler.Sharable
public class JsonHandler extends SimpleChannelInboundHandler<String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MessageService messageService;
	@Autowired
	private LoginService loginService;
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {

		Map<String, Object> result = new HashMap<>();

		// 접속자 채널 정보(연결 정보)
		Channel channel = ctx.channel();

		// 전송된 내용을 JSON 개체로 변환
		Map<String, Object> data;
		try {

			data = objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {
			});

		} catch (JsonParseException | JsonMappingException e) {

			e.printStackTrace();

			messageService.returnMessage(channel, result, e, "1001");
			return;

		}

		messageService.execute(channel, data, result);

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		loginService.removeUser(ctx.channel());

		ctx.close();

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

}

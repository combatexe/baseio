package com.generallycloud.test.nio.base;

import java.io.File;

import com.generallycloud.nio.codec.base.future.BaseReadFuture;
import com.generallycloud.nio.common.CloseUtil;
import com.generallycloud.nio.common.FileUtil;
import com.generallycloud.nio.connector.SocketChannelConnector;
import com.generallycloud.nio.extend.FixedSession;
import com.generallycloud.nio.extend.SimpleIOEventHandle;
import com.generallycloud.test.nio.common.IoConnectorUtil;

public class TestSimpleBigParam {
	
	
	public static void main(String[] args) throws Exception {

		String serviceKey = "TestSimpleServlet";
		
		SimpleIOEventHandle eventHandle = new SimpleIOEventHandle();

		SocketChannelConnector connector = IoConnectorUtil.getTCPConnector(eventHandle);

		FixedSession session = new FixedSession(connector.connect());

		String temp = "网易科技腾讯科技阿里巴巴";
		StringBuilder builder = new StringBuilder(temp);
		for (int i = 0; i < 600000; i++) {
			builder.append("\n");
			builder.append(temp);
		}
		BaseReadFuture future = session.request(serviceKey, builder.toString());
		FileUtil.write(new File(TestSimpleBigParam.class.getName()), future.getReadText());
		System.out.println("处理完成");
		
		CloseUtil.close(connector);
		
		
		
	}
}

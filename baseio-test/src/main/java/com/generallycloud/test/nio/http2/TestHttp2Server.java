package com.generallycloud.test.nio.http2;

import java.io.File;

import com.generallycloud.nio.acceptor.SocketChannelAcceptor;
import com.generallycloud.nio.codec.http2.Http2ProtocolFactory;
import com.generallycloud.nio.codec.http2.Http2SessionFactory;
import com.generallycloud.nio.codec.http2.future.Http2FrameHeader;
import com.generallycloud.nio.common.SharedBundle;
import com.generallycloud.nio.common.ssl.SSLUtil;
import com.generallycloud.nio.common.ssl.SslContext;
import com.generallycloud.nio.component.SocketChannelContext;
import com.generallycloud.nio.component.SocketChannelContextImpl;
import com.generallycloud.nio.component.IoEventHandleAdaptor;
import com.generallycloud.nio.component.LoggerSocketSEListener;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.configuration.ServerConfiguration;
import com.generallycloud.nio.protocol.ReadFuture;

public class TestHttp2Server {

	public static void main(String[] args) throws Exception {
		
		SharedBundle.instance().loadAllProperties("http");

		IoEventHandleAdaptor eventHandleAdaptor = new IoEventHandleAdaptor() {

			public void accept(SocketSession session, ReadFuture future) throws Exception {
				Http2FrameHeader f = (Http2FrameHeader) future;
				System.out.println(f);
				String res = "yes server already accept your message:";
//				future.write(res);
				session.flush(future);
			}
		};
		
		SocketChannelContext context = new SocketChannelContextImpl(new ServerConfiguration(443));

		SocketChannelAcceptor acceptor = new SocketChannelAcceptor(context);

		context.addSessionEventListener(new LoggerSocketSEListener());

		context.setIoEventHandleAdaptor(eventHandleAdaptor);

		context.setProtocolFactory(new Http2ProtocolFactory());
		
		context.setSocketSessionFactory(new Http2SessionFactory());

		File certificate = SharedBundle.instance().loadFile("nio/conf/generallycloud.com.crt");
		File privateKey = SharedBundle.instance().loadFile("nio/conf/generallycloud.com.key");

		SslContext sslContext = SSLUtil.initServer(privateKey, certificate);

		context.setSslContext(sslContext);

		acceptor.bind();

	}

}

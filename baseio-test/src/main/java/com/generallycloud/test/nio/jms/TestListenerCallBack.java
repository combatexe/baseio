package com.generallycloud.test.nio.jms;

import com.generallycloud.nio.common.CloseUtil;
import com.generallycloud.nio.common.ThreadUtil;
import com.generallycloud.nio.connector.SocketChannelConnector;
import com.generallycloud.nio.extend.FixedSession;
import com.generallycloud.nio.extend.SimpleIOEventHandle;
import com.generallycloud.nio.extend.plugin.jms.Message;
import com.generallycloud.nio.extend.plugin.jms.client.MessageConsumer;
import com.generallycloud.nio.extend.plugin.jms.client.OnMessage;
import com.generallycloud.nio.extend.plugin.jms.client.impl.DefaultMessageConsumer;
import com.generallycloud.test.nio.common.IoConnectorUtil;

public class TestListenerCallBack {

	public static void main(String[] args) throws Exception {
		
		SimpleIOEventHandle eventHandle = new SimpleIOEventHandle();

		SocketChannelConnector connector = IoConnectorUtil.getTCPConnector(eventHandle);

		FixedSession session = new FixedSession(connector.connect());

		session.login("admin", "admin100");
		
		MessageConsumer consumer = new DefaultMessageConsumer(session);

		consumer.receive( new OnMessage() {
			
			public void onReceive(Message message) {
				System.out.println(message);
			}
		});

		
		ThreadUtil.sleep(1000);
		CloseUtil.close(connector);
		

	}

}

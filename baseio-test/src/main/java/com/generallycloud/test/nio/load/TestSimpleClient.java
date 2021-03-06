package com.generallycloud.test.nio.load;

import com.generallycloud.nio.codec.base.BaseProtocolFactory;
import com.generallycloud.nio.codec.base.future.BaseReadFuture;
import com.generallycloud.nio.common.CloseUtil;
import com.generallycloud.nio.common.SharedBundle;
import com.generallycloud.nio.common.ThreadUtil;
import com.generallycloud.nio.component.IoEventHandleAdaptor;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.connector.SocketChannelConnector;
import com.generallycloud.nio.protocol.ReadFuture;
import com.generallycloud.test.nio.common.IoConnectorUtil;
import com.generallycloud.test.nio.common.ReadFutureFactory;

public class TestSimpleClient {

	public static void main(String[] args) throws Exception {

		SharedBundle.instance().loadAllProperties("nio");

		IoEventHandleAdaptor eventHandleAdaptor = new IoEventHandleAdaptor() {

			public void accept(SocketSession session, ReadFuture future) throws Exception {
				System.out.println(future);
			}
		};

		SocketChannelConnector connector = IoConnectorUtil.getTCPConnector(eventHandleAdaptor);

		connector.getContext().setProtocolFactory(new BaseProtocolFactory());
		
		connector.connect();

		SocketSession session = connector.getSession();

		BaseReadFuture future = ReadFutureFactory.create(session, "test", session.getContext().getIoEventHandleAdaptor());

		future.write("hello server!");

		session.flush(future);
		
		ThreadUtil.sleep(500);

		CloseUtil.close(connector);

	}
}

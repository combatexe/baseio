package com.generallycloud.nio.extend.plugin.jms.server;

import com.generallycloud.nio.codec.base.future.BaseReadFuture;
import com.generallycloud.nio.common.ByteUtil;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.extend.plugin.jms.Message;

public class MQProducerServlet extends MQServlet {

	public static final String	SERVICE_NAME	= MQProducerServlet.class.getSimpleName();

	public void doAccept(SocketSession session, BaseReadFuture future, MQSessionAttachment attachment) throws Exception {

		MQContext context = getMQContext();

		Message message = context.parse(future);

		context.offerMessage(message);

		future.write(ByteUtil.TRUE);

		session.flush(future);

	}

}

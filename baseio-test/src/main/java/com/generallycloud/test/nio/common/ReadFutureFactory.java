package com.generallycloud.test.nio.common;

import com.generallycloud.nio.codec.base.future.BaseReadFuture;
import com.generallycloud.nio.codec.base.future.BaseReadFutureImpl;
import com.generallycloud.nio.codec.http11.future.ClientHttpReadFuture;
import com.generallycloud.nio.codec.http11.future.HttpReadFuture;
import com.generallycloud.nio.component.IoEventHandle;
import com.generallycloud.nio.component.SocketSession;

public class ReadFutureFactory {

	public static BaseReadFuture create(SocketSession session, BaseReadFuture future) {
		BaseReadFuture readFuture = (BaseReadFuture) future;
		return create(session, readFuture.getFutureID(), readFuture.getFutureName(), readFuture.getIOEventHandle());
	}

	public static BaseReadFuture create(SocketSession session, Integer futureID, String serviceName,
			IoEventHandle ioEventHandle) {

		BaseReadFutureImpl textReadFuture = new BaseReadFutureImpl(session.getContext(),futureID, serviceName);

		textReadFuture.setIOEventHandle(ioEventHandle);

		return textReadFuture;
	}

	public static BaseReadFuture create(SocketSession session, Integer futureID, String serviceName) {

		return create(session, futureID, serviceName, session.getContext().getIoEventHandleAdaptor());
	}

	public static BaseReadFuture create(SocketSession session, String serviceName, IoEventHandle ioEventHandle) {

		return create(session, 0, serviceName, ioEventHandle);
	}

	public static HttpReadFuture createHttpReadFuture(SocketSession session, String url) {
		return new ClientHttpReadFuture(session.getContext(),url, "GET");
	}

	public static BaseReadFuture create(SocketSession session, String serviceName) {

		return create(session, 0, serviceName, session.getContext().getIoEventHandleAdaptor());
	}
}

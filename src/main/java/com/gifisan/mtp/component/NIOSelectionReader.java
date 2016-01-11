package com.gifisan.mtp.component;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.gifisan.mtp.common.CloseUtil;
import com.gifisan.mtp.schedule.ServletAcceptJob;
import com.gifisan.mtp.server.EndPoint;
import com.gifisan.mtp.server.ServerEndPoint;
import com.gifisan.mtp.server.ServletContext;
import com.gifisan.mtp.server.selector.SelectionAccept;
import com.gifisan.mtp.server.session.InnerSession;
import com.gifisan.mtp.server.session.MTPSessionFactory;

public class NIOSelectionReader implements SelectionAccept {

	private ServletContext	context			= null;
	private ThreadPool		servletDispatcher	= null;
	private ServletService	service			= null;

	public NIOSelectionReader(ServletContext context, ServletService service, ThreadPool servletDispatcher) {
		this.context = context;
		this.service = service;
		this.servletDispatcher = servletDispatcher;
	}

	protected boolean isEndPoint(Object object) {
		if (object == null) {
			return false;
		}

		return object.getClass() == ServerNIOEndPoint.class || object instanceof EndPoint;

	}

	private ServerEndPoint getEndPoint(SelectionKey selectionKey) throws SocketException {

		Object attachment = selectionKey.attachment();

		if (isEndPoint(attachment)) {
			return (ServerEndPoint) attachment;
		}

		SocketChannel channel = (SocketChannel) selectionKey.channel();

		ServerEndPoint endPoint = new ServerNIOEndPoint(channel);

		selectionKey.attach(endPoint);

		return endPoint;

	}

	public void accept(SelectionKey selectionKey) throws IOException {

		ServerEndPoint endPoint = getEndPoint(selectionKey);

		if (endPoint.isEndConnect()) {
			return;
		}

		if (endPoint.inStream()) {
			synchronized (endPoint) {
				endPoint.notify();
				return;
			}
		}

		ServletContext context = this.context;

		boolean decoded = endPoint.protocolDecode(context);

		if (!decoded) {
			CloseUtil.close(endPoint);
			return;
		}

		ProtocolDecoder decoder = endPoint.getProtocolDecoder();

		if (decoder.isBeat()) {
			return;
		}

		String sessionID = decoder.getSessionID();

		MTPSessionFactory factory = context.getMTPSessionFactory();

		InnerSession session = factory.getSession(endPoint, sessionID,service);
		
//		Request request = session.getRequest(endPoint);
		
//		Response response = session.getResponse(endPoint);
		
		ServletAcceptJob job = session.updateServletAcceptJob(endPoint);
		
		servletDispatcher.dispatch(job);

	}

}

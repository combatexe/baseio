package com.generallycloud.nio.extend.service;

import java.io.IOException;

import com.generallycloud.nio.common.LifeCycleUtil;
import com.generallycloud.nio.common.Logger;
import com.generallycloud.nio.common.LoggerFactory;
import com.generallycloud.nio.common.StringUtil;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.extend.ApplicationContext;
import com.generallycloud.nio.extend.DynamicClassLoader;
import com.generallycloud.nio.extend.RESMessage;
import com.generallycloud.nio.extend.configuration.Configuration;
import com.generallycloud.nio.protocol.NamedReadFuture;
import com.generallycloud.nio.protocol.TextReadFuture;

public class FutureAcceptorServiceFilter extends FutureAcceptorFilter {

	private Logger						logger	= LoggerFactory.getLogger(FutureAcceptorServiceFilter.class);
	private DynamicClassLoader			classLoader;
	private FutureAcceptorServiceLoader	acceptorServiceLoader;

	public FutureAcceptorServiceFilter(DynamicClassLoader classLoader) {
		this.classLoader = classLoader;
		this.setSortIndex(Integer.MAX_VALUE);
	}

	protected void accept(SocketSession session, NamedReadFuture future) throws Exception {

		String serviceName = future.getFutureName();

		if (StringUtil.isNullOrBlank(serviceName)) {

			this.accept404(session, future, serviceName);

		} else {

			this.accept(serviceName, session, future);
		}
	}

	private void accept(String serviceName, SocketSession session, NamedReadFuture future) throws Exception {

		FutureAcceptorService acceptor = acceptorServiceLoader.getFutureAcceptor(serviceName);

		if (acceptor == null) {

			this.accept404(session, future, serviceName);

		} else {

			future.setIOEventHandle(acceptor);

			acceptor.accept(session, future);
		}
	}
	
	

	protected void accept404(SocketSession session, NamedReadFuture future, String serviceName) throws IOException {

		if (!(future instanceof TextReadFuture)) {
			return;
		}
		
		logger.info("[NIOServer] 未发现命令：" + serviceName);

		RESMessage message = new RESMessage(404, "service name not found :" + serviceName);

		flush(session, (TextReadFuture) future, message);
	}

	private void flush(SocketSession session, TextReadFuture future, RESMessage message) throws IOException {

		future.setIOEventHandle(this);

		future.write(message.toString());

		session.flush(future);
	}

	public void destroy(ApplicationContext context, Configuration config) throws Exception {
		LifeCycleUtil.stop(acceptorServiceLoader);

	}

	public void initialize(ApplicationContext context, Configuration config) throws Exception {

		this.acceptorServiceLoader = new FutureAcceptorServiceLoader(context, classLoader);

		LifeCycleUtil.start(acceptorServiceLoader);
	}

	public void prepare(ApplicationContext context, Configuration config) throws Exception {

		this.acceptorServiceLoader = new FutureAcceptorServiceLoader(context, classLoader);

		this.acceptorServiceLoader.prepare(context, config);

	}

	public void unload(ApplicationContext context, Configuration config) throws Exception {
		this.acceptorServiceLoader.unload(context, config);
	}

	public FutureAcceptorServiceLoader getFutureAcceptorServiceLoader() {
		return acceptorServiceLoader;
	}

}

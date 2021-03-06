package com.generallycloud.nio.extend.startup;

import java.io.IOException;
import java.util.Scanner;

import com.generallycloud.nio.codec.base.future.BaseReadFuture;
import com.generallycloud.nio.common.CloseUtil;
import com.generallycloud.nio.component.SocketChannelContext;
import com.generallycloud.nio.component.SocketChannelContextImpl;
import com.generallycloud.nio.component.LoggerSocketSEListener;
import com.generallycloud.nio.configuration.ServerConfiguration;
import com.generallycloud.nio.connector.SocketChannelConnector;
import com.generallycloud.nio.extend.FixedSession;
import com.generallycloud.nio.extend.SimpleIOEventHandle;
import com.generallycloud.nio.extend.implementation.SYSTEMStopServerServlet;

public class BaseServerShutdown {

	public static void main(String[] args) throws IOException {

		BaseServerShutdown shutdown = new BaseServerShutdown();
		
		shutdown.shutdown(args);
		
	}

	public void shutdown(String [] args) throws IOException {
		
		if (args == null || args.length < 3) {

			System.out.print("参数不正确，按回车键退出>>");

			Scanner scanner = new Scanner(System.in);

			scanner.nextLine();
			
			CloseUtil.close(scanner);

			return;
		}

		int port = Integer.valueOf(args[0]);

		String username = args[1];

		String password = args[2];
		
		String serviceName = SYSTEMStopServerServlet.SERVICE_NAME;

		SimpleIOEventHandle eventHandle = new SimpleIOEventHandle();
		
		SocketChannelContext context = new SocketChannelContextImpl(new ServerConfiguration(port));

		SocketChannelConnector connector = new SocketChannelConnector(context);

		context.setIoEventHandleAdaptor(eventHandle);

		context.addSessionEventListener(new LoggerSocketSEListener());

		FixedSession session = new FixedSession(connector.connect());
		
		session.login(username, password);
		
		BaseReadFuture future = session.request(serviceName, null);

		System.out.println(future.getReadText());

		CloseUtil.close(connector);

		System.out.print("按回车键退出>>");

		Scanner scanner = new Scanner(System.in);

		scanner.nextLine();
		
		CloseUtil.close(scanner);
	}

}

package com.generallycloud.nio.extend.example.baseio;

import com.generallycloud.nio.codec.base.future.BaseReadFuture;
import com.generallycloud.nio.codec.base.future.BaseReadFutureImpl;
import com.generallycloud.nio.common.StringUtil;
import com.generallycloud.nio.component.SocketSession;
import com.generallycloud.nio.extend.service.BaseFutureAcceptorService;

public class TestListenSimpleServlet extends BaseFutureAcceptorService{
	
	public static final String SERVICE_NAME = TestListenSimpleServlet.class.getSimpleName();
	
	protected void doAccept(SocketSession session, BaseReadFuture future) throws Exception {

		String test = future.getReadText();

		if (StringUtil.isNullOrBlank(test)) {
			test = "test";
		}
		
		future.write(test);
		future.write("$");
		session.flush(future);
		
		for (int i = 0; i < 5; i++) {
			
			BaseReadFuture f = new BaseReadFutureImpl(session.getContext(),future.getFutureID(),future.getFutureName());
			
			f.write(test);
			f.write("$");
			
			session.flush(f);
		}
		
	}

}

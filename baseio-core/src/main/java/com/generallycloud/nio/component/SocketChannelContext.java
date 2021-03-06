package com.generallycloud.nio.component;

import com.generallycloud.nio.Linkable;
import com.generallycloud.nio.common.ssl.SslContext;
import com.generallycloud.nio.component.SocketSessionManager.SocketSessionManagerEvent;
import com.generallycloud.nio.component.concurrent.EventLoopGroup;
import com.generallycloud.nio.protocol.ProtocolEncoder;
import com.generallycloud.nio.protocol.ProtocolFactory;

public interface SocketChannelContext extends ChannelContext {
	
	public abstract void setSessionManager(SocketSessionManager sessionManager) ;
	
	public abstract SocketSessionManager getSessionManager();

	public abstract IoEventHandleAdaptor getIoEventHandleAdaptor();

	public abstract EventLoopGroup getEventLoopGroup();

	public abstract int getSessionAttachmentSize();
	
	public abstract void setSessionAttachmentSize(int sessionAttachmentSize);

	public abstract BeatFutureFactory getBeatFutureFactory();

	public abstract void setBeatFutureFactory(BeatFutureFactory beatFutureFactory);

	public abstract void setIoEventHandleAdaptor(IoEventHandleAdaptor ioEventHandleAdaptor);

	public abstract void setProtocolFactory(ProtocolFactory protocolFactory);
	
	public abstract ProtocolFactory getProtocolFactory();
	
	public abstract ProtocolEncoder getProtocolEncoder();
	
	public abstract SslContext getSslContext() ;

	public abstract void setSslContext(SslContext sslContext) ;
	
	public abstract ChannelByteBufReader getChannelByteBufReader();

	public abstract boolean isEnableSSL() ;
	
	public abstract SocketSessionFactory getSessionFactory() ;

	public abstract void setSocketSessionFactory(SocketSessionFactory sessionFactory) ;
	
	public abstract ReadFutureAcceptor getReadFutureAcceptor();
	
	public abstract Linkable<SocketSessionEventListener> getSessionEventListenerLink();
	
	public abstract void addSessionEventListener(SocketSessionEventListener listener);
	
	public abstract void offerSessionMEvent(SocketSessionManagerEvent event);

}
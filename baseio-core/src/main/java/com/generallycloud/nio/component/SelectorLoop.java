package com.generallycloud.nio.component;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import com.generallycloud.nio.Looper;
import com.generallycloud.nio.buffer.ByteBufAllocator;
import com.generallycloud.nio.protocol.ProtocolDecoder;
import com.generallycloud.nio.protocol.ProtocolEncoder;
import com.generallycloud.nio.protocol.ProtocolFactory;

public interface SelectorLoop extends SelectionAcceptor, Looper {

	public abstract Selector buildSelector(SelectableChannel channel) throws IOException;

	public abstract Selector getSelector();

	public abstract Thread getMonitor();

	public abstract boolean isMainSelector();

	public abstract void setMonitor(Thread monitor);

	public abstract void accept(SelectionKey key);

	public abstract BaseContext getContext();

	public abstract void startup() throws IOException;

	public abstract SelectableChannel getSelectableChannel();

	public abstract ByteBufAllocator getByteBufAllocator();

	public abstract void wakeup();

	public abstract void fireEvent(SelectorLoopEvent event);

	public abstract boolean isWaitForRegist();

	public abstract void setWaitForRegist(boolean isWaitForRegist);

	public abstract byte[] getIsWaitForRegistLock();

	public abstract void setMainSelector(boolean isMainSelector);

	public interface SelectorLoopEvent extends Closeable {

		/**
		 * 返回该Event是否结束
		 */
		boolean handle(SelectorLoop selectLoop) throws IOException;
	}

	public abstract void rebuildSelector();

	public abstract boolean isShutdown();

	public abstract ProtocolDecoder getProtocolDecoder();

	public abstract ProtocolEncoder getProtocolEncoder();

	public abstract ProtocolFactory getProtocolFactory();

}

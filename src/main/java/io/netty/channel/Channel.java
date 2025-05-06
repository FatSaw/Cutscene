package io.netty.channel;

public interface Channel {
	ChannelPipeline pipeline();
	EventLoop eventLoop();
}

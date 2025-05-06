package io.netty.channel;

public interface ChannelPipeline {
	public ChannelPipeline addBefore(String s1, String s2, ChannelHandler cdh);
	public ChannelPipeline remove(String s);
}

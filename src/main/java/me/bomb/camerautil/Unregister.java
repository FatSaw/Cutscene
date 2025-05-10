package me.bomb.camerautil;

import io.netty.channel.ChannelPipeline;

final class Unregister implements Runnable {
	
	private final ChannelPipeline pipeline;
	
	protected Unregister(ChannelPipeline pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public void run() {
		this.pipeline.remove("cutscene");
	}

}

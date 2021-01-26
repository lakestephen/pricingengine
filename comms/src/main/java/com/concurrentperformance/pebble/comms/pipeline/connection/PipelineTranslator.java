package com.concurrentperformance.pebble.comms.pipeline.connection;

import java.io.IOException;



public interface PipelineTranslator<T> {

	T readNext(byte discriminator, PipelineReader reader) throws IOException;
}

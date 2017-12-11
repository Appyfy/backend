package rnd.data;

import rnd.process.Processor;
import rnd.process.ProcessorCallback;

public interface DataProcessor<Rq, Rs> extends Processor<Rq, Rs> {

	ProcessorCallback<?> getProcessorCallback(Rq requestPayload);

	Rs process(Rq requestPayload) throws Throwable;

}
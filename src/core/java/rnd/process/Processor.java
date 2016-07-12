package rnd.process;

public interface Processor<Rq, Rs> {

	ProcessorCallback<?> getProcessorCallback(Rq payload);

	Rs process(Rq payload) throws Throwable;

}
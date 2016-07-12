package rnd.process;

public interface ProcessorCallback<CTX extends ProcessorCallbackContext> {

	void processCallback(CTX context);

}
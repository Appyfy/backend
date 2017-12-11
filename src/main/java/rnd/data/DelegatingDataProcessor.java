package rnd.data;

public class DelegatingDataProcessor<Rq, Rs> extends AbstractDataProcessor<Rq, Rs> {

	@Override
	public Rs process(Rq requestPayload) throws Throwable {
		return (Rs) getDelegate().process(requestPayload);
	}

}
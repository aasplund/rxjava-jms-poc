package se.asplund;

import rx.Observable;

public interface AbstractAsyncController {

	Observable<ApiMessage> getObservable();

}

package com.eiben.asyncloader.loader;


import com.eiben.asyncloader.loader.base.IParam;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liumingrui on 16/9/24.
 */

public class DataEngine {
    DataSource dataSource = new DataSource();

    public Observable<IParam> load(final IParam... params) throws IllegalArgumentException {
        final Observable<IParam> observable = matchObservable(params);
        if (null != observable) {
            return Observable.defer(new Func0<Observable<IParam>>() {
                @Override
                public Observable<IParam> call() {
                    return observable;
                }
            })
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                        }
                    })
                    .doOnNext(new Action1<IParam>() {
                        @Override
                        public void call(IParam iParam) {
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            throw new IllegalArgumentException("参数超出支持范围");
        }
    }

    public Observable<IParam> matchObservable(IParam... param) {
        if (null == param || param.length == 0) {
            return null;
        }
        Observable<IParam> temp = null;
        if (param.length == 1) {
            temp = load(param[0]);
        } else if (param.length == 2) {
            temp = load(param[0], param[1]);
        } else if (param.length == 3) {
            temp = load(param[0], param[1], param[2]);
        }
        return temp;
    }

    private Observable<IParam> load(IParam data) {
        return getData(data);
    }

    private Observable<IParam> load(IParam data1, IParam data2) {
        return Observable.concat(getData(data1), getData(data2));
    }

    private Observable<IParam> load(IParam data1, IParam data2, IParam data3) {
        return Observable.concat(getData(data1), getData(data2), getData(data3));
    }

    private Observable<IParam> load(IParam data1, IParam data2, IParam data3, IParam data4) {
        return Observable.concat(getData(data1), getData(data2), getData(data3), getData(data4));
    }

    private Observable<IParam> getData(IParam data) {
        return Observable.concat(
                dataSource.fromCache(data),
                dataSource.fromNet(data))
                .first(new Func1<IParam, Boolean>() {
                    @Override
                    public Boolean call(IParam data) {
                        boolean flag = (data == null ? false : true);
                        return flag;
                    }
                });

    }
}

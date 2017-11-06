package io.tylerstone.prm;

import android.app.Application;
import android.content.Context;

import com.orm.SugarApp;

/**
 * Created by tyler on 5/13/2017.
 */

public class PRM extends SugarApp {
    private static PRM instance;

    public static PRM getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}

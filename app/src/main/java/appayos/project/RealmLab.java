package appayos.project;

import android.app.Application;

import io.realm.Realm;

public class RealmLab extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}

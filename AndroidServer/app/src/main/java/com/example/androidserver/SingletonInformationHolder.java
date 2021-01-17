package com.example.androidserver;

public class SingletonInformationHolder{

    // private field that refers to the object
    private static SingletonInformationHolder singleObject;
    private MainActivity mainActivity;

    private SingletonInformationHolder() {
    }

    public static SingletonInformationHolder getInstance() {
        // create object if it's not already created
        if(singleObject == null) {
            singleObject = new SingletonInformationHolder();
        }

        // returns the singleton object
        return singleObject;
    }

    public void setMainActivity(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    public MainActivity getMainActivity()
    {
        return mainActivity;
    }
}

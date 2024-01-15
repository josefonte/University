package com.example.demo.App.Observer;

import com.example.demo.App.JsonModels.MessageFormat;

public interface Subject {
    public void registerObserver(Observer o);

    public void removeObserver(Observer o);

    public void notifyObservers(Object o);
}

package org.rodinapihandler.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;


public class App implements IApplication {
  public Object start(IApplicationContext context) throws Exception {
    System.out.println("Hello world!");
    return IApplication.EXIT_OK;
  }
  public void stop() {
  }
}
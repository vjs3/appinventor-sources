package com.google.appinventor.buildserver.util;

import com.android.utils.ILogger;

public class BaseLogger implements ILogger {

  @Override
  public void error(Throwable t, String msgFormat, Object... args) {
    System.err.println("[ERROR] " + msgFormat);
  }

  @Override
  public void warning(String msgFormat, Object... args) {
    System.err.println("[WARN] " + msgFormat);
  }

  @Override
  public void info(String msgFormat, Object... args) {
    System.err.println("[INFO] " + msgFormat);
  }

  @Override
  public void verbose(String msgFormat, Object... args) {
    System.err.println("[DEBUG] " + msgFormat);
  }

}

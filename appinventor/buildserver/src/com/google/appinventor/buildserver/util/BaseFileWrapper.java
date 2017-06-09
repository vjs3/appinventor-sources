package com.google.appinventor.buildserver.util;

import java.io.InputStream;
import java.io.OutputStream;

import com.android.io.IAbstractFile;
import com.android.io.IAbstractFolder;
import com.android.io.StreamException;

public abstract class BaseFileWrapper implements IAbstractFile {

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getOsLocation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean exists() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public IAbstractFolder getParentFolder() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public InputStream getContents() throws StreamException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setContents(InputStream source) throws StreamException {
    // TODO Auto-generated method stub

  }

  @Override
  public OutputStream getOutputStream() throws StreamException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PreferredWriteMode getPreferredWriteMode() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long getModificationStamp() {
    // TODO Auto-generated method stub
    return 0;
  }

}

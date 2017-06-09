package com.google.appinventor.buildserver.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.IOUtils;

import com.android.io.StreamException;
import com.android.xml.AndroidManifest;

public class AARLibrary {
  private static final String MANIFEST = "AndroidManifest.xml";
  private static final String CLASSES = "classes.jar";
  private static final String R_TEXT = "R.txt";
  private static final String RES_DIR = "res/";
  private static final String ASSET_DIR = "assets/";
  private static final String LIBS_DIR = "libs/";
  private static final String JNI_DIR = "jni/";
  
  private final File aarPath;
  private final String name;
  
  private String packageName;
  private File basedir;
  private File resdir = null;
  // taken from https://sites.google.com/a/android.com/tools/tech-docs/new-build-system/aar-format
  private File manifest;
  private File classes;
  private File rtxt;
  private Set<File> resources = new HashSet<>();
  private Set<File> assets = new HashSet<>();
  private Set<File> libs = new HashSet<>();
  private Set<File> jni = new HashSet<>();
  
  private static class ZipEntryWrapper extends BaseFileWrapper {
    private final InputStream stream;
    
    ZipEntryWrapper(InputStream stream) {
      this.stream = stream;
    }

    @Override
    public InputStream getContents() throws StreamException {
      return this.stream;
    }
  }

  public AARLibrary(final File aar) {
    aarPath = aar;
    String temp = aar.getAbsolutePath();
    name = temp.substring(temp.lastIndexOf('/'), temp.length()-4);
  }
  
  public File getFile() {
    return aarPath;
  }
  
  public String getSimpleName() {
    return name;
  }
  
  public String getPackageName() {
    return packageName;
  }
  
  public File getDirectory() {
    return basedir;
  }
  
  public File getResDirectory() {
    return resdir;
  }
  
  public File getManifest() {
    return manifest;
  }
  
  public File getClassesJar() {
    return classes;
  }
  
  public File getRTxt() {
    return rtxt;
  }
  
  public Set<File> getResources() {
    return resources;
  }
  
  public Set<File> getAssets() {
    return assets;
  }
  
  public Set<File> getLibraries() {
    return libs;
  }
  
  public Set<File> getNatives() {
    return jni;
  }
  
  private String extractPackageName(ZipFile zip) throws IOException {
    ZipEntry entry = zip.getEntry("AndroidManifest.xml");
    if (entry == null) {
      throw new IllegalArgumentException(zip.getName() + " does not contain AndroidManifest.xml");
    }
    try {
      ZipEntryWrapper wrapper = new ZipEntryWrapper(zip.getInputStream(entry));
      // the following call will automatically close the input stream opened above
      return AndroidManifest.getPackage(wrapper);
    } catch(StreamException|XPathExpressionException e) {
      throw new IOException("Exception processing AndroidManifest.xml", e);
    }
  }
  
  private void catalog(File file) {
    if (MANIFEST.equals(file.getName())) {
      manifest = file;
    } else if (CLASSES.equals(file.getName())) {
      classes = file;
    } else if (R_TEXT.equals(file.getName())) {
      rtxt = file;
    } else if (file.getPath().startsWith(RES_DIR)) {
      resources.add(file);
    } else if (file.getPath().startsWith(ASSET_DIR)) {
      assets.add(file);
    } else if (file.getPath().startsWith(LIBS_DIR)) {
      libs.add(file);
    } else if (file.getPath().startsWith(JNI_DIR)) {
      jni.add(file);
    }
  }
  
  public void unpackToDirectory(final File path) throws IOException {
    ZipFile zip = null;
    InputStream input = null;
    OutputStream output = null;
    try {
      zip = new ZipFile(aarPath);
      packageName = extractPackageName(zip);
      basedir = new File(path, packageName);
      if (!basedir.mkdirs()) {
        throw new IOException("Unable to create directory for AAR package");
      }
      Enumeration<? extends ZipEntry> i = zip.entries();
      while (i.hasMoreElements()) {
        ZipEntry entry = i.nextElement();
        File target = new File(basedir, entry.getName());
        if (entry.isDirectory() && !target.exists() && !target.mkdirs()) {
          throw new IOException("Unable to create directory " + path.getAbsolutePath());
        } else if (!entry.isDirectory()) {
          output = new FileOutputStream(target);
          input = zip.getInputStream(entry);
          IOUtils.copy(input, output);
          catalog(target);
        }
      }
      resdir = new File(basedir, "res");
      if (!resdir.exists()) {
        resdir = null;
      }
    } finally {
      IOUtils.closeQuietly(output);
      IOUtils.closeQuietly(input);
      IOUtils.closeQuietly(zip);
    }
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o == null) {
      return false;
    } else if (getClass() != o.getClass()) {
      return false;
    } else {
      return getFile().equals(((AARLibrary) o).getFile());
    }
  }
  
  @Override
  public int hashCode() {
    return aarPath.hashCode();
  }
}

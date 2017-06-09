package com.google.appinventor.buildserver.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

import com.android.builder.internal.SymbolLoader;
import com.android.builder.internal.SymbolWriter;
import com.android.ide.common.internal.PngCruncher;
import com.android.ide.common.res2.MergedResourceWriter;
import com.android.ide.common.res2.MergingException;
import com.android.ide.common.res2.ResourceMerger;
import com.android.ide.common.res2.ResourceSet;
import com.android.utils.ILogger;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class AARLibraries extends HashSet<AARLibrary> {
  private static final long serialVersionUID = -5005733968228085856L;
  private static final ILogger LOG = new BaseLogger();
  private final String generated;
  private File outputDir;
  private Set<File> classes = new HashSet<>();
  private Set<File> resources = new HashSet<>();
  private Set<File> assets = new HashSet<>();
  private Set<File> libraries = new HashSet<>();
  private Set<File> natives = new HashSet<>();
  private Multimap<String, SymbolLoader> symbols = HashMultimap.create();

  public AARLibraries(final File generated) {
    this.generated = generated.getAbsolutePath();
  }

  @Override
  public boolean add(AARLibrary e) {
    if (super.add(e)) {
      final String packageName = e.getPackageName();
      classes.add(e.getClassesJar());
      resources.addAll(e.getResources());
      assets.addAll(e.getAssets());
      libraries.addAll(e.getLibraries());
      natives.addAll(e.getNatives());
      try {
        if (e.getRTxt() != null) {
          SymbolLoader loader = new SymbolLoader(e.getRTxt(), LOG);
          loader.load();
          symbols.put(packageName, loader);
        }
      } catch(IOException ex) {
        throw new IllegalArgumentException("IOException merging resources", ex);
      }
      return true;
    }
    return false;
  }
  
  @Override
  public boolean remove(Object o) {
    // we don't support removing AAR libraries during compilation
    throw new UnsupportedOperationException();
  }
  
  public Set<File> getClasses() {
    return classes;
  }
  
  public Set<File> getResources() {
    return resources;
  }
  
  public Set<File> getAssets() {
    return assets;
  }
  
  public Set<File> getLibraries() {
    return libraries;
  }
  
  public Set<File> getNatives() {
    return natives;
  }
  
  public File getOutputDirectory() {
    return outputDir;
  }
  
  private List<ResourceSet> getResourceSets() {
    List<ResourceSet> resourceSets = new ArrayList<>();
    for (AARLibrary library : this) {
      if (library.getResDirectory() != null) {
        ResourceSet resourceSet = new ResourceSet(library.getDirectory().getName());
        resourceSet.addSource(library.getResDirectory());
        resourceSets.add(resourceSet);
      }
    }
    return resourceSets;
  }
  
  public boolean mergeResources(File outputDir, File mainResDir, PngCruncher cruncher) {
    List<ResourceSet> resourceSets = getResourceSets();
    ResourceSet mainResSet = new ResourceSet("main");
    mainResSet.addSource(mainResDir);
    resourceSets.add(mainResSet);
    ResourceMerger merger = new ResourceMerger();
    
    try {
      for (ResourceSet resourceSet : resourceSets) {
        resourceSet.loadFromFiles(LOG);
        merger.addDataSet(resourceSet);
      }

      MergedResourceWriter writer = new MergedResourceWriter(outputDir, cruncher, false, false, null);
      writer.setInsertSourceMarkers(true);
      merger.mergeData(writer, false);
      return true;
    } catch(MergingException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public int writeRClasses(File outputDir, String appPackageName, File appRTxt) throws IOException, InterruptedException {
    this.outputDir = outputDir;
    SymbolLoader baseSymbolTable = new SymbolLoader(appRTxt, LOG);
    baseSymbolTable.load();
    
    // aggregate symbols into one writer per package
    Map<String, SymbolWriter> writers = new HashMap<>();
    for (String packageName : symbols.keys()) {
      Collection<SymbolLoader> loaders = symbols.get(packageName);
      SymbolWriter writer = new SymbolWriter(generated, packageName, baseSymbolTable);
      for (SymbolLoader loader : loaders) {
        writer.addSymbolsToWrite(loader);
      }
      writers.put(packageName, writer);
      writer.write();
    }
    
    // construct compiler command line
    List<String> args = new ArrayList<>();
//    args.add(appPackageName + ".R");
//    for (Map.Entry<String, SymbolWriter> writer : writers.entrySet()) {
//      writer.getValue().write();
//      args.add(writer.getKey() + ".R");
//    }
    args.add("-1.7");
    args.add("-d");
    args.add(outputDir.getAbsolutePath());
//    args.add("-sourcepath");
    args.add(generated);
    
    // compile R classes using ECJ batch compiler
    PrintWriter out = new PrintWriter(System.out);
    PrintWriter err = new PrintWriter(System.err);
    if (BatchCompiler.compile(args.toArray(new String[0]), out, err, new NOPCompilationProgress())) {
      return 0;
    } else {
      return 1;
    }
  }

}

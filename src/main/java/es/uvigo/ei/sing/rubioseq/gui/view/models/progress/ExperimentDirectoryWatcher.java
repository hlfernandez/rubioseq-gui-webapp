/*
	This file is part of RUbioSeq-GUI.

	RUbioSeq-GUI is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	RUbioSeq-GUI is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with RUbioSeq-GUI.  If not, see <http://www.gnu.org/licenses/>.
*/
package es.uvigo.ei.sing.rubioseq.gui.view.models.progress;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.ei.sing.rubioseq.Experiment;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;

/**
 * 
 * @author hlfernandez
 *
 */
public class ExperimentDirectoryWatcher {

	public static final String LOG_FILE_START = "log_";
	public static final String LOG_FILE_END = ".txt";
	
	private List<String> pathsToWatch = new LinkedList<String>();
	private boolean existPendingPathsToWatch = false;
	private List<String> pendingPathsToWatch;
	private WatchService myWatcher;
	private HashMap<String, LogFileWatcher> fileNameToLogFileWatcher = new HashMap<String, LogFileWatcher>();
	private ExperimentProgress experimentProgress;
	private Thread watcherThread;
	private OutputLogFileWatcher outputLogFileWatcher;

	public ExperimentDirectoryWatcher(List<String> pathsToWatch,
			ExperimentProgress experimentProgress,
			OutputLogFileWatcher outputLogFileWatcher) {
		this.pathsToWatch = pathsToWatch;
		this.pendingPathsToWatch = new LinkedList<String>();
		this.experimentProgress = experimentProgress;
		this.outputLogFileWatcher = outputLogFileWatcher;
		try {
			this.init();
		} catch (IOException e) {
		}
	}
	
	private void init() throws IOException {
		HashMap<WatchKey, Path> watchkeyToDir = new HashMap<WatchKey, Path>();
		for (String pathToWatch : pathsToWatch) {
			if(Utils.existDirectory(pathToWatch)){
				this.registerWatcher(pathToWatch, watchkeyToDir);
			} else{
				this.pendingPathsToWatch.add(pathToWatch);
				this.existPendingPathsToWatch = true;
			}
		}

		checkInitialLogs();

		MyWatchQueueReader fileWatcher = new MyWatchQueueReader(myWatcher,
				watchkeyToDir);
		// start the file watcher thread below
		watcherThread = new Thread(fileWatcher, "FileWatcher");
		watcherThread.start();

	}
	
	private void registerWatcher(String pathToWatch,
			HashMap<WatchKey, Path> watchkeyToDir) throws IOException {
		Path toWatch = Paths.get(pathToWatch);
		if (toWatch == null) {
			System.err.println("Could not watch path " + pathToWatch);
		} else if (myWatcher == null) {
			myWatcher = toWatch.getFileSystem().newWatchService();
		}

		WatchKey currentKey = toWatch.register(myWatcher, ENTRY_CREATE,
				ENTRY_MODIFY);
//		System.err.println(toWatch.toFile().getAbsolutePath());
		watchkeyToDir.put(currentKey, toWatch);
	}
	
    private void checkInitialLogs() {
    	for(String pathToWatch : pathsToWatch){
    		if(Utils.existDirectory(pathToWatch)){
	    		File dir = new File(pathToWatch);
	    		for(File f : dir.listFiles()){
	    			if(f.getName().startsWith(LOG_FILE_START) && f.getName().endsWith(LOG_FILE_END)){
	    	           	if(fileNameToLogFileWatcher.get(f.getAbsolutePath()) == null){
	                		createLogFileWatcher(f);
	                	} 
	                	try {
							fileNameToLogFileWatcher.get(f.getAbsolutePath()).parseFile();
	                	}catch(IOException e){
	                		
	                	}
	    			}
	    		}
    		}
    	}
		try {
			this.outputLogFileWatcher.parseFile();
		} catch (IOException e) {
		}
	}

    private void createLogFileWatcher(File f){
    	LogFileWatcher logFileWatcher = new LogFileWatcher(f, this.experimentProgress);
		fileNameToLogFileWatcher.put(f.getAbsolutePath(), logFileWatcher);
    }
        
	/**
     * This Runnable is used to constantly attempt to take from the watch
     * queue, and will receive all events that are registered with the
     * fileWatcher it is associated. In this sample for simplicity we
     * just output the kind of event and name of the file affected to
     * standard out.
     */
    private class MyWatchQueueReader implements Runnable {
  
        /** the watchService that is passed in from above */
        private WatchService myWatcher;
        private HashMap<WatchKey, Path> watchkeyToDir;
        
        public MyWatchQueueReader(WatchService myWatcher, HashMap<WatchKey, Path> watchkeyToDir) {
            this.myWatcher = myWatcher;
            this.watchkeyToDir = watchkeyToDir;
        }
  
        /**
         * In order to implement a file watcher, we loop forever
         * ensuring requesting to take the next item from the file
         * watchers queue.
         */
        @Override
        public void run() {
            try {
                // get the first event before looping
                WatchKey key = myWatcher.take();
                while((key != null && !Thread.currentThread().isInterrupted())) {
                	if(existPendingPathsToWatch){
      	            	 /**
      	                 * Check if the pending directories exist
      	                 */
                		LinkedList<String> pendingPathsToRemove = new LinkedList<String>();
   	   	                for(String pendingPathToWatch : pendingPathsToWatch){
   	   	                	if(Utils.existDirectory(pendingPathToWatch)){
//   	   	                		System.err.println("pendingPathToWatch = " + pendingPathToWatch + "Exists! Proceed to register it.");
   	   	                		try {
   	   								registerWatcher(pendingPathToWatch, watchkeyToDir);
   	   								for(File fileName : new File(pendingPathToWatch).listFiles()){
   	   									if(fileName.getName().startsWith(LOG_FILE_START) && fileName.getName().endsWith(LOG_FILE_END)){
   	   		                        		createLogFileWatcher(fileName);
   	   	   				                	try {
   	   	   										fileNameToLogFileWatcher.get(fileName.getAbsolutePath()).parseFile();
   	   	   				                	}catch(IOException e){
   	   	   				                	}
   	   		                        	} 
   	
   	   								}
   	   							} catch (IOException e) {
   	   							}
   	   	                		pendingPathsToRemove.add(pendingPathToWatch);
//   	   	                		pendingPathsToWatch.remove(pendingPathToWatch); --> Cause ConcurrentModificationException
   	   	                	}
   	   	                }
   	   	                pendingPathsToWatch.removeAll(pendingPathsToRemove);
   	   	                
   	   	                if(pendingPathsToWatch.size() == 0){
   	   	                	existPendingPathsToWatch = false;
   	   	                }
                   	}
                    // we have a polled event, now we traverse it and
                    // receive all the states from it
                    for (@SuppressWarnings("rawtypes") WatchEvent event : key.pollEvents()) {
                    	Path file = (Path) event.context(); 
                    	File eventFile = new File(this.watchkeyToDir.get(key).toFile().getAbsolutePath(), file.getFileName().toString());
//                        System.err.println("Received " + event.kind() + " event for file: "+ eventFile.getAbsolutePath());
                        String absoluteFileName = eventFile.getAbsolutePath();
                        String fileName = eventFile.getName();
                        if(event.kind().equals(ENTRY_CREATE)){
                        	if(fileName.startsWith(LOG_FILE_START) && fileName.endsWith(LOG_FILE_END)){
                        		createLogFileWatcher(eventFile);
                        	} 
                        } else if(event.kind().equals(ENTRY_MODIFY)){
                        	if(fileName.startsWith(LOG_FILE_START) && fileName.endsWith(LOG_FILE_END)){
	                        	if(fileNameToLogFileWatcher.get(absoluteFileName) == null){
	                        		createLogFileWatcher(eventFile);
	                        	} 
	                        	try {
									fileNameToLogFileWatcher.get(absoluteFileName).parseFile();
								} catch (IOException e) {
								}
                        	} else if (fileName.endsWith(Experiment.LOG_FILE)){
	                        		try {
	                        			outputLogFileWatcher.parseFile();
	                        		} catch (IOException e) {
	                        		}
	                        	} else if (fileName.endsWith(Experiment.LOG_FILE+".PPTW")){
	                        		try {
	                        			eventFile.delete();
	                        		} catch (Exception e) {
	                        		}
	                        	}
                        }
                    }
                    
                    key.reset();
                    key = myWatcher.take();
                }
            } catch (InterruptedException e) {
            	Thread.currentThread().interrupt();
            }
        }
        
    }
    
    public Thread getWatcherThread() {
		return watcherThread;
	}

	public void addPathsToWatch(List<String> paths) {
		this.pendingPathsToWatch.addAll(paths);
		this.existPendingPathsToWatch = true;
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.outputLogFileWatcher.getLogFile()+".PPTW", true)));
		    out.println("There are new paths to watch\n");
		    out.close();
		} catch (IOException e) {
		}
	}
    
//    public static void main(String[] args) {
//		List<String> pathsToWatch = new LinkedList<String>();
//		pathsToWatch.add("/home/hlfernandez/Tmp/");
//		pathsToWatch.add("/home/hlfernandez/Tmp/IMPRIMIR");
//		
//		ExperimentDirectoryWatcher edw = new ExperimentDirectoryWatcher(pathsToWatch);
//	}
}


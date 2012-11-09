/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.nio.file.*;
import java.util.*;
import java.io.*;
import static java.nio.file.StandardWatchEventKinds.*;


public class DirectoryWatcher {
    private	Map<WatchKey, Path> keys;
    private WatchService watcher;
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    	return (WatchEvent<T>) event;
    }
	
	public DirectoryWatcher() throws IOException{
		keys = new HashMap<WatchKey, Path>();
		watcher = FileSystems.getDefault().newWatchService();
	}
	
	public void Register(Path aPath) throws IOException {
		WatchKey key = aPath.register(watcher, ENTRY_CREATE);
		keys.put(key, aPath);
	}
	
	public void processEvents() {
		while (true) {
			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				continue;
			}
			
			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!");
				continue;
			}
			
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();
				
				// event can be OVERFLOW
				if (kind == OVERFLOW) {
					continue;
				}
				
				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);
				
				// print out event
				System.out.format("Processing %s: %s\n", child, event.kind().name());
				
				// Create a new thread processing this page
				(new Thread(new HTMLParser(child))).start();
				
				key.reset();
				
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		// Get the root directory
		Path root = Paths.get("Topics");
		DirectoryStream<Path> stream = Files.newDirectoryStream(root);
		
		DirectoryWatcher watcher = new DirectoryWatcher();
		
		for (Path child : stream) {
			watcher.Register(child);
		}
		watcher.processEvents();
	}
}

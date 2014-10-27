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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import es.uvigo.ei.sing.rubioseq.Experiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.ViewExperimentModel;

/**
 * 
 * @author hlfernandez
 *
 */
public class OutputLogFileWatcher {
	
	private File logFile;
	private long lastLengh = 0;
	private ViewExperimentModel viewExperimentModel;

	public OutputLogFileWatcher(String workingDirectory, ViewExperimentModel viewExperimentModel){
		this.logFile = new File(workingDirectory + "/" + Experiment.LOG_FILE);
		this.viewExperimentModel = viewExperimentModel;
	}
	
	public synchronized void parseFile() throws IOException{
		@SuppressWarnings("resource")
		RandomAccessFile raf = new RandomAccessFile(this.logFile, "r");
		StringBuilder fileContentSB = new StringBuilder(
				this.viewExperimentModel.getExperimentLog());
		if (this.lastLengh < this.logFile.length()) {
			raf.seek(this.lastLengh);
			String line;
			while ((line = raf.readLine()) != null) {
				fileContentSB.append(line).append("\n");
				if(line.startsWith(RUbioSeqEvent.RUBIOSEQ_EVENT)){
					try {
//						System.err.println("\t" + line);
						RUbioSeqEvent rsEvent = new RUbioSeqEvent(line, this.logFile.getName());
						this.viewExperimentModel.getExperimentProgress().updateProgress(rsEvent);
					} catch (Exception e) {
						System.err.println("Error parsing event:\n\t" + line
								+ "\n from file: " + this.logFile.getName());
						e.printStackTrace();
					}
				}
			}
			this.lastLengh = this.logFile.length();
			this.viewExperimentModel.setExperimentLog(fileContentSB.toString());
		}
	}
	
	public File getLogFile() {
		return logFile;
	}
}

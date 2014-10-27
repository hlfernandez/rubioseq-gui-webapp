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
package es.uvigo.ei.sing.rubioseq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Represents a RUbioSeq command and allows to execute it in the machine by
 * properly redirecting the standard and error outputs to the log file 
 * specified in the class Experiment.
 * @author hlfernandez
 * @see Experiment
 */
public class RubioSeqCommand {
	
	private String rsCommand;
	private String workingDirectory;
	private Process process;
	
	
	/**
	 * @param command A valid RUbioSeq command in the format:  
	 * 	pathToRUbioSeq/RUbioSeq.pl --analysis <analysis> --config <ExperimentConfigFile>
	 * @param workingDirectory The working directory specified in <ExperimentConfigFile> 
	 */
	public RubioSeqCommand(String command, String workingDirectory){
		this.rsCommand = command;
		this.workingDirectory = workingDirectory;
	}
	
	public void execCommand() {
		File workingDirectoryFile = new File(workingDirectory);
		workingDirectoryFile.mkdirs();
		File tmpCommandFile = new File(workingDirectory, "run-rubioseq.sh");
		tmpCommandFile.setExecutable(true);
		
		FileWriter fw;
		try {
			fw = new FileWriter(tmpCommandFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw
			.append("#!/bin/bash")
			.append("\n")
			.append(this.rsCommand)
			.append(" > ")
			.append(this.workingDirectory + "/" + Experiment.LOG_FILE)
			.append(" 2>&1 ")
			.append("\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String command = "sh " + tmpCommandFile.getPath();
		System.err.println("\n\nRun command:\n\t"+command+"\n\n");
		
		try {
			this.process = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Process getProcess(){
		return this.process;
	}
	
	public String getCommand() {
		return rsCommand;
	}
}

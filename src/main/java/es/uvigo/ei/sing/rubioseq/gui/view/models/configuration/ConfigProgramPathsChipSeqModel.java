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
package es.uvigo.ei.sing.rubioseq.gui.view.models.configuration;

import java.io.File;
import java.io.IOException;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.gui.view.models.ConfigurationViewModel;

/**
 * 
 * @author hlfernandez
 *
 */
public class ConfigProgramPathsChipSeqModel {
	
	private ConfigProgramPathsChipSeq config = new ConfigProgramPathsChipSeq();

	private File configProgramPathsFile = null;
	
	@Init
	public void init() {
		File cppFile = (File) Sessions.getCurrent().getAttribute(
				ConfigurationViewModel.CPP_CHIPSEQ);
		if (cppFile != null) {
			this.configProgramPathsFile = cppFile;
			this.config.loadDataFromFile(cppFile);
		} else {
			Executions.sendRedirect("/administrator/configuration.zul");
		}
		Sessions.getCurrent().removeAttribute(
				ConfigurationViewModel.CPP_CHIPSEQ);
	}

	@Command
	public void saveConfigProgramPaths() {
		try {
			this.config.exportToXML(this.configProgramPathsFile);
			Messagebox.show("Configuration saved", "OK", Messagebox.OK,
					Messagebox.INFORMATION);
		} catch (IOException e) {
			Messagebox.show("Error saving configuration", "Error",
					Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	public ConfigProgramPathsChipSeq getConfiguration() {
		return config;
	}
	
	public void setConfiguration(ConfigProgramPathsChipSeq cfg) {
		this.config = cfg;
	}

	@DependsOn({ "configuration.bwaPath", "configuration.samtoolsPath",
			"configuration.picardPath", "configuration.macsPath",
			"configuration.pythonPath", "configuration.bedtoolsPath",
			"configuration.ccatPath", "configuration.fastqcPath",
			"configuration.bedgraphtobigwigPath", "configuration.idrPath",
			"configuration.peakannotatorPath", "configuration.queueSystem" })
	public boolean isEnabledSaveButton() {
		if (config.getBwaPath() == null
				|| config.getSamtoolsPath() == null
				|| config.getPicardPath() == null
				|| config.getMacsPath() == null
				|| config.getPythonPath() == null
				|| config.getBedtoolsPath() == null
				|| config.getCcatPath() == null
				|| config.getBedgraphtobigwigPath() == null
				|| config.getIdrPath() == null
				|| config.getFastqcPath() == null
				|| config.getPeakannotatorPath() == null
				|| config.getQueueSystem() == null) {
			return false;
		}
		return true;
	}
	
	@NotifyChange("configuration")
	@Command
	public void restoreNthr(){
		config.setNthr(config.getNthr_DV());
	}
	
	@NotifyChange("configuration")
	@Command
	public void restoreJavaRam(){
		config.setJavaRam(config.getJavaRam_DV());
	}
	
	@NotifyChange("configuration")
	@Command
	public void restoreQueueName(){
		config.setQueueName(config.getQueueName_DV());
	}
	
	@NotifyChange("configuration")
	@Command
	public void restoreMulticoreName(){
		config.setMulticoreName(config.getMulticoreName_DV());
	}
	
	@NotifyChange("configuration")
	@Command
	public void restoreMulticoreNumber(){
		config.setMulticoreNumber(config.getMulticoreNumber_DV());
	}
	
	@DependsOn("enabledSaveButton")
	public String getExportButtonClass(){
		if(isEnabledSaveButton()){
			return "actionButton";
		}else{
			return "";
		}
	}
}

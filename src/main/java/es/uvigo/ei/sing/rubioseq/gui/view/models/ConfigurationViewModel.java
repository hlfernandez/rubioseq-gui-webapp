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
package es.uvigo.ei.sing.rubioseq.gui.view.models;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.RUbioSeqConfiguration;
import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.util.DBUtils;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;

/**
 * 
 * @author hlfernandez
 *
 */
public class ConfigurationViewModel {
	
	public static final String RUBIOSEQ_SCRIPT = "RUbioSeq.pl";
	
	public static final String CPP_SNV = "configProgramPaths.xml-snv";
	public static final String CPP_CNV = "configProgramPaths.xml-cnv";
	public static final String CPP_METHYLATION = "configProgramPaths.xml-met";
	public static final String CPP_CHIPSEQ = "configProgramPaths.xml-cs";
	
	public static final String CONFIG_FILE_SNV = "variantCalling/config/snv/configProgramPaths.xml";
	public static final String CONFIG_FILE_CNV = "variantCalling/config/cnv/configProgramPaths.xml";
	public static final String CONFIG_FILE_METHYLATION = "MethylCSeq/MethylBis/config/configProgramPaths.xml";
	public static final String CONFIG_FILE_CHIPSEQ = "ChIPseq/config/configProgramPaths.xml";
	
	private Integer visibleConfigProgramPaths = null;
	private File configProgramPathsFileSNV;
	private File configProgramPathsFileCNV;
	private File configProgramPathsFileMethylation;
	private File configProgramPathsFileChipSeq;
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	public RUbioSeqConfiguration getConfiguration(){
		return DBUtils.getConfiguration(this.em);
	}
	
	@SuppressWarnings("unchecked")
	public List<RUbioSeqConfiguration> getConfigurations(){
		return (List<RUbioSeqConfiguration>) this.em
				.createQuery("SELECT c FROM RUbioSeqConfiguration c")
				.getResultList();
	}
	
	@NotifyChange({ "configurations", "visibleConfigProgramPaths", "labelRubioSeqPathCommandClass", "labelDatastoresPathCommandClass" })
	@Command("editConfiguration")
	public void editConfiguration(
			@BindingParam("config") RUbioSeqConfiguration config) {
		if (config.getPrivateDatastoresRootDirectory().endsWith("/")) {
			config.setPrivateDatastoresRootDirectory(config
					.getPrivateDatastoresRootDirectory().substring(
							0,
							config.getPrivateDatastoresRootDirectory()
									.lastIndexOf("/")));
		}
		if (this.getConfiguration()
				.isCreatePrivateDatastoresOnUserRegistration()
				&& !Utils.isValidDirectory(config
						.getPrivateDatastoresRootDirectory())) {
			Messagebox
					.show("The specified directory does not exist or it is not writable, please, choose another location.",
							"Invalid path", Messagebox.OK, Messagebox.ERROR);
		}
		if (!Utils.isValidFile(config.getRubioseqCommand())) {
			Messagebox
					.show("The specified RUbioSeqPath does not exist or it is not a valid directory, please, choose another directory.",
							"Invalid directory", Messagebox.OK, Messagebox.ERROR);
			hideConfigProgramPathsGrid();
		} else {
			showConfigProgramPathsGrid(config.getRubioseqCommand());
		}
		this.em.getTransaction().begin();
		this.em.merge(config);
		this.em.getTransaction().commit();
	}
	
	public boolean isVisibleConfigProgramPaths() {
		if (this.visibleConfigProgramPaths == null) {
			String rsCommand = this.getConfiguration().getRubioseqCommand();
			if (!Utils.isValidFile(rsCommand)) {
				hideConfigProgramPathsGrid();
			} else {
				showConfigProgramPathsGrid(rsCommand);
			}
		}
		return visibleConfigProgramPaths.equals(1);
	}

	private void hideConfigProgramPathsGrid() {
		this.visibleConfigProgramPaths = 0;
	}

	private void showConfigProgramPathsGrid(String rubioSeqCommand) {
		this.visibleConfigProgramPaths = 1;
		File rubioSeqCommandFile = new File(rubioSeqCommand);
		String rubioSeqPath = rubioSeqCommandFile.getParent();
		
		this.configProgramPathsFileSNV = new File(rubioSeqPath, CONFIG_FILE_SNV);
		this.configProgramPathsFileCNV = new File(rubioSeqPath, CONFIG_FILE_CNV);
		this.configProgramPathsFileMethylation = new File(rubioSeqPath, CONFIG_FILE_METHYLATION);
		this.configProgramPathsFileChipSeq = new File(rubioSeqPath, CONFIG_FILE_CHIPSEQ);
		
		notifyChangesConfigProgramPathFiles();
	}

	private void notifyChangesConfigProgramPathFiles() {
		BindUtils.postNotifyChange(null,null,this,"configProgramPathsFileSNV");
		BindUtils.postNotifyChange(null,null,this,"configProgramPathsFileCNV");
		BindUtils.postNotifyChange(null,null,this,"configProgramPathsFileMethylation");
		BindUtils.postNotifyChange(null,null,this,"configProgramPathsFileChipSeq");
	}

	@DependsOn("configurations")
	public boolean isPrivateDatastoresRootDirectoryTextBoxEnabled(){
		return this.getConfiguration().isCreatePrivateDatastoresOnUserRegistration();
	}
	
	public File getConfigProgramPathsFileSNV() {
		return configProgramPathsFileSNV;
	}
	
	public File getConfigProgramPathsFileCNV() {
		return configProgramPathsFileCNV;
	}
	
	public File getConfigProgramPathsFileMethylation() {
		return configProgramPathsFileMethylation;
	}
	
	public File getConfigProgramPathsFileChipSeq() {
		return configProgramPathsFileChipSeq;
	}
	
	@DependsOn("configProgramPathsFileSNV")
	public boolean isEditSNVEnabled() {
		return isValidCPPFile(this.configProgramPathsFileSNV);
	}
	
	@DependsOn("configProgramPathsFileCNV")
	public boolean isEditCNVEnabled() {
		return isValidCPPFile(this.configProgramPathsFileCNV);
	}
	
	@DependsOn("configProgramPathsFileMethylation")
	public boolean isEditMethylationEnabled() {
		return isValidCPPFile(this.configProgramPathsFileMethylation);
	}
	
	@DependsOn("configProgramPathsFileChipSeq")
	public boolean isEditChipSeqEnabled() {
		return isValidCPPFile(this.configProgramPathsFileChipSeq);
	}
	
	private boolean isValidCPPFile(File f) {
		return f != null && f.isFile() && f.canRead() && f.canWrite();
	}
	
	private static final String INVALID_FILE_STYLE = "text-decoration:line-through;";
	
	public static String getStyle(boolean enabled) {
		if (enabled) {
			return "";
		} else {
			return INVALID_FILE_STYLE;
		}
	}
	
	@DependsOn("configProgramPathsFileSNV")
	public String getStyleSNV() {
		return getStyle(this.isEditSNVEnabled());
	}
	
	@DependsOn("configProgramPathsFileCNV")
	public String getStyleCNV() {
		return getStyle(this.isEditCNVEnabled());
	}
	
	@DependsOn("configProgramPathsFileMethylation")
	public String getStyleMethylation() {
		return getStyle(this.isEditMethylationEnabled());
	}
	
	@DependsOn("configProgramPathsFileChipSeq")
	public String getStyleChipSeq() {
		return getStyle(this.isEditChipSeqEnabled());
	}
	
	public static String getButtonClass(boolean enabled){
		if(enabled){
			return "actionButton";
		}else{
			return "";
		}
	}

	@DependsOn("configProgramPathsFileSNV")
	public String getButtonClassSNV() {
		return getButtonClass(this.isEditSNVEnabled());
	}
	
	@DependsOn("configProgramPathsFileCNV")
	public String getButtonClassCNV() {
		return getButtonClass(this.isEditCNVEnabled());
	}
	
	@DependsOn("configProgramPathsFileMethylation")
	public String getButtonClassMethylation() {
		return getButtonClass(this.isEditMethylationEnabled());
	}
	
	@DependsOn("configProgramPathsFileChipSeq")
	public String getButtonClassChipSeq() {
		return getButtonClass(this.isEditChipSeqEnabled());
	}
	
	public static String getButtonTooltip(boolean enabled, File file){
		if(enabled){
			return "Edit configProgramPaths.xml";
		}else{
			if(file!=null){
				return file.getAbsolutePath() + " does not exist, it cannot be read or it is not writtable.";
			} else return "";
		}
	}
	
	@DependsOn("configProgramPathsFileSNV")
	public String getButtonTooltipSNV() {
		return getButtonTooltip(this.isEditSNVEnabled(), this.getConfigProgramPathsFileSNV());
	}
	
	@DependsOn("configProgramPathsFileCNV")
	public String getButtonTooltipCNV() {
		return getButtonTooltip(this.isEditCNVEnabled(), this.getConfigProgramPathsFileCNV());
	}
	
	@DependsOn("configProgramPathsFileMethylation")
	public String getButtonTooltipMethylation() {
		return getButtonTooltip(this.isEditMethylationEnabled(), this.getConfigProgramPathsFileMethylation());
	}
	
	@DependsOn("configProgramPathsFileChipSeq")
	public String getButtonTooltipChipSeq() {
		return getButtonTooltip(this.isEditChipSeqEnabled(), this.getConfigProgramPathsFileChipSeq());
	}
	
	@Command
	public void editSNV(){
		Sessions.getCurrent().setAttribute(CPP_SNV,
				this.configProgramPathsFileSNV);
		Executions.getCurrent().sendRedirect(
				"/administrator/configuration/config-program-paths-snv.zul");
	}
	
	@Command
	public void editCNV(){
		Sessions.getCurrent().setAttribute(CPP_CNV,
				this.configProgramPathsFileCNV);
		Executions.getCurrent().sendRedirect(
				"/administrator/configuration/config-program-paths-cnv.zul");
	}
	
	@Command
	public void editMethylation(){
		Sessions.getCurrent().setAttribute(CPP_METHYLATION,
				this.configProgramPathsFileMethylation);
		Executions.getCurrent().sendRedirect(
				"/administrator/configuration/config-program-paths-methylation.zul");
	}
	
	@Command
	public void editChipSeq(){
		Sessions.getCurrent().setAttribute(CPP_CHIPSEQ,
				this.configProgramPathsFileChipSeq);
		Executions.getCurrent().sendRedirect(
				"/administrator/configuration/config-program-paths-chipseq.zul");
	}

	private RUbioSeqFile rubioseqPathDirectory;
	private boolean selectRubioseqPathDirectory;
	private boolean editRubioseqPath = false;
	private boolean editPrivateDatastoresPath = false;
	
	public RUbioSeqFile getRubioseqPathDirectory() {
		return rubioseqPathDirectory;
	}
	
	public void setRubioseqPathDirectory(RUbioSeqFile rubioseqPathDirectory) {
		if(!rubioseqPathDirectory.getFile().isDirectory()){
			Messagebox.show(" You must select a directory.",
					"Invalid file", Messagebox.OK, Messagebox.ERROR);
		}
		this.rubioseqPathDirectory = rubioseqPathDirectory;
	}
	
	public boolean isSelectRubioseqPathDirectory() {
		return selectRubioseqPathDirectory;
	}
	
	@NotifyChange({ "selectRubioseqPathDirectory" })
	@Command
	public void editRubioseqPath(){
		this.editRubioseqPath = true;
		this.selectRubioseqPathDirectory = true;
	}
	
	@NotifyChange({ "selectRubioseqPathDirectory" })
	@Command
	public void editPrivateDatastoresPath(){
		this.editPrivateDatastoresPath = true;
		this.selectRubioseqPathDirectory = true;
	}
	
	@NotifyChange({ "configurations", "selectRubioseqPathDirectory", "visibleConfigProgramPaths" })
	@Command
	public void savePath(){
		if(this.editRubioseqPath){
			File rubioseqPath = new File(this.rubioseqPathDirectory.getFile(), ConfigurationViewModel.RUBIOSEQ_SCRIPT);
			RUbioSeqConfiguration config = this.getConfiguration();
			config.setRubioseqCommand(rubioseqPath.getAbsolutePath());
			this.editConfiguration(config);
		} 
		if(this.editPrivateDatastoresPath){
			RUbioSeqConfiguration config = this.getConfiguration();
			config.setPrivateDatastoresRootDirectory(this.rubioseqPathDirectory.getFile().getAbsolutePath());
			this.editConfiguration(config);
		}
		this.cancelSelectPath();
	}
	
	@NotifyChange({ "selectRubioseqPathDirectory" })
	@Command
	public void cancelSelectPath(){
		this.selectRubioseqPathDirectory = false;
		this.editPrivateDatastoresPath = false;
		this.editRubioseqPath = false;
	}
	
	public String getLabelRubioSeqPathCommandClass(){
		if (!Utils.isValidFile(this.getConfiguration().getRubioseqCommand())) {
			return Utils.SCLASS_INVALID_PATH;
		} else{
			return "";
		}
	}
	
	public String getLabelDatastoresPathCommandClass(){
		RUbioSeqConfiguration config = this.getConfiguration();
		if (config.isCreatePrivateDatastoresOnUserRegistration()
				&& !Utils.isValidDirectory(config
						.getPrivateDatastoresRootDirectory())) {
			return Utils.SCLASS_INVALID_PATH;
		} else{
			return "";
		}

	}
	
	/*
	 *  Map<String,Object> args = new HashMap<String,Object>();
        args.put("data", "postX");
        BindUtils.postGlobalCommand("myqueue", EventQueues.DESKTOP, "cmdX", args);
	 */
}

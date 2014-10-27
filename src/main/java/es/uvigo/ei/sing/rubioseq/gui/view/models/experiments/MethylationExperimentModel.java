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
package es.uvigo.ei.sing.rubioseq.gui.view.models.experiments;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.Experiment;
import es.uvigo.ei.sing.rubioseq.ExperimentType;
import es.uvigo.ei.sing.rubioseq.User;
import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.view.models.EditExperimentModel;
import es.uvigo.ei.sing.rubioseq.gui.view.models.ExecuteExperimentModel;

/**
 * 
 * @author hlfernandez
 *
 */
public class MethylationExperimentModel {
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	private MethylationExperiment experiment = new MethylationExperiment();

	private RUbioSeqFile outputFilePathRSFile;
	private boolean selectOutputFile;
	private String outputFilePath = "/home/hlfernandez/Tmp";
	private String outputFileName = "experiment.xml";

	private RUbioSeqFile currentExperimentFile;

	private boolean executeExperiment = false;
	
	@Init
	public void init(){
		RUbioSeqFile editFile = (RUbioSeqFile) Sessions.getCurrent().getAttribute(EditExperimentModel.EDIT_EXPERIMENT_FILE);
		if(editFile!=null){
			this.outputFilePathRSFile = new RUbioSeqFile(new File(editFile.getFile().getParent()), editFile.getDatastore());
			this.outputFilePath = editFile.getFile().getParent();
			this.outputFileName = editFile.getFile().getName();
			Object data = Sessions.getCurrent().getAttribute(EditExperimentModel.EDIT_EXPERIMENT_DATA);
			if(data!=null){
				this.experiment = (MethylationExperiment) data;
			}
//			try{
//				this.experiment.loadDataFromFile(editFile.getFile());
//			} catch(InvalidRubioSeqParameterException e){
//				Sessions.getCurrent().setAttribute(EditExperimentModel.EDIT_EXPERIMENT_FILE, editFile);
//				Sessions.getCurrent().setAttribute(EditExperimentModel.EDIT_EXPERIMENT_ERROR_MSG, e.getMessage());
//				Executions.getCurrent().sendRedirect("edit-experiment.zul");
//				return;
//			}
		}
		Sessions.getCurrent().removeAttribute(EditExperimentModel.EDIT_EXPERIMENT_DATA);
		Sessions.getCurrent().removeAttribute(EditExperimentModel.EDIT_EXPERIMENT_FILE);
	}
	
	@SuppressWarnings("unchecked")
	public List<Experiment> getUserexperiments(){
		final Session webSession = Sessions.getCurrent();
		String username = ((User) webSession.getAttribute("user")).getUsername();
		return (List<Experiment>) this.em.createQuery(
				"SELECT e FROM Experiment e where e.user='" + username + "'")
				.getResultList();
	}
	
	@NotifyChange({"selectOutputFile"})	
	@Command
	public void selectOutputFile(){
		this.selectOutputFile = true;
	}
	
	@NotifyChange({"selectOutputFile"})	
	@Command 
	public void cancelSelectOutputFile(){
		this.selectOutputFile = false;
	}
	
	public boolean isSelectOutputFile() {
		return selectOutputFile;
	}
	
	public String getOutputFileName() {
		return outputFileName;
	}
	
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}
	
	public RUbioSeqFile getOutputFilePath() {
		return outputFilePathRSFile;
	}
	
	public void setOutputFilePath(RUbioSeqFile outputFilePathRSFile) {
		this.outputFilePathRSFile = outputFilePathRSFile;
		this.outputFilePath = this.outputFilePathRSFile.getFile().getAbsolutePath(); 
	}
	
	@NotifyChange({"selectOutputFile", "currentExperimentFile"})
	@Command
	public void generateXMLConfigurationFile(){
		File output = new File(this.outputFilePath, this.outputFileName);
		File outputDirectory = new File(this.outputFilePath);
		if (outputDirectory.isDirectory()){
			if (outputDirectory.canWrite()){
				try {
					experiment.generateXMLConfigurationFile(output);
					this.currentExperimentFile = new RUbioSeqFile(output, outputFilePathRSFile.getDatastore());
					Messagebox.show("Configuration saved", "OK", Messagebox.OK,
							Messagebox.INFORMATION);
				} catch (IOException e) {
					Messagebox.show("Error saving configuration", "Error", Messagebox.OK,
							Messagebox.ERROR);
					this.currentExperimentFile = null;
					e.printStackTrace();
				}
				this.selectOutputFile = false;
			} else{
				this.currentExperimentFile = null;
				Messagebox.show("Cannot write to file " + output.getName(),
						"Invalid file", Messagebox.OK, Messagebox.ERROR);
			}				
		} else{
			this.currentExperimentFile = null;
			Messagebox.show(this.outputFilePath + " is not a directory.",
					"Invalid file", Messagebox.OK, Messagebox.ERROR);
		}
		if(this.executeExperiment){
			this.executeExperiment();
			this.executeExperiment = false;
		}
	}
	
	public MethylationExperiment getExperiment() {
		return experiment;
	}
	
	public void setExperiment(MethylationExperiment experiment) {
		this.experiment = experiment;
	}
	
	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	@DependsOn({"experiment.referencePath", "experiment.plattform", "experiment.projectCompletePath",
		"samples", "experiment.readsPath", "experiment.methylType"})
	public boolean isEnabledExportButton(){
		if(experiment.getReferencePath()==null || 
				experiment.getPlattform()==null ||
				experiment.getProjectCompletePath()==null ||
				!experiment.checkSamples() ||
				experiment.getReadsPath()==null ||
				experiment.getMethylType()==null){
			return false;
		}
		return true;
	}
	
	public boolean isStringOk(String str){
		if(str == null || str.equals("")){
			return false;
		}
		return true;
	}
	
	@NotifyChange({"samples", "selectSampleFile", "selectedSampleFile"})
	@Command
	public void addSample(){
		MethylationSample metSample = new MethylationSample();
		this.experiment.getSamples().add(metSample);
		this.editSample = metSample;
		this.selectSampleFile = true;
		this.editSample1 = true;
		this.selectedSampleFile = metSample.getSample1();
	}
	
	@NotifyChange({"samples"})
	@Command
	public void deleteSample(@BindingParam("sample") MethylationSample sample){
		this.experiment.getSamples().remove(sample); 
	}
	
	public List<MethylationSample> getSamples(){
		return experiment.getSamples();
	}
	
	@NotifyChange({"experiment", "samples"})
	@Command("restoreSample2")
	public void restoreSample2(@BindingParam("sample") MethylationSample sample){
		sample.setSample2(null);
	}
	
	@NotifyChange("experiment")
	@Command("restoreIntervals")
	public void restoreIntervals(){
		experiment.setIntervalsPath(experiment.getIntervalsPath_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreSeedLength")
	public void restoreSeedLength(){
		experiment.setSeedLength(experiment.getSeedLength_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreNumMiss")
	public void restoreNumMiss(){
		experiment.setNumMis(experiment.getNumMis_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreTrimTagLength")
	public void restoreTrimTagLength(){
		experiment.setTrimTagLength(experiment.getTrimTagLength_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreMinQual")
	public void restoreMinQual(){
		experiment.setMinQual(experiment.getMinQual_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreDepthFilter")
	public void restoreDepthFilter(){
		experiment.setDepthFilter(experiment.getDepthFilter_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreContextType")
	public void restoreContextType(){
		experiment.setContextType(experiment.getContextType_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreMultiExec")
	public void restoreMultiExec(){
		experiment.setMultiExec(experiment.getMultiExec_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreFastqc")
	public void restoreFastqc(){
		experiment.setFastqc(experiment.getFastqc_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restorequeueSGEProject")
	public void restorequeueSGEProject(){
		experiment.setQueueSGEProject(experiment.getQueueSGEProject_DV());
	}
	
	@DependsOn("currentExperimentFile")
	public boolean isEnabledExecuteExperimentButton(){
		return this.currentExperimentFile!=null;
	}
	
	@NotifyChange({"selectOutputFile"})	
	@Command
	public void saveBeforeExecuteExperiment(){
		this.selectOutputFile = true;
		this.executeExperiment   = true;
	}
	
	@Command
	public void executeExperiment(){
		Sessions.getCurrent().setAttribute(
				ExecuteExperimentModel.EXPERIMENT_FILE,
				new ExecuteExperimentModel.ExperimentFile(
						this.currentExperimentFile, ExperimentType.Methylation));
		Executions.getCurrent().sendRedirect("/experiments/execute-experiment.zul");
	}
	
	@DependsOn("enabledExportButton")
	public String getExportButtonClass(){
		if(isEnabledExportButton()){
			return "actionButton";
		}else{
			return "";
		}
	}
	
	private boolean selectSampleFile = false;
	private RUbioSeqFile selectedSampleFile = null;
	private MethylationSample editSample = null;
	private boolean editSample1 = false;
	
	@NotifyChange({"selectSampleFile", "selectedSampleFile"})
	@Command
	public void editSample1(@BindingParam("sample") MethylationSample sample){
		this.editSample = sample;
		this.selectSampleFile = true;
		this.editSample1 = true;
		this.selectedSampleFile = sample.getSample1();
	}
	
	@NotifyChange({"selectSampleFile", "selectedSampleFile"})
	@Command
	public void editSample2(@BindingParam("sample") MethylationSample sample){
		this.editSample = sample;
		this.selectSampleFile = true;
		this.editSample1 = false;
		this.selectedSampleFile = sample.getSample2();
	}
	
	public boolean isSelectSampleFile() {
		return selectSampleFile;
	}
	
	public RUbioSeqFile getSelectedSampleFile() {
		return selectedSampleFile;
	}
	
	public void setSelectedSampleFile(RUbioSeqFile s) {
		this.selectedSampleFile = s;
	}
	
	@NotifyChange({"selectSampleFile", "samples"})
	@Command
	public void saveSampleFile(){
		this.selectSampleFile = false;
		if(editSample1){
			this.editSample.setSample1(this.selectedSampleFile);
		} else{
			this.editSample.setSample2(this.selectedSampleFile);
		}
		this.selectedSampleFile = null;
	}
	
	@NotifyChange({"selectSampleFile"})
	@Command
	public void cancelSelectSampleFile(){
		this.selectedSampleFile = null;
		this.selectSampleFile = false;
	}
}

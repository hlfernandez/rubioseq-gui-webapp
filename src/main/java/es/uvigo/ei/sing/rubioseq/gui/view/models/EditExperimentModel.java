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
import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.ExperimentType;
import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.ChipSeqExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.CopyNumberVariationExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.ExperimentUtils;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.InvalidRubioSeqParameterException;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.MethylationExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.SingleNucleotideVariantExperiment;

/**
 * 
 * @author hlfernandez
 *
 */
public class EditExperimentModel {
	
	public static final String EDIT_EXPERIMENT_FILE = "edit_experiment_file";
	public static final String EDIT_EXPERIMENT_DATA = "edit_experiment_data";
	
	private RUbioSeqFile configFile;
	private ExperimentType experimentType;
	
	public RUbioSeqFile getConfigFile() {
		return configFile;
	}
	
//	@Init
//	public void init(){
//		RUbioSeqFile editFile = (RUbioSeqFile) Sessions.getCurrent().getAttribute(EditExperimentModel.EDIT_EXPERIMENT_FILE);
//		String errorMessage = (String) Sessions.getCurrent().getAttribute(EditExperimentModel.EDIT_EXPERIMENT_ERROR_MSG);
//		if(editFile!=null && errorMessage!=null){
//			Messagebox.show(errorMessage, "Error reading configuration XML", Messagebox.OK, Messagebox.ERROR);
//			this.setConfigFile(editFile);
//		}
//	}
	
	@NotifyChange({"experimentType", "configFile"})
	public void setConfigFile(RUbioSeqFile configFile) {
		this.configFile = configFile;
		File experimentFile = this.configFile.getFile();
		try{
			this.setExperimentType(ExperimentUtils.getExperimentType(experimentFile));
			if(experimentFile.isDirectory() || !experimentFile.getName().toLowerCase().endsWith(".xml")){
				clearStatus();
				Messagebox
						.show("Invalid input file. You have to select a .xml file.",
								"Invalid input file", Messagebox.OK,
								Messagebox.ERROR);
			} else if (!experimentFile.canRead()){
				clearStatus();
					Messagebox
					.show("Can't read the input file.",
							"Invalid input file", Messagebox.OK,
							Messagebox.ERROR);
					}
		} catch(InvalidRubioSeqParameterException ex){
			clearStatus();
			Messagebox
			.show(ex.getMessage(),
					"Invalid input file", Messagebox.OK,
					Messagebox.ERROR);
			
		}
	}
	
	private final void clearStatus(){
		this.configFile = null;
		this.experimentType = null;
	}
	
	public ExperimentType getExperimentType() {
		return experimentType;
	}
	
	public void setExperimentType(ExperimentType experimentType) {
		this.experimentType = experimentType;
	}
	
	public List<ExperimentType> getExperimentTypeValues(){
		return Arrays.asList(ExperimentType.values());
	}
	
	@DependsOn({"experimentType", "configFile"})
	public boolean isEditExperimentEnabled(){
		if (this.getConfigFile() != null
				&& !this.getConfigFile().equals("")
				&& this.getConfigFile().getFile().getName().toLowerCase().endsWith(".xml")
				&& this.experimentType != null) {
			return true;
		}
		return false;
	}

	@DependsOn({"configFile"})
	public String getConfigurationFileLabelClass(){
		if (this.getConfigFile() != null
				&& !this.getConfigFile().equals("")
				&& this.getConfigFile().getFile().getName().toLowerCase().endsWith(".xml")){
			return "";
		}
		return Utils.SCLASS_INVALID_PATH;
	}
	
	@Command
	public void editExperiment(){
		
		Sessions.getCurrent().setAttribute(EDIT_EXPERIMENT_FILE, getConfigFile());
				
		if (this.experimentType.equals(ExperimentType.SNV)) {
			try{
				SingleNucleotideVariantExperiment snv = new SingleNucleotideVariantExperiment();
				snv.loadDataFromFile(getConfigFile().getFile());
				Sessions.getCurrent().setAttribute(EDIT_EXPERIMENT_DATA, snv);
				Executions.sendRedirect("/experiments/create-experiment-snv.zul");
			} catch(InvalidRubioSeqParameterException ex){
				handleXMLError(ex.getMessage());
			}
		} else if (this.experimentType.equals(ExperimentType.CNV)) {
			try{
				CopyNumberVariationExperiment cnv = new CopyNumberVariationExperiment();
				cnv.loadDataFromFile(getConfigFile().getFile());
				Sessions.getCurrent().setAttribute(EDIT_EXPERIMENT_DATA, cnv);
				Executions.sendRedirect("/experiments/create-experiment-cnv.zul");
			} catch(InvalidRubioSeqParameterException ex){
				handleXMLError(ex.getMessage());
			}
		} else if (this.experimentType.equals(ExperimentType.CHIPSeq)) {
			try{
				ChipSeqExperiment cs = new ChipSeqExperiment();
				cs.loadDataFromFile(getConfigFile().getFile());
				Sessions.getCurrent().setAttribute(EDIT_EXPERIMENT_DATA, cs);
				Executions
					.sendRedirect("/experiments/create-experiment-chipseq.zul");
			} catch(InvalidRubioSeqParameterException ex){
				handleXMLError(ex.getMessage());
			}
		} else if (this.experimentType.equals(ExperimentType.Methylation)) {
			try{
				MethylationExperiment met = new MethylationExperiment();
				met.loadDataFromFile(getConfigFile().getFile());
				Sessions.getCurrent().setAttribute(EDIT_EXPERIMENT_DATA, met);
				Executions
					.sendRedirect("/experiments/create-experiment-methylation.zul");
			} catch(InvalidRubioSeqParameterException ex){
				handleXMLError(ex.getMessage());
			}
		}
//		Sessions.getCurrent().setAttribute(EDIT_EXPERIMENT_FILE, getConfigFile());
//		
//		if(this.experimentType.equals(ExperimentType.SNV)){
//			Executions.sendRedirect("/experiments/create-experiment-snv.zul");
//		} else if(this.experimentType.equals(ExperimentType.CNV)){
//				Executions.sendRedirect("/experiments/create-experiment-cnv.zul");
//			} else if(this.experimentType.equals(ExperimentType.CHIPSeq)){
//					Executions.sendRedirect("/experiments/create-experiment-chipseq.zul");
//				} else if(this.experimentType.equals(ExperimentType.Methylation)){
//						Executions.sendRedirect("/experiments/create-experiment-methylation.zul");
//					}
	}
	
	private void handleXMLError(String errorMessage){
		Messagebox.show(errorMessage, "Error reading configuration XML",
				Messagebox.OK, Messagebox.ERROR);
	}

	@DependsOn("editExperimentEnabled")
	public String getEditButtonClass(){
		if(isEditExperimentEnabled()){
			return "actionButton";
		}else{
			return "";
		}
	}
	
	@DependsOn("experimentType")
	public boolean isExperimentTypeRowVisible(){
		return this.experimentType != null;
	}
}

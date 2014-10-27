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

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.Experiment;
import es.uvigo.ei.sing.rubioseq.ExperimentStatus;
import es.uvigo.ei.sing.rubioseq.ExperimentType;
import es.uvigo.ei.sing.rubioseq.User;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.ChipSeqExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.CopyNumberVariationExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.MethylationExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.SingleNucleotideVariantExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.ExperimentDirectoryWatcher;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.ExperimentProgress;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.LogFileWatcher;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.Measurable;

/**
 * 
 * @author hlfernandez
 *
 */
public class ExperimentsViewModel {
	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<Experiment> getUserexperiments(){
		final Session webSession = Sessions.getCurrent();
		String username = ((User) webSession.getAttribute("user")).getUsername();
		List<Experiment> toret = (List<Experiment>) this.em.createQuery("SELECT e FROM Experiment e where e.user='" + username + "'").getResultList();
		return toret;
	}
	
	@NotifyChange({"userexperiments"})	
	@Command
	public void delete(@BindingParam("experiment") final Experiment experiment){
		this.em.getTransaction().begin();
		this.em.remove(experiment);
		this.em.flush();
		this.em.getTransaction().commit();
	}

	@Command
	public void view(@BindingParam("experiment") final Experiment experiment){
		Sessions.getCurrent().setAttribute("experiment", experiment);
		Executions.sendRedirect("/experiments/view-experiment.zul");
	}
	
	@NotifyChange({"userexperiments"})
	@Command 
	public void updateExperimentProgress(@BindingParam("experiment") final Experiment experiment){
		_updateExperimentProgress(experiment);
	}
	
	@NotifyChange({"userexperiments"})
	
	@Command 
	public void updateExperiments(){
		for(Experiment experiment : getUserexperiments()){
			_updateExperimentProgress(experiment);
		}
	}
	
	private void _updateExperimentProgress(Experiment experiment){
		if(experiment.getStatus().equals(ExperimentStatus.Running)){
			if(!Utils.existDirectory(experiment.getWorkingDirectory())){
				Messagebox.show("Directory " + experiment.getWorkingDirectory()
						+ " does not exist", "Invalid experiment directory",
						Messagebox.OK, Messagebox.ERROR);
				return;
			}
			File configurationFile = lookUpXMLConfigurationFile(experiment.getWorkingDirectory());
			if(configurationFile == null){
				Messagebox.show("Experiment configuration file for experiment in working directory " + experiment.getWorkingDirectory()
						+ " does not exist", "Cannot found experiment configuration file",
						Messagebox.OK, Messagebox.ERROR);
				return;
			}
			ViewExperimentModel vEM = new ViewExperimentModel();
			ExperimentProgress experimentProgress = new ExperimentProgress(vEM);
			vEM.setExperiment(experiment);
			vEM.setExperimentProgress(experimentProgress);
			vEM.setWorkingDirectory(experiment.getWorkingDirectory());
			Measurable experimentData = null;
			try{
				if(experiment.getType().equals(ExperimentType.SNV)){
					SingleNucleotideVariantExperiment snv = new SingleNucleotideVariantExperiment();
					snv.loadDataFromFile(configurationFile);
					experimentData = snv;
				} else if(experiment.getType().equals(ExperimentType.CNV)){
					CopyNumberVariationExperiment cnv = new CopyNumberVariationExperiment();
					cnv.loadDataFromFile(configurationFile);
					experimentData = cnv;
				} else if(experiment.getType().equals(ExperimentType.CHIPSeq)){
					ChipSeqExperiment cs = new ChipSeqExperiment();
					cs.loadDataFromFile(configurationFile);
					experimentData = cs;
				} else if(experiment.getType().equals(ExperimentType.Methylation)){
					MethylationExperiment met = new MethylationExperiment();
					met.loadDataFromFile(configurationFile);
					experimentData = met;
				}
				
				vEM.setExperimentData(experimentData);
				
				List<String> pathsToWatch = experimentData.getPathsToWatch();
				
				for(String pathToWatch : pathsToWatch){
		    		if(Utils.existDirectory(pathToWatch)){
			    		File dir = new File(pathToWatch);
			    		for(File f : dir.listFiles()){
			    			if(f.getName().startsWith(ExperimentDirectoryWatcher.LOG_FILE_START) && f.getName().endsWith(ExperimentDirectoryWatcher.LOG_FILE_END)){
			    				LogFileWatcher logFileWatcher = new LogFileWatcher(f, experimentProgress);
			    				logFileWatcher.parseFile();
			    			}
			    		}
		    		}
		    	}
				if(experimentProgress.isAborted()){
					changeExperimentStatus(experiment, ExperimentStatus.Aborted);
					return;
				}
				if(experimentProgress.getProgress() == 100){
					changeExperimentStatus(experiment, ExperimentStatus.Finished);
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
	private void changeExperimentStatus(Experiment experiment, ExperimentStatus newStatus){
		experiment.setStatus(newStatus);
		this.em.getTransaction().begin();
		this.em.merge(experiment);
		this.em.flush();
		this.em.getTransaction().commit();
	}
	
	private static File lookUpXMLConfigurationFile(String dir){
		for(File f : (new File(dir)).listFiles()){
			if(f.canRead() && f.getName().endsWith(".xml")){
				return f;
			}
		}
		return null;
	}
}

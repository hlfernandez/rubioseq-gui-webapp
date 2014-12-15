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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;

/**
 * 
 * @author hlfernandez
 *
 */
public class ConfigProgramPathsMethylation {

	private static final String COMMENT_METHYLATION = "RUbioSeq MethylBis CONFIG FILE";
	private static final String TEMPLATE_METHYLATION = "template_configProgramPaths.txt";
	public static final String CONFIG_DATA = "configData";
	public static final String BISMARK_PATH = "bismarkPath";
	public static final String BOWTIE_PATH = "bowtiePath";
	public static final String PICARD_PATH = "picardPath";
	public static final String BED_TOOLS_PATH = "bedToolsPath";
	public static final String FASTQC_PATH = "fastqcPath";
	public static final String FASTX_PATH = "fastxPath";
	public static final String FILO_PATH = "filoPath";
	public static final String NTHR = "nthr";
	public static final String JAVA_RAM = "javaRam";
	public static final String QUEUE_SYSTEM = "queueSystem";
	public static final String QUEUE_NAME = "queueName";
	public static final String MULTICORE_NAME = "multicoreName";
	public static final String MULTICORE_NUMBER = "multicoreNumber";

	private RUbioSeqFile bismarkPath;
	private RUbioSeqFile bowtiePath;
	private RUbioSeqFile picardPath;
	private RUbioSeqFile bedtoolsPath;
	private RUbioSeqFile fastqcPath;
	private RUbioSeqFile fastxPath;
	private RUbioSeqFile filoPath;
	private Integer nthr = 1;
	private Integer nthr_DV = 1;
	private String javaRam = "";
	private String javaRam_DV = "";
	private QueueSystem queueSystem;
	private String queueName = "";
	private String queueName_DV = "";
	private String multicoreName = "";
	private String multicoreName_DV = "";
	private Integer multicoreNumber = -1;
	private Integer multicoreNumber_DV = -1;
	
	public enum QueueSystem {
		SGE("SGE"), 
		PBS("PBS"),
		NONE("none");
		
		private final String displayName;
		
		private QueueSystem(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public List<QueueSystem> getQueueSystemValues(){
		return Arrays.asList(QueueSystem.values());
	}
	
	public RUbioSeqFile getPicardPath() {
		return picardPath;
	}

	public void setPicardPath(RUbioSeqFile picardPath) {
		this.picardPath = picardPath;
	}
	
	public RUbioSeqFile getBedtoolsPath() {
		return bedtoolsPath;
	}
	
	public void setBedtoolsPath(RUbioSeqFile bedtoolsPath) {
		this.bedtoolsPath = bedtoolsPath;
	}

	public RUbioSeqFile getFastqcPath() {
		return fastqcPath;
	}

	public void setFastqcPath(RUbioSeqFile fastqcPath) {
		this.fastqcPath = fastqcPath;

	}
	
	public RUbioSeqFile getBismarkPath() {
		return bismarkPath;
	}

	public void setBismarkPath(RUbioSeqFile bismarkPath) {
		this.bismarkPath = bismarkPath;
	}

	public RUbioSeqFile getBowtiePath() {
		return bowtiePath;
	}

	public void setBowtiePath(RUbioSeqFile bowtiePath) {
		this.bowtiePath = bowtiePath;
	}

	public RUbioSeqFile getFastxPath() {
		return fastxPath;
	}

	public void setFastxPath(RUbioSeqFile fastxPath) {
		this.fastxPath = fastxPath;
	}

	public RUbioSeqFile getFiloPath() {
		return filoPath;
	}

	public void setFiloPath(RUbioSeqFile filoPath) {
		this.filoPath = filoPath;
	}

	public Integer getNthr() {
		return nthr;
	}

	public void setNthr(Integer nthr) {
		this.nthr = nthr;
	}

	public String getJavaRam() {
		return javaRam;
	}

	public void setJavaRam(String javaRam) {
		this.javaRam = javaRam;
	}

	public QueueSystem getQueueSystem() {
		return queueSystem;
	}

	public void setQueueSystem(QueueSystem queueSystem) {
		this.queueSystem = queueSystem;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getMulticoreName() {
		return multicoreName;
	}

	public void setMulticoreName(String multicoreName) {
		this.multicoreName = multicoreName;
	}

	public Integer getMulticoreNumber() {
		return multicoreNumber;
	}

	public void setMulticoreNumber(Integer multicoreNumber) {
		this.multicoreNumber = multicoreNumber;
	}

	public Integer getNthr_DV() {
		return nthr_DV;
	}

	public String getJavaRam_DV() {
		return javaRam_DV;
	}

	public String getQueueName_DV() {
		return queueName_DV;
	}

	public String getMulticoreName_DV() {
		return multicoreName_DV;
	}


	public Integer getMulticoreNumber_DV() {
		return multicoreNumber_DV;
	}

	public boolean validElement(Element e) {
		return e != null && e.getValue() != null && !e.getValue().equals("");
	}
	
	private void loadDefaultParameters(File configFile) {
		File defaultParametersFile = new File(configFile.getParentFile(),
				TEMPLATE_METHYLATION);
		Map<String, String> paramNameToValue = Utils.loadTemplateParametersFile(defaultParametersFile);
		for(String parameterName : paramNameToValue.keySet()){
			String parameterValue = paramNameToValue.get(parameterName);
			if (parameterName.equals(NTHR)) {
				try {
					this.nthr_DV = Integer.valueOf(parameterValue);
					this.nthr = this.nthr_DV;
				} catch (Exception ex) {

				}
			} else if (parameterName.equals(JAVA_RAM)) {
				this.javaRam_DV = parameterValue;
				this.javaRam = parameterValue;
			} else if (parameterName.equals(QUEUE_NAME)) {
				this.queueName_DV = parameterValue;
				this.queueName = parameterValue;
			} else if (parameterName.equals(MULTICORE_NAME)) {
				this.multicoreName_DV = parameterValue;
				this.multicoreName = parameterValue;
			} else if (parameterName.equals(MULTICORE_NUMBER)) {
				try {
					this.multicoreNumber_DV = Integer
							.valueOf(parameterValue);
					this.multicoreNumber = this.multicoreNumber_DV;
				} catch (Exception ex) {

				}
			}
		}
	}
	
	public void loadDataFromFile(File editFile) {
		
		loadDefaultParameters(editFile);
		
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document document = saxBuilder.build(editFile);
			Element configData = document.getRootElement(); 
			
			Element bismarkPath = configData.getChild(BISMARK_PATH);
			if(validElement(bismarkPath)){
				this.setBismarkPath(Utils.getRUbioSeqFile(bismarkPath.getValue()));
			}
			
			Element bowtiePath = configData.getChild(BOWTIE_PATH);
			if(validElement(bowtiePath)){
				this.setBowtiePath(Utils.getRUbioSeqFile(bowtiePath.getValue()));
			}
			
			Element picardPath = configData.getChild(PICARD_PATH);
			if(validElement(picardPath)){
				this.setPicardPath(Utils.getRUbioSeqFile(picardPath.getValue()));
			}
			
			Element fastqcPath = configData.getChild(FASTQC_PATH);
			if(validElement(fastqcPath)){
				this.setFastqcPath(Utils.getRUbioSeqFile(fastqcPath.getValue()));
			}
			
			Element BEDToolsPath = configData.getChild(BED_TOOLS_PATH);
			if(validElement(BEDToolsPath)){
				this.setBedtoolsPath(Utils.getRUbioSeqFile(BEDToolsPath.getValue()));
			}
			
			Element fastxPath = configData.getChild(FASTX_PATH);
			if(validElement(fastxPath)){
				this.setFastxPath(Utils.getRUbioSeqFile(fastxPath.getValue()));
			}
			
			Element filoPath = configData.getChild(FILO_PATH);
			if(validElement(filoPath)){
				this.setFiloPath(Utils.getRUbioSeqFile(filoPath.getValue()));
			}
			
			Element nthr = configData.getChild(NTHR);
			if(validElement(nthr)){
				try{
					this.setNthr(Integer.valueOf(nthr.getValue()));
				} catch(Exception e){}
			}
			
			Element javaRam = configData.getChild(JAVA_RAM);
			if(validElement(javaRam)){
				this.setJavaRam(javaRam.getValue());
			}
			
			Element queueSystem = configData.getChild(QUEUE_SYSTEM);
			if(validElement(queueSystem)){
				String queueSystemValue = queueSystem.getValue().toUpperCase();
				if(queueSystemValue.equals("SGE")){
					this.setQueueSystem(QueueSystem.SGE);
				} else if(queueSystemValue.equals("PBS")){
						this.setQueueSystem(QueueSystem.PBS);
					} else if(queueSystemValue.equals("NONE")){
							this.setQueueSystem(QueueSystem.NONE);
						}
			}	
			
			Element queueName = configData.getChild(QUEUE_NAME);
			if(validElement(queueName)){
				this.setQueueName(queueName.getValue());
			}
			
			Element multicoreName = configData.getChild(MULTICORE_NAME);
			if(validElement(multicoreName)){
				this.setMulticoreName(multicoreName.getValue());
			}
			
			Element multicoreNumber = configData.getChild(MULTICORE_NUMBER);
			if(validElement(multicoreNumber)){
				try{
					this.setMulticoreNumber(Integer.valueOf(multicoreNumber.getValue()));
				} catch(Exception e){}
			}
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportToXML(File output) throws IOException {
		Document configurationFile = new Document();
		Comment comment = new Comment(COMMENT_METHYLATION);
		configurationFile.addContent(comment);

		Element configData = new Element(CONFIG_DATA);
		configurationFile.setRootElement(configData);

		Element bismarkPath = new Element(BISMARK_PATH);
		bismarkPath.addContent(this.getBismarkPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(bismarkPath);
		
		Element bowtiePath = new Element(BOWTIE_PATH);
		bowtiePath.addContent(this.getBowtiePath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(bowtiePath);
		
		Element picardPath = new Element(PICARD_PATH);
		picardPath.addContent(this.getPicardPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(picardPath);
		
		Element BEDToolsPath = new Element(BED_TOOLS_PATH);
		BEDToolsPath.addContent(this.getBedtoolsPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(BEDToolsPath);
		
		Element fastxPath = new Element(FASTX_PATH);
		fastxPath.addContent(this.getFastxPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(fastxPath);
		
		Element filoPath = new Element(FILO_PATH);
		filoPath.addContent(this.getFiloPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(filoPath);
		
		Element fastqcPath = new Element(FASTQC_PATH);
		fastqcPath.addContent(this.getFastqcPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(fastqcPath);
		
		if(!this.getNthr().equals(nthr_DV)){
			Element nthr = new Element(NTHR);
			nthr.addContent(String.valueOf(this.getNthr()));
			configurationFile.getRootElement().addContent(nthr);
		}
		
		if(!this.getJavaRam().equals(javaRam_DV)){
			Element javaRam = new Element(JAVA_RAM);
			javaRam.addContent(this.getJavaRam());
			configurationFile.getRootElement().addContent(javaRam);
		}
		
		Element queueSystem = new Element(QUEUE_SYSTEM);
		queueSystem.addContent(this.getQueueSystem().getDisplayName());
		configurationFile.getRootElement().addContent(queueSystem);

		if(!this.getQueueName().equals(queueName_DV)){
			Element queueName = new Element(QUEUE_NAME);
			queueName.addContent(this.getQueueName());
			configurationFile.getRootElement().addContent(queueName);
		}
		
		if(!this.getMulticoreName().equals(multicoreName_DV)){
			Element multicoreName = new Element(MULTICORE_NAME);
			multicoreName.addContent(this.getMulticoreName());
			configurationFile.getRootElement().addContent(multicoreName);
		}
		
		if(!this.getMulticoreNumber().equals(multicoreNumber_DV)){
			Element multicoreNumber = new Element(MULTICORE_NUMBER);
			multicoreNumber.addContent(String.valueOf(this.getMulticoreNumber()));
			configurationFile.getRootElement().addContent(multicoreNumber);
		}

		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(configurationFile, new FileWriter(output));
	}
}

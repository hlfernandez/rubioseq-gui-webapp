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
public class ConfigProgramPathsChipSeq {

	private static final String COMMENT_CHIPSEQ = "RUbioSeq ChIPSeq CONFIG FILE";
	private static final String TEMPLATE_CHIPSEQ = "template_configProgramPaths.txt";
	public static final String CONFIG_DATA = "configData";
	public static final String BWA_PATH = "bwaPath";
	public static final String SAMTOOLS_PATH = "samtoolsPath";
	public static final String PICARD_PATH = "picardPath";
	public static final String MACS_PATH = "MACSPath";
	public static final String PYTHON_PATH = "PythonPath";
	public static final String BED_TOOLS_PATH = "BEDToolsPath";
	public static final String CCAT_PATH = "CCATPath";
	public static final String BGTW_PATH = "BedgraphtoWigPath";
	public static final String IDR_PATH = "IDRPath";
	public static final String PEAKANNOTATOR_PATH = "PeakAnnotatorPath";
	public static final String FASTQC_PATH = "fastqcPath";
	public static final String NTHR = "nthr";
	public static final String JAVA_RAM = "javaRam";
	public static final String QUEUE_SYSTEM = "queueSystem";
	public static final String QUEUE_NAME = "queueName";
	public static final String MULTICORE_NAME = "multicoreName";
	public static final String MULTICORE_NUMBER = "multicoreNumber";

	private RUbioSeqFile bwaPath;
	private RUbioSeqFile samtoolsPath;
	private RUbioSeqFile picardPath;
	private RUbioSeqFile macsPath;
	private RUbioSeqFile pythonPath;
	private RUbioSeqFile bedtoolsPath;
	private RUbioSeqFile ccatPath;
	private RUbioSeqFile bedgraphtobigwigPath;
	private RUbioSeqFile idrPath;
	private RUbioSeqFile peakannotatorPath;
	private RUbioSeqFile fastqcPath;
	private Integer nthr = 1;
	private Integer nthr_DV = 1;
	private String javaRam = "-Xmx16G";
	private String javaRam_DV = "-Xmx16G";
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
	
	public RUbioSeqFile getBwaPath() {
		return bwaPath;
	}

	public void setBwaPath(RUbioSeqFile bwaPath) {
		this.bwaPath = bwaPath;
	}

	public RUbioSeqFile getSamtoolsPath() {
		return samtoolsPath;
	}

	public void setSamtoolsPath(RUbioSeqFile samtoolsPath) {
		this.samtoolsPath = samtoolsPath;
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
	
	public RUbioSeqFile getMacsPath() {
		return macsPath;
	}

	public void setMacsPath(RUbioSeqFile macsPath) {
		this.macsPath = macsPath;
	}

	public RUbioSeqFile getPythonPath() {
		return pythonPath;
	}

	public void setPythonPath(RUbioSeqFile pytthonPath) {
		this.pythonPath = pytthonPath;
	}

	public RUbioSeqFile getCcatPath() {
		return ccatPath;
	}

	public void setCcatPath(RUbioSeqFile ccatPath) {
		this.ccatPath = ccatPath;
	}

	public RUbioSeqFile getBedgraphtobigwigPath() {
		return bedgraphtobigwigPath;
	}

	public void setBedgraphtobigwigPath(RUbioSeqFile bedgraphtobigwigPath) {
		this.bedgraphtobigwigPath = bedgraphtobigwigPath;
	}

	public RUbioSeqFile getIdrPath() {
		return idrPath;
	}

	public void setIdrPath(RUbioSeqFile idrPath) {
		this.idrPath = idrPath;
	}

	public RUbioSeqFile getPeakannotatorPath() {
		return peakannotatorPath;
	}

	public void setPeakannotatorPath(RUbioSeqFile peakannotatorPath) {
		this.peakannotatorPath = peakannotatorPath;
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


	public boolean validElement(Element e){
		return e!=null && e.getValue()!=null && !e.getValue().equals("");
	}
	
	private void loadDefaultParameters(File configFile) {
		File defaultParametersFile = new File(configFile.getParentFile(),
				TEMPLATE_CHIPSEQ);
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
			
			Element bwaPath = configData.getChild(BWA_PATH);
			if(validElement(bwaPath)){
				this.setBwaPath(Utils.getRUbioSeqFile(bwaPath.getValue()));
			}
			
			Element samtoolsPath = configData.getChild(SAMTOOLS_PATH);
			if(validElement(samtoolsPath)){
				this.setSamtoolsPath(Utils.getRUbioSeqFile(samtoolsPath.getValue()));
			}

			Element picardPath = configData.getChild(PICARD_PATH);
			if(validElement(picardPath)){
				this.setPicardPath(Utils.getRUbioSeqFile(picardPath.getValue()));
			}
			
			Element MACSPath = configData.getChild(MACS_PATH);
			if(validElement(MACSPath)){
				this.setMacsPath(Utils.getRUbioSeqFile(MACSPath.getValue()));
			}

			Element PythonPath = configData.getChild(PYTHON_PATH);
			if(validElement(PythonPath)){
				this.setPythonPath(Utils.getRUbioSeqFile(PythonPath.getValue()));
			}
			
			Element fastqcPath = configData.getChild(FASTQC_PATH);
			if(validElement(fastqcPath)){
				this.setFastqcPath(Utils.getRUbioSeqFile(fastqcPath.getValue()));
			}
			
			Element BEDToolsPath = configData.getChild(BED_TOOLS_PATH);
			if(validElement(BEDToolsPath)){
				this.setBedtoolsPath(Utils.getRUbioSeqFile(BEDToolsPath.getValue()));
			}
			
			Element BedgraphtoWigPath = configData.getChild(BGTW_PATH);
			if(validElement(BedgraphtoWigPath)){
				this.setBedgraphtobigwigPath(Utils.getRUbioSeqFile(BedgraphtoWigPath.getValue()));
			}
			
			Element IDRPath = configData.getChild(IDR_PATH);
			if(validElement(IDRPath)){
				this.setIdrPath(Utils.getRUbioSeqFile(IDRPath.getValue()));
			}
			
			Element PeakAnnotatorPath = configData.getChild(PEAKANNOTATOR_PATH);
			if(validElement(PeakAnnotatorPath)){
				this.setPeakannotatorPath(Utils.getRUbioSeqFile(PeakAnnotatorPath.getValue()));
			}
			
			Element CCATPath = configData.getChild(CCAT_PATH);
			if(validElement(CCATPath)){
				this.setCcatPath(Utils.getRUbioSeqFile(CCATPath.getValue()));
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
			System.err.println("\n\n\t QUEUE_SYSTEM = " + queueSystem + "\n");
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
		Comment comment = new Comment(COMMENT_CHIPSEQ);
		configurationFile.addContent(comment);

		Element configData = new Element(CONFIG_DATA);
		configurationFile.setRootElement(configData);

		Element bwaPath = new Element(BWA_PATH);
		bwaPath.addContent(this.getBwaPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(bwaPath);
		
		Element samtoolsPath = new Element(SAMTOOLS_PATH);
		samtoolsPath.addContent(this.getSamtoolsPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(samtoolsPath);
		
		Element picardPath = new Element(PICARD_PATH);
		picardPath.addContent(this.getPicardPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(picardPath);
		
		Element MACSPath = new Element(MACS_PATH);
		MACSPath.addContent(this.getMacsPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(MACSPath);
		
		Element PythonPath = new Element(PYTHON_PATH);
		PythonPath.addContent(this.getPythonPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(PythonPath);
		
		Element BEDToolsPath = new Element(BED_TOOLS_PATH);
		BEDToolsPath.addContent(this.getBedtoolsPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(BEDToolsPath);
		
		Element CCATPath = new Element(CCAT_PATH);
		CCATPath.addContent(this.getCcatPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(CCATPath);
		
		Element BedgraphtoWigPath = new Element(BGTW_PATH);
		BedgraphtoWigPath.addContent(this.getBedgraphtobigwigPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(BedgraphtoWigPath);
		
		Element IDRPath = new Element(IDR_PATH);
		IDRPath.addContent(this.getIdrPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(IDRPath);
		
		Element PeakAnnotatorPath = new Element(PEAKANNOTATOR_PATH);
		PeakAnnotatorPath.addContent(this.getPeakannotatorPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(PeakAnnotatorPath);
		
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
//		xmlOutput.output(configurationFile, new FileWriter(new File(output.getAbsolutePath()+".test")));
	}
}

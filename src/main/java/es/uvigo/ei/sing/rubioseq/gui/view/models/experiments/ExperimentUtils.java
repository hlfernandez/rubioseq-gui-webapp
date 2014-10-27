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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import es.uvigo.ei.sing.rubioseq.ExperimentType;

/**
 * 
 * @author hlfernandez
 *
 */
public class ExperimentUtils {
	
	public static final String BRANCH = "branch";
	public static final String BRANCH_SNV = "SNV";
	public static final String BRANCH_CNV = "CNV";
	public static final String BRANCH_METHYLATION = "Methylation";
	public static final String BRANCH_CHIPSEQ = "ChipSeq";
	private static final String BRANCH_MISSING_MESSAGE = "The attribute \'branch\' of the \'configData\' "
			+ "element is missing. Please, update your XML to add this attribute which indicates the RUbioSeq branch "
			+ "of this configuration file.\n\n "
			+ "Allowed values:"
			+ "\n\t<configData branch=\"SNV\">"
			+ "\n\t<configData branch=\"CNV\"> "
			+ "\n\t<configData branch=\"Methylation\">"
			+ "\n\t<configData branch=\"ChipSeq\"> ";
	
//	public static ExperimentType getExperimentType(File inputFile){
//		if (inputFile.isDirectory() 
//				|| !inputFile.canRead()
//				|| !inputFile.getName().endsWith(".xml")) {
//			return null;
//		}
//		SAXBuilder saxBuilder = new SAXBuilder();
//		try {
//			Document document = saxBuilder.build(inputFile);
//			Element configData = document.getRootElement(); 
//			
//			Element methylType = configData.getChild(MethylationExperiment.METHYLTYPE);
//			if(methylType!=null){
//				return ExperimentType.Methylation;
//			} else{
//				Element csExperiment = configData.getChild(ChipSeqExperiment.EXPERIMENT);
//				if(csExperiment!=null){
//					return ExperimentType.CHIPSeq;
//				} else{
//					Element genomes1000 = configData.getChild(SingleNucleotideVariantExperiment.GENOMES1000ANNOT);
//					if(genomes1000!=null){
//						return ExperimentType.SNV;
//					} else{
//						return ExperimentType.CNV;
//					}
//				}
//			}
//		} catch (JDOMException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public static ExperimentType getExperimentType(File inputFile) throws InvalidRubioSeqParameterException {
		if (inputFile.isDirectory() || !inputFile.canRead()
				|| !inputFile.getName().endsWith(".xml")) {
			return null;
		}
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document document = saxBuilder.build(inputFile);
			Element configData = document.getRootElement();
			String branch = configData
					.getAttributeValue(ExperimentUtils.BRANCH);
			if(branch == null){
				throw new InvalidRubioSeqParameterException(ExperimentUtils.BRANCH_MISSING_MESSAGE);
			} else{
				if (branch.equals(BRANCH_METHYLATION)) {
					return ExperimentType.Methylation;
				} else if (branch.equals(BRANCH_SNV)) {
					return ExperimentType.SNV;
				} else if (branch.equals(BRANCH_CNV)) {
					return ExperimentType.CNV;
				} else if (branch.equals(BRANCH_CHIPSEQ)) {
					return ExperimentType.CHIPSeq;
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

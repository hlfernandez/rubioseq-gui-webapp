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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.Measurable;

public class ExperimentStagesTest {

	@Test
	public void testSingleNucleotideVariantExperimentStages() {
		SingleNucleotideVariantExperiment experiment = new SingleNucleotideVariantExperiment();
		experiment.getSamples().add(new Sample());
		
		experiment.settCFlag(0);
		assertEquals(
			4,
			getStagesCount(0, experiment)
		);

		for(int level = 1; level <= 4; level ++) {
			assertEquals(
				1,
				getStagesCount(level, experiment)
			);
		}
		
		experiment.settCFlag(1);
		assertEquals(
			4,
			getStagesCount(0, experiment)
		);
		
		for(int level = 1; level <= 4; level ++) {
			assertEquals(
				1,
				getStagesCount(level, experiment)
			);
		}		
		
		experiment.getSamples().add(new Sample());
		assertEquals(
			5,
			getStagesCount(0, experiment)
		);
		
		for(int level = 1; level <= 3; level ++) {
			assertEquals(
				1,
				getStagesCount(level, experiment)
			);
		}
		
		assertEquals(
			2,
			getStagesCount(4, experiment)
		);		
	}
	
	@Test
	public void testCopyNumberVariationExperimentStages() {
		CopyNumberVariationExperiment experiment = new CopyNumberVariationExperiment();
		assertEquals(
			3,
			getStagesCount(0, experiment)
		);
	}
	
	@Test
	public void testChipSeqExperimentStages() {
		ChipSeqExperiment experiment = new ChipSeqExperiment();
		assertEquals(
			4,
			getStagesCount(0, experiment)
		);
	}
	
	@Test
	public void testMethylationExperimentStages() {
		MethylationExperiment experiment = new MethylationExperiment();
		assertEquals(
			2,
			getStagesCount(0, experiment)
		);
		
		for(int level = 1; level <= 3; level++) {
			assertEquals(
				1,
				getStagesCount(level, experiment)
			);	
		}
		
		experiment.setIntervalsPath(new RUbioSeqFile(null, null));
		assertEquals(
			3,
			getStagesCount(0, experiment)
		);
		
		for(int level = 1; level <= 3; level++) {
			assertEquals(
				1,
				getStagesCount(level, experiment)
			);	
		}
	}	
	
	private static final int getStagesCount(int executionLevel,
			Measurable experiment) {
		return experiment.getStagesCount(executionLevel);
	}
}

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
package es.uvigo.ei.sing.rubioseq.gui.view.models.progress;

import static java.lang.Integer.valueOf;
import static org.junit.Assert.*;

import org.junit.Test;

import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.RUbioSeqEvent.EventType;

public class RUbioSeqEventTest {

	@Test
	public void testVariantLevel1StartEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Sample Align::STEP_STARTS::Wed Jul  1 11:00:59 2015", 
			"log_S1_SRR397777.txt"
		);
		assertEquals(EventType.STEP_STARTS, event.getType());
		assertEquals(valueOf(1), event.getExecutionLevel());
		assertEquals("Sample Align", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}

	@Test
	public void testVariantLevel1EndEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Sample Align::STEP_ENDS::Wed Jul  1 11:06:50 2015", 
			"log_S1_SRR397777.txt"
		);
		assertEquals(EventType.STEP_ENDS, event.getType());
		assertEquals(valueOf(1), event.getExecutionLevel());
		assertEquals("Sample Align", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}

	@Test
	public void testMethylationLevel1TrimmingStartEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Trimming::STEP_STARTS::Wed Jul  1 11:00:59 2015", 
			"log_S1_trim_SRR397777.txt"
		);
		assertEquals(EventType.STEP_STARTS, event.getType());
		assertEquals(valueOf(1), event.getExecutionLevel());
		assertEquals("Trimming", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}
	
	@Test
	public void testMethylationLevel1TrimmingEndEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Trimming::STEP_ENDS::Wed Jul  1 11:06:50 2015", 
			"log_S1_trim_SRR397777.txt"
		);
		assertEquals(EventType.STEP_ENDS, event.getType());
		assertEquals(valueOf(1), event.getExecutionLevel());
		assertEquals("Trimming", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}

	@Test
	public void testVariantLevel2StartEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::GATK first step::STEP_STARTS::Wed Jul  1 11:06:50 2015", 
			"log_S2_SRR397777.txt"
		);
		assertEquals(EventType.STEP_STARTS, event.getType());
		assertEquals(valueOf(2), event.getExecutionLevel());
		assertEquals("GATK first step", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}
	
	@Test
	public void testVariantLevel2EndEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::GATK first step::STEP_ENDS::Wed Jul  1 11:08:31 2015", 
			"log_S2_SRR397777.txt"
		);
		assertEquals(EventType.STEP_ENDS, event.getType());
		assertEquals(valueOf(2), event.getExecutionLevel());
		assertEquals("GATK first step", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}
	
	
	@Test
	public void testMethylationLevel2FormattingStartEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Formatting methylation files::STEP_STARTS::Wed Jul  1 11:06:50 2015", 
			"log_S2_format_SRR397777.txt"
		);
		assertEquals(EventType.STEP_STARTS, event.getType());
		assertEquals(valueOf(2), event.getExecutionLevel());
		assertEquals("Formatting methylation files", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}
	
	@Test
	public void testMethylationLevel2FormattingEndEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Formatting methylation files::STEP_ENDS::Wed Jul  1 11:08:31 2015", 
			"log_S2_format_SRR397777.txt"
		);
		assertEquals(EventType.STEP_ENDS, event.getType());
		assertEquals(valueOf(2), event.getExecutionLevel());
		assertEquals("Formatting methylation files", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}
	
	@Test
	public void testVariantLevel3StartEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Calling and filtering::STEP_STARTS::Wed Jul  1 11:09:32 2015", 
			"log_S3_SRR397777.txt"
		);
		assertEquals(EventType.STEP_STARTS, event.getType());
		assertEquals(valueOf(3), event.getExecutionLevel());
		assertEquals("Calling and filtering", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}
	
	@Test
	public void testVariantLevel3EndEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Calling and filtering::STEP_ENDS::Wed Jul  1 11:09:32 2015", 
			"log_S3_SRR397777.txt"
		);
		assertEquals(EventType.STEP_ENDS, event.getType());
		assertEquals(valueOf(3), event.getExecutionLevel());
		assertEquals("Calling and filtering", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}
	
	@Test
	public void testVariantLevel4StartEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Post-analysis::STEP_STARTS::Wed Jul  1 11:09:32 2015", 
			"log_S4.txt"
		);
		assertEquals(EventType.STEP_STARTS, event.getType());
		assertEquals(valueOf(4), event.getExecutionLevel());
		assertEquals("Post-analysis", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
	}
	
	@Test
	public void testVariantLevel4EndEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Post-analysis::STEP_ENDS::Wed Jul  1 11:09:32 2015", 
			"log_S4.txt"
		);
		assertEquals(EventType.STEP_ENDS, event.getType());
		assertEquals(valueOf(4), event.getExecutionLevel());
		assertEquals("Post-analysis", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
	}

	@Test
	public void testChipseqLevel4StartEventFromControlFile() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::IDR Analysis::STEP_STARTS::Wed Jul  1 11:09:32 2015", 
			"log_S4_control_Experiment1.txt"
		);
		assertEquals(EventType.STEP_STARTS, event.getType());
		assertEquals(valueOf(4), event.getExecutionLevel());
		assertEquals("IDR Analysis", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("Experiment1", event.getSampleName());
	}
	
	@Test
	public void testChipseqLevel4EndEventFromControlFile() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::IDR Analysis::STEP_ENDS::Wed Jul  1 11:09:32 2015", 
			"log_S4_control_Experiment1.txt"
		);
		assertEquals(EventType.STEP_ENDS, event.getType());
		assertEquals(valueOf(4), event.getExecutionLevel());
		assertEquals("IDR Analysis", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("Experiment1", event.getSampleName());
	}

	@Test
	public void testVariantLevelExtraTCStartEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::TC analysis::STEP_STARTS::Wed Jun 18 15:30:28 2014", 
			"log_Int_expe.txt"
		);
		assertEquals(EventType.STEP_STARTS, event.getType());
		assertEquals(valueOf(-1), event.getExecutionLevel());
		assertEquals("TC analysis", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
	}
	
	@Test
	public void testVariantLevelExtraTCEndEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::TC analysis::STEP_ENDS::Wed Jun 18 15:30:28 2014", 
			"log_Int_expe.txt"
		);
		assertEquals(EventType.STEP_ENDS, event.getType());
		assertEquals(valueOf(-1), event.getExecutionLevel());
		assertEquals("TC analysis", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
	}

	@Test
	public void testVariantLevelExtraConcatStartEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Concat Samples::STEP_STARTS::Wed Jun 18 15:30:28 2014", 
			"log_CON_SRR397777.txt"
		);
		assertEquals(EventType.STEP_STARTS, event.getType());
		assertEquals(valueOf(-1), event.getExecutionLevel());
		assertEquals("Concat Samples", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}
	
	@Test
	public void testVariantLevelExtraConcatEndEvent() throws Exception {
		RUbioSeqEvent event = new RUbioSeqEvent(
			"RUBIOSEQ_EVENT::1/1::Concat Samples::STEP_ENDS::Wed Jun 18 15:30:28 2014", 
			"log_CON_SRR397777.txt"
		);
		assertEquals(EventType.STEP_ENDS, event.getType());
		assertEquals(valueOf(-1), event.getExecutionLevel());
		assertEquals("Concat Samples", event.getMessage());
		assertEquals(valueOf(1), event.getTotalSteps());
		assertEquals(valueOf(1), event.getStep());
		assertEquals("SRR397777", event.getSampleName());
	}
}

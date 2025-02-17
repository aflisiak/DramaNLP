package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.quadrama.io.tei.GerDraCorReader;
import de.unistuttgart.quadrama.io.tei.TextgridTEIUrlReader;

public class TestSpeakerAssignmentRules {

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(GerDraCorReader.class, TextgridTEIUrlReader.PARAM_INPUT,
						"src/test/resources/tei/tx4z.0.xml", TextgridTEIUrlReader.PARAM_REMOVE_XML_ANNOTATIONS, true),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_USE_DOCUMENT_ID, true,
						XmiWriter.PARAM_TARGET_LOCATION, "src/test/resources/SpeakerAssignmentRules/"));

		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(GerDraCorReader.class, TextgridTEIUrlReader.PARAM_INPUT,
						"src/test/resources/tei/w3zd.0.xml", TextgridTEIUrlReader.PARAM_REMOVE_XML_ANNOTATIONS, true),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_USE_DOCUMENT_ID, true,
						XmiWriter.PARAM_TARGET_LOCATION, "src/test/resources/SpeakerAssignmentRules/"));

		new File("src/test/resources/SpeakerAssignmentRules/typesystem.xml").delete();
	}

	@Test
	public void testRules1() throws Exception {
		org.apache.uima.fit.pipeline.JCasIterator iter = SimplePipeline.iteratePipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/SpeakerAssignmentRules/tx4z.0.xmi", XmiReader.PARAM_LENIENT, true),
				AnalysisEngineFactory.createEngineDescription(FigureReferenceAnnotator.class),
				AnalysisEngineFactory.createEngineDescription(SpeakerAssignmentRules.class,
						SpeakerAssignmentRules.PARAM_RULE_FILE_URL,
						new File("src/test/resources/SpeakerAssignmentRules/speaker-assignment-mapping.tsv").toURI()
								.toURL()))
				.iterator();
		if (iter.hasNext()) {
			JCas jcas = iter.next();
			for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
				if (speaker.getCoveredText().equalsIgnoreCase("carlos"))
					assertNotNull(speaker.getCoveredText(), speaker.getFigure());
			}
		}
	}

	@Test
	public void testRules2() throws Exception {
		org.apache.uima.fit.pipeline.JCasIterator iter = SimplePipeline.iteratePipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/SpeakerAssignmentRules/w3zd.0.xmi", XmiReader.PARAM_LENIENT, true),
				AnalysisEngineFactory.createEngineDescription(FigureReferenceAnnotator.class),
				AnalysisEngineFactory.createEngineDescription(SpeakerAssignmentRules.class,
						SpeakerAssignmentRules.PARAM_RULE_FILE_URL,
						new File("src/test/resources/SpeakerAssignmentRules/speaker-assignment-mapping.tsv").toURI()
								.toURL()))
				.iterator();
		assertTrue(iter.hasNext());
		JCas jcas = iter.next();
		assertNotNull(jcas);
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			if (speaker.getCoveredText().equalsIgnoreCase("der capitain"))
				assertNotNull(speaker.getCoveredText(), speaker.getFigure());
		}
	}

	public void testRules3() throws Exception {
		org.apache.uima.fit.pipeline.JCasIterator iter = SimplePipeline.iteratePipeline(
				CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,
						"src/test/resources/SpeakerAssignmentRules/w3zd.0.xmi", XmiReader.PARAM_LENIENT, true),
				AnalysisEngineFactory.createEngineDescription(FigureReferenceAnnotator.class),
				AnalysisEngineFactory.createEngineDescription(SpeakerAssignmentRules.class,
						SpeakerAssignmentRules.PARAM_RULE_FILE_URL,
						"https://raw.githubusercontent.com/quadrama/metadata/780d8dce562e8df39b91c485955d91fdc7af44ca/speaker-assignment-mapping.csv"))
				.iterator();
		assertTrue(iter.hasNext());
		JCas jcas = iter.next();
		assertNotNull(jcas);
		for (Speaker speaker : JCasUtil.select(jcas, Speaker.class)) {
			if (speaker.getCoveredText().equalsIgnoreCase("der capitain"))
				assertNotNull(speaker.getCoveredText(), speaker.getFigure());
		}
	}

}

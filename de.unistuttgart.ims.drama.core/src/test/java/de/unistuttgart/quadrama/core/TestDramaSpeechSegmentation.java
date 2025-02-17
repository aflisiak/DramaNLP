package de.unistuttgart.quadrama.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.unistuttgart.quadrama.io.tei.GerDraCorReader;
import de.unistuttgart.quadrama.io.tei.TextgridTEIUrlReader;

public class TestDramaSpeechSegmentation {
	JCas jcas;

	AnalysisEngineDescription desc;

	@Before
	public void setUp() throws UIMAException, SAXException, IOException {

		jcas = JCasFactory.createJCas();
		XmiCasDeserializer.deserialize(getClass().getResourceAsStream("/DramaSpeechSegmenter/rfxf.0.xmi"),
				jcas.getCas(), true);
		jcas.setDocumentLanguage("de");

		desc = D.getWrappedSegmenterDescription(LanguageToolSegmenter.class);
	}

	@Test
	public void testSegmentation() throws ResourceInitializationException, AnalysisEngineProcessException {

		SimplePipeline.runPipeline(jcas, desc);

		assertTrue(JCasUtil.exists(jcas, Token.class));
		assertEquals("Unbegreiflich", JCasUtil.selectByIndex(jcas, Token.class, 0).getCoveredText());
		assertEquals("ruhig", JCasUtil.selectByIndex(jcas, Token.class, 20).getCoveredText());
	}

	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		SimplePipeline.runPipeline(
				CollectionReaderFactory.createReaderDescription(GerDraCorReader.class, TextgridTEIUrlReader.PARAM_INPUT,
						"src/test/resources/tei/rfxf.0.xml", TextgridTEIUrlReader.PARAM_REMOVE_XML_ANNOTATIONS, true),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_USE_DOCUMENT_ID, true,
						XmiWriter.PARAM_TARGET_LOCATION, "src/test/resources/DramaSpeechSegmenter"));

	}
}

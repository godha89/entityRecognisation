package com.shopping.portal.nlp;

import java.util.Properties;
import java.util.stream.Collectors;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class CoreNlpPoc {

	static String text = "Hi Gowri,\r\n" + 
			"I have shared you the profile for senior software developer position in your company.\r\n" + 
			"Please review that and let me know if the profile suits the requirement.\r\n" + 
			"\r\n" + 
			"Thanks & Regards,\r\n" + 
			"Saurabh Godha\r\n" + 
			"saurabh.godha@barclays.com\r\n" + 
			"BTCI India,\r\n" + 
			"+919977051782";
	
	
	
	
	
	public static void main(String[] args) {
		// Create a document. No computation is done yet.
		
		Properties props = new Properties();
		//props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Document doc = new Document(
				text);
		
		for (Sentence sent : doc.sentences()) { // Will iterate over two sentences
			// We're only asking for words -- no need to load any models yet
			System.out.println("The second word of the sentence '" + sent + "' is " + sent.word(1));
			// When we ask for the lemma, it will load and run the part of speech tagger
			System.out.println("The third lemma of the sentence '" + sent + "' is " + sent.lemma(2));
			// When we ask for the parse, it will load and run the parser
			System.out.println("The parse of the sentence '" + sent + "' is " + sent.parse());
			// ...
		}
		
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		CoreDocument coredoc = new CoreDocument(
				text);
		
		pipeline.annotate(coredoc );
		ner(pipeline, coredoc);
	}

	public void annotateExample() {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER,
		// parsing, and coreference resolution
		Properties props = new Properties();
		//props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		props.setProperty("annotators", "ner");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);


		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		
		// run all Annotators on this text
		pipeline.annotate(document);

	}
	
	public static void ner(StanfordCoreNLP pipeline, CoreDocument doc) {
		// make an example document

		
	    // annotate the document
	    // view results
	    System.out.println("---");
	    System.out.println("entities found");
	    for (CoreEntityMention em : doc.entityMentions())
	      System.out.println("\tdetected entity: \t"+em.text()+"\t"+em.entityType()+"\t" + em.entity());
	    System.out.println("---");
	    System.out.println("tokens and ner tags");
	    String tokensAndNERTags = doc.tokens().stream().map(token -> "("+token.word()+","+token.ner()+")").collect(
	        Collectors.joining(" "));
	    System.out.println(tokensAndNERTags);
	}

}

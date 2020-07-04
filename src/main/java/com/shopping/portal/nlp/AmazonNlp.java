package com.shopping.portal.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectDominantLanguageRequest;
import com.amazonaws.services.comprehend.model.DetectDominantLanguageResult;
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DominantLanguage;
import com.amazonaws.services.comprehend.model.Entity;
import com.amazonaws.services.comprehend.model.EntityType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.portal.nlp.model.EntityMapping;

public class AmazonNlp {

	static String text_1 = "Hi Gowri,\r\n"
			+ "I have shared you the profile for senior software developer position last week in your company based in India.\r\n"
			+ "Please review that and let me know if the profile suits the requirement.\r\n" + "\r\n"
			+ "Thanks & Regards,\r\n" + "Saurabh Godha\r\n" + "saurabh.godha@barclays.com\r\n" + " Barclays,\r\n"
			+ "+919977051782";
	
	static String text = "Hi Melissa,\r\n" + 
			"Can you please share the JD on godha89@abd.co.in for these positions?\r\n" + 
			"I am based in India, working in Barclays would be requiring sponsorship to work in US.\r\n" + 
			"--\r\n" + 
			"Thanks & Regards,\r\n" + 
			"Saurabh Godha\r\n" + 
			"Cell: +919977051782";

	public static void main(String[] args) {

		// Initialize Comprehand
		AWSCredentials creds = new BasicAWSCredentials("AKIAV2XCYOUDEN2ZXFKK",
				"L59gQxWOnBtc1TJ7eRB678eo3iQUcIIy/p05ByZj");
		AWSCredentialsProvider awsCreds = new AWSStaticCredentialsProvider(creds);

		AmazonComprehend comprehendClient = AmazonComprehendClientBuilder.standard().withCredentials(awsCreds)
				.withRegion(Regions.AP_SOUTHEAST_1).build();

		AmazonNlp nlp = new AmazonNlp();

		// Get possible languages for text
		String[] languages = nlp.detectLanguage(comprehendClient, text);
		List<EntityMapping> mappingList = new ArrayList<EntityMapping>();
		
		for (int i = 0; i < languages.length; i++) {
			String language = languages[i];
			System.out.println("Calling DetectEntities for language " + language);
			mappingList.addAll(nlp.fetchEntitites(comprehendClient, language, text));
		}
		
		 ObjectMapper objectMapper = new ObjectMapper();
	      try {
	         String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mappingList);
	         System.out.println(json);
	      } catch(Exception e) {
	         e.printStackTrace();
	      }

		// Call detectEntities API

	}

	private String[] detectLanguage(AmazonComprehend comprehendClient, String inputText) {

		// Call detectDominantLanguage API
		System.out.println("Calling DetectDominantLanguage");
		DetectDominantLanguageRequest detectDominantLanguageRequest = new DetectDominantLanguageRequest()
				.withText(inputText);
		DetectDominantLanguageResult detectDominantLanguageResult = comprehendClient
				.detectDominantLanguage(detectDominantLanguageRequest);
		List<DominantLanguage> languages = detectDominantLanguageResult.getLanguages();
		String[] languageList = new String[languages.size()];
		int i = 0;
		for (DominantLanguage language : languages) {
			System.out.println("Language : " + language.getLanguageCode());
			languageList[i] = language.getLanguageCode();
			i++;
		}
		return languageList.length > 0 ? languageList : null;
	}

	private List<EntityMapping> fetchEntitites(AmazonComprehend comprehendClient, String language, String inputText) {
		DetectEntitiesRequest detectEntitiesRequest = new DetectEntitiesRequest().withText(inputText)
				.withLanguageCode(language);
		DetectEntitiesResult detectEntitiesResult = comprehendClient.detectEntities(detectEntitiesRequest);
		// detectEntitiesResult.getEntities().forEach(System.out::println);
		List<Entity> entities = detectEntitiesResult.getEntities();
		
		List<EntityMapping> entityMappings = new ArrayList<EntityMapping>();
		
		for (Entity entity : entities) {
			EntityMapping mapping = new EntityMapping();
			System.out.println("Entity Text: " + entity.getText() + " entity Type: " + entity.getType());
			mapping.setText(entity.getText().trim());
			
			if(EntityType.OTHER.toString().equals(entity.getType())) {
				mapping.setType(validateUnknown(mapping.getText()));
			}else {
				mapping.setType(entity.getType());
			}
			
			entityMappings.add(mapping);
		}

		System.out.println("End of DetectEntities\n");
		
		return entityMappings;

	}

	private String validateUnknown(String entityText) {

		
		if(validateEmail(entityText)) {
			return "EMAIL";
		}
		
		if(validatePhoneNumber(entityText)) {
			return "PHONE";
		}
		
		return null;
	}
	
	private boolean validateEmail(String email) {
		 // Get an EmailValidator
	      EmailValidator validator = EmailValidator.getInstance();
	      // Validate an email address
	      return validator.isValid(email);
	}
	
	private boolean validatePhoneNumber(String phone) {

	String pattern = "(?:(?:\\+?([1-9]|[0-9][0-9]|[0-9][0-9][0-9])\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([0-9][1-9]|[0-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?";
    return Pattern.matches(pattern, phone);
	}

}

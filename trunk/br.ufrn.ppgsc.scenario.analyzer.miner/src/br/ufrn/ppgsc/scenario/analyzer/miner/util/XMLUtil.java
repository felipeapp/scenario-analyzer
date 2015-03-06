package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;

public abstract class XMLUtil {

	public static Document getXMLFromURL(String url) {
		SAXBuilder builder = new SAXBuilder(); 
		Document doc = null;
		
		try {
			doc = builder.build(HttpsUtil.getInputStream(url));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return doc;
	}

	public static boolean WasIssueFound(Document document, String attribute, String message) {
		for (Object obj : document.getRootElement().getChildren()) {
			String value = ((Element) obj).getAttributeValue(attribute);
			
			if (value == null)
				return true;
			
			if (value.equals(message))
				return false;
		}
		
		return true;
	}

	/*  It uses the default attributes below:
	    	id
	    	number
	    	status
			component
			affectedVersion
			creationDate
			type
			shortDescription
		You should use a map to map from default issue model to issue system model
	 */
	public static Issue getIssueFromXML(Document doc, SimpleDateFormat sdf, Map<String, String> fields) {
		Issue issue = null;

		for (Object obj : doc.getRootElement().getChildren()) {
			Element item = (Element) obj;

			issue = new Issue();

			issue.setId(Long.parseLong(item.getChildText(fields.get("id"))));
			issue.setNumber(Long.parseLong(item.getChildText(fields.get("id"))));
			
			try {
				issue.setCreationDate(sdf.parse(item.getChildText(fields.get("creationDate"))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			issue.setShortDescription(item.getChildText(fields.get("shortDescription")));
			issue.setComponent(item.getChildText(fields.get("component")));
			issue.setAffectedVersion(item.getChildText(fields.get("affectedVersion")));
			issue.setStatus(item.getChildText(fields.get("status")));
			issue.setType(item.getChildText(fields.get("type")));
			
		}

		return issue;
	}
	
	public static Issue getIssueFromRemoteXML(String url, String msg_attribute, String msg_value, SimpleDateFormat sdf, Map<String, String> fields) {
		Issue issue = null;
		Document document = XMLUtil.getXMLFromURL(url);
		
		if (XMLUtil.WasIssueFound(document, msg_attribute, msg_value))
			issue = XMLUtil.getIssueFromXML(document, sdf, fields);
		
		return issue;
	}
	
}

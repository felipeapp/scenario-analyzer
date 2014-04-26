package br.ufrn.ppgsc.scenario.analyzer.miner.argouml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.HttpsUtil;

public class ArgoUMLMiner implements IQueryIssue {

	private Properties properties;

	public ArgoUMLMiner() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("resources/argouml.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Document getXMLByIssueId(Long issueId) throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		InputStream inputStream = HttpsUtil.getInputStreamFixHttps(properties
				.getProperty("host") + issueId);
		InputStreamReader isr = new InputStreamReader(inputStream);

		LineNumberReader lineNumberReader = new LineNumberReader(isr);
		String line = null;
		StringBuilder sb = new StringBuilder();

		while ((line = lineNumberReader.readLine()) != null) {
			if (!line.contains("<!DOCTYPE")) {
				sb.append(line);
				sb.append(System.getProperty("line.separator"));
			}
		}

		StringReader sr = new StringReader(sb.toString());
		InputSource inputSource = new InputSource(sr);
		Document doc = builder.build(inputSource);
		
		return doc;
	}

	private boolean verifyIssueWasFound(Document document) {
		for (Object obj : document.getRootElement().getChildren()) {
			Element element = (Element) obj;
			if (element.getAttributeValue("status_message").trim().equals(properties.getProperty("status_message"))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Método que verifica se o argumento passado é diferente de nulo. Se não
	 * for, uma exceção é lançada.
	 * 
	 * @param issueId
	 * @throws IllegalArgumentException
	 */
	private void verifyValidArguments(Long issueId)
			throws IllegalArgumentException {
		if (issueId == null) {
			throw new IllegalArgumentException("O id da tarefa está nulo");
		}
	}
	
	private Issue getIssueInfoFromXML(Document doc) throws ParseException {
		Issue issue = null;
		SimpleDateFormat sdf = new SimpleDateFormat(properties.getProperty("dateFormat"));

		for (Object obj : doc.getRootElement().getChildren()) {
			Element item = (Element) obj;

			issue = new Issue();

			issue.setAffectedVersion(item.getChildText("version"));
			issue.setComponent("component");
			issue.setDateCreation(sdf.parse(item.getChildText("creation_ts")));
			issue.setIssueId(Integer.parseInt(item.getChildText("issue_id")));
			issue.setIssueStatus(item.getChildText("issue_status"));
			issue.setIssueType(item.getChildText("issue_type"));
			issue.setShortDescription(item.getChildText("short_desc"));
		}

		return issue;
	}

	@Override
	public Issue getIssueByNumber(long taskNumber) {
		Issue issue = null;
		try {
			verifyValidArguments(taskNumber);
			Document document = getXMLByIssueId(taskNumber);
			if (verifyIssueWasFound(document)) {
				issue = getIssueInfoFromXML(document);
			}
		} catch (IOException | JDOMException | ParseException e) {
			e.printStackTrace();
		}
		return issue;
	}

	@Override
	public List<Long> getIssueNumbersFromMessageLog(String messageLog) {
		List<Long> issuesId = new ArrayList<Long>();
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(messageLog);
		while(matcher.find()) {
			try {
				Long issueId = Long.parseLong(matcher.group());
				Document doc = getXMLByIssueId(issueId);
				if (verifyIssueWasFound(doc)) {
					issuesId.add(issueId);
				}
 			} catch (NumberFormatException | IOException | JDOMException e) {
				e.printStackTrace();
			}
		}
		return issuesId;
	}

}

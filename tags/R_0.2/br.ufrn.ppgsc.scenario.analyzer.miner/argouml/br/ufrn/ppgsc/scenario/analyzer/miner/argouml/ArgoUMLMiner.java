package br.ufrn.ppgsc.scenario.analyzer.miner.argouml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
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
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class ArgoUMLMiner implements IQueryIssue {

	private SystemMetadataUtil metadata;

	public ArgoUMLMiner() {
		metadata = SystemMetadataUtil.getInstance();
	}

	private Document getXMLByIssueId(Long issueId) throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		InputStream inputStream = HttpsUtil.getInputStreamFixHttps(metadata.getStringProperty("host") + issueId);
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
			
			if (element.getAttributeValue("status_message").trim().equals(metadata.getStringProperty("status_message")))
				return false;
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
	private void verifyValidArguments(Long issueId) throws IllegalArgumentException {
		if (issueId == null)
			throw new IllegalArgumentException("O id da tarefa está nulo");
	}

	private Issue getIssueInfoFromXML(Document doc) throws ParseException {
		Issue issue = null;
		SimpleDateFormat sdf = new SimpleDateFormat(metadata.getStringProperty("dateFormat"));

		for (Object obj : doc.getRootElement().getChildren()) {
			Element item = (Element) obj;

			issue = new Issue();

			issue.setAffectedVersion(item.getChildText("version"));
			issue.setComponent("component");
			issue.setDateCreation(sdf.parse(item.getChildText("creation_ts")));
			issue.setIssueId(Integer.parseInt(item.getChildText("issue_id")));
			issue.setNumber(Integer.parseInt(item.getChildText("issue_id")));
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
			
			if (verifyIssueWasFound(document))
				issue = getIssueInfoFromXML(document);
		} catch (IOException | JDOMException | ParseException e) {
			e.printStackTrace();
		}
		
		return issue;
	}

	@Override
	public Collection<Long> getIssueNumbersFromMessageLog(String messageLog) {
		Set<Long> result = new HashSet<Long>();

		String words = "(issue|defect|bug|task)";
		String regex1 = words + ".\\d+";
		String regex2 = words + ".\\d+.and.\\d+";
		
		Pattern pattern = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(messageLog);

		while (matcher.find()) {
			String stmt = matcher.group();
			Scanner in = new Scanner(stmt);
			
			// This read the issue word
			in.next();
			
			// this read the number after the issue word
			long issue_number = in.nextLong();
			
			result.add(issue_number);
			in.close();
		}
		
		pattern = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		matcher = pattern.matcher(messageLog);

		while (matcher.find()) {
			String stmt = matcher.group();
			Scanner in = new Scanner(stmt);
			
			// This read the issue word
			in.next();
			
			// this read the first number after the issue word
			long issue_number = in.nextLong();
			result.add(issue_number);
			
			// this read the word and between the numbers
			in.next();
			
			// this read the second number after the word and
			issue_number = in.nextLong();
			result.add(issue_number);
			
			in.close();
		}
		
		return result;
	}

	public static void main(String[] args) {
		
		String texto[] = {
			"Issue 570 -",
			"[Issue 6450]",
			"Issue 5601:",
			"Fix for issue 6436:",
			"issue 6169,",
			"issue 6169",
			"issue 5990.",
			"Part of issue 5990 that is",
			"Issue 5959:Further",
			"Issue 6032 and issue 6035",
			"For isSue 6208)",
			"Fix for issue 3990 and issue 3403: Add",
			"Defect 6358 - ",
			"Task 6348",
			"Issue 6048 and 6053",
			"Fixed issue ",
			"This consider issue 6054, issue 4034 and issue 1234",
			"This consider issue 6054, issue 4034 and 1234"
		};
		
		for (String t : texto) {
			System.out.println(t);
			
			for (long id : new ArgoUMLMiner().getIssueNumbersFromMessageLog(t))
				System.out.println("\t" + id);
		}

	}

}

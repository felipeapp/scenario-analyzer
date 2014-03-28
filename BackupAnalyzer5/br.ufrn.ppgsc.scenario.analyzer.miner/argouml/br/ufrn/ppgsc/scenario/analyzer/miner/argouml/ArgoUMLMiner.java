package br.ufrn.ppgsc.scenario.analyzer.miner.argouml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.eclipse.core.internal.dtree.ObjectNotFoundException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import br.ufrn.ppgsc.scenario.analyzer.miner.argouml.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.argouml.model.IssueStatus;
import br.ufrn.ppgsc.scenario.analyzer.miner.argouml.model.IssueType;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.HttpsUtil;

public class ArgoUMLMiner {

	private Properties properties;

	public ArgoUMLMiner() {
		properties = new Properties();
		try {
			properties
					.load(new FileInputStream("resources/argouml.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método de entrada. Recebe o id da issue como parâmetro.
	 * 
	 * @param issueId
	 * @return issue
	 */
	public Issue getIssueInfoByIssueId(Integer issueId) {
		Issue issue = null;
		try {
			verifyValidArguments(issueId);
			issue = getIssueInfo(issueId);
		} catch (IOException | JDOMException | ParseException e) {
			e.printStackTrace();
		}
		return issue;

	}

	private Issue getIssueInfo(Integer issueId) throws IOException,
			JDOMException, ParseException {
		SAXBuilder builder = new SAXBuilder();
		InputStream inputStream = HttpsUtil.getInputStreamFixHttps(properties
				.getProperty("host") + issueId);
		InputStreamReader isr = new InputStreamReader(inputStream);
		Issue issue = null;

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
		// builder.setValidation(true);
		Document doc = builder.build(inputSource);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for (Object obj : doc.getRootElement().getChildren()) {
			Element item = (Element) obj;

			verifyIssueWasFound(item);

			issue = new Issue();

			issue.setAffectedVersion(item.getChildText("version"));
			issue.setComponent("component");
			issue.setDateCreation(sdf.parse(item.getChildText("creation_ts")));
			issue.setIssueId(Integer.parseInt(item.getChildText("issue_id")));
			issue.setIssueStatus(IssueStatus.valueOf(item
					.getChildText("issue_status")));
			issue.setIssueType(IssueType.valueOf(item
					.getChildText("issue_type")));
			issue.setShortDescription(item.getChildText("short_desc"));
		}

		return issue;
	}

	private void verifyIssueWasFound(Element item)
			throws ObjectNotFoundException {
		if (item.getAttributeValue("status_message").trim().equals("NotFound")) {
			throw new ObjectNotFoundException(
					"Não existe issue para o id informado.");
		}
	}

	/**
	 * Método que verifica se o argumento passado é diferente de nulo. Se não
	 * for, uma exceção é lançada.
	 * 
	 * @param issueId
	 * @throws IllegalArgumentException
	 */
	private void verifyValidArguments(Integer issueId)
			throws IllegalArgumentException {
		if (issueId == null) {
			throw new IllegalArgumentException("O id da tarefa está nulo");
		}
	}

}

package br.ufrn.ppgsc.scenario.analyzer.cdynamic.parsers;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeGenericAnnotation;

public abstract class GenericAnnotationParser {

	public static List<GenericAnnotationParser> getAnnotationParsers(Properties properties) {
		List<GenericAnnotationParser> result = new ArrayList<GenericAnnotationParser>();

		String regex = properties.getProperty("regex");
		String parsers = properties.getProperty("annotation_parsers");

		if (parsers == null)
			return result;

		if (regex == null)
			regex = ":";

		String class_list[] = parsers.split(regex);

		for (String class_name : class_list) {
			GenericAnnotationParser parser = null;

			try {
				parser = (GenericAnnotationParser) Class.forName(class_name).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			result.add(parser);
		}

		return result;
	}

	public abstract RuntimeGenericAnnotation getRuntimeAnnotation(Member member);

}

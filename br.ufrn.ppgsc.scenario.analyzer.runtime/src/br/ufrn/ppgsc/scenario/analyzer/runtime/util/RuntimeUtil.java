package br.ufrn.ppgsc.scenario.analyzer.runtime.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.runtime.annotation.GenericAnnotationParser;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.Execution;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeGenericAnnotation;

public class RuntimeUtil {

	private static final RuntimeUtil runtimeUtil = new RuntimeUtil();
	
	private Execution execution = new Execution();
	private Map<String, RuntimeGenericAnnotation> annotations;
	private List<GenericAnnotationParser> parsers;

	private RuntimeUtil() {
		Properties props = new Properties();
		
		execution = new Execution();
		annotations = new HashMap<String, RuntimeGenericAnnotation>();
		
		try {
			props.load(new FileInputStream("runtime.properties"));
		} catch (IOException e) {
			System.err.println("Can't load runtime.properties file!");
			System.exit(0);
		}
		
		parsers = GenericAnnotationParser.getAnnotationParsers(props);
		
		String name = props.getProperty("system_name");
		String version = props.getProperty("system_version");
		
		execution.setSystemName(name == null ? "Unknown" : name);
		execution.setSystemVersion(version == null ? "Unknown" : version);
	}
	
	public static RuntimeUtil getInstance() {
		return runtimeUtil;
	}
	
	public Execution getCurrentExecution() {
		return execution;
	}

	public Set<RuntimeGenericAnnotation> parseMemberAnnotations(Member member) {
		Set<RuntimeGenericAnnotation> set = new HashSet<RuntimeGenericAnnotation>();
		
		for (GenericAnnotationParser p : parsers) {
			RuntimeGenericAnnotation ann_parsed = p.getRuntimeAnnotation(member);
			
			if (ann_parsed != null) {
				RuntimeGenericAnnotation ann_stored = annotations.get(ann_parsed.getName());
				
				if (ann_stored == null) {
					set.add(ann_parsed);
					annotations.put(ann_parsed.getName(), ann_parsed);
				}
				else {
					set.add(ann_stored);
				}
			}
		}
		
		return set;
	}

}

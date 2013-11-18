package br.ufrn.ppgsc.scenario.analyzer.d.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Reliability;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Robustness;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Security;
import br.ufrn.ppgsc.scenario.analyzer.d.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimePerformance;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeReliability;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeRobustness;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeSecurity;

public abstract class RuntimeUtil {

	private static final Execution execution = new Execution();

	private static final Map<String, RuntimeGenericAnnotation> annotations =
			new HashMap<String, RuntimeGenericAnnotation>();

	static {
		Properties props = new Properties();
		
		try {
			props.load(new FileInputStream("analyzer_info.properties"));
		} catch (IOException e) {
			System.err.println("Can't find analyzer_info.properties file!");
		}
		
		String name = props.getProperty("system_name");
		String version = props.getProperty("system_version");
		
		execution.setSystemName(name == null ? "Unknown" : name);
		execution.setSystemVersion(version == null ? "Unknown" : version);
	}
	
	public static Execution getCurrentExecution() {
		return execution;
	}

	private static RuntimePerformance getRuntimePerformanceAnnotation(Member member) {
		RuntimePerformance rp = null;
		Annotation annotation = ((AnnotatedElement) member).getAnnotation(Performance.class);

		if (annotation != null) {
			Performance pann = (Performance) annotation;

			rp = (RuntimePerformance) annotations.get(pann.name());

			if (rp == null) {
				rp = new RuntimePerformance(pann.name(), pann.limitTime());
				annotations.put(pann.name(), rp);
			}
		}

		return rp;
	}
	
	private static RuntimeSecurity getRuntimeSecurityAnnotation(Member member) {
		RuntimeSecurity rs = null;
		Annotation annotation = ((AnnotatedElement) member).getAnnotation(Security.class);

		if (annotation != null) {
			Security sann = (Security) annotation;

			rs = (RuntimeSecurity) annotations.get(sann.name());

			if (rs == null) {
				rs = new RuntimeSecurity(sann.name());
				annotations.put(sann.name(), rs);
			}
		}

		return rs;
	}
	
	private static RuntimeRobustness getRuntimeRobustnessAnnotation(Member member) {
		RuntimeRobustness rb = null;
		Annotation annotation = ((AnnotatedElement) member).getAnnotation(Robustness.class);

		if (annotation != null) {
			Robustness rann = (Robustness) annotation;

			rb = (RuntimeRobustness) annotations.get(rann.name());

			if (rb == null) {
				rb = new RuntimeRobustness(rann.name());
				annotations.put(rann.name(), rb);
			}
		}

		return rb;
	}
	
	private static RuntimeReliability getRuntimeReliabilityAnnotation(Member member) {
		RuntimeReliability rr = null;
		Annotation annotation = ((AnnotatedElement) member).getAnnotation(Reliability.class);

		if (annotation != null) {
			Reliability rann = (Reliability) annotation;

			rr = (RuntimeReliability) annotations.get(rann.name());

			if (rr == null) {
				rr = new RuntimeReliability(rann.name(), rann.failureRate());
				annotations.put(rann.name(), rr);
			}
		}

		return rr;
	}
	
	public static Set<RuntimeGenericAnnotation> parseMemberAnnotations(Member member) {
		Set<RuntimeGenericAnnotation> set = new HashSet<RuntimeGenericAnnotation>();
		
		RuntimePerformance performance = RuntimeUtil.getRuntimePerformanceAnnotation(member);
		if (performance != null)
			set.add(performance);
		
		RuntimeSecurity security = RuntimeUtil.getRuntimeSecurityAnnotation(member);
		if (security != null)
			set.add(security);
		
		RuntimeRobustness robustness = RuntimeUtil.getRuntimeRobustnessAnnotation(member);
		if (robustness != null)
			set.add(robustness);
		
		RuntimeReliability reliability = RuntimeUtil.getRuntimeReliabilityAnnotation(member);
		if (reliability != null)
			set.add(reliability);
		
		return set;
	}

}

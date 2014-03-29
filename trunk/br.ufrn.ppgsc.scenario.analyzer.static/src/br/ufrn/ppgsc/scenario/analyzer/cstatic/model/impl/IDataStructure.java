package br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.Annotation;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ClassData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.MethodData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ScenarioData;

public interface IDataStructure {

	public String getVersion();

	public void setVersion(String version);

	public void showCallGraphStats();

	public List<MethodData> getMethodInvocations(String signature);
	
	public List<MethodData> getMethodParents(String signature);
	
	public MethodData getMethodDataFromIndex(String key);

	public MethodData[] getMethodDataAsArray();

	public void addClassDataToIndex(String key, ClassData cls);

	public ClassData getClassDataFromIndex(String key);

	public List<Annotation> getAnnotations(String simple_name);

	public List<Annotation> getAnnotations(Class<? extends java.lang.annotation.Annotation> cls);

	public void addAnnotationToIndex(String simple_name, Annotation node);

	public void addAnnotationToIndex(Class<? extends java.lang.annotation.Annotation> cls, Annotation node);

	public void addScenario(ScenarioData scenario);

	public List<ScenarioData> getScenarios();

	public void printInfo();

	public void buildIndexes(IJavaProject project);

	public void buildCallGraph(IJavaProject project);

}

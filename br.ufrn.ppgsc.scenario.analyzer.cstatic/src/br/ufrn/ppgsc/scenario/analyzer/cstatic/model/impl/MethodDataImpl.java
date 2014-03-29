package br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.AbstractQAData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ClassData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.MethodData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ScenarioData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.ScenarioAnalyzerUtil;

// TODO Adicionar signature para este elemento como key
public class MethodDataImpl implements MethodData {

	/*
	 * TODO Está referência será setada apenas para o startMethod. Depois
	 * podemos considerar uma classe especializada desta para tratar o primeiro
	 * method do cenário, evitando que o atributo seja null na maioria dos casos
	 */
	private ScenarioData scenario;

	/*
	 * TODO Depois preciso ver uma forma de não colocar isso em todos os
	 * métodos. Esta key representa a versão da estrutura do grafo que deve ser
	 * usada para buscar o método
	 */
	private String version;

	private String name;
	private String signature;
	private ClassData declaringClass;
	private List<AbstractQAData> qualityAttributes;

	public MethodDataImpl() {
		qualityAttributes = new ArrayList<AbstractQAData>();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public ClassData getDeclaringClass() {
		return declaringClass;
	}

	public void setDeclaringClass(ClassData declaringClass) {
		this.declaringClass = declaringClass;
	}

	public ScenarioData getScenario() {
		return scenario;
	}

	public void setScenario(ScenarioData scenario) {
		this.scenario = scenario;
	}

	public List<MethodData> getMethodInvocations() {
		return ScenarioAnalyzerUtil.getDataStructureInstance(version).getMethodInvocations(signature);
	}

	public List<MethodData> getMethodParents() {
		return ScenarioAnalyzerUtil.getDataStructureInstance(version).getMethodParents(signature);
	}

	public List<AbstractQAData> getQualityAttributes() {
		return qualityAttributes;
	}

	public void setQualityAttributes(List<AbstractQAData> qualityAttributes) {
		this.qualityAttributes = qualityAttributes;
	}

	@Override
	public boolean equals(Object m) {
		return this.getSignature().equals(((MethodData) m).getSignature());
	}

}

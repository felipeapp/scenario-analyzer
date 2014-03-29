package br.ufrn.ppgsc.scenario.analyzer.cstatic.processors;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.JDTWALADataStructure;

public interface IAnnotationProcessor {

	public void process(JDTWALADataStructure data);

	public void addProcessorQA(Class<? extends AbstractProcessorQA> cls);

}

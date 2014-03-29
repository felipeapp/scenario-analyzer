package br.ufrn.ppgsc.scenario.analyzer.cstatic.processors;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl.IDataStructure;

public interface IAnnotationProcessor {

	public void process(IDataStructure data);

	public void addProcessorQA(Class<? extends AbstractProcessorQA> cls);

}

package br.ufrn.ppgsc.scenario.analyzer.cstatic.processors;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.AbstractQAData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.MethodData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl.IDataStructure;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.ScenarioAnalyzerUtil;

public abstract class AbstractProcessorQA {

	public final void processQA(IDataStructure data) {
		
		for (Annotation node : data.getAnnotations(getAnnotationClass())) {
			
			MethodDeclaration method_declaration = (MethodDeclaration) node.getParent();
			IMethodBinding method_binding = method_declaration.resolveBinding();
			MethodData method_data = data.getMethodDataFromIndex(
					ScenarioAnalyzerUtil.getStandartMethodSignature(method_binding));
	
			/*
			 * Se null, significa que o método é anotado por uma atributo de
			 * qualidade, mas não está presente nos casos de uso existente
			 */
			if (method_data != null) {
				AbstractQAData qa_data = createInstance();
				IAnnotationBinding ann_binding = node.resolveAnnotationBinding();
				
				qa_data.setName((String) ScenarioAnalyzerUtil.getAnnotationValue(ann_binding, "name"));
	
				method_data.getQualityAttributes().add(qa_data);
				qa_data.setMethod(method_data);
				qa_data.setType(getAnnotationClass());
	
				setExtraFields(qa_data, ann_binding);
			} else {
				System.err.println("[AbstractProcessorQA] Ignoring annotation @" + node.getTypeName().getFullyQualifiedName()
						+ " in " + method_binding.getDeclaringClass().getQualifiedName() + "." + method_binding.getName());
			}
			
		}
		
	}

	public abstract AbstractQAData createInstance();
	
	public abstract Class<? extends java.lang.annotation.Annotation> getAnnotationClass();
	
	public void setExtraFields(AbstractQAData qa_data, IAnnotationBinding ann_binding) { }
	

}

package br.ufrn.dimap.ttracker.aspects;

import java.applet.Applet;
import java.applet.AudioClip;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;

import junit.framework.TestCase;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;
import org.springframework.context.annotation.Scope;

import br.ufrn.dimap.ttracker.data.TestCoverage;
import br.ufrn.dimap.ttracker.data.TestCoverageMapping;
import br.ufrn.dimap.ttracker.data.TestData;
import br.ufrn.dimap.ttracker.data.Variable;
import br.ufrn.dimap.ttracker.util.FileUtil;

public aspect TestTracker {
	private pointcut exclusion() : !within(br.ufrn.dimap.ttracker..*) && !within(br.ufrn.dimap.rtquality..*);// && !within(junit.*);
	private pointcut beforePointcut() :
		cflow(
			within(br.ufrn.sigaa.biblioteca..*) &&
			(
				within(@Scope("request") *) ||
				within(@Scope("session") *) ||
				execution(* TestCase+.*()) ||
				@annotation(Test)
			) &&
			(
				execution(* *(..)) ||
				execution(*.new(..))
			)
		) &&
		(
			execution(* *(..)) ||
			execution(*.new(..))
		) &&
		exclusion();
	private pointcut afterPointcut() :
		within(br.ufrn.sigaa.biblioteca..*) &&
		(
			within(@Scope("request") *) ||
			within(@Scope("session") *) ||
			execution(* TestCase+.*()) ||
			@annotation(Test)
		) &&
		(
			execution(* *(..)) ||
			execution(*.new(..))
		) &&
		exclusion();
	
	before() : beforePointcut() {
		Long threadId = Thread.currentThread().getId();
		Signature signature = thisJoinPoint.getSignature();
		Member member = getMember(signature);
		loadTestCoverageMappingInstanceFromFile(member);
		TestCoverageMapping.getInstance().setCurrentRevision(FileUtil.getTestCoverageMappingRevisionByResource(member.getDeclaringClass()));
		TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
		if(testCoverage == null){
			if((isTestClassMember(member) && isTestMethod(member)) || (isManagedBeanMember(member) && isActionMethod(member))){
				testCoverage = new TestCoverage();
				TestData testData = testCoverage.getTestData();
				testData.setSignature(signature.toString()); //retorno pacote classe método parâmetros
				testData.setClassFullName(member.getDeclaringClass().getCanonicalName()); //pacote classe
				testData.setManual(!isTestClassMember(member) && isManagedBeanMember(member));
				testCoverage.addCoveredMethod(signature.toString(), getInputs(member, thisJoinPoint.getArgs()));
				TestCoverageMapping.getInstance().getTestCoverageBuilding().put(threadId, testCoverage);
				saveTestCoverageMapping(member);
			}
		}
		else {
			testCoverage.addCoveredMethod(signature.toString(), new LinkedHashSet<Variable>(0));
			saveTestCoverageMapping(member);
		}
	}

//	after() returning(Object theReturn) : afterPointcut() {
//		Long threadId = Thread.currentThread().getId();
//		Signature signature = thisJoinPoint.getSignature();
//		Member member = getMember(signature);
//		if(member != null) {
//			if(isTestClassMember(member) || isManagedBeanMember(member)){
//				if(isTestMethod(member) || isActionMethod(member)) {
//					TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
//					if(testCoverage != null) {
//						testCoverage.updateCoveredMethod(signature.toString(), getReturn(member, theReturn));
//						saveTestCoverageMapping(member);
//					}
//				}
//			}
//		}
//	}
	
	after() : afterPointcut() {
		Long threadId = Thread.currentThread().getId();
		Signature signature = thisJoinPoint.getSignature();
		Member member = getMember(signature);
		if(member != null) {
			TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
			if(testCoverage != null){
				TestData testData = testCoverage.getTestData();
				if(((!testData.isManual() && isTestClassMember(member)) ||
				(testData.isManual() && isManagedBeanMember(member) && isActionMethod(member))) &&
				testData.getSignature().equals(signature.toString())){
					TestCoverageMapping.getInstance().finishTestCoverage(threadId);
					saveTestCoverageMapping(member);
					String tcm = TestCoverageMapping.getInstance().printAllTestsCoverage();
					String resultFolder = FileUtil.getResultFolderByResource(member.getDeclaringClass());
					FileUtil.saveTextToFile(tcm, resultFolder, "tcmText", "txt"); //TODO: Utilizado para testes e debug
					try {
						AudioClip clip = Applet.newAudioClip(new URL("file:///D:/Joao/workspaces/SIGAALast/br.ufrn.dimap.ttracker/sounds/beep-06.wav"));
						clip.play();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} 
				}
			}
		}
	}
	
	private void saveTestCoverageMapping(Member member) {
		String resultFolder = FileUtil.getResultFolderByResource(member.getDeclaringClass());
		TestCoverageMapping.getInstance().setFileDirectory(resultFolder);
		String testCoverageMappingName = FileUtil.getTestCoverageMappingNameByResource(member.getDeclaringClass());
		TestCoverageMapping.getInstance().setName(testCoverageMappingName);
		TestCoverageMapping.getInstance().save(); //TODO: Após executar todos os testes e os depois os testes selecionados verificar se ambos não serão acumulados no mesmo TestCoverageMapping, se sim, desenvolver uma função clear para o TestCoverageMapping.
	}
	
	private void loadTestCoverageMappingInstanceFromFile(Member member) {
		try{
			String resultFolder = FileUtil.getResultFolderByResource(member.getDeclaringClass());
			String testCoverageMappingName = FileUtil.getTestCoverageMappingNameByResource(member.getDeclaringClass());
			Object obj = FileUtil.loadObjectFromFile(resultFolder, testCoverageMappingName, "tcm");
			if(obj != null && obj instanceof TestCoverageMapping)
				TestCoverageMapping.setInstance((TestCoverageMapping) obj);
		} catch(ClassCastException cce) {
			cce.printStackTrace();
		}
	}
	
	private LinkedHashSet<Variable> getInputs(Member member, Object[] args){
		Class<?>[] types = getParameterTypes(member);
		String name = member.getDeclaringClass().getName()+"."+member.getName()+"."+"arg";
		LinkedHashSet<Variable> inputs = new LinkedHashSet<Variable>(args.length);
		if(types.length == args.length){
			for(int i=0;i<args.length;i++) {
				String arg = "null";
				if(args[i] != null)
					arg = args[i] instanceof String ? (String) args[i] : String.valueOf(args[i].hashCode());
				inputs.add(new Variable(types[i].getName(),name+i,arg));
			}
		}
		return inputs;
	}
	
	private Variable getReturn(Member member, Object theReturnObject){
		Class<?> type = getReturnType(member);
		String name = member.getDeclaringClass().getName()+"."+member.getName()+"."+"return";
		String theReturnString = "null";
		if(theReturnObject != null)
			theReturnString = theReturnObject instanceof String ? (String) theReturnObject : String.valueOf(theReturnObject.hashCode());			
		return new Variable(type.getName(),name,theReturnString);
	}
	
	/**
	 * @param member
	 * @return
	 */
	private Class<?>[] getParameterTypes(Member member) {
		Class<?>[] types = new Class<?>[1];
		if(member instanceof Method)
			types = ((Method) member).getParameterTypes();
		else if(member instanceof Constructor)
			types = ((Constructor) member).getParameterTypes();
		return types;
	}
	
	/**
	 * @param member
	 * @return
	 */
	private Class<?> getReturnType(Member member) {
		Class<?> type = null; 
		if(member instanceof Method)
			type = ((Method) member).getReturnType();
		return type;
	}

	private Member getMember(Signature sig) {
		if (sig instanceof MethodSignature)
			return ((MethodSignature) sig).getMethod();
		else if (sig instanceof ConstructorSignature)
			return ((ConstructorSignature) sig).getConstructor();
		
		return null;
	}
	
	private boolean isManagedBeanMember(Member member) {
		Annotation anotations[] = member.getDeclaringClass().getAnnotations();
		boolean managedBean = false;
		for(Annotation annotation : anotations){
			String annotationString = annotation.toString();
			if(annotationString.startsWith("@org.springframework.context.annotation.Scope(") && (annotationString.contains("value=request") ||
					annotationString.contains("value=session"))) {
				managedBean = true;
				break;
			}
		}
		return managedBean;
	}
	
	private boolean isActionMethod(Member member){
		return !isSetOrGetOrIs(member) && !(member instanceof Constructor);
	}
	
	private boolean isSetOrGetOrIs(Member member){
		return member.getName().startsWith("set") || member.getName().startsWith("get") || member.getName().startsWith("is");
	}
	
	private boolean isPublicSetMethod(Member member){
		return Modifier.isPublic(member.getModifiers()) && member.getName().startsWith("set");
	}
	
	private boolean isTestClassMember(Member member) {
		if(member instanceof Method)
			return ((Method) member).getAnnotation(Test.class) != null || ((Method) member).getDeclaringClass().getSuperclass().equals(TestCase.class);
		return false;
	}
	
	private boolean isTestMethod(Member member) {
		if(member instanceof Method)
			return (((Method) member).getAnnotation(Test.class) != null) || (((Method) member).getName().startsWith("test") && ((Method) member).getDeclaringClass().getSuperclass().equals(TestCase.class));
		return false;
	}
	
}

package br.ufrn.dimap.ttracker.aspects;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Map;

import junit.framework.TestCase;
import org.junit.Test;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import br.ufrn.dimap.ttracker.data.TestCoverage;
import br.ufrn.dimap.ttracker.data.TestCoverageMapping;
import br.ufrn.dimap.ttracker.data.TestData;
import br.ufrn.dimap.ttracker.data.Variable;
import br.ufrn.dimap.ttracker.util.FileUtil;

public aspect TestTracker {
	private static final Integer NOTFOUND = -1;
	
	private pointcut exclusion() : !within(br.ufrn.dimap.ttracker..*) && !within(br.ufrn.dimap.rtquality..*);// && !within(junit.*);
	private pointcut teste() :
		cflow(
			(
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
	
	private pointcut beforeExecutions() :
		cflow(
			(
				within(
					@javax.context.RequestScoped* * ||
					@javax.context.ApplicationScoped* * ||
					@javax.context.ConversationScoped* * ||
					@javax.context.SessionScoped* * ||
					@javax.annotation.ManagedBean* *
				) ||
				execution(* TestCase+.*())
			)&&	
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
	
	private pointcut afterExecutions() :
		(
			within(
				@javax.context.SessionScoped* * ||
				@javax.context.RequestScoped* * ||
				@javax.context.ApplicationScoped* * ||
				@javax.context.ConversationScoped* * ||
				@javax.annotation.ManagedBean* *
			) ||
			execution(* TestCase+.*())
		) &&
		execution(public * *(..)) &&
		!(
			execution(*.new(..)) ||
			execution(* set*(..)) ||
			execution(* get*(..)) ||
			execution(* is*(..))
		) &&
		exclusion();
	
	private pointcut afterOthers() : //Este after é capaz de capturar tudo que o before é capaz, só que após a execução (excluindo construtures, sets, gets e iss)
		cflow(
			(
				within(
					@javax.context.RequestScoped* * ||
					@javax.context.ApplicationScoped* * ||
					@javax.context.ConversationScoped* * ||
					@javax.context.SessionScoped* * ||
					@javax.annotation.ManagedBean* *
				) ||
				execution(* TestCase+.*())
			)&&	
			(
				execution(* *(..)) ||
				execution(*.new(..))
			)
		) &&
		(
			execution(* *(..)) ||
			execution(*.new(..))
		) &&
		!(
			execution(*.new(..)) ||
			execution(* set*(..)) ||
			execution(* get*(..)) ||
			execution(* is*(..))
		) &&
		exclusion();
		
	before() : teste() {
		Long threadId = Thread.currentThread().getId();
		Signature signature = thisJoinPoint.getSignature();
		Member member = getMember(signature);
		loadTestCoverageMappingInstanceFromFile(member);
		TestCoverageMapping.getInstance().setCurrentRevision(FileUtil.getTestCoverageMappingRevisionByResource(member.getDeclaringClass()));
		String projectName = FileUtil.getProjectNameByResource(member.getDeclaringClass());
		TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
		if(testCoverage == null){
			if(isTestClassMember(member) || isManagedBeanMember(member)){
				testCoverage = new TestCoverage();
				if(isTestMethod(member) || isActionMethod(member)) {
					TestData testData = testCoverage.getTestData();
					testData.setSignature(projectName+"."+signature.toString()); //retorno pacote classe método parâmetros
					testData.setClassFullName(member.getDeclaringClass().getCanonicalName()); //pacote classe
					testData.setManual(!isTestClassMember(member) && isManagedBeanMember(member));
				}
				testCoverage.addCoveredMethod(projectName+"."+signature.toString(), getInputs(member, thisJoinPoint.getArgs()));
				TestCoverageMapping.getInstance().getTestCoverageBuilding().put(threadId, testCoverage);
			}
		}
		else{
			TestData testData = testCoverage.getTestData();
			if(testData.getSignature().isEmpty() && (isTestMethod(member) || isActionMethod(member))) {
				testData.setSignature(projectName+"."+signature.toString());
				testData.setClassFullName(member.getDeclaringClass().getCanonicalName());
				testData.setManual(!isTestClassMember(member) && isManagedBeanMember(member));
			}
			testCoverage.addCoveredMethod(projectName+"."+signature.toString(), getInputs(member, thisJoinPoint.getArgs()));
		}
		saveTestCoverageMapping(member);
	}
	
	after() returning(Object theReturn) : teste() {
		Long threadId = Thread.currentThread().getId();
		Signature signature = thisJoinPoint.getSignature();
		Member member = getMember(signature);
		String projectName = FileUtil.getProjectNameByResource(member.getDeclaringClass());
		TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
		if(testCoverage != null)
			testCoverage.updateCoveredMethod(projectName+"."+signature.toString(), getReturn(member, theReturn));
		saveTestCoverageMapping(member);
	}
	
	after() : teste() {
		Long threadId = Thread.currentThread().getId();
		Signature signature = thisJoinPoint.getSignature();
		Member member = getMember(signature);
		String projectName = FileUtil.getProjectNameByResource(member.getDeclaringClass());
		TestCoverageMapping tcm2 = TestCoverageMapping.getInstance();
		TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
		if(testCoverage != null){
			TestData testData = testCoverage.getTestData();
			if(((!testData.isManual() && isTestClassMember(member)) ||
			(testData.isManual() && isManagedBeanMember(member) && isActionMethod(member))) &&
			testData.getSignature().equals(projectName+"."+signature.toString())){
				TestCoverageMapping.getInstance().finishTestCoverage(threadId);
				saveTestCoverageMapping(member);
			}
		}
	}
	private void saveTestCoverageMapping(Member member) {
		String resultFolder = FileUtil.getResultFolderByResource(member.getDeclaringClass());
		TestCoverageMapping.getInstance().setFileDirectory(resultFolder);
		String testCoverageMappingName = FileUtil.getTestCoverageMappingNameByResource(member.getDeclaringClass());
		TestCoverageMapping.getInstance().setName(testCoverageMappingName);
		TestCoverageMapping.getInstance().save(); //TODO: Após executar todos os testes e os depois os testes selecionados verificar se ambos não serão acumulados no mesmo TestCoverageMapping, se sim, desenvolver uma função clear para o TestCoverageMapping.
		String tcm = TestCoverageMapping.getInstance().printAllTestsCoverage();
		FileUtil.saveTextToFile(tcm, resultFolder, "tcmText", "txt"); //TODO: Utilizado para testes e debug
	}
	
//	// Intercepta lan�amentos de exce��es
//	after() throwing(Throwable t) : teste()  {
//		Long threadId = Thread.currentThread().getId();
//		Signature signature = thisJoinPoint.getSignature();
//		Member member = getMember(signature);
//		String projectName = FileUtil.getProjectNameByResource(member.getDeclaringClass());
//		TestCoverageMapping tcm2 = TestCoverageMapping.getInstance();
//		TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
//		if(testCoverage != null){
//			TestData testData = testCoverage.getTestData();
//			if(((!testData.isManual() && isTestClassMember(member)) ||
//			(testData.isManual() && isManagedBeanMember(member) && isActionMethod(member))) &&
//			testData.getSignature().equals(projectName+"."+signature.toString())){
//				TestCoverageMapping.getInstance().finishTestCoverage(threadId);
//				Integer testCount = TestCoverageMapping.getInstance().getTestCount();
//				Integer testClassesSize = FileUtil.getTestClassesSizeByResource(member.getDeclaringClass());
//				testCoverage.print();
//				if(testClassesSize.equals(NOTFOUND) || testClassesSize.equals(testCount)){
//					String resultFolder = FileUtil.getResultFolderByResource(member.getDeclaringClass());
//					TestCoverageMapping.getInstance().setFileDirectory(resultFolder);
//					String testCoverageMappingName = FileUtil.getTestCoverageMappingNameByResource(member.getDeclaringClass());
//					TestCoverageMapping.getInstance().setName(testCoverageMappingName);
//					TestCoverageMapping.getInstance().save(); //TODO: Após executar todos os testes e os depois os testes selecionados verificar se ambos não serão acumulados no mesmo TestCoverageMapping, se sim, desenvolver uma função clear para o TestCoverageMapping.
//					String tcm = TestCoverageMapping.getInstance().printAllTestsCoverage();
//					FileUtil.saveTextToFile(tcm, resultFolder, "tcmText", "txt");
//				}
//			}
//		}
//	}
	
//	// Intercepta capturas de exce��es
//	before(Throwable t) : handler(Throwable+) && args(t) && teste() {
//		System.out.println("before catch exception");
//	}
	
//	before() : beforeExecutions() {
//		Long threadId = Thread.currentThread().getId();
//		Signature signature = thisJoinPoint.getSignature();
//		Member member = getMember(signature);
//		TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
//		if(testCoverage == null || !testCoverage.isStart()){
//			if(canCreateNewTestCoverage(testCoverage,member,thisJoinPoint.getArgs().length))
//				testCoverage = new TestCoverage();
//			if(isTestMethod(member)){
//				testCoverage.setTestMethod(isTestMethod(member));
//				testCoverage.setSignature(signature.toString());
//			}
//			else if(isManagedBeanMethod(member)){
//				if(isActionMethod(member)){
//					testCoverage.addCoveredMethod(signature.toString());
//					testCoverage.setStart(true);
//					if(thisJoinPoint.getArgs().length > 0){
//						Method method = (Method) member;
//						Class<?> classes[] = method.getParameterTypes();
//						Object parameters[] = thisJoinPoint.getArgs();
//						for(int i=0;i<thisJoinPoint.getArgs().length;i++)
//							testCoverage.getInputs().add(new Input(classes[i], getParameterName(method,i), parameters[i]));
//					}
//				}
//				else if(isPublicSetMethod(member) && thisJoinPoint.getArgs().length == 1){
//					Object parameters[] = thisJoinPoint.getArgs();
//					Method method = (Method) member;
//					Class<?> classes[] = method.getParameterTypes();
//					testCoverage.getInputs().add(new Input(classes[0], getParameterName(member), parameters[0]));
//				}
//			}
//			if(testCoverage != null)
//				TestCoverageMapping.getInstance().getTestsCoverageBuilding().put(threadId, testCoverage);
//		}
//		else{
//			testCoverage.addCoveredMethod(signature.toString());
//		}
//	}
//	
//	after() : beforeExecutions() {
//		Long threadId = Thread.currentThread().getId();
//		Signature signature = thisJoinPoint.getSignature();
//		Member member = getMember(signature);
//		TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
//	}
//	
//	after() : afterExecutions() {
//		Long threadId = Thread.currentThread().getId();
//		Signature signature = thisJoinPoint.getSignature();
//		Member member = getMember(signature);
//		TestCoverage testCoverage = TestCoverageMapping.getInstance().getOpenedTestCoverage(threadId);
//		if(testCoverage != null){
//			if(testCoverage.isTestMethod()){
//				if(isTestMethod(member) && testCoverage.getSignature().equals(signature.toString())){
//					TestCoverageMapping.getInstance().finishTestCoverage(threadId);
//					printTestCoverage(testCoverage);
//				}
//			}
//			else if(isManagedBeanMethod(member) && isActionMethod(member)) {
//				Iterator<MethodData> iterator = testCoverage.getCoveredMethods().iterator();
//				if(iterator.hasNext() && iterator.next().getSignature().equals(signature.toString())){
//					TestCoverageMapping.getInstance().finishTestCoverage(threadId);
//					printTestCoverage(testCoverage);
//				}
//			}
//		}
//	}
	
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
		LinkedHashSet<Variable> inputs = new LinkedHashSet<Variable>();
		if(types.length == args.length){
			for(int i=0;i<args.length;i++)
				inputs.add(new Variable(types[i].getName(),name+i,args[i]));
		}
		return inputs;
	}
	
	private Variable getReturn(Member member, Object theReturnObject){
		Class<?> type = getReturnType(member);
		String name = member.getDeclaringClass().getName()+"."+member.getName()+"."+"return";
		return new Variable(type.getName(),name,theReturnObject);
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
			String packageClass = annotation.annotationType().getName();
			if(packageClass.equals("javax.context.SessionScoped") || packageClass.equals("javax.context.ApplicationScoped") ||
			packageClass.equals("javax.context.ConversationScoped") || packageClass.equals("javax.context.RequestScoped") ||
			packageClass.equals("javax.annotation.ManagedBean")){
				managedBean = true;
				break;
			}
		}
		return managedBean;
	}
	
	private boolean isActionMethod(Member member){
		return isPublic(member) && !isSetOrGetOrIs(member) && !(member instanceof Constructor);
	}
	
	private boolean isPublic(Member member){
		return Modifier.isPublic(member.getModifiers());
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

	private boolean canCreateNewTestCoverage(TestCoverage testCoverage, Member member, int argsLength){
		return (testCoverage == null) && (isTestClassMember(member) || (isManagedBeanMember(member) && (isActionMethod(member) || (isPublicSetMethod(member) && argsLength == 1))));
	}
	
}

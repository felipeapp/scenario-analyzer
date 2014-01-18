package br.ufrn.dimap.rtquality.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.internal.core.JavaElementInfo;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.launching.JavaRuntime;
import org.junit.Test;

import br.ufrn.dimap.rtquality.history.ChangedAssetsMinerUtil;
import br.ufrn.dimap.rtquality.history.MethodLimit;
import br.ufrn.dimap.rtquality.history.MethodLimitBuilder;
import br.ufrn.dimap.rtquality.history.Project;
import br.ufrn.dimap.rtquality.history.SVNConfig;
import br.ufrn.dimap.rtquality.history.UpdatedLine;
import br.ufrn.dimap.rtquality.history.UpdatedMethod;
import br.ufrn.dimap.ttracker.data.Revision;
import br.ufrn.dimap.ttracker.data.TestCoverageMapping;
import br.ufrn.dimap.ttracker.util.FileUtil;

public class ProjectUtil {

	public static ClassLoader getIProjectClassLoader(IProject iProject) {
		try {
			IJavaProject iJavaProject = JavaCore.create(iProject);
			String[] classPaths = JavaRuntime.computeDefaultRuntimeClassPath(iJavaProject);
			URL[] urls = new URL[classPaths.length];
			for (int i = 0; i < classPaths.length; i++) {
				urls[i] = (new File(classPaths[i])).toURL();
				//urls[classPaths.length] = (new File("<project path>"+"<build path>"+"<package path>"+"<class name>"+".class")).toURL(); //TODO: Estudar porque não foi possível executar o test recem adicionado, será que foi adicionado no classloader de forma inforreta ou incompleta?
			}
			return new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public static List<IProject> getIProjects(SVNConfig sVNConfig, Integer revision) {
		List<IProject> iProjects = new ArrayList<IProject>(sVNConfig.getProjects().size());
		for(Project project : sVNConfig.getProjects()) {
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getProjectPath().substring(1));
			iProjects.add(iProject);
		}
		return iProjects;
	}
	
	public static List<IProject> getIProject(SVNConfig sVNConfig) {
		List<IProject> iProjects = new ArrayList<IProject>(sVNConfig.getProjects().size());
		for(Project project : sVNConfig.getProjects()) {
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getProjectPath().substring(1));
			iProjects.add(iProject);
		}
		return iProjects;
	}
	
	public static Set<String> getAllTestClasses(IProject iProject, Set<String> subPaths) throws JavaModelException {
		Set<String> testClasses = new HashSet<String>(0);
		for(IPackageFragment iPackageFragment : JavaCore.create(iProject).getPackageFragments()) {
			if(iPackageFragment.getKind() == IPackageFragmentRoot.K_SOURCE && (subPaths != null ? subPaths.contains(iPackageFragment.getParent().getPath().toString()) : true)) {
				for(ICompilationUnit iCompilationUnit : iPackageFragment.getCompilationUnits()) {
					for(IType iType : iCompilationUnit.getTypes()) {
						if(isTestClass(iType))
							testClasses.add(iType.getFullyQualifiedName());
					}
				}
			}
		}
//		Set<String> testClasses = new HashSet<String>(0);
//		for(IPackageFragmentRoot iPackageFragmentRoot : JavaCore.create(iProject).getAllPackageFragmentRoots()) {
//			if(iPackageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE)
//				testClasses.addAll(getTestClasses(iPackageFragmentRoot));
//		}
		return testClasses;
	}
	
	private static Set<String> getTestClasses(IJavaElement iJavaElement) {
		Set<String> testClasses = new HashSet<String>();
		Object info = JavaModelManager.getJavaModelManager().getInfo(iJavaElement);
		if(info != null) {
			JavaElementInfo javaElementInfo = ((JavaElementInfo) info);
			for(IJavaElement child : javaElementInfo.getChildren()) {
				if(child instanceof ICompilationUnit){
					try {
						for(IType iType : ((ICompilationUnit) child).getTypes()) {
							if(isTestClass(iType))
								testClasses.add(iType.getFullyQualifiedName());
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
				else
					testClasses.addAll(getTestClasses(child));
			}
		}
		return testClasses;
	}
	
	public static String getAClass(IProject iProject) throws JavaModelException {
		String aClass = null;
		for(IPackageFragmentRoot iPackageFragmentRoot : JavaCore.create(iProject).getAllPackageFragmentRoots()) {
			if(iPackageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
				aClass = getAClass(iPackageFragmentRoot);
				if(aClass != null)
					return aClass;
			}
		}
		return aClass;
	}
	
	private static String getAClass(IJavaElement iJavaElement) {
		String aClass = null;
		Object info = JavaModelManager.getJavaModelManager().getInfo(iJavaElement);
		if(info != null) {
			JavaElementInfo javaElementInfo = ((JavaElementInfo) info);
			for(IJavaElement child : javaElementInfo.getChildren()) {
				if(child instanceof ICompilationUnit){
					try {
						for(IType iType : ((ICompilationUnit) child).getTypes()) {
							aClass = iType.getFullyQualifiedName();
							if(aClass != null)
								return aClass;
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
				else {
					aClass = getAClass(child);
					if(aClass != null)
						return aClass;
				}
			}
		}
		return aClass;
	}

	private static boolean isTestClass(IType iType) throws JavaModelException {
		String superTypeName2 = iType.getSuperclassName();
		if(superTypeName2 != null && superTypeName2.equals("TestCase"))
			return true;
		for(IMethod iMethod : iType.getMethods()){
			for(IAnnotation iAnnotation : iMethod.getAnnotations()){
				IAnnotation teste = iMethod.getAnnotation(Test.class.getCanonicalName());
				IAnnotation arrobaTeste = iMethod.getAnnotation(Test.class.getSimpleName());
				if(iAnnotation.equals(arrobaTeste) || iAnnotation.equals(teste))
					return true;
			}
		}
		return false;
	}
	
	public static TestCoverageMapping getTestCoverageMapping(IProject iProject, String testCoverageMappingName) {
		try{
			String testClass = ProjectUtil.getAClass(iProject);
			ClassLoader iProjectClassLoader = ProjectUtil.getIProjectClassLoader(iProject);
			String loadFileDirectory = TestUtil.getSaveFileDirectory(iProjectClassLoader, testClass);
			return (TestCoverageMapping) FileUtil.loadObjectFromFile(loadFileDirectory, testCoverageMappingName, "tcm");
		} catch(ClassCastException cce) {
			cce.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static TestCoverageMapping setAllUncoveredMethods(Project project, String testCoverageMappingName) throws ClassNotFoundException {
		try{
			IProject iProject = project.getIProject();
			String aClass = ProjectUtil.getAClass(iProject);
			ClassLoader iProjectClassLoader = ProjectUtil.getIProjectClassLoader(iProject);
			String resultFolder = FileUtil.getResultFolderByResource(iProjectClassLoader.loadClass(aClass));
			TestCoverageMapping testCoverageMapping = (TestCoverageMapping) FileUtil.loadObjectFromFile(resultFolder, testCoverageMappingName, "tcm");
			//Adiciona métodos uncovered
			IJavaProject iJavaProject = JavaCore.create(iProject);
			IPackageFragment[] iPackageFragments = iJavaProject.getPackageFragments();
			for (IPackageFragment iPackageFragment : iPackageFragments) {
				if (iPackageFragment.getKind() == IPackageFragmentRoot.K_SOURCE) {
					for (ICompilationUnit iCompilationUnit : iPackageFragment.getCompilationUnits()) {
						IType[] iTypes = iCompilationUnit.getAllTypes();
						for (IType iType : iTypes) {
							IMethod[] iMethods = iType.getMethods();
							for (IMethod iMethod : iMethods) {
								char parametersTypes[][] = new char[iMethod.getParameters().length][];
								for(int i=0;i<iMethod.getParameters().length;i++)
									parametersTypes[i] = Signature.toCharArray(iMethod.getParameters()[i].getTypeSignature().toCharArray());//ElementType()lementName()TypeRoot().getElementName()ypeSignature()arameterTypes()[i].toCharArray();//toCharArray();
								String signature = new String(Signature.toCharArray(iMethod.getSignature().toCharArray(),(iType.getFullyQualifiedName()+"."+iMethod.getElementName()).toCharArray(),parametersTypes,false,true));
								int inicio = signature.lastIndexOf('(');
								int fim = signature.lastIndexOf(')');
								String parametros = signature.substring(inicio+1,fim);
								signature = signature.substring(0,inicio+1);
								String splittedParameters[] = parametros.split(" ");
								for(int i=0;i<splittedParameters.length;i+=2) {
									signature += splittedParameters[i] + ",";
								}
								signature = project.getProjectName()+"."+signature.substring(0,signature.length()-1) + ")";
								testCoverageMapping.findOrCreateMethodData(signature);
							}
						}
					}
				}
			}
			//Modifica o state dos modifieds
			
			testCoverageMapping.save();
		} catch(ClassCastException cce) {
			cce.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	} 

}

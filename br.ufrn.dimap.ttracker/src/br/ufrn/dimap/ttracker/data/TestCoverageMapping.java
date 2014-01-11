/**
 * 
 */
package br.ufrn.dimap.ttracker.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import br.ufrn.dimap.ttracker.util.FileUtil;

/**
 * @author João Guedes
 * 
 */
public class TestCoverageMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	private static TestCoverageMapping instance = new TestCoverageMapping();

	private String name;
	private Revision currentRevision;
	private Revision oldRevision;
	private Map<String, MethodData> methodPool;
	private Map<MethodState, Map<String, MethodData>> methodStatePool;
	private Map<Long, TestCoverage> testCoverageBuilding;
	private SortedSet<TestCoverage> testCoverages;
	private Integer nextId;
	private String fileDirectory;
	private Integer testCount;

	private TestCoverageMapping() {
		this.name = "TestCoverageMapping";
		this.methodPool = new HashMap<String, MethodData>();
		this.methodStatePool = new HashMap<MethodState, Map<String, MethodData>>(4);
		this.methodStatePool.put(new MethodState(true, true), new HashMap<String, MethodData>());
		this.methodStatePool.put(new MethodState(true, false), new HashMap<String, MethodData>());
		this.methodStatePool.put(new MethodState(false, true), new HashMap<String, MethodData>());
		this.methodStatePool.put(new MethodState(false, false), new HashMap<String, MethodData>());
		this.testCoverageBuilding = new HashMap<Long, TestCoverage>();
		this.testCoverages = new TreeSet<TestCoverage>();
		this.nextId = 1;
		this.fileDirectory = "C:/";
		this.testCount = 0;
	}

	public MethodData addCoveredMethod(String methodSignature) {
		MethodData methodData = findOrCreateMethodData(methodSignature);
		if (!methodData.getMethodState().isCovered()) {
			methodStatePool.get(methodData.getMethodState()).remove(methodData.getSignature());
			methodData.getMethodState().setCovered(true);
			methodStatePool.get(methodData.getMethodState()).put(methodData.getSignature(), methodData);
		}
		return methodData;
	}

	/**
	 * Atualiza o TestCoverageMapping definindo quais métodos sofreram
	 * modificações, excluindo os métodos que ainda não foram implementados (nas
	 * versões iniciais) ou excluindo os métodos que não existem mais (nas
	 * versões finais)
	 * 
	 * @param modifiedMethods
	 * @return Set<String> dos métodos que ainda são válidos
	 */
	public Set<String> setModifiedMethods(Set<String> modifiedMethods) {
		Set<String> validModifiedMethods = new HashSet<String>(modifiedMethods.size());
		for (String methodSignature : modifiedMethods) {
			MethodData methodData = findMethodData(methodSignature);
			if (methodData != null && !methodData.getMethodState().isModified()) {
				methodStatePool.get(methodData.getMethodState()).remove(methodData.getSignature());
				methodData.getMethodState().setModified(true);
				methodStatePool.get(methodData.getMethodState()).put(methodData.getSignature(), methodData);
				validModifiedMethods.add(methodSignature);
			}
		}
		return validModifiedMethods;
	}

	/**
	 * Remove, da relação de métodos modificados, os métodos que foram excluídos
	 * na versão antiga ou os métodos que foram incluídos na versão nova
	 * 
	 * @param modifiedMethods
	 * @return
	 */
	public Set<String> getValidModifiedMethods(Set<String> modifiedMethods) {
		Set<String> validModifiedMethods = new HashSet<String>(modifiedMethods.size());
		for (String methodSignature : modifiedMethods) {
			MethodData methodData = findMethodData(methodSignature);
			if (methodData != null)
				validModifiedMethods.add(methodSignature);
		}
		return validModifiedMethods;
	}

	/**
	 * Remove na versão antiga todos os métodos que foram excluídos e remove na
	 * versão nova todos os métodos que foram adicionados
	 * 
	 * @param oldTestCoverageMapping
	 * @param newTestCoverageMapping
	 */
	public static void intersection(TestCoverageMapping oldTestCoverageMapping,
			TestCoverageMapping newTestCoverageMapping) {
		Set<MethodData> oldInvalidMethods = new HashSet<MethodData>(oldTestCoverageMapping.getMethodPool().values());
		Set<MethodData> newInvalidMethods = new HashSet<MethodData>(newTestCoverageMapping.getMethodPool().values());
		oldInvalidMethods.removeAll(newTestCoverageMapping.getMethodPool().values());
		newInvalidMethods.removeAll(oldTestCoverageMapping.getMethodPool().values());
		for (MethodData methodData : oldInvalidMethods) {
			if (oldTestCoverageMapping.getMethodPool().remove(methodData.getSignature()) != null)
				oldTestCoverageMapping.getMethodStatePool().get(methodData.getMethodState())
						.remove(methodData.getSignature());
		}
		for (MethodData methodData : newInvalidMethods) {
			if (newTestCoverageMapping.getMethodPool().remove(methodData.getSignature()) != null)
				newTestCoverageMapping.getMethodStatePool().get(methodData.getMethodState())
						.remove(methodData.getSignature());
		}
	}

	/**
	 * Based on the method signature verifies if it is on the Mapping, if true
	 * return a set with all the tests that covered this method, else return an
	 * empty set.
	 * 
	 * @param methodSignature
	 * @return
	 */
	public Set<TestCoverage> getTestsCoverageByCalledMethod(String methodSignature) {
		if (methodPool.containsKey(methodSignature)) {
			return methodPool.get(methodSignature).getTestsCoverage();
		}
		return new HashSet<TestCoverage>(1);
	}

	/**
	 * Based on the set of changed methods signatures verifies if each one is on
	 * the Mapping, if true include their tests coverage on the resultant test
	 * coverage set
	 * 
	 * @return
	 */
	public Set<TestCoverage> getModifiedChangedTestsCoverage() {
		Set<TestCoverage> testsCoverage = new HashSet<TestCoverage>(0);
		Set<MethodData> modifiedChangedMethods = new HashSet<MethodData>(methodStatePool.get(
				new MethodState(true, true)).values());
		for (MethodData modifiedChangedMethod : modifiedChangedMethods) {
			testsCoverage.addAll(modifiedChangedMethod.getTestsCoverage());
		}
		return testsCoverage;
	}

	public Map<String, MethodData> getModifiedCoveredMethods(Set<String> modifiedMethods) {
		return methodStatePool.get(new MethodState(true, true));
		// Map<String,MethodData> modifiedCoveredMethods = new
		// HashMap<String,MethodData>(0);
		// for(String modifiedMethod : modifiedMethods) {
		// if(methodPool.containsKey(modifiedMethod))
		// modifiedCoveredMethods.put(modifiedMethod,
		// methodPool.get(modifiedMethod));
		// }
		// return modifiedCoveredMethods;
	}

	public Map<String, MethodData> getModifiedNotCoveredMethods(Set<String> modifiedMethods) {
		return methodStatePool.get(new MethodState(true, false));
	}

	public Map<String, MethodData> getNotModifiedCoveredMethods(Set<String> modifiedMethods) {
		return methodStatePool.get(new MethodState(false, true));
	}

	public Map<String, MethodData> getNotModifiedNotCoveredMethods(Set<String> modifiedMethods) {
		return methodStatePool.get(new MethodState(false, false));
	}

	public Set<String> getTestsFullyQuilifiedNames() {
		Set<String> fullyQuilifiedNames = new HashSet<String>(testCoverages.size());
		for (TestCoverage testCoverage : testCoverages)
			fullyQuilifiedNames.add(testCoverage.getTestData().getClassFullName());
		return fullyQuilifiedNames;
	}

	/**
	 * Based on the method signature search on the Mapping for this one, if not
	 * found creates one and return it.
	 * 
	 * @param methodSignature
	 * @return
	 */
	public MethodData findOrCreateMethodData(String methodSignature) {
		if (methodSignature != null) {
			if (methodPool.containsKey(methodSignature)) {
				return methodPool.get(methodSignature);
			} else {
				MethodData methodData = new MethodData(methodSignature);
				methodPool.put(methodData.getSignature(), methodData);
				methodStatePool.get(methodData.getMethodState()).put(methodData.getSignature(), methodData);
				return methodData;
			}
		}
		return null;
	}

	public void printMethodPool() {
		System.out
				.println("\n================================= Printing MethodPool =================================\n");
		for (MethodData methodData : methodPool.values()) {
			System.out.println("\n" + methodData.getSignature());
		}
	}

	public String printAllTestsCoverage() {
		String all = "\n================================= Printing All =================================\n\n";
		for (TestCoverage testCoverage : testCoverages) {
			all += testCoverage.getPrint()+"\n";
		}
		System.out.println(all);
		return all;
	}

	public MethodData findMethodData(String methodSignature) {
		if (methodSignature != null && methodPool.containsKey(methodSignature))
			return methodPool.get(methodSignature);
		return null;
	}

	public TestCoverage getOpenedTestCoverage(Long threadId) {
		if (threadId != null && testCoverageBuilding.containsKey(threadId)) {
			return testCoverageBuilding.get(threadId);
		}
		return null;
	}

	private TestCoverage removeOpenedTestCoverage(Long threadId) {
		if (threadId != null && testCoverageBuilding.containsKey(threadId)) {
			return testCoverageBuilding.remove(threadId);
		}
		return null;
	}

	public void finishTestCoverage(Long threadId) {
		testCoverages.add(removeOpenedTestCoverage(threadId));
		testCount++;
	}

	public void save() {
		FileUtil.saveObjectToFile(this, fileDirectory, name, "tcm");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Long, TestCoverage> getTestsCoverageBuilding() {
		return testCoverageBuilding;
	}

	public Set<TestCoverage> getTestCoverages() {
		return testCoverages;
	}

	public Integer getNextId() {
		return nextId++;
	}

	public Integer seeNextId() {
		return nextId;
	}

	public static TestCoverageMapping getInstance() {
		return instance;
	}

	public static void setInstance(TestCoverageMapping otherInstance) {
		instance = otherInstance;
	}

	public Map<String, MethodData> getMethodPool() {
		return methodPool;
	}

	public Map<MethodState, Map<String, MethodData>> getMethodStatePool() {
		return methodStatePool;
	}

	public Revision getCurrentRevision() {
		return currentRevision;
	}

	public void setCurrentRevision(Revision currentRevision) {
		if (currentRevision.getId() > this.currentRevision.getId()) {
			this.oldRevision = this.currentRevision;
			this.currentRevision = currentRevision;
		}
	}

	public Revision getOldRevision() {
		return oldRevision;
	}

	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	public Integer getTestCount() {
		return testCount;
	}

}

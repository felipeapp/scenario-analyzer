package br.ufrn.ppgsc.scenario.analyzer.miner.argouml;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;

public class ArgoUMLPathTransformer implements IPathTransformer {

	public String[] convert(String method_signature, String repository_prefix, String workcopy_prefix_v1, String workcopy_prefix_v2) {
		/*
		 * Neste caso as classes foram geradas localmente e
		 * não fazem parte do repositório, logo serão ignoradas.
		 * Simplesmente retorna null
		 */
		if (method_signature.startsWith("org.omg.uml"))
			return null;
		
		String full_path = "";
		String name = method_signature.replaceAll("[(].*[)]", "");
		
		/* 
		 * Se é um método (nome inicia com minúsculo)
		 * Isso precisa ser melhorado com um booleano nas próximas versões
		 */
		if (Character.isLowerCase(name.charAt(name.lastIndexOf('.') + 1)))
			name = name.replaceAll("[.][^.]+$", "");
		
		name = name.replaceAll("[.]", "/").replaceAll("[$].*", "");
		
		// Se for enum ainda ficará com uma barra no final que precisa ser removida
		if (name.endsWith("/"))
			name = name.substring(0, name.lastIndexOf('/'));
		
		// Refazendo o caminho de classes internas
		if (name.equals("org/argouml/profile/internal/DummyDependencyChecker"))
			name = "org/argouml/profile/internal/TestDependencyResolver";
		else if (name.equals("org/argouml/uml/diagram/static_structure/ui/PackagePortFigRect"))
			name = "org/argouml/uml/diagram/static_structure/ui/FigPackage";
		else if (name.equals("org/argouml/profile/internal/DependentString"))
			name = "org/argouml/profile/internal/TestDependencyResolver";
		else if (name.equals("org/argouml/persistence/ProfileConfigurationParser"))
			name = "org/argouml/persistence/ProfileConfigurationFilePersister";
		else if (name.equals("org/argouml/cognitive/checklist/ui/TableModelChecklist"))
			name = "org/argouml/cognitive/checklist/ui/TabChecklist";
		else if (name.equals("org/argouml/model/mdr/Registry"))
			name = "org/argouml/model/mdr/ModelEventPumpMDRImpl";
		else if (name.equals("org/argouml/uml/diagram/ui/FigAssociationEndAnnotation"))
			name = "org/argouml/uml/diagram/ui/FigAssociation";
		else if (name.equals("org/argouml/util/TokenSep"))
			name = "org/argouml/util/MyTokenizer";
		else if (name.equals("org/argouml/util/QuotedStringSeparator"))
			name = "org/argouml/util/MyTokenizer";
		else if (name.equals("org/argouml/util/ExprSeparatorWithStrings"))
			name = "org/argouml/util/MyTokenizer";
		else if (name.equals("org/argouml/cognitive/EnabledCM"))
			name = "org/argouml/cognitive/StandardCM";
		else if (name.equals("org/argouml/uml/diagram/ui/ActionNavigateUpFromDiagram"))
			name = "org/argouml/uml/diagram/ui/PropPanelDiagram";
		else if (name.equals("org/argouml/uml/diagram/ui/UMLDiagramHomeModelComboBoxModel"))
			name = "org/argouml/uml/diagram/ui/PropPanelDiagram";
		else if (name.equals("org/argouml/uml/diagram/ui/ActionSetDiagramHomeModel"))
			name = "org/argouml/uml/diagram/ui/PropPanelDiagram";
		else if (name.equals("org/argouml/cognitive/NotSnoozedCM"))
			name = "org/argouml/cognitive/StandardCM";
		else if (name.equals("org/argouml/uml/diagram/collaboration/ui/FigMessageGroup"))
			name = "org/argouml/uml/diagram/collaboration/ui/FigAssociationRole";
		// ---------------------------------------

		// Redirecionando para o projeto correto de acordo com pacote
		if (method_signature.startsWith("org.argouml.model.mdr"))
			full_path = "argouml-core-model-mdr/";
		else if (method_signature.startsWith("org.argouml.model") && belongsToTestFolder(name))
			full_path = "argouml-app/";
		else if (method_signature.startsWith("org.argouml.model"))
			full_path = "argouml-core-model/";
		else
			full_path = "argouml-app/";
		// ---------------------------------------------------------
		
		// Redirecionando para a pasta de código correta: src ou test
		if (belongsToTestFolder(name) ||
				name.equals("org/argouml/profile/ProfileMother") ||
				name.equals("org/argouml/FileHelper"))
			full_path += "tests/";
		else
			full_path += "src/";
		
		// Completa o nome do arquivo
		full_path += name + ".java";
		
		String result[] = new String[3];
		result[0] = repository_prefix + "src/" + full_path;
		result[1] = workcopy_prefix_v1 + full_path;
		result[2] = workcopy_prefix_v2 + full_path;
		
		return result;
	}
	
	private boolean belongsToTestFolder(String class_path) {
		String name = class_path.substring(class_path.lastIndexOf('/') + 1);
		
		return name.equals("ThreadHelper") || name.equals("CheckUMLModelHelper") ||
				name.equals("AbstractUMLModelElementListModel2Test") ||
				name.equals("InitializeModel") || name.startsWith("Test") ||
				name.startsWith("AbstractTest") || name.startsWith("GenericUmlObjectTest");
	}
	
}

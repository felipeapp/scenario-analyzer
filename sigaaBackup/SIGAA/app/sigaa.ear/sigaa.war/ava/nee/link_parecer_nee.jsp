<%--

	Página que exibe o link que exibe o perfil do usuário na turma virtual.
	Necessita da variável idPessoaPerfil setada com o id da pessoa cujo perfil se deseja visualizar.
	
--%>

<a4j:commandLink actionListener="#{ turmaVirtual.visualizarParecerNee }" reRender="panelNee" oncomplete="document.getElementById('bVisualizarParecerNee').click();">
	<h:graphicImage value="/img/acessibilidade.png" styleClass="hidelink" id="imgNee" title="Visualizar Parecer sobre Necessidade Educacional Especial do Aluno" />
	<f:param name="idDiscente_nee" value="#{ idDiscente_nee }" />
</a4j:commandLink>
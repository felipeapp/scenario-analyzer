<%--

	P�gina que exibe o link que exibe o perfil do usu�rio na turma virtual.
	Necessita da vari�vel idPessoaPerfil setada com o id da pessoa cujo perfil se deseja visualizar.
	
--%>

<a4j:commandLink styleClass="naoImprimir" actionListener="#{ turmaVirtual.visualizarPerfil }" reRender="basePanelPerfil" oncomplete="document.getElementById('bVisualizarPerfil').click();" title="Visualizar Perfil">
	<h:graphicImage value="/img/comprovante.png" title="Visualizar Perfil" alt="(Perfil)" />
	<f:param name="idPessoa" value="#{ idPessoaPerfil }" />
</a4j:commandLink>
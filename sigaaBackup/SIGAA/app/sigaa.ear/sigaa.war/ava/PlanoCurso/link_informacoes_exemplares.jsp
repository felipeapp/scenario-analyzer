<%--

	P�gina que exibe o link que exibe as informa��es sobre os exemplares de um livro presente no acervo da institui��o.
	Necessita da vari�vel idReferencia setada com o id da refer�ncia ao livro.
	
--%>

<a4j:commandLink actionListener="#{planoCurso.exibirExemplares}" oncomplete="document.getElementById('bVisualizarInformacoesLivro').onclick();" reRender="listaExemplares" rendered="#{ r.tituloCatalografico != null }">
	<h:graphicImage value="/img/book_blue_view_peq.png" title="Visualizar Informa��es sobre Exemplares"/>
	<f:param name="indiceIR" value="#{idReferencia}" />
</a4j:commandLink>	
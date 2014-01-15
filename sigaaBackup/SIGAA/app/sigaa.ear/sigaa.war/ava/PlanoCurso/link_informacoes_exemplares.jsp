<%--

	Página que exibe o link que exibe as informações sobre os exemplares de um livro presente no acervo da instituição.
	Necessita da variável idReferencia setada com o id da referência ao livro.
	
--%>

<a4j:commandLink actionListener="#{planoCurso.exibirExemplares}" oncomplete="document.getElementById('bVisualizarInformacoesLivro').onclick();" reRender="listaExemplares" rendered="#{ r.tituloCatalografico != null }">
	<h:graphicImage value="/img/book_blue_view_peq.png" title="Visualizar Informações sobre Exemplares"/>
	<f:param name="indiceIR" value="#{idReferencia}" />
</a4j:commandLink>	
<h2>Conte�do do T�pico de Aula</h2>
<dl>
	<dt>
		${consultaPublicaTurmas.conteudoMaterial.titulo}
		<small>(<i>Cadastrado em <fmt:formatDate value="${consultaPublicaTurmas.conteudoMaterial.dataCadastro}" pattern="dd/MM/yyyy"/></i>)</small>
	</dt>
	<dd>${consultaPublicaTurmas.conteudoMaterial.conteudo} </dd>
</dl>
<br clear="all"/>
<center>	
	<h:commandLink value="<< Voltar para os T�picos" action="#{consultaPublicaTurmas.detalhesTopicoAula}" >
		<f:param name="tid" value="#{ consultaPublicaTurmas.obj.id }" />
	</h:commandLink>
</center>	 
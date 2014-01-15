<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<h:form>

<%@include file="/ava/menu.jsp" %>

<h:messages showDetail="true" />

<fieldset>
	<legend>Fórum</legend>
	
<ul class="show">
	<li>
		<label>Título:</label>
		<div class="campo">		 
		<h:outputText value="#{forumMedio.object.titulo}" />
		</div>
	</li>
	
	<li>		
		<label>Texto: </label>
		<div class="campo">		
			${forumMedio.object.descricao}
		</div>
	</li>
	
</ul>

<div class="botoes-show" align="center">

	<h:commandLink action="#{forumMedio.editar}" value="Editar" id="btneditar">
			<f:param name="id" value="#{forumMedio.object.id}"/> |
	</h:commandLink>	
	<h:commandLink action="#{forumMedio.listar}" value="Voltar" id="btnVoltar"/>
	
</div>

</fieldset>

</h:form>
</f:view>

<%@include file="/ava/rodape.jsp" %>
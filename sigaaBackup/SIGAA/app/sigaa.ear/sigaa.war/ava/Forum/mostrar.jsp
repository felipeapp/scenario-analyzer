<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<h:messages showDetail="true" />

<fieldset>
	<legend>Fórum</legend>
	
<ul class="show">
	<li>
		<label>Título:</label>
		<div class="campo">		 
		<h:outputText value="#{forum.object.titulo}" />
		</div>
	</li>
	
	<li>		
		<label>Texto: </label>
		<div class="campo">		
			${forum.object.descricao}
		</div>
	</li>
	
</ul>

<div class="botoes-show" align="center">

	<h:commandLink action="#{forum.editar}" value="Editar">
			<f:param name="id" value="#{forum.object.id}"/> |
	</h:commandLink>	
	<h:commandLink action="#{forum.listar}" value="Voltar"/>
	
</div>

</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>

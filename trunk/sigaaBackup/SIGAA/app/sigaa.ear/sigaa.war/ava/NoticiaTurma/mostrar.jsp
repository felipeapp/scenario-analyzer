<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<h:messages showDetail="true" />

<fieldset>
	<legend>Visualização de Notícia</legend>

<ul class="form">	

	<li>
		<label>Título:</label>
		<span style="padding-left:20px;">
			<h:outputText value="#{noticiaTurma.object.descricao}" />
		</span>
	</li>
	
	<li>
		<label>Data:</label>
		<span style="padding-left:20px;">
			<fmt:formatDate value="${noticiaTurma.object.data}" pattern="dd/MM/yyyy HH:mm"/> 	 
		</span>
	</li>
	
	<li>
		<table><tr><th style="padding-left:90px;vertical-align:top;">Texto: 
		</th><td style="width:500px;vertical-align:top;">
				<div style="padding-left:20px;">
				${noticiaTurma.object.noticia}
				</div>
		</td></tr></table>
	</li>
	
</ul>
<div class="botoes-show" align="center">

	<input type="hidden" name="id" value="${ noticiaTurma.object.id }"/>

	<c:if test="${ turmaVirtual.docente }">
	<h:commandButton action="#{noticiaTurma.editar}" value="Editar" /> | 
	</c:if>	
	<h:commandButton action="#{noticiaTurma.listar}" value="<< Voltar"/>
	
</div>

</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>

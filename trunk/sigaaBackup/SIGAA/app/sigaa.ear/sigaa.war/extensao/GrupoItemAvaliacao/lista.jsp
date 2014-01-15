<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Grupo de Avaliação</h2>
	<br>

	<h:outputText value="#{grupoItemAvaliacaoExtensao.create}"/>
	
	<h:form>

	<div class="infoAltRem">
	
	
		<h:commandLink title="Cadastrar Grupo" action="#{grupoItemAvaliacaoExtensao.iniciarCadastroGrupo}">		      
		      <h:graphicImage url="/img/adicionar.gif"/> Cadastrar Grupo
		</h:commandLink>
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Grupo
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Grupo<br/>
	    
	</div>


	
	<table class="listagem">
			<caption class="listagem">Lista de Grupos de Avaliação </caption>
    <thead>
		<tr>
			<th>Tipo</th>
			<th>Nome</th>
			<th>Ativo</th>		
			<th></th>								
			<th></th>
		</tr>
	</thead>
	<tbody>

	<c:if test="${not empty grupoItemAvaliacaoExtensao.allExtensao}">
		<c:forEach items="#{grupoItemAvaliacaoExtensao.allExtensao}" var="grupo" varStatus="status">

   	       <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${(grupo.tipo)} </td>
				<td> ${grupo.denominacao} </td>
				<td> ${(grupo.ativo == true ? 'Sim': 'Não')} </td>			
				<td width="2%">
						<h:commandLink title="Alterar" action="#{grupoItemAvaliacaoExtensao.atualizar}" style="border: 0;">
						      <f:param name="id" value="#{grupo.id}"/>
						      <h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
				</td>		
				<td width="2%">
						<h:commandLink title="Remover" action="#{grupoItemAvaliacaoExtensao.preRemover}" style="border: 0;">
						      <f:param name="id" value="#{grupo.id}"/>
						      <h:graphicImage url="/img/delete.gif" />
						</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</c:if>
	
	<c:if test="${empty grupoItemAvaliacaoExtensao.allExtensao}">
	           <tr> <td> <font color="red">Não há grupos cadastrados!</font> </td></tr>
	</c:if>
		
	</tbody>
	
	
	
	</table>
	</h:form>
	
	<br/>
	
	
	
	
	<div class="infoAltRem">
		Legenda de Tipos<br/>
	</div>
	<center>	
		<b>E:</b> Grupo para Avalição de PROJETOS DE EXTENSÃO<br/>   	
   </center>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
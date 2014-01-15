<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Grupo de Avaliação</h2>
	<br>

	<h:outputText value="#{grupoItemAvaliacao.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Grupo
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Grupo<br/>
	</div>


	<h:form>
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

	<c:if test="${not empty grupoItemAvaliacao.allMonitoria}">
		<c:forEach items="#{grupoItemAvaliacao.allMonitoria}" var="grupo" varStatus="status">

   	       <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${(grupo.tipo)} </td>
				<td> ${grupo.denominacao} </td>
				<td> ${(grupo.ativo == true ? 'Sim': 'Não')} </td>			
				<td width="2%">
						<h:commandLink title="Alterar" action="#{grupoItemAvaliacao.atualizar}" style="border: 0;">
						      <f:param name="id" value="#{grupo.id}"/>
						      <h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
				</td>		
				<td width="2%">
						<h:commandLink title="Remover" action="#{grupoItemAvaliacao.preRemover}" style="border: 0;">
						      <f:param name="id" value="#{grupo.id}"/>
						      <h:graphicImage url="/img/delete.gif" />
						</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</c:if>
	
	<c:if test="${empty grupoItemAvaliacao.allMonitoria}">
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
		<b>P:</b> Grupo para Avaliação de PROJETOS DE ENSINO DE MONITORIA<br/>
	   	<b>R:</b> Grupo para Avaliação de RELATÓRIOS PARCIAIS E FINAIS DE PROJETOS<br/>	   	
   </center>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
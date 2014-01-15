<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Ação Desenvolvida</h2><br>
<h:outputText value="#{atividadeDesenvolvida.create}"/>

<h:form>
	<table class=listagem><tr>
	<caption class="listagem"> Lista de Ações Desenvolvidas</caption><td>Denominação</td>
			<td>Resultados Quantitativos</td>
			<td>Resultados Qualitativos</td>
			<td>Dificuldades</td>
			<td>Relatorio Cursos Eventos</td>
			<td>RelatorioProjetos</td>
			<td></td><td></td></tr>
	
			<c:forEach items="#{atividadeDesenvolvida.all}" var="item">
			<tr>
					<td>${item.denominacao}</td>
					<td>${item.resultadosQuantitativos}</td>
					<td>${item.resultadosQualitativos}</td>
					<td>${item.dificuldades}</td>
					<td>${item.relatorioCursosEventos}</td>
					<td>${item.relatorioProjetos}</td>
	
					<td  width="5%">

						<h:commandLink title="Alterar" action="#{atividadeDesenvolvida.atualizar}">
						         <f:param name="id" value="#{item.id}"/>
	                   			<h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>

						<h:commandLink title="Remover" action="#{atividadeDesenvolvida.preRemover}">
						         <f:param name="id" value="#{item.id}"/>
	                   			<h:graphicImage url="/img/delete.gif" />
						</h:commandLink>
						
					</td>
			</tr>
			</c:forEach>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
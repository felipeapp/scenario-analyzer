<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Detalhamento de Recursos</h2><br>

<h:outputText value="#{detalhamentoRecursos.create}"/>

<h:form>
	<table class=listagem>
		<caption class="listagem"> Lista de Detalhamento de Recursoss</caption>
			<tr>
					<td>Elemento</td>
					<td>Faex</td>
					<td>Funpec</td>
					<td>Outros</td>
					<td>Relatorio de Cursos/Eventos</td>
					<td></td>
			</tr>
		<c:forEach items="#{detalhamentoRecursos.all}" var="item">
			<tr>
					<td>${item.elemento}</td>
					<td>${item.faex}</td>
					<td>${item.funpec}</td>
					<td>${item.outros}</td>
					<td>${item.relatorioCursosEventos}</td>
					<td  width="5%">
							<h:commandLink title="Alterar" action="#{detalhamentoRecursos.atualizar}" style="border: 0;">
							         <f:param name="id" value="#{item.id}"/>
		                   			<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
							
							<h:commandLink titlte="Remover" action="#{detalhamentoRecursos.preRemover}" style="border: 0;">
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
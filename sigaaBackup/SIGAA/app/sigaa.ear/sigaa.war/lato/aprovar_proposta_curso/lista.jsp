<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Aprovar Proposta de Curso</h2>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Aprovar Proposta
	</div>
	<h:form>
	<table class="listagem">
		<caption>Lista das Propostas Submetidas</caption>
		<thead>
			<tr>
				<th>Curso</th>
				<th>Coordenador</th>
				<th></th>
			</tr>	
		</thead>
		<tbody>
			<c:forEach var="pcl" items="#{aprovarPropostaLato.propostasSubmetidas}" varStatus="count">
			<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td>${pcl.nomeCurso}</td>
				<td>${pcl.coordenador }</td>
				<td>
					<h:commandLink action="#{aprovarPropostaLato.preAprovar}">
						<f:param name="id" value="#{pcl.idCurso}" />
						<h:graphicImage url="/img/seta.gif" />
					</h:commandLink>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Aprovar Proposta de Curso</h2>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Aprovar Proposta
	</div>
	<h:form>
	<table class="listagem">
		<caption>Lista das Propostas Submetidas (${ fn:length(aprovarPropostaLato.propostasSubmetidas)})</caption>
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
				<td><font color=${pcl.coordenador == 'INATIVO' ? 'red':'black'}>${pcl.coordenador }</font></td>
				<td>
					<h:commandLink action="#{aprovarPropostaLato.preAprovar}">
						<f:param name="id" value="#{pcl.idCurso}" />
						<h:graphicImage url="/img/seta.gif" title="Aprovar Proposta"/>
					</h:commandLink>
				</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3" align="center">
					<h:commandButton action="#{aprovarPropostaLato.cancelar}" value="Cancelar" onclick="#{confirm}" id="buttonCancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
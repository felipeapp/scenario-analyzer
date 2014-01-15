<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr td.periodo {
		background: #C4D2EB;
		padding: 3px;
		font-weight: bold;
	}

</style>

<f:view>

	<h2><ufrn:subSistema /> &gt; Todas as Turmas</h2>

	<div class="infoAltRem" style="font-variant: small-caps;">
			<h:graphicImage value="/img/avancar.gif"style="overflow: visible;"/>: Acessar Turma Virtual
	</div>

<h:form>
	<h:outputText value="#{portalDiscente.create}" />
	<table class="listagem" width="80%">
		<caption> Todas as Turmas Virtuais (${ fn:length(portalDiscente.turmas) })</caption>
		<thead>
			<tr>
				<td width="60%">Disciplina</td>
				<td style="text-align:center;">Turma</td>
				<td style="text-align:right;">Alunos</td>
				<td style="text-align:right;">Créditos</td>
				<td style="text-align:center;">Horário</td>
				<td> </td>
			</tr>
		</thead>
		<c:set var="periodoAtual" value=""/>
		<c:forEach items="#{portalDiscente.turmas}" var="t" varStatus="loop">

			<c:if test="${t.turmaAgrupadora != null}">
				<c:set var="t" value="#{t.turmaAgrupadora}" />
			</c:if>

			<c:set var="periodo" value="${t.ano}.${t.periodo}"/>
			<c:if test="${ periodo != periodoAtual }">
				<c:set var="periodoAtual" value="${periodo}"/>
				<tr>
					<td class="periodo" colspan="6"> ${periodoAtual} </td>
				</tr>
			</c:if>

			<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td>${t.disciplina.codigoNome}</td>
				<td align="center">${ t.codigo }</td>
				<td align="right">${t.totalMatriculados}</td>
				<td align="right">${t.disciplina.crTotal} <small>(${t.disciplina.chTotal}h)</small> </td>
				<td align="center">${t.descricaoHorario}</td>
				<td>
					<h:commandLink action="#{turmaVirtual.entrar}" title="Acessar Turma Virtual">
						<h:graphicImage value="/img/avancar.gif"/>
						<f:param name="idTurma" value="#{ t.id }"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>

	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

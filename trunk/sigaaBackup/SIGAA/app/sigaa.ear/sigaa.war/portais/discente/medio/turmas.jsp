<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr td.ano {
		background: #C4D2EB;
		padding: 3px;
		font-weight: bold;
	}

</style>

<f:view>

	<h2><ufrn:subSistema /> &gt; Todas as Disciplinas</h2>

	<div class="infoAltRem" style="font-variant: small-caps;">
			<h:graphicImage value="/img/avancar.gif"style="overflow: visible;"/>: Acessar Disciplina Virtual
	</div>

<h:form>
	<h:outputText value="#{portalDiscenteMedio.create}" />
	<table class="listagem" width="80%">
		<caption class="listagem"> Todas as Disciplinas Virtuais (${ fn:length(portalDiscenteMedio.turmas) })</caption>
		<thead>
			<tr>
				<td width="30%">Disciplina</td>
				<td style="text-align:left;">Série</td>
				<td style="text-align:center;">Turma</td>
				<td style="text-align:left;">Situação</td>
				<td style="text-align:right;">Alunos</td>
				<td style="text-align:right;">Carga Horária</td>
				<td width="20%"style="text-align:center;">Horário</td>
				<td> </td>
			</tr>
		</thead>
		<tbody>
		<c:set var="anoAtual" value=""/>
		<c:forEach items="#{portalDiscenteMedio.turmas}" var="t" varStatus="loop">

			<c:if test="${t.turmaAgrupadora != null}">
				<c:set var="t" value="#{t.turmaAgrupadora}" />
			</c:if>

			<c:set var="ano" value="${t.ano}"/>
			<c:if test="${ ano != anoAtual }">
				<c:set var="anoAtual" value="${ano}"/>
				<tr>
					<td class="ano" colspan="8"> ${anoAtual} </td>
				</tr>
			</c:if>

			<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td>${t.disciplina.nome}</td>
				<td align="left">${ t.turmaSerie.serie.descricaoCompleta }</td>
				<td align="center">${ t.turmaSerie.nome }</td>
				<td align="left">${ t.situacaoTurma.descricao }</td>
				<td align="right">${ t.totalMatriculados }</td>
				<td align="right">${ t.disciplina.chTotal}h </td>
				<td align="center">${ t.descricaoHorario }</td>
				<td>
					<h:commandLink action="#{turmaVirtual.entrar}" title="Acessar Turma Virtual">
						<h:graphicImage value="/img/avancar.gif"/>
						<f:param name="idTurma" value="#{ t.id }"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

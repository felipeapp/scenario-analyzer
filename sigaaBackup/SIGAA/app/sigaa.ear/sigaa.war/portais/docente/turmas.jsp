<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr td.periodo {
		background: #C4D2EB;
		padding: 3px;
		font-weight: bold;
	}

</style>

<f:view>

<h2><ufrn:subSistema/> > Todas as Turmas</h2>

	<div class="infoAltRem" style="font-variant: small-caps;">
			<h:graphicImage value="/img/avancar.gif"style="overflow: visible;"/>: Acessar Turma Virtual
	</div>

<h:form>

	<table class="listagem">
	<caption>Lista de Turmas Virtuais</caption>
	<thead>
		<tr><th>Código</th><th>Disciplina</th><th>Ano/Período</th><th>Turma</th><th>Créditos</th><th>Horário</th><th></th></tr>
	</thead>
	
	<c:set var="periodoAtual" value=""/>
	<c:forEach var="t" items="#{ portalDocente.turmas }" varStatus="loop">

		<c:set var="periodo" value="${t.ano}.${t.periodo}"/>
		<c:if test="${ periodo != periodoAtual }">
			<c:set var="periodoAtual" value="${periodo}"/>
			<tr><td class="periodo" colspan="7"> ${periodoAtual} </td></tr>
		</c:if>

		<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td><h:outputText value="#{ t.disciplina.codigo }"/></td>
			<td>
				<h:commandLink action="#{turmaVirtual.entrar}" value="#{t.disciplina.nome}">
					<f:param name="idTurma" value="#{t.id}"/>
				</h:commandLink>
			</td>
			<td align="center"><h:outputText value="#{ t.anoPeriodo }"/></td>
			<td align="center"><h:outputText value="#{ t.codigo }"/></td>
			<td align="center"><h:outputText value="#{ t.disciplina.detalhes.crTotal }"/></td>
			<td><h:outputText value="#{ t.descricaoHorario }"/></td>
			<td>
				<h:commandLink action="#{turmaVirtual.entrar}">
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

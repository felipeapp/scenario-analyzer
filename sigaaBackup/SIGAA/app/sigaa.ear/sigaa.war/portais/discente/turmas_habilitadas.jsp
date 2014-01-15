<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> &gt; Todas as Turmas Habilitadas</h2>

	<div class="infoAltRem" style="font-variant: small-caps;">
		<h:graphicImage value="/img/avancar.gif"style="overflow: visible;"/>: Acessar Turma Virtual
	</div>

<h:form>
	<h:outputText value="#{portalDiscente.create}" />
	<table class="listagem" width="80%">
		<caption> Todas as Turmas Virtuais Habilitadas (${ fn:length(portalDiscente.turmasVirtuaisHabilitadas) })</caption>
		<thead>
			<tr>
				<td width="60%">Disciplina</td>
				<td style="text-align:right;">Créditos</td>
				<td style="text-align:center;">Horário</td>
				<td> </td>
			</tr>
		</thead>
		<c:forEach items="#{portalDiscente.turmasVirtuaisHabilitadas}" var="t" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "odd" : "" }">
					<td class="descricao">
						<h:commandLink id="turmasVirtuaisHabilitadas" action="#{turmaVirtual.entrar}" value="#{t.anoPeriodo} - #{t.disciplina.nome} - T#{t.codigo}">
							<f:param name="idTurma" value="#{ t.id }"/>
						</h:commandLink>
					</td>
					<td class="info"><center>${t.disciplina.crTotal}</center></td>
					<td class="info" width="35%"><center>${t.descricaoHorario}</center></td>
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

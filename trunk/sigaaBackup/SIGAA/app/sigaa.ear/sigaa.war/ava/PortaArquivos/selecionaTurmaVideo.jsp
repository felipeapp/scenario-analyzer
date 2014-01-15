<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form>
<h2><ufrn:subSistema/> &gt; Selecionar turma para enviar arquivo</h2>

<c:set var="turmasVirtuais" value="#{ portalDocente.turmasAbertas }"/>
<c:if test="${!empty turmasVirtuais}">

	<table class="listagem">
		<thead>
			<tr>
				<th>Disciplina</th>
				<th align="center">Créditos</th>
				<th align="center">Horário</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="#{turmasVirtuais}" var="t" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td class="descricao">${t.disciplina.nome}</td>
				<td class="info"><center>${t.disciplina.crTotal}</center></td>
				<td class="info" width="35%">${t.descricaoHorario}</td>
				<td>
					<h:commandLink action="#{videoTurma.selecionarTurma}">
						<f:param name="idTurma" value="#{t.id}" />
						<h:graphicImage value="/img/avancar.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</table>
</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
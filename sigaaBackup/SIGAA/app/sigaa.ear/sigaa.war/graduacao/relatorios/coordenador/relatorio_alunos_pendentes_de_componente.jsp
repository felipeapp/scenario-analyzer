<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<c:set var="relatorio" value="#{relatorioAlunosPendentesDeComponente}"/>

	<h2>${relatorio.nomeRelatorio}</h2>
	
	<div id="parametrosRelatorio">
		<table>
			<c:if test="${relatorio.curso.id > 0}">
				<tr><th>Curso:</th><td><h:outputLabel value="#{relatorio.curso.descricao}"/></td></tr>
			</c:if>
			<c:if test="${relatorio.matrizCurricular.id > 0}">
				<tr><th>Curso / Matriz Curricular:</th><td><h:outputLabel value="#{relatorio.matrizCurricular.descricao}"/></td></tr>
			</c:if>	
			<tr><th>Componente Curricular:</th><td><h:outputLabel value="#{relatorio.disciplina.descricao}"/></td></tr>
			<c:if test="${relatorio.ano != null}">
				<tr><th>Ano de Ingresso:</th><td><h:outputLabel value="#{relatorio.ano}"/></td></tr>
			</c:if>	
			<c:if test="${relatorio.periodo != null}">
				<tr><th>Período de Ingresso:</th><td><h:outputLabel value="#{relatorio.periodo}"/></td></tr>
			</c:if>	
			<c:if test="${not empty(relatorio.condicoes)}">
				<tr>
					<th rowspan="${ fn:length(relatorio.condicoes)}">Condição:</th>
					<td>
					<c:forEach items="#{relatorio.condicoes}" var="condicao">
						${condicao}<br/>
					</c:forEach>
					</td>
				</tr>
			</c:if>	
			
		</table>
	</div>
	<br/>
	<table class="relatorio" width="100%">
		<c:set var="_matriz"/>
		<c:forEach items="#{relatorio.resultados}" var="discente">
			<c:set var="matrizAtual" value="${discente.matrizCurricular.descricao}"/>
			<c:if test="${_matriz ne matrizAtual}">
				<c:set var="_matriz" value="${matrizAtual}"/>
				<thead>
					<tr>
						<th colspan="3" style="text-align:center; border-color: black; border-style: solid; border-width: 1px;" >
							<b>${discente.matrizCurricular.descricao}</b>
							<br>
						</th>
					</tr>
				</thead>
			
				<tr>
					<th style="text-align: center;font-weight: bold;">Matrícula</th>
					<th style="text-align: left;font-weight: bold;">Nome</th>
					<th style="text-align: left;font-weight: bold;">Status</th>
				</tr>
			</c:if>	
			<tbody>
				<tr>
					<td align="center"><h:outputText value="#{discente.matricula}" /></td>
					<td><h:outputText value="#{discente.nome}" /></td>
					<td><h:outputText value="#{discente.statusString}" /></td>
				</tr>
			</tbody>
		</c:forEach>
	</table>
	<br>
	<table class="relatorio" width="100%">
		<tfoot align="center">
			<tr><td align="center"><b>Total de Registros: </b>${ fn:length(relatorio.resultados)}</b></td></tr>
		</tfoot>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
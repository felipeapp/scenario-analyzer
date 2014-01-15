<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{movimentacaoAluno.create}" />
	<h2 class="title">${movimentacaoAluno.tituloOperacao} &gt; Informe os Dados</h2>

	<c:set var="discente" value="#{movimentacaoAluno.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	<h:form id="form">
	<table class="listagem">
		<caption>Selecione as matrículas que serão ${movimentacaoAluno.stricto ? 'TRANCADAS' : 'CANCELADAS' }</caption>
		<thead>
		<tr>
			<th width="2%"> </th>
			<th width="5%">  </th>
			<th> Componente </th>
			<th> Turma</th>
			<th> Status</th>
		</tr>
		</thead>
		<tbody>
			<c:forEach var="matricula" items="${movimentacaoAluno.matriculasDiscente}" varStatus="status">
			<c:if test="${not matricula.componente.subUnidade}">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td> <input type="checkbox" name="selecaoMatriculas" value="${matricula.id}" id="mat${status.index}"> </td>
					<td> ${matricula.anoPeriodo } </td>
					<td> <label for="mat${status.index}"> ${matricula.componenteDescricao}</label> </td>
					<td align="center">${matricula.turma.codigo}</td>
					<td align="center">${matricula.situacaoMatricula.descricao}</td>
				</tr>
			</c:if>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" align="center">
					<h:commandButton value="<< Dados do Trancamento" action="#{movimentacaoAluno.telaDadosMovimentacao}" id="dadosMovimentacao"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{movimentacaoAluno.cancelar}" id="cancelar" />
					<h:commandButton value="Próximo Passo >>" action="#{movimentacaoAluno.submeterSelecaoMatriculas}" id="prox"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
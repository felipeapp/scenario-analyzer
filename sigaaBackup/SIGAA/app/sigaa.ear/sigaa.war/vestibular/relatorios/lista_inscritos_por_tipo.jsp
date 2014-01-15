<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<center>
	<h3><b>Lista de <c:if
		test="${relatoriosVestibular.perfil == 1}">Discentes</c:if> <c:if
		test="${relatoriosVestibular.perfil == 2}">Servidores</c:if> Inscritos
	para Seleção de Fiscal do <h:outputText
		value="#{relatoriosVestibular.obj.nome}" /></b></h3>
	<hr />
	</center>
	<c:set var="total" value="0" />
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<tr class="header">
			<td align="left"><b>Nome</b></td>
			<td align="right"><b> <c:if
				test="${relatoriosVestibular.perfil == 1}">Curso</c:if> <c:if
				test="${relatoriosVestibular.perfil == 2}">Unidade</c:if> <c:if
				test="${relatoriosVestibular.perfil == 0}">Curso/Unidade</c:if> </b></td>
		</tr>
		<c:set var="total" value="0" />
		<c:forEach items="#{inscricoes}" var="inscricao">
			<tr class="componentes">
				<td>${inscricao.pessoa.nome}</td>
				<c:if test="${inscricao.discente != null}">
					<td align="right">${inscricao.discente.curso}</td>
				</c:if>
				<c:if test="${inscricao.servidor != null}">
					<td align="right">${inscricao.servidor.unidade.nome}</td>
				</c:if>
			</tr>
			<c:set var="total" value="${total+1}" />
		</c:forEach>
		<tr class="foot">
			<td colspan="2" align="center">Total: ${total}</td>
		<tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

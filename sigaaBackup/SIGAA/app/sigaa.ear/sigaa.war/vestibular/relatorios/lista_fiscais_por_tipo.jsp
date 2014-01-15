<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<center>
	<h3><b>Lista de Fiscais do ${processoSeletivo.nome}</b></h3>
	<hr />
	</center>
	<c:set var="total" value="0" />
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<tr class="header">
			<td align="left"><b>Nome</b></td>
			<td align="left"><b> <c:if
				test="${relatoriosVestibular.perfil == 1}">Curso</c:if> <c:if
				test="${relatoriosVestibular.perfil == 2}">Unidade</c:if> <c:if
				test="${relatoriosVestibular.perfil == 0}">Curso/Unidade</c:if> </b></td>
			<td align="center"><b>IRA / Média Geral</b></td>
			<td align="center"><b>Status</b></td>
		</tr>
		<c:set var="total" value="0" />
		<c:forEach items="#{fiscais}" var="item">
			<tr class="componentes">
				<td>${item.pessoa.nome}</td>
				<c:if test="${item.discente != null}">
					<td align="left">${item.discente.curso}</td>
					<c:if test="${item.discente.graduacao }">
						<td align="center">${item.discente.ira}</td>
					</c:if>
					<c:if test="${item.discente.stricto }">
						<td align="center">${item.discente.mediaGeral}</td>
					</c:if>
				</c:if>
				<c:if test="${item.servidor != null}">
					<td align="left">${item.servidor.unidade.nome}</td>
					<td align="center" >-</td>
				</c:if>
				<td align="center"><c:if test="${item.reserva}">Reserva</c:if>
				<c:if test="${not item.reserva}">Titular</c:if></td>
			</tr>
			<c:set var="total" value="${total+1}" />
		</c:forEach>
		<tr class="foot">
			<td colspan="2" align="center">Total: ${total}</td>
		<tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

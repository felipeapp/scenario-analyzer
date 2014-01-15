<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555; font-weight: bold; font-size: 14px;}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h:outputText value="#{ relatoriosCoordenador.create }" />
	<c:set var="relatorio" value="#{ relatoriosCoordenador.relatorio }"/>
	<c:set var="anoPeriodo" value="${ relatoriosCoordenador.ano }.${ relatoriosCoordenador.periodo }"/>
	<table width="100%">
		<caption><b>RELATÓRIO DE TURMAS CONSOLIDADAS</b></caption>
			<tr><td>&nbsp;</td></tr>
			<tr>
				<c:if test="${ acesso.chefeDepartamento || acesso.secretarioDepartamento }">
					<th style="font-weight: bold;">Unidade:</th>
						<td colspan="3" width="80%">
							${sessionScope.usuario.vinculoAtivo.unidade.nome}
						</td>
				</c:if>
				<c:if test="${ ! acesso.chefeDepartamento && !acesso.secretarioDepartamento }">								
					<th style="font-weight: bold;">Curso:</th>
						<td colspan="3" width="80%">
							${ relatoriosCoordenador.curso }
						</td>
				</c:if>
			</tr>
			<tr>
				<th style="font-weight: bold;">Ano-Período:</th>
				<td colspan="3" width="80%">
					${ anoPeriodo }
				</td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<caption><b>Turmas encontradas</b></caption>
		<c:set var="totalGeral" value="0"/>
		<thead>
			<tr>
				<td>Código</td>
				<td>Nome do Componente</td>
				<td style="text-align: right;" width="10%">Número de Turmas</td>
			</tr>
		</thead>

		<tbody>
		<c:forEach var="linha" items="#{ relatorio }" varStatus="status">
			<c:set var="totalGeral" value="#{ totalGeral + linha.value.total }"/>
			<tr class="componentes">
				<td> ${ linha.value.codigo } </td>
				<td> ${ linha.value.nome } </td>
				<td style="text-align: right;"> ${ linha.value.total } </td>
			</tr>
		</c:forEach>
		<tr class="foot">
			<td>Total:</td> <td colspan="3" style="text-align: right;">${ totalGeral }</td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

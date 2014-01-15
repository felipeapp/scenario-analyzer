<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>	
	table.listagem tr.destaque td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
<c:set value="${componenteCurricular.componentes}" var="resultado" />
		<h2>Componentes Curriculares Encontrados (${ fn:length(resultado) })</h2>
		<table class="relatorio" id="lista-turmas" width="100%">
	
			<c:if test="${empty resultado}">
				<tr><td>Nenhum componente curricular foi encontrado com os critérios utilizados</td></tr>
			</c:if>
			<c:if test="${not empty resultado}">
			<thead>
				<tr>
					<td width="10%" align="center">Código</td>
					<td width="40%" align="left">Nome</td>
					<td width="10%" align="right">CR Total</td>
					<td width="10%" align="right">CH Total</td>
					<td width="1%"></td>
					<td width="15%" align="left">Tipo</td>
				</tr>
			</thead>
			<tbody>
				<c:set var="disciplinaAtual" value="0" />
				<c:forEach items="#{componenteCurricular.componentes}" var="componente" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
						<td align="center">${componente.codigo}</td>
						<td align="left"> ${componente.detalhes.nome}</td>
						<td align="right">${componente.detalhes.crTotal}</td>
						<td align="right">${componente.detalhes.chTotal}</td>
						<td></td>
						<td align="left">${componente.tipoComponente.descricao}</td>
					</tr>
				</c:forEach>
			</tbody>
			</c:if>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

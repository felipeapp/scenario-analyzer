<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Confirma a remoção desta coordenação?')) return false" scope="request"/>
<style>
	table.listagem tr.programa td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Coordenadores de Programa de Residência</h2>
	<h:outputText value="#{coordenacaoCurso.create}" />
	<table class="listagem">
		<caption>Coordenadores de Programa de Residência</caption>
		<thead>
			<tr>
				<th> Servidor(a) </th>
				<th> Função</th>
				<th> Login</th>
				<th> E-mail</th>
				<th> Telefone Fixo</th>
				<th> Telefone Celular</th>				
			</tr>
		</thead>
		<tbody>
			<c:set var="idFiltro" value="-1" />
			<c:forEach var="coord" items="${ coordenacaoCurso.resultadosBusca}" varStatus="loop">
			
			<c:set var="idLoop" value="${coord.unidade.id}" />
			<c:if test="${coord.unidade.id != idFiltro}">
				<c:set var="idFiltro" value="${coord.unidade.id}" />
				<tr class="programa">
					<td colspan="6"> ${coord.unidade.nome} </td>
				</tr>
			</c:if>
						
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
				<td> ${coord.servidor.nome}</td>
				<td> ${coord.cargoAcademico.descricao} </td>
				<td> ${coord.servidor.primeiroUsuario.login}</td>
				<td> ${coord.emailContato}</td>
				<td> ${coord.servidor.pessoa.telefone}</td>
				<td> ${coord.servidor.pessoa.celular}</td>
				
			</tr>
			</c:forEach>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

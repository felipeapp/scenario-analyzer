<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Confirma a remoção desta secretaria?')) return false" scope="request"/>
<style>
	table.listagem tr.programa td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Coordenadores de Programa de Pós</h2>
	<h:outputText value="#{coordenacaoCurso.create}" />	
	<table class="listagem">
		<caption>Coordenadores de Programa de Pós (${fn:length(coordenacaoCurso.resultadosBusca)})</caption>
		<thead>
			<tr>
				<th> Servidor(a) </th>
				<th> Função</th>
				<!-- <th> Login</th> -->
				<th> E-mail</th>
				<th> Tel. Fixo</th>
				<th> Celular</th>				
				<th> Inicio</th>
				<th> Fim</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="idFiltro" value="-1" />
			
			<c:forEach var="coord" items="${ coordenacaoCurso.resultadosBusca}" varStatus="loop">
			
				<c:set var="idLoop" value="${coord.unidade.id}" />
				<c:if test="${coord.unidade.id != idFiltro}">
					<c:set var="idFiltro" value="${coord.unidade.id}" />
					<tr class="programa">
						<td colspan="7"> ${coord.unidade.nome} </td>
					</tr>
				</c:if>
				
				<c:if test="${coord.id ==0}">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
					<td colspan="7"> Sem Coordenador/Vice</td>
				</tr>
				</c:if>
				
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
					
					<td>
						<c:if test="${not empty coord.servidor.nome}">
							${coord.servidor.nome} 
							<c:if test="${not empty coord.servidor.primeiroUsuario.login}">
								(${coord.servidor.primeiroUsuario.login})
							</c:if>
						</c:if> 
					</td>
					
					<td> ${coord.cargoAcademico.descricaoResumido} </td>
					<%-- <td> ${coord.servidor.primeiroUsuario.login}</td> --%>
					<td> ${coord.emailContato}</td>
					<td> ${coord.servidor.pessoa.telefone}</td>
					<td> ${coord.servidor.pessoa.celular}</td>
					<td> <ufrn:format type="data" valor="${coord.dataInicioMandato}" /></td>
					<td> <ufrn:format type="data" valor="${coord.dataFimMandato}" /></td>
				</tr>
				
			</c:forEach>
			
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

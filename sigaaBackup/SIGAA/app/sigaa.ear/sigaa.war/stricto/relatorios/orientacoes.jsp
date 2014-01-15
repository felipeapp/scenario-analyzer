<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style>
	table.listagem tr.orientador td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>

	<c:if test="${not empty relatorioOrientacoes.orientacoes}">
	

		<br />
			<center>
			<h2> Relatório de Orientações do Programa </h2>
			</center>
			<c:if test="${relatorioOrientacoes.portalCoordenadorStricto}">
				<strong>Programa:</strong> 	${relatorioOrientacoes.programaStricto.nome}
			</c:if>
			<c:if test="${relatorioOrientacoes.portalPpg}">
					<c:if test="${relatorioOrientacoes.unidade.id < 0}">
						<strong>Programa:</strong> TODOS
					</c:if>		
					<c:if test="${relatorioOrientacoes.unidade.id > 0}">
						<strong>Programa:</strong> 	${relatorioOrientacoes.unidade.nome}
					</c:if>
			</c:if>
			<br/>
			<strong>Status da Orientação:</strong> 
			<c:if test="${not empty relatorioOrientacoes.ativo}">
				${relatorioOrientacoes.ativo ? 'ATIVO' : 'FINALIZADO'}
			</c:if>
			<c:if test="${empty relatorioOrientacoes.ativo}">
				TODOS
			</c:if>
			<br/>
			<strong>Status do Discente:</strong>
			<c:if test="${relatorioOrientacoes.statusDiscente.id == 0}">
				TODOS
			</c:if>
			<c:if test="${relatorioOrientacoes.statusDiscente.id != 0}">
				${ relatorioOrientacoes.statusDiscente.descricao }
			</c:if>
			<hr/>
		<table class="listagem" width="100%">
			<thead>
			<tr>
				<th style="text-align: center;"> Matricula </th>
				<th> Discente </th>
				<th> Nível </th>
				<th> Tipo de Orientação </th>
				<c:if test="${relatorioOrientacoes.statusDiscente.id == 0 }">
					<th width="12%"> Status do Aluno </th>				
				</c:if>
				<c:if test="${empty relatorioOrientacoes.ativo}">
					<th width="12%"> Status da Orientação </th>
				</c:if>
				<th> Tipo </th>
				<th> Tipo Bolsa </th>
			</tr>
			<tbody>
				<h:form>
				<c:set var="idFiltro" value="-1" />

				<c:forEach items="#{relatorioOrientacoes.orientacoes}" var="orientacao" varStatus="status">

				<c:set var="idLoop" value="${orientacao.idDocente}" />
				
				<c:if test="${ idFiltro != idLoop}">
					<c:set var="idFiltro" value="${orientacao.idDocente}" />
					<tr class="orientador">
						<td colspan="8">
							Orientador: ${orientacao.descricaoOrientador}
						</td>
					</tr>
				</c:if>

				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td style="text-align: center;">${orientacao.discente.matricula}</td>
					<td>${orientacao.discente.nome}</td>
					<td>${orientacao.discente.nivelDesc}</td>
					<td>${ orientacao.orientador ? "Orientando" : "Co-Orientando" }</td>
					<c:if test="${relatorioOrientacoes.statusDiscente.id == 0 }">
						<td width="8%">${orientacao.discente.statusString}</td>
					</c:if>
					<c:if test="${empty relatorioOrientacoes.ativo}">
						<td width="8%">${orientacao.ativo ? 'ATIVO' : 'FINALIZADO'}</td>
					</c:if>
					<td width="8%">${orientacao.discente.tipoString}</td>
					<td>${orientacao.tipoBolsa}</td>
				</tr>
				</c:forEach>

				</h:form>
			</tbody>
		</table>
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
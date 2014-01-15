<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.listagem tr.projeto td{
		background: #C8D5EC;
		font-weight: bold;
	}
</style>

<f:view>
	<h2><ufrn:subSistema /> > Relatório de Monitores do Mês</h2>

	<c:set var="historicosDiscentes" value="${comissaoMonitoria.historicosDiscentes}"/>

	<c:if test="${empty historicosDiscentes}">
		<center><i> Nenhum Discente a localizado </i></center>
	</c:if>

		<table class="listagem">
			<caption> Monitores Localizados</caption>
			
			<thead>
			<tr>
				<th> Projeto </th>
				<th> Situação</th>
				<th> Vínculo </th>
				<th> Data </th>				
			</tr>
			</thead>
			
			<tbody>
				<c:set var="projeto" value=""/>	 				
				<c:forEach items="${historicosDiscentes}" var="hist" varStatus="status">

						<c:if test="${hist.discenteMonitoria.projetoEnsino.id != projeto }">
								<c:set var="projeto" value="${ hist.discenteMonitoria.projetoEnsino.id }"/>
							<tr class="projeto">
									<td colspan="4" >
										${hist.discenteMonitoria.projetoEnsino.anoTitulo}
									</td>
							</tr>							
						</c:if>

						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td style="padding-left: 20px;">${hist.discenteMonitoria.discente.matriculaNome}</td>
							<td>${hist.situacaoDiscenteMonitoria.descricao}</td>
							<td>${hist.discenteMonitoria.tipoVinculo.descricao}</td>
							<td><fmt:formatDate value="${hist.data}" pattern="dd/MM/yyyy"/></td>							
						</tr>
					
				</c:forEach>
			</tbody>

			<tfoot>
					<tr>
						<td colspan="6" align="center">
							<input type="button" value="Imprimir" onclick="javascript:window.print()"/>		
						</td>
					</tr>
			</tfoot>
		</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
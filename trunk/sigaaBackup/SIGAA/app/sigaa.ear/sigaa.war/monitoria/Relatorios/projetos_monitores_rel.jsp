<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
	<h2><ufrn:subSistema /> > Relatório Quantitativo de Monitores Por Projetos do Centro</h2>

	<c:set var="projetos" value="${comissaoMonitoria.projetos}"/>

	<c:if test="${empty projetos}">
	<center><i> Nenhum Projeto a localizado </i></center>
	</c:if>




	<c:if test="${not empty projetos}">
		<br />
		<table class="listagem">
			<caption> Monitores Por Projeto<br/>
					${ fn:length(projetos) } Projetos 
					<c:if test="${comissaoMonitoria.checkBuscaCentro}">
						 do ${comissaoMonitoria.unidade.sigla}
					</c:if> 
					possuem ${comissaoMonitoria.total} Monitores ativos
					<c:if test="${comissaoMonitoria.checkBuscaCurso}">
						do curso de ${comissaoMonitoria.curso.descricao}
					</c:if> 		
			</caption>

			
			
			<thead>
			<tr>
				<th> Matrícula </th>
				<th> Nome </th>
				<th> Situação</th>
				<th> Vínculo </th>
				<th> </th>
			</tr>
			</thead>
			
			<tbody>
			
				<c:forEach items="${projetos}" var="projeto" varStatus="status">

					<tr class="curso">
						<td colspan="4"> ${projeto.titulo} - ${projeto.ano} - ${projeto.unidade.sigla}</td>
					</tr>

					<c:forEach items="${projeto.discentesMonitoria}" var="d" varStatus="status">
					
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td>${d.discente.matricula} </td>
							<td>${d.discente.nome}</td>
							<td>${d.situacaoDiscenteMonitoria.descricao}</td>
							<td>${d.tipoVinculo.descricao}</td>
						</tr>
					
					</c:forEach>
					
					
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
	</c:if>


							

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
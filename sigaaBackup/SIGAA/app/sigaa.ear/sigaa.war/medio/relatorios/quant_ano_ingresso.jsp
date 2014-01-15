<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>Relatório Quantitativo de Alunos por Ano Ingresso</h2>
	
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<td><b>Unidade:</b></td>
				<td>${usuario.unidade.nome}</td>
			</tr>
		</table>
	</div>

	<br />
	
	<table class="tabelaRelatorioBorda" width="60%" align="center">
	<c:set var="total_geral" value="0"/>
	<c:set var="_curso"/>
	<c:forEach items="${relatoriosMedio.lista}" var="linha" varStatus="row">
		<c:set var="cursoAtual" value="${linha.id_curso}"/>
		<c:if test="${_curso != cursoAtual}">
			<c:set var="_curso" value="${cursoAtual}"/>
			<thead>
				<tr>
					<th colspan="2" style="text-align:center; border-color: black; border-style: solid; border-width: 1px;" >
						<b>${linha.nome_curso}</b>
						<br>
					</th>
				</tr>
			</thead>
			
				<tr class="header">
					<td style="text-align: center;font-weight: bold; ">Ano de Ingresso</td>
					<td style="text-align: right;font-weight: bold; ">Quantidade de Alunos</td>
				</tr>
		</c:if>
		<tr class="componentes">
			<td style="text-align: center;">${linha.ano_ingresso}</td>
			<td align="right">${linha.total}</td>
		</tr>		
		<c:set var="total_geral" value="${total_geral + linha.total}"/>
		
	</c:forEach>
   		<tr>
   			<td colspan="2" align="center"><br><b>Total de Alunos: ${total_geral}</b></td>
   		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

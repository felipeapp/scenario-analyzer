<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
}
.destacado{
	color: red;
}
</style>
<f:view>
<c:if test="${not empty relatorioOrientacoes.orientacoes}">
	<br />
	<center><h2> Relatório Sintético de Orientações do Programa </h2></center>
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
	<strong>Situação:</strong> 
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
	<table class="listagem" width="80%">
		<thead>
		<tr>
			<th> Orientador </th>
			<th class="alinharDireita"> Total de Orientandos </th>
		</tr>
		
		<tbody>
			<c:forEach items="#{relatorioOrientacoes.orientacoes}" var="orientacao" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>${orientacao.descricaoOrientador}</td>
					<td align="right">${orientacao.total}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
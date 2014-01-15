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
</style>
<f:view>

	<h2>Relatório de Bolsistas com Mais de uma Bolsa</h2>

<div id="parametrosRelatorio">
</div>
<br/>
	<c:set value="0" var="totalRegistros"/>
	<table class="tabelaRelatorioBorda" style="font-size: 10px" align="center">
		<thead>
			<tr>
				<td class="alinharCentro">Matricula</td>
				<td>Nome</td>
				<td>Bolsas Cadastradas</td>
			</tr>
		</thead>
		<tbody>
		<c:if test="${not empty relatorioAcompanhamentoBolsas.dadosRelatorio}">
			<c:forEach items="${relatorioAcompanhamentoBolsas.dadosRelatorio}" var="linha" varStatus="indice">
				<c:set var="totalRegistros" value="${totalRegistros + 1}"/>
					<tr>
						<td align="center">${linha.matricula }</td>
						<td align="left">${linha.nome }</td>
						<td align="left">${linha.denominacao_bolsas}</td> 
					</tr>
			</c:forEach>
		</c:if>
		</tbody>
	</table>
	<br/>
	<div align="center">
		Total de registros: <ufrn:format type="valorint" valor="${totalRegistros}" />
	</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

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

	<h2>Relatório de Acompanhamento de Desempenho Acadêmico de Bolsistas</h2>

<div id="parametrosRelatorio">
<table>
	<c:if test="${ relatorioAcompanhamentoBolsas.ano > 0 }">
		<tr>
			<th>Ano-Período:</th>
			<td>${relatorioAcompanhamentoBolsas.ano}.${relatorioAcompanhamentoBolsas.periodo}</td>
		</tr>
	</c:if>
	<c:if test="${ relatorioAcompanhamentoBolsas.anoIngresso > 0 }">
		<tr>
			<th>Ano-Período Ingresso:</th>
			<td>${relatorioAcompanhamentoBolsas.anoIngresso}.${relatorioAcompanhamentoBolsas.periodoIngresso}</td>
		</tr>
	</c:if>
	<c:if test="${ relatorioAcompanhamentoBolsas.tipoBolsaAuxilio.id > 0}">
	<tr>
		<th>Tipo Bolsa:</th>
		<td>${relatorioAcompanhamentoBolsas.tipoBolsaAuxilio.denominacao}</td>
	</tr>
	</c:if>
	<c:if test="${ relatorioAcompanhamentoBolsas.situacaoBolsaAuxilio.id > 0 }">
	<tr>
		<th>Situação Bolsa:</th>
		<td>${relatorioAcompanhamentoBolsas.situacaoBolsaAuxilio.denominacao}</td>
	</tr>
	</c:if>
	
	
	<c:if test="${relatorioAcompanhamentoBolsas.somenteDestacados}">
		<tr>
			<th valign="top">Condição:</th>
			<td>Somente discentes que apresentam IECH ou IEPL abaixo da média do curso.</td>
		</tr>
	</c:if>
</table>
</div>
<br><br>
	<c:set value="0" var="totalRegistros"/>
	<c:set value="0" var="totalReprovados"/>
	<table class="tabelaRelatorioBorda" style="font-size: 10px" align="center" width="100%">
		<thead>
			<tr>
				<th class="alinharCentro" rowspan="2" width="10%">Matrícula</th>
				<th class="alinharEsquerda" rowspan="2">Nome</th>
				<th class="alinharEsquerda" rowspan="2">Bolsa Auxilio</th>
				<th colspan="2" class="alinharCentro"> IECH <sup>¹</sup></th>
				<th colspan="2" class="alinharCentro"> IEPL <sup>²</sup></th>
			</tr>
			<tr>
				<th class="alinharCentro" width="8%">Curso</th> 
				<th class="alinharCentro" width="8%">Discente</th> 
				<th class="alinharCentro" width="8%">Curso</th> 
				<th class="alinharCentro" width="8%">Discente</th> 
			</tr>			 
		</thead>
		<tbody>	
			<c:forEach items="${relatorioAcompanhamentoBolsas.dadosRelatorioIndiceAcademico}" var="linha" varStatus="indice">
				<tr style="${ ( linha.iechCurso > linha.iechDiscente || linha.ieplCurso > linha.ieplDiscente ) ? 'color: red;' : 'color: black;' }">
					<td class="alinharCentro">${ linha.discente.matricula } </td>					
					<td>${ linha.discente.pessoa.nome } </td>
					<td class="alinharEsquerda">${ linha.tipoBolsa } </td>
					<td class="alinharCentro">${ linha.iechCurso } </td>
					<td class="alinharCentro">${ linha.iechDiscente } </td>
					<td class="alinharCentro">${ linha.ieplCurso } </td>
					<td class="alinharCentro">${ linha.ieplDiscente } </td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<br/>
	
	<center>
		<b> ${ fn:length(relatorioAcompanhamentoBolsas.dadosRelatorioIndiceAcademico) } Registro(s) Encontrado(s)</b>
	</center>
	
<br>
<p align="justify">
<b>LEGENDAS:</b>
<br>
<br><b>¹</b> Índice de Eficiência em Carga Horária
<br><b>²</b> Índice de Eficiência em Períodos Letivos
<br>
<br>Discentes destacados com a cor <font color="red"><b>vermelho</b></font> significam que os mesmos estão abaixo do IEPL ou IECH do curso.
<br>Discentes com a cor <b>preta</b> significam que os mesmos estão com IEPL e IECH acima da média do curso.
</p>
<br><br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
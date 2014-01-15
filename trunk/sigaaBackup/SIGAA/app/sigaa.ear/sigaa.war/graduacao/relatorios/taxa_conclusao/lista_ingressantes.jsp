<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555; background-color: #DEDFE3;}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
<h2>Lista de Discentes que ingressaram em um determinado ano semestre<h2>
<div id="parametrosRelatorio" style="font-size: 10px;">
	<table width="100%">
			<tr>
				<th width="20%">Ano-Semestre:</th>
				<td><h:outputText value="#{relatorioTaxaConclusao.ano}"/>-<h:outputText
					value="#{relatorioTaxaConclusao.periodo}"/>
				</td>
			</tr>
	</table>
</div>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<c:set var="cursoLoop"/>
	<c:set var="matrizLoop"/>
	<c:forEach items="${relatorioTaxaConclusao.listaDiscente}" var="linha">
		<c:set var="cursoAtual" value="${linha.id_curso}"/>
		<c:set var="matrizAtual" value="${linha.modalidade_nome} - ${linha.habilitacao_nome}"/>
		
		<c:if test="${cursoLoop != cursoAtual || matrizLoop != matrizAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="matrizLoop" value="${matrizAtual}"/>
			<tr><td colspan="4">&nbsp;</td></tr>
			<tr style="border-bottom: 1px solid #555; background-color: #DEDFE3;">
				<td colspan="4">
					<b>${linha.centro} - ${linha.curso_nome} - ${linha.modalidade_nome}
					<c:if test="${not empty linha.habilitacao_nome}">- ${linha.habilitacao_nome}</c:if> 
					 - ${linha.turno_descricao}</b>
				</td>
			</tr>
			<tr>
				<td width="15%"><b>Matricula</b></td>
				<td width="63%"><b>Discente</b></td>
				<td width="12%"><b>Status</b></td>
				<td width="10%"><b>Ingresso</b></td>
			<tr>
		</c:if>
		<tr>
			<td> ${linha.matricula}</td>
			<td> ${linha.aluno_nome} </td>
			<td> ${linha.discente_status} </td>
			<td> ${linha.forma_ingresso_descricao} </td>
		</tr>
	</c:forEach>
	</table>
	<br/>
	<div style="font-size: 10px;">
		<b>Total de Registros: ${fn:length( relatorioTaxaConclusao.listaDiscente ) }</b>
	</div>
	
	<br/>
	
	<table width="60%" class="tabelaRelatorioBorda" align="center" style="font-size: 10px;">
		<thead>
			<tr>
				<th><b>Forma de Ingresso</b></th>
				<th style="text-align: right; width: 20%;"><b>Total</b></th>
			<tr>		
		</thead>
		<c:set var="total" value="0"/>
		<c:forEach items="${relatorioTaxaConclusao.lista}" var="linha">
			<tr>
				<td>${linha.descricao}</td>
				<td style="text-align: right;">${linha.total}</td>
			</tr>
			<c:set var="total" value="${total + linha.total}"/>
		</c:forEach>
		<tr>
			<th style="text-align: right;">TOTAL</th>
			<th style="text-align: right;">${total}</th>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

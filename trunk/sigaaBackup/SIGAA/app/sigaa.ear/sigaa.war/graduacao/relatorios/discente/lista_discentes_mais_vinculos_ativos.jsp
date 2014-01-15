<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<style>
	.centro{text-align: center !important;}
	.direita{text-align: right !important;}
</style>
<f:view>

<h2>Relatório de Alunos com mais de um Vínculo Ativo</h2>

<div id="parametrosRelatorio">
	<table >
			<tr>
				<th>Ano de Ingresso:</th>
				<td><h:outputText value="#{relatorioDiscente.ano}"/></td>
			</tr>
	</table>
</div>	
<br />
	
	<table cellspacing="1" width="100%" class="tabelaRelatorioBorda">
		
	<thead>
		<tr >
			<th width="30%"> Nome </th>
			<th class="centro">CPF </th>
			<th> Curso Atual </th>
			<th class="direita"> Matrícula Atual </th>
			<th> Status </th>
			<th> Novo Curso </th>
			<th class="centro"> Nova Matrícula </th>
			<th class="centro"> Ano.Período </th>
			<th> Forma de Ingresso </th>
		</tr>
	</thead>
	
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
	
		<tr >
			<td>
				 ${linha.nome}
				 <c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if> 
			</td>
			<td class="centro"> <ufrn:format type="cpf_cnpj" valor="${linha.cpf_cnpj}"/></td>
			<td> ${linha.nome_curso_atual} - ${linha.municipio_curso_atual} </td>
			<td class="direita"> ${linha.matricula_curso_atual} </td>
			<td> ${linha.status} </td>
			<td> ${linha.nome_curso_novo} - ${linha.municipio_curso_novo} </td>
			<td class="centro"> ${linha.matricula_curso_novo} </td>
			<td class="centro"> ${linha.ano_ingresso} . ${linha.periodo_ingresso} </td>
			<td> ${linha.forma_ingresso} </td>
		</tr>
		
	</c:forEach>
	</table>
	
	<br /><br />
	
	<table width="100%">
		<tr align="center">
			<td colspan="3"><b>Total de Registros: ${fn:length(relatorioDiscente.listaDiscente)}</b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

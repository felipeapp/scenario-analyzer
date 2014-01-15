<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #DEDFE3;}
</style>

<h2> LISTA DE TODOS OS ALUNOS DO TÉCNICO </h2>

<table class="tabelaRelatorio" width="100%" style="font-size: 10px;" border="1" bordercolor="black">
			<tr class="header">
				<td style="text-align: center;"><b>Matricula</b></td> 	
				<td  style="text-align: left;"><b>Nome</b></td>
				<td style="text-align: center;"><b>CPF</b></td>
				<td  style="text-align: left;"><b>Curso</b></td> 		
			</tr>
	<c:set var="total" value="0"/>
	<c:forEach items="#{relatoriosTecnico.lista}" var="linha">
		<tr class="componentes">
			<td style="text-align: center;"> ${linha.matricula }</td>
			<td  style="text-align: left;"> ${linha.nome}</td>
			<td style="text-align: center;" width="15%"><ufrn:format type="cpf_cnpj" valor="${linha.cpf}" /> </td>
			<td  style="text-align: left;"> ${linha.turma}</td>
			<c:set var="total"  value="${total + 1}"/>
		</tr>
		</c:forEach>
		<tr class="total">
			<td align="right" COLSPAN="3"><b> Total de Alunos: </b></td>
			<td align="right"> <b>${total}</b></td>
		</tr>
	<tbody>
</table>
<br />

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
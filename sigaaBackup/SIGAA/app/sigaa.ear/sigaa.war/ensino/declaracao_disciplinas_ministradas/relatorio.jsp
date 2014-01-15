<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<br/>
<h2>Declaração de Disciplinas Ministradas</h2>
<br/>
<p align="justify">
	&emsp;&emsp;&emsp;${declaracaoDisciplinasMinistradas.parametro.textoDeclaracao}
</p>
<br/>
<table class="tabelaRelatorioBorda" width="100%">
	<c:set var="anoPeriodo" value="0"/>
	<c:forEach items="${declaracaoDisciplinasMinistradas.turmas}" var="item" varStatus="loop">
		<c:if test="${item.anoPeriodo != anoPeriodo}">
			<c:set var="anoPeriodo" value="${item.anoPeriodo}"/>
			<thead>
			<tr>
				<th width="80%"> ${anoPeriodo} </th>
				<th width="20%"> Nível </th>
			</tr>	
			</thead>			
		</c:if>					
		<tr>
			<td width="80%">${item.disciplina.detalhes.nome} - ${item.disciplina.detalhes.chTotal} h</td>
			<td width="20%">${item.disciplina.nivelDesc}</td>
		</tr>
	</c:forEach>
</table>

<br/><br/><br/>

<center>${declaracaoDisciplinasMinistradas.parametro.data}</center>

<br/><br/>
<center>
<div style="margin-left: auto; margin-right: auto; padding-top: 10px; border: 1px solid black; width: 200px; height: 40px; text-align: center; vertical-align: middle;">
	Código de Verificação:<br/>
	<span style="font-weight: bold;">${declaracaoDisciplinasMinistradas.comprovante.codigoSeguranca}</span>	
</div>
</center>
<br/>

<p style="text-align: center;">
	${declaracaoDisciplinasMinistradas.parametro.textoRodape}
</p>		


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
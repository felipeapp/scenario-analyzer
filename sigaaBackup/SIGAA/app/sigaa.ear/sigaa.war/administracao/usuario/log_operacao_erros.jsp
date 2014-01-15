<%@include file="../include/head.jsp"%>
<%@page import="br.ufrn.sipac.arq.dominio.*"%>
<span class="title">Log de Operações de Erros</span>
<hr>
<br>

<table class="listagem" align="center" cellspacing="1" border="1"
	width="100%">

	<caption class="listagem">Operações do Usuário na Sessão</caption>

	<thread class="listagem">
	<th>Hora</th>
	<th>Action</th>
	<th>Parametros Entrada</th>
	<th>Performance</th>
	<th>Entrada</th>
	
	</thread>


	<tbody class="listagem">
		<logic:iterate name="operacoes" id="log">
		
			<tr>
			
				<td><sipac:format name="log" property="hora" type="datahora" /></td>
				<td>
				<a href="showAtividade.do?idOperacao=${log.id}">
					${log.action}<br>
				</a>
				</td>
				<td>${log.parameters}</td>
				<td align=right>${log.tempoSegundos}s</td>
				<td> ${log.idRegistroEntrada} </td>
				
			</tr>
			<tr>
				<td colspan=5>
					<font face="Verdana">
					${log.tracing}
					</font>
				</td>	
			
			</tr>
		</logic:iterate>
</table>

<br>
<br>

<center><a href="javascript:history.go(-1)">Voltar</a> <br>
</center>

<%@include file="../include/tail.jsp"%>

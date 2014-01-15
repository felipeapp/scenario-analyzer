<%@include file="../include/head.jsp"%>
<%@page import="br.ufrn.sipac.arq.dominio.*"%>
<span class="title">VISUALIZAR OPERAÇÃO</span>
<hr>
<br>

<table class="listagem" align="center" cellspacing="1" border="1"
	width="100%">

	<caption class="listagem">Detalhes da Operação</caption>

		<tbody class="listagem">
		
			<tr>
				<td> Hora: </td> 
				<td><sipac:format name="log" property="hora" type="datahora" /></td>
			</tr>
			<tr>
				<td> URL: </td>
				<td>
				<a href="showLogOperacao.do?idOperacao=${operacao.id}">
					${log.action}
				</a>
				</td>
			</tr>
			<tr>
				<td> Parametros </td>
				<td>${log.parameters}</td>
			</tr>
			<tr>
				<td> </td>
				<td>${log.tempoSegundos}s</td>
			</tr>
							
			<tr>
				<td>Erro: </td>
				<td>${log.erro}</td>
			</tr>

			<tr>
				<td>Trace: </td>
				<td>${log.tracing}</td>
			</tr>
		</tbody>			

</table>

<br>
<br>

<center><a href="javascript:history.go(-1)">Voltar</a> <br>
</center>

<%@include file="../include/tail.jsp"%>

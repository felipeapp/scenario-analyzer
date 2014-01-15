<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Resumos para Congresso de Inicia��o Cient�fica
</h2>


<c:forEach items="${relatorios}" var="relatorio">
<br/>

<table>
	<tr>
		<tr>
			<td> T�tulo: </td>
			<td> ${relatorio.planoTrabalho.titulo} </td>
		</tr>
	</tr>
	<tr>
		<td> Autores: </td>
		<td>
			${relatorio.planoTrabalho.membroProjetoDiscente.discente.pessoa.nome}<br/>
			${relatorio.planoTrabalho.orientador.pessoa.nome}
		</td>
	</tr>
	<tr>
		<td> Resumo: </td>
		<td>
			<ufrn:format type="texto" name="relatorio" property="resumo"/>
		</td>
	</tr>
</table>

</c:forEach>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
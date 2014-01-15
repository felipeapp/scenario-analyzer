<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<h2> Visualizar Solicita��o Reconsidera��o </h2>


<table class="tabelaRelatorio" width="100%">
	<caption> Dados da Solicita��o de Reconsidera��o </caption>
	<tbody>

		<tr>
			<th width="20%"><b> Ano:</b> </th>
			<td> ${solicitacaoReconsideracao.obj.projeto.ano} </td>
		</tr>
		<tr>
			<th><b> T�tulo:</b> </th>
			<td> ${solicitacaoReconsideracao.obj.projeto.titulo} </td>
		</tr>
		<tr>
			<th><b> Coordenador(a):</b> </th>
			<td> ${solicitacaoReconsideracao.obj.projeto.coordenador.pessoa.nome }</td>
		</tr>
		
		<tr>
			<td colspan="2"><hr/></td>
		</tr>
		
		<tr>
			<th><b> Data da Solicita��o:</b> </th>
			<td> <fmt:formatDate value="${solicitacaoReconsideracao.obj.dataSolicitacao}" pattern="dd/MM/yyyy HH:mm:ss"/> </td>
		</tr>
		
		<tr>
			<th><b> Justificativa:</b> </th>
			<td> ${solicitacaoReconsideracao.obj.justificativa }</td>
		</tr>

		<tr>
			<td colspan="2"><hr/></td>
		</tr>

		<tr>
			<th><b> Data do Parecer:</b> </th>
			<td> <fmt:formatDate value="${solicitacaoReconsideracao.obj.dataParecer}" pattern="dd/MM/yyyy HH:mm:ss"/> </td>
		</tr>

		<tr>
			<th><b> Parecer:</b> </th>
			<td> ${solicitacaoReconsideracao.obj.parecer }</td>
		</tr>

		<tr>
			<th><b>Situa��o:</b></th>
			<td><c:if test="${not empty solicitacaoReconsideracao.obj.dataParecer}"> ${solicitacaoReconsideracao.obj.aprovado ? 'SOLICITA��O APROVADA' : 'SOLICITA��O N�O APROVADA'}</c:if></td>
		</tr>

	</tbody>
</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
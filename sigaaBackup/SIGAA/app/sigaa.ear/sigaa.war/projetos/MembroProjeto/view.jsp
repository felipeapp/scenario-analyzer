<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<h2>Visualizar Membro da Equipe</h2>

<h3 class="tituloTabelaRelatorio">Dados do Membro da Equipe</h3>
<table class="tabelaRelatorio" width="100%">
		<tr>
			<th>Título da Ação:</th>
			<td> ${membroProjeto.obj.projeto.titulo} </td>
		</tr>
		<tr>
            <th>Ano da Ação:</th>
            <td> ${membroProjeto.obj.projeto.ano} </td>
        </tr>
		
		<tr>
			<th> Coordenador(a): </th>
			<td> ${membroProjeto.obj.projeto.coordenador.servidor.pessoa.nome }</td>
		</tr>
		<tr>
			<th> Membro da Equipe: </th>
			<td> ${membroProjeto.obj.pessoa.nome }</td>
		</tr>
		
		<tr>
			<th>Categoria:</th>
			<td>${membroProjeto.obj.categoriaMembro.descricao}</td>
		</tr>

		<tr>
			<th>Função:</th>
			<td>${membroProjeto.obj.funcaoMembro.descricao}</td>
		</tr>

		<tr>
			<th>Remunerado:</th>
			<td>${membroProjeto.obj.remunerado ? 'SIM' : 'NÃO'}</td>
		</tr>

		<tr>
			<th>Ch Semanal:</th>
			<td>${membroProjeto.obj.chDedicada} hora(s)</td>
		</tr>

		<tr>
			<th>Data Início:</th>
			<td><fmt:formatDate value="${membroProjeto.obj.dataInicio}" pattern="dd/MM/yyyy" /></td>
		</tr>

		<tr>
			<th>Data Fim:</th>
			<td><fmt:formatDate value="${membroProjeto.obj.dataFim}" pattern="dd/MM/yyyy" /></td>
		</tr>
</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
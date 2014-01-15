<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Grupo de Pesquisa
</h2>

<table class="visualizacao">
	<caption> Dados do Grupo de Pesquisa </caption>
	<tr>
		<th width="25%">Grupo de Pesquisa:</th>
		<td>${objeto.codigo} - ${objeto.nome}</td>
	</tr>
	<tr>
		<th> Coordenador:</th>
		<td> ${objeto.coordenador.pessoa.nome} </td>
	</tr>
	<tr>
		<th>Página na Internet:</th>
		<td> ${objeto.homePage} </td>
	</tr>
	<tr>
		<th> E-mail para contato: </th>
		<td> ${objeto.email} </td>
	</tr>
	<tr>
		<th> Àrea de Conhecimento:  </th>
		<td> ${objeto.areaConhecimentoCnpq.nome} </td>
	</tr>
	<tr>
		<th> Categoria do Grupo de Pesquisa:  </th>
		<td> ${objeto.categoriaGrupoPesquisa.nome} </td>
	</tr>
	<tr>
		<th> Linhas de Pesquisa: </th>
		<td>
			<c:forEach items="${objeto.linhasPesquisa}" var="linha">
				${linha.nome} <br />
			</c:forEach>
		</td>
	</tr>
	<tr>
		<th> Data da Última Atualização: </th>
		<td> <ufrn:format type="data" name="objeto" property="dataUltimaAtualizacao" /> </td>
	</tr>
	<tr>
		<th> Repercussões do Trabalho do Grupo: </th>
		<td> ${objeto.repercursoesTrabGrupo} </td>
	</tr>
</table>

<br />
<div align="center">
	<a href="javascript:history.go(-1);"> << Voltar </a>
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
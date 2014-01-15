<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema></ufrn:subSistema> >
	Relatório Anual de Projeto de Pesquisa
</h2>

<table class="formulario" width="95%">
<caption> Relatório Anual de Projeto de Pesquisa</caption>
<tbody>
	<tr>
		<th width="20%"> Projeto de Pesquisa: </th>
		<td> ${objeto.projetoPesquisa.codigoTitulo} </td>
	</tr>
	<tr>
		<th> Coordenador: </th>
		<td> ${objeto.projetoPesquisa.coordenador.nome} </td>
	</tr>
	<tr>
		<th> Data de Envio: </th>
		<td>
			<c:choose>
			<c:when test="${ not empty objeto.dataEnvio }">
				<ufrn:format type="datahora" name="objeto" property="dataEnvio" />
			</c:when>
			<c:otherwise>
				<em> indisponível </em>
			</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario"> Resumo Expandido </td>
	</tr>
	<tr>
		<td colspan="2" style="padding: 10px 15px">
			<c:choose>
			<c:when test="${ not empty objeto.resumo }">
				<ufrn:format type="texto" name="objeto" property="resumo" />
			</c:when>
			<c:otherwise>
				<em> indisponível </em>
			</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="subFormulario"> Parecer (${objeto.statusString }) </td>
	</tr>
	<tr>
		<td colspan="2" style="padding: 5px 10px">
			<c:choose>
			<c:when test="${ not empty objeto.parecerConsultor }">
				<ufrn:format type="texto" name="objeto" property="parecerConsultor" />
			</c:when>
			<c:otherwise>
				<em> O parecer deste relatório ainda não foi emitido</em>
			</c:otherwise>
			</c:choose>
		</td>
	</tr>
</tbody>
</table>

<div class="voltar">
	<a href="javascript:history.back();"> Voltar </a>
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
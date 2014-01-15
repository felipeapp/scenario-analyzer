<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<style>	
	table.listagem tr.destaque td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	#relatorio-paisagem-container {
		width: 90%;
	}
	hr {
		background-color: black;
	}
</style>

<f:view>
<h2>Relatório Geral de Manifestações</h2>

<c:if test="${relatoriosOuvidoria.checkOrigem }">
	<b>Origem da Manifestação: </b>${relatoriosOuvidoria.origemManifestacao.descricao }<br/>
</c:if>
<c:if test="${relatoriosOuvidoria.checkCategoriaSolicitante }">
	<b>Categoria do Solicitante: </b>${relatoriosOuvidoria.categoriaSolicitante.descricao }<br/>
</c:if>
<c:if test="${relatoriosOuvidoria.checkCategoriaAssunto }">
	<b>Categoria do Assunto: </b>${relatoriosOuvidoria.categoriaAssuntoManifestacao.descricao }<br/>
</c:if>
<c:if test="${relatoriosOuvidoria.checkAssunto }">
	<b>Assunto da Manifestação: </b>${relatoriosOuvidoria.assuntoManifestacao.descricao }<br/>
</c:if>
<c:if test="${relatoriosOuvidoria.checkStatusManifestacao }">
	<b>Status da Manifestação: </b>${relatoriosOuvidoria.statusManifestacao.descricao }<br/>
</c:if>
<c:if test="${relatoriosOuvidoria.checkPeriodo }">
	<b>Período de Cadastro: </b> De: <ufrn:format valor="${relatoriosOuvidoria.dataInicial }" type="data" /> Até: <ufrn:format valor="${relatoriosOuvidoria.dataFinal }" type="data" /><br/>
</c:if>
<c:if test="${relatoriosOuvidoria.somenteNaoRespondidas }">
	<b>Apenas manifestações ainda não respondidas. </b><br/>
</c:if>
<hr/>
<br/>
	<c:set value="#{relatoriosOuvidoria.historicos }" var="resultado" />
	<table class="tabelaRelatorio" border="1" align="center">
		<thead>
			<tr>
				<th style="text-align: right;">Número/Ano</th>
				<th style="text-align: center;">Data de Cadastro</th>
				<th>Categoria do Solicitante</th>
				<th>Categoria do Assunto</th>
				<th>Assunto</th>
				<th>Título</th>
				<th>Unidade Responsável</th>
				<th>Designado</th>
				<th>Status da Manifestação</th>
				<th style="text-align: center;">Prazo de Resposta</th>
				<th style="text-align: right;">Respondida</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${resultado}" var="h" varStatus="s">
			<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td style="text-align: right;">${h.manifestacao.numeroAno }</td>
				<td style="text-align: center;"><ufrn:format valor="${h.manifestacao.dataCadastro }" type="datahora" /></td>
				<td>${h.manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }</td>
				<td>${h.manifestacao.assuntoManifestacao.categoriaAssuntoManifestacao.descricao }</td>
				<td>${h.manifestacao.assuntoManifestacao.descricao }</td>
				<td>${h.manifestacao.titulo }</td>
				<td>
					<c:if test="${h.unidadeResponsavel == null }">
						-
					</c:if>
					<c:if test="${h.unidadeResponsavel != null }">
						${h.unidadeResponsavel.nome }
					</c:if>
				</td>
				<td>
					<c:if test="${empty h.delegacoesUsuarioResposta }">
						-
					</c:if>
					<c:if test="${not empty h.delegacoesUsuarioResposta }">
						<c:forEach items="${h.delegacoesUsuarioResposta }" var="d">
						${d.pessoa.nome }<br />
						</c:forEach>
					</c:if>
				</td>
				<td>${h.manifestacao.statusManifestacao.descricao }</td>
				<td style="text-align: center;">
					<c:if test="${h.prazoResposta == null }">
						-
					</c:if>
					<c:if test="${h.unidadeResponsavel != null }">
						<ufrn:format valor="${h.prazoResposta }" type="data" />
					</c:if>
				</td>
				<td style="text-align: right;"><ufrn:format type="simnao" valor="${h.dataResposta != null }"></ufrn:format> </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<br />
	<div align="center"><b>Manifestações Encontradas:</b> ${ fn:length(resultado) }</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

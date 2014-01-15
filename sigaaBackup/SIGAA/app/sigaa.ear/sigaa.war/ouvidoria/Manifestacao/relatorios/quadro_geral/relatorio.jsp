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
</style>

<f:view>
<h2>Quadro Geral de Manifestações</h2>
<br/>
	<c:set value="#{relatoriosOuvidoria.historicos }" var="resultado" />
	
	<table class="tabelaRelatorio" border="1">
		<thead>
			<tr>
				<th style="text-align: center" nowrap="nowrap">Data de Cadastro</th>
				<th style="text-align: right">Número</th>
				<th nowrap="nowrap">Categoria do Assunto</th>
				<th>Assunto</th>
				<th>Tipo</th>
				<th nowrap="nowrap">Categoria do Solicitante</th>
				<th nowrap="nowrap">Unidade Responsável</th>
				<th>Designado</th>
				<th style="text-align: center" nowrap="nowrap">Resposta no Prazo</th>
				<th style="text-align: center">Lida</th>
				<th>Origem</th>
				<th>Status</th>
				<th>Respondida</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${resultado}" var="h" varStatus="s">
			<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td style="text-align: center"><ufrn:format valor="${h.manifestacao.dataCadastro }" type="datahora" /></td>
				<td style="text-align: right">${h.manifestacao.numero }</td>
				<td>${h.manifestacao.assuntoManifestacao.categoriaAssuntoManifestacao.descricao }</td>
				<td>${h.manifestacao.assuntoManifestacao.descricao }</td>
				<td>${h.manifestacao.tipoManifestacao.descricao }</td>
				<td>${h.manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }</td>
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
				<td style="text-align: center">
					<c:if test="${h.resposta == null }">
						-
					</c:if>
					<c:if test="${h.resposta != null }">
						<ufrn:format valor="${h.diasAtraso == 0 }" type="simnao" />
					</c:if>
				</td>
				<td style="text-align: center">
					<c:if test="${h.resposta == null }">
						-
					</c:if>
					<c:if test="${h.resposta != null }">
						<ufrn:format valor="${h.manifestacao.lida }" type="simnao" />
					</c:if>
				</td>
				<td>${h.manifestacao.origemManifestacao.descricao }</td>
				<td>${h.manifestacao.statusManifestacao.descricao }</td>
				<td><ufrn:format type="simnao" valor="${h.resposta != null }"></ufrn:format></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<br />
	<div align="center"><b>Manifestações Encontradas no Período:</b> ${ fn:length(resultado) }</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

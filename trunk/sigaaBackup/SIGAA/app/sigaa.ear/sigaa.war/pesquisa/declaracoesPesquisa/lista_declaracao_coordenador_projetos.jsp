<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form prependId="false">
		<h:messages showDetail="true" />
		<h2><ufrn:subSistema />&gt; Declarações de Coordenações de Projetos</h2>
		<div class="infoAltRem">
			<h:graphicImage value="/img/pesquisa/certificado.png" style="overflow: visible;" />: Emitir Declaração
		</div>
		<table class="listagem">
			<caption>Projetos Encontrados (${fn:length(declaracoesPesquisa.declaracoesCoordenador)})</caption>
			<thead>
				<tr>
					<th>Ano</th>
					<th>Título</th>
					<th>Status</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{declaracoesPesquisa.declaracoesCoordenador}" var="membro" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${membro.projeto.ano}</td>
						<td>${membro.projeto.titulo}</td>
						<td>${membro.projeto.situacaoProjeto.descricao}</td>
						<td>
							<h:commandLink title="Emitir Declaração" action="#{declaracoesPesquisa.preEmitirDeclaracaoCoordenadorProjeto}" immediate="true">
								<f:param name="id" value="#{membro.id}" />
								<h:graphicImage value="/img/pesquisa/certificado.png" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
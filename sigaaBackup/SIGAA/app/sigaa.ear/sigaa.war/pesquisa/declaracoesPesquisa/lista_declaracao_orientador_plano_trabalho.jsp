<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form prependId="false">
		<h:messages showDetail="true"/>
		<h2><ufrn:subSistema />&gt; Declara��es de Orienta��es de Planos de Trabalhos</h2>
		<div class="infoAltRem">
			<h:graphicImage value="/img/pesquisa/certificado.png" style="overflow: visible;" />: Emitir Declara��o
		</div>
		<table class="listagem">
			<caption>Projetos de Pesquisa quem cont�m Planos de Trabalho que eu oriento (${fn:length(declaracoesPesquisa.desclaracoesOrientador)})</caption>
			<thead>
				<tr>
					<th>C�digo</th>
					<th>T�tulo</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{declaracoesPesquisa.desclaracoesOrientador}" var="proPesq" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td>${proPesq.codigo}</td>
						<td>${proPesq.projeto.titulo}</td>
						<td>
							<h:commandLink title="Emitir Declara��o" immediate="true" action="#{declaracoesPesquisa.emitirDeclaracaoOrientacaoPlanoTrabalho}">
								<f:param name="id" value="#{proPesq.id}" />
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
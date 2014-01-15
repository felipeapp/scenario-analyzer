<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema />&gt; Orientação IC Externo</h2>

	<h:form>
		<div class="infoAltRem">
			<h:graphicImage	value="/img/adicionar.gif" style="overflow: visible;" /> 
			<h:commandLink action="#{orientacoesICExternoBean.direcionaCadastrar}" value="Cadastrar Nova Orientação" /> 
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar	Orientação 
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Orientação
		</div>
		
		
		<table class="visualizacao">
			<caption>Lista das Orientações IC Externo</caption>
			<thead>
			<tr>
				<td width="20%">Nome Orientando</td>
				<td width="15%">Bolsa</td>
				<td width="30%">Instituição</td>
				<td width="13%">Data Inicio</td>
				<td width="13%" align="center">Data Fim</td>
				<td colspan="2" width="5%">&nbsp;</td>
			</tr>
			</thead>

			<c:set var="outra" value="3" />
			<c:set var="ativo" />
			<tbody>
			<c:set var="linha" value="false" />
			<c:forEach items="#{orientacoesICExternoBean.allServidor}" var="item"
			varStatus="status">
			<c:set var="ativo" value="${item.ativo}" />
			<c:if test="${ativo == true}">
				<c:set var="linha" value="${linha == false ? true : false }" />
				<tr class="${linha == true ? "linhaPar" : "linhaImpar" }">
					<c:set var="bolsaAtual" value="${item.tipoBolsa}" />
					<td>${item.nomeOrientando}</td>
					<c:if test="${bolsaAtual == outra}">
						<td>${item.tipoBolsaOutra}</td>
					</c:if>
					<c:if test="${bolsaAtual == 1}">
						<td>Institucional</td>
					</c:if>
					<c:if test="${bolsaAtual == 2}">
						<td>Pibic</td>
					</c:if>
					<td>${item.instituicao.nome}</td>
					<td><ufrn:format valor="${item.dataInicio}" type="data" /></td>
					<td align="center"><ufrn:format valor="${item.dataFim}" type="data" /></td>
					<td width="20%" align="right">
						<h:commandLink title="Alterar Orientação" action="#{orientacoesICExternoBean.atualizar}">
							<f:param name="id" value="#{item.id}"/>
							<h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
						&nbsp;&nbsp;
						<h:commandLink title="Remover Orientação" action="#{orientacoesICExternoBean.remover}">
							<f:param name="id" value="#{item.id}"/>
							<h:graphicImage url="/img/delete.gif" />
						</h:commandLink>
					</td>
				</tr>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
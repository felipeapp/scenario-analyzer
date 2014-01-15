<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Lista de Configuração de GRU</h2>
	<h:form>
	<div class="infoAltRem">
		<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{configuracaoGRUMBean.preCadastrar}" value="Cadastrar"/>
		<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	</div>
	
	<table class="listagem">
		<caption class="listagem">Configuração de GRU Cadastradas</caption>
		<thead>
		<tr>
			<th>Descrição</th>
			<th>Grupo de Emissão da GRU</th>
			<th>Tipo de Arrecadação</th>
			<th>Unidade</th>
			<th>Tipo</th>
			<th>Status</th>
			<th width="2%"></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{configuracaoGRUMBean.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td valign="top">${item.descricao}</td>
				<td valign="top">${item.grupoEmissaoGRU}</td>
				<td valign="top">${item.tipoArrecadacao.descricao}</td>
				<td valign="top">${item.unidade.nome}</td>
				<td valign="top">
					<h:outputText value="Simples" rendered="#{ item.gruSimples }" />
					<h:outputText value="Cobrança" rendered="#{ !item.gruSimples }" />
				</td>
				<td valign="top">
					<h:outputText value="Ativo" rendered="#{ item.ativo }" />
					<h:outputText value="Inativo" rendered="#{ !item.ativo }" />
				</td>
				<td valign="top">
					<h:commandLink title="Alterar" style="border: 0;" action="#{configuracaoGRUMBean.atualizar}" >
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/alterar.gif" alt="Alterar" />
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>

	<div style="text-align: center;"> 
    <h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
 
    <h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
	<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
    </h:selectOneMenu>
 
    <h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
    <br/><br/>
 
    <em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
	</div>
</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
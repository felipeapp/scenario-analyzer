<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Tipo de Bolsa de Pesquisa</h2>
	<h:outputText value="#{tipoBolsaPesquisa.create}" />
	
	<center>
		<div class="infoAltRem">
			<h:form id="formLegenda">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
				<h:commandLink action="#{tipoBolsaPesquisa.preCadastrar}" value="Cadastrar"/>
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
		        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
			</h:form>
		</div>
	</center>
		
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Vinculação à Iniciação em Pesquisa</caption>
		<thead>
		<tr>
			<td>Descrição</td>
			<td>Categoria</td>
			<td>Órgão Financiador</td>
			<td nowrap="nowrap" style="text-align: center;">Vinculado a cota</td>
			<td nowrap="nowrap" style="text-align: center;">Necessita Relatório</td>
			<td style="text-align: center;">Níveis</td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<h:form id="formListagem">
		<c:forEach items="#{tipoBolsaPesquisa.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td width="20%">${item.descricaoCategoria}</td>
				<td width="20%">${item.entidadeFinanciadora.nome}</td>
				<td align="center" >${item.vinculadoCota?'Sim':'Não'}</td>
				<td align="center" >${item.necessitaRelatorio?'Sim':'Não'}</td>
				<td style="text-align: center;">${item.niveisPermitidos}</td>
				<td width=20>
					<h:commandLink title="Alterar" action="#{tipoBolsaPesquisa.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width=25>
					<h:commandLink title="Remover" action="#{tipoBolsaPesquisa.inativar}"  onclick="#{confirmDelete}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</h:form>
	</table>
	
	
	<center>
		<h:form id="formPaginacao">
			<h:graphicImage value="/img/voltar_des.gif" rendered="#{paginacao.primeiraPagina}"/>
			<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{!paginacao.primeiraPagina}"/>
			<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
			</h:selectOneMenu>
			<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}" rendered="#{!paginacao.ultimaPagina}"/>
			<h:graphicImage value="/img/avancar_des.gif" rendered="#{paginacao.ultimaPagina}"/>
		</h:form>
	</center>
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

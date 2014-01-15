<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Tipo de Invenção</h2>
	<h:outputText value="#{tipoInvencao.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>:
					<h:commandLink action="#{tipoInvencao.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>
		
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Invenção</caption>
		<thead>
		<tr>
			<td>Descrição</td>
			<td>Ativo</td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<h:form>
		<c:forEach items="#{tipoInvencao.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>${item.ativo?'Sim':'Não'}</td>
				<td width=20>
					<h:commandLink title="Alterar" action="#{tipoInvencao.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width=25>
					<h:commandLink title="Remover" action="#{tipoInvencao.preRemover}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</h:form>
	</table>
	
	
	<center>
	<h:form>
		<h:messages showDetail="true"/>
		<h:commandButton image="/img/voltar_des.gif" actionListener="#{paginacao.previousPage}"/>
		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>
		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"/>
	</h:form>
	</center>
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

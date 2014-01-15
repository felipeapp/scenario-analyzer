<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Entidade Financiadora Outra</h2>
	<h:outputText value="#{entidadeFinanciadoraOutra.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>:
					<h:commandLink action="#{entidadeFinanciadoraOutra.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>
		
	<table class=listagem>
		<tr>
			<caption class="listagem">Lista de Entidades Financiadora Outra</caption>
			<td>Descri��o</td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach items="${entidadeFinanciadoraOutra.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{entidadeFinanciadoraOutra.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{entidadeFinanciadoraOutra.preRemover}" /></td>
				</h:form>
			</tr>
		</c:forEach>
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

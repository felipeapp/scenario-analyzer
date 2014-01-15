<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Congresso de Iniciação Científica</h2>
	<h:outputText value="#{congressoIniciacaoCientifica.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{congressoIniciacaoCientifica.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>
		
	<table class="listagem">
		<caption class="listagem">Lista de Congressos Cadastrados</caption>
		<thead>
		<tr>
			<td>Congresso</td>
			<td>Ativo</td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<h:form>
		<c:forEach items="#{congressoIniciacaoCientifica.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>${item.ativo?'Sim':'Não'}</td>
				<td width=20>
					<h:commandLink title="Alterar" action="#{congressoIniciacaoCientifica.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width=25>
					<h:commandLink title="Remover" action="#{congressoIniciacaoCientifica.preRemover}">
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
		<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
 		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>
 		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
		<br/><br/>
 		<em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
	</h:form>
</center>	
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

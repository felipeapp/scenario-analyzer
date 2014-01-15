<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Modalidade de Curso </h2>
	
	<h:outputText value="#{modalidadeCursoMedio.create}" />
	
	<center>
		<div class="infoAltRem">
			<h:form>
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible; margin-right:-4px;"/>:
				<h:commandLink action="#{modalidadeCursoMedio.preCadastrar}" value="Cadastrar"/>
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible; margin-right:-4px;"/>: Alterar
		        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
			</h:form>
		</div>
	</center>
		
	<table class="listagem" style="width:100%">
		<caption>Lista de Modalidade de Curso de Nível Médio</caption>
		<thead>
			<tr>
				<th>Descrição</th>
				<th></th>
				<th></th>
			</tr>
		</thead>	
		<c:forEach items="${modalidadeCursoMedio.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td width=20>
					<h:form>
						<input type="hidden" value="${item.id}" name="id" /> 
						<h:commandButton image="/img/alterar.gif" value="Alterar" title="Alterar" action="#{modalidadeCursoMedio.atualizar}" />
					</h:form>
				</td>
				<td width=20>
					<h:form>
						<input type="hidden" value="${item.id}" name="id" /> 
						<h:commandButton image="/img/delete.gif" alt="Remover" title="Remover" action="#{modalidadeCursoMedio.preRemover}" />
					</h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
	
	<div style="text-align: center;"> 
		<br/>
		<h:commandButton image="/img/voltar_des.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true" rendered="#{paginacao.totalPaginas > 1}">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>
		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}" rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
		<br/>
		<em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
	</div>	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Item para Tradução</h2>

	<h:outputText value="#{itemTraducaoMBean.create}" />
	
	<center>
		<div class="infoAltRem">
			<h:form>
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/><h:commandLink action="#{itemTraducaoMBean.preCadastrar}" value="Cadastrar"/>
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
		        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
			</h:form>
		</div>
	</center>
		

	<table class=listagem>
		<caption class="listagem">Lista de Itens para Tradução</caption>
		<thead>
			<tr>
				<th>Entidade</th>
				<th>Nome</th>
				<th>Atributo</th>
				<th>Idiomas</th>
				<th></th>
				<th></th>
			</tr>
		</thead>	
		<c:forEach items="${itemTraducaoMBean.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.entidade.nome}</td>
				<td>${item.nome}</td>
				<td>${item.atributo}</td>
				<td>${item.idiomas}</td>
				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar" title="Alterar" alt="Alterar" 
						action="#{itemTraducaoMBean.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover" title="Remover"
						action="#{itemTraducaoMBean.remover}" onclick="#{confirmDelete}" /></td>
				</h:form>
			</tr>
		</c:forEach>
		<tfoot>
			<tr align="center">
				<td colspan=6>
				<h:form>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{itemTraducaoMBean.cancelar}" />
				</h:form>	
				</td>
			</tr>
		</tfoot>
	</table>
	
	<center>
	<h:form id="formPaginacao">
		<br/>
		<div style="text-align: center;"> 
			<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }" style="vertical-align:middle" id="paginacaoVoltar"/>
			<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true" id="mudaPagina">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
			</h:selectOneMenu>
			<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}" style="vertical-align:middle" id="paginacaoAvancar"/>
			<br/><br/>
  				<em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
			</div>
	</h:form>
	</center>
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Eleição</h2>
	<h:outputText value="#{eleicao.create}" />

	<center>
			<h:messages/>
			<div class="infoAltRem">
	  			
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{eleicao.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>
		

	<table class=listagem>
	  <caption class="listagem">Lista de Eleições</caption>
		<thead>
			<tr>
				<td><b>Título</b></td>
				<td style="text-align: center;"><b>Data Inicio</b></td>
				<td style="text-align: center;"><b>Data Fim</b></td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<c:forEach items="${eleicao.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.titulo}</td>
				<td align="center">${item.dataInicio}</td>
				<td align="center">${item.dataFim}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{eleicao.atualizar}" /></h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{eleicao.remover}" onclick="#{confirmDelete}"/></h:form>
				</td>
				
			</tr>
		</c:forEach>
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

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
 <h2><ufrn:subSistema /> > Área da Sesu</h2>

	<h:outputText value="#{areaSesu.create}" />
	
		<center>
			

			<div class="infoAltRem">
	  			
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{areaSesu.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			    
			</div>
	</center>
	
	<table class="listagem" style="width: 100%">
		<caption>Lista de Area da Sesu</caption>
			<thead>
				<tr>
					<td>Código</td>
					<td>Nome</td>
					<td style="text-align: right;">Duração Padrão</td>
					<td style="text-align: right;">Fator Retenção</td>
					<td style="text-align: right;">Peso do Grupo</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
		<c:forEach items="${areaSesu.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.codigo}</td>
				<td>${item.nome}</td>
				<td align="right">${item.duracaoPadrao}</td>
				<td align="right"> <ufrn:format type="valor4" valor="${item.fatorRetencao}"/></td>
				<td align="right"><ufrn:format type="valor1" valor="${item.pesoGrupo}"/></td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{areaSesu.atualizar}" /></h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{areaSesu.remover}" onclick="#{confirmDelete}"/></h:form>
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
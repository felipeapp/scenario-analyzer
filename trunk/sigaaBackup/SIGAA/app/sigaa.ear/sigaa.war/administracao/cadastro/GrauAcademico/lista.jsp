<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Graus Acadêmicos</h2>
	<h:outputText value="#{grauAcademico.create}" />
	
	<c:if test="${ sessionScope.acesso.cdp || sessionScope.acesso.administracao }">
		<center>
				<h:messages/>
				<div class="infoAltRem">
					<h:form>
						<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
						<h:commandLink action="#{grauAcademico.preCadastrar}" value="Cadastrar"/>
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>
						<h:outputText value=": Alterar" />
				        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
					</h:form>
				</div>
		</center>
	</c:if>
	<table class="listagem">
		<caption class="listagem">Lista de Graus Acadêmicos</caption>
		<thead>
		<tr>
			<td>Descrição</td>
			<td>Título Masculino</td>
			<td>Título Feminino</td>
			<c:if test="${ sessionScope.acesso.cdp || sessionScope.acesso.administracao }">
			<td></td>
			<td></td>
			</c:if>
		</tr>
		<tbody>
		<c:forEach items="${grauAcademico.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>${item.tituloMasculino}</td>
				<td>${item.tituloFeminino}</td>
				<c:if test="${ sessionScope.acesso.cdp || sessionScope.acesso.administracao }">
				
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar" alt="Alterar" title="Alterar"
						action="#{grauAcademico.atualizar}" /></h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton 
						image="/img/delete.gif" alt="Remover" title="Remover"
						action="#{grauAcademico.remover}"  onclick="#{confirmDelete}"/></h:form>
				</td>
				</c:if>
			</tr>
		</c:forEach>
		</tbody>
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
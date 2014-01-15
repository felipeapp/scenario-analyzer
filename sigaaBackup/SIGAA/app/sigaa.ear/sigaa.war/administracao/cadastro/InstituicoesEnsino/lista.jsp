<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
.vermelho
{
	color: red;
}
-->
</style>

<f:view>
	<h2><ufrn:subSistema /> > Institui��es de Ensino</h2>
	<h:outputText value="#{instituicoesEnsino.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{instituicoesEnsino.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>
		
	<table class="listagem">
		<caption class="listagem">Lista de Institui��es de Ensino</caption>
		<thead>
			<td>Nome</td>
			<td>Sigla</td>
			<td style="text-align: center;">Cnpj</td>
			<td>Pa�s</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${instituicoesEnsino.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.nome}</td>
				<td>${item.sigla}</td>
				<td align="center"><ufrn:format type="cpf_cnpj" valor="${item.cnpj}"/>  </td>
				<td>${item.pais.nome}</td>
				<td width=20>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{instituicoesEnsino.atualizar}" />
					</h:form>	
				</td>
				<td width=25>
				<h:form>
				 <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{instituicoesEnsino.remover}" onclick="#{confirmDelete}" />
				 </h:form>	
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
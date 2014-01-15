<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:outputText value="#{turno.create }"></h:outputText>
<ufrn:subSistema teste="not graduacao, consulta">
</ufrn:subSistema>
	<h2><ufrn:subSistema /> > Turno</h2>

<ufrn:subSistema teste="not graduacao, consulta">
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{turno.preCadastrar}" value="Cadastrar"/>
				</h:form>
			</div>
	</center>
</ufrn:subSistema>
	<table class="listagem" style="width: 60%">
			<caption >Lista de Turnos</caption>
		<thead>
		<tr>
			<td>Descrição</td>
			<td>Sigla</td>
			<td style="text-align: center;">Ativo</td>
		</tr>
		</thead>
		<c:forEach items="${turno.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>${item.sigla}</td>
				<td align="center">${item.ativo ? 'Sim' : 'Não'}</td>
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
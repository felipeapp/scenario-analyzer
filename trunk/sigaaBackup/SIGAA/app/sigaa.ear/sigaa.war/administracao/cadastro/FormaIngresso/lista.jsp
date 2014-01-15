<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2> <ufrn:subSistema /> > Forma de Ingresso</h2>

	<h:outputText value="#{formaIngresso.create}" />
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
					<h:commandLink action="#{formaIngresso.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/> :Alterar
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/> :Remover
				</h:form>
			</div>
	</center>
	
	<table class=listagem>
		<caption class="listagem">Lista de Formas de Ingressos</caption>
		<thead>
			<tr>
			 	<th width="50%" style="text-align: center;">Descrição</th>
				<th>Realiza Processo Seletivo</th>
				<th>Mobilidade Estudantil</th>
				<th colspan="2"></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${formaIngresso.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>
					<c:if test="${empty item.realizaProcessoSeletivo}">
						Não especificado
					</c:if>
					<ufrn:format type="SimNao" valor="${item.realizaProcessoSeletivo}" />
				</td>
 				<td><ufrn:format type="SimNao" valor="${item.tipoMobilidadeEstudantil}" /></td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar" alt="Alterar" title="Alterar"
						action="#{formaIngresso.atualizar}" /></h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"title="Remover"
						action="#{formaIngresso.remover}" onclick="#{confirmDelete}"/></h:form>
				</td>
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
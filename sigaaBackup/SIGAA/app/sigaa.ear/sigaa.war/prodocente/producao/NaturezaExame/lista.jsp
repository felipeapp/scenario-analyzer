<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Natureza do Exame</h2>
	<br>
	<h:outputText value="#{naturezaExame.create}" />
	
	<table class=listagem>
		<tr>
			<caption class="listagem">Lista de Natureza do Exame</caption>
			<td>descrição</td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach items="${naturezaExame.all}" var="item">
			<tr>

				<td>${item.descricao}</td>
				<td width=20>
				 <h:form>
					<input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{naturezaExame.atualizar}" />
				 </h:form>
				</td>

				<td width=25>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{naturezaExame.remover}" onclick="#{confirmDelete}"/>
					</h:form>
				  </td>
			</tr>
		</c:forEach>
	</table>
	<center>
	<br />
	<h:form>
		<h:messages showDetail="true"/>
		<h:commandButton image="/img/voltar_des.gif" actionListener="#{paginacao.previousPage}"/>
		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>
		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"/>
	</h:form>
	</center>
<h:outputText value="#{naturezaExame.dropList}" />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

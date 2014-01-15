<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>Monografia</h2>

	<h:outputText value="#{monografia.create}" />

	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" /> <h:commandLink action="#{monografia.preCadastrar}" value="Cadastrar Nova Monografia"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Monografia
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Monografia <br/>
		</div>
	</h:form>

	<table class="listagem" width="100%" border="1">
		<caption class="listagem">Lista de Monografias</caption>
		<thead>
			<td>Título</td>
			<td>Orientando</td>
			<td>Instituição</td>
			<td>Data de Inicio</td>
			<td>Data de Fim</td>
			<td></td>
			<td></td>
		</thead>

		<c:set var="lista" value="${monografia.allAtividades}" />

		<c:if test="${empty lista}">
			<tr>
			<td colspan="6">
			<br />
			<center>
			<span style="color:red;">Nenhuma Monografia Encontrada.</span>
			</center>
			</td>
			</tr>
		</c:if>


		<c:forEach items="${monografia.allAtividades}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.titulo}</td>
				<td>${item.orientando==null?item.aluno.pessoa.nome : item.orientando}</td>
				<td>${item.instituicao}</td>
				<td><ufrn:format type="data" name="item" property="periodoInicio"/></td>
				<td><ufrn:format type="data" name="item" property="periodoFim"/></td>

				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{monografia.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{monografia.remover}" onclick="#{confirmDelete}"/></td>
				</h:form>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>
		<ufrn:subSistema /> > Mini-Curso
	</h2>

	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" />: <h:commandLink action="#{miniCurso.preCadastrar}" value="Cadastrar Novo Mini-Curso"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Mini-Curso
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Mini-Curso <br/>
		</div>
	</h:form>
	<h:messages showDetail="true"></h:messages>

	<h:outputText value="#{miniCurso.create}" />

	<table class=listagem width="100%" border="1">
		<caption class="listagem">Lista de Mini-Cursos</caption>
		<thead>
		<tr>
			<td>Nome do Congresso</td>
			<td>Título</td>
			<td>Data de Início</td>
			<td>Data de Fim</td>
			<td></td>
			<td></td>
		</tr>

		</thead>
		<c:set var="lista" value="${miniCurso.allAtividades}" />

		<c:if test="${empty lista}">
			<tr>
			<td colspan="6">
			<br />
			<center>
			<span style="color:red;">Nenhum Mini-Curso Encontrado.</span>
			</center>
			</td>
			</tr>
		</c:if>

		<c:forEach items="${miniCurso.allAtividades}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.nomeCongresso}</td>
				<td>${item.titulo}</td>
				<td><ufrn:format type="data" name="item" property="periodoInicio"/></td>
				<td><ufrn:format type="data" name="item" property="dataFim"/></td>
				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id"/>
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{miniCurso.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id"/>
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{miniCurso.remover}" onclick="#{confirmDelete}"/></td>
				</h:form>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

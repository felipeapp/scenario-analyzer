<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema/> &gt; Cadastro Único</h2>

<h:form>
<div class="infoAltRem">
	<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>	<h:commandLink action="#{cadastroUnicoBolsa.iniciarCadastro}" value="Cadastrar"/>
	<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar
	<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
</div>

<c:if test="${ not empty cadastroUnicoBolsa.cadastros}">
	<table class="listagem" style="width: 90%;">
		<caption class="listagem">Cadastro Único</caption>
		<thead>
			<tr>
				<th>Questionário Utilizado</th>
				<th colspan="3" width="5%" nowrap="nowrap"></th>
			</tr>
		</thead>
		
		<c:forEach items="#{cadastroUnicoBolsa.cadastros}" var="item" varStatus="status">
		<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			<td>${item.questionario.titulo}</td>
			<td>
				<h:commandLink title="Visualizar questionário" action="#{questionarioBean.view}">
					<f:param name="id" value="#{item.questionario.id}" />
					<h:graphicImage url="/img/view.gif"/>
				</h:commandLink>
			</td>
			<td>
				<c:if test="${item.ativo}">
					<h:commandLink title="Alterar cadastro" action="#{cadastroUnicoBolsa.iniciarAlterar}">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</c:if>
			</td>
			<td>
				<c:if test="${item.ativo}">
					<h:commandLink title="Remover" action="#{cadastroUnicoBolsa.remover}" onclick="return confirm('Tem certeza que deseja remover esse registro?');">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</c:if>
			</td>
		</tr>
		</c:forEach>
	</table>
</c:if>
<c:if test="${ empty cadastroUnicoBolsa.cadastros}">
	<br />
	<div align="center"><p style="font-style: italic; ">Nenhum registro de cadastro único encontrado.</p></div>
</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
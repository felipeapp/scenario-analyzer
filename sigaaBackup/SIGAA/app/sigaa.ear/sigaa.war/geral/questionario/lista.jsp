<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema/> &gt; Questionários</h2>

<h:form>
<div class="infoAltRem">
	<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
	<h:commandLink action="#{questionarioBean.iniciarCadastro}" value="Cadastrar Novo Questionário"/>&shy;
	<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
	<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
</div>

<table class="listagem">
	<caption class="listagem"> Lista de Questionários</caption>
	<thead>
		<tr>
			<th>Título do Questionário</th>
			<th>Tipo</th>
			<th width="10%" style="text-align: right;">Perguntas</th>
			<th colspan="4" width="10%"></th>
		</tr>
	</thead>
	
	<c:forEach items="#{questionarioBean.questionarios}" var="item" varStatus="status">
	<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
		<td>${item.titulo}</td>
		<td>${item.tipo.descricao}</td>
		<td align="right"> ${fn:length(item.perguntas)} </td>
		<td>
			<h:commandLink title="Visualizar" action="#{questionarioBean.view}">
				<f:param name="id" value="#{item.id}" />
				<h:graphicImage url="/img/view.gif"/>
			</h:commandLink>
		</td>
		<td>
			<h:commandLink title="Alterar" action="#{questionarioBean.atualizar}" >
				<f:param name="id" value="#{item.id}" />
				<h:graphicImage url="/img/alterar.gif"/>
			</h:commandLink>
		</td>
		<td>
			<h:commandLink title="Remover" action="#{questionarioBean.preRemover}">
				<f:param name="id" value="#{item.id}" />
				<h:graphicImage url="/img/delete.gif"/>
			</h:commandLink>
		</td>
	</tr>
	</c:forEach>
</table>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
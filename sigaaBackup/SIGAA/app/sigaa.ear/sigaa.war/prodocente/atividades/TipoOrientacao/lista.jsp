<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Orientação</h2>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
			 <h:commandLink action="#{tipoOrientacao.direcionar}"
			 value="Cadastrar Tipo de Orientação"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Tipo de Orientação
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Orientação<br/>
		</div>
	</h:form>
	<h:outputText value="#{tipoOrientacao.create}" />
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Orientação</caption>
		<thead>
			<tr>
				<td>Descrição</td>
				<td>Contexto</td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<c:forEach items="${tipoOrientacao.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>${item.nivelEnsino}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{tipoOrientacao.atualizar}" /></h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{tipoOrientacao.remover}" onclick="#{confirmDelete}" 
						immediate="true"/></h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
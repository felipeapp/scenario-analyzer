<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form id="form">
	<h2><ufrn:subSistema /> &gt; Campus</h2>
	<div class="infoAltRem" style="width: 100%">
		<img src="/shared/img/adicionar.gif" style="overflow: visible;" /> 
		<h:commandLink action="#{campusIes.preCadastrar}" value="Cadastrar Novo Campus" />
		<img src="/shared/img/alterar.gif" style="overflow: visible;" />: Alterar 
		<img src="/shared/img/delete.gif" style="overflow: visible;" />: Remover
	</div>
		<c:if test="${ not empty campusIes.all }">
			<table class="listagem">
				<caption>Lista de Campi</caption>
				<thead>
					<tr>
						<th>Nome</th>
						<th>Sigla</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="#{campusIes.all}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${item.nome}</td>
						<td>${item.sigla}</td>
						<td><h:commandLink action="#{ campusIes.atualizar }">
							<f:verbatim>
								<img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" />
							</f:verbatim>
							<f:param name="id" value="#{ item.id }" />
						</h:commandLink></td>
						<td><h:commandLink action="#{ campusIes.inativar }"
							onclick="#{confirmDelete}">
							<f:verbatim>
								<img src="/shared/img/delete.gif" alt="Remover" title="Remover" />
							</f:verbatim>
							<f:param name="id" value="#{ item.id }" />
						</h:commandLink></td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

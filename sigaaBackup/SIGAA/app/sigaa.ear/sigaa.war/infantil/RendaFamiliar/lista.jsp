<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Renda Familiar</h2>
<div class="infoAltRem" style="width: 100%">
	<img src="/shared/img/adicionar.gif" style="overflow: visible;"/>
	<a href="${ctx}/infantil/RendaFamiliar/form.jsf">Cadastrar Nova Renda Familiar</a> 
	<img src="/shared/img/alterar.gif" style="overflow: visible;"/>: Alterar
	<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover
</div>
<h:form id="form">
	<c:if test="${ not empty rendaFamiliarMBean.all }">
		<table class="listagem">
			<caption> Lista de Rendas Familiares</caption>
			<thead>
				<tr>
					<th>Descrição</th>
				<th></th><th></th></tr>
			</thead>
			<c:forEach items="#{rendaFamiliarMBean.all}" var="item" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${item.descricao}</td>
					<td width="3%" align="center">
						<h:commandLink action="#{ rendaFamiliarMBean.atualizar }">
							<f:verbatim><img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar"/></f:verbatim>
							<f:param name="id" value="#{ item.id }" />
						</h:commandLink>
					</td>
					<td width="3%" align="center">
						<h:commandLink action="#{ rendaFamiliarMBean.remover }" onclick="#{confirmDelete}">
							<f:verbatim><img src="/shared/img/delete.gif" alt="Remover" title="Remover"/></f:verbatim>
							<f:param name="id" value="#{ item.id }" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

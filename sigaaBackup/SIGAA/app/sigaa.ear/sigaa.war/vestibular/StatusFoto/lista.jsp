<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form id="form">
	<a4j:keepAlive beanName="statusFotoMBean"></a4j:keepAlive>	
	<h2><ufrn:subSistema /> &gt; Status de Fotos</h2>

	<div class="infoAltRem" style="width: 100%">
		<img src="/shared/img/adicionar.gif" style="overflow: visible;" />: <h:commandLink value="Cadastrar Novo Status" action="#{statusFotoMBean.preCadastrar}" /> 
		<img src="/shared/img/alterar.gif" style="overflow: visible;" />: Alterar
		<img src="/shared/img/delete.gif" style="overflow: visible;" />: Remover
	</div>
		<c:if test="${ not empty statusFotoMBean.all }">
			<table class="listagem">
				<caption>Lista de Status de Fotos 3x4</caption>
				<thead>
					<tr>
						<th>Válida</th>
						<th>Descrição</th>
						<th>Recomendação</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="#{statusFotoMBean.all}" var="item"
					varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td><ufrn:format type="simnao" valor="${item.valida}" /></td>
						<td>${item.descricao}</td>
						<td>${item.recomendacao}</td>
						<td><h:commandLink action="#{ statusFotoMBean.atualizar }" id="atualizar">
							<f:verbatim>
								<img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" />
							</f:verbatim>
							<f:param name="id" value="#{ item.id }" />
						</h:commandLink></td>
						<td><h:commandLink action="#{ statusFotoMBean.preRemover }" id="remover">
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

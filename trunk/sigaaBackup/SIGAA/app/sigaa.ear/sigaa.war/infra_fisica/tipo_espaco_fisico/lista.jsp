<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema/> > Tipo de Espaço de Físico</h2>
	<div class="infoAltRem" style="width: 100%">
		<img src="/shared/img/adicionar.gif" style="overflow: visible;"/>
		<a href="${ctx}/infra_fisica/tipo_espaco_fisico/form.jsf">Cadastrar Novo Tipo de Espaço Físico</a> 
		<img src="/shared/img/alterar.gif" style="overflow: visible;"/>: Alterar 
		<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover 
	</div>
<h:form>
 <c:if test="${ not empty tipoEspacoFisicoMBean.allAtivos }">
	<table class="formulario" width="70%">
	 <caption> Lista de Tipos de Espaço Físico</caption>
		<thead>
		  <tr>
			<th style="text-align: left;">Denominação</th>
			<th style="text-align: center;">Reservável</th>
			<th style="text-align: center;">Espaço Aulas</th>
			<th colspan="2"></th>
		  </tr>
		</thead>
		
		<c:forEach items="#{tipoEspacoFisicoMBean.allOrdenado}" var="item" varStatus="loop">
		   <tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td align="left">${item.denominacao}</td>
			<td align="center">${item.reservavel ? 'Sim' : 'Não'}</td>
			<td align="center">${item.espacoAulas ? 'Sim' : 'Não'}</td>
			<td align="right" width="5px">
				<h:commandLink action="#{ tipoEspacoFisicoMBean.atualizar }">
				<f:verbatim><img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar"/></f:verbatim>
				<f:param name="id" value="#{ item.id }" />
				</h:commandLink>
			</td>
			<td align="right" width="5px">
				<h:commandLink action="#{ tipoEspacoFisicoMBean.inativar }" onclick="#{confirmDelete}">
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
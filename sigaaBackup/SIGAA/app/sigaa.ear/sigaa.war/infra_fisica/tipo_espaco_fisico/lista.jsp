<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema/> > Tipo de Espa�o de F�sico</h2>
	<div class="infoAltRem" style="width: 100%">
		<img src="/shared/img/adicionar.gif" style="overflow: visible;"/>
		<a href="${ctx}/infra_fisica/tipo_espaco_fisico/form.jsf">Cadastrar Novo Tipo de Espa�o F�sico</a> 
		<img src="/shared/img/alterar.gif" style="overflow: visible;"/>: Alterar 
		<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover 
	</div>
<h:form>
 <c:if test="${ not empty tipoEspacoFisicoMBean.allAtivos }">
	<table class="formulario" width="70%">
	 <caption> Lista de Tipos de Espa�o F�sico</caption>
		<thead>
		  <tr>
			<th style="text-align: left;">Denomina��o</th>
			<th style="text-align: center;">Reserv�vel</th>
			<th style="text-align: center;">Espa�o Aulas</th>
			<th colspan="2"></th>
		  </tr>
		</thead>
		
		<c:forEach items="#{tipoEspacoFisicoMBean.allOrdenado}" var="item" varStatus="loop">
		   <tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td align="left">${item.denominacao}</td>
			<td align="center">${item.reservavel ? 'Sim' : 'N�o'}</td>
			<td align="center">${item.espacoAulas ? 'Sim' : 'N�o'}</td>
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
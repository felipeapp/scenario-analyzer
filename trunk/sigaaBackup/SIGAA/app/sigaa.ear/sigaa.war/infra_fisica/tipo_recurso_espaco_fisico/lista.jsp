<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema/> > Tipo de Recurso para Espaço Físico</h2>

	<div class="infoAltRem" style="width: 100%">
		<img src="/shared/img/adicionar.gif" style="overflow: visible;"/>
		<a href="${ctx}/infra_fisica/tipo_recurso_espaco_fisico/form.jsf">Cadastrar Novo Tipo de Recurso para Espaço Físico</a> 
		<img src="/shared/img/alterar.gif" style="overflow: visible;"/>: Alterar 
		<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover 
	</div>
<h:form>
<c:if test="${ not empty tipoRecursoEspacoFisicoMBean.allAtivos}">
 <table class="formulario" width="55%">
  <caption> Lista de Tipo de Recurso para Espaço Físico</caption>
	<thead>
		<tr>
			<th colspan="3">Descrição</th>
		</tr>
	</thead>
	   <c:forEach items="#{tipoRecursoEspacoFisicoMBean.allAtivos}" var="item" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		  <td>${item.denominacao}</td>
	      <td align="right" width="5px">
			<h:commandLink action="#{ tipoRecursoEspacoFisicoMBean.atualizar }">
				<f:verbatim><img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar"/></f:verbatim>
				<f:param name="id" value="#{ item.id }" />
			</h:commandLink>
		  </td>
          <td align="right" width="5px">
			<h:commandLink action="#{ tipoRecursoEspacoFisicoMBean.inativar}" onclick="#{confirmDelete}" immediate="true">
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
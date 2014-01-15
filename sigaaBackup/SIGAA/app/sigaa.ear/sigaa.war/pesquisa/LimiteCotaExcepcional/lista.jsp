<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Limite de Cota Excepcional</h2>
<div class="infoAltRem" style="width: 100%">
<img src="/shared/img/adicionar.gif" style="overflow: visible;"/>
<a href="${ctx}/pesquisa/LimiteCotaExcepcional/form.jsf">Cadastrar Novo Limite de Cota Excepcional</a> 
<img src="/shared/img/alterar.gif" style="overflow: visible;"/>: Alterar 
<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover 
</div>
<h:form id="form">
<c:if test="${ not empty limiteCotaExcepcionalMBean.allAtivos }">
<table class="listagem">
<caption> Lista de Limites de Cota Excepcionais</caption>
<thead>
<tr>
<th>Docente</th>
<th>Limite</th>
<th></th><th></th></tr>
</thead>
<c:forEach items="#{limiteCotaExcepcionalMBean.all}" var="item" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
<td>${item.servidor}</td>
<td>${item.limite}</td>
<td>
<h:commandLink id="link_atualizar" action="#{ limiteCotaExcepcionalMBean.atualizar }">
<f:verbatim><img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar"/></f:verbatim>
<f:param name="id" value="#{ item.id }" />
</h:commandLink>
</td>
<td>
<h:commandLink id="link_remover" action="#{ limiteCotaExcepcionalMBean.inativar }" onclick="#{confirmDelete}">
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

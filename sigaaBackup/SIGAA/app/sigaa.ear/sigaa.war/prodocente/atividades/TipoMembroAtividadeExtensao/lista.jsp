<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>tipoMembroAtivividadeExtensao</h2><br>
<h:outputText value="#{tipoMembroAtivividadeExtensao.create}"/>
<table class=listagem><tr>
<caption class="listagem"> Lista de tipoMembroAtivividadeExtensaos</caption><td>descricao</td>
<td></td><td></td></tr>
<c:forEach items="${tipoMembroAtivividadeExtensao.all}" var="item">
<tr>
<td>${item.descricao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{tipoMembroAtivividadeExtensao.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{tipoMembroAtivividadeExtensao.remover}"/>
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

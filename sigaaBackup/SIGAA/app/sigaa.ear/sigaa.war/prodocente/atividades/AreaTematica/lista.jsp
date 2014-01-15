<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>areaTematica</h2><br>
<h:outputText value="#{areaTematica.create}"/>
<table class=listagem><tr>
<caption class="listagem"> Lista de areaTematicas</caption><td>descricao</td>
<td></td><td></td></tr>
<c:forEach items="${areaTematica.all}" var="item">
<tr>
<td>${item.descricao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{areaTematica.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{areaTematica.remover}"/>
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

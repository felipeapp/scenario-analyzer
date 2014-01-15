<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>instituicaoFomento</h2><br>
<h:outputText value="#{instituicaoFomento.create}"/>
<table class=listagem><tr>
<caption class="listagem"> Lista de instituicaoFomentos</caption><td>infonome</td>
<td></td><td></td></tr>
<c:forEach items="${instituicaoFomento.all}" var="item">
<tr>
<td>${item.infonome}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{instituicaoFomento.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{instituicaoFomento.remover}"/>
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

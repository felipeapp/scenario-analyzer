<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>financiamentoVisitaCientifica</h2><br>
<h:outputText value="#{financiamentoVisitaCientifica.create}"/>
<table class=listagem>
<caption class="listagem"> Lista de financiamentoVisitaCientificas</caption>
<thead>
<td>entidade</td>
<td>servidor</td>
<td>valor</td>
<td>visitaCientifica</td>
<td></td><td></td>
</thead>
<c:forEach items="${financiamentoVisitaCientifica.all}" var="item" varStatus="status">
<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
<td>${item.entidade}</td>
<td>${item.servidor}</td>
<td>${item.valor}</td>
<td>${item.visitaCientifica}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{financiamentoVisitaCientifica.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{financiamentoVisitaCientifica.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

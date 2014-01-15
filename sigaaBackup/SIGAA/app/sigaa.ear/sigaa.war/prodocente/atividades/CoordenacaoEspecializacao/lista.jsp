<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>coordenacaoEspecializacao</h2><br>
<h:outputText value="#{coordenacaoEspecializacao.create}"/>
<table class=listagem><tr>
<caption class="listagem"> Lista de coordenacaoEspecializacaos</caption><td>servidor</td>
<td>curso</td>
<td>periodoInicio</td>
<td>periodoFim</td>
<td></td><td></td></tr>
<c:forEach items="${coordenacaoEspecializacao.all}" var="item">
<tr>
<td>${item.servidor}</td>
<td>${item.curso}</td>
<td>${item.periodoInicio}</td>
<td>${item.periodoFim}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{coordenacaoEspecializacao.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{coordenacaoEspecializacao.remover}"/>
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

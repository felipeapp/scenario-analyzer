<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>linhaProgramaticaAtividadeExtensao</h2><br>
<h:outputText value="#{linhaProgramaticaAtividadeExtensao.create}"/>
<table class=listagem><tr>
<caption class="listagem"> Lista de linhaProgramaticaAtividadeExtensaos</caption><td>denominacao</td>
<td>definicao</td>
<td></td><td></td></tr>
<c:forEach items="${linhaProgramaticaAtividadeExtensao.all}" var="item">
<tr>
<td>${item.denominacao}</td>
<td>${item.definicao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{linhaProgramaticaAtividadeExtensao.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{linhaProgramaticaAtividadeExtensao.remover}"/>
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

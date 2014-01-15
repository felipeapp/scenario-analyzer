<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>situacaoAtividadeExtensao</h2><br>
<h:outputText value="#{situacaoAtividadeExtensao.create}"/>
<table class=listagem><tr>
<caption class="listagem"> Lista de situacaoAtividadeExtensaos</caption><td>descricao</td>
<td></td><td></td></tr>
<c:forEach items="${situacaoAtividadeExtensao.allAtividades}" var="item">
<tr>
<td>${item.descricao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{situacaoAtividadeExtensao.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{situacaoAtividadeExtensao.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

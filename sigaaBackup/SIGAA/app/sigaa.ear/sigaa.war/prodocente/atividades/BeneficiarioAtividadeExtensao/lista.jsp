<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>beneficiarioAtividadeExtensao</h2><br>
<h:outputText value="#{beneficiarioAtividadeExtensao.create}"/>
<table class=listagem><tr>
<caption class="listagem"> Lista de beneficiarioAtividadeExtensaos</caption><td>cidade</td>
<td>descricao</td>
<td>quantidadePrevista</td>
<td>quantidadeRealizada</td>
<td>atividadeExtensao</td>
<td></td><td></td></tr>
<c:forEach items="${beneficiarioAtividadeExtensao.allAtividades}" var="item">
<tr>
<td>${item.cidade}</td>
<td>${item.descricao}</td>
<td>${item.quantidadePrevista}</td>
<td>${item.quantidadeRealizada}</td>
<td>${item.atividadeExtensao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{beneficiarioAtividadeExtensao.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{beneficiarioAtividadeExtensao.remover}"/>
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

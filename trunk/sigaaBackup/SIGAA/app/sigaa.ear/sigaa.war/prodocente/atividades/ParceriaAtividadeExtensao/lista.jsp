<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>parceriaAtividadeExtensao</h2><br>
<h:outputText value="#{parceriaAtividadeExtensao.create}"/>
<table class=listagem>
<caption class="listagem"> Lista de parceriaAtividadeExtensaos</caption>
<thead>
<td>instinstituicao</td>
<td>convenio</td>
<td>valorFinanciamento</td>
<td>instituicao</td>
<td>atividadeExtensao</td>
<td></td><td></td>
</thead>
<c:forEach items="${parceriaAtividadeExtensao.allAtividades}" var="item">
<tr>
<td>${item.instinstituicao}</td>
<td>${item.convenio}</td>
<td>${item.valorFinanciamento}</td>
<td>${item.instituicao}</td>
<td>${item.atividadeExtensao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{parceriaAtividadeExtensao.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{parceriaAtividadeExtensao.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

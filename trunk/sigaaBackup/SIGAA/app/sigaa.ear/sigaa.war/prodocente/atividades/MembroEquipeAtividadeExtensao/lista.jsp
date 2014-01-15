<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>membroEquipeAtividadeExtensao</h2><br>
<h:outputText value="#{membroEquipeAtividadeExtensao.create}"/>
<table class=listagem>
<caption class="listagem">Lista de Membro de Equipe  de Atividade de Extensão</caption>
<thead>
<td>Servidor</td>
<td>Data Entrada</td>
<td>Data Saída</td>
<td>Carga Horária Semanal</td>
<td>Atividade Extensao</td>
<td>Categoria</td>
<td>Tipo</td>
<td></td><td></td>
</thead>
<c:forEach items="${membroEquipeAtividadeExtensao.allAtividades}" var="item" varStatus="status">
<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
<td>${item.servidor}</td>
<td>${item.dataEntrada}</td>
<td>${item.dataSaida}</td>
<td>${item.chDedicada}</td>
<td>${item.atividadeExtensao}</td>
<td>${item.categoriaMembroAtividadeExtensao}</td>
<td>${item.tipoMembroAtividadeExtensao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{membroEquipeAtividadeExtensao.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{membroEquipeAtividadeExtensao.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

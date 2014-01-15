<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<style>
<!--
table.visualizacao td {text-align: left;}
table.visualizacao th {font-weight: bold;}
-->
</style>


<f:view>
<h:outputText value="#{consultaSolicitacoes.create}"/>
${consultaSolicitacoes.carregarSolicitacoes}




<c:set var="discente" value="#{consultaSolicitacoes.discente}"/>
<%@include file="/graduacao/info_discente.jsp"%>

<h:form>
<table class="listagem" style="width: 100%">
<caption>Matrículas Orientadas</caption>
<thead style="font-size: xx-small;">
	<td width="1%"></td>
	<td width="8%"></td>
	<td>Componente Curricular</td>
	<td width="7%">Situação</td>
	<td width="10%">Horário</td>
</thead>
<tbody>

<c:if test="${empty consultaSolicitacoes.solicitacoes}">
<tr>
	<td colspan="5" align="center"><font color="red"> O discente não possui orientações de matrícula.</font></td>
</tr>

</c:if>

<c:if test="${not empty consultaSolicitacoes.solicitacoes}">

<c:forEach items="${consultaSolicitacoes.solicitacoes}" var="solicitacao" varStatus="status">
	<c:set value="${solicitacao.turma}" var="turmaVar" />
	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
		<td>
		<c:if test="${acesso.administradorDAE}">
		<input type="checkbox" onclick="marcarAnulado(this)" name="anulados" value="${solicitacao.id}" id="anul_${solicitacao.id}" class="noborder" ${solicitacao.anulado ? 'checked=checked' : '' }>
		</c:if>
		</td>
		<td align="center">
		<a href="#" onclick="PainelTurma.show(${turmaVar.id})" title="Ver Detalhes dessa turma">
		Turma ${turmaVar.codigo}
		</a>
		</td>
		<td>
		<a href="#" onclick="PainelComponente.show(${turmaVar.disciplina.id})" title="Ver Detalhes do Componente Curricular">
		${turmaVar.disciplina.detalhes.codigo}
		</a> - ${turmaVar.disciplina.nome}
		</td>
		<td>${solicitacao.statusDescricao}</td>
		<td>${turmaVar.descricaoHorario}</td>
	</tr>
	<c:if test="${param['exibirOrientacoes']}">
	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
		<td colspan="5" style="font-size: x-small; font-style: italic">
			<strong>Orientações na matrícula: </strong>  ${solicitacao.observacao}
			<c:if test="${empty solicitacao.observacao}"> <font color="red"> Não há orientações de matrícula para esta disciplina </font></c:if>
		</td>
	</tr>
	</c:if>
	
</c:forEach>
</c:if>
</tbody>
</table>



</h:form>
</f:view>
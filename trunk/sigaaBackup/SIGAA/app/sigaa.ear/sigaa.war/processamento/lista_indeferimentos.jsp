<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<c:if test="${ listaIndeferimentos.orientadorAcademico }">
	<f:subview id="menu">
	<%@include file="/portais/docente/menu_docente.jsp" %>
	</f:subview>
</c:if>

<h2><ufrn:subSistema /> > Lista de Indeferimentos</h2>

<c:if test="${ !listaIndeferimentos.orientadorAcademico }">

<h:form id="form">
<table class="formulario" width="60%">
<caption>Opções de Listagem</caption>
<tbody>
	<tr>
		<th class="required">Ano-Periodo:</th>
		<td>
			<h:inputText value="#{ listaIndeferimentos.ano }" id="ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> - <h:inputText value="#{ listaIndeferimentos.periodo }" id="periodo" size="2" maxlength="1" onkeyup="return formatarInteiro(this);"/>
		</td>
	</tr>
	
	<c:if test="${acesso.dae}">
		<tr>
			<th>Centro</th>
			<td>
				<h:selectOneMenu value="#{ listaIndeferimentos.unidade.id }" id="unidade">
					<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
					<f:selectItem itemLabel="ALUNOS ESPECIAIS" itemValue="-1"/>
					<f:selectItems value="#{ unidade.allCentroCombo }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
	</c:if>
</tbody>
<tfoot>
	<tr><td colspan="2">
		<h:commandButton value="Consultar" action="#{ listaIndeferimentos.consultar }" id="btnConsultar"/>
		<h:commandButton value="Cancelar" action="#{ listaIndeferimentos.cancelar }" onclick="#{confirm}" id="btnCancelar" immediate="true" /> 
	</td></tr>
</tfoot>
</table>

</h:form>

</c:if>

<br/>

<c:if test="${ not empty listaIndeferimentos.indeferimentos }">
<table class="listagem">
<caption>Alunos com Matrículas Indeferidas</caption>
<thead>
	<tr><th>Matrícula</th><th>Nome</th><th>Curso</th><th>Indeferimentos</th></tr>
</thead>
<tbody>
<c:forEach var="i" items="${ listaIndeferimentos.indeferimentos }" varStatus="outerLoop">
	<tr class="${ outerLoop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${ i.matricula }</td><td nowrap="nowrap">${ i.nome }</td><td nowrap="nowrap">${ i.curso }</td>
	<td width="200">
		<c:forEach var="codigo" items="${ i.indeferimentos }" varStatus="innerLoop">
			${ codigo }<c:if test="${ !innerLoop.last }">,</c:if>
		</c:forEach>
	</td></tr>
</c:forEach>
</tbody>
</table>
</c:if>
<c:if test="${ (empty listaIndeferimentos.indeferimentos) && (listaIndeferimentos.orientadorAcademico) }">
<center><span class="erro">Não há orientandos com matrículas indeferidas</span></center>
</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
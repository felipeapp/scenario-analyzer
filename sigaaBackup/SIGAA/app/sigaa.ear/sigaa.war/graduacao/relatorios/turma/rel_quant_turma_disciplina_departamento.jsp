<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.parametro td {padding: 0px; border-bottom: 1px solid #555;}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
	tr.espaco td {padding: 12px; font-weight: bold;}
	tr.titulo td {border-bottom: 2.5px solid #555; }
</style>

	<h2> RELATÓRIO QUANTITATIVO DE TURMAS E COMPONENTES CURRICULARES POR DEPARTAMENTO </h2>

<f:view>

	<br>
	<div id="parametrosRelatorio">
		<table >
		<tr>
			<th>Ano - Período:</th>
			<td> ${relatorioTurma.ano }.${relatorioTurma.periodo }</td>
		</tr>
		</table>
	</div>
	
	
	<br><br>
	
	
	<table width="100%" class="tabelaRelatorioBorda">
		<thead>
		<tr>
			<th align="left"> Departamento</th>
			<th style="text-align: right;"> Qtde. Turmas</th>
			<th style="text-align: right;"> Qtde. Componentes Curriculares</th>
		</tr>
		</thead>
	
	<c:set var="ultimo" value=""/>
	<c:set var="centro" value=""/>
		
	<c:forEach items="#{relatorioTurma.listaTurma}" var="linha" varStatus="indice">
		<c:set var="centro" value="${linha.centro}"/>	
		<c:if test="${centro ne ultimo}">
			<c:if test="${ultimo ne '' }">
			<tr class="componentes">
				<td align="left" style="font-size: 10px"><b> Total por Centro </b></td>
				<td align="right"><b> ${_total_turma_centro}</b></td>
				<td align="right"><b> ${_total_disciplina_centro}</b></td>
				<c:set var="_total_turma_centro" value="0"/>
				<c:set var="_total_disciplina_centro" value="0"/>
			</tr>
			</c:if>
			<c:set var="ultimo" value="${centro}"/>
			<tr>
				<th align="left" colspan="3"> ${linha.centro}</th>
			</tr>
		</c:if>
		<tr class="componentes">
			<td align="left"> ${linha.departamento}</td>
			<td align="right"> ${linha.qt_turma}</td>
			<td align="right"> ${linha.qt_disciplina}</td>
		<c:set var="_total_turma_centro" value="${_total_turma_centro + linha.qt_turma}"/>
		<c:set var="_total_disciplina_centro" value="${_total_disciplina_centro + linha.qt_disciplina}"/>
		<c:set var="_total_turma" value="${_total_turma + linha.qt_turma}"/>
		<c:set var="_total_disciplina" value="${_total_disciplina + linha.qt_disciplina}"/>
		</tr>
		</c:forEach>
		<tr class="componentes">
			<td align="left"><b> Total </b></td>
			<td align="right"><b> ${_total_turma}</b></td>
			<td align="right"><b> ${_total_disciplina}</b></td>
		</tr>
	
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
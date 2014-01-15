<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	#relatorio h3.semRegistro {color: red;}
	#relatorio tr.itemRel td {padding: 1px 0 0 ;}
	#relatorio tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	#relatorio tr.header td {padding: 3px ; background-color: #eee;}
	#relatorio tr.subheader td {padding: 1px ; background-color: #eee;}
	#relatorio tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	#relatorio tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
	#relatorio tr.componentescur td {padding: 1px ; font-size: 11px; border-bottom: 1px dashed #888}
	#relatorio tr.componentescur2 td {padding: 2px ; font-size: 9px;}
</style>
<f:view>
	<hr>
	<h2><b>Relatório de Turmas Ofertadas ao Curso</b></h2>
	<table>
			<tr class="itemRel">
				<td>Curso:</td>
				<td><b><h:outputText value="#{relatorioTurma.curso.descricao}"/></b></td>
			</tr>
			<tr class="itemRel">
				<td>Ano-Período:</td>
				<td><b><h:outputText value="#{relatorioTurma.ano}"/>.<h:outputText value="#{relatorioTurma.periodo}"/></b></td>
			</tr>
	</table>
	<hr>
	<c:choose>
		<c:when test="${fn:length(relatorioTurma.turmas) ne 0 }">
			<h3><b>Total de Registros: ${fn:length(relatorioTurma.turmas)}</b></h3>
		</c:when>
		<c:otherwise>
		    <h3 class="semRegistro"><b>Nenhum Registro Encontrado</b></h3>
		</c:otherwise>
	</c:choose>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
	
	<c:set var="departamento"/>
	<c:forEach items="#{relatorioTurma.turmas}" var="turma">
		<c:set var="departamentoAtual" value="${turma.disciplina.unidade.id}"/>
		<c:if test="${departamento != departamentoAtual}">
			<c:set var="departamento" value="${departamentoAtual}"/>
			<tr class="curso">
				<td colspan="5">
					<b>${turma.disciplina.unidade.nome}</b>
				</td>
			</tr>
			<tr class="header">
				<td><b>Componente Curricular</b></td>
				<td><b>Turma</b></td>
				<td><b>Horário</b></td>
				<td><b>Vagas</b></td>
				<td><b>Docentes</b></td>
			</tr>
		</c:if>
		
   		<tr class="componentescur">
			<td>${ turma.disciplina.nome }</td>
			<td align="center">	${turma.codigo}</td>
			<td>${ turma.descricaoHorario }</td>
			<td align="center">	${ turma.capacidadeAluno}</td>
			<td>${ turma.docentesNomes }</td>
		</tr>
		
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

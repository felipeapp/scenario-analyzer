<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.parametro td {padding: 30px 0 0; border-bottom: 1px solid #555; }
	tr.curso td {padding: 30px 0 0; }
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.materia td {padding: 20px 0 0; border-bottom: 1px solid #555; font-weight: bold; }
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<tr>
			<td align="center"><b>RELATÓRIO DOS ALUNOS QUE SOLICITARAM DISCIPLINA DE FÉRIAS</b></td>
		</tr>
	</table>
	<br />
	
	<table width="100%">
		<tr class="parametro">
			<td>
				<b>Ano - Período:</b> 
				<h:outputText value="#{relatorioTurma.ano}"/>.<h:outputText value="#{relatorioTurma.periodo}"/>
			</td>
		</tr>
	</table>
	<br />

    <c:set var="_curso" />
    <c:set var="_materia" />

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<c:forEach items="#{relatorioTurma.listaTurma}" var="linha" varStatus="indice">

			<c:set var="cursoAtual" value="${linha.curso}"/>
			<c:set var="materiaAtual" value="${linha.componente_curricular}"/>
			
			  <c:if test="${_curso != cursoAtual}">
					<tr class="curso">
						<td colspan="10" style="font-size: 12px;"><b>${linha.curso}</b></td>
					</tr>
						<c:set var="_curso" value="${cursoAtual}"/>
 			  </c:if>
			  
			  <c:if test="${_materia != materiaAtual}">
					<tr class="materia">
						<td colspan="9"><b>${linha.componente_curricular}</b></td>
					</tr>
						<c:set var="_materia" value="${materiaAtual}"/>
					<tr class="header">
						<td align="center"> Matrícula</td>
						<td align="left" colspan="2"> Nome</td>
						<td align="center"> Código</td>
						<td align="right"> Situação</td>
					</tr>
 			  </c:if>
					<tr class="componentes">
						<td align="center">  ${linha.matricula}</td>
						<td align="left" colspan="2"> ${linha.aluno}</td>
						<td align="center"> ${linha.codigo}</td>
						<td align="right"> ${linha.situacao}</td>
						<c:set var="total" value="${total + 1}"/>
					</tr>
					
					<c:set var="proximo" value="${relatorioTurma.listaTurma[indice.index+1].componente_curricular}" ></c:set>
					
					<c:if test="${materiaAtual != proximo}">
						<tr class="total">
							<td align="right" colspan="4">Total:</td>
							<td align="right">${total}</td>
						</tr>
						<c:set var="total" value="0"/>
					</c:if>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
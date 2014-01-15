<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #DEDFE3; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><h2>Relatório de Turmas Abertas sem Solicitação</h2></td></b>
	</table>
	<br />
	<br />
	<div id="parametrosRelatorio">
		<table >
		<tr>
			<th>Ano-Período:</th>
			<td><h:outputText value="#{relatorioTurma.ano}"/>.<h:outputText value="#{relatorioTurma.periodo}"/></td>
		</tr>
		
		</table>
	</div>
	
	<br />
    <c:set var="_departamento" />
    <c:set var="_turma"/>
    <c:set var="_total" value="0"/>
    <c:set var="_totalGeral" value="0" />
    
	<table cellspacing="1" width="100%" style="font-size: 11px;">
	<c:forEach items="#{relatorioTurma.listaTurma}" var="linha" varStatus="indice">
		<c:set var="departamentoAtual" value="${linha.departamento}"/>
		
		<c:if test="${_departamento != departamentoAtual}">
			<tr class="titulo">
				<td colspan="8"><b>Departamento: ${linha.departamento}</b></td>
			</tr>
		</c:if>
			
		<c:set var="turmaAtual" value="${linha.nome_componente}"/>
		
		<c:if  test="${_departamento != departamentoAtual}">
			<tr class="header">
					<td align="center"> Turma </td>
					<td align="center"> Data de Criação</td>
					<td align="right"> Vagas</td>
					<td align="right"> Total de Matriculados </td>
					<td align="center"> Situação da Turma </td>
					<td align="left"> Usuário</td>
				</tr>
		</c:if>	
			
	    <c:if test="${_turma != turmaAtual }">
			<c:if test="${_turma != turmaAtual }">
					<tr class="componentes">
						<td colspan="10" style="font-size: 11px; background-color: #EFEFEF; "><b>${linha.codigo_componente} - ${linha.nome_componente}</b></td>
					</tr>
			</c:if>
			<c:set var="_departamento" value="${departamentoAtual}"/>
			<c:set var="_turma" value="${turmaAtual}"/>
		</c:if>
		<tr class="componentes">
			<td align="center">  ${linha.codigo_turma}</td>
			<td align="center"> <ufrn:format type="data" valor="${linha.data_criacao_turma}" /></td>
			<td align="right"> ${linha.vagas}</td>
			<c:if test="${ not empty linha.numero_matriculados}">
			<td align="right"> ${linha.numero_matriculados}</td>
			</c:if>
			<c:if test="${ empty linha.numero_matriculados}">
			<td align="right"> 0 </td>
			</c:if>
			<td align="center"> ${linha.situacao_turma}</td>
			<td align="left"> ${linha.usuario}</td>
			<c:set var="_total" value="${_total + 1}"/>
		</tr>
		
		<c:set var="proximo" value="${relatorioTurma.listaTurma[indice.index+1].departamento}" ></c:set>
		<c:set var="proximaTurma" value="${relatorioTurma.listaTurma[indice.index+1].nome_componente}" ></c:set>
		
		<c:if test="${departamentoAtual != proximo }">
			<tr class="componentes" >
				<td align="right" colspan="5"><b>Total de Turmas:</b></td>
				<td align="right"><b>${_total}</b></td>
				<c:set var="_totalGeral" value="${_totalGeral + _total}"/>
			</tr>
			<tr class="espaco">
				<td colspan="8">&nbsp;</td>
			</tr>
			<c:set var="_total" value="0"/>
		</c:if>
	</c:forEach>
	<tr class="titulo">
		<td colspan="6" align="right"><b>Total Geral de Turmas: ${_totalGeral}<b/></td>
	</tr>
	</table>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
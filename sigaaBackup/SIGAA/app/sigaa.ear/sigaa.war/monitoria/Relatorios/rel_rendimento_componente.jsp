<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #DEDFE3; border: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><h2>Relatório de Análise de Rendimentos por Departamento</h2></td></b>
	</table>
	<br />
	<div id="parametrosRelatorio">
		<table >
			<c:if test="${relatorioRendimentoComponente.departamentoComponente.id > 0}">
			<tr>
				<th>Departamento:</th>
				<td> <h:outputText value="#{relatorioRendimentoComponente.departamentoComponente.nome}" /></td>
			</tr>
			</c:if>
			<c:if test="${relatorioRendimentoComponente.ano > 0}">
			<tr>
				<th>Período:</th>
				<td> <h:outputText value="#{relatorioRendimentoComponente.ano}" /> até <h:outputText value="#{relatorioRendimentoComponente.anoFim}" /></td>
			</tr>
			</c:if>

			<c:if test="${relatorioRendimentoComponente.codigo != null}">
			<tr>
				<th>Código da Disciplina:</th>
				<td> <h:outputText value="#{relatorioRendimentoComponente.codigo}" /></td>
			</tr>
			</c:if>

			<c:if test="${relatorioRendimentoComponente.docente.id > 0}">
			<tr>
				<th>Docente:</th>
				<td> <h:outputText value="#{relatorioRendimentoComponente.docente.nome}" /></td>
			</tr>
			</c:if>

				 
		</table>
	</div>
	
	<br />
   
    <c:set var="_total" value="0"/>
    <c:set var="_totalGeral" value="0"/>
    
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="titulo">
			<td colspan="8"></td>
		</tr>
		
		<tr class="header">
				<td align="left">Componente Currricular</td>
				<td align="center">Período</td>
				<td align="center">Turma</td>
				<td align="center">Projetos</td>
				<td align="right">AP</td>
				<td align="right">REP</td>
				<td align="right">REPf</td>
				<td align="right">TRAN</td>
				<td align="right">Média</td>
				<td align="right">Total</td>
			</tr>
		
		<c:forEach items="#{relatorioRendimentoComponente.lista}" var="linha" varStatus="indice">
		<tr class="componentes" bgcolor="<c:if test="${linha.ccm > 0}">#D3D3D3</c:if>">
			<c:if test="${linha.total != 0 }">
				
				<c:set var="reprovadoFalta" value="#{100 - ( ((linha.ap * 100)/linha.total) + ((linha.rp * 100)/linha.total) + ((linha.tr * 100)/linha.total)  )}" />
				
				<td align="left">
					${linha.codigo} - ${linha.nome}<br/>
					<i><small>${linha.prof}</small></i>
				</td> 
				<td align="center">${linha.ano_turma}.${linha.periodo_turma}</td>
				<td align="center">T${linha.codigo_turma}</td>
				<td align="center">${linha.ccm}</td>
				<td align="right"><h:outputText value="#{(linha.ap * 100)/linha.total}%" title="#{(linha.ap * 100)/linha.total }% Aprovados" /></td> 
				<td align="right"><h:outputText value="#{(linha.rp * 100)/linha.total }%" title="#{(linha.rp * 100)/linha.total }% Reprovados" /></td> 
				<td align="right"><h:outputText value="#{reprovadoFalta}%" title="#{reprovadoFalta}% Reprovados por Falta" /></td>
				<td align="right"><h:outputText value="#{(linha.tr * 100)/linha.total }%" title="#{(linha.tr * 100)/linha.total }% Tracamentos" /></td>
				<td align="right"><fmt:formatNumber value="${linha.media_turma}" pattern="##.0" /></td>
				<td align="right">${linha.total}</td>
			</c:if>
		</tr>
		
		</c:forEach>
		
	</table>
	[<b>Projetos</b> = Nº de Projetos de Monitoria ativos compatíveis com o componente curricular, <b>AP</b> = Aprovados, <b>RP</b> = Reprovados, <b>REPf</b> = Reprovados por falta, 
	<b>TRAN</b> = Trancamentos, <b>Média</b> = Média aritimética considerando apenas aprovados, <b>Total</b> = Total de discentes ]
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
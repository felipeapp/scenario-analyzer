<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000; border-right: 1px solid #000 !important; border-right: 2px solid #000 }
	tr.componentes td {padding: 5px 2px 2px; border: 1px solid #000; border-right: 1px solid #000 !important; border-right: 2px solid #000;}
	tr.titulo td {border-bottom: 2.5px solid #555; }
	tr.curso td {padding: 20px 0 0 0; border-bottom: 1px solid #555;}
</style>

	

<f:view>
	
	<div style="width: 95%">
	<table width="100%" align="center" style="font-size: 11px;">
		<caption><td align="center"><h2>RELATÓRIO DE ALUNOS COM NECESSIDADES EDUCACIONAIS ESPECIAS POR PROCESSO SELETIVO</h2></td></b>
	</table>
	</div>
	<br>
	<div id="parametrosRelatorio">
		<table>
		<c:forEach items="#{relatorioNee.filtroList}" var="filtro" varStatus="indice">
		<tr>
			<td> ${ filtro }</td>
		</tr>
		</c:forEach>
		</table>
	</div>
	
	<c:set var="curso_old" />
	<table width="96%" align="center"  style="font-size: 10px;" class="relatorio">
		<c:forEach items="#{relatorioNee.listaDiscente}" var="linha" varStatus="indice">
			<c:set var="curso_new_old" value="${linha.descricao_curso}"/>
			  <c:if test="${curso_old != curso_new_old}">
				<tr class="curso">
					<td colspan="10" style="text-align: left;"><b>Curso: ${linha.descricao_curso}</b></td>
				</tr>
				<tr class="header">
					<td style="text-align: left"> Aluno</td>
					<td style="text-align: center"> Matrícula</td>
					<td style="text-align: left"> Necessidade Educacional Especial</td>
					<td style="text-align: center"> Ano Ingresso</td>
				</tr>
				<c:set var="curso_old" value="${curso_new_old}"/>
			</c:if>
		<tr class="componentes">
			<td align="left"> ${linha.nome}</td>
			<td align="center"> ${linha.matricula}</td>
			<td align="left"> ${linha.tipo_necessidade}</td>
			<td align="center"> ${linha.anoingresso}</td>
		</tr>
		</c:forEach>
	</table>

	<table width="100%" align="center" style="font-size: 11px; clear: both; margin-top: 5px; font-weight: bold"  >
		<caption><td align="center">Total de Alunos: ${ fn:length(relatorioNee.listaDiscente) }</td></caption>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
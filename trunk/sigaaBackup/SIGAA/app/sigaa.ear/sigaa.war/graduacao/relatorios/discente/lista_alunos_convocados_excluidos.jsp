<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555;}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
</style>

<f:view>
	<h2><b><h:outputText value="#{relatorioProcessoSeletivoDiscente.relatorio}" /></b></h2>

	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Processo Seletivo:</th>
				<td> ${ relatorioProcessoSeletivoDiscente.descricaoProcessoSeletivo } </td>
			</tr>
			<tr>
				<th>Chamada:</th>
	 			<td> ${ relatorioProcessoSeletivoDiscente.descricaoChamada } </td>
		    </tr>
	    </table>
	</div>
	<br/>
	<div align="center"><b>Total de Alunos Ausentes no Período de Cadastramento: ${ fn:length(relatorioProcessoSeletivoDiscente.listaDiscente) }</b></div>
    <br/>
    <c:set var="curso_old" />
    <c:set var="qtd_aluno_curso" value="0"/>
	<table cellspacing="1" width="100%" style="font-size: 10px;" class="tabelaRelatorioBorda">
		<tbody>
			<c:forEach items="${relatorioProcessoSeletivoDiscente.listaDiscente}" var="linha">
				<c:set var="curso_new_old" value="${linha.curso} - ${linha.cidade}"/>
				  <c:if test="${curso_old != curso_new_old}">
					<c:if test="${ qtd_aluno_curso > 0 }">
						<td align="right" colspan="4"><b></>Total: ${ qtd_aluno_curso }</b></td>
					</c:if>	
					<c:set var="qtd_aluno_curso" value="0"/>
					<tr class="curso">
						<td colspan="10" style="text-align: left;"><b>Curso: ${linha.curso} - ${linha.cidade}</b></td>
					</tr>
					<tr class="header">
						<td align="right">Inscrição</td>
						<td>Nome</td>
						<td align="right">Identidade</td>
						<c:if test="${ relatorioProcessoSeletivoDiscente.convocacao == 0}">
							<td align="left">Chamada</td>
						</c:if>
					</tr>
					<c:set var="curso_old" value="${curso_new_old}"/>
				</c:if>
				<tr>
					<td align="right"> ${linha.numeroinscricao}</td>
					<td>
						 ${linha.nome}
						 <c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
					</td>
					<td align="right"> ${linha.identidade} </td>
					<c:if test="${ relatorioProcessoSeletivoDiscente.convocacao == 0}">
						<td align="left"> ${linha.descricaoconvocacao} </td>
					</c:if>	
					<c:set var="qtd_aluno_curso" value="${ qtd_aluno_curso + 1 }"/>
				</tr>
			</c:forEach>
			<td align="right" colspan="4"><b></>Total: ${ qtd_aluno_curso }</b></td>
			<tr><td align="right" colspan="4"><b> Total de Alunos Ausentes no Período de Cadastramento: ${ fn:length(relatorioProcessoSeletivoDiscente.listaDiscente) } </b></td></tr>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
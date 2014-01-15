<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px; font-weight: bold; background-color: #DEDFE3;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; }
	tr.observacao td {padding: 4px 2px 2px ; font-style: italic; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h2>Lista de Alunos que Concluíram o Programa</h2>
	<div id="parametrosRelatorio">
		<table>
			<caption><b></b></caption>
			<tr>
				<th>Unidade:</th>
				<td> ${usuario.vinculoAtivo.unidade.nome} </td>
			</tr>
			<tr>
				<th>Ano-Período:</th>
				<td><h:outputText value="#{relatoriosTecnico.ano}"/>.<h:outputText value="#{relatoriosTecnico.periodo}"/></td>
			</tr>
		</table>
	</div>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<c:set var="curso_"/>
		<c:set var="especializacao_"/>
		<c:forEach items="${relatoriosTecnico.lista}" var="linha">
				<c:set var="cursoAtual" value="${linha.id_curso}"/>
				<c:set var="especializacaoAtual" value="${linha.id_especializacao_turma_entrada}"/>
			<c:if test="${curso_ != cursoAtual}">
				<c:set var="curso_" value="${cursoAtual}"/>
				<tr class="curso">
					<td colspan="4">
						<br>
						<b>${linha.curso_nome}</b>
					</td>
				</tr>
				<tr class="header">
					<td style="text-align: center">Ingresso</td>
					<td style="text-align: center">Matrícula</td>
					<td>Nome</td>
					<td>Situação</td>
				</tr>
			</c:if>
			<c:if test="${especializacao_ != especializacaoAtual && not empty linha.especializacao_nome}">
				<c:set var="especializacao_" value="${especializacaoAtual}"/>
				<tr>
					<td colspan="4">
						<br>
						<b>${linha.especializacao_nome}</b>
						<hr>
					</td>
				</tr>
			</c:if>
			<tr class="componentes">
				<td style="text-align: center">
					${linha.ano_ingresso}.${linha.periodo_ingresso}
				</td>
				<td style="text-align: center">
					${linha.matricula}
				</td>
				<td>
					${linha.aluno_nome}
				</td>
				<td>
					${linha.descricao}
				</td>
			</tr>
			<tr class="observacao">
				<td colspan="4">
					<c:if test="${ not empty linha.observacao }">
						<b>Observação:</b> ${linha.observacao}
					</c:if>
				</td>
			</tr>
		</c:forEach>
		<tfoot>
			<tr>
				<td colspan="4" style="text-align: center; font-weight: bold; padding-top: 20px">
					Total de Alunos: <h:outputText value="#{relatoriosTecnico.numeroRegistrosEncontrados}"/>
				</td>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
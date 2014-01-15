<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>

<h2>Lista de Alunos Ingressantes</h2>

<div id="parametrosRelatorio">
	<table >
			<tr>
				<th>Unidade:</th>
				<td>${usuario.vinculoAtivo.unidade.nome}</td>
			</tr>
			<tr>
				<th>Ano-período de Ingresso:</th>
				<td><h:outputText value="#{relatoriosTecnico.ano}"/>.<h:outputText value="#{relatoriosTecnico.periodo}"/></td>
			</tr>
	</table>
</div>	
<br />
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		
	<c:set var="curso_"/>
	
	<c:forEach items="${relatoriosTecnico.lista}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			
		<c:if test="${curso_ != cursoAtual}">
			<c:set var="curso_" value="${cursoAtual}"/>
			<tr class="curso">
				<td colspan="5">
					<br>
					<b>${linha.curso_nome}</b>
					<hr>
				</td>
			</tr>
			<tr class="header">
				<td style="text-align: center;">Ingresso</td>
				<td style="text-align: center;">Matrícula</td>
				<td>Nome</td>
				<td>Situação</td>
				<td>
					<c:if test="${not empty linha.especializacao_nome}">
						Instrumento
					</c:if>
				</td>
			<tr>
		</c:if>
		
		<tr class="componentes">
			<td style="text-align: center;"> ${linha.ano_ingresso}.${linha.periodo_ingresso} </td>
			<td style="text-align: center;"> ${linha.matricula}	</td>
			<td> ${linha.aluno_nome} </td>
			<td>
				<c:choose>
					<c:when test="${linha.status == 1}">ATIVO</c:when>
					<c:when test="${linha.status == 2}">CADASTRADO</c:when>
					<c:when test="${linha.status == 3}">CONCLUÍDO</c:when>
					<c:when test="${linha.status == 4}">AFASTADO</c:when>
					<c:when test="${linha.status == 5}">TRANCADO</c:when>
					<c:when test="${linha.status == 6}">CANCELADO</c:when>
					<c:when test="${linha.status == 7}">JUBILADO</c:when>
					<c:otherwise>Status inválido</c:otherwise>
				</c:choose> 
			</td>
			<td> 
				<c:if test="${not empty linha.especializacao_nome}">
					${linha.especializacao_nome} 
				</c:if>
			</td>
		</tr>
	</c:forEach>
	</table>
	
	<br /><br />
	
	<table width="100%">
		<tr align="center">
			<td colspan="3"><b>Total de Registros: <h:outputText value="#{relatoriosTecnico.numeroRegistrosEncontrados}"/></b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

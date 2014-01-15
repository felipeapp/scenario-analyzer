<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #000000; color: black;}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #000000;  width: 40%;}
	tr.cursoVar td {border-bottom: 1px solid #000000;}
	hr{color: #000 !important; background-color: #000 !important;}
</style>

<f:view>
	<c:set var="cursoVar"/>
	<c:set var="matrizVar"/>
	<c:set var="total"/>
	<c:set var="totalGeral" value="${0}"/>
	<c:set var="graduacao" value="<%=NivelEnsino.GRADUACAO %>" />
	<c:set var="mestrado" value="<%=NivelEnsino.MESTRADO %>" />
	<c:set var="doutorado" value="<%=NivelEnsino.DOUTORADO %>" />

	<h2 class="tituloTabela"><b>Lista de Aluno para Eleição</b></h2>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="matrizAtual" value="${linha.id_matriz_curricular}"/>
			<c:set var="totalGeral" value="${totalGeral+1}"/>
				
			<c:if test="${cursoVar != cursoAtual || matrizVar != matrizAtual}">
				<c:if test="${!empty cursoVar}">
					<tr class="foot">
						<td colspan="2">Total: ${total} </td>
					</tr>
					<tr><td colspan="2">&nbsp;</td></tr>
				</c:if>
				
				<c:set var="cursoVar" value="${cursoAtual}"/>
				<c:set var="matrizVar" value="${matrizAtual}"/>
				<c:set var="total" value="0"/>
				<tr class="cursoVar">
					<td colspan="2">
						<b>Programa:</b> ${linha.centro}<br />
						<b>Curso:</b> ${linha.curso_nome}
						<c:if test="${linha.nivel == mestrado }">(MESTRADO)</c:if>
						<c:if test="${linha.nivel == doutorado }">(DOUTORADO)</c:if><br />
						<b>Município:</b> ${linha.municipio_nome}
						<i>${linha.turno_descricao} - ${linha.modalidade_aluno} - ${ empty linha.habilitacao_aluno? "MODALIDADE SEM HABILITAÇÃO": linha.habilitacao_aluno }</i><br />
						<c:if test="${linha.nivel == graduacao }">
							<b>Incluir Formandos?</b> 
							<c:if test="${relatorioDiscente.filtroAtivo}">SIM</c:if>
							<c:if test="${!relatorioDiscente.filtroAtivo}">NÃO</c:if><br />
						</c:if>
						<b>Somente Matriculados no Período Atual?</b> 
						<c:if test="${relatorioDiscente.matriculados}">SIM</c:if>
						<c:if test="${!relatorioDiscente.matriculados}">NÃO</c:if><br />
					</td>
				</tr>
				<tr class="header">
					<td><b>Discente</b></td>
					<td><b>Assinatura</b></td>
				</tr>
			</c:if>
			
			<c:set var="total" value="${total+1}"/>
			
			<tr class="componentes">
				<td>
					${linha.matricula} -  ${linha.aluno_nome }
					<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
				</td>
				<td class="assinatura">
				</td>
			</tr>
		</c:forEach>
		
		<c:if test="${total>0}">
			<tr class="foot">
				<td colspan="2">Total: ${total} </td>
			</tr>
			<tr><td colspan="2">&nbsp;</td></tr>
			<tr>
				<td colspan="2" align="center"><b>TOTAL DE ALUNOS:</b> ${totalGeral} </td>
			</tr>
		</c:if>
		
		<c:if test="${total<1}">
			<p><i>Nenhum resultado encontrado.</i></p>
		</c:if>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
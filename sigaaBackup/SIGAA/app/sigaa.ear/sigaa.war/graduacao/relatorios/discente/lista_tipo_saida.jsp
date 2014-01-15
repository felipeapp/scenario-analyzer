<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	span.semRegistro {font: normal; color: red;}
	tr.itemRel td {padding: 1px 0 0 ;}
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
</style>
<f:view>
	<hr>
	<h2><b>Relatório de Alunos pelo seu tipo de saída</b></h2>
	<table>
			<tr class="itemRel">
				<td>Ano-Período Saída:</td>
				<td><b><h:outputText value="#{relatorioDiscente.ano}"/>-<h:outputText value="#{relatorioDiscente.periodo}"/></b>
				</td>
			</tr>
			<tr class="itemRel">
				<td>Status do Discente:</td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroAtivo ? relatorioDiscente.statusDescricao : 'TODOS'}"/></b>
				</td>
			</tr>
			<tr class="itemRel">
				<td>Forma Ingresso:</td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroIngresso ? relatorioDiscente.formaIngresso.descricao : 'TODAS'}"/></b>
				</td>
			</tr>
			<tr class="itemRel">
				<td>Egresso (Saída):</td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroEgresso ? relatorioDiscente.tipoMovimentacaoAluno.descricao : 'TODOS'}"/></b>
				</td>
			</tr>
			<tr class="itemRel">
				<td>Unidade:</td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroCentro ? relatorioDiscente.centro.nome : 'TODOS'}"/></b>
				</td>
			</tr>
			<tr class="itemRel">
				<td >Curso:</td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroCurso ? relatorioDiscente.curso.descricao : 'TODOS'}"/></b>
				</td>
			</tr>
			<tr class="itemRel">
				<td>Matriz Curricular:</td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroMatriz ? relatorioDiscente.matrizCurricular.descricaoMin : 'TODAS'}"/></b>
				</td>
			</tr>

	</table>
	<hr>
	<h3><b>Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></b></h3>

	<c:if test="${relatorioDiscente.numeroRegistosEncontrados == 0}">
		<span class="semRegistro"></span>
	</c:if>
	<table cellspacing="1" width="100%" style="font-size: 10px;">

	<c:set var="cursoLoop"/>
	<c:set var="matrizLoop"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="matrizAtual" value="${linha.id_matriz_curricular}"/>
		<c:if test="${cursoLoop != cursoAtual || matrizLoop != matrizAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="matrizLoop" value="${matrizAtual}"/>
			<tr class="curso">
				<td colspan="4">
					<b>${linha.centro}	- ${linha.curso_aluno} - ${linha.municipio_nome}<br>
					<i>${linha.turno_descricao} - ${linha.modalidade_nome} - ${ empty linha.habilitacao_nome? "MODALIDADE SEM HABILITAÇÃO": linha.habilitacao_nome }</i></b>
				</td>
			</tr>
			<tr class="header">
				<td><b>Discente</b></td>
				<td><b>Tipo de Entrada</b></td>
				<td><b>Tipo de Saída</b></td>
				<td><b>Status Atual</b></td>
			<tr>
		</c:if>
		<tr class="componentes">
			<td>
				${linha.matricula} -  ${linha.aluno_nome }
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td>
				${linha.forma_ingresso }
			</td>
			<td>
				${linha.aluno_tipo_saida }
			</td>
			<td>
				${linha.aluno_status }
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

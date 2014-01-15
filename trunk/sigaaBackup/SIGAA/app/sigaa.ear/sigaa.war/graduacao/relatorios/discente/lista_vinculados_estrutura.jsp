<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<table width="100%">
		<br>
		<caption><b>LISTA DE ALUNOS  ${relatorioDiscente.filtroAtivo ? 'ativos' : '' }  VINCULADOS A UM ESTRUTURA CURRICULAR</b></caption>
		<tr><th>&nbsp;</th></tr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<c:set var="cursoLoop"/>
	<c:set var="matrizLoop"/>
	<c:set var="curriculoLoop"/>
	<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="matrizAtual" value="${linha.id_matriz_curricular}"/>
			<c:set var="curriculoAtual" value="${linha.codigo_curriculo}"/>
		<c:if test="${cursoLoop != cursoAtual || matrizLoop != matrizAtual || curriculoLoop != curriculoAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="matrizLoop" value="${matrizAtual}"/>
			<c:set var="curriculoLoop" value="${curriculoAtual}"/>
			<tr>
				<td colspan="4">
					<br>
					<b>${linha.centro}	- ${linha.curso_nome} - ${linha.municipio_nome}<br>
					 <i>${linha.turno_desc} - ${linha.modalidade_curso} - ${linha.habilitacao_nome } - Currículo ${linha.codigo_curriculo }</i></b>
					<hr>
				</td>
			</tr>
			<tr>
					<td><b>Ingresso</b></td>
					<td><b>Discente</b></td>
			<tr>
		</c:if>
		<tr>
			<td>
				${linha.ano_ingresso}-${linha.periodo_ingresso}
			</td>
			<td>
				${linha.matricula}	- ${linha.aluno_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
		</tr>
	</c:forEach>
	</table>
	</table>
	<br></br>
	<hr>
	<center>
			<b>Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></b>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

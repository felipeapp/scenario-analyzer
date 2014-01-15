<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Quantitativos de Alunos Ativos de Probásica sem matrícula</b></caption>
			<tr>
				<th>Curso:</th>
				<td><b><h:outputText
					value="#{relatorioDiscente.curso.descricao }" /></b></td>
				<th>Centro:</th>
				<td><b><h:outputText
					value="#{relatorioDiscente.curso.unidade.nome }" /></b></td>
			</tr>
	</table>


		<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></b></caption>
	<c:set var="curso"/>
    <c:set var="grauAcademico"/>
    <c:set var="habilitacao"/>
    <c:set var="totalHomens " value="0"/>
    <c:set var="totalHomensGrupo" value="0"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha" varStatus="row">
			<c:set var="cursoAtual" value="${linha.curso_nome}"/>
			<c:set var="grauAcademicoAtual" value="${linha.modalidade_aluno}"/>
			<c:set var="habilitacaoAtual" value="${linha.habilitacao_nome}"/>
		<c:if test="${(curso != cursoAtual || grauAcademico != grauAcademicoAtual || habilitacao != habilitacaoAtual) && row.index!=0}">
		    <tr>
    			<td colspan=3><hr></td>
    		</tr>
    		<tr>
    			<td align="right"><b>Total:</b></td>
    			<td><b>${totalHomensGrupo}</b></td>
    		</tr>
		</c:if>
		<c:if test="${curso != cursoAtual || grauAcademico != grauAcademicoAtual || habilitacao != habilitacaoAtual}">
			<c:set var="totalHomensGrupo" value="0"/>
			<c:set var="curso" value="${cursoAtual}"/>
			<c:set var="grauAcademico" value="${grauAcademicoAtual}"/>
			<c:set var="habilitacao" value="${habilitacaoAtual}"/>
			<tr>
				<td colspan="3">
					<br>
					<b>${linha.centro}	-  ${linha.curso_nome} - ${linha.modalidade_aluno } - ${linha.habilitacao_nome } </b><br>
					<hr>
				</td>
			</tr>
			<tr>
				<td><b>Cidade</b></td>
				<td><b>Quantidade</b></td>
			</tr>
		</c:if>
		 <c:set var="totalHomensGrupo" value="${totalHomensGrupo + linha.qtd_nao_matriculados}"/>
		 <c:set var="totalHomens" value="${totalHomens + linha.qtd_nao_matriculados}"/>
		<tr>

			<td>
				${linha.municipio_nome}
			</td>
			<td>
				${linha.qtd_nao_matriculados}
			</td>
		</tr>
	</c:forEach>
		 <tr>
    			<td colspan=3><hr></td>
    		</tr>
    		<tr>
    			<td align="right"><b>Total:</b></td>
    			<td><b>${totalHomensGrupo}</b></td>
    		</tr>
	</table>
	<table width="100%">
		<tr>
			<td colspan="2"> <hr></td>
		</tr>
		<tr align="center">
			<td colspan="2"><b>Total de Matriculados: ${totalHomens}</b></td>
		</tr>
		<tr>
			<td colspan="2"><hr><br></td>
		</tr>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

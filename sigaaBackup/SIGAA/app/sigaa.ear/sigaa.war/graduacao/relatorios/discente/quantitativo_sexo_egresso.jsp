<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Relatório de Quantitativos de Alunos por Sexo e Egresso</b></caption>
			<tr>
				<th width="10%" nowrap="nowrap">Ano-Período:</th>
				<td colspan="3"><b><h:outputText value="#{relatorioDiscente.ano}"/>-<h:outputText value="#{relatorioDiscente.periodo}"/></b>
				</td>
			</tr>
			<tr>
				<th>Centro:</th>
				<td colspan="3"><b><h:outputText
					value="#{relatorioDiscente.centro.nome }" /></b></td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></b></caption>
	<c:set var="cursoLoop"/>
    <c:set var="municipioLoop"/>
    <c:set var="totalHomens " value="0"/>
    <c:set var="totalMulher" value="0"/>
    <c:set var="totalHomensGrupo" value="0"/>
    <c:set var="totalMulheresGrupo" value="0"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha" varStatus="row">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="municipioAtual" value="${linha.id_municipio}"/>
		<c:if test="${cursoLoop != cursoAtual || municipioLoop != municipioAtual}">
			<c:set var="totalHomensGrupo" value="0"/>
		    <c:set var="totalMulheresGrupo" value="0"/>
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="municipioLoop" value="${municipioAtual}"/>
			<tr>
				<td colspan="3">
					<br>
					<b>${linha.centro}	-  ${linha.curso_nome} -  ${linha.municipio} </b><br>
					<hr>
				</td>
			</tr>
			<tr>
				<td><b>Forma de Egresso</b></td>
				<td><b>Homens</b></td>
				<td><b>Mulheres</b></td>
			</tr>
		</c:if>
		 <c:set var="totalHomensGrupo" value="${totalHomensGrupo + linha.qtd_masculino}"/>
		 <c:set var="totalMulheresGrupo" value="${totalMulheresGrupo + linha.qtd_feminino}"/>
		 <c:set var="totalHomens" value="${totalHomens + linha.qtd_masculino}"/>
		 <c:set var="totalMulher" value="${totalMulher + linha.qtd_feminino}"/>
		<tr>
			<td>
				${linha.movimentacao_aluno}
			</td>
			<td>
				${linha.qtd_masculino}
			</td>
			<td>
				${linha.qtd_feminino}
			</td>
		</tr>
		
		<tr>
   			<td colspan=3><hr></td>
   		</tr>
   		<tr>
   			<td align="right"><b>Total:</b></td>
   			<td><b>${totalHomensGrupo}</b></td>
   			<td><b>${totalMulheresGrupo}</b><br></td>
   		</tr>

	</c:forEach>
	<tfoot>
	</table>
	<br />
	<table width="100%">
		<tr align="center">
			<td><b>Total de Homens: ${totalHomens}</b></td>
			<td><b>Total de Mulheres: ${totalMulher}</b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

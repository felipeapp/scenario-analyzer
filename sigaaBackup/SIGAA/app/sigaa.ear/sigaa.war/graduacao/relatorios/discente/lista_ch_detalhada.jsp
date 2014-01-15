"<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>
	<c:set var="cursoLoop"/>
	<c:set var="turnoLoop"/>
	<c:set var="habilitacaoLoop"/>
    <c:set var="curriculoLoop"/>
	<c:forEach items="#{resultado}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>
			<c:set var="habilitacaoAtual" value="${linha.id_habilitacao}"/>
			<c:set var="curriculoAtual" value="${linha.curriculo_codigo}"/>
		<c:if test="${cursoLoop != cursoAtual || turnoLoop != turnoAtual || habilitacaoLoop != habilitacaoAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="turnoLoop" value="${turnoAtual}"/>
			<c:set var="habilitacaoLoop" value="${habilitacaoAtual}"/>
			<c:set var="curriculoLoop"/>
			<tr>
				<td colspan="8">
					<br>
					<b>${linha.centro}	- ${linha.curso_nome}  - ${linha.habilitacao} / ${linha.sigla_turno} </b>
					<hr>
				</td>
			</tr>
		</c:if>
		<c:if test="${curriculoLoop != curriculoAtual}">
		    <c:set var="curriculoLoop" value="${curriculoAtual}"/>
			<tr>
				<td colspan="8">
					<br>
					<b>Currículo: ${linha.curriculo_codigo}</b>
					<hr>
				</td>
			</tr>
			<tr>
				<td> </td>
				<td colspan="7" style="text-align: center; border: 1px solid black; border-bottom-color: white">CH</td>
			</tr>
			<tr>
				<td>Discente</td>
				<td style="text-align: center; border: 1px solid white; border-left-color: black; border-bottom-color: black;">Disc.</td>
				<td style="text-align: center; border: 1px solid white; border-bottom-color: black;">Compl.</td>
				<td style="text-align: center; border: 1px solid white; border-bottom-color: black;">Obrig.</td>
				<td style="text-align: center; border: 1px solid white; border-bottom-color: black;">Cumprida Disc.</td>
				<td style="text-align: center; border: 1px solid white; border-bottom-color: black;">Cumprida Compl.</td>
				<td style="text-align: center; border: 1px solid white; border-bottom-color: black;">Integ.</td>
				<td style="text-align: center; border: 1px solid white; border-right-color:black; border-bottom-color: black;">Pendente Disc.</td>
			</tr>
		</c:if>
		<tr>
			<td>
				${linha.matricula} - ${linha.aluno_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td style="text-align: center;">
				${linha.ch_nao_atividade_obrigatoria}
			</td>
			<td style="text-align: center;">
				${linha.ch_optativas_minima}
			</td>
			<td style="text-align: center;">
				${linha.ch_total_minima}
			</td>
			<td style="text-align: center;">
				${linha.ch_nao_atividade_obrig_integ}
			</td>
			<td style="text-align: center;">
				${linha.ch_optativa_integralizada}
			</td>
			<td style="text-align: center;">
				${linha.ch_total_integralizada}
			</td>
			<td style="text-align: center;">
				${linha.ch_nao_atividade_obrig_pendente}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

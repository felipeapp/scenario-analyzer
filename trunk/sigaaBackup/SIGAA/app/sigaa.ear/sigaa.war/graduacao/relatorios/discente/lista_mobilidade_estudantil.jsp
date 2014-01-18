<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<f:view>
	
<c:if test="${not empty resultado}">
	<h2>Discentes com Mobilidade Estudantil</h2>
	
	<c:set var="anoPeriodo" value="${relatorioDiscente.ano}${relatorioDiscente.periodo ne null ? '.' : ''}${relatorioDiscente.periodo}" />
	<c:set var="anoPeriodoFim"  value="${relatorioDiscente.anoFim}${relatorioDiscente.periodoFim ne null ? '.' : ''}${relatorioDiscente.periodoFim}"/>
	
	<h3>Prazo de Mobilidades Estudantis selecionado: 
		${anoPeriodo ne '' ? anoPeriodo : 'Não Informado' }	à ${anoPeriodoFim ne '' ? anoPeriodoFim : 'Não Informado' }</h3>
	<br/>
	
	<c:set var="cursoLoop"/>
	<c:set var="turnoLoop"/>
	<c:set var="cidadeLoop"/>
    <c:set var="grauAcademicoLoop"/>
    <c:set var="fechaTabela" value="false" />
	<c:forEach items="${resultado}" var="linha">
		<c:set var="cursoAtualLoop" value="${linha.id_curso}"/>
		<c:set var="turnoAtual" value="${linha.id_turno}"/>
		<c:set var="cidadeAtual" value="${linha.id_municipio}"/>
		<c:set var="grauAcademicoAtual" value="${linha.id_grau_academico}"/>
		<c:if test="${cursoLoop != cursoAtualLoop || turnoLoop != turnoAtual || cidadeLoop != cidadeAtual || grauAcademicoLoop != grauAcademicoAtual}">
			<c:if test="${fechaTabela}">
					</tbody>
				</table>
				<br/>
			</c:if>
			<c:set var="fechaTabela" value="true" />
			<table class="tabelaRelatorio" width="100%">
				<c:set var="cursoLoop" value="${cursoAtualLoop}"/> 
				<c:set var="turnoLoop" value="${turnoAtual}"/>
				<c:set var="cidadeLoop" value="${cidadeAtual}"/>
				<c:set var="grauAcademicoLoop" value="${grauAcademicoAtual}"/>
				<thead>
					<tr>
						<th colspan="8">
							${linha.unidade_sigla}- ${linha.curso_nome} - ${linha.modalidade_nome} - ${linha.turno_sigla} - ${linha.municipio}
						</th>
					</tr>
					<tr>
						<th style="text-align: center;">Matrícula</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: center;">Ano-Período</th>
						<th style="text-align: left;">Tipo</th>
						<th style="text-align: left;">Sub-Tipo</th>
						<th style="text-align: left;">País</th>
						<th style="text-align: left;">Instituição</th>
						<th style="text-align: left;">Status do Discente</th>
					</tr>
				</thead>
				<tbody>
		</c:if>
		<tr>
			<td style="text-align: center;" width="5%"> ${linha.matricula}</td>
			<td style="text-align: left;" width="30%"> 
				${linha.aluno_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td style="text-align: center;" width="20%"> ${linha.ano_mobilidade}.${linha.periodo_mobilidade} à ${linha.periodo_mob_final} </td>
			<td style="text-align: left;" width="10%"> ${linha.desc_tipo_mobilidade} </td>
			<td style="text-align: left;" width="10%"> ${linha.subtipo} </td>
			<td style="text-align: 'left';" width="10%"> ${linha.tipo_mobilidade == '2' && linha.subtipo_mobilidade == '2' ? linha.pais_externa : '  -'} </td>
			<td style="text-align: left;" width="20%"> ${linha.tipo_mobilidade == '1' ? linha.campus_destino : linha.ies_externa } </td>
			<td style="text-align: left;" width="15%"> ${linha.status_aluno} </td>
		</tr>
	</c:forEach>
	<c:if test="${fechaTabela}">
			</tbody>
		</table>
	</c:if>
	<BR/>
	<div align="center"><b>Mobilidades Estudantis ativas: ${ fn:length(resultado) }</b></div>
</c:if>
<br/>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

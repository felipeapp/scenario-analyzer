<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
}
</style>
<f:view>
	<table width="100%">
	<caption><b>LISTA DE ALUNOS LAUREADOS</b></caption>
	<br>
	</table>
		<br><br>
		<div id="parametrosRelatorio">
		<table>
			<tr>
				<td align="left">
					<b>Ano-Período:</b>
					<td colspan="3">
						<h:outputText value="#{relatorioDiscente.ano}"/>.<h:outputText value="#{relatorioDiscente.periodo}"/>
				</td>
			</tr>
			<tr>
				<td align="left">
					<b>Índice Acadêmico: </b>
					<td colspan="3">
						<h:outputText value="#{relatorioDiscente.siglaIndiceSelecionado}"/>
				</td>
			</tr>
		</table>
		</div>
		<br><br>
		<table width="100%" class="tabelaRelatorioBorda" id="listaLaureados">
			<caption>Alunos Laureados</caption>
		<thead>
			<th class="alinharEsquerda" width="50%"><b>Curso</b></th>
			<th class="alinharEsquerda" width="45%"><b>Discente</b></th>
			<th class="alinharDireita" width="5%"><b>${relatorioDiscente.siglaIndiceSelecionado}</b></th>
		</thead>	
	<c:set var="cursoLoop"/>
    <c:set var="cidadeLoop"/>
	<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="cidadeAtual" value="${linha.id_municipio}"/>
		<c:if test="${cursoLoop != cursoAtual || cidadeLoop != cidadeAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="cidadeLoop" value="${cidadeAtual}"/>

		</c:if>
		<tr valign="top">
			<td align="left">
				${linha.centro}	- ${linha.curso_nome} - ${linha.descricao_grau} - ${linha.municipio_nome}
			</td>
			<td align="left">
				${linha.matricula} - ${linha.discente_nome }
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<c:if test ="${linha.iean < relatorioDiscente.valorMinimo}">
				<td align="right"><font color="red">${linha.iean}</font></td>
			</c:if>
			<c:if test ="${linha.iean >= relatorioDiscente.valorMinimo}">
				<td align="right">${linha.iean}</td>
			</c:if>
		</tr>
	</c:forEach>
	</table>
	<br>
	<table  align="center">
		<tr>
			<td>
				<b>Total de Registros:</b>
				 <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/>
			</td>
		</tr>
	</table>
	<br><br>
	<br>
	<table width="100%" class="tabelaRelatorioBorda" id="listaDiscenteNaoAtingiuIEANMinimo">
		<caption>Alunos que não atingiram pontuação mínima do ${relatorioDiscente.siglaIndiceSelecionado}</caption>
		<thead>
			<th class="alinharEsquerda" width="50%"><b>Curso</b></th>
			<th class="alinharEsquerda" width="45%"><b>Discente</b></th>
			<th class="alinharDireita" width="5%"><b>${relatorioDiscente.siglaIndiceSelecionado}* </b></th>
		</thead>	
	<c:set var="cursoLoop"/>
    <c:set var="cidadeLoop"/>
	<c:forEach items="#{relatorioDiscente.listaDiscenteNaoAtingiuIndiceLaureadoMinimo}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="cidadeAtual" value="${linha.id_municipio}"/>
		<c:if test="${cursoLoop != cursoAtual || cidadeLoop != cidadeAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="cidadeLoop" value="${cidadeAtual}"/>
		</c:if>
		<tr valign="top">
			<td align="left">
				${linha.centro}	- ${linha.curso_nome} - ${linha.descricao_grau} - ${linha.municipio_nome}
			</td>
			<td align="left">
				${linha.matricula} - ${linha.discente_nome }
			</td>
				<td align="right"><font color="red">${linha.iean}</font></td>
		</tr>
	</c:forEach>
	</table>
	<br>
	<table  align="center">
		<tr>
			<td>
				<b>Total de Registros:</b>
				 <h:outputText value="#{relatorioDiscente.numeroRegistosEncontradosDiscenteNaoAtingiuIndicelaureadoMinimo}"/>
			</td>
		</tr>
	</table>
	<br>	
	<p><b>LEGENDA:</b></p>
	<p align ="justify"><b>*</b> Valores de ${relatorioDiscente.siglaIndiceSelecionado} destacados em vermelho indicam o curso em que nenhum aluno atingiu a pontuação mínima,
	             sendo relacionado aquele discente que tenha atingido a maior pontuação daquele curso.
	</p><br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

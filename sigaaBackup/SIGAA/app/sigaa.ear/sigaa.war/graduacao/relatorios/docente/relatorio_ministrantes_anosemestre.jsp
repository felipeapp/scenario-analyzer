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
.destacado{
	color: red;
}
</style>
<body>
<f:view>
	<h2>Relatório de Docentes Ministrantes de um Componente</h2>
	
	<div id="parametrosRelatorio">
	<table width="100%">
		<tr>
			<th width="10%">Período:</th>
			<td>${relatorioMinistrantes.anoInicio}-${relatorioMinistrantes.periodoInicio} até ${relatorioMinistrantes.anoFim}-${relatorioMinistrantes.periodoFim}</td>
		</tr>
	</table>
	</div>
	
	<c:set var="disciplinaAntiga"/>	
	<table class="tabelaRelatorioBorda" width="100%">
	<c:forEach items="${relatorioMinistrantes.ministrantes}" var="dt">
		<c:set var="disciplinaAtual" value="${dt.turma.disciplina.id}"/>
		<c:if test="${disciplinaAtual != disciplinaAntiga}">
			<table width="100%"><tr><td>&nbsp;</td></tr></table>
			<table width="100%" class="tabelaRelatorioBorda">
				<tr class="centro">
					<td align="center" colspan="2">
						<b>${dt.turma.disciplina.codigo} - ${dt.turma.disciplina.detalhes.nome}</b>
					</td>
				</tr>
			</table>
			<table width="100%" class="tabelaRelatorioBorda">
			<thead>		
			<tr>
				<th class="alinharEsquerda">Docente</th>
				<th class="alinharDireita">Ano-Período</th>

			</tr>	
			</thead>
		</c:if>
		<tr>
			<td width="50%" class="alinharEsquerda">${dt.docenteNome}</td>
			<td width="10%" class="alinharDireita">${dt.turma.ano}-${dt.turma.periodo}</td>
		</tr>
		<c:set var="disciplinaAntiga" value="${dt.turma.disciplina.id}"/>		
	</c:forEach>
	</table>
	<br>
	<table width="100%" class="tabelaRelatorioBorda">
		<tfoot align="center">
			<tr><td align="center"><b>Total de Registros: ${fn:length(relatorioMinistrantes.ministrantes)} </b></td></tr>
		</tfoot>
	</table>
</f:view>
</body>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
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

<f:view>

	<h2>Relatório de Docentes e Disciplinas por Semestre</h2>

	<div id="parametrosRelatorio">
		<table >
			<c:forEach var="entry" items="#{relatorioDocenteDisciplinasMBean.mapParametrosRelatotio}">
				<tr>
					<td><b>${entry.key}</b> ${entry.value}</td>
				</tr>	
			</c:forEach>
		</table>
	</div>
	<br/>
	<table class="tabelaRelatorioBorda" width="100%">	
	
		<c:forEach var="entry" items="#{relatorioDocenteDisciplinasMBean.resultado}">
			<tr class="linhaCinza">
				<td colspan="3"><strong>${entry.key.nome}</strong></td>
			</tr>
			<tr>
				<th class="alinharCentro" width="16%">SIAPE / CPF</th>
				<th>Docente</th>
				<th>Unidade de Lotação</th>
			</tr>
			
			<c:forEach var="docente" items="#{entry.value}">	 
				<tr class="linhaCinza" style="font-weight: normal;">
					<td class="alinharCentro" colspan="1"> ${ docente.key.identificacao } </td>										
					<td> ${ docente.key.nome } </td>
					<td> ${ docente.key.unidade.nome } </td>
				</tr>
				<tr>
				<td rowspan="${fn:length(docente.value)+1}" class="alinharDireita">Disciplina(s):</td> 
				<c:forEach var="disciplina" items="#{docente.value}">
					<tr><td colspan="3" class="alinharEsquerda" style="padding-left: 20px;"> ${disciplina.descricaoResumida} </td></tr>
				</c:forEach>
				</tr>
				<tr><td colspan="3">&nbsp</td></tr>
			</c:forEach>
		</c:forEach>	
	</table>
	
	<br/>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
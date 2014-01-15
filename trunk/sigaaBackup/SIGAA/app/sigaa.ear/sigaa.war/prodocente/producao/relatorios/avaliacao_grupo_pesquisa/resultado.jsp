<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<style>
table.tabelaRelatorioBorda {
	border-top:2px black solid;
	padding: 0px;
}

table.tabelaRelatorio caption, table.tabelaRelatorioBorda caption, h3.tituloTabelaRelatorio{

	/*  h3.tituloTabelaRelatorio --> Estilo definido devido a bug de impressao do firefox */

	font-size: 1.1em;
	font-weight: bold;
	border-top:2px black solid;
	font-variant:  small-caps;
	text-align: center;
}
table.subTabelaRelatorio caption, h3.tituloSubTabelaRelatorio{
	font-size: 1.1em;
	font-weight: bold;
	border-bottom:1px black solid;
	font-variant:  small-caps;
}

table.tabelaRelatorio tfoot, table.tabelaRelatorioBorda tfoot{
	font-weight: bold;
}


table.tabelaRelatorio thead td, table.tabelaRelatorio thead th{
	font-weight: bold;
	padding: 0px;
	text-align: left;
}



table.tabelaRelatorio tbody tr.destaque, table.tabelaRelatorioBorda tbody tr.destaque{
	font-weight: bold;
	background: #DDD;
	border-top: 1px solid #CCC;
	font-variant: small-caps;
	font-size: 1em;
}

table.tabelaRelatorioBorda thead td, table.tabelaRelatorioBorda thead th{
	border:1px black solid;
	font-weight: bold;
	padding: 0px;
	text-align: left;	
}

table.tabelaRelatorioBorda tbody td, table.tabelaRelatorioBorda tbody th{
	border:1px silver solid;
	padding: 0px;
}

table.tabelaRelatorio tbody td{
	padding: 0px;
	text-align: left;
}

table.tabelaRelatorio tbody th, table.tabelaRelatorioBorda tbody th{
	font-weight: bold;
	text-align: left;
}
</style>
<f:view>
	<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.create}" />
	
		<h2>Relatório para Avaliação dos Grupos de Pesquisa</h2>
		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Grupo de Pesquisa:</th>
					<td>${relatorioAvaliacaoGrupoPesquisaMBean.grupo.nome}</td>
				</tr>
				<tr>
					<th>Período:</th>
					<td>
						${relatorioAvaliacaoGrupoPesquisaMBean.mesInicialString}/${relatorioAvaliacaoGrupoPesquisaMBean.anoInicial} 
						a ${relatorioAvaliacaoGrupoPesquisaMBean.mesFinalString}/${relatorioAvaliacaoGrupoPesquisaMBean.anoFinal} 
					</td>
				</tr>
				<tr>
					<th>Última atualização:</th>
					<td><ufrn:format type="data" valor="${relatorioAvaliacaoGrupoPesquisaMBean.grupo.dataUltimaAtualizacao}" /></td>
				</tr>
			</table>
		</div>
		<br/>
		<br/>
		<c:set var="relatorio_" value="#{relatorioAvaliacaoGrupoPesquisaMBean.obj}"/>
		<table class="tabelaRelatorioBorda" width="100%">
			<thead>
				<tr>
					<td colspan="3" style="background-color: #DEDFE3">Item</td>
					<td style="text-align: right; background-color: #DEDFE3">Pontuação</td>
					<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.cabecalho}" escape="false"/>
					<td style="text-align: right; background-color: #DEDFE3">Total Qtd.</td>
					<td style="text-align: right; background-color: #DEDFE3">Total Pts.</td>
				</tr>
			</thead>
			<tbody>
			<tr>
				<td rowspan="2">ARTIGO COMPLETO EM PERIÓDICO</td>
				<td rowspan="2">INDEXADO</td>
				<td>NACIONAL</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.artigoCompletoPeriodicoIndexadoNacional}" escape="false"/>
			</tr>
			<tr>
				<td>INTERNACIONAL</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.artigoCompletoPeriodicoIndexadoInternacional}" escape="false"/>
			</tr>
			<tr>
				<td rowspan="12">ANAIS DE EVENTOS</td>
				<td rowspan="4">RESUMO</td>
				<td>LOCAL (teto = 2)</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosResumoLocal}" escape="false"/>
			</tr>
			<tr>
				<td>REGIONAL (teto = 2)</td>				
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosResumoRegional}" escape="false"/>
			</tr>
			<tr>
				<td>NACIONAL (teto = 3)</td>				
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosResumoNacional}" escape="false"/>
			</tr>
			<tr>
				<td>INTERNACIONAL (teto = 3)</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosResumoInternacional}" escape="false"/>
			</tr>
			<tr>
				<td rowspan="4">RESUMOS EXPANDIDOS</td>
				<td>LOCAL (teto = 3)</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosResumoExpandidoLocal}" escape="false"/>
			</tr>
			<tr>
				<td>REGIONAL (teto = 3)</td>				
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosResumoExpandidoRegional}" escape="false"/>
			</tr>
			<tr>
				<td>NACIONAL (teto = 4)</td>				
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosResumoExpandidoNacional}" escape="false"/>
			</tr>
			<tr>
				<td>INTERNACIONAL (teto = 4)</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosResumoExpandidoInternacional}" escape="false"/>
			</tr>
			<tr>
				<td rowspan="4">TRABALHO COMPLETO</td>
				<td>LOCAL (teto = 5)</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosTrabalhoCompletoLocal}" escape="false"/>
			</tr>
			<tr>
				<td>REGIONAL (teto = 5)</td>				
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosTrabalhoCompletoRegional}" escape="false"/>
			</tr>
			<tr>
				<td>NACIONAL</td>				
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosTrabalhoCompletoNacional}" escape="false"/>
			</tr>
			<tr>
				<td>INTERNACIONAL</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.anaisEventosTrabalhoCompletoInternacional}" escape="false"/>
			</tr>
			<tr>
				<td rowspan="2">LIVRO</td>
				<td rowspan="2">COM ISBN</td>
				<td>NACIONAL</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.livroIsbnNacional}" escape="false"/>
			</tr>
			<tr>
				<td>INTERNACIONAL</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.livroIsbnInternacional}" escape="false"/>
			</tr>
			<tr>
				<td rowspan="2">CAPÍTULO DE LIVRO</td>
				<td rowspan="2">COM ISBN</td>
				<td>NACIONAL</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.capituloLivroIsbnNacional}" escape="false"/>
			</tr>
			<tr>
				<td>INTERNACIONAL</td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.capituloLivroIsbnInternacional}" escape="false"/>
			</tr>
			</tbody>
			<tfoot>
			<tr>
				<td colspan="3" style="text-align: right;">Total Quantitativo Individual</td>
				<td style="text-align: center"> - </td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.rodape}" escape="false"/>
			</tr>
			<tr>
				<td colspan="3" style="text-align: right;">Total Pontuação Individual</td>
				<td style="text-align: center"> - </td>
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.rodapePontuacao}" escape="false"/>
			</tr>
			</tfoot>
		</table>
		
		<br/>
		<br/>
		Pesquisadores:
		<ol>
		<c:forEach items="${relatorio_.docentes}" var="docente">
			<li>${docente}</li>
		</c:forEach>
		</ol>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

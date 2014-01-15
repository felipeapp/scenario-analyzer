<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
</style>

<f:view>
	<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.create}" />
	
		<h2>Relatório para Avaliação dos Grupos de Pesquisa</h2>
		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Período:</th>
					<td>
						${relatorioAvaliacaoGrupoPesquisaMBean.mesInicialString}/${relatorioAvaliacaoGrupoPesquisaMBean.anoInicial} 
						a ${relatorioAvaliacaoGrupoPesquisaMBean.mesFinalString}/${relatorioAvaliacaoGrupoPesquisaMBean.anoFinal} 
					</td>
				</tr>
			</table>
		</div>
		<br/>
		<br/>
		<c:set var="relatorio_" value="#{relatorioAvaliacaoGrupoPesquisaMBean.obj}"/>
		<table class="tabelaRelatorioBorda" width="100%">
				<h:outputText value="#{relatorioAvaliacaoGrupoPesquisaMBean.rodapeGeral}" escape="false"/>
		</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
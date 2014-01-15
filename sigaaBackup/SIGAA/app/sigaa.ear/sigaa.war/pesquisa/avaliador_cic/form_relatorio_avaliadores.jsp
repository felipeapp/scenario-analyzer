<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Relatório de Avaliadores de Apresentação de Resumo do CIC</h2>
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Dados para o Relatório</caption>
			<tr>
				<th>Congresso:</th>
				<td>
					<h:selectOneMenu id="congresso" value="#{avaliacaoApresentacaoResumoBean.congresso.id}">
						<f:selectItems value="#{congressoIniciacaoCientifica.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Centro/Unidade:</th>
				<td>
					<h:selectOneMenu id="centro" value="#{avaliacaoApresentacaoResumoBean.unidade.id}">
						<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
						<f:selectItems value="#{siglaUnidadePesquisaMBean.unidadesCombo}"/>
						<f:selectItem itemLabel="UFRN" itemValue="605"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
				<td colspan="2">
					<h:commandButton value="Emitir Relatório" action="#{avaliacaoApresentacaoResumoBean.relatorioAvaliadores}" /> 
					<h:commandButton value="Cancelar" action="#{avaliacaoApresentacaoResumoBean.cancelar}" onclick="#{confirm}" />
				</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Relat�rio de Distribui��o de Avalia��es de Apresenta��o de Resumo do CIC</h2>
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Dados para o Relat�rio</caption>
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
					<h:commandButton value="Emitir Relat�rio" action="#{avaliacaoApresentacaoResumoBean.relatorioDistribuicao}" /> 
					<h:commandButton value="Cancelar" action="#{avaliacaoApresentacaoResumoBean.cancelar}" onclick="#{confirm}" />
				</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

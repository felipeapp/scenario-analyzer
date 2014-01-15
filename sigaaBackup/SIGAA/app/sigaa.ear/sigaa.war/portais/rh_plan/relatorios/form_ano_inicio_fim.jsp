<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<h:form id="form">
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatoriosPlanejamento.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; ${relatoriosPlanejamento.tituloRelatorio}</h2>
<h:inputHidden value="#{relatoriosPlanejamento.tituloRelatorio}"/>
<h:inputHidden value="#{relatoriosPlanejamento.validaPeriodoAno}" />
<h:inputHidden value="#{relatoriosPlanejamento.nomeRelatorio}"/>
<h:inputHidden value="#{tituloRelatorio"/>
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th width="50%">Ano Início: </th> 
		<td>
			<h:inputText id="anoInicio" value="#{relatoriosPlanejamento.anoInicio}" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>
		</td>
	</tr>
	<tr>
		<th>Ano Fim: </th>
		<td>
			<h:inputText id="anoFim" value="#{relatoriosPlanejamento.anoFim}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" />
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
			action="#{relatoriosPlanejamento.gerarRelatorio}"/> 
			<h:commandButton value="Cancelar" action="#{relatoriosPlanejamento.cancelar}" id="cancelar" />
		</td>
	</tr>
	</tfoot>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
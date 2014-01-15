<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Relatório Sintético Entradas Por Ano </h2>
<f:view>

  <h:form id="form">
	<table class="formulario" style="width: 30%" align="center">
		<caption> Informe os critérios de busca </caption>
		<tbody>
		<tr>
			<th class="obrigatorio">Ano Inicial:</th>
			<td>
				<h:inputText value="#{relatoriosLato.anoInicial}" id="ano" size="6" maxlength="4"  onkeyup="return formatarInteiro(this);"/>
			</td>
		</tr>
	
		<tr>
			<th class="obrigatorio">Ano Final:</th>
			<td>
				<h:inputText value="#{relatoriosLato.ano}" id="anoInicial" size="6" maxlength="4" onkeyup="return formatarInteiro(this);"/>
			</td>
		</tr>
	
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosLato.entradasAnoSintetico}" /> 
					<h:commandButton id="cancelar" value="Cancelar" action="#{relatoriosLato.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
  </h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
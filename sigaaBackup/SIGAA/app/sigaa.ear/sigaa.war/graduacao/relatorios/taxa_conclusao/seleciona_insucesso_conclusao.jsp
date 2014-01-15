<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Relatório Analítico de Acontecimentos por Ano de Entrada</h2>
	<h:form id="formConsulta">
	<h:outputText value="#{relatorioTaxaConclusao.create}" />
		<table class="formulario" width="40%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<th class="obrigatorio" style="width: 50%;">
						 Ano de Entrada:
					</th>
					<td align="left">
					    <h:inputText value="#{relatorioTaxaConclusao.ano}" id="ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" id="gerarRelatorio" action="#{relatorioTaxaConclusao.gerarRelatorioInsucessoConcluintes}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioTaxaConclusao.cancelar}" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>		
	</h:form>
  <br />
  <br />
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> >  Quantitativo Geral de Defesas Por Ano</h2>

	<h:messages/>

	<div class="descricaoOperacao">
		Informe um Ano início e fim para consultar o Relatório de Quantitativo de Defesas.
	</div>
	
	<h:form id="form">

		<table class="formulario" width="40%">
			<caption>Informe os Dados para a Geração do Relatório</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Ano Início: </th>
					<td align="left" width="30px"><h:selectOneMenu value="#{relatoriosStricto.anoInicio}" id="anoIni">
	 						<f:selectItems value="#{relatoriosStricto.anos}" />
						</h:selectOneMenu> 												
					</td>
					<th class="obrigatorio">Ano Fim:</th>
					<td>
						<h:selectOneMenu value="#{relatoriosStricto.anoFim}" id="anoFim">
				   			<f:selectItems value="#{relatoriosStricto.anos}" />
				   		</h:selectOneMenu>					
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="Gerar Relatório" action="#{ relatoriosStricto.gerarRelatorioQuantitativoDefesasMatriculadosAnos }" id="Botaorelatorio" /> 
						<h:commandButton value="Cancelar" action="#{ relatoriosStricto.cancelar }" id="BotaoCancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
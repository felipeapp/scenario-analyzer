<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Relatório das Movimentaçõs dos Discentes</h2>

	<h:form id="form">
	<table class="formulario" style="width: 60%">
			
		<caption>Informe os critérios para a emissão do relatório</caption>

		<tr>
			<th class="obrigatorio" width="30%;">Ano-Período:</th>
			<td>&nbsp;
				<h:inputText id="ano" value="#{relatoriosSaeMBean.ano}" onkeyup="return formatarInteiro(this);" size="4" maxlength="4" />
				-
				<h:inputText id="periodo" value="#{relatoriosSaeMBean.periodo}" onkeyup="return formatarInteiro(this);" size="2" maxlength="1" />
			</td>
		</tr>

		<tr>
			<th class="obrigatorio">Tipo Relatório Escolhido:</th>
			<td colspan="2"> 
				<h:selectManyCheckbox value="#{relatoriosSaeMBean.relatorioMovimentacaoEscolhido}" id="relatorioMovEscolhido" layout="pageDirection">
					<f:selectItems value="#{relatoriosSaeMBean.tiposRelatorioMovimentacao}" />
				</h:selectManyCheckbox>
			</td>
		</tr>
				
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioMovimentacaoDiscente}" value="Emitir Relatório" />
					<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br />
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br/>
		</center>
		
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
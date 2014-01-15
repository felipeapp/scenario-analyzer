<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Relat�rio das Movimenta��s dos Discentes</h2>

	<h:form id="form">
	<table class="formulario" style="width: 60%">
			
		<caption>Informe os crit�rios para a emiss�o do relat�rio</caption>

		<tr>
			<th class="obrigatorio" width="30%;">Ano-Per�odo:</th>
			<td>&nbsp;
				<h:inputText id="ano" value="#{relatoriosSaeMBean.ano}" onkeyup="return formatarInteiro(this);" size="4" maxlength="4" />
				-
				<h:inputText id="periodo" value="#{relatoriosSaeMBean.periodo}" onkeyup="return formatarInteiro(this);" size="2" maxlength="1" />
			</td>
		</tr>

		<tr>
			<th class="obrigatorio">Tipo Relat�rio Escolhido:</th>
			<td colspan="2"> 
				<h:selectManyCheckbox value="#{relatoriosSaeMBean.relatorioMovimentacaoEscolhido}" id="relatorioMovEscolhido" layout="pageDirection">
					<f:selectItems value="#{relatoriosSaeMBean.tiposRelatorioMovimentacao}" />
				</h:selectManyCheckbox>
			</td>
		</tr>
				
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioMovimentacaoDiscente}" value="Emitir Relat�rio" />
					<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br />
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br/>
		</center>
		
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
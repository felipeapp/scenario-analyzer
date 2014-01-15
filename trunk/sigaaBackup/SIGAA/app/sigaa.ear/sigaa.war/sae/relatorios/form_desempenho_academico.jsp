<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Relatório de Desempenho de Bolsistas</h2>

	<h:form id="form">
		<table class="formulario" style="width: 50%">
			<caption>Informe os critérios para a emissão do relatório</caption>

			<tr>
				<td></td>
				<th class="required" nowrap="nowrap">Ano e Período: </th>
				<td>
					<h:inputText id="ano" value="#{relatoriosSaeMBean.ano}"
					size="5" maxlength="4" onkeyup="formatarInteiro(this)" />.<h:inputText
					id="semestre" value="#{relatoriosSaeMBean.periodo}" size="2"
					maxlength="1" onkeyup="formatarInteiro(this)" />
				</td>
			</tr>
				<td>
					<h:selectBooleanCheckbox value="#{relatoriosSaeMBean.checkTipoBolsa}" id="selectBuscaBolsa" styleClass="noborder" /> 
				</td>
				<th>
					<label for="tipoBolsa"> Tipo de Bolsa: </label> 
				</th>
				<td>
					<h:selectOneMenu id="buscaBolsa" value="#{relatoriosSaeMBean.tipoBolsaSelecionada}" 
	    	 		onchange="javascript:$('form:selectBuscaBolsa').checked = true; 
	    	 			javascript:setarLabel(this,'tipoBolsa');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
	    	 		<f:selectItems value="#{relatoriosSaeMBean.allBolsaCombo}" />
	    	 	</h:selectOneMenu>
				</td>
			<tr>
				
			</tr>
			
			<tfoot>
				<tr>	
					<td colspan="3">
						<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioDesempenhoAcademico}" value="Emitir Relatório" />
						<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" immediate="true" 
						 	onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"> 
						</h:commandButton>
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
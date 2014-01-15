<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>
function atribuirLabels(){
	var select1 = $('formBuscaAtividade:buscaTipoAcao');
	var select2 = $('formBuscaAtividade:buscaSituacaoAcao');
	$('formBuscaAtividade:labelTipoAtividade').value = select1.options[select1.selectedIndex].text.replace(/--/g,' ');
	$('formBuscaAtividade:labelSituacaoAtividade').value = select2.options[select2.selectedIndex].text.replace(/--/g,' ');
}
</script>

<f:view>
	<h2><ufrn:subSistema /> &gt; Relatório dos Participantes das Ações de Extensão Detalhado Por Área Temática </h2>
	<h:messages showDetail="true" />
	<h:form id="formBuscaAtividade">
	
		<table class="formulario" width="70%">
			<caption>Consultar</caption>
			<tbody>
				<tr>
					<th class="obrigatorio" colspan="2">Tipo da Ação:</th>
					<td>
						<h:selectOneMenu id="buscaTipoAcao" value="#{relatorioEquipeExtensao.buscaTipoAtividade}">	
							<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
	    	 				<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
	    	 			</h:selectOneMenu>
	    	 		</td>
	    	 		<h:inputHidden id="labelTipoAtividade" value="#{relatorioEquipeExtensao.labelTipoAtividade}" />
		
				</tr>
				
				<tr>
					<th class="obrigatorio" colspan="2">Situação da Ação:</th>
					<td>
					<h:selectOneMenu id="buscaSituacaoAcao" value="#{relatorioEquipeExtensao.buscaSituacaoAtividade}">
						<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
		    	 		<f:selectItems value="#{atividadeExtensao.tipoSituacaoAtividadeCombo}" />
	 			 	</h:selectOneMenu>
	 			 	</td>
	 			 	<h:inputHidden id="labelSituacaoAtividade" value="#{relatorioEquipeExtensao.labelSituacaoAtividade}" />
				
				</tr>
								
				<tr>
					<td width="5%"><input type="radio" name="data" value="ano" checked="checked"></td>
					<td align="left">Ano:</td>
					<td colspan="2">
						<h:inputText value="#{relatorioEquipeExtensao.ano}" size="4" maxlength="4" 
						onkeyup="formatarInteiro(this);" 
						onclick="javascript:document.getElementsByName('data')[0].checked = true;"/>
					</td>
				</tr>
				
				<tr>
					<td width="5%"><input type="radio" name="data" value="periodo" ></td>
					<td align="left">Período:</td>
					<td><t:inputCalendar  value="#{relatorioEquipeExtensao.inicio}" renderAsPopup="true" 
						renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10"
						onchange="javascript:document.getElementsByName('data')[1].checked = true;">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						a
						<t:inputCalendar  value="#{relatorioEquipeExtensao.fim}" renderAsPopup="true" 
						renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10"
						onchange="javascript:document.getElementsByName('data')[1].checked = true;">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Consultar" onclick="atribuirLabels()"
						action="#{relatorioEquipeExtensao.gerarRelatorioEstatisticas}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}"
						action="#{relatorioEquipeExtensao.cancelar}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
		
	</h:form>
	
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

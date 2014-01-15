<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria"%>

<f:view>
	<h2><ufrn:subSistema /> > Configurar Mês de Recebimento de Frequência</h2>
	<br>
	<h:form id="form">
		<h:inputHidden value="#{envioFrequencia.confirmButton}" />
		<h:inputHidden value="#{envioFrequencia.obj.id}" />
		<table class="formulario" width="100%" >
			<caption class="listagem">Configurar Mês de Recebimento de Frequência</caption>

			<tr>
				<th width="45%" class="required">Receber frequências de:</th> 
				<td> 		
					<h:selectOneMenu value="#{ envioFrequencia.obj.mes }" id="txtMes">
					        <f:selectItem itemLabel="Janeiro"   itemValue="1"/>                             
                            <f:selectItem itemLabel="Fevereiro" itemValue="2"/>                                                             
                            <f:selectItem itemLabel="Março"     itemValue="3"/>
                            <f:selectItem itemLabel="Abril"     itemValue="4"/>
                            <f:selectItem itemLabel="Maio"      itemValue="5"/>
                            <f:selectItem itemLabel="Junho"     itemValue="6"/>
                            <f:selectItem itemLabel="Julho"     itemValue="7"/>
                            <f:selectItem itemLabel="Agosto"    itemValue="8"/>
                            <f:selectItem itemLabel="Setembro"  itemValue="9"/>
                            <f:selectItem itemLabel="Outubro"   itemValue="10"/>
                            <f:selectItem itemLabel="Novembro"  itemValue="11"/>
                            <f:selectItem itemLabel="Dezembro"  itemValue="12"/>
					</h:selectOneMenu>
				</td>
			</tr>

            <tr>
                <th class="required">Referentes ao ano de:</th>
                <td>
                    <h:inputText value="#{envioFrequencia.obj.ano}" size="4" readonly="#{envioFrequencia.readOnly}"
                        maxlength="4"  onkeyup="return formatarInteiro(this)"/>
                </td>
            </tr>

			<tr>
				<th class="required">Iniciar recebimento em:</th>
				<td>
					<t:inputCalendar
						renderAsPopup="true"
						renderPopupButtonAsImage="true"
						value="#{envioFrequencia.obj.dataInicioRecebimento}" 
						popupDateFormat="dd/MM/yyyy"
						popupTodayString="Hoje é"
						size="10"
						maxlength="10"
						onkeypress="return(formataData(this,event))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>

			<tr>
				<th class="required">Finalizar recebimento em:</th>
				<td>
					<t:inputCalendar
						renderAsPopup="true"
						renderPopupButtonAsImage="true"
						value="#{envioFrequencia.obj.dataFimRecebimento}" 
						popupDateFormat="dd/MM/yyyy"
						popupTodayString="Hoje é"
						size="10"
						maxlength="10"
						onkeypress="return(formataData(this,event))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>


			<tr>
				<th class="required">Monitores cadastrados no período:</th>
				<td>
					<t:inputCalendar
						renderAsPopup="true"
						renderPopupButtonAsImage="true"
						value="#{envioFrequencia.obj.dataInicioEntradaMonitorPermitido}" 
						popupDateFormat="dd/MM/yyyy"
						popupTodayString="Hoje é"
						size="10" 
						maxlength="10"
						onkeypress="return(formataData(this,event))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
					a
					<t:inputCalendar
						renderAsPopup="true"
						renderPopupButtonAsImage="true"
						value="#{envioFrequencia.obj.dataFimEntradaMonitorPermitido}" 
						popupDateFormat="dd/MM/yyyy"
						popupTodayString="Hoje é"
						size="10"
						maxlength="10"
						onkeypress="return(formataData(this,event))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
					
					<ufrn:help img="/img/ajuda.gif">Libera o cadastro de frequências somente para os monitores inseridos no programa de monitoria durante o período informado.</ufrn:help>
				</td>
			</tr>
			
			<tr>
                <th class=required>Em projetos do ano: </th>
                <td>
                    <h:inputText value="#{envioFrequencia.obj.anoInicioProjetosPermitidos}" readonly="#{envioFrequencia.readOnly}" id="anoInicio"  maxlength="4" size="4" onkeyup="return formatarInteiro(this)"/>
                    até <h:inputText value="#{envioFrequencia.obj.anoFimProjetosPermitidos}" readonly="#{envioFrequencia.readOnly}" id="anoFim" maxlength="4" size="4" onkeyup="return formatarInteiro(this)"/>                 
                    <ufrn:help img="/img/ajuda.gif">Libera para o envio de freqüências, somente os projetos submetidos dentro do período informado. (informar ano com 4 dígitos)</ufrn:help>
					<br/>
					<br/>					
                </td>
            </tr>
		</table>

			
			
		<table class="${ empty cancelarBolsaMonitoria.discentes ? "formulario" : "subFormulario" }" width="100%">
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton id="cadastrar" value="Cadastrar" action="#{envioFrequencia.cadastrar}"/>						
							<h:commandButton value="Cancelar" action="#{envioFrequencia.cancelar}" onclick="#{confirm}"/>						
						</td>
					</tr>
				</tfoot>
		</table>
			
		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
		
	</h:form>					
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
function checkAll(elem) {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = elem.checked;
	});
}
</script>
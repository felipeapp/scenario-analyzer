<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	table.listagem tr td.periodo {
		background: #C4D2EB;
		padding: 3px;
		font-weight: bold;
	}

.button
{
background: #f0f0f0 url(buttonbackground.jpg) no-repeat bottom center;
font: 12px verdana, geneva, lucida, 'lucida grande', arial, helvetica, sans-serif;
border: 1px solid #EB1717;
}
</style>

<f:view>
	<a4j:keepAlive beanName="relatorioAtividadeAP"></a4j:keepAlive>

	<h2> <ufrn:subSistema /> &gt; Relatório Quantitativos de Atividades </h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usuário, </b>
		</p> 	
		<p>Nesta tela é possível consultar o relatório quantitativo de atividades. </p>		
	</div>	
	
<h:form>
	
	<table class="formulario" width="50%">
		<caption>FILTROS</caption>
		<tbody>
			<tr>
				<th width="35%" class="required" style="text-align:right;">Período:</th>
				<td>
					<t:inputCalendar value="#{relatorioAtividadeAP.dataInicio}" readonly="false" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Início">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</t:inputCalendar>
					-
					<t:inputCalendar value="#{relatorioAtividadeAP.dataFim}" readonly="false" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Fim">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th width="35%" style="text-align:right;">Formato:</th>
				<td>
					<h:selectOneRadio value="#{ relatorioAtividadeAP.formato }" styleClass="noborder">
						<f:selectItem itemValue="html" itemLabel="HTML"/>
						<f:selectItem itemValue="xls" itemLabel="XLS"/>
					</h:selectOneRadio>
				</td>
			</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Gerar Relatório" action="#{relatorioAtividadeAP.gerarQuantitativo}" />
			</td>
		</tr>	
		</tfoot>	
	</table>
	<br/>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
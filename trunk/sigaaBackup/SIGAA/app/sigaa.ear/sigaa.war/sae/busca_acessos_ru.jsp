<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>
<script type="text/javascript" src="/sigaa/javascript/graduacao/busca_discente.js"> </script>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>

<f:view>

	<h2 class="tituloPagina"> <ufrn:subSistema/> > Buscar Acessos ao RU</h2>
	
	<h:form id="formulario">
	<table class="formulario" width=80%>
		<caption> Informe os critérios de busca</caption>
		<tbody>
			
			<tr>
				<td colspan="2"> 
				<label style="padding-left: 85px;" class="required" for="bolsa">Tipo da Bolsa:</label>
					<h:selectOneMenu value="#{buscaAcessoRUMBean.tipoBolsaAuxilio.id}" style="width:400px;">
						<f:selectItem itemLabel="-- TODOS --" itemValue="0" />
						<f:selectItems value="#{buscaAcessoRUMBean.allTiposBolsasCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
				<label style="padding-left: 62px;" class="required" for="bolsa">Tipo da Liberação:</label>
					<h:selectOneMenu value="#{buscaAcessoRUMBean.tipoLiberacaoAcessoRU.id}" style="width:400px;">
						<f:selectItems value="#{buscaAcessoRUMBean.allTiposLiberacaoCatracaCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
				<label style="padding-left: 120px;" class="required" for="bolsa">Período:</label>
					<t:inputCalendar value="#{buscaAcessoRUMBean.dataInicio}" size="10" maxlength="10" 
						id="dataInicio" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" 
						onkeypress="return(formatarMascara(this,event,'##/##/####'))">
				 		<f:converter converterId="convertData"/> 
				 	</t:inputCalendar> a
					<t:inputCalendar value="#{buscaAcessoRUMBean.dataFinal}" size="10" maxlength="10" 
						id="dataFim" popupDateFormat="dd/MM/yyyy" renderAsPopup="true"  renderPopupButtonAsImage="true" 
						onkeypress="return(formatarMascara(this,event,'##/##/####'))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{buscaAcessoRUMBean.consultarAcessoRUCatraca}" value="Buscar" id="buscar"/>
					<h:commandButton action="#{buscaAcessoRUMBean.cancelar}" onclick="#{confirm}" value="Cancelar" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
	<br/>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br/>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
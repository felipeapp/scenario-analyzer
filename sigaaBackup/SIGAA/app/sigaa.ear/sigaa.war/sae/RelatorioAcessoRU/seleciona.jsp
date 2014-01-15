<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>
<script type="text/javascript" src="/sigaa/javascript/graduacao/busca_discente.js"> </script>


<f:view>

	<h2> <ufrn:subSistema/> > Relatório de Acessos das Catracas do RU</h2>
	<h:form id="formulario">
	<table class="formulario" width=30% >
		<caption> Informe os critérios de busca</caption>
		<tbody>
			<tr>
				<th class="obrigatorio" >
				 Período:
				</th>
				<td>
					<t:inputCalendar value="#{relatorioAcessoRu.buscaDataInicio}" size="10" maxlength="10" 
						id="dataInicio" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" 
						onkeypress="return(formatarMascara(this,event,'##/##/####'))">
				 		<f:converter converterId="convertData"/> 
				 	</t:inputCalendar> a
					<t:inputCalendar value="#{relatorioAcessoRu.buscaDataFim}" size="10" maxlength="10" 
						id="dataFim" popupDateFormat="dd/MM/yyyy" renderAsPopup="true"  renderPopupButtonAsImage="true" 
						onkeypress="return(formatarMascara(this,event,'##/##/####'))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th >
					<h:selectBooleanCheckbox value="#{relatorioAcessoRu.checkDetalhes}" styleClass="noborder" id="formaAtuacao" style="vertical-align: middle;"  />  
				</th>
				<td>
					Gerar relatório detalhado
				</td>
			</tr>
			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{relatorioAcessoRu.buscarAcessosRu}" value="Buscar" id="buscar"/>
					<h:commandButton action="#{relatorioAcessoRu.cancelar}" onclick="#{confirm}" value="Cancelar" id="cancelar" />
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
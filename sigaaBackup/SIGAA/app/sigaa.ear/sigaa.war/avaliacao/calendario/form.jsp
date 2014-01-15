<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2 class="title"><ufrn:subSistema /> &gt; Calendário de Aplicação da Avaliação Institucional</h2>
	<a4j:keepAlive beanName="calendarioAvaliacaoInstitucionalBean"></a4j:keepAlive>
	<div class="descricaoOperacao">
		<p> <strong>Caro Usuário,</strong> </p>
		<p>Neste formulário, você poderá cadastrar um período para preenchimento do formulário destinado à Avaliação Institucional.</p>
	</div>

	<h:form id="form">
	
	<table class="formulario" width="100%">
		<caption>Informe os dados do Calendário</caption>
		<tbody>
			<tr>
				<th class="required">Formulário:</th>
				<td>
					<h:selectOneMenu value="#{ calendarioAvaliacaoInstitucionalBean.obj.formulario.id }" id="calendario"
						disabled="#{ calendarioAvaliacaoInstitucionalBean.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ calendarioAvaliacaoInstitucionalBean.formulariosCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Ano-Período:</th>
				<td>
					<h:inputText value="#{ calendarioAvaliacaoInstitucionalBean.obj.ano }" id="ano" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" size="4" maxlength="4"
					readonly="#{ calendarioAvaliacaoInstitucionalBean.readOnly}"/>-
					<h:inputText value="#{ calendarioAvaliacaoInstitucionalBean.obj.periodo }" id="periodo" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" size="1" maxlength="1"
					readonly="#{ calendarioAvaliacaoInstitucionalBean.readOnly}"/>
				</td> 
			</tr>
			<tr>
				<th class="required">Início:</th>
				<td>
					 <t:inputCalendar value="#{calendarioAvaliacaoInstitucionalBean.obj.inicio}" id="dataInicio" size="10" maxlength="10" 
					    onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
					    renderAsPopup="true" renderPopupButtonAsImage="true" 
					    readonly="#{ calendarioAvaliacaoInstitucionalBean.readOnly}">
					      <f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td> 
			</tr>
			<tr>
				<th class="required">Fim:</th>
				<td>
					<t:inputCalendar value="#{calendarioAvaliacaoInstitucionalBean.obj.fim}" id="dataFim" size="10" maxlength="10" 
					    onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
					    renderAsPopup="true" renderPopupButtonAsImage="true" 
					    readonly="#{ calendarioAvaliacaoInstitucionalBean.readOnly}">
					      <f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td> 
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="#{calendarioAvaliacaoInstitucionalBean.confirmButton}" action="#{ calendarioAvaliacaoInstitucionalBean.cadastrar }" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{ calendarioAvaliacaoInstitucionalBean.cancelar }" id="cancelar" onclick="#{confirm }" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br /><br />
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link href="/sigaa/css/ensino/questionarios.css" rel="stylesheet" type="text/css" />

<f:view>
<h2> <ufrn:subSistema/> &gt; Questionário > Dados Gerais </h2>

<h:form id="form">

<table class="formulario" width="100%">
	<caption class="formulario">Dados Gerais</caption>

	<tr>
		<th ${empty questionarioBean.tiposQuestionario ? 'style="font-weight:bold;"' : 'class="obrigatorio"'}>Tipo de Questionário:</th>
		<td>
		  	<c:if test="${empty questionarioBean.tiposQuestionario}">
				${questionarioBean.obj.tipo}
			</c:if>
			<h:selectOneMenu value="#{questionarioBean.obj.tipo.id}" id="tipoQuestionario" rendered="#{!empty questionarioBean.tiposQuestionario}"
				valueChangeListener="#{questionarioBean.selecionaTipoGerenciado}">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>	
				<f:selectItems value="#{questionarioBean.tiposQuestionario}"/>		
				<a4j:support event="onchange" reRender="form"/>	
			</h:selectOneMenu>		
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Título:</th>
		<td>
			<h:inputText size="70" id="txtTitulo" value="#{questionarioBean.obj.titulo}" maxlength="100"/>
		</td>
	</tr>
	<c:if test="${questionarioBean.obj.necessarioPeriodoPublicacao}">
	<tr>
		<th class="obrigatorio">Data Inicial:</th>
		<td>
  			 <t:inputCalendar value="#{questionarioBean.obj.inicio}" id="dataInicio" size="10" maxlength="10" 
  				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
  				renderAsPopup="true" renderPopupButtonAsImage="true" >
    				<f:converter converterId="convertData"/>
			</t:inputCalendar>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Data Final:</th>
		<td>
  			 <t:inputCalendar value="#{questionarioBean.obj.fim}" id="dataFim" size="10" maxlength="10" 
  				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
  				renderAsPopup="true" renderPopupButtonAsImage="true" >
    				<f:converter converterId="convertData"/>
			</t:inputCalendar>
		</td>
	</tr>
	</c:if>	

	<tfoot>
		<tr>
			<td colspan=2>
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{questionarioBean.cancelar}" id="cancelar" />
				<h:commandButton value="Avançar >>" action="#{questionarioBean.submeterDadosGerais}" id="submeter"/> 
			</td>
		</tr>
	</tfoot>
</table>
</h:form>

<br/>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	<br><br>
</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
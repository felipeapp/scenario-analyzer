<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Cronograma de Execu��o </h2>
<a4j:keepAlive beanName="cronogramaExecucao"/>
<script type="text/javascript">
function mascara(o,f){
    obj = o
    fun = f
    setTimeout("gerarmascara()",1)
}

function gerarmascara(){
    obj.value=fun(obj.value)
}

function masknumeros(texto){
    texto = texto.replace(/\D/g,"")
    return texto
}

</script>

<h:form id="form">
	<table class="formulario" style="width: 100%">
	  <caption>Cadastro Cronograma de Execu��o</caption>
		<tbody>
			<!--Lista de cursos -->
			<tr>
				<th width="25%" class="obrigatorio">Curso:</th>
				<td >
					<h:selectOneMenu value="#{cronogramaExecucao.cronograma.curso.id}" valueChangeListener="#{cronogramaExecucao.carregarModulos}" id="curso" required="true">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cronogramaExecucao.cursosCombo}" />
				 		<a4j:support event="onchange" reRender="modulo" />  
					</h:selectOneMenu>
				</td>
			</tr>
			
			<!--Lista de M�dulos -->
			<tr>
				<th class="obrigatorio">M�dulo:</th>
				<td>
					<h:selectOneMenu value="#{cronogramaExecucao.cronograma.modulo.id}" id="modulo" required="true">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cronogramaExecucao.modulosCombo}" /> 
					</h:selectOneMenu>
				</td>
			</tr>
			
			<!-- Descri��o do Cronograma-->
			<tr>
				<th>Descri��o:</th>
				<td>
					<h:inputText value="#{cronogramaExecucao.cronograma.descricao}" maxlength="50" size="60" />
				</td>
			</tr>
			
			<!-- Ano Per�odo -->
			<tr>
				<th class="obrigatorio">Ano - Per�odo:</th>
				<td>
					<h:inputText value="#{cronogramaExecucao.cronograma.ano}" size="4" maxlength="4" id="ano" onkeypress="return mascara(this,masknumeros);" />
					- <h:inputText value="#{cronogramaExecucao.cronograma.periodo}" 
  					size="2" maxlength="1" id="periodo" onkeypress="return mascara(this,masknumeros);" />
  					
				</td>
			</tr>
			
			<!-- Data inicial e final do per�odo letivo-->	
			<tr>
				<th width="20%;" class="obrigatorio">Per�odo Letivo:</th>
				<td ><t:inputCalendar value="#{cronogramaExecucao.cronograma.dataInicio}" size="10" maxlength="10" disabled=""
 						 onkeypress="return formataData(this,event)" popupDateFormat="dd/MM/yyyy" 
 					id="inicioPeriodo" renderAsPopup="true" renderPopupButtonAsImage="true" title="In�cio Per�odo Letivo" > 
 					</t:inputCalendar> 
 					at�
 					<t:inputCalendar value="#{cronogramaExecucao.cronograma.dataFim}" size="10" maxlength="10" disabled="" 
 						 onkeypress="return formataData(this,event)" popupDateFormat="dd/MM/yyyy" 
 					id="fimPeriodo" renderAsPopup="true" renderPopupButtonAsImage="true" title="Fim Per�odo Letivo"> 
 					</t:inputCalendar> 
 				</td>
 			</tr>
 			
 			<!-- Periodicidade do cronograma -->
 			<tr>
				<th width="25%" class="obrigatorio">Periodicidade:</th>
				<td>
					<h:selectOneMenu value="#{cronogramaExecucao.cronograma.unidadeTempo.id}" id="periodicidade">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cronogramaExecucao.periodicidadesCombo}" /> 
					</h:selectOneMenu>
				</td>
			</tr>		
		</tbody>
		<tfoot>   
		   <tr>
				<td colspan="2">
					<h:commandButton value="Avan�ar >>" action="#{cronogramaExecucao.avancar}" id="avancar" />
					<h:commandButton value="Cancelar" action="#{cronogramaExecucao.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   </tr>
		</tfoot>
	</table>
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
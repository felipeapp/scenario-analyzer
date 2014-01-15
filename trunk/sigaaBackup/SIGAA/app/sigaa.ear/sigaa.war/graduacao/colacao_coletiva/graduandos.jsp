<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.colLeft{text-align: left; }
	.colCenter{text-align: center; }
	.colRight{text-align: right; }
	.rich-progress-bar-width { width: 640px;}
	.rich-progress-bar-uploaded-dig {font-size: 16px;}
	.rich-progress-bar-shell-dig {font-size: 16px;}
</style>

<script>
function desabilitaBotoes() {
	$('form:colarGrauSelecionados').disabled=true;
	$('form:colarGrauSelecionados').value = 'Aguarde...';
	$('form:escolherOutroCurso').disabled=true;
	$('form:cancelarAOperacao').disabled=true;
}
</script>


<f:view>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

<h2> <ufrn:subSistema /> > Conclus�o de Programa Coletiva </h2>

<h:form id="form">
<a4j:keepAlive beanName="colacaoColetiva"></a4j:keepAlive>
<div class="descricaoOperacao">
	<p>Caro Usu�rio,</p>
	<p>
		Abaixo est� a lista de discentes aptos a concluir o programa, ou seja, discentes que obtiveram o status <b>GRADUANDO</b> 
		no ano-per�odo informado.
	</p>
	<p>
		Desmarque os discentes que n�o dever�o concluir o programa agora ou que faltaram a cerim�nia de cola��o de grau, por exemplo.
	</p> 
	<p>Os discentes que n�o tem op��o
		para marcar/desmarcar apresentam <b>pend�ncias</b> com o ENADE e <b>n�o poder�o concluir o programa</b> neste momento. Neste caso, se houver algum engano
		a situa��o ENADE, esta dever� ser corrigida antes da conclus�o do programa. 
	</p>
</div>

<table class="visualizacao" >
	<caption>Dados da Turma de Conclus�o Coletiva</caption>
	<tr>
		<th width="20%"> Curso: </th>
		<td > ${colacaoColetiva.curso.descricao} </td>
	</tr>
	<tr>
		<th> Ano - Per�odo: </th>
		<td >${ colacaoColetiva.ano}.${ colacaoColetiva.periodo}</td>
	</tr>
</table>
<br/>
<table class="formulario" width="100%">
	<caption>Informe a Data de Cola��o da Turma</caption>
	<tbody>
		<tr>
			<th width="15%" class="obrigatorio">Data da Cola��o:</th>
			<td>
			<t:inputCalendar value="#{colacaoColetiva.dataColacao}" size="10" maxlength="10"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))"
				popupDateFormat="dd/MM/yyyy" id="colacao" renderAsPopup="true" renderPopupButtonAsImage="true" >
				<f:converter converterId="convertData"/>
			</t:inputCalendar>
			</td>
		</tr>
	
		<tr>
			<td colspan="2" class="subFormulario">Selecione os Graduandos que ser�o Conclu�dos (${ fn:length(colacaoColetiva.graduandos) })</td>
		</tr>
		<tr>
			<td colspan="2">
				<h:dataTable value="#{colacaoColetiva.graduandos}" var="graduando" rowClasses="linhaImpar,linhaPar" style="width:100%"
				columnClasses="colCenter,colLeft,colLeft,colLeft,colLeft" >
					<h:column headerClass="colCenter">
						<h:selectBooleanCheckbox value="#{graduando.matricular}"
							rendered="#{graduando.participacaoEnadeConcluinte != null 
										&& !graduando.participacaoEnadeConcluinte.participacaoPendente 
										&& graduando.participacaoEnadeIngressante != null
										&& !graduando.participacaoEnadeIngressante.participacaoPendente}" />
					</h:column>
					<h:column headerClass="colLeft">
						<f:facet name="header"><h:outputText value="Discente"/></f:facet>
						<h:outputText value="#{graduando.matriculaNome}"/>
					</h:column>
					<h:column headerClass="colLeft">
						<f:facet name="header"><h:outputText value="Matriz Curricular"/></f:facet>
						<h:outputText value="#{graduando.matrizCurricular.descricaoMin}"/>
					</h:column>
					<h:column headerClass="colLeft">
						<f:facet name="header"><h:outputText value="ENADE Ingressante"/></f:facet>
						<h:outputText value="#{graduando.participacaoEnadeIngressante.descricao}" />
					</h:column>
					<h:column headerClass="colLeft">
						<f:facet name="header"><h:outputText value="ENADE Concluinte"/></f:facet>
						<h:outputText value="#{graduando.participacaoEnadeConcluinte.descricao}" />
					</h:column>
				</h:dataTable>
			</td>
		</tr>
	</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton value="<< Escolher Outro Curso" action="#{colacaoColetiva.telaBuscaCurso}" id="escolherOutroCurso"/>
			<h:commandButton value="Cancelar" action="#{colacaoColetiva.cancelar}" onclick="#{confirm}" id="cancelarAOperacao"/>
			<a4j:commandButton value="Pr�ximo Passo >>" action="#{colacaoColetiva.confirmar}" id="colarGrauSelecionados" reRender="progressBar"
			  onclick="desabilitaBotoes()"/>
		</td>
	</tr>	
	</tfoot>
</table>
<br/>
<div style="width: 650px; margin: 5px auto; text-align: center;">
	<a4j:outputPanel>
		<rich:progressBar interval="1000" id="progressBar" minValue="0" maxValue="100"
			enabled="true"
			value="#{ colacaoColetiva.percentualProcessado }"
			label ="#{ colacaoColetiva.mensagemProgresso }">
		</rich:progressBar>
	</a4j:outputPanel>
</div>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
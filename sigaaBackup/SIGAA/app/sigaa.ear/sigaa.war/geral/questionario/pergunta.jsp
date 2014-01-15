<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@page import="br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario"%>

<style>
.botoes {
	align: right;
	width: 10%;
}
.descricaoAlternativas {
    text-align: left;
	align: left;
	width: 87%;
}
.selectAlternativas{
	align: left;
	width: 3%;
}
</style>

<f:view>
<c:set var="confirmResumoQuestionario" scope="application" 
	value="if($('adicionarPergunta:txtPergunta').value != ''){return confirm('Todos os dados preenchidos da pergunta serão perdidos! Tem certeza que deseja realizar esta operação?');}" />

<h:outputText value="#{questionarioBean.create}"/>
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
<h2 class="title">  <ufrn:subSistema/> &gt; Questionário &gt; <h:outputText value="#{questionarioBean.confirmButtonPergunta }" /> </h2>

<h:form id="adicionarPergunta">
			
<table class="formulario" width="100%">
	<caption class="formulario"><h:outputText value="#{questionarioBean.confirmButtonPergunta }" /></caption>
<a4j:region>
	<tr>
		<th class="required" width="15%">Tipo de pergunta:</th>
		<td>
			<h:selectOneMenu id="tipoPergunta" value="#{questionarioBean.pergunta.tipo}" onchange="alterarTipo(this); submit();">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
				<f:selectItems value="#{ questionarioBean.tiposPergunta}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="required">Pergunta:</th>
		<td>
				<h:inputTextarea value="#{questionarioBean.pergunta.pergunta}" rows="4" cols="80" id="txtPergunta"/>
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
		 	<h:outputLabel value="Pergunta Obrigatória?" for="obrigatoria"/>
		 	<h:selectBooleanCheckbox value="#{questionarioBean.pergunta.obrigatoria}" id="obrigatoria"/>
		</td>
	</tr>	
	
	<a4j:region rendered="#{ questionarioBean.pergunta.dissertativa || questionarioBean.pergunta.numerica }">
		<tr>
			<td colspan="2">
			 	<h:outputLabel value="Quantidade Máxima de Caracteres:" />
				<html:img page="/img/required.gif" style="vertical-align: top;" />
			 	<h:inputText id="tamanhoMaximo" value="#{questionarioBean.pergunta.maxCaracteres}" size="3" maxlength="4" onkeyup="return formatarInteiro(this);" />
			</td>
		</tr>	
	</a4j:region>
	
	<tr id="trResposta" style="display: none">
		<td colspan="2" width="100%">
			<table width="100%">
				<caption style="text-align: center">Gabarito</caption>
				<tr align="center">
				<td width="15%" align="right">Resposta:</td>		
				<td align="left">
					<h:inputText value="#{questionarioBean.pergunta.gabaritoNumerica}" rendered="#{questionarioBean.pergunta.numerica}" size="5" />
					
					<h:inputTextarea id="gabaritoDissertativa" value="#{questionarioBean.pergunta.gabaritoDissertativa}" cols="80" rows="4" rendered="#{questionarioBean.pergunta.dissertativa}" />
					
					<h:selectOneRadio rendered="#{questionarioBean.pergunta.vf}" value="#{questionarioBean.pergunta.gabaritoVf}" id="perguntaVF">
						<f:selectItem itemValue="true" itemLabel="Verdadeiro"/>
						<f:selectItem itemValue="false" itemLabel="Falso"/>
					</h:selectOneRadio>
				</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr id="trAdicionarAlternativas" style="display: none">
		<td colspan="2">
			<table class="subformulario" width="100%">
				<caption style="text-align: center">Adicionar alternativas</caption>

				<tr>
					<td colspan="5">
						<div class="infoAltRem" style="width: 100%">
							<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: Adicionar alternativa
							<h:graphicImage value="/img/prodocente/cima.gif"style="overflow: visible;"/> 
								/ <h:graphicImage value="/img/prodocente/baixo.gif" style="overflow: visible; margin-left: 0.3em"/>: 
								Mover alternativa para cima ou para baixo
					        <h:graphicImage value="/img/garbage.png" style="overflow: visible;"/>: Remover alternativa <br/>
						</div>
					</td>
				 </tr>

				<tr id="trPeso" style="display: none">
					<th width="15%" class="required">Peso: </th>
					<td colspan="2"> <h:inputText maxlength="4" value="#{questionarioBean.alternativa.peso}" id="txtPeso" style="width: 5%" onkeyup="return formatarInteiro(this);"/></td>
				</tr>				
				
				<tr>
					<th width="15%" class="required">Alternativa: </th>
					<td> <h:inputTextarea value="#{questionarioBean.alternativa.alternativa}" rows="1" id="txtAlternativa" style="width: 98%"/></td>

					<td width="5%"> 
						<a4j:commandButton image="/img/adicionar.gif" id="adicionarAlternativa"
							title="Adicionar Resposta" actionListener="#{questionarioBean.adicionarAlternativa}" 
							reRender="dataTableAlternativasPergunta, txtAlternativa, errosQuestionario, txtPeso" styleClass="noborder"/>
					</td>
					<td width="5%">
			            <a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</td>
				</tr>
			</table>
		
		</td>
	</tr>
	
	<tr id="trAlternativas" style="display: none">		
		<td colspan="2" align="center">
		
			<h:dataTable var="alternativa" value="#{questionarioBean.modelAlternativas}" id="dataTableAlternativasPergunta" 
				width="95%" columnClasses="selectAlternativas, descricaoAlternativas, botoes" rowClasses="linhaPar, linhaImpar">
				
				<f:facet name="header">
					<h:outputText value="ALTERNATIVAS"/>
				</f:facet>
				
				<h:column>
				
					<h:selectBooleanCheckbox value="#{alternativa.selecionado}" id="checkMultiplaEscolha" 
						rendered="#{questionarioBean.pergunta.multiplaEscolha}"/>
				
					<t:selectOneRadio id="radioUnicaEscolha" forceId="true" forceIdIndex="false" 
						value="#{questionarioBean.gabaritoUnicaEscolha}" 
						rendered="#{questionarioBean.pergunta.unicaEscolha || questionarioBean.pergunta.unicaEscolhaAlternativaPeso}">
						<f:selectItem itemValue="#{alternativa.ordem}" itemLabel=""/>
					</t:selectOneRadio>
					
				</h:column>
				
				<h:column>
					<h:outputText value="#{alternativa}"/>
					<h:outputText value=" - (Peso: #{alternativa.peso})" rendered="#{alternativa.peso > 0}"/>
				</h:column>
				
				<h:column>
					<a4j:commandButton id="cima" image="/img/prodocente/cima.gif"
						title="Mover alternativa para cima" actionListener="#{questionarioBean.moveAlternativaCima}" 
						reRender="dataTableAlternativasPergunta" styleClass="noborder" />
						
					<a4j:commandButton id="baixo" image="/img/prodocente/baixo.gif" title="Mover alternativa para baixo" 
						actionListener="#{questionarioBean.moveAlternativaBaixo}" 
						reRender="dataTableAlternativasPergunta"  styleClass="noborder" />
						
					<a4j:commandButton id="removerItem" image="/img/garbage.png" title="Remover alternativa" 
						actionListener="#{questionarioBean.removerAlternativa}" onclick="if (!confirm('Confirma a remoção desta informação?')) return false;"
						reRender="dataTableAlternativasPergunta" styleClass="noborder">
					</a4j:commandButton>
				</h:column>
			</h:dataTable>
		</td>
	</tr>
</a4j:region>	
	<tfoot>
		<tr>
		<td colspan="2">
			<h:commandButton value="#{questionarioBean.confirmButtonPergunta}" action="#{questionarioBean.adicionarPergunta}" id="btnAdd"/>
			<h:commandButton value="Resumo do Questionário" action="#{questionarioBean.cancelarAdicionarPergunta}" onclick="#{confirmResumoQuestionario}"  alt="teste" id="btnCancelar"/>
			<h:commandButton value="Cancelar" action="#{questionarioBean.cancelarAdicionarPergunta}" onclick="#{confirm}" id="cancel"/>
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
<script type="text/javascript" charset="UTF-8">
	$('adicionarPergunta:txtPergunta').focus();
	
	function alterarTipo(sel){
		var val = null; 
		if( sel != null) 
			val = sel.options[sel.selectedIndex].value;
			
		if ( val == <%=PerguntaQuestionario.MULTIPLA_ESCOLHA%> || val == <%=PerguntaQuestionario.UNICA_ESCOLHA%>) {
			$('trAdicionarAlternativas').show();
			$('trAlternativas').show();
			$('trResposta').hide();
			$('trPeso').hide();
		} else if ( ${questionarioBean.obj.possuiDefinicaoGabarito} && (val == <%=PerguntaQuestionario.DISSERTATIVA%> 
			|| val == <%=PerguntaQuestionario.NUMERICA%> 
			|| val == <%=PerguntaQuestionario.VF%>) ){
				$('trAdicionarAlternativas').hide();
				$('trAlternativas').hide();
				$('trResposta').show();
				$('trPeso').hide();
		} else if (val == <%=PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO%>) {
			$('trAdicionarAlternativas').show();
			$('trAlternativas').show();
			$('trResposta').hide();
			$('trPeso').show();
		} else {
			$('trAdicionarAlternativas').hide();
			$('trAlternativas').hide();
			$('trResposta').hide();
			$('trPeso').hide();
		}
		
	} 
	alterarTipo($('adicionarPergunta:tipoPergunta'));

</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
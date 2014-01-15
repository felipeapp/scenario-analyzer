<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoDocumentoNormalizacaoCatalogacao"%>

<f:view>

<h2> <ufrn:subSistema/> &gt; Agendamento de Orienta��o de Normaliza��o</h2>

<c:set var="mbean" value="#{solicitacaoOrientacaoMBean}" />

<div class="descricaoOperacao"> 
    <p style="text-align: justify;">
    	Caro usu�rio, nesta tela voc� poder� solicitar um agendamento com um bibliotec�rio para 
    	receber orienta��o a respeito da normaliza��o de uma obra. Para isso, basta selecionar 
    	uma biblioteca e, se desejar, informar alguns dados relevantes para o bibliotec�rio, 
    	como por exemplo o hor�rio dispon�vel para ser atendido.
    	Um bibliotec�rio ir� atender o seu pedido e agendar um hor�rio <strong>segundo disponibilidade</strong>, 
    	o qual voc� <strong>dever�</strong> aprovar ou n�o. 
   	</p>
</div>

<h:form id="formSolicitaOrientacao">

<a4j:keepAlive beanName="solicitacaoOrientacaoMBean"></a4j:keepAlive>

<table class="formulario" width="60%">
	<caption class="listagem">Novo Agendamento de Orienta��o de Normaliza��o</caption>
	
	<tr>
		<th style="width: 40%" class="required">Biblioteca de Envio da Solicita��o:</th>
		<td>
			<h:selectOneMenu id="comBoxBibliotecas"
					value="#{mbean.obj.biblioteca.id}"
					style="display: inline; min-width: 310px">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
				<f:selectItems value="#{mbean.bibliotecasCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th style="width: 40%" class="required">Disponibilidade:</th>
		<td>
			<h:selectBooleanCheckbox id="smcTurnoDisponivelManha" value="#{mbean.turnoManha}"></h:selectBooleanCheckbox>Manh�
			<h:selectBooleanCheckbox id="smcTurnoDisponivelTarde" value="#{mbean.turnoTarde}"></h:selectBooleanCheckbox>Tarde
			<h:selectBooleanCheckbox id="smcTurnoDisponivelNoite" value="#{mbean.turnoNoite}"></h:selectBooleanCheckbox>Noite
			<ufrn:help>Indique os turnos nos quais voc� tem mais disponibilidade para ser atendido.</ufrn:help>
		</td>
	</tr>
	<tr>
		<th>Coment�rios:</th>
		<td>
			<h:inputTextarea id="txtComentarios" value="#{mbean.obj.comentariosUsuario}" cols="45" rows="5" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 100);" />
			<ufrn:help>Digite aqui seu hor�rio dispon�vel e outras informa��es importantes que gostaria de passar ao bibliotec�rio.</ufrn:help>
		</td>
	</tr>
	<tr>
		<th style="font-weight: normal;">Caracteres Restantes:</th>
		<td>
			<span id="quantidadeCaracteresDigitados">100</span>/100
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="confirmButton" value="#{mbean.confirmButton}" action="#{mbean.cadastrarSolicitacao}" disabled="#{empty(mbean.bibliotecas)}" /> 
				<h:commandButton id="voltar" value="<< Voltar" action="#{mbean.verMinhasSolicitacoes}" />
				<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{mbean.cancelar}" immediate="true"/>
			</td>
		</tr>
	</tfoot>		
</table>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</h:form>

</f:view>

<script type="text/javascript">
<!--

function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

-->
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

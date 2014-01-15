<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoDocumentoNormalizacaoCatalogacao"%>

<f:view>

<h2> <ufrn:subSistema/> &gt; Solicitação de Normalização</h2>

<c:set var="mbean" value="#{solicitacaoNormalizacaoMBean}" />

<div class="descricaoOperacao"> 
    <p>Caro usuário, para realizar a solicitação de normalização selecione a biblioteca de destino e informe o 
    	documento a ser normalizado.</p>
</div>

<h:form id="formSolicitaNormalizacao" enctype="multipart/form-data">

<a4j:keepAlive beanName="solicitacaoNormalizacaoMBean"></a4j:keepAlive>
<a4j:keepAlive beanName="solicitacaoServicoDocumentoMBean"></a4j:keepAlive>

<table class="formulario" width="98%">
	<caption class="listagem">Nova Solicitação de Normalização</caption>
	
	<tr>
		<th style="width: 40%" class="required">Biblioteca de Envio da Solicitação:</th>
		<td>
			<h:selectOneMenu id="comBoxBibliotecas"
					value="#{mbean.obj.biblioteca.id}"
					style="display: inline;">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{mbean.bibliotecasCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="required" width="25%">Tipo de Obra:</th>
		<td>
			<h:selectOneMenu
					id="tipoDocumento"
					value="#{mbean.obj.tipoDocumento.id}"
					onchange="mudarTipoDocumento()">
				<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --"  />
				<f:selectItems value="#{tipoDocumentoNormalizacaoCatalogacaoMBean.allCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr id="trTipoOutro">
		<th class="required">Especifique o Tipo da Obra:</th>
		<td>
			<h:inputText
					value="#{mbean.obj.outroTipoDocumento}"
					id="tipoOutro" size="50" maxlength="100"/>
		</td>
	</tr>

	<tr>
		<th class="obrigatorio" style="font-weight: normal; padding-right: 13px; width: 25%">Obra: <h:graphicImage url="/img/porta_arquivos/icones/doc.png"/> </th>
		<td>
			<t:inputFileUpload id="ifuArquivoObra" size="70" value="#{solicitacaoNormalizacaoMBean.arquivoTrabalho}" />
			<span style="vertical-align: bottom;"><ufrn:help>O arquivo digital da obra/trabalho deve estar no formato .doc ou docx.</ufrn:help></span>
		</td>
	</tr>

	<tr>
		<th class="required">Aspectos a serem Normalizados:</th>
		<td>
			<h:selectBooleanCheckbox value="#{mbean.obj.trabalhoTodo}" 
					id="checkTrabalhoTodo" onclick="deselecionaOutrosCheckBox(this);"/>
			Trabalho no todo<br/>
			<h:selectBooleanCheckbox value="#{mbean.obj.referencias}"
					id="checkReferencias"  onclick="deselecionaCheckBoxTodos();" styleClass="naoTrabalhoTodo" />
			Referências<br/>
			<h:selectBooleanCheckbox value="#{mbean.obj.citacoes}"
					id="checkCitacoes"     onclick="deselecionaCheckBoxTodos();" styleClass="naoTrabalhoTodo" />
			Citações <br/>
			<h:selectBooleanCheckbox value="#{mbean.obj.estruturaDoTrabalho}"
					id="checkEstrutura"    onclick="deselecionaCheckBoxTodos();" styleClass="naoTrabalhoTodo" />
			Estrutura do trabalho<br/>
			<h:selectBooleanCheckbox value="#{mbean.obj.preTextuais}"
					id="checkPreTextuais"  onclick="deselecionaCheckBoxTodos();" styleClass="naoTrabalhoTodo" />
			Pré-textuais<br/>
			<h:selectBooleanCheckbox value="#{mbean.obj.proTextuais}"
					id="checkPosTextuais"  onclick="deselecionaCheckBoxTodos();" styleClass="naoTrabalhoTodo" />
			Pós-textuais<br/>
			<h:selectBooleanCheckbox value="#{mbean.obj.outrosAspectosNormalizacao}"
					id="checkOutros" styleClass="naoTrabalhoTodo" /> 
			Outros: <h:inputText value="#{mbean.obj.descricaoOutrosAspectosNormalizacao}" size="50" maxlength="250"/>
		</td>
	</tr>
	
	<%-- <tr>
		<th>Autoriza Descarte?</th>
		<td>
			<h:selectOneRadio id="autorizaDescarte" value="#{mbean.obj.autorizaDescarte}" style="display: inline;">
				<f:selectItem itemLabel="Sim" itemValue="true"/>
				<f:selectItem itemLabel="Não" itemValue="false"/>
			</h:selectOneRadio>
		</td>
	</tr> --%>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="confirmButton" value="#{mbean.confirmButton}" action="#{mbean.cadastrarSolicitacao}" 
							onclick="return confirm('Confirma o envio do pedido para atendimento ?');"
							disabled="#{empty(mbean.bibliotecas)}" /> 
				<h:commandButton id="voltar" value="<< Voltar" action="#{mbean.verMinhasSolicitacoes}" immediate="true" />
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

function mudarTipoDocumento() {
	var sel = $('formSolicitaNormalizacao:tipoDocumento');
	if (sel.options[sel.selectedIndex].value == '<%= TipoDocumentoNormalizacaoCatalogacao.getIdTipoDocumentoOutro() %>') {
		$('trTipoOutro').show();
	} else {
		$('trTipoOutro').hide();
	}
}

mudarTipoDocumento();

function deselecionaOutrosCheckBox(check){

	if ( $("formSolicitaNormalizacao:checkTrabalhoTodo").checked == true ) {
		$$(".naoTrabalhoTodo").each( function( elem ) {
			$(elem).checked = false;
		} );
	}
}

function deselecionaCheckBoxTodos(){
	$('formSolicitaNormalizacao:checkTrabalhoTodo').checked = false;
}

-->
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

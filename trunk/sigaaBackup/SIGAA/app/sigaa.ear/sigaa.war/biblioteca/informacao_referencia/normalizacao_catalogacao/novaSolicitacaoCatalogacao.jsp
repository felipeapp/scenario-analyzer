<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoDocumentoNormalizacaoCatalogacao"%>

<f:view>

<h2> <ufrn:subSistema/> &gt; Solicitação de Catalogação na Fonte</h2>

<c:set var="mbean" value="#{solicitacaoCatalogacaoMBean}" />

<c:if test="${mbean.dadosComunicacaoPessoaDefinidos}">
	<div class="descricaoOperacao">  
	    <p>Caro usuário, para realizar a solicitação de catalogação selecione a biblioteca de destino e informe o 
	    	documento a ser catalogado.</p>
	</div>
	
	<h:form id="formSolicitaCatalogacao" enctype="multipart/form-data">
	
	<a4j:keepAlive beanName="solicitacaoCatalogacaoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="solicitacaoServicoDocumentoMBean"></a4j:keepAlive>
	
	<table class="formulario" width="98%">
		<caption class="listagem">Nova Solicitação de Catalogação na Fonte</caption>
		
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
				<t:inputFileUpload id="ifuArquivoObra" size="70" value="#{solicitacaoCatalogacaoMBean.arquivoTrabalho}" />
				<span style="vertical-align: bottom;"> <ufrn:help>O arquivo digital da obra/trabalho deve estar no formato .doc ou docx.</ufrn:help></span>
			</td>
		</tr>
		
		<tr id="trNPaginas" >
			<th class="required">Nº de páginas:</th>
			<td>
				<h:inputText value="#{mbean.obj.numeroPaginas}" size="3" maxlength="4" onkeypress="return ApenasNumeros(event);"/>
			</td>
		</tr>
		
		<tr id="trPalavrasChave">
			<th class="required">Palavras-chave (de 3 a 6):</th>
			<td>
				<h:inputText size="60" maxlength="255" id="palavrasChaves" value="#{mbean.obj.palavrasChaveString}" />
				<ufrn:help>Separe as palavras com vírgula. Por exemplo: programação, didática, ficção...</ufrn:help>
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
					<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{mbean.cancelar}" immediate="true" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
	
	</h:form>
	
</c:if>
<c:if test="${!mbean.dadosComunicacaoPessoaDefinidos}">
	<div class="descricaoOperacao"> 		
	<p>Caro usuário, para poder confirmar a solicitação de catalogação na fonte, é necessário que seus dados de email, telefone e/ou celular estejam atualizados.
			Por favor, Atualize seus dados de contato clicando abaixo.</p>
	
	<h:form>
	<table  class="subFormulario" align="center">
		<tr>
		<td width="8%" valign="middle" align="center" style="background-color:#FFFFE4">
		<h:commandLink  id="meusDadosPessoais" action="#{ alteracaoDadosDiscente.iniciarAcessoDiscente}" value="Meus Dados Pessoais" />
		</td>
		</tr>
		</table>
	</h:form>
	</div>
</c:if>

</f:view>

<script type="text/javascript">
<!--

function mudarTipoDocumento() {
	var sel = $('formSolicitaCatalogacao:tipoDocumento');
	if (sel.options[sel.selectedIndex].value == '<%= TipoDocumentoNormalizacaoCatalogacao.getIdTipoDocumentoOutro() %>') {
		$('trTipoOutro').show();
	} else {
		$('trTipoOutro').hide();
	}
}

mudarTipoDocumento();

function deselecionaOutrosCheckBox(check){

	if ( $("formSolicitaCatalogacao:checkTrabalhoTodo").checked == true ) {
		$$(".naoTrabalhoTodo").each( function( elem ) {
			$(elem).checked = false;
		} );
	}
}

function deselecionaCheckBoxTodos(){
	$('formSolicitaCatalogacao:checkTrabalhoTodo').checked = false;
}

-->
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

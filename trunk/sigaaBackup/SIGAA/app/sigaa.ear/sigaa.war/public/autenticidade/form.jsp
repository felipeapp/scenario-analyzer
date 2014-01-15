<%@include file="/public/include/cabecalho.jsp" %>
<%@page import="br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado"%>

<f:view>

<script type="text/javascript">
	function changeTipoDocumento(obj) {

		var tr_matricula = document.getElementById("tr_matricula") ;
		var tr_documento = document.getElementById("tr_documento") ;

		if ((obj.value == <%= String.valueOf(TipoDocumentoAutenticado.DECLARACAO_COM_IDENTIFICADOR) %>) ||
			(obj.value == <%= String.valueOf(TipoDocumentoAutenticado.CERTIFICADO) %>)){
			tr_documento.style.display = "";
			tr_matricula.style.display = "none";
		}else{
			tr_documento.style.display = "none";
			tr_matricula.style.display = "";
		}		
	}	
</script>


<h2> Validação de Documentos </h2>

	<div class="descricaoOperacao">
		<p>
			Bem-vindo ao validador de documentos emitidos pela ${ configSistema['siglaInstituicao'] }. Este serviço propõe-se a confirmar a validade
			dos <b>documentos emitidos pelo ${ configSistema['siglaSigaa'] } </b>.
		</p>
		<p>
			Para proceder com validação informe os seguintes dados:
		</p>
		<ul>
			<c:if test="${autenticidade.emissao.tipoDocumento != DECLARACAO_QUITACAO_BIBLIOTECA}">
				<li> <b>Identificador:</b> <i>dependendo do tipo de documento pode ser: Matrícula do Aluno, SIAPE, CPF. Localizada no cabeçalho do documento</i> </li>
			</c:if>
		
			<c:if test="${autenticidade.emissao.tipoDocumento == DECLARACAO_QUITACAO_BIBLIOTECA}">
				<li> <b>Identificador:</b> <i>dependendo do tipo de documento pode ser: Matrícula do Aluno, SIAPE, CPF. Localizada no próprio documento</i> </li>
			</c:if>
		
			<li> <b>Data de Emissão:</b> <i>localizada no cabeçalho do documento</i> </li>
			<li> <b>Código de Verificação:</b> <i>impresso no rodapé do documento</i> </li>
			<c:if test="${autenticidade.emissao.tipoDocumento != DECLARACAO_QUITACAO_BIBLIOTECA}">
				<li> <b>Número do documento:</b> <i>impresso no rodapé do documento</i> </li>
			</c:if>
		</ul>
	</div>

	<c:if test="${not empty requestScope.erroValidacao}">
		<h3 style="color: red; text-align: center"> ${requestScope.erroValidacao} </h3>
		<br />
	</c:if>
<h:messages showDetail="true"/>

	<table class="formulario" width="60%">

	<caption> Dados para Validação </caption>

	<h:form id="form">

	<c:set var="DECLARACAO_COM_IDENTIFICADOR" 	value="<%= String.valueOf(TipoDocumentoAutenticado.DECLARACAO_COM_IDENTIFICADOR) %>"	scope="application"/>
	<c:set var="HISTORICO" 			value="<%= String.valueOf(TipoDocumentoAutenticado.HISTORICO) %>"	scope="application"/>
	<c:set var="HISTORICO_MEDIO" 			value="<%= String.valueOf(TipoDocumentoAutenticado.HISTORICO_MEDIO) %>"	scope="application"/>
	<c:set var="ATESTADO_MATRICULA" 			value="<%= String.valueOf(TipoDocumentoAutenticado.ATESTADO) %>"	scope="application"/>
	<c:set var="DECLARACAO_QUITACAO_BIBLIOTECA" value="<%= String.valueOf(TipoDocumentoAutenticado.DECLARACAO_QUITACAO_BIBLIOTECA) %>"	scope="application"/>
	<c:set var="TERMO_PUBLICACAO_TESE_DISSERTACAO" value="<%= String.valueOf(TipoDocumentoAutenticado.TERMO_PUBLICACAO_TESE_DISSERTACAO) %>"	scope="application"/>
	
	<c:if test="${(autenticidade.emissao.tipoDocumento == DECLARACAO_COM_IDENTIFICADOR) ||(autenticidade.emissao.tipoDocumento == HISTORICO) ||(autenticidade.emissao.tipoDocumento == ATESTADO_MATRICULA) || (autenticidade.emissao.tipoDocumento == DECLARACAO_QUITACAO_BIBLIOTECA) || (autenticidade.emissao.tipoDocumento == TERMO_PUBLICACAO_TESE_DISSERTACAO) || (autenticidade.emissao.tipoDocumento == HISTORICO_MEDIO)}">
		<tr>
			<th class="required" >Identificador:</th>
			<td><h:inputText value="#{autenticidade.emissao.identificador}" size="9" id="matricula"/> </td>
		</tr>
	</c:if>


	<c:set var="DECLARACAO_COM_NUMDOCUMENTO" 	value="<%= String.valueOf(TipoDocumentoAutenticado.DECLARACAO_COM_NUMDOCUMENTO) %>"	scope="application"/>
	<c:set var="CERTIFICADO" 	value="<%= String.valueOf(TipoDocumentoAutenticado.CERTIFICADO) %>"	scope="application"/>

	<c:if test="${(autenticidade.emissao.tipoDocumento == DECLARACAO_COM_NUMDOCUMENTO) ||(autenticidade.emissao.tipoDocumento == CERTIFICADO)}">
		<tr>
			<th width="150" class="required"> Número do documento: </th>
			<td> <h:inputText value="#{autenticidade.emissao.numeroDocumento}" size="9" id="numero_documento" onkeyup="return formatarInteiro(this)" /> </td>
		</tr>
	</c:if>
	<tr>
		<th width="30%" class="required">Data de Emissão: </th>
		<td>
			<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{autenticidade.emissao.dataEmissao}"
			onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
			popupDateFormat="dd/MM/yyyy"  size="10" maxlength="10" id="data_emissao"/>
		</td>
	</tr>
	<tr>
		<th width="25%" class="required">Código de Verificação: </th>
		<td> <h:inputText value="#{autenticidade.emissao.codigoSeguranca}" id="codigo_verificacao"/> </td>
	</tr>


	<tr>
		<td colspan="2" align="center" style="background: #F5F5F5">
			<table>
				<tr>
				<th width="15%" class="required">
					<td align="center">
						<i>	Digite o conteúdo da imagem ao lado: <br>
						</i><br>
						<h:inputText value="#{autenticidade.captcha}" size="8" style="font-size: 14px" id="captcha"/>
					</td>
					<td>
						<img src="/sigaa/captcha.jpg" id="imagem"/> <br>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton action="#{autenticidade.validar}" value="Validar Documento" style="font-size: 10px; font-weight: bold" id="bt_validar"/>
			
		 </td>
	</tr>
	</tfoot>

	</h:form>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
</f:view>
<br>
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/autenticidade/tipo_documento.jsf" style="color: #404E82;"><< selecionar novo tipo de documento</a> ||
		<a href="/sigaa/public/home.jsf" style="color: #404E82;"><< voltar ao menu principal</a>
	</div>
<br>
<%@include file="/public/include/rodape.jsp" %>
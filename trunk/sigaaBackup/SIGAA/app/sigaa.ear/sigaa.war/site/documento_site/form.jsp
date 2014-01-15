<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<c:if test="${documentoSite.portalDocente}"> 
	<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>
	<c:if test="${documentoSite.portalCoordenadorStricto}">
	<%@include file="/stricto/menu_coordenador.jsp"%>
	</c:if>
	<c:if test="${documentoSite.portalCoordenadorGraduacao}">
	<%@include file="/graduacao/menu_coordenador.jsp"%>
	</c:if>

	<h2><ufrn:subSistema /> &gt; Documentos 
	&gt; ${documentoSite.confirmButton}</h2>

	<div class="descricaoOperacao">
		<p>Caro Usu�rio(a),</p>
		<br />
		<p>O formul�rio permite ao usu�rio cadastrar/alterar um documento.
			Ap�s o usu�rio clicar no bot�o <b>Cadastrar</b> ou <b>Alterar</b>, as modifica��es ser�o disponibilizadas 
			imediatamente no portal p�blico. 
		</p>
	</div>

	<h:messages />
	<h:form id="formulario" enctype="multipart/form-data">
		<table class=formulario width="100%">
			<caption>Dados do Documento</caption>
			<h:inputHidden value="#{documentoSite.confirmButton}" />
			<h:inputHidden value="#{documentoSite.obj.id}" />
			<h:inputHidden value="#{documentoSite.obj.idArquivo}" />
			<h:inputHidden value="#{documentoSite.obj.dataCadastro}" />

			<tr>
				<th class="required" width="10%">Categoria:</th>
				<td width="70%" >
					<h:selectOneMenu
						value="#{documentoSite.obj.tipoDocumentoSite.id}" id="idTipoDocumentoSite"
						disabled="#{tipoDocumentoSite.readOnly}">
						<f:selectItems value="#{tipoDocumentoSite.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required" width="10%">Nome:</th>
				<td width="70%"><h:inputText size="80" maxlength="100"
					value="#{documentoSite.obj.nome}"
					readonly="#{documentoSite.readOnly}" /></td>
			</tr>
			<tr>
				<th class="${documentoSite.obj.id==0?'required':''}">Arquivo:</th>
				<td>
					<t:inputFileUpload value="#{documentoSite.documento}"
					size="68" id="documento" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{documentoSite.confirmButton}"
						action="#{documentoSite.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}"
						action="#{documentoSite.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif"	style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
		<br><br>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
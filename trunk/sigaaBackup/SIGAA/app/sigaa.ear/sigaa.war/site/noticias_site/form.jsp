<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js"
	type="text/javascript"></script>
<script type="text/javascript">
	tinyMCE.init({
	  theme_advanced_toolbar_location : "bottom",
	  content_css : "${ctx}/site/css/tinyMCE_content.css",
      mode : "textareas",
      theme : "advanced",
      width : "100%",
      height : "450",
      language : "en",
      plugins : "preview, emotions, iespell, print, fullscreen, advhr, directionality, searchreplace, insertdatetime, paste",
      plugin_preview_width : "500",
      plugin_preview_height : "600",
      extended_valid_elements : "hr[class|width|size|noshade]",
      plugin_insertdate_dateFormat : "%Y-%m-%d",
      plugin_insertdate_timeFormat : "%H:%M:%S"
	});
</script>
<f:view>

	<h:messages></h:messages>
	<c:if test="${noticiaSite.portalDocente}"> 
	<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>
	<c:if test="${noticiaSite.portalCoordenadorStricto}">
	<%@include file="/stricto/menu_coordenador.jsp"%>
	</c:if>
	<c:if test="${noticiaSite.portalCoordenadorGraduacao}">
	<%@include file="/graduacao/menu_coordenador.jsp"%>
	</c:if>
	
	<h2><ufrn:subSistema /> &gt; Not�cias &gt; ${noticiaSite.confirmButton}</h2>
	
	<div class="descricaoOperacao">
		<p>Caro Usu�rio(a),</p>
		<br />
		<p>
		O formul�rio permite cadastrar/alterar uma not�cia. Caso deseje visualizar uma not�cias antes de public�-la, basta selecionar a op��o <b>Publicar : N�o</b> e ap�s a inclus�o ou altera��o da not�cia, utilizar o �cone <b>Visualizar</b>. Para publicar a not�cia, basta escolher uma not�cia na listagem e selecionar a op��o 'Alterar', mudando o item <b>Publicar: </b> para <b>Sim</b> 
		</p>
	</div>
	
	
	<h:form id="formulario" enctype="multipart/form-data">
		<h:inputHidden value="#{noticiaSite.confirmButton}" />
		<table class=formulario width="85%">
			<caption class="listagem">Dados da Not�cia</caption>
			<tr>
				<th class="required">Publicar:</th>
				<td >
					<h:selectOneRadio id="publicar" value="#{noticiaSite.obj.publicada}" >
						<f:selectItems value="#{noticiaSite.simNao}" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th class="required">Idioma:</th>
				<td><h:selectOneMenu value="#{noticiaSite.obj.locale}"
					readonly="#{noticiaSite.readOnly}">
					<f:selectItems value="#{noticiaSite.linguagens}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Titulo:</th>
				<td><h:inputText size="72" maxlength="200"
					value="#{noticiaSite.obj.titulo}"
					readonly="#{noticiaSite.readOnly}" /></td>
			</tr>
			<tr>
				<th>Foto da Not�cia:</th>
				<td valign="middle">
					<c:if test="${not empty noticiaSite.obj.idFoto}">
					<a href="/sigaa/verFoto?idFoto=${noticiaSite.obj.idFoto}
						&key=${ sf:generateArquivoKey(noticiaSite.obj.idFoto) }"
						title="Clique aqui para visualizar a foto da not�cia" target="_blank" >
						<img src="/sigaa/img/picture.gif" /> 
					</a>
					</c:if>
					<t:inputFileUpload value="#{noticiaSite.foto}" size="68" id="foto" />
				</td>
			</tr>
			<tr>
				<th>Arquivo:</th>
				<td>
					<c:if test="${not empty noticiaSite.obj.idArquivo}">
						<a class="linkArquivo"
						href="/sigaa/verProducao?idProducao=${noticiaSite.obj.idArquivo}
						&key=${ sf:generateArquivoKey(noticiaSite.obj.idArquivo) }"
						target="_blank" title="Visualizar arquivo da not�cia" > 
						<img src="/sigaa/img/file.png" />
						</a>
					</c:if>
					<t:inputFileUpload value="#{noticiaSite.anexo}" size="68" id="anexo" />
				</td>
			</tr>
			<tr>
				<th valign="top" class="required">Descri��o:</th>
				<td><h:inputTextarea cols="70" rows="4"
					value="#{noticiaSite.obj.descricao}"
					readonly="#{noticiaSite.readOnly}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="#{noticiaSite.confirmButton}" action="#{noticiaSite.cadastrar}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{noticiaSite.cancelar}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigat�rio. </span> <br>
	<br>
	</center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
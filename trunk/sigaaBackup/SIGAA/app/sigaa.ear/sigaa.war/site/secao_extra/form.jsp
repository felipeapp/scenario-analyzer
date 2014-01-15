<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
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
	
	<c:if test="${secaoExtraSite.portalDocente}"> 
	<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>
	<c:if test="${secaoExtraSite.portalCoordenadorStricto}">
	<%@include file="/stricto/menu_coordenador.jsp"%>
	</c:if>
	<c:if test="${secaoExtraSite.portalCoordenadorGraduacao}">
	<%@include file="/graduacao/menu_coordenador.jsp"%>
	</c:if>
	
	<h2><ufrn:subSistema /> &gt; 
	<a href="/sigaa/site/secao_extra/lista.jsf">Se��o Extra </a> &gt; ${secaoExtraSite.confirmButton}</h2>
	
	<div class="descricaoOperacao">
		<p>Caro Usu�rio(a),</p>
		<br />
		<p>O formul�rio permite ao usu�rio cadastrar/alterar uma se��o extra(p�gina), e selecionar o status <b>Pendente</b> caso deseje visualiz�-la
			 antes de publicar.
			Selecione a op��o n�o no campo <u>Publicar</u>, e cadastre a se��o extra.
			Ser� exibido uma listagem das se��es extras ordenadas por status e data de cadastro,
			clique no �cone <b>Visualizar</b> correspondente a se��o extra desejada. 
			Ap�s a visualiza��o, para publicar acesse listagem das not�cias e clique no �cone <b>Alterar</b> da se��o correspondente,
			 selecione sim no campo <u>Publicar</u>, e clique no bot�o <b>Alterar</b> 
		</p>
	</div>

	<h:form id="form">
		<h:inputHidden value="#{secaoExtraSite.confirmButton}" />

		<table class=formulario width="100%">
			<caption class="listagem">Dados da Se��o Extra</caption>
			<tr>
				<th class="required">Publicar:</th>
				<td >
					<h:selectOneRadio id="publicar" value="#{secaoExtraSite.obj.publicada}" >
						<f:selectItems value="#{secaoExtraSite.simNao}" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th class="required">Idioma:</th>
				<td><h:selectOneMenu value="#{secaoExtraSite.obj.locale}"
					disabled="#{secaoExtraSite.readOnly}">
					<f:selectItems value="#{secaoExtraSite.linguagens}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th width="18%">Acesso Link Externo:</th>
				<td align="left"><t:selectOneRadio id="permiteLinkExterno"
					onclick="exibirCampos();" onchange="exibirCampos();"
					value="#{secaoExtraSite.obj.isLinkExterno}"
					styleClass="noborder radio11" readonly="#{secaoExtraSite.readOnly}">
					<f:selectItem itemLabel="Sim" itemValue="true" />
					<f:selectItem itemLabel="N�o" itemValue="false" />
				</t:selectOneRadio></td>
			</tr>
			<tr id="trTitulo">
				<th class="required">Titulo da Se��o:</th>
				<td><h:inputText size="72" maxlength="200"
					value="#{secaoExtraSite.obj.titulo}"
					readonly="#{secaoExtraSite.readOnly}" /></td>
			</tr>
			<tr id="trLinkExterno">
				<th class="required">Link Externo:</th>
				<td><h:inputText size="72" maxlength="200"
					value="#{secaoExtraSite.obj.linkExterno}" id="linkExterno"
					readonly="#{secaoExtraSite.readOnly}" /></td>
			</tr>
			<tr id="trDescricao">
				<td colspan="2" align="center"><b><i> Conte�do da Se��o
				</i></b>
				<center><i> <small> Escreva aqui o seu texto
				formatado da maneira que deseja que apare�a na p�gina </small> </i></center>

				<h:inputTextarea id="descricao" style="width: 95%" rows="4"
					value="#{secaoExtraSite.obj.descricao}"
					readonly="#{secaoExtraSite.readOnly}"
					disabled="#{secaoExtraSite.readOnly}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2>
					<h:commandButton
						value="#{secaoExtraSite.confirmButton}"
						action="#{secaoExtraSite.cadastrar}"/>
					 <h:commandButton
						value="Cancelar" onclick="#{confirm}"
						action="#{secaoExtraSite.cancelar}" /> 
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
<script>
function exibirCampos(){
	var valor = getEl('form:permiteLinkExterno').getChildrenByTagName('input')[0].dom.checked;
	if (valor != true) {
		$('trTitulo').show()
		//$('form:linkExterno').value='';
		$('trLinkExterno').hide();
		$('trDescricao').show();
	} else {
		$('form:descricao').value='';
		$('trDescricao').hide();
		$('trLinkExterno').show();
	}
} 
exibirCampos();
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
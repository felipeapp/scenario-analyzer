<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script src="/shared/javascript/tiny_mce/tiny_mce.js"
	type="text/javascript"></script>

<link rel="stylesheet" type="text/css" href="/shared/css/mailbox.css" />
<link href="/shared/css/mailbox_novo.css" type="text/css"
	rel="stylesheet" />
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:form  enctype="multipart/form-data" id="form">
	<h2><ufrn:subSistema /> > Comunicar Coordenadores de Ações de Extensão</h2>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" /> Remover Coordenador
	</div>
			
	<c:set var="coordenadores" value="#{comunicarCoordenadores.coordenadoresNotificacao}" />
	
		<h:panelGroup style="width:100%; height:200px; overflow:auto;" layout="block" id="tableCoord">
			<table class="formulario" width="100%" id="tabCabecalho">
				<caption>Enviar Comunicado</caption>
				<tr>
					<td style="text-align: center"><h3>Coordenadores (${ fn:length(comunicarCoordenadores.coordenadoresNotificacao) })</h3></td>
				</tr>
			</table>
		
			<table class="listagem">
				<c:forEach items="#{comunicarCoordenadores.coordenadoresNotificacao}" var="coord" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${coord.pessoa.nome}</td>
						<td>${coord.unidade.nome}</td>
						<td style="text-align: right;">
							<a4j:commandLink action="#{comunicarCoordenadores.removerCoordenadorNotificacao}" reRender="tableCoord" title="Remover Coordenador" id="btnRmv" >
								<f:param name="idCoord" value="#{coord.pessoa.id}" />
								<h:graphicImage url="/img/delete.gif" />
							</a4j:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</h:panelGroup>
		
		<table class="formulario" width="100%" style="border-style:none;">
			<tr>
				<td class="obrigatorio" align="right">Assunto:&nbsp;&nbsp;</td>
				<td>
					<h:inputText value="#{comunicarCoordenadores.obj.titulo}"
						size="103" id="assunto" style="align: center" maxlength="500">
				</h:inputText></td>
			</tr>
		</table>
		<table class="formulario" width="100%" style="border-style:hidden;">
			<tbody>
				<tr>
					<td colspan="2">&nbsp;&nbsp;Corpo da Mensagem: <span class="required" /></td>
				</tr>
				<tr>
					<td colspan="2" >
						<h:inputTextarea id="texto" 
							value="#{comunicarCoordenadores.obj.mensagem}" />
					</td>
				</tr>
			</tbody>
		</table>
		<table class="formulario" width="100%" style="border-style:none;">
			<tbody>
				<tr>
					<td colspan="2" style="text-align: left">Responder para:
						<h:inputText value="#{comunicarCoordenadores.obj.emailRespostas}" 
							size="103" id="emailRespostas" style="align: center"/>
							<ufrn:help>Preencha este campo com um e-mail válido, caso deseje receber respostas dos destinatários.</ufrn:help> <span class="info"> (e-mail opcional) </span>
					</td>
				</tr>
				
				<tr>
					<td align="left"><img src="/shared/img/anexo.png" />Anexar arquivo:</td>
					<td align="left">
						<t:inputFileUpload value="#{comunicarCoordenadores.obj.anexo}"
							storage="file" id="arq" size="90" />
						<span class="info"> (opcional) </span>
					</td>
				</tr>
				
				<tr>
				<td colspan="2" align="center">
					<h:selectBooleanCheckbox title="Enviar cópia para meu Email"  id="enviarEmail"
						value="#{comunicarCoordenadores.enviarCopia}"> 
					</h:selectBooleanCheckbox>
					<h:outputText
						value="Desejo enviar esta mensagem como cópia para o meu e-mail" />
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Enviar Comunicado" action="#{comunicarCoordenadores.enviar}" />
						<input type="button" value="<< Voltar"	onclick="javascript:history.go(-1)" />
						<h:commandButton value="Cancelar" action="#{comunicarCoordenadores.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
		<center>
			<div class="required-items">
				<span class="required"></span>Campos de Preenchimento Obrigatório
			</div>
		</center>
		
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas",
    theme : "advanced",
    width : "100%",
    height : "300",
    language : "pt",
    theme_advanced_toolbar_location : "top",
    plugins : "table, preview, iespell, print, fullscreen, advhr, directionality, searchreplace, insertdatetime, paste",
    theme_advanced_buttons1 : "fullscreen,separator,preview,separator,cut,copy,paste,separator,undo,redo,separator,search,replace,separator,code,separator,cleanup,separator,bold,italic,underline,strikethrough,separator,forecolor,backcolor,separator,justifyleft,justifycenter,justifyright,justifyfull,separator,help",
    theme_advanced_buttons2 : "removeformat,styleselect,formatselect,fontselect,fontsizeselect,separator,bullist,numlist,outdent,indent,separator,link,unlink,anchor",
    plugin_preview_width : "400",
    plugin_preview_height : "300",
    fullscreen_settings : {
    	theme_advanced_path_location : "top"
    },
    extended_valid_elements : "hr[class|width|size|noshade]",
    plugin_insertdate_dateFormat : "%Y-%m-%d",
    plugin_insertdate_timeFormat : "%H:%M:%S"
});

</script>

<ul class="form">

	<h:panelGroup rendered="#{ forumMensagemBean.obj.tipoResposta }">	
		<li>
			<table>
				<tr>
					<%-- Foto e mensagem --%>
					<td align="center" style="background-color: #EFF3FA;" valign="top" rowspan="2" width="10%">				 
						<c:if test="${ not empty forumMensagemBean.obj.mensagemPai.usuario.idFoto }">
							<img src="${ ctx }/verFoto?idFoto=${ forumMensagemBean.obj.mensagemPai.usuario.idFoto }&key=${ sf:generateArquivoKey( forumMensagemBean.obj.mensagemPai.usuario.idFoto) }" width="60" height="80"/>
						</c:if>
						<c:if test="${ empty forumMensagemBean.obj.mensagemPai.usuario.idFoto }">
							<img src="${ ctx }/img/no_picture.png" width="60" height="80"/>
						</c:if>
					</td>
					<td>
						<li>
							
							<b><h:outputText id="titulo" value="#{ forumMensagemBean.obj.mensagemPai.titulo }" /></b>
							<br/>
							<i> por </i> <h:outputText id="por" value="#{ forumMensagemBean.obj.mensagemPai.usuario.nome }" />
							<i> em </i><h:outputText id="em" value="#{ forumMensagemBean.obj.mensagemPai.data }"><f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/></h:outputText>
							<br/>
							<br/>
							<p style="text-align: justify;"><h:outputText id="conteudo" value="#{ forumMensagemBean.obj.mensagemPai.conteudo }" escape="false" /></p>
						</li>
					</td>
				</tr>
			</table>
		</li>		
		<hr/>
		<br />
	</h:panelGroup>

	<li>
		<label class="required" for="assunto">Assunto:<span class="required">&nbsp;</span></label> 
		<h:inputText id="assunto" size="59"	value="#{ forumMensagemBean.obj.titulo }" title="Título" />
	</li>
	<li>
		<label class="required" for="mensagem">Mensagem:<span class="required">&nbsp;</span></label> 
		<t:inputTextarea value="#{ forumMensagemBean.obj.conteudo }" cols="20" rows="20" id="mensagem" title="Mensagem"/>
	</li>
	
	<li>
		<label for="arquivo">Arquivo: </label>
		<t:inputFileUpload id="uFile" value="#{ forumMensagemBean.obj.arquivo }" storage="file" size="50"/>		 
	</li>
	<li>
		<label for="notificar">&nbsp;</label>
		<h:selectBooleanCheckbox id="notificar" value="#{forumMensagemBean.obj.notificarTurma }" rendered="#{ forumMensagemBean.obj.tipoTopico && forumMensagemBean.cadastrarNovoTopico}" />
		<h:outputText value=" Notificar criação do tópico para a turma?" rendered="#{ forumMensagemBean.obj.tipoTopico && forumMensagemBean.cadastrarNovoTopico}"/>
	</li>
	
</ul>


<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "620", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>
<%@ taglib uri="/tags/mf_core" prefix="f" %>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
	tinyMCE.init({
    	mode : "textareas",
        theme : "advanced",
        width : "95%",
        height : "200",
        language : "pt_br",
        plugins : "preview, emotions, iespell, print, fullscreen, zoom, advhr, directionality, searchreplace, insertdatetime,  anchor, newdocument, separator,  cut, copy, paste,  forecolor, backcolor, charmap, visualaid",
        theme_advanced_buttons3_add : "preview, emotions, iespell, print, fullscreen, zoom, advhr, ltr,rtl, search,replace,insertdate,inserttime, anchor, newdocument, separator,  cut, copy, paste, forecolor, backcolor, charmap, visualaid",
        plugin_preview_width : "500",
        plugin_preview_height : "600",
        fullscreen_settings : {
        	theme_advanced_path_location : "top"
        },
        extended_valid_elements : "hr[class|width|size|noshade]",
        plugin_insertdate_dateFormat : "%Y-%m-%d",
        plugin_insertdate_timeFormat : "%H:%M:%S"
	});
</script>

<h:outputText value="#{ forumMensagem.create }"/>
<h:outputText value="#{ forum.create }"/>

<c:if test="${ forum.forum.mural }">
<h3>Mensagens do Mural</h3>
</c:if>
<c:if test="${ !forum.forum.mural }">
<h3>Mensagens do Fórum</h3>
</c:if>

<!-- <c:set var="mensagems" value="${ forumMensagem.allMensagens }"/> -->
<c:set var="mensagems" value="${ forumMensagem.lista }"/>

<c:if test="${ not empty mensagems }">
	<table style="border: 1px solid #C4D2EB; width: 99%" cellpadding="3">
	<c:forEach items="${mensagems}" var="item" varStatus="loop">
	
		<%-- Primeira Linha: Dados da mensagem --%>
		<tr style="background-color: #C4D2EB">
			<td align="center" width="20%"><fmt:formatDate value="${ item.data }" pattern="dd/MM/yyyy HH:mm:ss"/></td>
			<td><strong>Assunto:</strong> ${ item.titulo }</td>
			<td width="8%" align="center">
				<c:if test="${ (item.usuario.id == usuario.id || turmaVirtual.docente) }">
					<a href="${ pageContext.request.contextPath }/portais/turma/ForumMensagem/form.jsf?idForum=${ param['idForum'] }&idTopico=${ param['idTopico'] }&idMensagem=${ item.id }">Editar</a>
				</c:if>
			</td>
			<c:if test="${ turmaVirtual.docente}">
			<td width="8%" align="center">
			<h:form>
				<h:commandLink value="Remover" action="#{ forumMensagem.remover }" rendered="#{ turmaVirtual.docente }"/>
				<input type="hidden" name="idForum" value="${ param['idForum'] }"/>
				<input type="hidden" name="idTopico" value="${ param['idTopico'] }"/>
				<input type="hidden" name="id" value="${ item.id }"/>
			</h:form>
			</td>
			</c:if>
		</tr>
		
		<%-- Segunda Linha: Foto e mensagem --%>
		<tr>
			<td align="center" style="background-color: #EFF3FA;">
			<c:if test="${ not empty item.usuario.idFoto }">
				<img src="${ctx}/verFoto?idFoto=${item.usuario.idFoto}" width="60" height="80"/>
			</c:if>
			<c:if test="${ empty item.usuario.idFoto }">
				<img src="${ctx}/img/no_picture.png" width="60" height="80"/>
			</c:if>
			<br/>
			${ item.usuario.pessoa.nome }
			</td>
			<td colspan="${ turmaVirtual.docente ? "3" : "2" }" valign="top"  style="background-color: #FCFCFC;">${ item.conteudo }</td>
		</tr>
	</c:forEach>
	<tr><td align="right" colspan="4">
	<c:if test="${ forumMensagem.paginaAtual > 1 }"><a href="${ctx }/portais/turma/Forum/mensagens.jsf?idForum=${ param['idForum'] }&idTopico=${ param['idTopico'] }&pagina=${ forumMensagem.paginaAtual - 1 }">Página Anterior</a></c:if> 
	&nbsp;&nbsp;&nbsp;&nbsp; 
	<c:if test="${ forumMensagem.paginaAtual < forumMensagem.totalPaginas }"><a href="${ctx }/portais/turma/Forum/mensagens.jsf?idForum=${ param['idForum'] }&idTopico=${ param['idTopico'] }&pagina=${ forumMensagem.paginaAtual + 1 }">Próxima Página</a></c:if>
	</td></tr>
	</table>
	
</c:if>
<c:if test="${ empty mensagems }">
	<p style="text-align: center; margin: 20px auto; color: red;">Não há mensagens para este tópico.</p>
</c:if>
<p>&nbsp;</p>
<h:form>

<c:if test="${ !forumMensagem.invalido }">
	<h:outputText value="#{ forumMensagem.configForm }"/>

<fieldset>
<legend>Postar Nova Mensagem</legend>
<ul>
	<li>
		<label for="assunto" class="required">Assunto <span class="required">&nbsp;</span></label>
		<h:inputText id="assunto" value="#{ forumMensagem.obj.titulo }" style="width: 95%;" />
	</li>
	<li>
		<label for="mensagem" class="required">Mensagem <span class="required">&nbsp;</span></label>
		<h:inputTextarea id="mensagem" value="#{ forumMensagem.obj.conteudo }"/>
	</li>
	<li>
		<label for="email">Enviar notificação por e-mail?</label>
		<p class="descricao-campo">(Selecione sim se desejar enviar um e-mail para todos os participantes da turma avisando sobre a criação do Fórum.)</p>
		<t:selectOneRadio id="email" value="#{forumMensagem.notificar}" styleClass="noborder">
			<f:selectItem itemLabel="Sim" itemValue="true"/>
			<f:selectItem itemLabel="Não" itemValue="false"/>
		</t:selectOneRadio>
	</li>
	<li class="botoes">
		<input type="hidden" name="idForum" value="${ param['idForum'] }" />
		<c:if test="${ param['idTopico'] != null }">
			<input type="hidden" name="idTopico" value="${ param['idTopico'] }" />
		</c:if>
		<h:inputHidden value="#{ forumMensagem.obj.id }"/>
		<h:commandButton value="Postar" action="#{ forumMensagem.cadastrar }" /> 
		<h:commandButton value="Cancelar" action="#{ forumMensagem.cancelar }"/>
	</li>
</ul>
</fieldset>
</c:if>

<%@include file="/portais/turma/rodape.jsp"%>

</h:form>
</f:view>

<c:remove var="msgFlash" scope="session"/>
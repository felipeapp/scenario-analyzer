<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>

	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	<div class="secaoComunidade">
	
	<h2>Mensagens e Respostas</h2>
	<table class="formulario" width="80%">
	
	<div class="infoAltRem">
		<c:if test="${ !comunidadeVirtualMBean.membro.visitante }">
       		<h:graphicImage value="/img/garbage.png"/>: Remover mensagem
       	</c:if>
	</div>
	
	<caption>Mensagens e Respostas</caption>
			
	<c:forEach items="#{forumMensagemComunidadeMBean.listagem}" var="item" varStatus="loop">
		<tr style="background-color: #C4D2EB">
			<h:form>
			
			<td align="center" width="20%"><fmt:formatDate value="${ item.data }" pattern="dd/MM/yyyy HH:mm:ss"/>
				<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || item.usuario.id == forumMensagemComunidadeMBean.discenteLogado }">
					<h:commandLink action="#{ forumMensagemComunidadeMBean.removerMensagensTopicos }" styleClass="confirm-remover" title="Remover mensagem"
						onclick="return(confirm('Deseja remover essa mensagem?'));">
						<f:param name="id" value="#{ item.id }"/>
						<f:param name="topico" value="#{ item.topico == null ? 'true' : 'false' }"/>
						<h:graphicImage value="/ava/img/bin.png"/>
					</h:commandLink>
				</c:if>
			</td>
			</h:form>
			<td>
				<strong> <b> ${ item.titulo } </b> </strong>
			</td>
		</tr>
		
		<%-- Segunda Linha: Foto e mensagem --%>
		<tr>
			<td align="center" style="background-color: #EFF3FA;">
			<c:if test="${ not empty item.usuario.idFoto }">
				<img src="${ctx}/verFoto?idFoto=${item.usuario.idFoto}&key=${ sf:generateArquivoKey(item.usuario.idFoto) }" width="60" height="80"/>
			</c:if>
			<c:if test="${ empty item.usuario.idFoto }">
				<img src="${ctx}/img/no_picture.png" width="60" height="80"/>
			</c:if>
			<br/>
			${ item.usuario.pessoa.nome }  
			</td>
			<td colspan="${ turmaVirtual.docente ? "3" : "2" }" valign="top"  style="background-color: #FFFFFF"> ${item.conteudo}
			</td>
		</tr>
	</c:forEach>
	</table>
	<h:form>
	<table class="formulario" width="80%">
	<tr> 
		<td colspan="2" class="subFormulario" align="center"> 
			<center>
				
				<h:commandLink action="#{forumMensagemComunidadeMBean.pageFirst}" rendered="#{forumMensagemComunidadeMBean.firstRow == 0 || forumMensagemComunidadeMBean.rowsPerPage >= forumMensagemComunidadeMBean.totalRows}" disabled="true">
					<h:graphicImage value="/img/primeira_des.gif" alt="Primeira" />
				</h:commandLink>
			                 
				<h:commandLink action="#{forumMensagemComunidadeMBean.pagePrevious}" rendered="#{forumMensagemComunidadeMBean.firstRow == 0 || forumMensagemComunidadeMBean.rowsPerPage >= forumMensagemComunidadeMBean.totalRows}" disabled="true">
					<h:graphicImage value="/img/voltar_des.gif" alt="Voltar" />
				</h:commandLink>
				
				<h:commandLink action="#{forumMensagemComunidadeMBean.pageFirst}" rendered="#{forumMensagemComunidadeMBean.firstRow != 0}">
					<h:graphicImage value="/img/primeira.gif" alt="Primeira" />
				</h:commandLink>
			                 
				<h:commandLink action="#{forumMensagemComunidadeMBean.pagePrevious}" rendered="#{forumMensagemComunidadeMBean.firstRow != 0 && forumMensagemComunidadeMBean.rowsPerPage <= forumMensagemComunidadeMBean.totalRows }">
					<h:graphicImage value="/img/voltar.gif" alt="Voltar" />
				</h:commandLink>
				   
				<h:commandLink action="#{forumMensagemComunidadeMBean.pageNext}" rendered="#{forumMensagemComunidadeMBean.firstRow + forumMensagemComunidadeMBean.rowsPerPage < forumMensagemComunidadeMBean.totalRows}">
					<h:graphicImage value="/img/avancar.gif" alt="Avançar" />
				</h:commandLink>
				    
				<h:commandLink action="#{forumMensagemComunidadeMBean.pageLast}" rendered="#{forumMensagemComunidadeMBean.firstRow + forumMensagemComunidadeMBean.rowsPerPage < forumMensagemComunidadeMBean.totalRows}">
					<h:graphicImage value="/img/ultima.gif" alt="Última" />
				</h:commandLink>
				
				<h:commandLink action="#{forumMensagemComunidadeMBean.pageNext}" rendered="#{forumMensagemComunidadeMBean.firstRow + forumMensagemComunidadeMBean.rowsPerPage >= forumMensagemComunidadeMBean.totalRows}" disabled="true">
					<h:graphicImage value="/img/avancar_des.gif" alt="Avançar" />
				</h:commandLink>
				    
				<h:commandLink action="#{forumMensagemComunidadeMBean.pageLast}" rendered="#{forumMensagemComunidadeMBean.firstRow + forumMensagemComunidadeMBean.rowsPerPage >= forumMensagemComunidadeMBean.totalRows}" disabled="true">
					<h:graphicImage value="/img/ultima_des.gif" alt="Última" />
				</h:commandLink>
				
			</center>
			
			<center>
				<h:outputText value="Página #{forumMensagemComunidadeMBean.currentPage} / #{forumMensagemComunidadeMBean.totalPages}" />
			</center>	
			
			<c:if test="${ comunidadeVirtualMBean.membro.visitante == false}">	
				<input type="hidden" name="idForumMensagem" value="${param.idForumMensagem}"/>						

					<tr>
					<th><h:outputLabel for="descricao">Título:</h:outputLabel></th>
						<td>
							<h:inputText id="titulo" size="89" maxlength="69" value="#{forumMensagemComunidadeMBean.object.titulo }"/>
						</td>
					</tr>
					
					<tr>
					<th class="required"><h:outputLabel for="descricao">Mensagem:</h:outputLabel></th>
						<td>
							<t:inputTextarea value="#{forumMensagemComunidadeMBean.object.conteudo}" rows="15" cols="100" />
						</td>
					</tr>
					<tr>
						<td style="text-align: right;"> <h:selectBooleanCheckbox id="notificacao" value="#{ forumMensagemComunidadeMBean.notificar }" styleClass="noborder" /> </td>
						<th style="text-align: left;">
						Notificar por e-mail?
						<ufrn:help>
							Todos os participantes da comunidade serão notificados por e-mail.
						</ufrn:help>
						</th>
					</tr>
			</c:if>
			</td>
		</tr>
		
		<tfoot>
			<tr> 
				<td colspan="2"> 
				<c:if test="${ !comunidadeVirtualMBean.membro.visitante }">
					<h:commandButton value="Postar Mensagem" action="#{ forumMensagemComunidadeMBean.cadastrarResposta }" />
				</c:if>
					<h:commandButton action="#{forumMensagemComunidadeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true"/> 
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>	

	</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>	
<%@include file="/cv/include/rodape.jsp" %>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
	tinyMCE.init({
		mode : "textareas", theme : "advanced", width: "90%", height: "250", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});
</script>

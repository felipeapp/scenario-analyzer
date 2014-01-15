<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<a4j:keepAlive beanName="forumMensagem" />
<%@include file="/ava/menu.jsp" %>
<!-- <h:outputText value="#{ forumMensagem.create }"/> -->

<h:messages showDetail="true" />

<fieldset>
	<legend>Mensagens e Respostas</legend>
	
<ul class="show">
	
</ul>

	<c:if test="${forumMensagem.listagem != null}">
		<table style="border: 1px solid #C4D2EB; width: 99%" cellpadding="3">
		<h:form>
		<c:forEach items="#{forumMensagem.listagem}" var="item" varStatus="loop">
			<tr style="background-color: #C4D2EB">
				<td align="center" width="20%">
						<fmt:formatDate value="${ item.data }" pattern="dd/MM/yyyy HH:mm:ss"/>
						<h:commandLink action="#{ forumMensagem.remover }" styleClass="confirm-remover" 
										title="Remover Mensagem"
										rendered="#{turmaVirtual.docente || item.usuario.id == usuario.id}"
										onclick="return(confirm('Deseja remover essa mensagem?'));">
							<f:param name="id" value="#{ item.id }"/>
							<f:param name="topico" value="#{ item.topico == null ? 'true' : 'false' }"/>
							<h:graphicImage value="/ava/img/bin.png"/>
						</h:commandLink>
				</td>
				<td>
					<strong>${item.titulo}</strong>
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
					${item.usuario.pessoa.nome}  
				</td>
				<td colspan="${ turmaVirtual.docente ? "3" : "2" }" valign="top"  style="background-color: #FCFCFC;">
					${sf:escapeHtml(item.conteudo)}
				</td>
			</tr>
		</c:forEach>
		</h:form>
		
		</table>	
	
		<h:form id="paginacaoForm">
			<center>
				<h:commandLink action="#{forumMensagem.pageFirst}" disabled="#{forumMensagem.firstRow == 0}">
					<h:graphicImage value='/img/primeira#{forumMensagem.firstRow == 0 ? "_des" : ""}.gif' alt="Primeira" />
				</h:commandLink>
		                  
		              <h:commandLink action="#{forumMensagem.pagePrevious}" disabled="#{forumMensagem.firstRow == 0}">
		              	<h:graphicImage value='/img/voltar#{forumMensagem.firstRow == 0 ? "_des" : ""}.gif' alt="Voltar" />
		              </h:commandLink>
		                 
		              <h:commandLink action="#{forumMensagem.pageNext}" disabled="#{forumMensagem.firstRow + forumMensagem.rowsPerPage >= forumMensagem.totalRows}">
		              	<h:graphicImage value='/img/avancar#{(forumMensagem.firstRow + forumMensagem.rowsPerPage) >= forumMensagem.totalRows ? "_des" : ""}.gif' alt="Avançar" />
		             	</h:commandLink>
		                  
		              <h:commandLink action="#{forumMensagem.pageLast}" disabled="#{forumMensagem.firstRow + forumMensagem.rowsPerPage >= forumMensagem.totalRows}">
		              	<h:graphicImage value='/img/ultima#{(forumMensagem.firstRow + forumMensagem.rowsPerPage) >= forumMensagem.totalRows ? "_des" : ""}.gif' alt="Última" />
		              </h:commandLink>
			</center>
			
			<center>
				<h:outputText value="Página #{forumMensagem.currentPage} / #{forumMensagem.totalPages}" />
			</center>			
		</h:form>
	</c:if>
</fieldset>

<p>

<h:form>
<input type="hidden" name="idForumMensagem" value="${param.idForumMensagem}"/>						
<fieldset>
<legend>Postar no Tópico</legend>
<ul class="form">

	<li>
		<label for="titulo">Titulo: <span class="required">&nbsp;</span></label>
		<h:inputText id="titulo" value="#{ forumMensagem.object.titulo }" size="64" maxlength="200"/>
	</li>
	<li> 
		<table><tr><th style="width:130px;vertical-align:middle;"><label for="mensagem"><strong>Mensagem:</strong><span class="required">&nbsp;</span></label>
		</th><td>
		<h:inputTextarea id="mensagem" value="#{ forumMensagem.object.conteudo }" cols="63" rows="10" />
		</td></tr></table> 
	</li>
	
</ul>

<div class="botoes">
		<div class="form-actions">
			<h:commandButton value="Postar mensagem" action="#{ forumMensagem.responderTopicoForum }" /> 
		</div>
		<div class="other-actions">
			<h:commandButton value="<< Voltar" action="#{ forumMensagem.cancelar }"/>
		</div>
		<div class="required-items">
			<span class="required">&nbsp;</span>
			Itens de Preenchimento Obrigatório. Devem existir ao menos duas respostas.
		</div>
	</div>
<br/>
</fieldset>

</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>

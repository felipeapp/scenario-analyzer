<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="forumMensagem" />
	<a4j:keepAlive beanName="listagemCursosForumDocenteMBean"></a4j:keepAlive>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h:form>

		<h2> <ufrn:subSistema /> > Discuss�o sobre ${forumMensagem.tituloCursoDiscutidoAtualmente} </h2>	
	
	<c:if test="${empty forumMensagem.listaForumMensagens}">
		<i><center>N�o possui mensagens.</center></i>
	</c:if>
	
	<c:if test="${not empty forumMensagem.listaForumMensagens}">

		
		<div class="infoAltRem">
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:Remover Mensagem
			<h:graphicImage value="/img/despublicar.png" style="overflow: visible;" />:Denunciar Mensagem
			<h:graphicImage value="/img/portal_turma/site_add.png" alt="Download" style="overflow: visible;" />: Download
		</div>

		<!--  <table style="border: 1px solid #C4D2EB; width: 99%" cellpadding="3">-->
		<table class="formulario" width="100%">

		
			
			<c:forEach items="#{forumMensagem.listaForumMensagens}" var="item" varStatus="loop">

				<tr style="background-color: #C4D2EB">	
					
					<td align="center" width="20%"><fmt:formatDate value="${ item.data }" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						
					<td><strong>Postado por:</strong> 
				  	${ item.usuario.pessoa.nome }
					<h:outputText value=" - "/> 	
					<c:forEach items="#{item.matriculas}" var="matricula" varStatus="loopMatricula">
						${ matricula }<c:if test="${!loopMatricula.last}">,</c:if>
					</c:forEach>

					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO } %>">
						<h:commandLink value="Emitir Hist�rico" action="#{forumMensagem.emitirHistoricoAluno}" id="emitirHistorico" 
							rendered="#{not empty item.usuario.discente}">
							<f:param name="matricula" value="#{item.matriculas[0]}"/>
						</h:commandLink>
					</ufrn:checkRole> 
					
					</td>
							
					<td align="right">
						<c:if test="${ item.idArquivo != null }"> 
							<h:outputLink value="/sigaa/verProducao?idProducao=#{item.idArquivo}&key=#{ sf:generateArquivoKey(item.idArquivo) }" 
								target="_blank" title="Download">
								<h:graphicImage value="/img/portal_turma/site_add.png" alt="Download" /> 
							</h:outputLink>
						</c:if>
						&nbsp;
						
						<h:commandLink action="#{ forumMensagem.denunciarMensagensTopicos }" styleClass="noborder" title="Denunciar Mensagem" id="denMensagensTopico"
								rendered="#{ item.usuario.id != forumMensagem.discenteLogado}">
								<h:graphicImage url="/img/despublicar.png" />
								<f:param name="idForumMensagem" value="#{ item.id }"/>
								<f:param name="topico" value="#{ item.topico == null ? 'true' : 'false' }"/>
						</h:commandLink>

						<c:if test="${not loop.first}">
							<h:commandLink action="#{ forumMensagem.removerMensagensTopicos }" title="Remover Mensagem" id="remMensagensTopico"
								onclick="return(confirm('Deseja realmente excluir este item?'));" 
								rendered="#{ forumMensagem.coordenadorDoCursoEscolhido || forumMensagem.gestorForumCurso || item.usuario.id == forumMensagem.discenteLogado}">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />
								<f:param name="id" value="#{ item.id }"/>
								<f:param name="topico" value="#{ item.topico == null ? 'true' : 'false' }"/>
							</h:commandLink>
						</c:if>
					</td>
	
				</tr>

				<%-- Segunda Linha: Foto e mensagem --%>
				<tr>
					<td align="center" style="background-color: #EFF3FA;"><c:if
						test="${ not empty item.usuario.idFoto }">
						<img src="${ctx}/verFoto?idFoto=${item.usuario.idFoto}&key=${ sf:generateArquivoKey(item.usuario.idFoto) }" width="60"
							height="80" />
					</c:if> <c:if test="${ empty item.usuario.idFoto }">
						<img src="${ctx}/img/no_picture.png" width="60" height="80" />
					</c:if> 
					</td>
					<td colspan="${ turmaVirtual.docente ? " 3" : "2" }" valign="top" style="background-color: #FCFCFC;">
						<ufrn:format type="texto" valor="${item.conteudo}"/> 
					</td>
				</tr>
			</c:forEach>
			
		</table>
			<center>
				<h:commandLink action="#{forumMensagem.pageFirst}" disabled="#{forumMensagem.firstRow == 0}" id="pageFirst">
					<h:graphicImage value='/img/primeira#{forumMensagem.firstRow == 0 ? "_des" : ""}.gif' alt="Primeira" />
				</h:commandLink>
                    
                <h:commandLink action="#{forumMensagem.pagePrevious}" disabled="#{forumMensagem.firstRow == 0}" id="pagePrevious">
                	<h:graphicImage value='/img/voltar#{forumMensagem.firstRow == 0 ? "_des" : ""}.gif' alt="Voltar" />
                </h:commandLink>
                   
                <h:commandLink action="#{forumMensagem.pageNext}" disabled="#{forumMensagem.firstRow + forumMensagem.rowsPerPage >= forumMensagem.totalRows}" id="pageNext">
                	<h:graphicImage value='/img/avancar#{(forumMensagem.firstRow + forumMensagem.rowsPerPage) >= forumMensagem.totalRows ? "_des" : ""}.gif' alt="Avan�ar" />
               	</h:commandLink>
                    
                <h:commandLink action="#{forumMensagem.pageLast}" disabled="#{forumMensagem.firstRow + forumMensagem.rowsPerPage >= forumMensagem.totalRows}" id="pageLast">
                	<h:graphicImage value='/img/ultima#{(forumMensagem.firstRow + forumMensagem.rowsPerPage) >= forumMensagem.totalRows ? "_des" : ""}.gif' alt="�ltima" />
                </h:commandLink>
			</center>
			
			<center>
				<h:outputText value="P�gina #{forumMensagem.currentPage} / #{forumMensagem.totalPages}" />
			</center>
		<br/>
		
		<table class="subFormulario" width="100%">
			<caption>Responder T�pico</caption>
			<tr>
				<td align="center">	
					<input type="hidden" name="idForumMensagem" value="${param.idForumMensagem}"/>
					<h:inputTextarea id="mensagem"value="#{ forumMensagem.object.conteudo }" cols="100" rows="10"  rendered="#{forumMensagem.usuarioAtivo}"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="Postar Mensagem" action="#{ forumMensagem.responderTopicoForumCursos }" rendered="#{forumMensagem.usuarioAtivo}" id="postarmensagens"/> 
						<h:commandButton value="<< Voltar" action="#{ forumMensagem.voltarParaForumMensagensCursos }" id="voltarMsg"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
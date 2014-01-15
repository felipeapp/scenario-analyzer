<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="forumMensagemMedio" />
	<a4j:keepAlive beanName="listagemCursosForumDocenteMBean"></a4j:keepAlive>
	
	<h:form>

		<h2> <ufrn:subSistema /> > Discussão sobre ${forumMensagemMedio.tituloTurmaSerieDiscutidaAtualmente} </h2>	
	
	<c:if test="${empty forumMensagemMedio.listaForumMensagens}">
		<i><center>Não possui mensagens.</center></i>
	</c:if>
	
	<c:if test="${not empty forumMensagemMedio.listaForumMensagens}">

		
		<div class="infoAltRem">
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:Remover Mensagem
		</div>

		<!--  <table style="border: 1px solid #C4D2EB; width: 99%" cellpadding="3">-->
		<table class="formulario" width="100%">

		
			
			<c:forEach items="#{forumMensagemMedio.listaForumMensagens}" var="item" varStatus="loop">

				<tr style="background-color: #C4D2EB">
					
					<td align="center" width="20%"><fmt:formatDate value="${ item.data }" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						
					<td><strong>Postado por:</strong> 
				  	${ item.usuario.pessoa.nome }
					<h:outputText value=" - "/> 	
					<c:forEach items="#{item.matriculas}" var="matricula" varStatus="loopMatricula">
						${ matricula }<c:if test="${!loopMatricula.last}">,</c:if>
					</c:forEach>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO } %>">
						<h:commandLink value="Emitir Histórico" action="#{forumMensagemMedio.emitirHistoricoAluno}" id="emitirHistorico" 
							rendered="#{not empty item.usuario.discente}">
							<f:param name="idDiscentePost" value="#{item.usuario.discente.id}"/>
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
						<h:commandLink action="#{ forumMensagemMedio.removerMensagensTopicos }" title="Remover Mensagem" id="remMensagensTopico"
							onclick="return(confirm('Deseja realmente excluir este item?'));" 
							rendered="#{ forumMensagemMedio.coordenadorDoCursoEscolhido || forumMensagemMedio.gestorForumCurso || item.usuario.id == forumMensagemMedio.discenteLogado}">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />
							<f:param name="id" value="#{ item.id }"/>
							<f:param name="topico" value="#{ item.topico == null ? 'true' : 'false' }"/>
						</h:commandLink>
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
				<h:commandLink action="#{forumMensagemMedio.pageFirst}" disabled="#{forumMensagemMedio.firstRow == 0}" id="pageFirst">
					<h:graphicImage value='/img/primeira#{forumMensagemMedio.firstRow == 0 ? "_des" : ""}.gif' alt="Primeira" />
				</h:commandLink>
                    
                <h:commandLink action="#{forumMensagemMedio.pagePrevious}" disabled="#{forumMensagemMedio.firstRow == 0}" id="pagePrevious">
                	<h:graphicImage value='/img/voltar#{forumMensagemMedio.firstRow == 0 ? "_des" : ""}.gif' alt="Voltar" />
                </h:commandLink>
                   
                <h:commandLink action="#{forumMensagemMedio.pageNext}" disabled="#{forumMensagemMedio.firstRow + forumMensagemMedio.rowsPerPage >= forumMensagemMedio.totalRows}" id="pageNext">
                	<h:graphicImage value='/img/avancar#{(forumMensagemMedio.firstRow + forumMensagemMedio.rowsPerPage) >= forumMensagemMedio.totalRows ? "_des" : ""}.gif' alt="Avançar" />
               	</h:commandLink>
                    
                <h:commandLink action="#{forumMensagemMedio.pageLast}" disabled="#{forumMensagemMedio.firstRow + forumMensagemMedio.rowsPerPage >= forumMensagemMedio.totalRows}" id="pageLast">
                	<h:graphicImage value='/img/ultima#{(forumMensagemMedio.firstRow + forumMensagemMedio.rowsPerPage) >= forumMensagemMedio.totalRows ? "_des" : ""}.gif' alt="Última" />
                </h:commandLink>
			</center>
			
			<center>
				<h:outputText value="Página #{forumMensagemMedio.currentPage} / #{forumMensagemMedio.totalPages}" />
			</center>
		<br/>
		
		<table class="subFormulario" width="100%">
			<caption>Responder Tópico</caption>
			<tr>
				<td align="center">	
					<input type="hidden" name="idForumMensagem" value="${param.idForumMensagem}"/>
					<h:inputTextarea id="mensagem"value="#{ forumMensagemMedio.object.conteudo }" cols="100" rows="10"  rendered="#{forumMensagemMedio.usuarioAtivo}"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="Postar Mensagem" action="#{ forumMensagemMedio.responderTopicoForumTurmaSerie }" rendered="#{forumMensagemMedio.usuarioAtivo}" id="postarmensagens"/> 
						<h:commandButton value="<< Voltar" action="#{ forumMensagemMedio.voltarParaForumMensagensTurmas }" id="voltarMsg"/>
					</td>
				</tr>
			</tfoot>
			
		</table>

</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
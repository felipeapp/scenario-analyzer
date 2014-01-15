<c:set var="atividades" value="${ turmaVirtual.ultimasAtividades }"/>
<c:set var="noticias" value="${ turmaVirtual.noticias }"/>

<div id="barraDireita" class="ui-layout-east">
	<table style="width:100%;border-bottom:1px solid #99BBE8;" cellspacing="0"><tr><td style="color:#15428B;background:url('/sigaa/ava/img/painel_bg.png');height:24px;text-align:right;font-weight:bold;font-size:8pt;padding-right:5px;" id="toggleDireita"><h:graphicImage value="/ava/img/painel_seta_dir.png" /></td></tr></table>
	<rich:simpleTogglePanel switchType="client" label="Notícias" styleClass="blocoDireita" headerClass="headerBloco">
        <f:facet name="openMarker">
			<h:graphicImage value="/ava/img/painel_seta_baixo.png"></h:graphicImage>
		</f:facet>
		<f:facet name="closeMarker">
			<h:graphicImage value="/ava/img/painel_seta_cima.png"></h:graphicImage>
		</f:facet>
		
        <c:if test="${ not empty noticias }">
			<c:forEach var="noticia" items="#{noticias}">
				<fmt:formatDate value="${noticia.data}" pattern="dd/MM/yyyy HH:mm"/><br>
				<i>${noticia.descricao } </i> <br>
				<h:form>
				<input type="hidden" name="id" value="${noticia.id}"/>
				<h:commandLink action="#{ noticiaTurma.mostrar }" value="(Visualizar)"/>
				</h:form>
				<br>
			</c:forEach>
		</c:if>			
		<c:if test="${ empty noticias }">
			<center>Não há notícias cadastradas</center>
		</c:if>
    </rich:simpleTogglePanel>
    
    <rich:simpleTogglePanel switchType="client" label="Enquete" styleClass="blocoDireita" headerClass="headerBloco">
   		<f:facet name="openMarker">
			<h:graphicImage value="/ava/img/painel_seta_baixo.png"></h:graphicImage>
		</f:facet>
		<f:facet name="closeMarker">
			<h:graphicImage value="/ava/img/painel_seta_cima.png"></h:graphicImage>
		</f:facet>
		
		${ enquete.zerarEnqueteMaisAtual }
		
        <c:if test="${ enquete.enqueteMaisAtual != null }">
        	<h:form>
				<p class="pergunta-enquete">${ enquete.enqueteMaisAtual.pergunta }</p>
				<p class="prazo-enquete" style="text-indent:5px;">Prazo para votação: <ufrn:format type="data" valor="${ enquete.enqueteMaisAtual.dataFim }" /></p>
				<ul class="enquete">
					<c:if test="${ enquete.respostaUsuarioEnqueteMaisAtual == null }" >
						<c:if test="${ not empty enquete.enqueteMaisAtual.respostas }">
							<c:forEach var="resposta" items="${ enquete.enqueteMaisAtual.respostas }">
							<li><input type="radio" name="idEnqueteResposta" id="idEnqueteResposta" value="${resposta.id}" class="noborder"/> ${ resposta.resposta }</li>
							</c:forEach>
						</c:if>
					</c:if>
					 
					<c:if test="${ enquete.respostaUsuarioEnqueteMaisAtual != null}">
						<c:if test="${ not empty enquete.estatisticaDeVotosMaisAtual }">
							<c:forEach var="item" items="${ enquete.estatisticaDeVotosMaisAtual }">
								<li ${ item.id == respostaUsuarioEnquete.id ? 'class="votado"' : '' }>${item.resposta} - <fmt:formatNumber pattern="#" value="${item.porcentagemVotos}" />% (${item.totalVotos } Voto${item.totalVotos == 1 ? "" : "s" })</li>
							</c:forEach>
						</c:if>
					</c:if>
								
				</ul>
				
				<div class="botoes-enquete">
					<c:if test="${not enquete.enqueteMaisAtual.fimPrazoVotacao}">
						<h:commandLink value="Votar" action="#{ enquete.alo }" rendered="#{ enquete.respostaUsuarioEnqueteMaisAtual == null }">
							<f:param name="id" value="#{ enquete.enqueteMaisAtual.id }"/>
						</h:commandLink> &nbsp;
					</c:if>
					
					<h:commandLink value="Ver Votos" action="#{ enquete.mostrar }" rendered="#{ turmaVirtual.docente }">
						<f:param name="id" value="#{ enquete.enqueteMaisAtual.id }"/>
					</h:commandLink>
				</div>
			</h:form>
		</c:if>
	
		<c:if test="${ enquete.enqueteMaisAtual == null }">
			<center>Nenhuma enquete encontrada</center>
		</c:if>
    </rich:simpleTogglePanel>
        
    <rich:simpleTogglePanel switchType="client" label="Atividades" styleClass="blocoDireita" headerClass="headerBloco">
    	<f:facet name="openMarker">
			<h:graphicImage value="/ava/img/painel_seta_baixo.png"></h:graphicImage>
		</f:facet>
		<f:facet name="closeMarker">
			<h:graphicImage value="/ava/img/painel_seta_cima.png"></h:graphicImage>
		</f:facet>
		
        <c:if test="${ not empty atividades }">
			<ul class="menu-direita">
				<c:forEach var="atividade" items="${ atividades }">
					<li>
						<span class="data"><fmt:formatDate pattern="dd/MM" value="${ atividade.data }"/></span>
						<span class="descricao">${ atividade.descricao } </span>
					</li>
				</c:forEach>
			</ul>
		</c:if>
		<c:if test="${ empty atividades }">
			<center><em> Nenhuma atividade registrada</em></center>
		</c:if>
    </rich:simpleTogglePanel>
    
    <rich:simpleTogglePanel switchType="client" label="Avaliações" styleClass="blocoDireita" headerClass="headerBloco">
    	<f:facet name="openMarker">
			<h:graphicImage value="/ava/img/painel_seta_baixo.png"></h:graphicImage>
		</f:facet>
		<f:facet name="closeMarker">
			<h:graphicImage value="/ava/img/painel_seta_cima.png"></h:graphicImage>
		</f:facet>
		
		<c:set var="avaliacoes" value="#{ turmaVirtual.avaliacoes }"/>
		<c:if test="${ empty avaliacoes }">
			<center>
				<em>Nenhuma avaliação cadastrada</em>
			</center>
		</c:if>
		<c:if test="${ not empty avaliacoes }">
			<ul class="menu-direita">
				<c:forEach items="#{avaliacoes}" var="item">
					<li style="text-align: center"><span class="data"><fmt:formatDate pattern="dd/MM" value="${ item.data }"/> ${item.hora}</span><br/> <span class="descricao">${item.descricao}</span> </li>
				</c:forEach>
			</ul>
		</c:if>
    </rich:simpleTogglePanel>
	
    <rich:simpleTogglePanel switchType="client" label="Mensagens dos Fóruns" styleClass="blocoDireita" headerClass="headerBloco">
        <f:facet name="openMarker">
			<h:graphicImage value="/ava/img/painel_seta_baixo.png"></h:graphicImage>
		</f:facet>
		<f:facet name="closeMarker">
			<h:graphicImage value="/ava/img/painel_seta_cima.png"></h:graphicImage>
		</f:facet>
		
        <c:if test="${ not empty turmaVirtual.ultimasMensagens }">
			<c:forEach var="mensagem" items="#{turmaVirtual.ultimasMensagens}" varStatus="loop">
				<h:form>
					<div id="mensagemForum" style="${ !loop.last ? 'border-bottom: 1px solid #C4D2EB;margin-bottom: 10px' : '' }">
						<span class="data"><fmt:formatDate pattern="dd/MM" value="${ mensagem.data }"/></span>  
						<h:commandLink action="#{ forumMensagemBean.view }">
							${ mensagem.titulo }
							<f:setPropertyActionListener value="#{ mensagem.mensagemPai }" target="#{ forumMensagemBean.obj }" />
						</h:commandLink>
						<br/>
						<span class="data"> Enviada por: </span> <span style="font-size: 0.8em">${ mensagem.usuario.nome }</span> 
						<br/><br/>
						<i>${mensagem.conteudo }</i>
						<br/>
						<h:commandLink action="#{ forumMensagemBean.view }">
							leia mais...
							<f:setPropertyActionListener value="#{ mensagem.mensagemPai }" target="#{ forumMensagemBean.obj }" />
						</h:commandLink>
						<br/><br/>
					</div>
				</h:form>
			</c:forEach>
		</c:if>			
		<c:if test="${ empty turmaVirtual.ultimasMensagens }">
			<center>Não há mensagens cadastradas</center>
		</c:if>
    </rich:simpleTogglePanel>

</div>

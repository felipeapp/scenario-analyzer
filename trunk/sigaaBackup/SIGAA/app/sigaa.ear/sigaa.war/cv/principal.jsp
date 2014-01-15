<%@include file="/cv/include/cabecalho.jsp"%>

<f:view>

<%@include file="/cv/include/_menu_comunidade.jsp" %>
<%@include file="/cv/include/_info_comunidade.jsp" %>


<h:form>

<%-- Tópicos --%>
<div id="topicos">

<div class="colNoticiaEnquete">
	<rich:panel header="NOTÍCIAS" styleClass="noticia" headerClass="headerBloco">
		
		<c:set var="noticias" value="#{noticiaComunidadeMBean.listagem}"/>
		
		<c:forEach items="#{ noticias }" var="item" varStatus="loop" end="0">
			<h:commandLink action="#{ noticiaComunidadeMBean.mostrar }"> 
				<h:outputText value="#{ item.descricao }"/>
				<f:param name="id" value="#{ item.id }"/>
			</h:commandLink> 
		</c:forEach>
		
		<p align="center">
			<br/>
			<h:commandLink action="#{ noticiaComunidadeMBean.listar }" value="Ver mais notícias..."/>
		</p>
	</rich:panel>
	
	<rich:panel header="CONTEÚDOS PERMANENTES" headerClass="headerBloco">
		
		<c:set var="documentos" value="#{conteudoComunidadeMBean.listaPermanentes}"/>
		
		<c:forEach items="#{ documentos }" var="item" varStatus="loop" end="0">
			<h:commandLink action="#{ conteudoComunidadeMBean.mostrar }"> 
				<h:outputText value="#{ item.titulo }"/>
				<f:param name="id" value="#{ item.id }"/>
			</h:commandLink> 
		</c:forEach>
		
		<p align="center">
			<br/>
			<h:commandLink action="#{ conteudoComunidadeMBean.listar }" value="Ver mais conteúdos..."/>
		</p>
	</rich:panel>
	
	<rich:panel header="ÚLTIMAS ENQUETES" styleClass="enquete" headerClass="headerBloco">
		
		<c:set var="enquetes" value="#{ enqueteComunidadeMBean.listagem }" />
		 
		<c:forEach var="a" items="#{ enquetes }" varStatus="loop" end="2">
			
			<p class="enquetePergunta"> &rsaquo;
			${ a.pergunta }
			<c:if test="${ !comunidadeVirtualMBean.membro.visitante }">
				<c:if test="${a.publicada}">
					<h:commandLink action="#{ enqueteComunidadeMBean.telaVotacao }">
						<f:param name="id" value="#{ a.id }" />
						<h:graphicImage styleClass="enqueteVotar" width="12px" value="/cv/img/accept.png" alt="Votar" title="Votar" />
					</h:commandLink>
				</c:if>
			</c:if><br/>
			</p>
			
			<!--
			<a href="#" mce_href="#" onClick="window.open('${ctx}/cv/EnqueteComunidade/votar.jsf?id=${a.id}&ajaxRequest=true','popup','width=600,height=350')">Abrir</a>
			-->
			
		</c:forEach>
		
		<p align="center">
			<br/>
			<h:commandLink action="#{ enqueteComunidadeMBean.listar }" value="Ver todas as enquetes..."/><br/>
		</p>
	</rich:panel>
</div>

<div class="colComunidadeForum">

<rich:panel header="TÓPICOS DA COMUNIDADE" style="position:relative;width:auto;" headerClass="headerBloco">

	<c:set var="limiteTopicos" value="3"/>
	
	<c:forEach var="_topico" items="#{ topicoComunidadeMBean.topicos }" varStatus="loop" end="${limiteTopicos}" >
	
	<div class="topicoComunidade" style="padding:10px;margin-left: ${ 10 + (20*_topico.nivel)}px;background:rgb(<h:outputText value='#{_topico.cor }' />)">
		<h3>  
		<h:outputText value="#{ _topico.descricao }" />
				
		<fmt:formatDate value="${ _topico.dataCadastro}" pattern="dd/MM/yyyy"/> 
		
			<h:commandLink id="idExibirTopico" action="#{ topicoComunidadeMBean.exibirTopico }" 
				rendered="#{ !_topico.visivel && (comunidadeVirtualMBean.membro.permitidoModerar || _topico.usuario.id == comunidadeVirtualMBean.usuarioLogado.id) }">
				<f:param name="id" value="#{ _topico.id }" />
				<h:graphicImage value="/img/show.gif" title="Tornar tópico visível para membros" alt="Tornar tópico visível para membros" />
			</h:commandLink>
				
			<h:commandLink id="idEsconderTopico" style="position:relative;" action="#{ topicoComunidadeMBean.esconderTopico }"
				 rendered="#{ _topico.visivel && (comunidadeVirtualMBean.membro.permitidoModerar || _topico.usuario.id == comunidadeVirtualMBean.usuarioLogado.id) }">
				<f:param name="id" value="#{ _topico.id }" />
				<h:graphicImage value="/img/hide.gif" title="Esconder tópico para membros" alt="Esconder tópico para membros" />
			</h:commandLink>
		
			<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar }">
				<small>
					<i>
						<h:commandLink action="#{ topicoComunidadeMBean.editar }" title="Editar">
							<f:param name="id" value="#{ _topico.id }" />
						</h:commandLink>
						<h:commandLink action="#{ topicoComunidadeMBean.remover }" title="Remover"
							onclick="return(confirm('Deseja realmente excluir este item?'));">
							<f:param name="id" value="#{ _topico.id }" />
						</h:commandLink>
					</i>
				</small>
			</c:if>
		</h3>

	<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar }">
		<div class="topico-acoes" style="text-align: right;">
			<h:selectOneMenu id="opcaoComboTopico" valueChangeListener="#{ topicoComunidadeMBean.acaoTopico }" styleClass="#{ _topico.id }" onchange="submit()">
				<f:selectItem itemLabel="-- Selecione uma ação --" itemValue="0"/>
				<f:selectItem itemLabel="Adicionar Arquivo" itemValue="1"/>
				<f:selectItem itemLabel="Cadastrar Sub-Tópico" itemValue="2"/>
				<f:selectItem itemLabel="Cadastrar Referência (Site, Livro, ..)" itemValue="3"/>
				<f:selectItem itemLabel="Cadastrar Conteúdo" itemValue="5"/>
				<f:selectItem itemLabel="Editar Tópico" itemValue="6"/>
			</h:selectOneMenu>
		</div>
	</c:if>
			
		<p class="descricao-aula">
			${ _topico.conteudo }
		</p>
		
		<c:if test="${ not empty _topico.materiais }">
			<ul class="materiais">
			<c:forEach var="material" items="#{ _topico.materiais }">
			<li>
				<c:choose>
					<c:when test="${ material.tipoConteudo }">
						<h:commandLink action="#{ conteudoComunidadeMBean.mostrar }" title="Visualizar">
							<h:graphicImage value="#{ material.icone }"/>
							<h:outputText value=" #{ material.nome }"/>
							<f:param name="id" value="#{ material.id }"/>
						</h:commandLink>
						
						<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar }">&nbsp;
							<h:commandLink action="#{ conteudoComunidadeMBean.editar }" title="Editar Conteúdo">
									<h:graphicImage value="/img/alterar.gif"/>
									<f:param name="id" value="#{ material.id }"/>
									<f:param name="paginaOrigem" value="portalPrincipal"/>  
							</h:commandLink>
						
							<h:commandLink action="#{ conteudoComunidadeMBean.remover }" title="Remover Conteúdo" onclick="#{confirmDelete}">
								<h:graphicImage value="/img/porta_arquivos/delete.gif"/>
								<f:param name="id" value="#{ material.id }"/>
							</h:commandLink>
						</c:if>
					</c:when>				
				
					<c:when test="${ material.tipoArquivo }">
						 
						 <c:if test="${ comunidadeVirtualMBean.membro.visitante}">
						 	<a href="${ctx}/" target="_blank" 
						 		title="${material.descricao}" onclick="alert('Atenção: essa opção está disponível apenas para membros da comunidade.');">
						 	<img src="${ ctx }/${ material.icone }" title="${material.descricao}"> ${ material.nome }</a>
						 </c:if>
						 
						 <c:if test="${ not comunidadeVirtualMBean.membro.visitante}">
						 	<a href="${ctx}/verProducao?idProducao=${ material.arquivo.idArquivo }&&key=${ sf:generateArquivoKey(material.arquivo.idArquivo) }" target="_blank" 
						 		title="${material.descricao}"><img src="${ ctx }/${ material.icone }" title="${material.descricao}"> ${ material.nome }</a>
						 </c:if>
						 
						 <c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar }">&nbsp;
							<h:commandLink action="#{ arquivoUsuarioCVMBean.removerAssociacaoArquivo }"  
							onclick="#{confirmDelete}" title="Remover arquivo">
								<h:graphicImage value="/img/porta_arquivos/delete.gif"/>
								<f:param name="id" value="#{ material.id }"/>
							</h:commandLink>
						</c:if>
					</c:when>

					<c:when test="${ material.tipoIndicacao }">
						<h:commandLink action="#{ indicacaoReferenciaComunidadeMBean.mostrar }" rendered="#{!material.site}" title="Visualizar">
							<h:graphicImage value="#{ material.icone }"/>
							<h:outputText value=" #{ material.nome }"/>
							<f:param name="id" value="#{ material.id }"/>
						</h:commandLink>
						
						<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar }">&nbsp;
							<h:commandLink action="#{ indicacaoReferenciaComunidadeMBean.editar }" title="Editar Indicação Referência">
									<h:graphicImage value="/img/alterar.gif"/>
									<f:param name="id" value="#{ material.id }"/>
									<f:param name="paginaOrigem" value="portalPrincipal"/>  
							</h:commandLink>
						
							<h:commandLink action="#{ indicacaoReferenciaComunidadeMBean.remover }" title="Remover Indicação Referência" onclick="#{confirmDelete}">
								<h:graphicImage value="/img/porta_arquivos/delete.gif"/>
								<f:param name="id" value="#{ material.id }"/>
							</h:commandLink>
						</c:if>
						
						<c:if test="${material.site}">
							<a href="${material.url}" target="_blank">
								<img src="/sigaa/img/portal_turma/site_add.png">
								<h:outputText value=" #{ material.nome }"/>
							</a>
						</c:if>
						<i><small>
						(${material.tipoDesc})
						</small></i>
					</c:when>
					
				</c:choose>
			</li>
			</c:forEach>
			</ul>
		</c:if>
	</div>	
</c:forEach>
		
		<br/>
		<p align="center">
			<h:commandLink action="#{ topicoComunidadeMBean.listarAllTopicosConteudos}" value="Ver todos os tópicos...">
				<f:param name="id" value="#{ comunidadeVirtualMBean.mural.id }"/> 
			</h:commandLink>
		</p>
		
</rich:panel>
	

<rich:panel header="FÓRUM" style="position:relative;width:auto;" headerClass="headerBloco">
	<c:set var="foruns" value="#{comunidadeVirtualMBean.topicosComunidadeAtual}"/>
	<c:if test="${ empty foruns }">
		<p class="vazio">Não há mensagens cadastradas para o fórum</p>
	</c:if>
	<c:if test="${ not empty foruns }">
	<table class="listagem">
	<thead>
		<tr>
			<th>Título</th>
			<th>Respostas</th>
			<th>Criador</th>
			<td style="text-align: center;">Última Postagem</th>
			<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || n.usuario.id == comunidadeVirtualMBean.usuarioLogado.id }">
			<th></th>
			</c:if>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="n" items="#{ foruns }" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">

			<c:if test="${loop.index < 5}">
			
					<td class="first90">
						<h:commandLink
							action="#{ forumMensagemComunidadeMBean.mostrar }" title="Visualizar mensagem">
							<h:outputText value="#{ n.titulo }" />
							<f:param name="idForumMensagem" value="#{ n.id }" />
							<f:param name="id" value="#{ n.forum.id }" />
						</h:commandLink>
					</td>
					
					<td class="width90">${ n.respostas }</td>
					<td>${ n.usuario.pessoa.nome }</td>
					
					<td style="text-align: center;">
						<c:if test="${ n.ultimaPostagem != null }">
							<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ n.ultimaPostagem }" />
						</c:if> <c:if test="${ n.ultimaPostagem == null }">
							<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ n.data }" />
						</c:if>
					</td>
	
					<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar || n.usuario.id == comunidadeVirtualMBean.usuarioLogado.id }">
						<td class="icon"><h:commandLink
								action="#{ forumMensagemComunidadeMBean.remover }"
								styleClass="confirm-remover"
								onclick="return(confirm('Se excluir este tópico TODAS as mensagens que ele possui também serão removidas. Tem certeza?'));">
								<f:param name="topico" value="true" />
								<f:param name="id" value="#{ n.id }" />
								<h:graphicImage value="/ava/img/bin.png" alt="Remover" title="Remover" />
							</h:commandLink>
						</td>
					</c:if>
				
					<c:if test="${ !comunidadeVirtualMBean.membro.permitidoModerar || n.usuario.id != comunidadeVirtualMBean.usuarioLogado.id }">
						<td class="icon">
						</td>
					</c:if>
				
			</c:if>

				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<br/>
	<p align="center">
		<h:commandLink action="#{ forumMensagemComunidadeMBean.listar }" value="Ver todas as mensagens...">
			<f:param name="id" value="#{ comunidadeVirtualMBean.mural.id }"/> 
		</h:commandLink>
	</p>
		
	</c:if>
	</rich:panel>		

</div>
	
</div>
   
</h:form>

</f:view>
<%@include file="/cv/include/rodape.jsp"%>
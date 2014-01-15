<h2>
	Material da Comunidade Virtual
</h2>

<div class="topico-aula">
	<div class="colComunidadeForum">
	
	<c:forEach var="_topico" items="#{ consultaPublicaTurmas.comunidadeVirtual.topicos }">
	
	<div class="topicoComunidade" style="margin-left: ${ 10 + (20*_topico.nivel)}px;">
		
		<h3>  
			${ _topico.descricao }
			<fmt:formatDate value="${ _topico.dataCadastro}" pattern="dd/MM/yyyy"/> 
		</h3>

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
					</c:when>				
				
					<c:when test="${ material.tipoArquivo }">
						 <c:if test="${ not comunidadeVirtualMBean.membro.visitante}">
						 	<a href="${ctx}/verProducao?idProducao=${ material.arquivo.idArquivo }&&key=${ sf:generateArquivoKey(material.arquivo.idArquivo) }" target="_blank" 
						 		title="${material.descricao}"><img src="${ ctx }/${ material.icone }" title="${material.descricao}"> ${ material.nome }</a>
						 </c:if>
						 
					</c:when>

					<c:when test="${ material.tipoIndicacao }">
						<h:commandLink action="#{ indicacaoReferenciaComunidadeMBean.mostrar }" rendered="#{!material.site}" title="Visualizar">
							<h:graphicImage value="#{ material.icone }"/>
							<h:outputText value=" #{ material.nome }"/>
							<f:param name="id" value="#{ material.id }"/>
						</h:commandLink>
						
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
	
	<br/><br/>
		
</c:forEach>

	</div>
</div>
	
	<br clear="all"/>
	<center>	
	<h:commandLink value="<< Voltar para a Busca" action="#{consultaPublicaTurmas.buscarCursosAbertos}" >
	</h:commandLink>
	</center>
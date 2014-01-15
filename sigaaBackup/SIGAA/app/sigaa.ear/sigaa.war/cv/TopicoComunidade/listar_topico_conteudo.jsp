<%@include file="/cv/include/cabecalho.jsp"%>
<f:view>
	
	<%@include file="/cv/include/_menu_comunidade.jsp" %>
	<%@include file="/cv/include/_info_comunidade.jsp" %>
	
	<div class="secaoComunidade" id="topicos">

	<rich:panel header="TÓPICOS DA COMUNIDADE" style="position:relative;width:auto;" >
	<h:form>

	<c:forEach var="_topico" items="#{ topicoComunidadeMBean.topicos }" varStatus="loop">
	
	<div class="topicoComunidade" style="margin-left: ${ 10 + (20*_topico.nivel)}px;padding:10px;background:rgb(<h:outputText value='#{_topico.cor }' />)">
		<h3>  
		${ _topico.descricao }
		
		<fmt:formatDate value="${ _topico.dataCadastro}" pattern="dd/MM/yyyy"/> 
			<c:if test="${ comunidadeVirtualMBean.membro.permitidoModerar }">
				<small>
					<i>
						<h:commandLink action="#{ topicoComunidadeMBean.editar }">
							<f:param name="id" value="#{ _topico.id }" />
						</h:commandLink>
						<h:commandLink action="#{ topicoComunidadeMBean.remover }"
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
						<h:commandLink action="#{ conteudoComunidadeMBean.mostrar }">
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
						<h:commandLink action="#{ indicacaoReferenciaComunidadeMBean.mostrar }" rendered="#{!material.site}">
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
		
</h:form>
</rich:panel>
</div>

</f:view>
		
<%@include file="/cv/include/rodape.jsp" %>
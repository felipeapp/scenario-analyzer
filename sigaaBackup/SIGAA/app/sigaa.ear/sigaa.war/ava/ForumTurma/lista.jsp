<%@include file="/ava/cabecalho.jsp"%>

<f:view>
	<%@include file="/ava/menu.jsp"%>
	
<a4j:keepAlive beanName="forumTurmaBean" />
<fieldset>		
    <legend>Fóruns da Turma </legend>						 
	<h:form id="form">
		<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || permissaoAva.permissaoUsuario.forum || turmaVirtual.config.permiteAlunoCriarForum}">
			<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
				<ul class="menu-interno">
					<li class="botao-medio novoForum;">
						<h:commandLink action="#{ forumTurmaBean.novoForumTurma }" rendered="#{ turmaVirtual.docente  || permissaoAva.permissaoUsuario.docente || permissaoAva.permissaoUsuario.forum || turmaVirtual.config.permiteAlunoCriarForum}">
							<p style="margin-left:28px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Fórum</p> 
						</h:commandLink>
					</li>
				</ul>	
				<div style="clear:both;"></div>			
			</div>
		</c:if>
		<br/>
	
			<c:set var="foruns" value="#{ forumTurmaBean.forunsTurma }" />
	
			<c:if test="${ empty foruns }">
				<p class="empty-listing">Nenhum fórum foi encontrado.</p>
			</c:if>
			 
			<c:if test="${ not empty foruns }">
				<table class="listing">					
					<thead>
						<tr>
							<th>Título</th>							
							<th>Tipo</th>
							<th>Tópicos</th>
							<th>Autor(a)</th>
							<th>Criado em</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="ft" items="#{ foruns }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">	
								<td class="first">
									<h:commandLink action="#{ forumBean.view }" id="btnViewForum">
										<h:outputText value="#{ ft.forum.titulo }" />
										<f:setPropertyActionListener target="#{ forumBean.obj.id }" value="#{ ft.forum.id }" />
									</h:commandLink>
									<br/>
									<h:outputText value="<b>Aula: </b><i>" escape="false" rendered="#{not empty ft.topicoAula.descricao}" />
									<h:outputText value="#{ ft.topicoAula.descricao }" rendered="#{not empty ft.topicoAula.descricao}" />
									<h:outputText value="</i> <br/>" escape="false" rendered="#{not empty ft.topicoAula.descricao}" />
									<ufrn:format type="texto" length="15" valor="${ft.forum.descricaoSemFormatacao}"/>
								</td>

								<td class="width150">
										<h:outputText value="#{ ft.forum.tipo.descricao }" />
								</td>
	
								<td class="width150">
									<h:commandLink action="#{ forumBean.view }" id="btnViewTopicos">
										<h:outputText value="#{ ft.forum.totalTopicos }" />
										<f:setPropertyActionListener target="#{ forumBean.obj.id }" value="#{ ft.forum.id }" />
									</h:commandLink>
								</td>
	
								<td class="width150">
									${ ft.forum.usuario.pessoa.nome }
								</td>
								
								<td class="width90">
									<fmt:formatDate pattern="dd/MM/yyyy" value="${ ft.forum.dataCadastro }" />
								</td>
	
								<td class="icon">
									<h:commandLink action="#{ forumTurmaBean.preRemover }" id="btnPreRemoverForum"
										rendered="#{ forumTurmaBean.usuarioLogado.id == ft.forum.usuario.id }" title="Remover Fórum">										
										<f:setPropertyActionListener value="#{ ft }" target="#{ forumTurmaBean.obj }"/>
										<h:graphicImage value="/ava/img/bin.png" />
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			
	</h:form>
  </fieldset>
</f:view>
<%@include file="/ava/rodape.jsp"%>


<%@include file="/ava/cabecalho.jsp" %>

<f:view>

	<style>
		.botao-medio {
				margin-bottom:0px !important;
				height:60px !important;
		}
	</style>
	
	<%@include file="/ava/menu.jsp"%>
	<h:form>

	<c:set var="topicos" value="#{ topicoAula.listagem }" />
	<fieldset>
		<legend>Conteúdo Programado</legend> 
				
			<div class="menu-botoes" style="text-align:center;width:720px;margin: 0 auto;">
				<ul class="menu-interno">
					<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
						<li class="botao-medio novoTopico;">
							<h:commandLink action="#{ topicoAula.novo }"> 
								<p style="margin-left:20px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Criar Tópico de Aula</p> 
							</h:commandLink>
						</li>
						<li class="botao-medio gerenciaLote;">	
							<h:commandLink action="#{ topicoAula.iniciarGerenciaEmLote }"> 
								<p style="margin-left:40px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Gerenciar Todos</p> 
							</h:commandLink>
						</li>
					</c:if>
					<li class="botao-medio gantt">
						<h:commandLink action="#{ topicoAula.gantt }">
							<p style="margin-left:5px;font-variant:small-caps;font-size:1.3em;font-size:1.3em;font-weight:bold;" >Cronog. Gráfico de Aulas</p> 
						</h:commandLink>
					</li>
				</ul>
				
				<div style="clear:both;"></div>
			</div>
	
		<c:if test="${ empty topicos }">
			<p class="empty-listing">Nenhum item foi encontrado</p>
		</c:if> 

		<c:if test="${ not empty topicos }">
		
		<div class="infoAltRem">
			<img src="${ctx}/ava/img/zoom.png"/>: Visualizar
			<img src="${ctx}/img/show.gif"/>: Exibir tópico
			<img src="${ctx}/img/hide.gif"/>: Esconder tópico
			<img src="${ctx}/ava/img/page_edit.png"/>: Alterar	
			<img src="${ctx}/ava/img/bin.png"/>: Remover
		</div>
			<table class="listing">
				<thead>
					<tr>
						<th><p align="left">Descrição</p></th>
						<th>Início</th>
						<th>Fim</th>
						<th></th>
						<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
							<th></th>
							<th></th>
							<th></th>
						</c:if>
					</tr>
				</thead>
				<tbody>
					<c:set var="indice" value="0" />
					
					<c:forEach var="t" items="#{ topicos }" varStatus="loop">
						
						<%-- Se não for docente e não estiver visível, não exibe --%>
						<a4j:outputPanel rendered="#{turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || t.visivel}">
							<c:set var="indice" value="#{ indice + 1 }" />
							
							<tr class='<h:outputText value="#{ indice % 2 == 0 ? 'even' : 'odd' }" />' style='background:rgb(<h:outputText value="#{t.aulaCancelada ? '255,255,153': t.cor}" />)'>
								<td class="first"><h:outputText value="#{ t.descricao }" /></td>
								<td class="width75"><fmt:formatDate value="${ t.data }" pattern="dd/MM/yyyy" /></td>
								<td class="width75"><fmt:formatDate value="${ t.fim }" pattern="dd/MM/yyyy" /></td>
								<td class="icon">
									<h:commandLink title="Visualizar" action="#{ topicoAula.mostrar }">
										<f:param name="id" value="#{ t.id }" />
										<h:graphicImage value="/ava/img/zoom.png" />
									</h:commandLink>
								</td>
								
								<a4j:outputPanel rendered="#{turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}">
									<td class="icon">
										<h:commandLink title="Esconder Tópico" action="#{ topicoAula.esconderTopicoDiscente }" rendered="#{t.visivel}">
											<f:param name="id" value="#{ t.id }" />
											<h:graphicImage value="/img/hide.gif" />
										</h:commandLink>
										
										<h:commandLink title="Exibir Tópico" action="#{ topicoAula.exibirTopicoDiscente }" rendered="#{!t.visivel}">
											<f:param name="id" value="#{ t.id }" />
											<h:graphicImage value="/img/show.gif" />
										</h:commandLink>
									</td>
									
									<td class="icon">
										<h:commandLink title="Alterar" action="#{ topicoAula.editar }">
											<f:param name="id" value="#{ t.id }" />
											<h:graphicImage value="/ava/img/page_edit.png" />
										</h:commandLink>
									</td>
									<td class="icon">
										<h:commandLink title="Remover" action="#{ topicoAula.remover }" onclick="return(confirm('Deseja realmente excluir este item?'));">
											<f:param name="id" value="#{ t.id }" />
											<h:graphicImage value="/ava/img/bin.png" />
										</h:commandLink>
									</td>
								</a4j:outputPanel>
							</tr>
						</a4j:outputPanel>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</fieldset>
</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>
<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="videoTurma" />

	<style>
		.botao-medio {
				margin-bottom:0px !important;
				height:60px !important;
		}
	</style>

	<%@include file="/ava/menu.jsp" %>
	<h:form id="formAva">
		<fieldset>
			<legend>Vídeos</legend>
			
			<c:if test="${turmaVirtual.docente }">
				<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
					<ul class="menu-interno">
							<li class="botao-medio novaTarefa;">
								<h:commandLink action="#{ videoTurma.novoVideo }">
									<p style="margin-left:0px;text-indent:42px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Vídeo</p> 
								</h:commandLink>
							</li>
					</ul>	
					<div style="clear:both;"></div>	
				</div>
			</c:if>
			
			<c:if test="${ fn:length(videoTurma.videos) > 0 }">
				<div class="infoAltRem" style="width:80%;">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Vídeo
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Vídeo
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Vídeo
				</div>
				
				<table class="listing" style="width:80%;">
					<thead>
						<tr>
							<th style="text-align:left;">Título</th>
							<th style="width:20px!important;" width="20">&nbsp;</th>
							<th style="width:20px!important;" width="20">&nbsp;</th>
							<th style="width:20px!important;" width="20">&nbsp;</th>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach items="#{videoTurma.videos}" var="q" varStatus="s">
							<tr class='linha${ s.index % 2 == 0 ? "P" : "Imp" }ar'>
								<td class="first" style="text-align:left;">${ q.titulo }</td>
								<td style="width:20px!important;" width="20">
									<h:commandLink action="#{ videoTurma.exibir }" target="_blank">
										<f:param name="id" value="#{ q.id }" />
										<h:graphicImage value="/img/view.gif" title="Visualizar Vídeo" />
									</h:commandLink>
								</td>
								<td style="width:20px!important;" width="20">
									<h:commandLink action="#{ videoTurma.alterarVideo }" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
										<f:param name="idVideo" value="#{ q.id }" />
										<h:graphicImage value="/img/alterar.gif" title="Alterar Vídeo" />
									</h:commandLink>
								</td>
								<td style="width:20px!important;" width="20">
									<h:commandLink action="#{ videoTurma.remover }" onclick="#{confirmDelete}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
										<f:param name="id" value="#{ q.id }" />
										<h:graphicImage value="/img/delete.gif" title="Remover Vídeo" />
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			<c:if test="${fn:length(videoTurma.videos) == 0 }">
				<p class="empty-listing">Nenhum item foi encontrado.</p>
			</c:if>
			
		</fieldset>
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
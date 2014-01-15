<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="videoTurma" />

	<style>
		.botao-medio {
			margin-bottom:0px !important;
			height:60px !important;
		}
		.listing div {
			background-color: inherit !important;
		}
	</style>

	<%@include file="/ava/menu.jsp" %>
	<h:form id="formAva">
		<fieldset>
			<legend>Vídeos</legend>
			
			<c:if test="${ fn:length(videoTurma.videos) > 0 }">
				
				<div class="infoAltRem" style="width:80%;">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar vídeo
				</div>
				
				<table class="listing" style="width:80%;">
					<thead>
						<tr>
							<th style="text-align:left;" width="30%">Título</th>
							<th style="text-align:left;">Descrição</th>
							<th style="width:20px!important;" width="20">&nbsp;</th>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach items="#{videoTurma.videos}" var="q" varStatus="s">
							<tr class='${ s.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
								<td style="text-align:left; border-left: 1px solid black;">${ q.titulo }</td>
								<td style="text-align:left;">${ q.descricao }</td>
								<td style="width:20px!important;" width="20">
									<h:commandLink action="#{ videoTurma.exibir }" target="_blank">
										<f:param name="id" value="#{ q.id }" />
										<h:graphicImage value="/img/view.gif" title="Visualizar Vídeo" />
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
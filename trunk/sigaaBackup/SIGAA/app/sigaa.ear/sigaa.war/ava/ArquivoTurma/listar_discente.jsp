<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="arquivoUsuario" />
	
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
			<legend>Arquivos</legend>
			
			<div class="descricaoOperacao">
				<p>Caro(a) discente,</p>
										
				<p> Esta é a listagem de todos os arquivos publicados pelo docente nos tópicos de aula. Nela é possível baixar um arquivo de forma individual clicando no botão "Baixar Arquivo" ou baixar todos os arquivos no link "Baixar Todos os Arquivos".</p>
			</div>
			
			
			
			
			<c:if test="${ fn:length(arquivoUsuario.arquivosTurma) > 0 }">
				<div class="infoAltRem" style="width:80%;">
					<c:if test="${ fn:length(arquivoUsuario.arquivosTurma) > 0 }">	
							<h:commandLink action="#{ arquivoUsuario.baixarAllArquivosTurma }" target="_blank" style="">
								Baixar todos os arquivos
							</h:commandLink>
		  			</c:if>
					<h:graphicImage value="/ava/img/page_white_put.png" style="overflow: visible;" />: Baixar Arquivo
				</div>
				<table class="listing" style="width:80%;">
					<thead>
						<tr>
							<th style="text-align:left;" width="30%">Título</th>
							<th style="text-align:left;">Descrição</th>
							<th style="text-align:left;">Tópico de Aula</th>
							<th style="width:20px!important;" width="20">&nbsp;</th>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach var="arquivo" items="#{arquivoUsuario.arquivosTurma}" varStatus="status">
							<tr class='${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }'>
								<td style="text-align:left; border-left: 1px solid black;">${ arquivo.nome }</td>
								<td style="text-align:left;">${ arquivo.descricao }</td>
								<td style="text-align:left;">${ arquivo.aula.descricao }</td>
								<td style="width:20px!important;" width="20">
									<h:commandLink action="#{ arquivoUsuario.baixarArquivoPortalPrincipal }" target="_blank">
										<f:param name="id" value="#{ arquivo.arquivo.idArquivo }"/>
										<h:graphicImage value="/ava/img/page_white_put.png" title="Baixar Arquivo" />
									</h:commandLink>
								</td>
							</tr>
							
						</c:forEach>
						 
					</tbody>
				</table>
			</c:if>
			<c:if test="${fn:length(arquivoUsuario.arquivosTurma) == 0 }">
				<p class="empty-listing">Nenhum item foi encontrado.</p>
			</c:if>
			
		</fieldset>
	
	</h:form>
</f:view>


<%@include file="/ava/rodape.jsp" %>
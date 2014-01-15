<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.colData{
		text-align: center !important;
		width: 8%;
	}
</style>

<c:set var="confirmDelete"
	value="return confirm('Tem certeza que deseja remover este arquivo?');"
	scope="request" />
<f:view>
	<c:if test="${documentoSite.portalDocente}"> 
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>
	<c:if test="${documentoSite.portalCoordenadorStricto}">
		<%@include file="/stricto/menu_coordenador.jsp"%>
	</c:if>
	<c:if test="${documentoSite.portalCoordenadorGraduacao}">
		<%@include file="/graduacao/menu_coordenador.jsp"%>
	</c:if>
	
	<h2>
		 <ufrn:subSistema />
		 <h:outputText escape="false" rendered="#{not empty documentoSite.curso}" value="&gt; #{documentoSite.curso.descricao}" />
		 &gt; Lista de Documentos
	</h2>

	<h:outputText value="#{documentoSite.create}" />

	<div class="descricaoOperacao">
		<p>Caro Usuário(a),</p>
		<br />
		<p>
			Na listagem são exibidos todos os documentos cadastrados no portal público.
		</p>
	</div>
	
	<h:form>
	<div class="infoAltRem">
	   <h:commandLink action="#{documentoSite.preCadastrar}" >
		   <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />Cadastrar
	   </h:commandLink>
	   <h:graphicImage value="../../../shared/img/icones/download.png" style="overflow: visible;" />: Download do Arquivo
	   <h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar
       <h:graphicImage value="/img/delete.gif"	style="overflow: visible;" />: Remover <br />
	</div>

		<table class="listagem" style="width: 100%">
			<caption>Lista de Documentos (${fn:length(documentoSite.resultadosBusca)})</caption>
			<thead>
				<tr>
					<th>Nome</th>
					<th>Categoria</th>
					<th class="colData">Data</th>
					<th colspan="3" width="1%"></th>
				</tr>
			</thead>
			<c:set var="documentosProg" value="#{documentoSite.resultadosBusca}" />
			<c:choose>

				<c:when test="${not empty documentosProg}">
					<c:forEach items="#{documentosProg}" var="doc" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td width="50%">${doc.nome}</td>
							<td width="20%">
								<c:if test="${not empty doc.tipoDocumentoSite}">
								${doc.tipoDocumentoSite.nome}
								</c:if>
							</td>
							<td class="colData">
							<ufrn:format type="data" valor="${doc.dataCadastro}"/>
							</td>
							<td><a href="/sigaa/verProducao?idProducao=${doc.idArquivo}&key=${ sf:generateArquivoKey(doc.idArquivo) }"
								target="_blank" title="Download do Arquivo">
								 <h:graphicImage value="../../../shared/img/icones/download.png" title="Download do Arquivo"
								  style="overflow: visible;" />
								</a>
							</td>

							<td>
							<h:commandLink styleClass="noborder" title="Alterar"
								action="#{documentoSite.atualizar}">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="id" value="#{doc.id}" />
							</h:commandLink>
							</td>
							
							<td>
							<h:commandLink styleClass="noborder" title="Remover"
								action="#{documentoSite.remover}"
								onclick="#{confirmDelete}" immediate="true">
								<h:graphicImage url="/img/delete.gif" />
								<f:param name="id" value="#{doc.id}" />
							</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</c:when>

				<c:otherwise>
					<tr>
						<td colspan="10" align="center">
						<i>	Nenhum documento cadastrado até o momento</i>
						</td>
					</tr>
				</c:otherwise>

			</c:choose>

			<tfoot>
				<tr>
					<td colspan="10" align="center"><h:commandButton value="<< Voltar"
						action="#{detalhesSite.listarCursosTecnico}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
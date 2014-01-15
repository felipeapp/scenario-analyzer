<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>

	<a4j:keepAlive beanName="tipoDocumentoNormalizacaoCatalogacaoMBean" />

	<h2><ufrn:subSistema /> &gt; Tipos de Documentos de Normalização e Catalogação na Fonte</h2>
	
	<div class="descricaoOperacao">
		<p>Abaixo estão listados todos os Tipos de Documentos utilizados na Normalização e Catalogação na Fonte </p>
	</div>
	
	<h:form>

		<div class="infoAltRem" style="width:80%;">
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{tipoDocumentoNormalizacaoCatalogacaoMBean.preCadastrar}" value="Novo Tipo Documento" />
			
			
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Tipo de Documento
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Tipo de Documento
			
			</ufrn:checkRole>
		</div> 

		<table class="formulario" width="80%">
			<caption class="listagem">Lista de Tipos de Documentos (${tipoDocumentoNormalizacaoCatalogacaoMBean.size})</caption>
			<thead>
				<tr>
					<th>Descrição</th>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<th width="20"></th>
						<th width="20"></th>
					</ufrn:checkRole>
				</tr>
			</thead>
			
			<c:forEach items="#{tipoDocumentoNormalizacaoCatalogacaoMBean.all}" var="tipoDocumento" varStatus="status">
				
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
					<td>${tipoDocumento.denominacao}</td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<td>
							<h:commandLink action="#{tipoDocumentoNormalizacaoCatalogacaoMBean.preAtualizar}" rendered="#{tipoDocumento.editavel}" 
								title="Alterar Tipo Documento">
								<f:param name="id" value="#{tipoDocumento.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Tipo de Documento" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Remover Tipo de Documento"
								action="#{tipoDocumentoNormalizacaoCatalogacaoMBean.remover}" rendered="#{tipoDocumento.editavel}"
								onclick="return confirm('Confirma a remoção do Tipo de Documento ? ');">
								<f:param name="id" value="#{tipoDocumento.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Tipo de Documento" />
							</h:commandLink>
						</td>
					</ufrn:checkRole>
					
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{tipoDocumentoNormalizacaoCatalogacaoMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
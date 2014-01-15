<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>
	<h2><ufrn:subSistema /> > Formas de Documento</h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo estão listadas as Formas de Documento existentes no sistema. </p>
    	<p> <strong>Uma Forma de Documento é uma informação opcional utilizada para discriminar materiais que possuem o conteúdo diferente do meio físico no qual eles estão contidos. </strong> </p> 
    	<p>
    	Por exemplo um livro que esteja em um meio físico digital, um CD. Então o Tipo de Material deve ser cadastrado de acordo com o meio físico, neste exemplo "CD", e a Forma do Documento deve indicar o conteúdo, nesse caso "Livro". 
    	</p>
    	<p> Não podem existir duas os mais Formas de Documento com a mesma descrição no sistema.</p>
		<br/>
	</div>
	
	<h:form>

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
			<div class="infoAltRem" style="width:80%;">
	
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{formaDocumentoMBean.preCadastrar}" value="Nova Forma de Documento" />
	
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Forma de Documento
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Forma de Documento
			</div>
		</ufrn:checkRole>

		<%-- Para não ficar buscando várias vezes no banco --%>
		<c:set var="listaformaDocumento" value="${formaDocumentoMBean.all}" scope="request" />



		<table class="listagem" style="width:80%;">
			<caption>Lista de Formas de Documento(${fn:length(listaformaDocumento)})</caption>
			<thead>
				<tr>
					<th style="width: 90%;">Descrição</th>
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<th style="width: 5%;"></th>
						<th style="width: 5%;"></th>
					</ufrn:checkRole>
				</tr>
			</thead>
			
			<c:forEach items="#{listaformaDocumento}" var="f" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${f.denominacao}</td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<td>
							<h:commandLink title="Alterar Forma de Documento" action="#{formaDocumentoMBean.atualizar}"
								style="border: 0;">
								<f:param name="id" value="#{f.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Forma de Documento" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Remover Forma de Documento" style="border: 0;"
								action="#{formaDocumentoMBean.preRemover}">
								<f:param name="id" value="#{f.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Forma de Documento" />
							</h:commandLink>
						</td>
					</ufrn:checkRole>
					
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{formaDocumentoMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
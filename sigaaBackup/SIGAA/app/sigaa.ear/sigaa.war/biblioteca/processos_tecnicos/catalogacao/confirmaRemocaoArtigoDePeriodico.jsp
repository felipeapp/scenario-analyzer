<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
%>


<style type="text/css">
	div.menu-botoes li.fasciculo-substituidor a p {
		background-image: url('/sigaa/img/refresh.png');
	}
 
</style>


<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO }  %>">

	<h2>  <ufrn:subSistema /> &gt; Remoção de Artigos de Periódicos </h2>
	
	<div class="descricaoOperacao"> 
	
		 <p> Confirme a remoção do Artigo do sistema.</p>
	
	</div>


	<f:view>

		<h:form>
	
				
				<a4j:keepAlive beanName="removerEntidadeDoAcervoMBean"></a4j:keepAlive>
	
				<a4j:keepAlive beanName="pesquisarArtigoMBean"></a4j:keepAlive>
				
				<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>
				
				<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
				<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>
	
				<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	
				<table class="visualizacao"  style="width: 70%">
				
					<caption>Informações do Artigo que será Removido</caption>
		
					<tr>
						<th style="width: 30%">Número do Sistema:</th>				
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.numeroDoSistema}" />
						</td>
					</tr>
		
					<tr>	
						<th>Título:</th>		
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.titulo}" />
						</td>
					</tr>
		
					<tr>	
						<th>Autor:</th>
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.autor}" />
						</td>
					</tr>
		
					<c:if test="${ not empty removerEntidadeDoAcervoMBean.obj.autoresSecundarios }">
						<tr>	
							<th style="vertical-align: top;">Autores Secundários:</th>
							<td>
								<c:forEach var="autor" items="#{removerEntidadeDoAcervoMBean.obj.autoresSecundariosFormatados}">
									<h:outputText value="#{autor}" /> <br/>
								</c:forEach>
							</td>
						</tr>
					</c:if>
		
					<tr>
						<th>Intervalo de Páginas:</th>
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.intervaloPaginas}" />
						</td>
					</tr>
				
					<tfoot>
						<tr>
							<td colspan="2" style="text-align: center;">
								<h:commandButton value="Remover Artigo" action="#{removerEntidadeDoAcervoMBean.removerEntidade}" 
									onclick="return confirm('Confirma remoção do Artigo ? ');"/>
								<h:commandButton value="<< Voltar" action="#{removerEntidadeDoAcervoMBean.voltarTelaArtigos}" />
								<h:commandButton value="Cancelar"  action="#{removerEntidadeDoAcervoMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
							</td>
						</tr>
					</tfoot>
					
				</table>
				
		</h:form>

	</f:view>


</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
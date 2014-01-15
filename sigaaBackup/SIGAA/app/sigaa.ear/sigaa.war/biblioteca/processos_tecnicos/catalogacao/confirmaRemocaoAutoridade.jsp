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

	<h2>  <ufrn:subSistema /> &gt; Remoção de Autoridades</h2>
	
	<div class="descricaoOperacao"> 
	
		<p> Confirme a remoção da Autoridade do sistema.</p> 		
		 
	
	</div>


	<f:view>

		<h:form>
	
				
				<a4j:keepAlive beanName="removerEntidadeDoAcervoMBean"></a4j:keepAlive>
	
				<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
				
				<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
	
				<a4j:keepAlive beanName="cooperacaoTecnicaExportacaoMBean"></a4j:keepAlive>
				
				<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	
				<table class="visualizacao"  style="width: 70%">
				
					<caption>Informações da Autoridade que será removida</caption>
		
					<tr>
						<th>Número do Sistema:</th>				
						<td>
							<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.numeroDoSistema}" />
						</td>
					</tr>
		
					<%-- Autoridade de autor --%>
					<c:if test="${removerEntidadeDoAcervoMBean.obj.entradaAutorizadaAutor != null }">
		
						<tr>	
							<th style="width: 30%">Entrada Autoridade Autor:</th>		
							<td>
								<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.entradaAutorizadaAutorComIndicacaoCampo}" />
							</td>
						</tr>
		
						<tr>
							<th style="width: 30%">Nomes Remissivos Autor:</th>
							<td></td>
						</tr>
						<c:forEach items="${removerEntidadeDoAcervoMBean.obj.nomesRemissivosAutorFormatados}" var="nomeRemissivo">
							<tr>
								<th>
								</th>
								<td>
									${nomeRemissivo}
								</td>
							</tr>
						</c:forEach>
		
						</c:if>
					
					<%-- Autoridade de assunto --%>
					<c:if test="${removerEntidadeDoAcervoMBean.obj.entradaAutorizadaAutor == null }">	
						
						<tr>	
							<th style="width: 30%">Entrada Autoridade Assunto:</th>		
							<td>
								<h:outputText value="#{removerEntidadeDoAcervoMBean.obj.entradaAutorizadaAssuntoComIndicacaoCampo}" />
							</td>
						</tr>
						
						<tr>
							<th style="width: 30%">Nomes Remissivos Assunto:</th>
							<td></td>
						</tr>
						<c:forEach items="${removerEntidadeDoAcervoMBean.obj.nomesRemissivosAssuntoFormatados}" var="nomeRemissivo">
							<tr>
								<th>
								</th>
								<td>
									${nomeRemissivo}
								</td>
							</tr>
						</c:forEach>
						
					</c:if>
				
					<tfoot>
						<tr>
							<td colspan="2" style="text-align: center;">
								<h:commandButton value="Remover Autoridade" action="#{removerEntidadeDoAcervoMBean.removerEntidade}" 
									onclick="return confirm('Confirma remoção da Autoridade ? ');"/>
								<h:commandButton value="<< Voltar" action="#{removerEntidadeDoAcervoMBean.voltarTelaAutoridades}" />
								<h:commandButton value="Cancelar"  action="#{removerEntidadeDoAcervoMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
							</td>
						</tr>
					</tfoot>
					
				</table>
				
		</h:form>

	</f:view>


</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

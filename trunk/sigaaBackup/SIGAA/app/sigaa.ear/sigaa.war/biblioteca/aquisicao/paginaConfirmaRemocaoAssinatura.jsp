<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">

	<h2>  <ufrn:subSistema /> &gt; Confirmação de Remoção da Assinatura </h2>
	
	<div class="descricaoOperacao"> 
		 <p> Caro usuário, confirme a remoção da assinatura selecionada</p> 
	</div>


	<f:view>

		<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive> <%-- utilizado para voltar a tela de busca --%>
		<a4j:keepAlive beanName="removeAssinaturaPeriodicosMBean"></a4j:keepAlive>


		<h:form>
		
			<table class="visualizacao"  style="width: 70%">
				<caption>Informações da Assinatura </caption>
				
				<tr>
					<th style="width: 25%">Código:</th>				
					<td>
						<h:outputText value="#{removeAssinaturaPeriodicosMBean.obj.codigo}" />
					</td>
				</tr>
	
				<tr>	
					<th>Título:</th>		
					<td>
						<h:outputText value="#{removeAssinaturaPeriodicosMBean.obj.titulo}" />
					</td>
				</tr>
	
				<tr>	
					<th>ISSN:</th>		
					<td>
						<h:outputText value="#{removeAssinaturaPeriodicosMBean.obj.issn}" />
					</td>
				</tr>
	
				<tr>	
					<th>Unidade de Destino:</th>		
					<td>
						<h:outputText value="#{removeAssinaturaPeriodicosMBean.obj.unidadeDestino.descricao}" />
					</td>
				</tr>
	
				<tr>
					<th>Modalidade Aquisição:</th>
					<td>
						<c:if test="${removeAssinaturaPeriodicosMBean.obj.assinaturaDeCompra}">
							COMPRA
						</c:if>
						<c:if test="${removeAssinaturaPeriodicosMBean.obj.assinaturaDeDoacao}">
							DOAÇÃO
						</c:if>
						<c:if test="${! removeAssinaturaPeriodicosMBean.obj.assinaturaDeDoacao && ! removeAssinaturaPeriodicosMBean.obj.assinaturaDeCompra}">
							INDEFINIDO
						</c:if>
					</td>
				</tr>
				
				<tr>
					<th>Internacional:</th>
					<td>
						<c:if test="${removeAssinaturaPeriodicosMBean.obj.internacional}">
							SIM
						</c:if>
						<c:if test="${ ! removeAssinaturaPeriodicosMBean.obj.internacional}">
							NÃO
						</c:if>
					</td>
				</tr>
				
				<tr>
					<th>Criada por:</th>
					<td>
						<h:outputText value="#{removeAssinaturaPeriodicosMBean.obj.nomeCriador}" />
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="2" style="text-align: center;">
							<h:commandButton value="Remover Assinatura" action="#{removeAssinaturaPeriodicosMBean.removeAssinatura}" 
								onclick="return confirm('Confirma a remoção da assinatura ? ');"/>
							<h:commandButton value="Cancelar"  action="#{assinaturaPeriodicoMBean.telaBuscaAssinaturas}" onclick="#{confirm}" immediate="true"/>
						</td>
					</tr>
				</tfoot>
					
			</table>		

		</h:form>

	</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS }  %>">

	<h2>  <ufrn:subSistema /> &gt; Substituir Exemplar </h2>
	
	<div class="descricaoOperacao"> 
	
		<p> Confirme a substitui��o do exemplar. </p>
		<p> <strong> O exemplar quer for substitu�do ser� baixado do acervo. </strong> </p>
	</div>


	<f:view>

		<a4j:keepAlive beanName="substituirExemplarMBean"></a4j:keepAlive>
	
		<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>   <%-- Caso o usu�rio utilize o bot�o voltar --%>

		<h:form  id="formConfirmaSubstituicaoExemplar">
	
	
				<div class="infoAltRem" style="width:70%;">
					<h:graphicImage url="/img/buscar.gif" style="border:none"/>
					<h:commandLink id="cmdLinkProcurarExemplarSubstituido" value="Alterar Exemplar que vai ser Substitu�do" action="#{substituirExemplarMBean.pesquisaExemplarSubstituido}"/> 
				</div> 
	
				<table class=formulario width="70%">
				
					<caption>Informa��es do Exemplar que vai ser Substitu�do</caption>
		
					<tr>
						<th>Numero Patrim�nio:</th>
						<td>
							<h:outputText value="#{substituirExemplarMBean.obj.numeroPatrimonio}" />
						</td>
					</tr>
		
					<tr>
						<th>C�digo de Barras:</th>
						<td>
							<h:outputText value="#{substituirExemplarMBean.obj.codigoBarras}" />
						</td>
					</tr>
		
					<tr>
						<th>Biblioteca:</th>
						<td>
							<h:outputText value="#{substituirExemplarMBean.obj.biblioteca.descricao}" />
						</td>
					</tr>
					
					<tr>
						<th>Cole��o:</th>
						<td>
							<h:outputText value="#{substituirExemplarMBean.obj.colecao.descricao}" />
						</td>
					</tr>
					
					
					<tr>
						<th>Situa��o:</th>
						<td>
							<h:outputText value="#{substituirExemplarMBean.obj.situacao.descricao}" />
						</td>
					</tr>
					
					<tr>
						<th>Status:</th>
						<td>
							<h:outputText value="#{substituirExemplarMBean.obj.status.descricao}" />
						</td>
					</tr>
		
					<tr>
						<th>Tipo Material:</th>
						<td>
							<h:outputText value="#{substituirExemplarMBean.obj.tipoMaterial.descricao}" />
						</td>
					</tr>
					
				</table>
				
				<c:if test="${substituirExemplarMBean.exemplarSubstituidor != null}">
					<div class="infoAltRem" style="width:70%; margin-top: 20px;">
						<h:graphicImage url="/img/buscar.gif" style="border:none"/>
						<h:commandLink id="cmdLinkAlteraExemplarSubstituidor" value="Alterar Exemplar que ir� Substituir" action="#{substituirExemplarMBean.pesquisaExemplarSubstituidor}"/> 
					</div> 
				</c:if>
				
				<c:if test="${substituirExemplarMBean.exemplarSubstituidor == null}">
					<div class="menu-botoes" style="width:35%; margin-top: 20px; margin-left: auto; margin-right: auto; text-align: center;" >
						<ul class="menu-interno">
							<li class="botao-grande fasciculo-substituidor">
								<h:commandLink id="cmdLinkProcurarExemplarSubstituidor" action="#{substituirExemplarMBean.pesquisaExemplarSubstituidor}">
								  <h5>Procurar Exemplar para Substituir</h5> 
								  <p>
									Procurar o exemplar que ir� substituir o exemplar selecionado anteriormente.
								  </p>
								</h:commandLink>	
					 
							</li>
						</ul>
					</div>
				</c:if>
				
				<c:if test="${substituirExemplarMBean.exemplarSubstituidor != null}">
				
					<table class=formulario width="70%">
					
						<caption>Informa��es do Exemplar que vai Substituir o Exemplar Acima </caption>
			
						<tr>
							<th>Numero Patrim�nio:</th>
							<td>
								<h:outputText value="#{substituirExemplarMBean.exemplarSubstituidor.numeroPatrimonio}" />
							</td>
						</tr>
			
						<tr>
							<th>C�digo de Barras:</th>
							<td>
								<h:outputText value="#{substituirExemplarMBean.exemplarSubstituidor.codigoBarras}" />
							</td>
						</tr>
			
						<tr>
							<th>Biblioteca:</th>
							<td>
								<h:outputText value="#{substituirExemplarMBean.exemplarSubstituidor.biblioteca.descricao}" />
							</td>
						</tr>
						
						<tr>
							<th>Cole��o:</th>
							<td>
								<h:outputText value="#{substituirExemplarMBean.exemplarSubstituidor.colecao.descricao}" />
							</td>
						</tr>
						
						
						<tr>
							<th>Situa��o:</th>
							<td>
								<h:outputText value="#{substituirExemplarMBean.exemplarSubstituidor.situacao.descricao}" />
							</td>
						</tr>
						
						<tr>
							<th>Status:</th>
							<td>
								<h:outputText value="#{substituirExemplarMBean.exemplarSubstituidor.status.descricao}" />
							</td>
						</tr>
			
						<tr>
							<th>Tipo Material:</th>
							<td>
								<h:outputText value="#{substituirExemplarMBean.exemplarSubstituidor.tipoMaterial.descricao}" />
							</td>
						</tr>
						
					</table>
				</c:if>
				
				<table class=formulario width="70%">
					<tfoot>
						<tr>
							<td colspan="2">
								<h:commandButton id="cmdButtonConfirmaSubstituicaoExemplar" value="Substituir" action="#{substituirExemplarMBean.substituirExemplar}" 
									disabled="#{substituirExemplarMBean.exemplarSubstituidor == null}" 
									onclick="return confirm('Confirma substitui��o exemplar ? ');"/>
								<h:commandButton id="cancelarSubstituicao" value="Cancelar" action="#{substituirExemplarMBean.cancelar}" immediate="true" onclick="#{confirm}" />
							</td>
						</tr>
					</tfoot>
				</table>
				
				
		</h:form>

	</f:view>


</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
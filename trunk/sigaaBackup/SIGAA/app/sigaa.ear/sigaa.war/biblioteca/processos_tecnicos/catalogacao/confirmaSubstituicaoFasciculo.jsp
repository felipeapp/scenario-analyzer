<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
%>


<style type="text/css">
	div.menu-botoes li.fasciculo-substituidor a p {
		background-image: url('/sigaa/img/refresh.png');
	}
 
</style>


<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS }  %>">

	<h2>  <ufrn:subSistema /> &gt; Substituir Fasc�culo </h2>
	
	<div class="descricaoOperacao"> 
	
		 <p> Confirme a substitui��o do fasc�culo.</p> 
		 <p> <strong> Observa��o: O fasc�culo quer for substitu�do deixar� de aparecer nas consultas. </strong> </p>
	</div>


	<f:view>

		<h:form id="formConfirmaSubstituicaoFasciculo">
	
				
				<a4j:keepAlive beanName="substituirFasciculoMBean"></a4j:keepAlive>
	
				<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>  <%-- Caso o usu�rio utilize o bot�o voltar --%>
				
	
				<div class="infoAltRem" style="width:70%;">
					<h:graphicImage url="/img/buscar.gif" style="border:none"/>
					<h:commandLink id="cmdLinkProcurarFasciculoSubstituido" value="Alterar Fasc�culo que vai ser Substitu�do" action="#{substituirFasciculoMBean.escollheFasciculoSubstituido}"/> 
				</div> 
	
				<table class="visualizacao"  style="width: 70%">
				
					<caption>Informa��es do Fasc�culo que ser� Substitu�do</caption>
		
					<tr>
						<th>C�digo de Barras:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.codigoBarras}" />
						</td>
					</tr>
		
					<tr>
						<th>Assinatura:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.assinatura.codigo}" /> - <h:outputText value="#{substituirFasciculoMBean.obj.assinatura.titulo}" />
						</td>
					</tr>
		
					<tr>
						<th>Ano Cronol�gico:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.anoCronologico}" />
						</td>
					</tr>
		
					<tr>
						<th>Ano:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.ano}" />
						</td>
					</tr>
		
					<tr>
						<th>Volume:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.volume}" />
						</td>
					</tr>
					
					<tr>
						<th>N�mero:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.numero}" />
						</td>
					</tr>
					
					
					<tr>
						<th>Edi��o:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.edicao}" />
						</td>
					</tr>
					
					<tr>
						<th>Biblioteca:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.biblioteca.descricao}" />
						</td>
					</tr>
					
					<tr>
						<th>Cole��o:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.colecao.descricao}" />
						</td>
					</tr>
					
					
					<tr>
						<th>Situa��o:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.situacao.descricao}" />
						</td>
					</tr>
					
					<tr>
						<th>Status:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.status.descricao}" />
						</td>
					</tr>
		
					<tr>
						<th>Tipo Material:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.obj.tipoMaterial.descricao}" />
						</td>
					</tr>
					
				</table>
				
				<c:if test="${substituirFasciculoMBean.fasciculoSubstituidor != null}">
					<div class="infoAltRem" style="width:70%; margin-top: 20px;">
						<h:graphicImage url="/img/buscar.gif" style="border:none"/>
						<h:commandLink id="cmdLinkAlterarFasciculoSubstituidor" value="Alterar Fasc�culo que ir� Substituir" action="#{substituirFasciculoMBean.escollheFasciculoSubstituidor}"/> 
					</div> 
				</c:if>
				
				<c:if test="${substituirFasciculoMBean.fasciculoSubstituidor == null}">
					<div class="menu-botoes" style="width:35%; margin-top: 20px; margin-left: auto; margin-right: auto; text-align: center;" >
						<ul class="menu-interno">
							<li class="botao-grande fasciculo-substituidor">
								<h:commandLink id="cmdLinkProcurarFasciculoSubstituidor" action="#{substituirFasciculoMBean.escollheFasciculoSubstituidor}">
								  <h5>Procurar Fasc�culo para Substituir</h5> 
								  <p>
									Procurar o fasc�culo que ir� substituir o fasc�culo selecionado anteriormente.
								  </p>
								</h:commandLink>	
					 
							</li>
						</ul>
					</div>
				</c:if>
				
				
				<c:if test="${substituirFasciculoMBean.fasciculoSubstituidor != null}">
				
					<table class="visualizacao" style="width: 70%">
					
						<caption>Informa��es do Fasc�culo Substituir� o Fasc�culo Acima </caption>
			
						<tr>
						<th>C�digo de Barras:</th>
							<td>
								<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.codigoBarras}" />
							</td>
						</tr>
			
						<tr>
							<th>Assinatura:</th>
							<td>
								<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.assinatura.codigo}" /> - <h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.assinatura.titulo}" />
							</td>
						</tr>
			
						<tr>
							<th>Ano Cronol�gico:</th>
							<td>
								<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.anoCronologico}" />
							</td>
						</tr>
			
						<tr>
							<th>Ano:</th>
							<td>
								<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.ano}" />
							</td>
						</tr>
			
						<tr>
							<th>Volume:</th>
							<td>
								<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.volume}" />
							</td>
						</tr>
						
						<tr>
							<th>N�mero:</th>
							<td>
								<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.numero}" />
							</td>
						</tr>
						
						
						<tr>
							<th>Edi��o:</th>
							<td>
								<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.edicao}" />
							</td>
						</tr>
						
						<tr>
							<th>Biblioteca:</th>
							<td>
								<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.biblioteca.descricao}" />
							</td>
						</tr>
						
						<tr>
						<th>Cole��o:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.colecao.descricao}" />
						</td>
					</tr>
					
					
					<tr>
						<th>Situa��o:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.situacao.descricao}" />
						</td>
					</tr>
					
					<tr>
						<th>Status:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.status.descricao}" />
						</td>
					</tr>
		
					<tr>
						<th>Tipo Material:</th>
						<td>
							<h:outputText value="#{substituirFasciculoMBean.fasciculoSubstituidor.tipoMaterial.descricao}" />
						</td>
					</tr>
						
					</table>
				</c:if>
				
				<table class=formulario width="70%">
					<tfoot>
						<tr>
							<td colspan="2">
								<h:commandButton id="cmdButtonConfirmaSubstituicaoFasciculo" value="Substituir" action="#{substituirFasciculoMBean.substituirFasciculo}" 
									disabled="#{substituirFasciculoMBean.fasciculoSubstituidor == null}" 
									onclick="return confirm('Confirma substitui��o Fasc�culo ? ');"/>
								<h:commandButton id="cancelarSubstituicao" value="Cancelar" action="#{substituirFasciculoMBean.cancelar}" onclick="#{confirm}" immediate="true" />
							</td>
						</tr>
					</tfoot>
				</table>
				
				
		</h:form>

	</f:view>


</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
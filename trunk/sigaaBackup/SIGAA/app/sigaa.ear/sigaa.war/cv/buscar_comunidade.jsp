<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p" %>

<script type="text/javascript" src="/shared/javascript/jquery/jquery.js"></script>
<script>
	var YahooEvent = YAHOO.util.Event;
	//YAHOO.util.Event = null;
</script>

<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>
<script type="text/javascript" src="/sigaa/javascript/graduacao/busca_discente.js"> </script>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>

<f:view>
	<p:resources />
	<link rel="stylesheet" type="text/css" href="/sigaa/ava/primefaces/redmond/skin.css" />
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Buscar Comunidades Virtuais</h2>
	
	<h:form id="formulario">
	
	
		<table class="formulario" width="70%">
			<caption>Buscar Comunidade</caption>
			<tbody>
				<tr>
					<th nowrap="nowrap" width="30%">
						<h:outputLabel for="nomeComunidade" value="Nome da comunidade:"/> 
					</th>
					<td>
						<h:inputText value="#{ buscarComunidadeVirtualMBean.nomeComunidade }" id="nomeComunidade" style="width: 95%" />
					</td>
				</tr>
				<tr>	
					<th>Tipo da comunidade:</th>
					<td> 
						<h:selectOneMenu value="#{ buscarComunidadeVirtualMBean.obj.tipoComunidadeVirtual.id }">
							<f:selectItem itemLabel="TODAS" itemValue="5"/>
							<f:selectItems value="#{ buscarComunidadeVirtualMBean.allTiposComunidades }" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{buscarComunidadeVirtualMBean.buscar}" value="Buscar" id="buscar"/>
						<h:commandButton id="cancelar" action="#{buscarComunidadeVirtualMBean.cancelar}" value="Cancelar" immediate="true" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
		
	<br>		
	<c:if test="${not empty buscarComunidadeVirtualMBean.allPaginado}">
		<br>
		<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/pesquisa/outras_bolsas.png" title="Entrar na comunidade" />: Participar dessa comunidade
				<h:graphicImage value="/img/pesquisa/indicar_bolsista.gif" title="Solicitar participação" />: Solicitar participação
				<h:graphicImage value="/img/buscar.gif" title="Visualizar comunidade" />: Visualizar comunidade mas não tornar-se membro
			</div>
		</center>
		
		<p:dialog id="dialogIngressoComunidade" modal="true" header="Solicitar cadastro em comunidade virtual" widgetVar="dialogIC" width="500">
			<h:form>
				<%--p:messages id="mensagensIngresso" showDetail="true" /--%>
				<%@include file="/WEB-INF/jsp/include/errosAjax.jsp" %>
			
				<a4j:outputPanel id="descCadastroEmComunidade">
					<h:outputText value="<div class='descricaoOperacao'><p>A comunidade <strong>#{ buscarComunidadeVirtualMBean.comunidade.nome }</strong> é moderada e seu moderador informou um código de acesso para os novos ingressantes.</p><p>Para obter acesso imediato a esta comunidade, informe abaixo o código de acesso que lhe foi informado pelo moderador da mesma. Caso não saiba o código, simplesmente deixe o campo em branco e clique em 'Solicitar Cadastro'. Dessa forma, uma solicitação será enviada ao moderador da comunidade.</p></div>" escape="false" />
				</a4j:outputPanel>
			
				<style>.label {text-align:right;font-weight:bold;width:40%;}.footer{text-align:center;}</style>
			
				<h:panelGrid columns="2" cellpadding="5" columnClasses="label,dado" style="width:100%;" footerClass="footer">
					<h:outputLabel for="username" value="Dica:" />
					<h:outputText id="dicaCodigoCadastroEmComunidade" value="#{ buscarComunidadeVirtualMBean.comunidade.dicaCodigoAcesso }" />
					
					<h:outputLabel for="codigoAcesso" value="Código de Acesso:" />
					<h:inputSecret value="#{ buscarComunidadeVirtualMBean.codigoAcesso }" id="codigoAcesso" required="false" />
					
					<f:facet name="footer">
						<a4j:outputPanel>
							<h:commandButton value="Solicitar Cadastro" action="#{ buscarComunidadeVirtualMBean.tentarIngressarComCodigo }" />
							<p:ajaxStatus style="position:absolute;bottom:15px;right:10px;">
								<f:facet name="start">
									<h:outputText value="Carregando ..." />
								</f:facet>
								<f:facet name="complete">
									<h:outputText value="" />
								</f:facet>
							</p:ajaxStatus>
						</a4j:outputPanel>
					</f:facet>
				</h:panelGrid>
			</h:form>
		</p:dialog>
		  
		<script type="text/javascript">
			function handleIngressoComunidade (xhr, status) {
				dialogIC.hide();
			}
		</script>
			
		<h:form>
			<table class="listagem">
				<caption>Comunidades localizadas</caption>
				<thead>
					<tr>
						<th>Nome da comunidade</th>
						<th>Tipo da comunidade</th>
						<th>Criada em</th>
						<th>Criada por</th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				
					<!-- ${buscarComunidadeVirtualMBean.allPaginado} -->
				
					<c:forEach items="#{buscarComunidadeVirtualMBean.allPaginado}" var="cv">
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						
							<td><h:outputText value="#{cv.nome}" /></td>
							<td><h:outputText value="#{cv.tipoComunidadeVirtual.descricao}" /></td>
							<td><fmt:formatDate value="${cv.dataCadastro}" pattern="dd/MM/yyyy" /></td>
							<td><h:outputText value="#{cv.usuario.pessoa.nome}" /></td>
								
							<td class="botaoParticiparPublica">
								<h:commandButton image="/img/pesquisa/outras_bolsas.png" rendered="#{cv.tipoComunidadeVirtual.publica}"
									alt="Participar dessa comunidade" actionListener="#{buscarComunidadeVirtualMBean.participarComunidadeVirtualPublica}" 
									title="Participar dessa comunidade">
									<f:attribute name="idComunidade" value="#{cv.id}"/>
								</h:commandButton>
							
								<h:commandButton image="/img/pesquisa/outras_bolsas.png" rendered="#{cv.tipoComunidadeVirtual.restritoGrupo}"
									alt="Participar dessa comunidade" actionListener="#{buscarComunidadeVirtualMBean.participarComunidadeVirtualRestritoGrupo}" 
									title="Participar dessa comunidade">
									<f:attribute name="idComunidade" value="#{cv.id}"/>
								</h:commandButton>
							</td>
							
							<td class="botaoParticiparModerada">
								<h:commandButton id="solicitarSemCodigo" image="/img/pesquisa/indicar_bolsista.gif" rendered="#{cv.tipoComunidadeVirtual.moderada && (cv.codigoAcesso == null || cv.codigoAcesso == '') }"
									alt="Solicitar participação" title="Solicitar participação" actionListener="#{buscarComunidadeVirtualMBean.solicitarParticipacaoComunidadeVirtual}"
									onclick="if (!confirm('Tem certeza que deseja solicitar a participação nesta comunidade?')) return false;" >
									<f:attribute name="idComunidade" value="#{cv.id}"/>
								</h:commandButton>
								
								<a4j:commandButton id="solicitarComCodigo" image="/img/pesquisa/indicar_bolsista.gif" rendered="#{cv.tipoComunidadeVirtual.moderada && cv.codigoAcesso != null && cv.codigoAcesso != '' }"
									alt="Solicitar participação" actionListener="#{ buscarComunidadeVirtualMBean.prepararIngressoComCodigo }" title="Solicitar participação" reRender="descCadastroEmComunidade,dicaCodigoCadastroEmComunidade" 
									oncomplete="dialogIC.show();">
									<f:attribute name="idComunidade" value="#{cv.id}"/>
								</a4j:commandButton>
							</td>
							
							<td class="botaoVisualizar">
								<h:commandButton image="/img/buscar.gif" rendered="#{cv.tipoComunidadeVirtual.moderada || cv.tipoComunidadeVirtual.publica}" alt="Visualizar comunidade mas não tornar-se membro"
									actionListener="#{comunidadeVirtualMBean.visualizarComunidadeVirtualModerada}" title="Visualizar comunidade mas não tornar-se membro">
									<f:attribute name="idComunidade" value="#{cv.id}"/>
								</h:commandButton>
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				
				<div style="text-align: center;"> 
				    
				 	<c:if test="${paginacao.totalPaginas > 1}">   
				    	<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }" alt="Voltar"/>
				 		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
							<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
					    </h:selectOneMenu>
					 </c:if>
					 
				    <h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}" alt="Avançar"/>
				    <br/><br/>
				 
				    <em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
				</div>
				
		</h:form>
	</c:if>
	
</f:view>

<script>YAHOO.util.Event = YahooEvent;</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
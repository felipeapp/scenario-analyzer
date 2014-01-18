<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">var J = jQuery.noConflict();</script>
<script type="text/javascript">
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');
</script>

<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>

<style>
<!--

.body2 {
	height:100px;
	overflow:auto;
}
-->
</style>

<f:view>
<h:messages showDetail="true"></h:messages>


<h:panelGroup id="ajaxErros">
	<h:dataTable  value="#{atividadeExtensao.avisosAjax}" var="msg" rendered="#{not empty atividadeExtensao.avisosAjax}" width="100%">
		<t:column><h:outputText value="<div id='painel-erros' style='position: relative; padding-bottom: 15px;'><ul class='erros'><li>#{msg.mensagem}</li></ul></div>" escape="false"/></t:column>
	</h:dataTable>
</h:panelGroup>



	<c:set var="PROGRAMA" 	value="<%= String.valueOf(TipoAtividadeExtensao.PROGRAMA) %>" 	scope="application"/>	
	<c:set var="PRODUTO" 	value="<%= String.valueOf(TipoAtividadeExtensao.PRODUTO) %>" 	scope="application"/>		

	<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PROGRAMA}">
		<h2><ufrn:subSistema /> > Vincular Ações de Extensão ao Programa</h2>
	</c:if>
	
	<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PRODUTO}">
		<h2><ufrn:subSistema /> > Vincular Ações de Extensão ao Produto</h2>
	</c:if>
	

<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%" align="justify">
					<ul>
						<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PROGRAMA}"> 
							<li>
								Programa é entendido como o conjunto de ações coerentes articuladas ao ensino
								e à pesquisa e integradas às políticas institucionais da Universidade direcionadas às
								questões relevantes da sociedade, com caráter regular e continuado. Um programa é 
								composto de no mínimo 3 (três) projetos e 2 (duas) outras ações de extensão.
							</li>
						</c:if>
						<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PRODUTO}"> 
							<li>
								Produto é resultado de atividades de extensão, ensino e pesquisa, com a finalidade de difusão e divulgação
								cultural, científica ou tecnológica. É considerado produto: livros, anais, artigos, textos, revistas, manual,
								cartilhas, jornal, relatório, vídeos, filmes, programas de rádio e TV, softwares, CDs, DVDs, partituras, 
								arranjos musicais, entre outros.
							</li>
						</c:if>
						
						
					</ul>
			</td>
			<td>
				<%@include file="passos_atividade.jsp"%>
			</td>
		</tr>
	</table>
</div>


	<%@include file="/extensao/form_busca_atividade.jsp"%>
	

	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"	style="overflow: visible;"/>: Visualizar Ação de Extensão
	    <h:graphicImage value="/img/adicionar.gif"	style="overflow: visible;"/>: Vincular Ação de Extensão
	    <h:graphicImage value="/img/delete.gif"	style="overflow: visible;"/>: Remover Ação de Extensão
	</div>


	<h:form id="atividades">
		
		
		<%-- <h:inputHidden value="#{atividadeExtensao.comPaginacao}"/> --%>
		
		
		            <rich:simpleTogglePanel switchType="client" label="Lista de ações localizadas" bodyClass="body2">
						
								<h:outputText value="<center><font color='red'><i> Nenhuma Ação localizada </i></font></center>" rendered="#{empty atividadeExtensao.atividadesLocalizadas}" escape="false"/>
								
								<t:dataTable id="datatableTodasAtividades" 
									value="#{atividadeExtensao.atividadesLocalizadas}" var="atividade" align="center" width="100%" 
									styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
									rendered="#{not empty atividadeExtensao.atividadesLocalizadas}">
										<t:column><h:outputText value="#{atividade.anoTitulo}" /></t:column>
		
										<t:column>
											<h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}" />
										</t:column>
		
										<t:column styleClass="centerAlign">
											<h:commandLink title="Visualizar Ação" action="#{ atividadeExtensao.view }" immediate="true">
												<%-- onclick="javascript:window.open('/sigaa/extensao/Atividade/view.jsf', '', 'width=650, height=550, left=200, scrollbars, ');"--%>
											         <f:param name="id" value="#{atividade.id}"/>
						                   			<h:graphicImage url="/img/view.gif" />
											</h:commandLink>
										</t:column>
										
										<t:column styleClass="centerAlign">
											<a4j:commandLink action="#{atividadeExtensao.adicionaAtividade}" 	
												title="Adicionar Ação de Extensão" 
												reRender="atividadesVinculadas, ajaxErros">
												<f:param value="#{atividade.id}" name="id"/>
												<h:graphicImage url="/img/adicionar.gif" />
											</a4j:commandLink>
										</t:column>
								</t:dataTable>
				
					</rich:simpleTogglePanel>
					
					
					 <br/>
			            <a4j:status startText="Processando..." stopText="">
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>			                			                				            
			            </a4j:status>
					 <rich:separator height="2" lineType="dashed"/>
					 <br/>
					 
					<rich:simpleTogglePanel switchType="client" label="Lista de ações vinculadas à proposta atual" bodyClass="body2" id="atividadesVinculadas">
					
								<h:outputText value="<center><font color='red'><i> Nenhuma Ação vinculada </i></font></center>" escape="false" rendered="#{empty atividadeExtensao.obj.atividades}"/>
									
								<t:dataTable id="datatableAtividadesPrograma" value="#{atividadeExtensao.obj.atividades}" var="atividade" 
										align="center" width="100%" styleClass="listagem" 
										rowClasses="linhaPar, linhaImpar" 
										rendered="#{not empty atividadeExtensao.obj.atividades}">
					
										<t:column><h:outputText value="#{atividade.anoTitulo}" /></t:column>			
										<t:column><h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}" /></t:column>
					
										<t:column width="5%" styleClass="centerAlign">
											<a4j:commandLink action="#{atividadeExtensao.removeAtividade}"
												title="Remover Ação" 
												reRender="atividadesVinculadas, ajaxErros">
												<h:graphicImage url="/img/delete.gif" />
												<f:param value="#{atividade.id}" name="id"/>
											</a4j:commandLink>
											
										</t:column>
					
								</t:dataTable>
				
					</rich:simpleTogglePanel>
				
				<table class="formulario" width="100%">
						<tfoot>
							<tr>
								<td>
									<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" id="btVoltar"/>
									<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" id="btCancelar" onclick="#{confirm}"/>
									
									<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PROGRAMA}">
										<h:commandButton value="Avançar >>" action="#{programaExtensao.submeterAtividades}" id="btAvancarPrograma"/>
									</c:if>
										
									<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PRODUTO}">
										<h:commandButton value="Avançar >>" action="#{produtoExtensao.submeterAtividades}" id="btAvancarProduto"/>
									</c:if>	
								</td>
							</tr>
						</tfoot>
				</table>

		</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
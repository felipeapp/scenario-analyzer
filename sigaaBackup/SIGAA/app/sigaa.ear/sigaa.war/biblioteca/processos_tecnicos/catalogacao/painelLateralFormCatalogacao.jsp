


<%-- Página que desenha a arvore de materias do título, podem ser exemplares ou fascículos --%>

<%-- 
     Re Definição das tag utilizadas, apenas para não ficar dando erro no momento do desenvolvimento
     Já que as tag vão estão na página onde essa é incluída  --%>
    
     
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>
<%@ taglib uri="/tags/ajax" prefix="ajax"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<%@taglib uri="/tags/jawr" prefix="jwr"%>   
	
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<script type="text/javascript">

// Função para abrir a pagina com o histórico de alterações de um título. 
// Obs.: Tem que fica na mesma página, não funciona se colocado do arquivo formulario_catalogacao.js
var janelaHistorico = null;

function abreJanelaHistoricoAlteracoes(idEntidadeMarcVisualizarHistorico){

	<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
	
		if (janelaHistorico == null || janelaHistorico.closed){
			janelaHistorico = window.open('${ctx}/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracoesCatalogacaoTitulo.jsf?idEntidadeMarcVisualizarHistorico='+idEntidadeMarcVisualizarHistorico,'','width=1024,height=400,left=50,top=100,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janelaHistorico.location = '${ctx}/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracoesCatalogacaoTitulo.jsf?idEntidadeMarcVisualizarHistorico='+idEntidadeMarcVisualizarHistorico;
		}
		
		janelaHistorico.focus();
		
	</c:if>

	<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
		if (janelaHistorico == null || janelaHistorico.closed){
			janelaHistorico = window.open('${ctx}/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracoesCatalogacaoAutoridade.jsf?idEntidadeMarcVisualizarHistorico='+idEntidadeMarcVisualizarHistorico,'','width=1024,height=400,left=50,top=100,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janelaHistorico.location = '${ctx}/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracoesCatalogacaoAutoridade.jsf?idEntidadeMarcVisualizarHistorico='+idEntidadeMarcVisualizarHistorico;
		}
		
		janelaHistorico.focus();
	</c:if>
}


//Função para abrir a pagina com uma tela de busca para realizar busca em outras catalogações sem precisar abrir outra janela no navegador
var janelaBuscaCatalogacoes = null;

function abreJanelaBuscaCatalogacoes(){

	<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
	
		if (janelaBuscaCatalogacoes == null || janelaBuscaCatalogacoes.closed){
			janelaBuscaCatalogacoes = window.open('${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaTitulosPopUp.jsf','','width=1024,height=700,left=50,top=100,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janelaBuscaCatalogacoes.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaTitulosPopUp.jsf';
		}
		
		janelaBuscaCatalogacoes.focus();
		
	</c:if>

	<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
		if (janelaBuscaCatalogacoes == null || janelaBuscaCatalogacoes.closed){
			janelaBuscaCatalogacoes = window.open('${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaAutoridadesPopUp.jsf','','width=1024,height=700,left=50,top=100,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janelaBuscaCatalogacoes.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaAutoridadesPopUp.jsf';
		}
		
		janelaBuscaCatalogacoes.focus();
	</c:if>
}

/*
 * Abre o model panel para buscar os campos de dados com seus sub campos. Focando o input text para permitir utilizar o botão "ENTER" para realzar a busca
 */
function abreModelPanelBuscarCampoDados(){
	modelPanelBuscarCamposDados.show();  
	document.getElementById('fromDadosCatalograficos:inputTxtTagEtiquetaBuscaCampoDadosCompleto').focus(); 
	return true;
}


/*
 * Abre o model panel para buscar os campos de dados existentes na planilha de catalogação escolhida. Usado na catalogação simplificada.
 */
function abreModelPanelBuscarCampoDadosPlanilha(){
	modelPanelBuscarCamposDadosPlanilha.show();  
	return true;
}


/*  
 * Funda JQuery para ocultar/mostrar um Div, comentada porque ficava melhor fazer com ajax
 * 
 * Aqui é um modelo para ser precisar usar novamente
 *
window.onload = function() { 
	var J = jQuery.noConflict();
	
	J("#linkOcultarPainelLateral").click(function () {
		J("#menuLateralCatalogacao").fadeOut("slow"); // esconde o painel lateral
		
		J("#dadoCatalogacao").css('width', '100%');  // aumenta o tamanho do div de dados da catalogação
		
		J("#linkOcultarPainelLateral").css('display', 'none'); // esconde o link de ocultar
		J("#linkMostarPainelLateral").css('display', '');
	});
	
	J("#linkMostarPainelLateral").click(function () {
		J("#menuLateralCatalogacao").fadeIn("slow");
		
		J("#dadoCatalogacao").css('width', '80%');
		
		J("#linkMostarPainelLateral").css('display', 'none'); // esconde o link de ocultar
		J("#linkOcultarPainelLateral").css('display', '');
	});
 }

*/

</script>



<div id="botoesExibicaoPainalLateral" style="width: 20%; height: 35px; float: left; text-align: center; padding-bottom: 5px;">
	<table style="width: 100%; text-align: center; border: 1px solid #A6C9E2;">
		<caption style="background-color: #C4D2EB; font-weight: bold; height: 15px;"> Preferências de Visualização </caption>
		<tr>
			<td style="height: 20px;">
				<h:outputLink id="linkOcultarPainelLateral" style="margin-left: 10px;" value="#" rendered="#{catalogacaoMBean.exibirPainelLateral}">
					Ocultar Painel Lateral
					<a4j:support event="onclick" actionListener="#{catalogacaoMBean.atualizaExibirPainelLateral}" 
					reRender="divGeralFormCatalogacao" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}"/>
				</h:outputLink>
			
			
			
				<h:outputLink id="linkMostarPainelLateral" style="margin-left: 10px;" value="#" rendered="#{!catalogacaoMBean.exibirPainelLateral}">
					Exibir Painel Lateral
					<a4j:support event="onclick" actionListener="#{catalogacaoMBean.atualizaExibirPainelLateral}" 
					reRender="divGeralFormCatalogacao" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}"/>
				</h:outputLink>
			</td>
		</tr>
	</table>
	
</div>


<div id="menuLateralCatalogacao" style=" ${catalogacaoMBean.exibirPainelLateral ?  'width: 20%;' : 'display:none; width: 0%;'} height: 100%; clear:left; float: left;">
			
		 	
            <p:panel id="pnlOperacoes" header="Operações" toggleable="false" closable="false" style="height: 350px;">  
           
           		<c:if test="${catalogacaoMBean.obj.id > 0}">
					<a4j:commandLink onclick="abreJanelaHistoricoAlteracoes( #{catalogacaoMBean.obj.id} ); return false;" style="color: #003390;">
		                <h:outputText value="Visualizar Histórico de Alterações " /> 
		            </a4j:commandLink>
				</c:if>
				
				<br/><br/>
				
				<t:div id="divAdicionarCamposDados" rendered="#{catalogacaoMBean.usarTelaCatalogacaoCompleta}">
				
					<a4j:commandLink id="adicionaNovoCampoDadosMenu" onclick="return simulaClickLink('.botaoAdicionaCampoDados');" style="color: #003390;">				
						<h:outputText value="Adicionar Campo de Dados" /> 
					</a4j:commandLink>
					
					<br/><br/>
			
					<a onclick="return abreModelPanelBuscarCampoDados();" style="font-weight: bold; color: #003390; cursor: pointer;"> Adicionar Campo de Dados com Sub Campos </a>
					
					<br/><br/>
					
					<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
						<a4j:commandLink id="adicionaCamposDadosObrigatoriosMenu" style="color: #003390;"
									actionListener="#{catalogacaoMBean.adicionarCampoDadosObrigatorios}" reRender="dtTblCamposDadosCatalograficos"
									oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">				
							<h:outputText value="Adicionar Campos de Dados Obrigatórios" /> 
						</a4j:commandLink>
			           
			            <br/><br/>
		           
		            </c:if>
		            
	            </t:div>
	           
	           	
	           	<t:div id="divAdicionarCamposDadosSimplificado" rendered="#{! catalogacaoMBean.usarTelaCatalogacaoCompleta && catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0}">
	           		
	           		<a4j:commandLink style="color: #003390;"
								actionListener="#{catalogacaoMBean.carregaCamposPlanilhaSimplificada}" reRender="dtTblCamposDadosCatalograficos, divAdicionadoCamposPlanilha"
								oncomplete="abreModelPanelBuscarCampoDadosPlanilha(); #{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">				
						<h:outputText value="Adicionar Campo" /> 
					</a4j:commandLink>
					
	           		<br/><br/>
	           	
	           	</t:div>
	           
	           
	          	<a4j:commandLink onclick="abreJanelaBuscaCatalogacoes(); return false;" style="color: #003390;">
		                <h:outputText value="Buscar Catalogações no Acervo " /> 
		        </a4j:commandLink>
           
			</p:panel>
			
			
			
			
			
			
			
			
			<p:panel id="pnlMateriais" header="Materiais do Título" toggleable="false" closable="false"
					rendered="#{catalogacaoMBean.tipoCatalogacaoBibliografica && catalogacaoMBean.obj.id > 0}" 
					style="min-height: 300px; max-height: 420px;">  
           
           		<t:div style="width: 100%;" rendered="#{catalogacaoMBean.quantidadeMateriaisTitulo >= 0 && !catalogacaoMBean.visualizaListaMateriaisTitulo}">
           			<a4j:commandLink value="Visualizar Materiais do Título ( #{catalogacaoMBean.quantidadeMateriaisTitulo} )" style="color: #003390;"
           				actionListener="#{catalogacaoMBean.mostrarListaMateriaisTitulo}" reRender="pnlMateriais" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}"/>
           		</t:div>
           
           		<t:div style="max-height:300px; overflow-y:auto; overflow-x:auto;  width: 100%;" 
           		   			rendered="#{catalogacaoMBean.quantidadeMateriaisTitulo > 0 && catalogacaoMBean.visualizaListaMateriaisTitulo}">
	           		<rich:tree switchType="client" style="width:300px" var="node" value="#{catalogacaoMBean.arvoreMateriaisTitulo}"
					 			nodeSelectListener="#{catalogacaoMBean.selecinouMaterialEdicao}" ajaxSubmitSelection="true" ignoreDupResponses="true" 
					 			onclick="document.getElementById('fromDadosCatalograficos:cmdButtonRedirecionaPagina').click(); return true;">
					    
					    		<rich:treeNode  iconLeaf="/img/livro.png" icon="/img/livro.png">
					              		 <h:outputText value="#{node}" title="Clique para editar as informação desse material."/>
					            </rich:treeNode>
					    
					</rich:tree>
				   
				    <%-- "artifícil" utilizado para fazer o rith:tree redirecionar para outra página ao ser clicado  --%>
				   	<h:commandButton id="cmdButtonRedirecionaPagina" value="Redirecionar" action="#{catalogacaoMBean.redirecionaPaginaEdicaoMaterial}"  style="display:none;" />
				   	
           		</t:div>
           		
           		
			</p:panel>
			
			
			
			
			<p:panel id="pnlClassificacoes" header="Classificações e Associações" toggleable="false" closable="false"
				rendered="#{catalogacaoMBean.tipoCatalogacaoBibliografica}" style="height:500px;">  
           
           		<table id="tlbDadosClassificacao" width="100%">
						
						<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1}">
						
							<tr>
								<th style="text-align: left; font-weight: bold;">Classe ${classificacaoBibliograficaMBean.descricaoClassificacao1} : </th>
							</tr>
							<tr>
								<td><h:inputText value="#{catalogacaoMBean.obj.classificacao1}" size="30" maxlength="50" /> </td>
							</tr>
							<tr>
								<th style="text-align: left; font-weight: bold;">Classe Principal ${classificacaoBibliograficaMBean.descricaoClassificacao1} : </th> 
							</tr>
							<tr>
								<td> <h:inputText value="#{catalogacaoMBean.obj.classePrincipalClassificacao1}" size="10" maxlength="3" /></td>
							</tr>
							
							<tr>
								<th style="text-align: left; font-weight: bold;">Área CNPq associada à classe ${classificacaoBibliograficaMBean.descricaoClassificacao1} : </th> 
							</tr>
							<tr>
								<td> 
									<h:selectOneMenu id="selectAreaCNPqClassificacao1" value="#{catalogacaoMBean.obj.areaConhecimentoCNPQClassificacao1.id}">
										<f:selectItems value="#{catalogacaoMBean.grandesAreasCNPqComboBox}" />
									</h:selectOneMenu> 
								</td>
							</tr>
							
						</c:if>
						
						
						<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2}">
							<tr>
								<th style="text-align: left; font-weight: bold;">Classe ${classificacaoBibliograficaMBean.descricaoClassificacao2} : </th> 
							</tr>
							<tr>
								<td> <h:inputText value="#{catalogacaoMBean.obj.classificacao2}" size="30" maxlength="50" /> </td>
							</tr>
							<tr>
								<th style="text-align: left; font-weight: bold;">Classe Principal ${classificacaoBibliograficaMBean.descricaoClassificacao2} : </th>
							</tr>
							<tr>
								 <td><h:inputText value="#{catalogacaoMBean.obj.classePrincipalClassificacao2}" size="10" maxlength="4" /> </td>
							</tr>
							
							<tr>
								<th style="text-align: left; font-weight: bold;">Área CNPq  associada à  classe ${classificacaoBibliograficaMBean.descricaoClassificacao2} : </th> 
							</tr>
							<tr>	
								<td> 
									<h:selectOneMenu id="selectAreaCNPqClassificacao2" value="#{catalogacaoMBean.obj.areaConhecimentoCNPQClassificacao2.id}">
										<f:selectItems value="#{catalogacaoMBean.grandesAreasCNPqComboBox}" />
									</h:selectOneMenu> 
								</td>
							</tr>
							
						</c:if>
						

						<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3}">
							<tr>
								<th style="text-align: left; font-weight: bold;">Classe ${classificacaoBibliograficaMBean.descricaoClassificacao3} : </th> 
							</tr>
							<tr>
								<td> <h:inputText value="#{catalogacaoMBean.obj.classificacao3}" size="30" maxlength="50" /> </td>
							</tr>
							<tr>
								<th style="text-align: left; font-weight: bold;">Classe Principal ${classificacaoBibliograficaMBean.descricaoClassificacao3} : </th>
							</tr>
							<tr>
								 <td><h:inputText value="#{catalogacaoMBean.obj.classePrincipalClassificacao3}" size="10" maxlength="4" /> </td>
							</tr>
							
							<tr>
								<th style="text-align: left; font-weight: bold;">Área CNPq  associada à  classe ${classificacaoBibliograficaMBean.descricaoClassificacao3} : </th> 
							</tr>
							<tr>	
								<td> 
									<h:selectOneMenu id="selectAreaCNPqClassificacao3" value="#{catalogacaoMBean.obj.areaConhecimentoCNPQClassificacao3.id}">
										<f:selectItems value="#{catalogacaoMBean.grandesAreasCNPqComboBox}" />
									</h:selectOneMenu> 
								</td>
							</tr>
							
						</c:if>
						
						
					
					
					
						
						<tr style="height: 20px;">
						</tr>
						<tr>
							<th style="text-align: left; font-weight: bold;">Associação com Defesas: </th>
						</tr>
						<tr>
							<td>
							<c:if test="${catalogacaoMBean.obj.idDadosDefesa == null}">
								<i style="color: red;">Essa catalogação não está associada com uma defesa de Tese ou Dissertação </i>
							</c:if>
							</td>
						</tr>
						<tr>
							<td>
							<c:if test="${ catalogacaoMBean.obj.idDadosDefesa != null}">
									<i> 
									${catalogacaoMBean.obj.dadosDefesa.tituloStripHtml} <br>
								    ${catalogacaoMBean.obj.dadosDefesa.discente.nome}  
								    </i>
							</c:if>
							</td>
						</tr>
						<tr>		
							<td> 
								
								<h:commandLink action="#{consultarDefesaMBean.iniciar}" id="cmdAssociarAUmaTese">
									<f:param name="associacaoCatalogacao" value="true"/>
				    				<h:graphicImage url="/img/associado_pequeno.png" style="border:none" 
				    					alt="Clique aqui para associar/alterar a associação dessa catalogação com uma defesa no sistema." 
				    					title="Clique aqui para associar/alterar a associação dessa catalogação com uma defesa no sistema." />
				    			</h:commandLink>
				    			<c:if test="${ catalogacaoMBean.obj.idDadosDefesa != null}">
				    				<h:commandLink action="#{catalogacaoMBean.removerAssociadoDefesa}" id="cmdRemoverAssociacaoAUmaTese" 
				    						onclick="return confirm('Confirma a remoção da associação com a defesa ? ');">
				    					<h:graphicImage url="/img/associado_pequeno_excluir.png" style="border:none" 
				    					alt="Clique aqui remover a associação dessa catalogação com a defesa." 
				    					title="Clique aqui remover a associação dessa catalogação com a defesa." />
				    				</h:commandLink>
				    			</c:if>
							</td>
						</tr>
					</table>
           
			</p:panel>
			
			
			
		 	<%-- Só funcionado com o rich faces 3.3.3 
		   <rich:simpleTogglePanel switchType="client" label="Teclas de Atalho" height="120px">
	       		<strong> Alt + F7 </strong> &nbsp&nbsp&nbsp = &nbsp Adicionar Campo de Dados  <br/>
	       		<strong> Alt + F8 </strong> &nbsp&nbsp&nbsp = &nbsp Adicionar Campo de Dados com Sub Campos <br/>
	       		<strong> Alt + F9 </strong> &nbsp&nbsp&nbsp = &nbsp Validar Informações Marc  <br/>
	       		<strong> Alt + F10 </strong> &nbsp = &nbsp Ordenar os Campos de Dados   <br/>
	       		<strong> Alt + F11 </strong> &nbsp = &nbsp Remover os Campo de Dados Vazios   <br/>
	       		<strong> Alt + F12 </strong> &nbsp = &nbsp Cancelar Catalogação        <br/>
	       </rich:simpleTogglePanel>
	       --%>
	       
</div>


 
 
 
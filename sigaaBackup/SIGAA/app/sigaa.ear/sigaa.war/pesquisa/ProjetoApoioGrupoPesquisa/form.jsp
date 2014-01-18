<%@page import="br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<style>
.selecionado{
	background:#C4D2EB;
}
ul.nav { 
	margin:0; 
	padding:0;
	position: relative;	 
	}
ul.nav li {
	list-style:none; 
	display:inline;
}
ul.nav li a { 
	float: left;
	text-align:center;
	padding:0.3em;
	border-width:0.1em;
	border-color:#EEE #404E82 #404E82 #EEE;
	border-style:solid;
	text-decoration:none;
	width: 14%;
	height: 54px;	
	font-size: 10px;
	margin: 2px;
	}
ul.nav a:hover, .selecionado{
	background:#C4D2EB;
	color:#fff;
	border-color:#EEE #404E82 #404E82 #EEE;	
	}
.rich-tabhdr-side-cell, .rich-tabhdr-side-border { padding: 1px !important; }
.rich-tab-bottom-line td { padding: 0 !important; }
.fc-header-title {
	margin-top:0;
	text-align:center;
	white-space:nowrap;
	width:250px;
	}
.ui-autocomplete-loading {
	size: 80;
	}
</style>

<script>
function limitText(limitField, limitCount, limitNum) {
    if (limitField.value.length > limitNum) {
        limitField.value = limitField.value.substring(0, limitNum);
    } else {
        $(limitCount).value = limitNum - limitField.value.length;
    }
}
</script>

<f:view>

<rich:modalPanel id="painelLattes" height="100" width="500">
		<f:facet name="header">
			<h:panelGroup>
				<h:outputText value="Atualizar Endereço do CV Lattes"></h:outputText>
	        </h:panelGroup>
	     </f:facet>
	     <f:facet name="controls">
	         <h:panelGroup>
	          <h:outputLink value="#" id="btn1">  
	 		       <h:graphicImage value="/img/close.png"  style="margin-left:5px; cursor:pointer; border: none" />  
	               <rich:componentControl for="painelLattes" attachTo="btn1" operation="hide" event="onclick" />  
	          </h:outputLink>
	         </h:panelGroup>
	     </f:facet>
	
		<h:form id="formLattes">
	     	 <h:inputText id="endLattesPainel" value="#{projetoApoioGrupoPesquisaMBean.membroPainel.enderecoLattes}" size="60"/> <br /><br />
		     <h:commandButton value="Adicionar Lattes" actionListener="#{ projetoApoioGrupoPesquisaMBean.adicionarLattes }" rendered="#{ empty projetoApoioGrupoPesquisaMBean.membroPainel.enderecoLattes }"/>
		     <h:commandButton value="Alterar Lattes" actionListener="#{ projetoApoioGrupoPesquisaMBean.adicionarLattes }" rendered="#{ not empty projetoApoioGrupoPesquisaMBean.membroPainel.enderecoLattes }"/>
	     </h:form>
	     
</rich:modalPanel>

<p:resources/>

	<h2> <ufrn:subSistema /> &gt; Submissão de Projetos para ação de Apoio a Grupo de Pesquisa </h2>

	<div class="infoAltRem">
       	<h:graphicImage value="/img/prodocente/lattes.gif" style="overflow: visible;"/>: Currículo na Plataforma Lattes registrado no sistema
        <h:graphicImage value="/img/prodocente/question.png" style="overflow: visible;"/>: Currículo na Plataforma Lattes não registrado no sistema
	</div>

	<h:form id="form">
		<table class="formulario" width="100%">
		<caption> Projeto de Apoio a Grupo de Pesquisa </caption>
			<tr>
				<th class="obrigatorio">Título:</th>
				<td> 
					<h:inputText id="titulo" value="#{ projetoApoioGrupoPesquisaMBean.obj.projeto.titulo}" size="65" maxlength="400"/>
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">Edital de Pesquisa:</th>
				<td> 
					<h:selectOneMenu value="#{ projetoApoioGrupoPesquisaMBean.obj.editalPesquisa.id}" style="width: 100%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/> 
						<f:selectItems value="#{editalPesquisaMBean.editaisCombo}"/> 
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio" width="15%">Grupo de Pesquisa:</th>
				<td>
					<h:inputText id="suggestionNomeGP" value="#{ projetoApoioGrupoPesquisaMBean.obj.grupoPesquisa.nome}" size="65"/>
					<rich:suggestionbox for="suggestionNomeGP" width="450" height="100" minChars="3" id="nomeGrupoPesquisa" 
						suggestionAction="#{ grupoPesquisa.autocompleteGrupoPesquisa }" var="_grupoPesquisa" fetchValue="#{_grupoPesquisa.nome}">
							 
						<h:column>
							<h:outputText value="#{_grupoPesquisa.nome}" />
						</h:column>
					 
		        	   <f:param name="apenasAtivos" value="true" />
				       <a4j:support event="onselect" actionListener="#{ projetoApoioGrupoPesquisaMBean.carregaMembros }" reRender="dtPermanentes" immediate="true">
		        	 	  <f:param name="apenasAtivos" value="true" />
		               	  <f:attribute name="grupoPesqu" value="#{ _grupoPesquisa }"/>
					   </a4j:support>
					</rich:suggestionbox>
				</td>
			</tr>
			
			<tr>
				<td style="padding: 15px 0 0;"></td>
			</tr>
			
			<tr>
				<td class="subFormulario" colspan="3"> Lista do Membros do Grupo de Pesquisa </td>
				<rich:dataTable id="dtPermanentes" value="#{ projetoApoioGrupoPesquisaMBean.obj.grupoPesquisa.equipesGrupoPesquisaCol }" 
					var="membro" width="100%" styleClass="subFormulario" rowClasses="linhaPar, linhaImpar" binding="#{projetoApoioGrupoPesquisaMBean.membros}">
					<rich:column width="50%">
						<f:facet name="header"><f:verbatim>Pesquisador</f:verbatim></f:facet>
						<h:outputText value="#{membro.pessoa.nome}"/>
					</rich:column>
					<rich:column width="15%">
						<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
						<h:outputText value="#{ membro.categoriaString }"/>
					</rich:column>
					<rich:column width="15%">
						<f:facet name="header"><f:verbatim>Classificação</f:verbatim></f:facet>
						<h:outputText value="<font color='red'>" rendered="#{membro.classificacao == 1}"  escape="false"/>
							<h:outputText value="#{membro.classificacaoString}" />
						<h:outputText value="</font>" rendered="#{membro.classificacao == 1}"  escape="false"/>
					</rich:column>
					<rich:column width="15%">
						<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
						<h:outputText value="#{ membro.tipoMembroGrupoPesqString }"/>
					</rich:column>
					<rich:column width="5%">
						<center>
							<f:facet name="header"><h:outputText value="Lattes" /></f:facet>
							<a4j:commandLink id="showPanelOn" actionListener="#{projetoApoioGrupoPesquisaMBean.carregarPainel}" 
	               					oncomplete="Richfaces.showModalPanel('painelLattes')" immediate="true" reRender="painelLattes">  
								<h:graphicImage value="/img/prodocente/lattes.gif" style="overflow: visible;" rendered="#{not empty membro.enderecoLattes}" title="Currículo do Pesquisador na Plataforma Lattes"/>
								<h:graphicImage value="/img/prodocente/question.png" style="overflow: visible;" rendered="#{empty membro.enderecoLattes}" title="Endereço do CV não registrado no sistema"/>
						    </a4j:commandLink>
					    </center>
					</rich:column>
				</rich:dataTable>
			</tr>
			
			<br />
			
			<tr>
				<td colspan="2">
					<div id="abas-descricao">
			
						<div id="justificativa" class="aba">
							<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />&nbsp;<i> Descrição sumária da demanda apresentada, sua importância e necessidade </i><br /><br />
							<h:inputTextarea id="justificativa-objetivos" value="#{ projetoApoioGrupoPesquisaMBean.obj.projeto.justificativa }" style="width: 95%" rows="10" 
								onkeydown="limitText(this, countJustificativa, 5000);" onkeyup="limitText(this, countJustificativa, 5000);"/>
							<center>
                                 Você pode digitar <input readonly type="text" id="countJustificativa" size="3" value="${5000 - fn:length(projetoApoioGrupoPesquisaMBean.obj.projeto.justificativa) < 0 ? 0 : 5000 - fn:length(projetoApoioGrupoPesquisaMBean.obj.projeto.justificativa)}"> caracteres.
                            </center>							
						</div>
	
						<div id="integracao" class="aba">
							<i> Citar a integração do(s) projetos de Pesquisa desenvolvidos pelo grupo com atividades de ensino ou Extensão, quando se aplicar </i><br /><br />
							<h:inputTextarea id="integracao" value="#{projetoApoioGrupoPesquisaMBean.obj.integracao}" style="width: 95%" rows="10" 
								onkeydown="limitText(this, countIntegracao, 5000);" onkeyup="limitText(this, countIntegracao, 5000);"/>
							<center>
                                 Você pode digitar <input readonly type="text" id="countIntegracao" size="3" value="${5000 - fn:length(projetoApoioGrupoPesquisaMBean.obj.integracao) < 0 ? 0 : 5000 - fn:length(projetoApoioGrupoPesquisaMBean.obj.integracao)}"> caracteres.
                            </center>							
						</div>
	
					</div>
				</td>
			</tr>
			
			<br />
			
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%" >
						<caption class="listagem">Itens Solicitados</caption>
						<tr>
							<td colspan="5" class="descricaoOperacao">Selecione o Elemento de Despesa:<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/></td>
						</tr>
						<tr>
							<td colspan="5">
								<input type="hidden" id="idElementoDispesa" name="idElementoDespesa" value="${idElementoDespesa}" />
								<ul class="nav">
										<li><a href="#nb" id="34" onclick="checkTipoDespesa('34');"><img src="${ctx}/img/extensao/calendar_preferences.png" /><br/>Diárias</a></li>
										<li><a href="#nb" id="33" onclick="checkTipoDespesa('33');"><img src="${ctx}/img/extensao/shoppingcart_full.png" /> <br/>Material de Consumo</a></li>
										<li><a href="#nb" id="35" onclick="checkTipoDespesa('35');"><img src="${ctx}/img/extensao/passagens.png" /><br/> Passagens</a></li>							
										<li><a href="#nb" id="31" onclick="checkTipoDespesa('31');"><img src="${ctx}/img/extensao/pessoafisica.png" /><br/>Pessoa Física</a></li>
										<li><a href="#nb" id="29" onclick="checkTipoDespesa('29');"><img src="${ctx}/img/extensao/pessoajuridica.png"/><br/>	Pessoa Jurídica</a></li>
										<li><a href="#nb" id="38" onclick="checkTipoDespesa('38');"><img src="${ctx}/img/extensao/pc.png"/><br/>	Equipamentos</a></li>
								</ul>
							</td>
						</tr>
			
			            <tr>
			             <td colspan="5">
			                 <div class="descricaoOperacao" id="avisoMaterialEquipamento" style="display: none;">
			                    Prezado(a) docente,<br/>                  
			                    Os itens de material de consumo para o seu projeto, quando aprovado, só ficarão disponíveis se houver uma <b>prévia licitação</b>.
			                    Portanto, é essencial que observe o catálogo de materiais do SIPAC e verifique se este material encontra-se licitado em um pregão, 
			                    e, caso esteja, marcar a opção abaixo: "Este material está licitado?".
			                 </div>
			             </td>
			            </tr>
			
						<tr>
							<th width="15%"  class="required">Discriminação:</th>
							<td colspan="5">
								<h:inputTextarea id="discriminacao" value="#{projetoApoioGrupoPesquisaMBean.orcamento.discriminacao}" style="width: 95%" rows="2"/>
							</td>
						</tr>
			
						<tr>
							<th  class="required">Quantidade:</th>
							<td width="10%">
								<h:inputText id="quantidade" value="#{projetoApoioGrupoPesquisaMBean.orcamento.quantidade}" size="5" style="text-align: right" maxlength="5" onkeyup="return formatarInteiro(this)"/>
							</td>
			
							<th class="required" width="20%">Valor Unitário: R$</th>
							<td>
								<h:inputText id="valorUnitario" value="#{projetoApoioGrupoPesquisaMBean.orcamento.valorUnitario}" size="10" maxlength="10" 
								onkeypress="return(formataValor(this, event, 2))" style="text-align: right">
									<f:converter converterId="convertMoeda"/>
								</h:inputText>
							</td>
							
			                <td>
			                    <span id="spanMaterialLicitado" style="display: none;">
			                    <h:selectBooleanCheckbox id="checkMaterialLicitado" value="#{projetoBase.orcamento.materialLicitado}" label="Material Licitado" />
			                    Este material está licitado.  
			                       <%-- Chama a action do struts LogonSistemaAction que cria um passaporte e abre o sipac na pagina do relatório de bolsas --%>
			                       <% if (Sistema.isSipacAtivo()) { %>
			                           <a href="javascript://nop/" onclick="window.open('<%= RepositorioDadosInstitucionais.getLinkSipac() %>/public/listaMaterial.do','','width=1024,height=500,left=100,top=100,dependent=yes,scrollbars=yes,status=yes');"> Clique aqui para consultar. </a>
			                       <% } %>
			                      </span> 
			                </td>               
						</tr>
			
						<tfoot>
							<tr>
								<td colspan="5" align="center">
								<h:commandButton action="#{projetoApoioGrupoPesquisaMBean.adicionaOrcamento}" value="Adicionar Itens"/>
								</td>
							</tr>
						</tfoot>
					</table>
						
						<div class="infoAltRem">	    			
	    					<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Despesa<br/>
						</div>
					
						<table class="subFormulario" width="100%">
						  <caption class="subFormulario">Lista de Despesas Cadastradas</caption>
							<thead>
								<tr>
									<th>Descrição</th>
									<th>&nbsp;</th>
									<th style="text-align: right"  width="15%">Valor Unitário </th>
									<th style="text-align: right"  width="10%">Quant.</th>
									<th style="text-align: right" width="15%">Valor Total </th>
									<th>&nbsp;</th>
								</tr>
							</thead>

							<tbody>

								<c:if test="${not empty projetoApoioGrupoPesquisaMBean.tabelaOrcamentaria}">
									
									<c:set value="${projetoApoioGrupoPesquisaMBean.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
									<c:set value="0" var="total" />
									<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">

										<tr  style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
											<td colspan="6">${ tabelaOrc.key.descricao }</td>
										</tr>
											<c:set value="#{tabelaOrc.value.orcamentos}" var="orcas" />
											<c:forEach items="#{orcas}" var="orca" varStatus="status">
												<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
													<td style="padding-left: 20px"> ${orca.discriminacao}</td>
													<td align="right">${orca.materialLicitado ? '(Licitado)' : ''}</td>
													<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orca.valorUnitario}" type="currency" />  </td>
													<td align="right"> <ufrn:format type="valorint" valor="${orca.quantidade}"></ufrn:format> </td>
													<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orca.valorTotal}" type="currency"/>  </td>

													<td width="5%" align="right">
														<h:commandLink action="#{ projetoApoioGrupoPesquisaMBean.removeOrcamento }" title="Remover Despesa" onclick="return confirm('Deseja remover esta despesa?');" immediate="true" id="bt_remover_orcamento">
															<f:setPropertyActionListener value="#{ orca }" target="#{ projetoApoioGrupoPesquisaMBean.orcamentoRemocao }" />
									                   		<h:graphicImage url="/img/delete.gif" />
														</h:commandLink>
													</td>																
												</tr>
											</c:forEach>

											<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
												<td colspan="3"><b>SUB-TOTAL (${ tabelaOrc.key.descricao})</b></td>
												<td  align="right"><b> <ufrn:format type="valorint" valor="${ tabelaOrc.value.quantidadeTotal }"> aa</ufrn:format> </b></td>
												<td align="right"><b><fmt:formatNumber currencySymbol="R$  " value="${ tabelaOrc.value.valorTotalRubrica }" type="currency"/></b></td>
												<td align="right">&nbsp;</td>
											</tr>

											<tr>
												<td colspan="6">&nbsp;</td>
											</tr>

											<c:set var="total" value="${ total + tabelaOrc.value.valorTotalRubrica }" />

									</c:forEach>
									
											<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
												<td colspan="3"><b>TOTAL</b></td>
												<td  align="right"></td>
												<td align="right"><b><fmt:formatNumber currencySymbol="R$  " value="${ total }" type="currency"/></b></td>
												<td align="right">&nbsp;</td>
											</tr>
									
								</c:if>

									<c:if test="${empty projetoApoioGrupoPesquisaMBean.tabelaOrcamentaria}">
										<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
									</c:if>

								</tbody>
						</table>
				</td>
			</tr>
		</table>
		
		<table class="formulario" width="100%">
			<tfoot>
				<tr>
					<td>
						<h:commandButton id="btnGerarNumeracao" action="#{projetoApoioGrupoPesquisaMBean.cadastrar}" value="#{ projetoApoioGrupoPesquisaMBean.confirmButton }"/>
						<h:commandButton id="btnCancelar" action="#{projetoApoioGrupoPesquisaMBean.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<br />
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
		
	</h:form>
	
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-descricao');
        abas.addTab('justificativa', "Justificativa dos Recursos Solicitados");
        abas.addTab('integracao', "Integração do(s) Projetos de Pesquisa");
        abas.activate('justificativa');
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
YAHOO.ext.EventManager.onDocumentReady(Abas2.init, Abas2, true);
</script>

<script type="text/javascript">
	var ativo;

	function checkTipoDespesa(i){

			if (ativo){
				Element.removeClassName(ativo, 'selecionado');
			}

			var botao = $(i);
			Element.addClassName(botao, 'selecionado');
			ativo = botao;
			$('idElementoDispesa').value = i;

		    if (i == 33 || i == 38){
	                $('spanMaterialLicitado').style.display = '';
	                $('avisoMaterialEquipamento').style.display = '';               
	        }else{
	                $('form:checkMaterialLicitado').checked = false;
	                $('spanMaterialLicitado').style.display = 'none';
	                $('avisoMaterialEquipamento').style.display = 'none';
	        }
	}

</script>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
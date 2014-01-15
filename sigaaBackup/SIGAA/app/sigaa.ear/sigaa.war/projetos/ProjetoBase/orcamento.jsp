<%@page import="br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript">
<!--
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

//-->
</script>

<style>
<!--
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
	
-->
</style>

<script>

 function switchTexto() {
	 
	var elem = $('limite_recursos');
	var dpy = elem.style.display;
	if (dpy == "none" || dpy == "") {
		elem.style.display = "block";
		$('botao').src = "/sigaa/img/pesquisa/botao_menos.png";
	} else {
		elem.style.display = "none";
		$('botao').src = "/sigaa/img/pesquisa/botao_mais.png";
	}
 }

</script>


<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>

<h:messages showSummary="true" showDetail="true" />
<h2><ufrn:subSistema /> > Orçamento Detalhado</h2>

<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="40%">
				<ul>
					<li>
						Serão financiáveis os elementos de despesa desde que os dispêndios estejam comprovados e estritamente vinculados <br />
						à execução do projeto e sustentados nas definições metodológicas da proposta.
					</li>
		        	<li>
		        		As propostas deverão conter, obrigatoriamente, a discriminação dos itens a serem financiados.
		        	</li>
		        </ul>		        
			</td>
			<td>
				<%@include file="passos_projeto.jsp"%>
			</td>
		</tr>
	</table>
</div>


	<h:form id="form">	   
		<table class="formulario" width="90%">
			<caption>Selecione o Elemento de Despesa</caption>
			
			<tr>
				<td colspan="5">
					<input type="hidden" id="idElementoDispesa" name="idElementoDespesa" value="${idElementoDespesa}" />
						<div>
							<ul class="nav">
									<li><a href="#nb" id="34" onclick="checkTipoDespesa('34');"><img src="${ctx}/img/extensao/calendar_preferences.png" /><br/>Diárias</a></li>
									<li><a href="#nb" id="33" onclick="checkTipoDespesa('33');"><img src="${ctx}/img/extensao/shoppingcart_full.png" /> <br/>Material de Consumo</a></li>
									<li><a href="#nb" id="35" onclick="checkTipoDespesa('35');"><img src="${ctx}/img/extensao/passagens.png" /><br/>Passagens</a></li>							
									<li><a href="#nb" id="31" onclick="checkTipoDespesa('31');"><img src="${ctx}/img/extensao/pessoafisica.png" /><br/>Pessoa Física</a></li>
									<li><a href="#nb" id="29" onclick="checkTipoDespesa('29');"><img src="${ctx}/img/extensao/pessoajuridica.png"/><br/>Pessoa Jurídica</a></li>
									<li><a href="#nb" id="38" onclick="checkTipoDespesa('38');"><img src="${ctx}/img/extensao/pc.png"/><br/>Equipamentos</a></li>
							</ul>
						</div>
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
					<h:inputTextarea id="discriminacao" value="#{projetoBase.orcamento.discriminacao}" style="width: 95%" rows="2"/>
				</td>
			</tr>

			<tr>
				<th  class="required">Quantidade:</th>
				<td width="5%">
					<h:inputText id="quantidade" value="#{projetoBase.orcamento.quantidade}" size="8" style="text-align: right" 
						maxlength="10" onkeypress="return formataValor(this, event, 2);" label="Quantidade">
						<f:converter converterId="convertMoeda"/>
					</h:inputText>
				</td>
				<th class="required" width="15%">Valor Unitário(R$):</th>
				<td>
					<h:inputText id="valorUnitario" value="#{projetoBase.orcamento.valorUnitario}" size="12" maxlength="14" 
						onkeydown="return formataValor(this, event, 2)" style="text-align: right" label="Valor Unitário(R$)">
						<f:converter converterId="convertMoeda"/>
					</h:inputText>
				</td>
				
				<td>
				    <span id="spanMaterialLicitado" style="display: none;">
                    <h:selectBooleanCheckbox id="checkMaterialLicitado" value="#{projetoBase.orcamento.materialLicitado}" label="Material Licitado" />
                    Este material está licitado?  
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
					<h:commandButton	action="#{projetoBase.adicionarOrcamento}" value="Adicionar Despesa"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>

<br/>
	<div class="infoAltRem">	    			
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover<br/>
	</div>

			<h:form id="formLista">
					
						<table class="listagem">
								<caption class="listagem">Lista de Despesas Cadastradas (${fn:length(projetoBase.obj.orcamento)})</caption>
								<thead>
								<tr>
									<th>Descrição</th>
									<th>&nbsp;</th>
									<th style="text-align: right"  width="15%">Valor Unitário(R$) </th>
									<th style="text-align: right"  width="10%">Quant.</th>
									<th style="text-align: right" width="15%">Valor Total(R$)</th>
									<th>&nbsp;</th>
								</tr>
								</thead>

								<tbody>

									<c:if test="${not empty projetoBase.tabelaOrcamentaria}">
									
										<c:set value="${projetoBase.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
										<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">

												<tr  style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
													<td colspan="6">${ tabelaOrc.key.descricao }</td>
												</tr>
														<c:set value="#{tabelaOrc.value.orcamentos}" var="orcas" />
														<c:forEach items="#{orcas}" var="orca" varStatus="status">
															<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
																<td style="padding-left: 20px"> ${orca.discriminacao}</td>
                                                                <td align="right">${orca.materialLicitado ? '(Licitado)' : ''}</td>
																<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orca.valorUnitario}" type="currency" pattern="#,##0.00"/>  </td>
																<td align="right"> ${orca.quantidade}</td>
																<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orca.valorTotal}" type="currency" pattern="#,##0.00"/>  </td>
																<td width="2%" align="right">
																	<h:commandLink action="#{projetoBase.removeOrcamento}" title="Remover" onclick="return confirm('Deseja remover esta despesa?');" immediate="true" id="bt_remover_orcamento">
																		<f:param name="idOrcamentoDetalhado" value="#{orca.id}"/>
												                   		<h:graphicImage url="/img/delete.gif" />
																	</h:commandLink>
																</td>																
															</tr>
														</c:forEach>

												<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
													<td colspan="3"><b>SUB-TOTAL (${ tabelaOrc.key.descricao})</b></td>
													<td  align="right"><b><fmt:formatNumber value="${ tabelaOrc.value.quantidadeTotal }" maxFractionDigits="0"/></b></td>
													<td align="right"><b><fmt:formatNumber currencySymbol="R$  " value="${ tabelaOrc.value.valorTotalRubrica }" type="currency" pattern="#,##0.00" maxFractionDigits="2"/></b></td>
													<td align="right">&nbsp;</td>
												</tr>

												<tr>
													<td colspan="6">&nbsp;</td>
												</tr>

										</c:forEach>
									</c:if>

										<c:if test="${empty projetoBase.obj.orcamento}">
											<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
										</c:if>

								</tbody>
						</table>
						
			</h:form>


		<table class="formulario" width="100%">
			<tfoot>
				<tr>
					<td colspan="2">
					<h:form>
						<h:commandButton value="<< Voltar" action="#{projetoBase.passoAnterior}" id="btPassoAnteriorOrcamento"/>
						<h:commandButton value="Cancelar" action="#{projetoBase.cancelar}" onclick="#{confirm}" id="btCancelar"/>
						<h:commandButton value="Gravar e Avançar >>" action="#{projetoBase.submeterOrcamento}" id="btSubmeterOrcamento"/>
					</h:form>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript" language="javascript">
<!--
	var idElemento = ${sessionScope.idElementoDespesa};
	if(idElemento)
		checkTipoDespesa('${sessionScope.idElementoDespesa}');
 	else
 		checkTipoDespesa('34'); 		
 	
//-->
</script>
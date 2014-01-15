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

<f:view>
<h:messages showDetail="true"></h:messages>

		<h:outputText value="#{atividadeExtensao.create}" />
		
				

<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> > Orçamento Detalhado</h2>

<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="40%">
				<ul>
					<li>
						Serão financiáveis os elementos de despesa desde que <br />
						os dispêndios estejam comprovados e estritamente vinculados  <br />
						à execução da ação e sustentados nas definições metodológicas <br />
						da proposta.<br/>
					</li>
					<li>
				        FAEx não financia aquisição de equipamento e passagens para<br/>
				        apresentação de trabalhos em eventos acadêmicos (congressos,<br/>
				        seminários, colóquios, etc.),constituindo-­se em contrapartida<br/>
				        das Unidades Acadêmicas ou Administrativas da ${configSistema['siglaInstituicao']}.<br/>
				    </li>
		        	<li>
		        		As propostas deverão conter, obrigatoriamente, a discriminação dos <br/>
		        		 itens a serem financiados.<br/>
		        	</li>
		        </ul>
			</td>
			<td>
				<%@include file="passos_atividade.jsp"%>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<p><strong>OBSERVAÇÃO:</strong> Os dados informados só são cadastrados na base de dados quando clica-se em "Avançar >>".</p> 
			</td>
		</tr>
	</table>
</div>


	<h:form id="form">
		<table class="formulario" width="90%" >
			<caption class="listagem">Despesas</caption>
			<tr>
				<td colspan="5" class="descricaoOperacao">Selecione o Elemento de Despesa:</td>
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
					<h:inputTextarea id="discriminacao" value="#{atividadeExtensao.orcamento.discriminacao}" style="width: 95%" rows="2"/>
				</td>
			</tr>

			<tr>
				<th  class="required">Quantidade:</th>
				<td width="10%">
					<h:inputText id="quantidade" value="#{atividadeExtensao.orcamento.quantidade}" 
							size="20" style="text-align: right" maxlength="12" onkeypress="return formataValor(this, event, 2);">
						<f:converter converterId="convertMoeda"/>
					</h:inputText>
				</td>

				<th class="required" width="20%">Valor Unitário: R$</th>
				<td>
					<h:inputText id="valorUnitario" value="#{atividadeExtensao.orcamento.valorUnitario}" size="22" maxlength="14" 
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
					<h:commandButton	action="#{atividadeExtensao.adicionaOrcamento}" value="Adicionar Despesa"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

<br/>
	<div class="infoAltRem">	    			
	    			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Despesa<br/>
				</div>

			<h:form id="formLista">
					
						<table class="listagem">
								<caption class="listagem">Lista de Despesas Cadastradas</caption>
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

									<c:if test="${not empty atividadeExtensao.tabelaOrcamentaria}">
									
										<c:set value="${atividadeExtensao.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
										<c:set var="totalGeral" value="0" />
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
																<td align="right"> ${orca.quantidade}</td>
																<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orca.valorTotal}" type="currency"/>  </td>

																<td width="5%" align="right">
																	<h:commandLink action="#{atividadeExtensao.removeOrcamento}" title="Remover Despesa" onclick="return confirm('Deseja remover esta despesa?');" immediate="true" id="bt_remover_orcamento">
																		<f:param name="idOrcamentoDetalhado" value="#{orca.id}"/>
												                   		<h:graphicImage url="/img/delete.gif" />
																	</h:commandLink>
																</td>																
															</tr>
														</c:forEach>

												<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
													<td colspan="3"><b>SUB-TOTAL (${ tabelaOrc.key.descricao})</b></td>
													<td  align="right"><b>${ tabelaOrc.value.quantidadeTotal }</b></td>
													<td align="right"><b><fmt:formatNumber currencySymbol="R$  " value="${ tabelaOrc.value.valorTotalRubrica }" type="currency"/></b></td>
													<c:set var="totalGeral" value="${ totalGeral + tabelaOrc.value.valorTotalRubrica }" />
													<td align="right">&nbsp;</td>
												</tr>

												<tr>
													<td colspan="6">&nbsp;</td>
												</tr>

										</c:forEach>

										<tr style="background: #EEE; padding: 2px 0 2px 5px;">
											<td colspan="3"><b> TOTAL </b></td>
											<td  align="right"></td>
											<td align="right"><b><fmt:formatNumber currencySymbol="R$  " value="${ totalGeral }" type="currency"/></b></td>
											<td align="right">&nbsp;</td>
										</tr>

									</c:if>

										<c:if test="${empty atividadeExtensao.tabelaOrcamentaria}">
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
									<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" id="voltar"/>
									<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" id="cancelar"  onclick="#{confirm}"/>
									<h:commandButton value="Avançar >>" action="#{atividadeExtensao.submeterOrcamentoDetalhado}" id="avancar"/>
								</h:form>
								</td>
							</tr>
						</tfoot>
			</table>
		
			<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>





<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
<!--
	var idElemento = ${sessionScope.idElementoDespesa};
	if(idElemento)
		checkTipoDespesa('${sessionScope.idElementoDespesa}');
 	else
 		checkTipoDespesa('34'); 		
 	
//-->
</script>
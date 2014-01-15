<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Instituto de Ciência e Tecnologia</h2>
	<a4j:keepAlive beanName="institutoCienciaTecnologia" />
	<div class="descricaoOperacao">
		<p>
			Selecione a categoria do membro para realizar a busca de acordo com os critérios específicos.
		</p>
	</div>
	<h:form id="form" >
	<input type="hidden" name="categoriaMembro" id="categoriaMembro" value="${categoriaAtual}"/>
		<table class="formulario">
			<caption>Cadastro de Instituto de Ciência e Tecnologias</caption>
			<tr>
				<th class="obrigatorio" >Nome:</th>
				<td>
					<h:inputText maxlength="150" size="60" value="#{ institutoCienciaTecnologia.obj.nome }" readonly="#{institutoCienciaTecnologia.readOnly}" disabled="#{institutoCienciaTecnologia.readOnly}" />
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Volume de Recursos:</th>
				<td>
					<h:inputText value="#{ institutoCienciaTecnologia.obj.volumeRecursos }" readonly="#{institutoCienciaTecnologia.readOnly}" id="valumeRecurso" 
 						disabled="#{institutoCienciaTecnologia.readOnly}" onkeypress="return(formatarMascara(this,event,'#####.##'))" maxlength="8" />
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" >Período Inicial:</th>
				<td>
	    			 <t:inputCalendar value="#{ institutoCienciaTecnologia.obj.periodoInicio }" id="periodoInicial" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" 
	    				renderPopupButtonAsImage="true" readonly="#{ institutoCienciaTecnologia.readOnly }" 
	    				disabled="#{ institutoCienciaTecnologia.readOnly }">
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" >Período Final:</th>
				<td>
	    			 <t:inputCalendar value="#{ institutoCienciaTecnologia.obj.periodoFim }" id="periodoFinal" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" 
	    				renderPopupButtonAsImage="true" readonly="#{ institutoCienciaTecnologia.readOnly }" 
	    				disabled="#{ institutoCienciaTecnologia.readOnly }">
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Coordenador:</th>
				<td>
					<h:inputText value="#{ institutoCienciaTecnologia.obj.coordenador.pessoa.nome }" id="nomeCoordenador" size="59"/>
					<rich:suggestionbox for="nomeCoordenador" width="450" height="100" minChars="3" id="suggestionNomeCoord" 
						suggestionAction="#{ servidorAutoCompleteMBean.autocompleteNomeServidor }" var="_coord" 
						fetchValue="#{ _coord.pessoa.nome }">
					 
						<h:column>
							<h:outputText value="#{ _coord.pessoa.nome }" />
						</h:column>
					 
					  	<f:param name="apenasAtivos" value="true" />
			    	  	<f:param name="apenasDocentes" value="true" />

						<a4j:support event="onselect">
						  	<f:param name="apenasAtivos" value="true" />
				    	  	<f:param name="apenasDocentes" value="true" />
							<f:setPropertyActionListener value="#{ _coord.id }" target="#{ institutoCienciaTecnologia.obj.coordenador.id }" />
						</a4j:support>
					</rich:suggestionbox>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" >Unidade Federativa:</th>
				<td>
					<h:selectOneMenu value="#{ institutoCienciaTecnologia.obj.unidadeFederativa.id }" readonly="#{institutoCienciaTecnologia.readOnly}" disabled="#{institutoCienciaTecnologia.readOnly}">
					<f:selectItem itemLabel="--- Selecione ---" itemValue="0" />
					<f:selectItems
						value="#{institutoCienciaTecnologia.allUnidadesFederativas}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${institutoCienciaTecnologia.obj.id > 0}">
				<tr>
					<th>Ativo:</th>
					<td>
						<h:selectBooleanCheckbox
							value="#{ institutoCienciaTecnologia.obj.ativo }"
							readonly="#{institutoCienciaTecnologia.readOnly}"
							disabled="#{institutoCienciaTecnologia.readOnly}" />
					</td>
				</tr>
        	</c:if>
        	<tr>        	
				<td colspan="2" class="teste">
        			<table class="subFormulario" width="100%">
        			<caption> Membros do Instituto de Ciência e Tecnologia</caption>
        				<tr>
							<td colspan="2">
								<div id="tabs-membro">
									<div id="membro-docente" >
										<table width="100%">
											<tr>
												<th class="obrigatorio" width="26%">Docente:</th>
												<td nowrap="nowrap">
													<h:inputText id="nomeDocente" value="#{ institutoCienciaTecnologia.docente.pessoa.nome }" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.DOCENTE}" />
													<rich:suggestionbox for="nomeDocente" id="suggestion_docente"  width="430" height="100" minChars="3" 
													    suggestionAction="#{atividadeExtensao.autoCompleteNomeDocente}" var="_docente" fetchValue="#{_docente.siapeNome}" 
													    onsubmit="$('indicatorDocente').style.display='';" oncomplete="$('indicatorDocente').style.display='none';">
													  	
													  	<f:param name="apenasAtivos" value="true" />
											    	  	<f:param name="apenasDocentes" value="true" />
												      	
												      	<h:column>
													      	<h:outputText value="#{_docente.siapeNome}"/>
												      	</h:column>
												      	
														<a4j:support event="onselect">
														  	<f:param name="apenasAtivos" value="true" />
												    	  	<f:param name="apenasDocentes" value="true" />
															<f:setPropertyActionListener value="#{_docente.id }" target="#{ institutoCienciaTecnologia.docente.id }" />
														</a4j:support>
													</rich:suggestionbox>
													<img id="indicatorDocente" src="/sigaa/img/indicator.gif" style="display: none;">
												</td>
											</tr>
										</table>
									</div>
					
									<div id="membro-servidor">
										<table width="100%">
											<tr>
												<th class="obrigatorio" width="26%">Servidor:</th>
												<td nowrap="nowrap">
												
													<h:inputText id="nomeServidor" value="#{institutoCienciaTecnologia.servidor.pessoa.nome}" size="70" onfocus="$('categoriaMembro').value=#{categoriaMembro.SERVIDOR}" />
													<rich:suggestionbox id="suggestion_servidor"  width="430" height="100" minChars="3" 
													      for="nomeServidor" suggestionAction="#{atividadeExtensao.autoCompleteNomeServidorTecnico}" 
													      var="_servidor" fetchValue="#{_servidor.siapeNome}"
													      onsubmit="$('indicatorServidor').style.display='';" 
													      oncomplete="$('indicatorServidor').style.display='none';" 
													      reRender="indicatorServidor">
													
														<h:column>
															<h:outputText value="#{_servidor.siapeNome}" />
														</h:column>
													
														<a4j:support event="onselect">
														  	<f:param name="apenasAtivos" value="true" />
												    	  	<f:param name="apenasDocentes" value="true" />
															<f:setPropertyActionListener value="#{_servidor.id }" target="#{institutoCienciaTecnologia.servidor.id }" />
														</a4j:support>
													
													</rich:suggestionbox>
													<img id="indicatorServidor" src="/sigaa/img/indicator.gif" style="display: none;">
													<ufrn:help img="/img/ajuda.gif">Apenas os servidores do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
												</td>
											</tr>
										</table>
									</div>
					
									<div id="membro-discente">
										<table width="100%">
											<tr>
												<th class="obrigatorio" width="26%">Discente:</th>
												<td nowrap="nowrap">
													<h:inputText id="nomeDiscente" value="#{institutoCienciaTecnologia.discente.pessoa.nome}" size="70"
														onfocus="$('categoriaMembro').value=#{categoriaMembro.DISCENTE}" />
													<rich:suggestionbox id="suggestion_discente"  width="430" height="100" minChars="3" 
													      for="nomeDiscente" suggestionAction="#{atividadeExtensao.autoCompleteNomeDiscente}" 
													      var="_discente" fetchValue="#{_discente.matriculaNome}" 
													      onsubmit="$('indicatorDiscente').style.display='';" 
													      oncomplete="$('indicatorDiscente').style.display='none';" 
													      reRender="indicatorDiscente">

														<h:column>
															<h:outputText value="#{_discente.matriculaNome}" />
														</h:column>

														<a4j:support event="onselect">
															<f:setPropertyActionListener value="#{ _discente.id }" target="#{institutoCienciaTecnologia.discente.id }" />
														</a4j:support>

													</rich:suggestionbox>
													<img id="indicatorDiscente" src="/sigaa/img/indicator.gif" style="display: none;"> 
													<ufrn:help img="/img/ajuda.gif">Apenas os Discentes Ativos da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
												</td>
											</tr>
										</table>
									</div>
								</div>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio" width="21%">Data Início:</th>
							<td>
				    			 <t:inputCalendar value="#{ institutoCienciaTecnologia.membroEquipe.dataInicio }" id="dataInicio" size="10" maxlength="10" 
				    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" 
				    				renderPopupButtonAsImage="true" readonly="#{ institutoCienciaTecnologia.readOnly }" 
				    				disabled="#{ institutoCienciaTecnologia.readOnly }">
				      				<f:converter converterId="convertData"/>
								</t:inputCalendar> 
							</td>
						</tr>	
						<tr>
							<th>Data Fim:</th>
							<td>
				    			 <t:inputCalendar value="#{ institutoCienciaTecnologia.membroEquipe.dataFim }" id="dataFim" size="10" maxlength="10" 
				    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" 
				    				renderPopupButtonAsImage="true" readonly="#{ institutoCienciaTecnologia.readOnly }" 
				    				disabled="#{ institutoCienciaTecnologia.readOnly }">
				      				<f:converter converterId="convertData"/>
								</t:inputCalendar> 
							</td>
						</tr>
						<tfoot>
						<tr>
								<td colspan="2" align="center">
								<h:panelGroup id="botoes">
									<h:commandButton id="btnAdicionarMembro" value="Adicionar" action="#{institutoCienciaTecnologia.adicionarMembroEquipe}" rendered="#{!institutoCienciaTecnologia.alterar}" />
									<h:commandButton id="btnAlterarMembro" value="Alterar Membro" action="#{institutoCienciaTecnologia.adicionarMembroEquipe}" rendered="#{institutoCienciaTecnologia.alterar}" />
									<h:commandButton id="btnCancelarAlteracao" value="Cancelar Alteração de Membro" actionListener="#{institutoCienciaTecnologia.cancelarAlterarMembroEquipe}" rendered="#{institutoCienciaTecnologia.alterar}"/>
								</h:panelGroup>	
							</td>
						</tr>
						</tfoot>
					</table>
				</td>
			<tr>
				
				<td colspan="2" align="center">
					<c:if test="${not empty institutoCienciaTecnologia.obj.equipesInstitutoCienciaTecnologia}">
					<div class="infoAltRem">
							<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
				        	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
					</div>
					</c:if>
				</td>
				
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">
					Lista de Membros do Instituto de Ciência e Tecnologia
				</td>
			</tr>
			<c:if test="${ empty institutoCienciaTecnologia.obj.equipesInstitutoCienciaTecnologia}">
				<tr>
					<td colspan="2">
						<p style="text-align: center; font-style: italic; padding: 5px;">
							<h:outputText value="   Não existem membros nesse Instituto." />
						</p>
					</td>
				</tr>
			</c:if>
			<c:if test="${not empty institutoCienciaTecnologia.obj.equipesInstitutoCienciaTecnologia}">
			<tr>
				<td colspan="2">
					<input type="hidden" name="idMembro" value="0" id="idMembro"/>
					<input type="hidden" name="posicao" value="-1" id="posicao"/>
					
					<t:dataTable id="dt_membro_equipe" value="#{institutoCienciaTecnologia.obj.equipesInstitutoCienciaTecnologia}" var="membro" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="indice">
						<t:column>
							<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
							<h:outputText value="#{membro.servidor.pessoa.nome}" rendered="#{empty membro.pessoa && membro.categoriaMembro.id == 1}" id="nome_docente" />
							<h:outputText value="#{membro.pessoa.nome}" rendered="#{not empty membro.pessoa}" id="nome_pessoa" />
						</t:column>
						<t:column>
							<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
							<h:outputText value="#{membro.categoriaMembro.descricao}" rendered="#{not empty membro.categoriaMembro}" id="categoria" />
						</t:column>
						<t:column>
							<f:facet name="header"><f:verbatim>Data Início</f:verbatim></f:facet>
							<h:outputText value="#{membro.dataInicio}" rendered="#{not empty membro.dataInicio}" id="data_ini" />
						</t:column>
						<t:column>
							<f:facet name="header"><f:verbatim>Data Fim</f:verbatim></f:facet>
							<h:outputText value="#{membro.dataFim}" rendered="#{not empty membro.dataFim}" id="data_fim" />
						</t:column>
						<t:column>
							<h:commandButton image="/img/delete.gif" actionListener="#{institutoCienciaTecnologia.removeMembroEquipe}" id="bt_remove"
								alt="Remover membro" title="Remover membro"  
								onclick="$(idMembro).value=#{membro.id};$(posicao).value=#{indice};return confirm('Deseja Remover este Membro do Grupo de Pesquisa?')"/>

							<h:commandLink actionListener="#{institutoCienciaTecnologia.popularMembroEquipe}" id="bt_alterar" title="Alterar membro">
                    			<f:param name="id" value="#{membro.id}"/>
                    			<h:graphicImage url="/img/alterar.gif"/>
							</h:commandLink>
						</t:column>	
				</t:dataTable>

				</td>
			</tr>
        	</c:if>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden value="#{institutoCienciaTecnologia.obj.id}" /> 
						<h:commandButton value="#{institutoCienciaTecnologia.confirmButton}" action="#{institutoCienciaTecnologia.persistir}" /> 
						<h:commandButton value="Cancelar" action="#{institutoCienciaTecnologia.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-membro');
	        tabs.addTab('membro-docente', "Docente")
	        tabs.addTab('membro-servidor', "Servidor Técnico-Administrativo");
	        tabs.addTab('membro-discente', "Discente");

  		        tabs.activate('membro-docente');	////padrão

  		      <c:if test="${sessionScope.aba != null}">
			    	tabs.activate('${sessionScope.aba}');
		 </c:if>

	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
	
	var verificaEstrangeiro = function() {
		if ( $('equipe:checkEstrangeiro').checked ) {
			$('equipe:cpfExterno').disable();
			$('equipe:nomeExterno').enable();
			$('equipe:sexoExterno').enable();
			$('equipe:cpfExterno').value = "";
			$('equipe:nomeExterno').value = "";
		} else {
			$('equipe:cpfExterno').enable();
			$('equipe:nomeExterno').disable();
			$('equipe:sexoExterno').disable();
		}
	}
	
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

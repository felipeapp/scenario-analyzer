<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<a4j:keepAlive beanName="gerenciarParticipantesExtensaoByGestorExtensaoMBean" />
	
	<h2><ufrn:subSistema /> &gt; Gerenciar Participantes  &gt; Lista de Participantes </h2>
	
		<h:form id="formListaParticipantesGerenciar">
		
			<div class="descricaoOperacao">
					<p> Caro(a) Gestor(a) de Extensão, </p>
					<p> Abaixo são apresentadas os participantes das atividades ou mini atividade de extensão selecionada. </p>
					<br/>
					<p> <strong>ATENÇÃO:</strong> </p>
					<br/>
	    			<p> A emissão do certificado de cada participante só será autorizada quando as seguintes condições forem atingidas: </p>
	    			<ol>
	    				<li>A ação de extensão estiver finalizada <strong>e</strong> o projeto concluído <strong>ou</strong> o gestor autorizou a emissão antes de término da ação.</li>
	    				<li>O participante deverá ter frequência satisfatória.</li>
	    				<li>O participante deverá ter a emissão do certificado autorizada pela coordenação da ação. (válido para emissão pelo próprio participante) </li>
					</ol>
					
					<p> A emissão da declaração de cada participante só será autorizada quando as seguintes condições forem atingidas: </p>
					<ol>
	    				<li>A ação de extensão <strong>não</strong> estiver finalizada <strong>e</strong> o projeto <strong>não</strong> estiver concluído.</li>
	    				<li>O participante deverá ter a emissão da declaração autorizada pela coordenação da ação. (válido para emissão pelo próprio participante)</li>
					</ol>
			</div>
			
			<table class="formulario" style="margin-bottom: 20px; width: 70%;">
				<caption> Filtros </caption>
				
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.checkBuscaNome}"  id="checkBuscaNome" styleClass="checkNaoMarcar"  />
					</td>
					<td>Nome:</td>
					<td colspan="2"> 
						<h:inputText id="nome" value="#{ gerenciarParticipantesExtensaoByGestorExtensaoMBean.buscaNome }" size="60"  maxlength="60"
							onchange="javascript:$('formListaParticipantesGerenciar:checkBuscaNome').checked = true;" onfocus="javascript:$('formListaParticipantesGerenciar:checkBuscaNome').checked = true;"/>
					</td>
				</tr>
				
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.checkBuscaMunicipio}"  id="checkBuscaMunicipio" styleClass="checkNaoMarcar" />
					</td>
					<td>UF:</td>
					<td colspan="2">
						<table>		
							<tr>
								<td style="padding: 0px;">
									<h:selectOneMenu value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.idUnidadeFederativa}" id="uf" immediate="true">
										<f:selectItem itemLabel="-- Selecione -- " itemValue="-1" />
										<f:selectItems value="#{unidadeFederativa.allCombo}" />
										<a4j:support event="onchange" reRender="municipio" action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.carregarMunicipios}"/>
									</h:selectOneMenu>
								</td>
								<td>Município:</td>
								<td>
									<h:selectOneMenu value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.idMunicipio}" id="municipio" 
											onchange="javascript:$('formListaParticipantesGerenciar:checkBuscaMunicipio').checked = true;" onfocus="javascript:$('formListaParticipantesGerenciar:checkBuscaMunicipio').checked = true;">
										<f:selectItem itemLabel="-- Selecione -- " itemValue="-1" />
										<f:selectItems value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.municipiosComBox}" />
									</h:selectOneMenu>
								</td>
							<tr>
						</table>
					</td>
				</tr>
				
				
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.participantesSemFrequencia}" styleClass="checkNaoMarcar" id="checkFiltraSemFrequencia"/>
					</td>
					<th colspan="3" style="text-align:left;">Sem frequência</th>
				</tr>	
				
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.participantesNaoDeclaracao}" styleClass="checkNaoMarcar" id="checkFiltraNaoDeclaracao"/>
					</td>
					<th colspan="3" style="text-align:left;">Não autorizado declaração</th>
				</tr>	
				
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.participantesNaoCertificados}" styleClass="checkNaoMarcar" id="checkFiltraNaoCertificado"/>
					</td>
					<th colspan="3" style="text-align:left;">Não autorizado certificado</th>
				</tr>
				
				<tr>
					<td></td>
					<th style="text-align:left">Participantes por página:</th>
					<td colspan="3">
						<h:selectOneMenu value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.tamanhoPagina}">
							<f:selectItem itemLabel="50" itemValue="50"/>
							<f:selectItem itemLabel="100" itemValue="100"/>
							<f:selectItem itemLabel="200" itemValue="200"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="4">
							<h:commandButton value="Filtrar" action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.filtarParticipantesAtividade}" /> 
						</td>
					</tr>
				</tfoot>
			</table>
			
			
			
			
			<c:if test="${gerenciarParticipantesExtensaoByGestorExtensaoMBean.gerenciandoParticipantesAtividade}">
				<table class="visualizacao" style="width: 80%; margin-bottom: 20px;">
					<caption> Atividade </caption>
					<tr>
						<th>Código:</th>
						<td><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.atividadeSelecionada.codigo}" /></td>
					</tr>
					<tr>
						<th>Atividade:</th>
						<td><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.atividadeSelecionada.titulo}" /></td>
					</tr>
					<tr>
						<th>Coordenação:</th>
						<td><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.atividadeSelecionada.projeto.coordenador.pessoa.nome}" /></td>
					</tr>
					<tr>
						<th>Período:</th>
						<td><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.atividadeSelecionada.dataInicio}" > 
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText> 
							até 
							<h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.atividadeSelecionada.dataFim}" >
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText> 
						</td>
					</tr>
				
				</table>
			</c:if>
			
			<c:if test="${! gerenciarParticipantesExtensaoByGestorExtensaoMBean.gerenciandoParticipantesAtividade}">
				<table class="visualizacao" style="width: 80%; margin-bottom: 20px;">
					<caption> Mini Atividade </caption>
					<tr>
						<th>Código:</th>
						<td><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.subatividadeSelecionada.atividade.codigo}" /></td>
					</tr>
					<tr>
						<th>Atividade:</th>
						<td><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.subatividadeSelecionada.atividade.titulo}"  /></td>
					</tr>
					<tr>
						<th>Mini Atividade:</th>
						<td><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.subatividadeSelecionada.titulo}"  /></td>
					</tr>
					<tr>
						<th>Coordenação:</th>
						<td><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.subatividadeSelecionada.atividade.projeto.coordenador.pessoa.nome}" /></td>
					</tr>
					<tr>
						<th>Período:</th>
						<td><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.subatividadeSelecionada.inicio}" > 
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText> 
							até 
							<h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.subatividadeSelecionada.fim}" >
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText> 
						</td>
					</tr>
				
				</table>
			</c:if>
			
			
			
			
			
			
			
			<rich:contextMenu attached="false" id="menuOpcoesParticipantes" hideDelay="300" >
				
				<rich:menuItem value="Visualizar Participante" icon="/img/extensao/user1_view.png" action="#{gerenciarInscritosCursosEEventosExtensaoMBean.visualizarDadosParticipante}">	
						<f:param name="idCadastroParticipante" value="{_id_cadastro_participante_context_menu}"/>		
		        </rich:menuItem>
		        
		        <%-- 
		        <rich:menuItem value="Alterar Participante" icon="/img/extensao/user1_refresh.png" action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.preAlterarParticipante}">	
						<f:param name="idParticipante" value="{_id_participante_context_menu}"/>		
		        </rich:menuItem>
		        
		      
		        <rich:menuItem value="Remover Participante" icon="/img/extensao/user1_delete.png" action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.preRemoverParticipanteAtividade}">	
						<f:param name="idParticipante" value="{_id_participante_context_menu}"/>		
		        </rich:menuItem>
		        --%>
		        
			</rich:contextMenu>
			
			
			
			
			
			
			<div class="infoAltRem">
			
				<%-- 
				<h:graphicImage value="/img/monitoria/user1_add.png" style="overflow: visible;" styleClass="noborder" />
				<h:commandLink value="Adicionar Novo Participante" action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.preCadastrarNovoParticipante}" />
				--%>
			
				<h:graphicImage value="/img/pesquisa/view.gif" height="16" width="16" 
							style="overflow: visible;" styleClass="noborder" />: Emitir declaração
				<h:graphicImage value="/img/certificate.png" height="16" width="16" 
							style="overflow: visible;" styleClass="noborder" />: Emitir certificado
							
				<h:graphicImage value="/img/submenu.png" height="16" width="16" 
							style="overflow: visible;" styleClass="noborder" />: Opções
			</div>
			
			
			
			
			
			<div style="text-align: center;">
				<h:commandButton image="/img/voltar.gif"
				 	action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.previousPage}"
					rendered="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.paginaAtual > 0 }"
					style="vertical-align:middle" id="paginacaoVoltarHeader" >
				</h:commandButton> 
					
				<b><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.paginaAtual +1} de #{gerenciarParticipantesExtensaoByGestorExtensaoMBean.totalPaginas}"></h:outputText></b>
				
				<h:commandButton image="/img/avancar.gif"
						action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.nextPage}"
						rendered="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.paginaAtual < (gerenciarParticipantesExtensaoByGestorExtensaoMBean.totalPaginas - 1)}"
						style="vertical-align:middle" id="paginacaoAvancarHeader" >
				</h:commandButton> 
				<br/>
				<br />
			</div>
			
			
			
			
			
			<table id="tabelaAtividades"  class="listagem" style="width: 100%;">
	
					<caption> Lista de Participantes </caption>
					<thead>
						<tr>
							<th></th>
							<th><h:selectBooleanCheckbox value="false" onclick="checkAllParticipantes(this)"/></th>
							<th>CPF</th>
							<th>Passaporte</th>
							<th>Nome</th>
							<th class="centralizado">Participação</th>
							<th class="centralizado">Freq.</th>
							<th class="centralizado">Declaração</th>
							<th class="centralizado">Certificado</th>
							<th></th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						
						<c:if test="${gerenciarParticipantesExtensaoByGestorExtensaoMBean.qtdParticipantes > 0 }">
							
							<c:forEach items="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.participantes}" var="participante" varStatus="count">
								<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
									
									<td>${count.count}-</td>
									
									<td><h:selectBooleanCheckbox value="#{participante.selecionado}" styleClass="check_#{participante.id}"/></td>
									
									<td><ufrn:format type="cpf_cnpj" valor="${participante.cadastroParticipante.cpf}" /></td>
									<td>${participante.cadastroParticipante.passaporte}</td>
									
									<td>${participante.cadastroParticipante.nome}</td>
									<td class="centralizado">${participante.tipoParticipacao.descricao}</td>
									
									<td class="centralizado">
										<h:inputText label="Frequência" value="#{participante.frequencia}" maxlength="3" size="3" onkeyup="formatarInteiro(this);" 
												onblur="javascript: if(this.value && this.value.valueOf()>100) this.value = '100';" converter="#{ intConverter }"
												onchange="javascript:document.getElementsByClassName('check_#{participante.id}')[0].checked = true;"/>
										<b>%</b>
									</td>
									
									<td>
		                                 <h:selectOneMenu value="#{participante.autorizacaoDeclaracao}" id="declaracaoAutorizada" 
		                                 	rendered="#{acesso.coordenadorExtensao && participante != null  }" 
		                                 	onchange="javascript:document.getElementsByClassName('check_#{participante.id}')[0].checked = true;">
		                                     <f:selectItem itemValue="true" itemLabel="SIM" />
		                                     <f:selectItem itemValue="false" itemLabel="NÃO" />
		                                 </h:selectOneMenu>
			                        </td>
			                        
			                        <td>
		                                 <h:selectOneMenu value="#{participante.autorizacaoCertificado}" id="certificadoAutorizado" 
		                                 	rendered="#{acesso.coordenadorExtensao && participante != null  }"
		                                 	onchange="javascript:document.getElementsByClassName('check_#{participante.id}')[0].checked = true;">
		                                     <f:selectItem itemValue="true" itemLabel="SIM" />
		                                     <f:selectItem itemValue="false" itemLabel="NÃO" />
		                                 </h:selectOneMenu>
			                        </td>
									
									<td width="2%">
										<h:commandLink title="Emitir declaração" style="border: 0;" id="emitirDeclaracao"
												action="#{declaracaoExtensaoMBean.emitirDeclaracaoParticipante}">
											
											<f:setPropertyActionListener target="#{declaracaoExtensaoMBean.participante.id}" value="#{participante.id}" />
											<f:setPropertyActionListener target="#{declaracaoExtensaoMBean.isEmissaoByCoordenador}" value="#{true}" />
											
								    	    <h:graphicImage url="/img/pesquisa/view.gif" height="16" width="16" />
										</h:commandLink>
									</td>
										
									<td width="2%">
										<h:commandLink title="Emitir certificado" style="border: 0;" id="emitirCertificado"
												action="#{certificadoExtensaoMBean.emitirCertificadoParticipante}">
											
											<f:setPropertyActionListener target="#{certificadoExtensaoMBean.participante.id}" value="#{participante.id}" />
											<f:setPropertyActionListener target="#{certificadoExtensaoMBean.isEmissaoByCoordenador}" value="#{true}" />
											
											<f:param name="isEmissaoByCoordenador" value="true" />
							            	<h:graphicImage url="/img/certificate.png" height="16" width="16" />
										</h:commandLink>
									</td>
									
									<td width="2%"> <%-- Demais opções a serem realizadas sobre o participante --%>	
										<h:graphicImage value="/img/submenu.png" title="Opções">
											<rich:componentControl event="onmouseover" for="menuOpcoesParticipantes" operation="show">
										        <f:param value="#{participante.id}" name="_id_participante_context_menu"/>
										        <f:param value="#{participante.cadastroParticipante.id}" name="_id_cadastro_participante_context_menu"/>
										    </rich:componentControl>
										</h:graphicImage>
									</td>
									
								</tr>
							</c:forEach>
							
						</c:if>
						<c:if test="${gerenciarParticipantesExtensaoByGestorExtensaoMBean.qtdParticipantes == 0 }">
							<tr>
								<td colspan="10" style="color: red; text-align: center;">Não existem participantes para a atividade selecionada </td>
							</tr>
						</c:if>
						
					</tbody>
				
				<tfoot>
					<tr>
						<td align="center" colspan="12">
							<%-- gestores por enquanto não podem altera nada, apenas emitir o certificado. 
							<h:commandButton value="Salvar Alterações" id="btnConfirmarAlteracoes" actionListener="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.atualizarInformacoesEmtirDeclaracoesParticipantes}" />
							--%>
							<h:commandButton value="Cancelar" action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.telaListaCursosEventosParaGerenciarParticipantesByGestorExtensao}" immediate="true" /> 
						</td>
					</tr>
				</tfoot>
				
			</table>			
			
			
			<div style="text-align: center;">
				<h:commandButton image="/img/voltar.gif"
					rendered="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.paginaAtual > 0 }"
					 action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.previousPage}" 
					style="vertical-align:middle" id="paginacaoVoltarFooter" >
				</h:commandButton>	 
					
				<b><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.paginaAtual +1} de #{gerenciarParticipantesExtensaoByGestorExtensaoMBean.totalPaginas}"></h:outputText></b>
				
				<h:commandButton image="/img/avancar.gif"
						action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.nextPage}"
						rendered="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.paginaAtual < (gerenciarParticipantesExtensaoByGestorExtensaoMBean.totalPaginas - 1)}"
						style="vertical-align:middle" id="paginacaoAvancarFooter">
				</h:commandButton>		
				
				<br />
				<br />
				<em><h:outputText value="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.totalRegistros }" /> Registro(s) Encontrado(s)</em>
			</div>
			
		</h:form>
	
</f:view>


<script type="text/javascript">

function checkAllParticipantes(chk){
   for (i=0; i<document.formListaParticipantesGerenciar.elements.length; i++){
	  
	  elemento = document.formListaParticipantesGerenciar.elements[i];
     
	  if(elemento.type == "checkbox" && elemento.className != "checkNaoMarcar"){
          document.formListaParticipantesGerenciar.elements[i].checked = chk.checked;
      }
	}
}

</script>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>
	
	
	<a4j:keepAlive beanName="buscaPadraoProjetosDeExtensaoMBean"/>
	
	<%-- Sempre que um novo caso de uso chamar a busca padr�o de participantes, colocar os MBens dos casos de uso aqui. --%>
	
	<a4j:keepAlive beanName="gerenciarParticipantesExtensaoByGestorExtensaoMBean"/>
	<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean"/>
	
	
	
	<h2>
		<ufrn:subSistema/> > Busca Projetos Extens�o > ${buscaPadraoProjetosDeExtensaoMBean.operacao}
	</h2>
	
	
	<div class="descricaoOperacao">
		<h:outputText escape="false" value="#{buscaPadraoProjetosDeExtensaoMBean.descricaoOperacao}"/>
	</div>
	
	
	<h:form id="formBuscaPadraoParticipantesExtensao">
	
		<table class="formulario" style="width: 80%; margin-bottom: 20px;">
			
			<caption>Filtros da Busca</caption>
			<tbody>
					
					<tr>
						<td> <h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder" /> </td>
				    	<td> <label for="titulo" > T�tulo da A��o: </label> </td>
				    	<td> <h:inputText id="buscaTitulo" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaNomeAtividade}" style="width:90%"
				    		onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaTitulo').checked = true;" />
				    	</td>
				    </tr>
			
					<tr>
						<td> <h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaAno}" id="selectBuscaAno" styleClass="noborder" /> </td>
				    	<td> <label for="ano"> Ano: </label> </td>
				    	<td> <h:inputText id="buscaAno" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaAno}" maxlength="4" size="4" 
				    		onkeyup="return formatarInteiro(this)" onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaAno').checked = true;" />
				    	</td>
				    </tr>
				    
				    <tr>
						<td> <h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaCodigo}" id="selectBuscaCodigo" styleClass="noborder" /> </td>
				    	<td> <label for="ano"> C�digo: </label> </td>
				    	<td>
				    	     <h:inputText id="buscaCodigo" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaCodigo}" size="10" alt="" 
				    	     	onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaCodigo').checked = true;" />
				    	     <ufrn:help img="/img/ajuda.gif">
				    	     	O c�digo de uma a��o de extens�o � composto por no m�nimo dez caracteres.
				    	     	Os dois primeiros d�gitos identificam o tipo da a��o. Os n�meros  seguintes e que
				    	     	antecedem o caractere '-' representam o n�mero 'sequ�ncia' da a��o. Por fim, os
				    	     	�ltimos quatro d�gitos representam o ano da a��o. Por exemplo, o c�digo da a��o
				    	     	PD002-2009 informa que a a��o � um produto(PD) e que foi a segunda a��o do tipo produto(002)
				    	     	criada no ano de 2009. Existem c�digos ainda onde a sequ�ncia � representada tr�s
				    	     	caracteres: xxx. Por exemplo, PJxxx-2008. Neste caso, o c�digo da a��o n�o foi
				    	     	gerado completamente e ele poder� n�o ser �nico. Uma busca para este c�digo retornaria
				    	     	todos os projetos(PJ) de 2008.
				    	     </ufrn:help>
				    	</td>
				    	
				    </tr>
			
					<tr>
						<td> <h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaPeriodo}" id="selectBuscaPeriodo" styleClass="noborder" /> </td>
				    	<td> <label for="periodo"> Per�odo de execu��o: </label> </td>
				    	<td>
				    	
				    	<t:inputCalendar value="#{buscaPadraoProjetosDeExtensaoMBean.buscaInicio}"
							renderAsPopup="true" renderPopupButtonAsImage="true"
							popupDateFormat="dd/MM/yyyy" size="10" id="dataInicio"
							onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaPeriodo').checked = true;" 
							onkeypress="return(formatarMascara(this,event,'##/##/####'));"
							maxlength="10" popupTodayString="Hoje �">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
						a 
						<t:inputCalendar value="#{buscaPadraoProjetosDeExtensaoMBean.buscaFim}"
							renderAsPopup="true" renderPopupButtonAsImage="true"
							popupDateFormat="dd/MM/yyyy" size="10" id="dataFim"
							onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaPeriodo').checked = true;"
							onkeypress="return(formatarMascara(this,event,'##/##/####'));"
							maxlength="10" popupTodayString="Hoje �">
							<f:converter converterId="convertData" />
						</t:inputCalendar>	
				    	</td>
				    </tr>
			
					<tr>
						<td> <h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaPeriodoConclusao}" id="selectBuscaPeriodoConclusao" styleClass="noborder" /> </td>
				    	<td> <label for="periodoConclusao">Per�odo de conclus�o: </label> </td>
				    	<td>
				    	
				    	<t:inputCalendar value="#{buscaPadraoProjetosDeExtensaoMBean.buscaInicioConclusao}"
							renderAsPopup="true" renderPopupButtonAsImage="true"
							popupDateFormat="dd/MM/yyyy" size="10" id="dataInicioConclusao"
							onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaPeriodoConclusao').checked = true;"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							maxlength="10" popupTodayString="Hoje �">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
						a 
						<t:inputCalendar value="#{buscaPadraoProjetosDeExtensaoMBean.buscaFimConclusao}"
							renderAsPopup="true" renderPopupButtonAsImage="true"
							popupDateFormat="dd/MM/yyyy" size="10" id="dataFimConclusao"
							onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaPeriodoConclusao').checked = true;"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							maxlength="10" popupTodayString="Hoje �">
							<f:converter converterId="convertData" />
						</t:inputCalendar>	
				    	</td>
				    </tr>
				    
				    <tr>
			            <td> <h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaPeriodoInicio}" id="selectBuscaPeriodoInicio" styleClass="noborder" /> </td>
			            <td> <label for="periodoInicio">Per�odo de in�cio: </label> </td>
			            <td>            
				            <t:inputCalendar value="#{buscaPadraoProjetosDeExtensaoMBean.buscaInicioExecucao}"
				                renderAsPopup="true" renderPopupButtonAsImage="true"
				                popupDateFormat="dd/MM/yyyy" size="10" id="dataInicioExecucao"
				                onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaPeriodoInicio').checked = true;"
				                onkeypress="return(formatarMascara(this,event,'##/##/####'))"
				                maxlength="10" popupTodayString="Hoje �">
				                <f:converter converterId="convertData" />
				            </t:inputCalendar>
				            a 
				            <t:inputCalendar value="#{buscaPadraoProjetosDeExtensaoMBean.buscaFimExecucao}"
				                renderAsPopup="true" renderPopupButtonAsImage="true"
				                popupDateFormat="dd/MM/yyyy" size="10" id="dataFimExecucao"
				                onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaPeriodoInicio').checked = true;"
				                onkeypress="return(formatarMascara(this,event,'##/##/####'))"
				                maxlength="10" popupTodayString="Hoje �">
				                <f:converter converterId="convertData" />
				            </t:inputCalendar>  
			            </td>
			        </tr>
			
					<tr>
						<td> <h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaEdital}" id="selectBuscaEdital" styleClass="noborder"/> </td>
				    	<td> <label for="edital"> Edital: </label> </td>
				    	<td>	    	
				    	 	<h:selectOneMenu id="buscaEdital" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaEdital}" 
				    	 		onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaEdital').checked = true;">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
				    	 		<f:selectItems value="#{editalExtensao.allCombo}" />
				    	 	</h:selectOneMenu>	    	 
				    	 </td>
				    </tr>
			
					<tr>
						<td><h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaTipoAtividade}" 
								id="selectBuscaTipoAtividade" styleClass="noborder"/> </td>
				    	<td> <label for="tipoAcao"> Tipo da A��o: </label> </td>
				    	<td>
				    	 	<h:selectManyListbox id="buscaTipoAcao" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaTipoAtividade}" size="4"
				    	 		onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaTipoAtividade').checked = true;">
				    	 		<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
				    	 	</h:selectManyListbox>
				    	 	<ufrn:help img="/img/ajuda.gif">
				    	 		Poder� ser marcada mais de uma op��o, basta 
				    	 		apenas segurar a tecla "Ctrl" e clicar nas op��es.
				    	 		Role para baixo para mais op��es.
				    	     </ufrn:help>   	 
				    	 </td>
				    </tr>
			
					<tr>
						<td><h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaAreaCNPq}" 
								id="selectBuscaAreaCNPq" styleClass="noborder"/> </td>
				    	<td> <label for="areaCNPQ"> �rea do CNPq: </label> </td>
				    	<td>
					    	<h:selectOneMenu id="buscaAreaCNPq" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaAreaCNPq}" 
					    		onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaAreaCNPq').checked = true;"> 
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{area.allGrandesAreasCombo}"/>
							</h:selectOneMenu>
				    	 </td>
				    </tr>
			
					<tr>
						<td><h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaUnidadeProponente}" 
								id="selectBuscaUnidadeProponente" styleClass="noborder"/> </td>
				    	<td> <label for="unidade"> Unidade Proponente: </label> </td>
				    	<td>
							<h:selectOneMenu id="buscaUnidade"
								value="#{buscaPadraoProjetosDeExtensaoMBean.buscaUnidade}"
								onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaUnidadeProponente').checked = true;"
								readonly="#{buscaPadraoProjetosDeExtensaoMBean.readOnly}" style="width: 90%">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{unidade.unidadesProponentesProjetosCombo}" />
							</h:selectOneMenu></td>
				    </tr>
			
			   		<tr>
			   			<td>
							<h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaCentro}" id="selectBuscaCentro" styleClass="noborder" />
						</td>
						<td> <label for="centro">Centro da A��o:</label></td>
						<td>
							<h:selectOneMenu id="buscaCentro" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaCentro}" style="width: 300px" 
								onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaCentro').checked = true;">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
								<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
							</h:selectOneMenu>
						</td>
					</tr>
			
					<tr>
						<td><h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaAreaTematicaPrincipal}" 
								id="selectBuscaAreaTematicaPrincipal" styleClass="noborder"/>  
						</td>
				    	<td> <label for="unidade"> �rea Tem�tica: </label> </td>
				    	<td>
							<h:selectOneMenu id="buscaAreaTematica" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaAreaTematicaPrincipal}" 
								onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaAreaTematicaPrincipal').checked = true;">
								<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
								<f:selectItems value="#{areaTematica.allCombo}" />
							</h:selectOneMenu>
				    	</td>
				    </tr>
				    
				    
				    
				    <tr>
						<td>
							<h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaProjetoAssociado}" 
								id="selectBuscaAssociado" styleClass="noborder" />
						</td>
				    	<td> 
				    		Dimens�o Acad�mica: 
				    	</td>
				    	<td>
				    		<h:selectOneMenu value="#{buscaPadraoProjetosDeExtensaoMBean.buscaProjetoAssociado}" id="associado"
				    			onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaAssociado').checked = true;">
				    			<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItem itemValue="1" itemLabel="PROJETO ASSOCIADO" />
								<f:selectItem itemValue="2" itemLabel="PROJETO ISOLADO" />
			 			 	</h:selectOneMenu>
				    	 </td>
				    </tr>
				    
			
					<tr>
						<td><h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaServidor}" 
								id="selectBuscaServidor" styleClass="noborder" />
						</td>
						<td><label onclick="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaServidor').checked = !$('formBuscaPadraoParticipantesExtensao:selectBuscaServidor').checked;">Servidor:</label></td>
						
						<td>
							<h:inputText value="#{buscaPadraoProjetosDeExtensaoMBean.membroEquipe.servidor.pessoa.nome}" id="nomeServidor" size="59"
							 onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaServidor').checked = true;"/>
							<rich:suggestionbox for="nomeServidor" width="450" height="100" minChars="3" id="suggestionNomeServ" 
									suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_serv" 
									fetchValue="#{_serv.pessoa.nome}">
									 
								<h:column>
									<h:outputText value="#{_serv.pessoa.nome}" />
								</h:column>
							 
						        <f:param name="apenasAtivos" value="false" />
						        <a4j:support event="onselect" >
							        <f:param name="apenasAtivos" value="false" />
									<f:setPropertyActionListener value="#{_serv.id}" target="#{buscaPadraoProjetosDeExtensaoMBean.membroEquipe.servidor.id}" />
							    </a4j:support>
							</rich:suggestionbox>
						</td>
					</tr>
			
					<c:if test="${acesso.extensao}">
						<tr>
							<td><h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaSituacaoAtividade}" 
									id="selectBuscaSituacaoAtividade" styleClass="noborder" />  
							</td>
					    	<td> <label for="situacaoProjeto"> Situa��o da A��o: </label> </td>
					    	<td>
					    		<h:selectManyListbox id="buscaSituacao" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaSituacaoAtividade}" size="4" 
					    			onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaSituacaoAtividade').checked = true;">
					    	 		<f:selectItems value="#{atividadeExtensao.tipoSituacaoAtividadeCombo}" />
				 			 	</h:selectManyListbox>
				 			 	<ufrn:help img="/img/ajuda.gif">
									Poder� ser marcada mais de uma op��o, basta	apenas
									segurar a tecla "Ctrl" e clicar nas op��es.
					    	 		Role para baixo para mais op��es.
				    	     	</ufrn:help> 
					    	 </td>
					    </tr>
			
						<tr>
							<td><h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaRegistroSimplificado}" 
									id="selectBuscaRegistroSimplificado" styleClass="noborder"/>  
							</td>
					    	<td> <label for="tipoRegistro"> Tipo de Registro: </label> </td>
					    	<td>
								<h:selectOneMenu id="buscaTipoRegistro" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaRegistroSimplificado}" 
					    			onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaRegistroSimplificado').checked = true;">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
									<f:selectItem itemLabel="REGISTRO SIMPLIFICADO DE A��O ANTERIOR" itemValue="1" />
									<f:selectItem itemLabel="PROPOSTA COMPLETA DE A��O DE EXTENS�O" itemValue="2" />
				 				</h:selectOneMenu>
					    	 </td>
					    </tr>
					    
					    
					    <tr>
							<td><h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaAcaoSolicitacaoRenovacao}"	id="selectBuscaAcaoSolicitacaoRenovacao" styleClass="noborder"/>  
							</td>
					    	<td> <label for="projetoRenovado"> Solicita��o de Renova��o: </label> </td>
					    	<td>
								<h:selectOneMenu id="buscaAcaoSolicitacaoRenovacao" value="#{buscaPadraoProjetosDeExtensaoMBean.buscaAcaoSolicitacaoRenovacao}" 
					    			onchange="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaAcaoSolicitacaoRenovacao').checked = true;">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
									<f:selectItem itemLabel="SOLICITA��O DE RENOVA��O" itemValue="1" />
									<f:selectItem itemLabel="PROJETO NOVO" itemValue="2" />
				 				</h:selectOneMenu>
					    	 </td>
					    </tr>
					    
					    
					    
			
					    <tr>
							<td> <h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.checkBuscaFinanciamentoConvenio}" id="selectBuscaFinanciamentoConvenio" styleClass="noborder"/>  </td>
					    	<td> <label for="financiamentoConvenio"> Financiamentos & Conv�nios: </label> </td>
					    	<td>
						    	<h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.buscaFinanciamentoInterno}" styleClass="noborder" 
						    		onfocus="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaFinanciamentoConvenio').checked = true;" /> Solicitou Financiamento Interno
								<br />
						    	<h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.buscaFinanciamentoExterno}"	styleClass="noborder" 
						    		onfocus="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaFinanciamentoConvenio').checked = true;" /> Solicitou Financiamento Externo
								<br />
						    	<h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.buscaAutoFinanciamento}" styleClass="noborder" 
						    		onfocus="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaFinanciamentoConvenio').checked = true;" /> Auto Financiamento
								<br />
						    	<h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.buscaConvenioFunpec}" styleClass="noborder" 
						    		onfocus="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaFinanciamentoConvenio').checked = true;" /> Conv�nio Funpec
						    	<br />
						    	<h:selectBooleanCheckbox value="#{buscaPadraoProjetosDeExtensaoMBean.buscaRecebeuFinanciamentoInterno}" styleClass="noborder" 
						    		onfocus="javascript:$('formBuscaPadraoParticipantesExtensao:selectBuscaFinanciamentoConvenio').checked = true;" /> Recebeu Financiamento Interno
						    	<ufrn:help img="/img/ajuda.gif">A��es que solicitaram e receberam finaciamento interno ap�s aprova��o do presidente do comit�,
						    		ser�o listadas</ufrn:help>
					    	 </td>
					    </tr>
				</c:if>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="cmdBucarParticipante" value="Buscar" action="#{buscaPadraoProjetosDeExtensaoMBean.buscarProjeto}" />
						<h:commandButton id="cmdCancelar" value=" Cancelar " action="#{buscaPadraoProjetosDeExtensaoMBean.cancelarBuscaProjetoExtensao}" immediate="true" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	
	
		<%-- Lista os Resultados da busca  --%>
	
		<div class="infoAltRem" style="margin-bottom: 10px;">
			<h:graphicImage value="/img/seta.gif"/>: Selecionar Projeto
		</div>
	
	
		<c:if test="${buscaPadraoProjetosDeExtensaoMBean.quantidadeAtividadesBuscadas == 0}">
			<center><i> Nenhuma a��o de extens�o localizada </i></center>
		</c:if>


		<c:if test="${buscaPadraoProjetosDeExtensaoMBean.quantidadeAtividadesBuscadas > 0}">
	
		
	
			 <table class="listagem">
			    <caption>A��es de extens�o localizadas ( ${buscaPadraoProjetosDeExtensaoMBean.quantidadeAtividadesBuscadas} )</caption>
		
			      <thead>
			      	<tr>
			      		<th>C�digo</th>
			        	<th width="50%">T�tulo</th>
			        	<th>Unidade</th>
			        	<th>Situa��o</th>
			        	<th>Dimens�o Acad�mica</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>		        	
			        </tr>
			 	</thead>
			 	<tbody>
			 	
				 	 <c:forEach items="#{buscaPadraoProjetosDeExtensaoMBean.atividadesBuscadas}" var="atividade" varStatus="status">
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			
								<td> ${atividade.codigo} </td>
			                    <td> ${atividade.titulo} 
			                    	<h:outputText value="<br/><i>Coordenador(a): #{atividade.coordenacao.pessoa.nome}</i>" rendered="#{not empty atividade.coordenacao}" escape="false"/>
			                    </td>
			                    <td> ${atividade.unidade.sigla} </td>
								<td> ${atividade.situacaoProjeto.descricao} </td>
								<td> ${atividade.projeto.extensao ? 'ASSOCIADO' : 'EXTENS�O'}</td>
								<td>
									
									<%-- atividadeExtensao == atividadeExtensaoMBean --%>
														
									<h:commandLink title="Visualizar A��o" action="#{ atividadeExtensao.view }" immediate="true">
									        <f:param name="id" value="#{atividade.id}"/>
				                   			<h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</td>
								<td>
									<h:commandLink title="Selecionar A��o" action="#{ buscaPadraoProjetosDeExtensaoMBean.selecionouProjeto }">
									        <f:param name="idProjetoSelecionado" value="#{atividade.projeto.id}"/>
				                   			<h:graphicImage url="/img/seta.gif" />
									</h:commandLink>
								</td>
			              </tr>
			          </c:forEach>
			          
			 	</tbody>
			 </table>
		
	
		</c:if>
	
	
	</h:form>


</f:view>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
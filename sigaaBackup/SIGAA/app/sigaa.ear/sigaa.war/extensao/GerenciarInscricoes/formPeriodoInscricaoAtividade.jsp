<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>

<script type="text/javascript">
  tinyMCE.init({
        mode : "textareas",
        theme : "advanced",
        theme_advanced_toolbar_location : "top",
        theme_advanced_buttons1 : "bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,separator,link",
		theme_advanced_buttons2 : "",
		theme_advanced_buttons3 : "",
		theme_advanced_source_editor_wrap : false,   // o botão "code" que permite editar o html vem desmarcado a opção word wrap, se tiver marcado essa opção dá erro se o link for muito grande porque ele quebra o link em mais de uma linha.
		relative_urls : false,                     
	    remove_script_host : false,                  // mantenha esse negócio false, senão vai dá pau nos links absolutos, ele vai cortar e deixar somente o caminho relativo.
	    convert_urls : true,
        width : "650",
        height : "300"
 });
</script>

<f:view>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<h2><ufrn:subSistema /> &gt; Inscrição para ${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade ? 'Atividade' : 'Mini Atividade'} de Extensão</h2>

	<div class="descricaoOperacao">
		<p>Caro (a) Usuário (a),</p>
		<p>Esse formulário permite abrir ou editar um período de inscrição para um curso ou evento de extensão. </p>
		<br />
		<p> Existem dois métodos de preenchimento das vagas </p>
		<ul>
			<li><strong>COM CONFIRMAÇÃO</strong>: O coordenador deverá aprovar cada inscrição realizada para que os participantes estejam devidamente inscritos no curso ou evento.</li>
			<li><strong>PREENCHIMENTO AUTOMÁTICO</strong>: À medida que os participantes se inscrevem, eles automaticamente participam dos cursos ou evento. Não é necessária aprovação do coordenador.</li>
		</ul>
		
		<p> Caso haja cobrança de taxa de matrícula, será necessário informar os valores a serem pagos por cada tipo de participante. 
		   Com cobrança de taxa de matrícula o participante só participará da ação quando o pagamento for confirmado. 
		   Será mostrado para ele uma opção para emissão de uma Guia de Recolhimento da União para o pagamento da taxa de matrícula.
		</p>
		
		<br/>
		<p>	É possível escolher um questionário para o usuário responder no momento da inscrição.</p>
		<p>Ele também pode submeter algum arquivo. Essa opção pode ser usada para o participante enviar algum documento que comprove algum pré-requisito para fazer parte do curso ou evento. 
		Caso o envio de arquivo não seja obrigatório, o campo ainda aparecerá para ele, mas o sistema permitirá a inscrição mesmo que ele não submeta o arquivo.</p>
		
		<br/>
		<p style="font-weight: bold;">
			<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade}">
				Caso a atividade possua mini atividades, a quantidade de vagas para ela deve contemplar no mínimo o maior número de vagas aberto de alguma das suas mini atividades.
				Haja visto que para se inscrever em uma mini atividade, o participante deve primeiro se inscrever na atividade a qual a mini atividade pertence.
			</c:if>
			
			<c:if test="${! gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade}">
				O número máximo de vagas que poderão ser abertas para uma mini atividade será a quantidade de vagas abertas para a atividade. 
				Haja visto que para se inscrever em uma mini atividade, o participante deve primeiro se inscrever na atividade a qual a mini atividade pertence.
			</c:if>
		</p>
		
		<br/>
	</div>

	<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />
	
	
	
	<h:form id="formCadastraAlteraPeriodoInscricoesExtensao">

		

		<%-- Dados das inscrição  --%>
		
		<table class="formulario" style="width: 95%;">
			<caption>Dados da Inscrição</caption>
			
			<tr>
				<td colspan="2">
					<%-- Dados da mini atividade / Atividade --%>
					<table class="subFormulario" style="width: 100%; margin-bottom: 20px; border: 1px solid #DEDFE3;">
						<caption> Período de Inscrição para a  ${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade ? 'Atividade' : 'Mini Atividade'} </caption>
						<tr>
							<th style="width: 10%; font-weight: bold;">Atividade:</th>
							<td colspan="5"  style="font-weight: bold;">${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.codigo} - ${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.titulo}</td>
						</tr>
						<tr>
							<th style="width: 10%; font-weight: bold;">Ano:</th>
							<td style="width: 23%;">${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.projeto.ano}</td>
							<th style="width: 10%; font-weight: bold;">Tipo:</th>
							<td style="width: 23%;">${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.tipoAtividadeExtensao.descricao}</td>
							<th style="width: 15%; font-weight: bold;">Previsão de Vagas:</th>
							<td style="width: 23%;">${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.cursoEventoExtensao.numeroVagas}</td>	
						</tr>
						<c:if test="${! gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade}">
						
							<tr>
								<th style="width: 20%; font-weight: bold;">Mini Atividade:</th>
								<td colspan="5" style="font-weight: bold;">${gerenciarInscricoesCursosEventosExtensaoMBean.subAtividadeSelecionada.titulo}</td>
							</tr>
							<tr>
								<th style="width: 20%; font-weight: bold;">Tipo:</th>
								<td colspan="5" style="width: 90%;">${gerenciarInscricoesCursosEventosExtensaoMBean.subAtividadeSelecionada.tipoSubAtividadeExtensao.descricao}</td>
							</tr>
						</c:if>
					</table>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Método de Preenchimento das Vagas:</th>
				<td>
					<h:selectOneRadio value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.metodoPreenchimento}">  
	        				<f:selectItem itemLabel="COM CONFIRMAÇÃO" itemValue="1" />
	        				<f:selectItem itemLabel="PREENCHIMENTO AUTOMÁTICO" itemValue="2" />
	    			</h:selectOneRadio>
				</td>				
			</tr>
			
				
			<tr>
				<th class="obrigatorio" style="width: 20%;">Quantidade de Vagas:</th>
				<td><h:inputText id="quantidadeVagas" label="Quantidade de Vagas" value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.quantidadeVagas}" style="text-align: right;" size="6" maxlength="6" onkeyup="formatarInteiro(this);" />
				</td>
			</tr>
			
			
			<tr>
				<th class="obrigatorio">Período de Inscrição:</th>
				<td>
					<t:inputCalendar id="periodoInicio" value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.periodoInicio}" 
							renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
							size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
							maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
					<i>até</i>
					<t:inputCalendar id="periodoFim" value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.periodoFim}" 
							renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
							size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
							maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
			
			
			
			<tr>
				<th>Cobrança de Taxa de Matrícula:</th>
				<td>
					<h:selectOneRadio value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.cobrancaTaxaMatricula}">  
	        			<f:selectItem itemLabel="Sim" itemValue="true" />
	        			<f:selectItem itemLabel="Não" itemValue="false" />
	        			<a4j:support event="onchange" reRender="divPaiInformacoesCobranca" />
	    			</h:selectOneRadio>
				</td>				
			</tr>
		
			<tr>
				<td colspan="2">
					<t:div id="divPaiInformacoesCobranca">
						<t:div rendered="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.cobrancaTaxaMatricula}">
							<table style="width: 100%;">
								<tr>
									<th style="width: 20%;">Data de Vencimento das GRUs:</th>
									<td>
										<t:inputCalendar id="dataVencimentoGRU" value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.dataVencimentoGRU}" 
												renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
												size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
												maxlength="10" popupTodayString="Hoje é">
											<f:converter converterId="convertData" />
										</t:inputCalendar>
											
										<ufrn:help> A data de vencimento a ser impressa nas GRUs para pagamento da taxa de matrícula. <br/>
										 Será a data limite que o participante tem para efetivar o pagamento. <br/>
										<strong> Caso não seja informada, será por padrão a data de início da atividade. </strong></ufrn:help>
										
									</td>				
								</tr>
								
								<tr>
									<th colspan="2" style="text-align: center; font-weight: bold;">Valores das Taxas de Inscrição</th>
								</tr>
								
								<tr>
									<th class="obrigatorio">Modalidades dos Participantes</th>
									<td>
										<h:selectOneMenu id="comboModalidades"  value="#{gerenciarInscricoesCursosEventosExtensaoMBean.idMolidadeParticipanteSelecionada}">
											<f:selectItems value="#{modalidadeParticipanteMBean.allModalidadeParticipantesAtivasCombo}" />
										</h:selectOneMenu>
										<span style="top: 20px; vertical-align: bottom">
											<a4j:commandLink styleClass="botaoAdicionarPessoa"
													actionListener="#{gerenciarInscricoesCursosEventosExtensaoMBean.adicionarTaxaParaModalidade}"
													reRender="outputPanelModalidades" >
													
													<h:graphicImage url="/img/adicionar.gif" style="border: none;"
																		title="Clique aqui para adicionar essa modalidade de participante." />
													
											</a4j:commandLink>
										</span>
									</td>
								</tr>
								
								<tr>
									<td colspan="2">
										<a4j:outputPanel id="outputPanelModalidades" ajaxRendered="true">
												<t:dataTable id="dtModalidadePeriodo" var="modalidadePeriodoInscricao" rowIndexVar="index"
														rendered="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.modalidadesParticipantesDataModel != null}"
														value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.modalidadesParticipantesDataModel}"
														style="width: 50%; margin-left: auto; margin-right: auto;" rowClasses="linhaPar, linhaImpar">
													<h:column>
														<h:outputText value="#{modalidadePeriodoInscricao.modalidadeParticipante.nome}" />
													</h:column>
													<h:column>
														<h:outputText value="R$" />
														<h:inputText id="valorMulta" value="#{modalidadePeriodoInscricao.taxaMatriculaAsDouble}" maxlength="10" size="12" 
															style="text-align: right" onkeydown="return(formataValor(this, event, 2))" onfocus="javascript:select()" >
																   <f:converter converterId="convertMoeda"/>
															</h:inputText>
													</h:column>
													<h:column rendered="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.modalidadesParticipantesDataModel.rowCount > 0}" >
														<a4j:commandLink
																actionListener="#{gerenciarInscricoesCursosEventosExtensaoMBean.removerTaxaParaModalidade}"
																reRender="outputPanelModalidades">
															<h:graphicImage url="/img/delete.gif" style="border: none;"
																	title="Clique aqui para remover essa taxa de inscrição." />
														</a4j:commandLink>
													</h:column>
												</t:dataTable>
										</a4j:outputPanel>
									</td>
								</tr>
								
							</table>
						</t:div>
					</t:div>
				</td>
			</tr>
		
			
			
			
			
			
			<tr>
				<th class="obrigatorio">Instruções para Inscrição:
				<ufrn:help>Instruções mostradas ao participante no momento da inscrição.</ufrn:help>
				</th>
				<td><h:inputTextarea value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.instrucoesInscricao}" id="instrucoes" style="width:90%;"/></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Informações Gerais:
					<ufrn:help>Informações mostradas ao participante depois que ele realizou a inscrição, quando ele acessa a área interna, por exemplo, para emitir o certificado.</ufrn:help>
				</th>
				<td><h:inputTextarea value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.observacoes}" id="observações" style="width:90%;"/></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Envio de Arquivo Obrigatório:</th>
				<td>
					<h:selectOneRadio value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.envioArquivoObrigatorio}" id="envioArquivoObrigatorio">
						<f:selectItem itemValue="true" itemLabel="Sim"/>
						<f:selectItem itemValue="false" itemLabel="Não"/>
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tr>
				<th>Questionário:</th>
				<td>
					<h:selectOneMenu value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.questionario.id}" id="questionarioEspecifico" style="width:95%;">
						<f:selectItems value="#{gerenciarInscricoesCursosEventosExtensaoMBean.questionariosDaUnidadeCombo}" />
					</h:selectOneMenu>
				</td>				
			</tr>
			
			<tr>
				<td colspan="2" style="height: 20px;"></td>
			</tr>	
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{gerenciarInscricoesCursosEventosExtensaoMBean.confirmButton}" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.cadastrarPeriodoInscricao}" id="cadastrar" />
						<h:commandButton value="Cancelar" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.telaListaInscricoesAtividade}" id="cancelar"   onclick="#{confirm}"  immediate="true"/>
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
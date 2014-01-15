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
		theme_advanced_source_editor_wrap : false,   // o bot�o "code" que permite editar o html vem desmarcado a op��o word wrap, se tiver marcado essa op��o d� erro se o link for muito grande porque ele quebra o link em mais de uma linha.
		relative_urls : false,                     
	    remove_script_host : false,                  // mantenha esse neg�cio false, sen�o vai d� pau nos links absolutos, ele vai cortar e deixar somente o caminho relativo.
	    convert_urls : true,
        width : "650",
        height : "300"
 });
</script>

<f:view>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<h2><ufrn:subSistema /> &gt; Inscri��o para ${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade ? 'Atividade' : 'Mini Atividade'} de Extens�o</h2>

	<div class="descricaoOperacao">
		<p>Caro (a) Usu�rio (a),</p>
		<p>Esse formul�rio permite abrir ou editar um per�odo de inscri��o para um curso ou evento de extens�o. </p>
		<br />
		<p> Existem dois m�todos de preenchimento das vagas </p>
		<ul>
			<li><strong>COM CONFIRMA��O</strong>: O coordenador dever� aprovar cada inscri��o realizada para que os participantes estejam devidamente inscritos no curso ou evento.</li>
			<li><strong>PREENCHIMENTO AUTOM�TICO</strong>: � medida que os participantes se inscrevem, eles automaticamente participam dos cursos ou evento. N�o � necess�ria aprova��o do coordenador.</li>
		</ul>
		
		<p> Caso haja cobran�a de taxa de matr�cula, ser� necess�rio informar os valores a serem pagos por cada tipo de participante. 
		   Com cobran�a de taxa de matr�cula o participante s� participar� da a��o quando o pagamento for confirmado. 
		   Ser� mostrado para ele uma op��o para emiss�o de uma Guia de Recolhimento da Uni�o para o pagamento da taxa de matr�cula.
		</p>
		
		<br/>
		<p>	� poss�vel escolher um question�rio para o usu�rio responder no momento da inscri��o.</p>
		<p>Ele tamb�m pode submeter algum arquivo. Essa op��o pode ser usada para o participante enviar algum documento que comprove algum pr�-requisito para fazer parte do curso ou evento. 
		Caso o envio de arquivo n�o seja obrigat�rio, o campo ainda aparecer� para ele, mas o sistema permitir� a inscri��o mesmo que ele n�o submeta o arquivo.</p>
		
		<br/>
		<p style="font-weight: bold;">
			<c:if test="${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade}">
				Caso a atividade possua mini atividades, a quantidade de vagas para ela deve contemplar no m�nimo o maior n�mero de vagas aberto de alguma das suas mini atividades.
				Haja visto que para se inscrever em uma mini atividade, o participante deve primeiro se inscrever na atividade a qual a mini atividade pertence.
			</c:if>
			
			<c:if test="${! gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade}">
				O n�mero m�ximo de vagas que poder�o ser abertas para uma mini atividade ser� a quantidade de vagas abertas para a atividade. 
				Haja visto que para se inscrever em uma mini atividade, o participante deve primeiro se inscrever na atividade a qual a mini atividade pertence.
			</c:if>
		</p>
		
		<br/>
	</div>

	<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />
	
	
	
	<h:form id="formCadastraAlteraPeriodoInscricoesExtensao">

		

		<%-- Dados das inscri��o  --%>
		
		<table class="formulario" style="width: 95%;">
			<caption>Dados da Inscri��o</caption>
			
			<tr>
				<td colspan="2">
					<%-- Dados da mini atividade / Atividade --%>
					<table class="subFormulario" style="width: 100%; margin-bottom: 20px; border: 1px solid #DEDFE3;">
						<caption> Per�odo de Inscri��o para a  ${gerenciarInscricoesCursosEventosExtensaoMBean.gerenciandoInscricaAtividade ? 'Atividade' : 'Mini Atividade'} </caption>
						<tr>
							<th style="width: 10%; font-weight: bold;">Atividade:</th>
							<td colspan="5"  style="font-weight: bold;">${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.codigo} - ${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.titulo}</td>
						</tr>
						<tr>
							<th style="width: 10%; font-weight: bold;">Ano:</th>
							<td style="width: 23%;">${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.projeto.ano}</td>
							<th style="width: 10%; font-weight: bold;">Tipo:</th>
							<td style="width: 23%;">${gerenciarInscricoesCursosEventosExtensaoMBean.atividadeSelecionada.tipoAtividadeExtensao.descricao}</td>
							<th style="width: 15%; font-weight: bold;">Previs�o de Vagas:</th>
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
				<th class="obrigatorio">M�todo de Preenchimento das Vagas:</th>
				<td>
					<h:selectOneRadio value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.metodoPreenchimento}">  
	        				<f:selectItem itemLabel="COM CONFIRMA��O" itemValue="1" />
	        				<f:selectItem itemLabel="PREENCHIMENTO AUTOM�TICO" itemValue="2" />
	    			</h:selectOneRadio>
				</td>				
			</tr>
			
				
			<tr>
				<th class="obrigatorio" style="width: 20%;">Quantidade de Vagas:</th>
				<td><h:inputText id="quantidadeVagas" label="Quantidade de Vagas" value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.quantidadeVagas}" style="text-align: right;" size="6" maxlength="6" onkeyup="formatarInteiro(this);" />
				</td>
			</tr>
			
			
			<tr>
				<th class="obrigatorio">Per�odo de Inscri��o:</th>
				<td>
					<t:inputCalendar id="periodoInicio" value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.periodoInicio}" 
							renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
							size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
							maxlength="10" popupTodayString="Hoje �">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
					<i>at�</i>
					<t:inputCalendar id="periodoFim" value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.periodoFim}" 
							renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
							size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
							maxlength="10" popupTodayString="Hoje �">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
			
			
			
			<tr>
				<th>Cobran�a de Taxa de Matr�cula:</th>
				<td>
					<h:selectOneRadio value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.cobrancaTaxaMatricula}">  
	        			<f:selectItem itemLabel="Sim" itemValue="true" />
	        			<f:selectItem itemLabel="N�o" itemValue="false" />
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
												maxlength="10" popupTodayString="Hoje �">
											<f:converter converterId="convertData" />
										</t:inputCalendar>
											
										<ufrn:help> A data de vencimento a ser impressa nas GRUs para pagamento da taxa de matr�cula. <br/>
										 Ser� a data limite que o participante tem para efetivar o pagamento. <br/>
										<strong> Caso n�o seja informada, ser� por padr�o a data de in�cio da atividade. </strong></ufrn:help>
										
									</td>				
								</tr>
								
								<tr>
									<th colspan="2" style="text-align: center; font-weight: bold;">Valores das Taxas de Inscri��o</th>
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
																	title="Clique aqui para remover essa taxa de inscri��o." />
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
				<th class="obrigatorio">Instru��es para Inscri��o:
				<ufrn:help>Instru��es mostradas ao participante no momento da inscri��o.</ufrn:help>
				</th>
				<td><h:inputTextarea value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.instrucoesInscricao}" id="instrucoes" style="width:90%;"/></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Informa��es Gerais:
					<ufrn:help>Informa��es mostradas ao participante depois que ele realizou a inscri��o, quando ele acessa a �rea interna, por exemplo, para emitir o certificado.</ufrn:help>
				</th>
				<td><h:inputTextarea value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.observacoes}" id="observa��es" style="width:90%;"/></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Envio de Arquivo Obrigat�rio:</th>
				<td>
					<h:selectOneRadio value="#{gerenciarInscricoesCursosEventosExtensaoMBean.obj.envioArquivoObrigatorio}" id="envioArquivoObrigatorio">
						<f:selectItem itemValue="true" itemLabel="Sim"/>
						<f:selectItem itemValue="false" itemLabel="N�o"/>
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tr>
				<th>Question�rio:</th>
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
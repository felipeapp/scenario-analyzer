<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2 class="title">
		<ufrn:subSistema /> > Calendário
	</h2>

	<h:form id="form" enctype="multipart/form-data">

		<table width="100%" class="Formulario">
			<tr>
			
				<td colspan="2">

					<div id="tabs-dados-projeto" class="reduzido">

						<div id="datas" class="aba">
							<br /> <b>Ano Período:</b>
							<h:selectOneMenu value="#{calendarioBolsaAuxilioMBean.obj.id}"
								id="calendarios"
								valueChangeListener="#{calendarioBolsaAuxilioMBean.carregarCalendario}">
								<f:selectItem itemLabel="-- NOVO --" itemValue="0" />
								<f:selectItems
									value="#{calendarioBolsaAuxilioMBean.allAnoCadastrados}" />
								<a4j:support event="onchange" reRender="form" />
							</h:selectOneMenu>

							<table class="formulario" width="100%" align="center"
								cellpadding="5" style="margin-top: 15px;">
								<caption class="listagem">Cadastro do Calendário</caption>

								<h:inputHidden value="#{calendarioBolsaAuxilioMBean.obj.id}" />
								<tr>
									<th class="${calendarioBolsaAuxilioMBean.obj.id == 0 ? "obrigatorio" : "" }">Ano
										- Período:</th>
									<td width="60%"><c:if
											test="${calendarioBolsaAuxilioMBean.obj.id == 0}">
											<h:inputText value="#{calendarioBolsaAuxilioMBean.obj.ano}"
												id="ano" size="4" title="ano" maxlength="4"
												onkeyup="formatarInteiro(this);"
												converter="#{ intConveter }" /> - 
													
													<h:inputText
												value="#{calendarioBolsaAuxilioMBean.obj.periodo}"
												id="periodo" size="1" title="periodo" maxlength="1"
												onkeyup="formatarInteiro(this);"
												converter="#{ intConveter }" />
										</c:if> <c:if test="${calendarioBolsaAuxilioMBean.obj.id > 0}">
											<h:outputText
												value="#{calendarioBolsaAuxilioMBean.obj.anoPeriodo}"></h:outputText>
										</c:if></td>
								</tr>

								<tr>
									<th>Tornar esse calendário vigente:</th>
									<td><h:selectBooleanCheckbox
											value="#{calendarioBolsaAuxilioMBean.obj.vigente}"
											id="vigencia"
											disabled="#{calendarioBolsaAuxilioMBean.obj.vigente}" /></td>
								</tr>

								<tr>
									<th>Ano - Período Alimentação:</th>
									<td><h:inputText
											value="#{calendarioBolsaAuxilioMBean.obj.anoAlimentacao}"
											id="anoFeriasVigente" size="4" maxlength="4"
											onkeyup="formatarInteiro(this);" converter="#{ intConveter }" />
										- <h:inputText
											value="#{calendarioBolsaAuxilioMBean.obj.periodoAlimentacao}"
											id="periodoFeriasVigente" size="1" maxlength="1"
											onkeyup="formatarInteiro(this);" converter="#{ intConveter }" /></td>
								</tr>

								<tr>
									<td class="subFormulario" colspan="2">Período de
										solicitação das bolsas</td>
								</tr>

								<tr>
									<th class="obrigatorio">Tipo da Bolsa:</th>
									<td><h:selectOneMenu id="tipoBolsaDiscente"
											immediate="true"
											value="#{calendarioBolsaAuxilioMBean.calendario.tipoBolsaAuxilio.id}">
											<f:selectItem itemLabel="-- SELECIONE -- " itemValue="0" />
											<f:selectItems
												value="#{calendarioBolsaAuxilioMBean.tiposBolsaAuxilio}" />
										</h:selectOneMenu></td>
								</tr>

								<tr>
									<th class="obrigatorio">Município:</th>
									<td><t:selectOneMenu
											value="#{calendarioBolsaAuxilioMBean.calendario.municipio.id}"
											id="endMunicipio">
											<f:selectItem itemLabel="-- SELECIONE -- " itemValue="0" />
											<f:selectItems
												value="#{calendarioBolsaAuxilioMBean.allMunicipios}" />
										</t:selectOneMenu></td>
								</tr>

								<tr>
									<th class="obrigatorio">Inscrição de:</th>
									<td><t:inputCalendar
											value="#{calendarioBolsaAuxilioMBean.calendario.inicio}"
											size="10" maxlength="10" id="dataInicio"
											popupDateFormat="dd/MM/yyyy" renderAsPopup="true"
											renderPopupButtonAsImage="true"
											onkeypress="return(formatarMascara(this,event,'##/##/####'))">
											<f:converter converterId="convertData" />
										</t:inputCalendar> a <t:inputCalendar
											value="#{calendarioBolsaAuxilioMBean.calendario.fim}"
											size="10" maxlength="10" id="dataFim"
											popupDateFormat="dd/MM/yyyy" renderAsPopup="true"
											renderPopupButtonAsImage="true"
											onkeypress="return(formatarMascara(this,event,'##/##/####'))">
											<f:converter converterId="convertData" />
										</t:inputCalendar></td>
								</tr>

								<tr>
									<th>Resultados de:</th>
									<td><t:inputCalendar
											value="#{calendarioBolsaAuxilioMBean.calendario.inicioDivulgacaoResultado}"
											size="10" maxlength="10" id="dataInicioDivulgacao"
											popupDateFormat="dd/MM/yyyy" renderAsPopup="true"
											renderPopupButtonAsImage="true"
											onkeypress="return formataData(this,event)">
											<f:converter converterId="convertData" />
										</t:inputCalendar> a <t:inputCalendar
											value="#{calendarioBolsaAuxilioMBean.calendario.fimDivulgacaoResultado}"
											size="10" maxlength="10" id="dataFimDivulgacao"
											popupDateFormat="dd/MM/yyyy" renderAsPopup="true"
											renderPopupButtonAsImage="true"
											onkeypress="return formataData(this,event)">
											<f:converter converterId="convertData" />
										</t:inputCalendar></td>
								</tr>

								<tr>
									<th class="obrigatorio">Execução de:</th>
									<td><t:inputCalendar
											value="#{calendarioBolsaAuxilioMBean.calendario.inicioExecucaoBolsa}"
											size="10" maxlength="10" id="dataInicioExecucao"
											popupDateFormat="dd/MM/yyyy" renderAsPopup="true"
											renderPopupButtonAsImage="true"
											onkeypress="return formataData(this,event)">
											<f:converter converterId="convertData" />
										</t:inputCalendar> a <t:inputCalendar
											value="#{calendarioBolsaAuxilioMBean.calendario.fimExecucaoBolsa}"
											size="10" maxlength="10" id="dataFimExecucao"
											popupDateFormat="dd/MM/yyyy" renderAsPopup="true"
											renderPopupButtonAsImage="true"
											onkeypress="return formataData(this,event)">
											<f:converter converterId="convertData" />
										</t:inputCalendar></td>
								</tr>

								<tr>
									<th>Hora término:</th>
									<td><h:inputText
											value="#{calendarioBolsaAuxilioMBean.calendario.horaTermino}"
											id="horaFim" size="2" maxlength="2"
											onkeyup="return formatarInteiro(this);" /> : <h:inputText
											value="#{calendarioBolsaAuxilioMBean.calendario.minutoTermino}"
											id="minutoFim" size="2" maxlength="2"
											onkeyup="return formatarInteiro(this);" /> <ufrn:help
											img="/img/ajuda.gif">
												Informe a Hora término no formato 24h.
											</ufrn:help></td>
								</tr>

								<tr>
									<th><h:selectBooleanCheckbox
											value="#{calendarioBolsaAuxilioMBean.calendario.alunoVeterano}"
											id="veterano" /></th>
									<td>Alunos <b>veteranos</b> podem solicitar essa bolsa ?
									</td>
								</tr>

								<tr>
									<th><h:selectBooleanCheckbox
											value="#{calendarioBolsaAuxilioMBean.calendario.alunoNovato}"
											id="novato" /></th>
									<td>Alunos <b>novatos</b> podem solicitar essa bolsa ?
									</td>
								</tr>

								<tfoot>
									<tr>
										<td colspan="4"><h:commandButton value="Adicionar"
												id="adicionar"
												action="#{calendarioBolsaAuxilioMBean.adicionarPeriodoSolicitacaoBolsa}">
												<a4j:support
													reRender="tbCalendario,tipoBolsaDiscente, endMunicipio, dataInicio, dataFim,  dataInicioDivulgacao, dataFimDivulgacao, 
														dataInicioExecucao, dataFimExecucao, horaFim, minutoFim, veterano, novato" />
											</h:commandButton></td>
									</tr>
								</tfoot>

							</table>

							<a4j:region
								rendered="#{ not empty calendarioBolsaAuxilioMBean.obj.calendario }">

								<br />

								<table width="100%" class="formulario" border="1">

									<caption>Municípios já adicionados (${
										fn:length(calendarioBolsaAuxilioMBean.obj.calendario) })</caption>

									<tr>
										<td><t:dataTable
												value="#{calendarioBolsaAuxilioMBean.calendarios}"
												var="linha" align="center" width="100%"
												styleClass="listagem" rowClasses="linhaPar, linhaImpar"
												id="tbCalendario">

												<t:column>
													<f:facet name="header">
														<f:verbatim>Tipo Bolsa</f:verbatim>
													</f:facet>
													<h:outputText value="#{linha.tipoBolsaAuxilio.denominacao}" />
												</t:column>

												<t:column>
													<f:facet name="header">


														<f:verbatim>Município</f:verbatim>
													</f:facet>
													<h:outputText value="#{linha.municipio.nome}" />
												</t:column>

												<t:column styleClass="centerAlign">
													<f:facet name="header">


														<f:verbatim>Inscrições</f:verbatim>
													</f:facet>
													<h:outputText value="#{linha.inicio}" /> até <h:outputText
														value="#{linha.fim}" />
												</t:column>

												<t:column styleClass="centerAlign">
													<f:facet name="header">


														<f:verbatim>Resultados</f:verbatim>
													</f:facet>
													<h:outputText value="#{linha.inicioDivulgacaoResultado }" /> - 
													<h:outputText value="#{linha.fimDivulgacaoResultado}" />
												</t:column>

												<t:column styleClass="centerAlign">
													<f:facet name="header">
														<f:verbatim>Período de Execução</f:verbatim>
													</f:facet>
													<h:outputText value="#{linha.inicioExecucaoBolsa }" />
													<h:outputText
														value="#{not empty linha.inicioExecucaoBolsa ? ' até ' : ''  }" />
													<h:outputText value="#{linha.fimExecucaoBolsa}" />
												</t:column>

												<t:column styleClass="centerAlign">
													<f:facet name="header">

														<f:verbatim>Veteranos</f:verbatim>
													</f:facet>
													<h:outputText
														value="#{linha.alunoVeterano ? 'Sim' : 'Não'}" />
												</t:column>

												<t:column styleClass="centerAlign">
													<f:facet name="header">
														<f:verbatim>Novatos</f:verbatim>
													</f:facet>
													<h:outputText value="#{linha.alunoNovato ? 'Sim' : 'Não'}" />
												</t:column>

												<h:column>
													<h:commandLink
														action="#{calendarioBolsaAuxilioMBean.removerPeriodoSolicitacaoBolsa}"
														onclick=" if(!confirm('Deseja realmente remover esse registro ?')) { return false; }">
														<h:graphicImage value="/img/delete.gif"
															alt="Remover registro" />
													</h:commandLink>
												</h:column>

											</t:dataTable></td>
									</tr>
								</table>

							</a4j:region>

						</div>

						<div id="texto" class="aba">
							<table width="100%" class="formulario">
								<tr>
									<center>
										<td><h:inputTextarea id="textoReferencia"
												onfocus="setAba('texto')"
												value="#{ calendarioBolsaAuxilioMBean.obj.textoTelaAvisoDiscentes }" />
										</td>
									</center>
								</tr>
							</table>

							<table class=formulario width="100%">
									<caption class="listagem">Informe o local do Arquivo</caption>
									<h:inputHidden
										value="#{calendarioBolsaAuxilioMBean.confirmButton}" />
									<h:inputHidden value="#{calendarioBolsaAuxilioMBean.obj.id}" />

									<div class="descricaoOperacao">
										É possível incluir um arquivo com informações adicionais sobre
										a bolsa que está sendo ofertada. <br /> <b>Caso já exista
											um arquivo anexado, este será excluído ao anexar um novo. </b>

									</div>

									<tr>
										<th width="20%">Arquivo:</th>
										<td><t:inputFileUpload id="uFile"
												value="#{calendarioBolsaAuxilioMBean.file}" storage="file"
												size="70" /></td>
									</tr>
									
									<a4j:region id="tableArquivos"
										rendered="#{ calendarioBolsaAuxilioMBean.obj.possuiArquivo }">

										<c:if
											test="${not empty calendarioBolsaAuxilioMBean.obj.idArquivo}">
											<tr>
												<td colspan="2"><br />
													<div class="infoAltRem">
														<h:graphicImage value="/img/view.gif"
															style="overflow: visible;" />
														: Visualizar Arquivo
														<h:graphicImage value="/img/delete.gif"
															style="overflow: visible;" />
														: Remover Arquivo
													</div></td>
											</tr>

											<tr>
												<td colspan="2" class="subFormulario">Arquivo anexado</td>
											</tr>
											<tr>
												<td colspan="2"><input type="hidden" value="0"
													id="idArquivo" name="idArquivo" />
													<table class="listagem" width="100%">
														<thead>
															<tr>
																<td>Nome do Arquivo</td>
																<td width="5%"></td>
																<td width="5%"></td>
															</tr>
														</thead>
														<tr>
															<td width="90%">${calendarioBolsaAuxilioMBean.nomeArquivo}</td>
															<td width="5%"><a
																href="${ctx}/verProducao?idProducao=${ calendarioBolsaAuxilioMBean.obj.idArquivo }&&key=${ sf:generateArquivoKey(calendarioBolsaAuxilioMBean.obj.idArquivo) }"
																target="_blank" title="Visualizar Arquivo"> <h:graphicImage
																		value="/img/view.gif" style="overflow: visible;" />
															</a></td>
															<td width="5%"><h:commandLink
																	action="#{calendarioBolsaAuxilioMBean.removeArquivo}"
																	onclick="#{confirmDelete};setAba('texto')" immediate="true">
																	<h:graphicImage value="/img/delete.gif"
																		style="overflow: visible;" title="Remover" />
																	<a4j:support reRender="tableArquivo" />

																</h:commandLink></td>
														</tr>
													</table></td>
											</tr>
										</c:if>
									</a4j:region>
									<!-- ANEXO -->
								</table>
						</div>
					</div>
				</td>
			</tr>

			<table width="100%" class="formulario">
				<tfoot>
					<tr>
						<td><h:commandButton
								value="#{calendarioBolsaAuxilioMBean.confirmButton}"
								id="confirmar" action="#{calendarioBolsaAuxilioMBean.cadastrar}" />
							<h:commandButton value="Cancelar" onclick="#{confirm}"
								action="#{calendarioBolsaAuxilioMBean.cancelar}" id="cancelar" /></td>
					</tr>
				</tfoot>
			</table>
		</table>
		
	</h:form>

	<center>
		<br />
		<html:img page="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena">Campos de preenchimento obrigatório.
		</span>
	</center>

</f:view>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
	var Abas = function() {
		return {
		    init : function(){
		        var abas = new YAHOO.ext.TabPanel('tabs-dados-projeto');
			        abas.addTab('datas', "Datas Solicitação");
					abas.addTab('texto', "Texto Referência");
	    	    <c:if test="${sessionScope.aba != null}">
		    		abas.activate('${sessionScope.aba}');
		    	</c:if>
		        <c:if test="${empty sessionScope.aba}">
		        	abas.activate('datas');
		   	 	</c:if>
		    }
	    }
	}();
	YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	
	function setAba(aba) {
		document.getElementById('aba').value = aba;
	}

	J = jQuery.noConflict();						
							
	tinyMCE.init({
		mode : "textareas", theme : "advanced", width : "950", height : "400", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
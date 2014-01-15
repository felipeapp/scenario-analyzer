<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente"%>

<style>

.scrollbar {
  	margin-left: 155px;
	width: 98%;
	overflow:auto;
}
</style>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Cadastro do Plano de Trabalho</h2>

	<c:if test="${planoTrabalhoExtensao.obj.novoPlano}">
		<div class="descricaoOperacao">
			<b>Tipos de V�nculo</b><br />
			<ul>
				<li><b>Bolsista FAEx:</b> bolsista mantido com recursos concedidos pelo FAEx.</li>
				<li><b>Bolsista Externo:</b> bolsista mantido com recursos de outros org�os. CNPq, Petrobr�s, Minist�rio da Sa�de, etc.</li>
				<li><b>Volunt�rio:</b> s�o membros da equipe da a��o de extens�o que n�o s�o remunerados.</li>
				<li><b>Atividade Curricular:</b> s�o discentes n�o remunerados que fazem parte da equipe da a��o de extens�o.</li>
			</ul>				
		</div>
	</c:if>

	<h:form id="form">
			
		<table class="formulario" width="100%">
			<caption class="listagem">Cadastro do Plano de Trabalho </caption>	
			<h:inputHidden value="#{planoTrabalhoExtensao.obj.id}" />

			<tr>
				<th width="18%"><b>C�digo:</b></th>
				<td><h:outputText value="#{planoTrabalhoExtensao.obj.atividade.codigo}" /></td>
			</tr>
			
			<tr>
				<th width="18%"><b>T�tulo da A��o:</b></th>
				<td><h:outputText value="#{planoTrabalhoExtensao.obj.atividade.titulo}" /></td>
			</tr>
			
			<tr>
				<th><b>Per�odo do Projeto:</b></th>
				<td><h:outputText value="#{planoTrabalhoExtensao.obj.atividade.dataInicio}" /> at� <h:outputText value="#{planoTrabalhoExtensao.obj.atividade.dataFim}" /></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Orientador(a):</th>
				<td>
					<h:selectOneMenu value="#{planoTrabalhoExtensao.obj.orientador.id}" id="idOrientador">
						<f:selectItem  itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{planoTrabalhoExtensao.allOrientadoresCombo}" />																				
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">Per�odo do Plano:</th>
				<td><t:inputCalendar size="10" value="#{planoTrabalhoExtensao.obj.dataInicio}" renderAsPopup="true"
							renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" 
							maxlength="10" popupDateFormat="dd/MM/yyyy" id="inicioPlano">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
					<i>�</i> 
					<t:inputCalendar size="10" value="#{planoTrabalhoExtensao.obj.dataFim}" renderAsPopup="true"
							renderPopupButtonAsImage="true"	onkeypress="return(formataData(this,event))"
							maxlength="10" popupDateFormat="dd/MM/yyyy" id="fimPlano">
						<f:converter converterId="convertData" />
					</t:inputCalendar>	
				</td>
			</tr>

			<tr>
				<th class="obrigatorio"> Local de Trabalho:</th>
				<td><h:inputText style="width:98%" value="#{planoTrabalhoExtensao.obj.localTrabalho}" size="85"
							readonly="#{planoTrabalhoExtensao.readOnly}" id="localTrabalho" maxlength="85" />
				</td>	
			</tr>

			<tr>
				<th class="obrigatorio"> Justificativa:</th>
				<td><h:inputTextarea 	rows="4" style="width:98%" value="#{planoTrabalhoExtensao.obj.justificativa}" 
							readonly="#{planoTrabalhoExtensao.readOnly}" id="justificativa" />
				</td>	
			</tr>

			<tr>
				<th class="obrigatorio"> Objetivos:</th>
				<td><h:inputTextarea rows="4" style="width:98%" value="#{planoTrabalhoExtensao.obj.objetivo}" 
							readonly="#{planoTrabalhoExtensao.readOnly}" id="objetivo" />
				</td>	
			</tr>

			<tr>
				<th class="obrigatorio"> Atividades desenvolvidas:</th>
				<td><h:inputTextarea rows="4" style="width:98%" value="#{planoTrabalhoExtensao.obj.descricaoAtividades}" 
							readonly="#{planoTrabalhoExtensao.readOnly}" id="descricaoAtividades" />
				</td>	
			</tr>
			
			<c:if test="${!planoTrabalhoExtensao.obj.enviado}">
				<tr>
					<td colspan="2">		
						<table class="subFormulario">
							<caption class="listagem"> Dados do Discente </caption>
							<tr>
								<td colspan="2">
									<div class="descricaoOperacao">
										<b>Caro docente,</b>
										<br /> Para sua orienta��o, verifique a lista de discentes interessados na tabela abaixo. 
										Esses s�o os discentes que demonstraram interesse em participar da a��o de extens�o 
										atrav�s do portal do discente.
									</div>
								</td>
							</tr>	
	
							<tr>
								<td colspan="2">
									<table width="100%">
										<tr>
											<th class="obrigatorio" width="18%">Discente:</th>
											<td><h:inputHidden id="idDiscente" 
														value="#{ planoTrabalhoExtensao.obj.discenteExtensao.discente.id }" />
												<h:inputText id="nomeDiscente" style="width: 95%"
														value="#{ planoTrabalhoExtensao.obj.discenteExtensao.discente.pessoa.nome }" />
										
												<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
														baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
														indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
														parser="new ResponseXmlToHtmlListParser()" />
												<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif" 
														alt="Carregando..." title="Carregando..." id="imgIndicadorDiscente"/> </span>
											</td>
										</tr>
									</table>
								</td>
							</tr>
								
							<tr>
								<th width="18%" class="obrigatorio">Tipo de V�nculo:</th>
								<td>
									<h:selectOneMenu value="#{planoTrabalhoExtensao.obj.discenteExtensao.tipoVinculo.id}" id="tipoVinculo" 
										valueChangeListener="#{ planoTrabalhoExtensao.carregarDataInicialBolsista }">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{planoTrabalhoExtensao.obj.tipoVinculo.remunerado ? tipoVinculoDiscenteBean.remuneradosAtivosExtensaoCombo 
												: tipoVinculoDiscenteBean.naoRemuneradosAtivosExtensaoCombo}" />
										<a4j:support event="onchange" reRender="dataEntradaDiscente, inicioDiscentea, inicioDiscente" />																				
									</h:selectOneMenu>
								</td>
							</tr>
							
							<tr>
								<td colspan="2">
									<a4j:outputPanel ajaxRendered="true" id="dataEntradaDiscente">
										<table style="width: 100%;">
											<a4j:region rendered="#{ planoTrabalhoExtensao.obj.discenteExtensao.tipoVinculo.extensaoInterno }">
													<tr>
														<th width="18%">Data de In�cio do Discente: </th>
														<td colspan="2">
															<h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.dataInicio}" id="inicioDiscentea"/>
														</td>
													</tr>
											</a4j:region>
										
											<a4j:region rendered="#{ not planoTrabalhoExtensao.obj.discenteExtensao.tipoVinculo.extensaoInterno }">
													<tr>
														<th width="18%" class="obrigatorio">Data de In�cio do Discente: </th>
														<td colspan="2">
															<t:inputCalendar size="10" value="#{planoTrabalhoExtensao.obj.discenteExtensao.dataInicio}" 
																	renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy"
																	onkeypress="return(formataData(this,event))" maxlength="10" id="inicioDiscente">										
																<f:converter converterId="convertData" />
															</t:inputCalendar>	
														</td>
													</tr>
											</a4j:region>
										</table>
									</a4j:outputPanel>
								</td>
							</tr>
								
							<tr>
								<th class="obrigatorio"> Justificativa: </th>
								<td><h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.justificativa}" 
											id="justificativa_selecao_discente" size="50" />
									<ufrn:help img="/img/ajuda.gif">Justificativa da coordena��o para sele��o deste discente.</ufrn:help>										
								</td>
							</tr>
							
							<c:if test="${planoTrabalhoExtensao.obj.tipoVinculo.remunerado}">
								<tr>
									<td colspan="2" style="color: red; text-align: center;">
										Para os bolsistas remunerados, informe abaixo seus dados banc�rios.
										Observa��o: a conta informada N�O pode ser Conta Conjunta ou Conta Poupan�a.
									</td>
								</tr>
								
								<tr>
									<th class="obrigatorio"> Banco:</th>
									<td><h:selectOneMenu value="#{planoTrabalhoExtensao.obj.discenteExtensao.banco.id}" id="idBanco">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
											<f:selectItems value="#{discenteMonitoria.allBancoCombo}" />
										</h:selectOneMenu></td>
								</tr>
									
								<tr>
									<th class="obrigatorio"> N� Ag�ncia: </th>
									<td><h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.agencia}" size="10" 
												maxlength="15" id="agencia" /></td>
								</tr>
		
								<tr>
									<th class="obrigatorio"> N� Conta Corrente: </th>
									<td><h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.conta}" size="10" 
											maxlength="15" id="conta" /></td>
								</tr>
								
								<tr>
									<th> N� de Opera��o: </th>
									<td><h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.operacao}" size="4" 
											maxlength="15" id="operacao" /></td>
								</tr>
							</c:if>	
							
							<tr>
								<td class="subFormulario" colspan="2">
									<div class="infoAltRem">
										<html:img page="/img/report.png" style="overflow: visible;" />: Visualizar Hist�rico
										<html:img page="/img/email_go.png" style="overflow: visible;" />: Enviar Email
									</div>
								</td>
							</tr>	
							
							<tr>
								<td colspan="2">
									<table class="lista" width="100%">
										<caption>Discentes que realizaram ades�o ao cadastro �nico e demonstraram interesse 
												nesta a��o de extens�o</caption>
										<thead>
											<tr>
												<td width="10%">Matr�cula</td>
												<td>Discente</td>
												<td></td>
												<td></td>
											</tr>
										</thead>
										<tbody>
											<c:set value="#{planoTrabalhoExtensao.inscricoesSelecao}" var="atualizar_lista" /> 
											<c:forEach items="#{planoTrabalhoExtensao.obj.atividade.inscricoesSelecao}" 
													var="insc" varStatus="loop">
												<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
													<td><h:outputText value="#{insc.discente.matricula}" /></td>
													<td><h:outputText value="#{insc.discente.nome}" /><br />
														<b><h:outputText value="Priorit�rio (Segundo resolu��o N� 169/2008-CONSEPE)" 
																rendered="#{insc.prioritario}" /></b>
													</td>
													<td width="3%">
														<h:commandLink title="Visualizar Hist�rico"	immediate="true" 
																action="#{ historico.selecionaDiscenteForm }" id="view_historico">
															<f:param name="id" value="#{insc.discente.id}" />
															<h:graphicImage url="/img/report.png" />
														</h:commandLink>
													</td>
													<td width="3%">
														<h:commandLink action="#{planoTrabalhoExtensao.preEnviarEmail}" >
															<h:graphicImage value="/img/email_go.png" style="overflow: visible;" title="Enviar Email"/>
															<f:param name="id" value="#{ insc.id }" />
														</h:commandLink>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<center><h:outputText rendered="#{empty planoTrabalhoExtensao.obj.atividade.inscricoesSelecao}" 
													value="N�o h� discentes inscritos para esta a��o" /></center>
								</td>
							</tr>	
						</table>
					</td>
				</tr>
			</c:if>			
		
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" action="#{planoTrabalhoExtensao.cancelar}" onclick="#{confirm }" id="btCancelar" />
						<h:commandButton value="Salvar (Rascunho)" action="#{planoTrabalhoExtensao.salvar}" id="btSalvar"/>
						<h:commandButton value="Cronograma >>" action="#{planoTrabalhoExtensao.submeterDadosGerais}" id="btCronograma" /> 
					</td>
				</tr>
			</tfoot>
			
		</table>
		<br />
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
		</center>
		<br />
	
	</h:form>
	
</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs');
	        tabs.addTab('interessados', "Lista de Discentes Interessados")
	        tabs.addTab('livre', "Sele��o Livre");
	        tabs.activate('interessados');	////padr�o

   		    <c:if test="${sessionScope.aba != null}">
				tabs.activate('${sessionScope.aba}');
			</c:if>
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
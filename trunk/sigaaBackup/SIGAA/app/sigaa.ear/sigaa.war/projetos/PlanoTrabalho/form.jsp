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

	<h:form id="form">
			
		<table class="formulario" width="100%">
			<caption class="listagem">Cadastro do Plano de Trabalho </caption>	
			<h:inputHidden value="#{planoTrabalhoProjeto.obj.id}" />

			
			<tr>
				<th width="18%"><b>Título da Ação:</b></th>
				<td><h:outputText value="#{planoTrabalhoProjeto.obj.projeto.ano}  - #{planoTrabalhoProjeto.obj.projeto.titulo}" /></td>
			</tr>
			
			<tr>
				<th width="8%"><b>Período do Projeto:</b></th>
				<td><h:outputText value="#{planoTrabalhoProjeto.obj.projeto.dataInicio}" >
				              <f:convertDateTime type="date" dateStyle="medium"/>
				    </h:outputText>  a 
				    <h:outputText value="#{planoTrabalhoProjeto.obj.projeto.dataFim}" >
				              <f:convertDateTime type="date" dateStyle="medium"/>
				    </h:outputText></td>
			</tr>
			
			<tr>
				<th><b>Coordenador(a):</b></th>
				<td><h:outputText value="#{planoTrabalhoProjeto.obj.projeto.coordenador.pessoa.nome}" /></td>
			</tr>

			<tr>
				<th class="obrigatorio">Período:</th>
				<td><t:inputCalendar size="10" value="#{planoTrabalhoProjeto.obj.dataInicio}" renderAsPopup="true"
							renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" 
							maxlength="10" popupDateFormat="dd/MM/yyyy" id="inicioPlano">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
					<i>a</i> 
					<t:inputCalendar size="10" value="#{planoTrabalhoProjeto.obj.dataFim}" renderAsPopup="true"
							renderPopupButtonAsImage="true"	onkeypress="return(formataData(this,event))"
							maxlength="10" popupDateFormat="dd/MM/yyyy" id="fimPlano">
						<f:converter converterId="convertData" />
					</t:inputCalendar>	
				</td>
			</tr>

			<tr>
				<th class="obrigatorio"> Local de Trabalho:</th>
				<td><h:inputText style="width:98%" value="#{planoTrabalhoProjeto.obj.localTrabalho}" size="85"
							readonly="#{planoTrabalhoProjeto.readOnly}" id="localTrabalho" maxlength="85" />
				</td>	
			</tr>

			<tr>
				<th class="obrigatorio"> Justificativa:</th>
				<td><h:inputTextarea rows="4" style="width:98%" value="#{planoTrabalhoProjeto.obj.justificativa}" 
							readonly="#{planoTrabalhoProjeto.readOnly}" id="justificativa" />
				</td>	
			</tr>

			<tr>
				<th class="obrigatorio"> Objetivos:</th>
				<td><h:inputTextarea rows="4" style="width:98%" value="#{planoTrabalhoProjeto.obj.objetivos}" 
							readonly="#{planoTrabalhoProjeto.readOnly}" id="objetivo" />
				</td>	
			</tr>

			<tr>
				<th class="obrigatorio"> Metodologia/Atividades desenvolvidas:</th>
				<td><h:inputTextarea rows="4" style="width:98%" value="#{planoTrabalhoProjeto.obj.metodologia}" 
							readonly="#{planoTrabalhoProjeto.readOnly}" id="descricaoAtividades" />
				</td>	
			</tr>
			
			<c:if test="${!planoTrabalhoProjeto.obj.alterando}">
				<tr>
					<td colspan="2">		
						<table style="subFormulario" width="100%">
							<caption class="listagem"> Dados do Discente </caption>
	
							<tr>
								<td colspan="2">
									<table width="100%">
										<tr>
											<th class="obrigatorio" width="25%">Discente:</th>
											<td><h:inputHidden id="idDiscente" 
														value="#{ planoTrabalhoProjeto.obj.discenteProjeto.discente.id }" />
												<h:inputText id="nomeDiscente" style="width: 95%"
														value="#{ planoTrabalhoProjeto.obj.discenteProjeto.discente.pessoa.nome }" />
										
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
								<th class="obrigatorio" width="25%">Data de Início: </th>
								<td colspan="2">
									<t:inputCalendar size="10" value="#{planoTrabalhoProjeto.obj.discenteProjeto.dataInicio}" 
											renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy"
											onkeypress="return(formataData(this,event))" maxlength="10" id="inicioDiscente">										
										<f:converter converterId="convertData" />
									</t:inputCalendar>	
								</td>
							</tr>
								
							<tr>
								<th class="obrigatorio">Tipo de Vínculo:</th>
								<td>
									<h:selectOneMenu value="#{planoTrabalhoProjeto.obj.discenteProjeto.tipoVinculo.id}" id="tipoVinculo">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --" />
										<f:selectItems value="#{planoTrabalhoProjeto.voluntario ? tipoVinculoDiscenteBean.naoRemuneradosAtivosAssociadosCombo : tipoVinculoDiscenteBean.remuneradosAtivosAssociadosCombo}" />
									</h:selectOneMenu>
								</td>
									
							</tr>
								
							<tr>
								<th class="obrigatorio"> Justificativa: </th>
								<td><h:inputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.justificativa}" 
											id="justificativa_selecao_discente" size="50" />
									<ufrn:help img="/img/ajuda.gif">Justificativa da coordenação para seleção deste discente.</ufrn:help>										
								</td>
							</tr>
								
						  	<tr>
								<td colspan="2" style="color: red; text-align: center;">
									Para os bolsistas remunerados, informe abaixo seus dados bancários.
									Observação: a conta informada NÃO pode ser Conta Conjunta ou Conta Poupança.
								</td>
							</tr>
						
							<tr>
								<th class="obrigatorio"> Banco:</th>
								<td><h:selectOneMenu value="#{planoTrabalhoProjeto.obj.discenteProjeto.banco.id}" id="idBanco">
										<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM BANCO --" />
										<f:selectItems value="#{discenteMonitoria.allBancoCombo}" />
									</h:selectOneMenu></td>
							</tr>
								
							<tr>
								<th class="obrigatorio"> Nº Agência: </th>
								<td><h:inputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.agencia}" size="10" 
											maxlength="15" id="agencia" /></td>
							</tr>
	
							<tr>
								<th class="obrigatorio"> Nº Conta Corrente: </th>
								<td><h:inputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.conta}" size="10" 
										maxlength="15" id="conta" /></td>
							</tr>
							
							<tr>
								<th class="obrigatorio"> Nº de Operação: </th>
								<td><h:inputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.operacao}" size="5" 
										maxlength="15" id="operacao" /></td>
							</tr>
							
							<tr>
								<td colspan="2">
									<table class="lista" width="100%">
										<caption>Discentes que realizaram adesão ao cadastro único e demonstraram interesse 
												nesta ação associada</caption>
										<thead>
											<tr>
												<td width="10%">Matrícula</td>
												<td>Discente</td>
												<td></td>
												<td></td>
											</tr>
										</thead>
										<tbody>
											<c:set value="#{planoTrabalhoProjeto.inscricoesSelecao}" var="atualizar_lista" /> 
											<c:forEach items="#{planoTrabalhoProjeto.obj.projeto.inscricoesSelecao}" 
													var="insc" varStatus="loop">
												<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
													<td><h:outputText value="#{insc.discente.matricula}" /></td>
													<td><h:outputText value="#{insc.discente.nome}" /><br />
														<b><h:outputText value="Prioritário (Segundo resolução Nº 169/2008-CONSEPE)" 
																rendered="#{insc.prioritario}" /></b>
													</td>
													<td colspan="2"><h:commandLink title="Visualizar Histórico"	immediate="true" 
																action="#{ historico.selecionaDiscenteForm }" id="view_historico">
															<f:param name="id" value="#{insc.discente.id}" />
															<h:graphicImage url="/img/report.png" />
														</h:commandLink>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<center><h:outputText rendered="#{empty planoTrabalhoProjeto.obj.projeto.inscricoesSelecao}" 
													value="Não há discentes inscritos para esta ação associada" /></center>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</c:if>			
		
		
			<c:if test="${planoTrabalhoProjeto.obj.alterando}">			
				<tr>
					<td colspan="2" class="subFormulario">Dados do discente</td>
				</tr>
			
			
				<tr>
					<th><b>Discente: </b></th>
					<td><h:outputText id="txtNomeDiscente" value="#{ planoTrabalhoProjeto.obj.discenteProjeto.discente.pessoa.nome }" /></td>
				</tr>
					
				<tr>
					<th><b>Período: </b></th>
					<td>
						<h:outputText id="txtInicio" value="#{ planoTrabalhoProjeto.obj.discenteProjeto.dataInicio }" /> até 
						<h:outputText id="txtFim" value="#{ planoTrabalhoProjeto.obj.discenteProjeto.dataFim }" />
					</td>
				</tr>
					
				<tr>
					<th><b>Tipo de Vínculo: </b></th>
					<td><h:outputText id="txtVinculo" value="#{ planoTrabalhoProjeto.obj.discenteProjeto.tipoVinculo.descricao }" /></td>						
				</tr>
					
				<tr>
					<th><b>Justificativa: </b></th>
					<td><h:outputText id="txtJustificativa" value="#{ planoTrabalhoProjeto.obj.discenteProjeto.justificativa }" />
						<ufrn:help img="/img/ajuda.gif">Justificativa da coordenação para seleção deste discente.</ufrn:help>										
					</td>
				</tr>
			</c:if>			
		
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" action="#{planoTrabalhoProjeto.cancelar}" onclick="#{confirm }" id="btCancelar" />
						<h:commandButton value="Cronograma >>" action="#{planoTrabalhoProjeto.submeterDadosGerais}" id="btCronograma" /> 
					</td>
				</tr>
			</tfoot>
			
		</table>
		<br />
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</center>
		<br />
	
	</h:form>
	
</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs');
	        tabs.addTab('interessados', "Lista de Discentes Interessados")
	        tabs.addTab('livre', "Seleção Livre");
	        tabs.activate('interessados');	////padrão

   		    <c:if test="${sessionScope.aba != null}">
				tabs.activate('${sessionScope.aba}');
			</c:if>
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
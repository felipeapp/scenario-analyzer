<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="processoSeletivoVestibular"></a4j:keepAlive>
	<h2><ufrn:subSistema /> > Cadastro de Processo Seletivo</h2>
	<h:form id="form" enctype="multipart/form-data">
		<c:set var="readOnly" value="#{processoSeletivoVestibular.readOnly}" />
		<h:inputHidden value="#{processoSeletivoVestibular.obj.id}" />
		<table class=formulario width="100%">
			<caption class="listagem">Dados do Processo Seletivo</caption>
			<tbody>
			<tr>
				<th class="obrigatorio">Nome:</th>
				<td><h:inputText id="nome"
					value="#{processoSeletivoVestibular.obj.nome}"
					size="60" readonly="#{processoSeletivoVestibular.readOnly}"
					maxlength="160" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Sigla/Nome Abreviado:</th>
				<td><h:inputText id="sigla"
					value="#{processoSeletivoVestibular.obj.sigla}" size="60"
					readonly="#{processoSeletivoVestibular.readOnly}"
					maxlength="60" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano/Período de aplicação:</th>
				<td><h:inputText id="ano"
				    value="#{processoSeletivoVestibular.obj.ano}"
					readonly="#{processoSeletivoVestibular.readOnly}"
					size="4" maxlength="4" immediate="true"
					onkeyup="formatarInteiro(this)" converter="#{ intConverter }"/>.<h:inputText id="periodo"
					value="#{processoSeletivoVestibular.obj.periodo}" size="1"
					maxlength="1" onkeyup="formatarInteiro(this)" immediate="true"
					readonly="#{processoSeletivoVestibular.readOnly}" converter="#{ intConverter }"/>
				</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox id="ativo"
					value="#{processoSeletivoVestibular.obj.ativo}"  
					disabled="#{processoSeletivoVestibular.readOnly}"
					 /></th>
				<td>Ativo.</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox id="processoExterno"
					value="#{processoSeletivoVestibular.obj.processoExterno}"
					disabled="#{processoSeletivoVestibular.readOnly}"
					onchange="submit();" onclick="submit();"/>
				</th>
				<td>
					Processo Seletivo Externo.
					<ufrn:help>Indica que o processo seletivo é excutado externo à instituição e
					os dados dos candidatos aprovados serão importados posteriormente.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>
					<h:selectBooleanCheckbox value="#{processoSeletivoVestibular.obj.entradaDoisPeriodos}"
						disabled="#{processoSeletivoVestibular.readOnly}" id="entradaDoisPeriodos">
						<a4j:support event="onclick" reRender="form" />
					</h:selectBooleanCheckbox>
				</th>
				<td>
					Este Processo Seletivo dará entrada para os dois períodos letivos do ano.
					<a4j:region>
						<a4j:status id="statusEntrada">
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano/Período de entrada dos aprovados:</th>
				<td><h:inputText id="anoEntrada"
				    value="#{processoSeletivoVestibular.obj.anoEntrada}"
					readonly="#{processoSeletivoVestibular.readOnly}"
					size="4" maxlength="4" immediate="true"
					onkeyup="formatarInteiro(this)" converter="#{ intConverter }"/>
					<c:if test="${not processoSeletivoVestibular.obj.entradaDoisPeriodos}">
						.<h:inputText id="periodoEntrada"
						value="#{processoSeletivoVestibular.obj.periodoEntrada}" size="1"
						maxlength="1" onkeyup="formatarInteiro(this)" immediate="true"
						readonly="#{processoSeletivoVestibular.readOnly}" converter="#{ intConverter }"/>
					</c:if>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Forma de Ingresso:</th>
				<td><h:selectOneMenu id="formaIngresso"
					value="#{processoSeletivoVestibular.obj.formaIngresso.id}"
					disabled="#{processoSeletivoVestibular.readOnly}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{formaIngresso.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<c:if test="${ !processoSeletivoVestibular.obj.processoExterno }">
				<tr>
					<th class="obrigatorio">Questionário Sócio Econômico:</th>
					<td><h:selectOneMenu id="qse"
						value="#{processoSeletivoVestibular.obj.questionario.id}" 
						disabled="#{processoSeletivoVestibular.readOnly}">
						<f:selectItems value="#{processoSeletivoVestibular.possiveisQuestionarios}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th><h:selectBooleanCheckbox id="opcaoBeneficioInclusao" 
						value="#{processoSeletivoVestibular.obj.opcaoBeneficioInclusao}"
						disabled="#{processoSeletivoVestibular.readOnly}"
						 /></th>
					<td>O candidato poderá optar pelo benefício de inclusão na inscrição.</td>
				</tr>
				<tr>
					<th>Item no Edital sobre o benefícil de inclusão:</th>
					<td><h:inputText id="itemEditalArgumentoInclusao"
						value="#{processoSeletivoVestibular.obj.itemEditalArgumentoInclusao}" size="60"
						maxlength="60" readonly="#{processoSeletivoVestibular.readOnly}" />
						<ufrn:help>Exemplo: subitem 46.2</ufrn:help>
					</td>
				</tr>
				<!-- EDITAL -->
				<c:if test="${not empty processoSeletivoVestibular.obj.idEdital}">
					<tr>
						<th>Edital Atual:</th>
						<td valign="middle" align="left">
							<a href="${ctx}/verProducao?idProducao=${ processoSeletivoVestibular.obj.idEdital}&key=${ sf:generateArquivoKey(processoSeletivoVestibular.obj.idEdital) }"
							target="_blank"><h:graphicImage value="/img/report.png" style="overflow: visible;" /></a>
						</td>
					</tr>
					<tr>
						<th>Substituir o Edital:</th>
						<td> <t:inputFileUpload value="#{processoSeletivoVestibular.edital}" style="width:95%;" disabled="#{processoSeletivoVestibular.readOnly}"/> </td>
					</tr>
				</c:if>
				<c:if test="${empty processoSeletivoVestibular.obj.idEdital}">
					<tr>
						<th>Edital:</th>
						<td> <t:inputFileUpload value="#{processoSeletivoVestibular.edital}" style="width:95%;" disabled="#{processoSeletivoVestibular.readOnly}"/> </td>
					</tr>
				</c:if>
				<!-- MANUAL DO CANDIDATO -->
				<c:if test="${not empty processoSeletivoVestibular.obj.idManualCandidato}">
					<tr>
						<th>Manual do Candidato Atual:</th>
						<td valign="middle" align="left">
							<a href="${ctx}/verProducao?idProducao=${ processoSeletivoVestibular.obj.idManualCandidato}&key=${ sf:generateArquivoKey(processoSeletivoVestibular.obj.idManualCandidato) }"
							target="_blank"><h:graphicImage value="/img/report.png" style="overflow: visible;" /></a>
						</td>
					</tr>
					<tr>
						<th>Substituir o Manual do Candidato:</th>
						<td> <t:inputFileUpload value="#{processoSeletivoVestibular.manualCandidato}" style="width:95%;" disabled="#{processoSeletivoVestibular.readOnly}"/> </td>
					</tr>
				</c:if>
				<c:if test="${empty processoSeletivoVestibular.obj.idManualCandidato}">
					<tr>
						<th>Manual do Candidato:</th>
						<td> <t:inputFileUpload value="#{processoSeletivoVestibular.manualCandidato}" style="width:95%;" disabled="#{processoSeletivoVestibular.readOnly}"/> </td>
					</tr>
				</c:if>
				<tr>
					<td colspan="2" class="subFormulario">Dados para Geração da GRU</td> 
				</tr>
				<tr>
					<th class="obrigatorio">Valor da inscrição (R$):</th>
					<td><h:inputText id="valor"
						value="#{processoSeletivoVestibular.obj.valorInscricao}"
						readonly="#{processoSeletivoVestibular.readOnly}"
						size="8" maxlength="8"
						style="text-align: right" 
	  					onfocus="javascript:select()" onkeydown="return formataValor(this, event, 2)" >
	   						<f:converter converterId="convertMoeda"/>
					</h:inputText></td>
				</tr>
				<tr>
					<th class="obrigatorio">Data de vencimento da GRU:</th>
					<td><t:inputCalendar id="dataVencimento" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{processoSeletivoVestibular.readOnly}"
						disabled="#{processoSeletivoVestibular.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{processoSeletivoVestibular.obj.dataVencimentoBoleto}" 
						title="Data de vencimento da GRU">
						<f:converter converterId="convertData"/>
						</t:inputCalendar>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">
						Tipo de Arrecadação:
					</th>
					<td>
						<a4j:region>
						<h:selectOneMenu value="#{processoSeletivoVestibular.idTipoArrecadacao}" 
							disabled="#{processoSeletivoVestibular.readOnly}"
							id="tipoArrecadacao">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{processoSeletivo.tiposArrecadacaoCombo}"/>
							<a4j:support action="#{ processoSeletivoVestibular.atualizaConfiguracaoGRU }" event="onchange"
							 reRender="codRecolhimento, tipoGRU"/>
						</h:selectOneMenu>
						<a4j:status id="statusGRU">
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
						</a4j:region>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Configuração da GRU</td> 
				</tr>
				<tr>
					<th class="rotulo">
						Código de Recolhimento:
					</th>
					<td>
						<h:outputText value="#{processoSeletivoVestibular.configuracaoGRU.tipoArrecadacao.codigoRecolhimento.codigo} - #{processoSeletivoVestibular.configuracaoGRU.tipoArrecadacao.codigoRecolhimento.descricao}" id="codRecolhimento"/>
					</td>
				</tr>
				<tr>
					<th class="rotulo">
						Tipo de GRU:
					</th>
					<td>
						<h:panelGroup id="tipoGRU">
							<h:outputText value="GRU Simples" rendered="#{not empty processoSeletivoVestibular.configuracaoGRU and processoSeletivoVestibular.configuracaoGRU.gruSimples}"/>
							<h:outputText value="GRU Cobrança" rendered="#{not empty processoSeletivoVestibular.configuracaoGRU and not processoSeletivoVestibular.configuracaoGRU.gruSimples}"/>
							<h:outputText value="-" rendered="#{empty processoSeletivoVestibular.configuracaoGRU}"/>
						</h:panelGroup>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Calendário de Atividades</td>
				</tr>
				<tr>
					<th class="obrigatorio">Período de alteração da foto:</th>
					<td>de <t:inputCalendar renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{processoSeletivoVestibular.readOnly}"
						disabled="#{processoSeletivoVestibular.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{processoSeletivoVestibular.obj.inicioLimiteAlteracaoFotos}"
						id="inicioLimiteEnvioFoto" title="Início do Período de Alteração da Foto">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						 até <t:inputCalendar
						renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
						maxlength="10" onkeypress="return formataData(this,event)"
						readonly="#{processoSeletivoVestibular.readOnly}"
						disabled="#{processoSeletivoVestibular.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{processoSeletivoVestibular.obj.fimLimiteAlteracaoFotos}"
						id="fimLimiteEnvioFoto" title="Fim do Período de Alteração da Foto" >
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
					</td>
				</tr>
				<tr>
					<th>Período de inscrição para fiscal:</th>
					<td>de <t:inputCalendar renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{processoSeletivoVestibular.readOnly}"
						disabled="#{processoSeletivoVestibular.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{processoSeletivoVestibular.obj.inicioInscricaoFiscal}"
						id="inicioInscricaoFiscal" title="Início do Período de inscrição para fiscal" >
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						 até <t:inputCalendar
						renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
						maxlength="10" onkeypress="return formataData(this,event)"
						readonly="#{processoSeletivoVestibular.readOnly}"
						disabled="#{processoSeletivoVestibular.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{processoSeletivoVestibular.obj.fimInscricaoFiscal}"
						id="fimInscricaoFiscal" title="Fim do Período de inscrição para fiscal" >
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Período de inscrição de candidatos:</th>
					<td>de <t:inputCalendar renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{processoSeletivoVestibular.readOnly}"
						disabled="#{processoSeletivoVestibular.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{processoSeletivoVestibular.obj.inicioInscricaoCandidato}"
						id="inicioInscricaoCandidato" title="Início do Período de inscrição de candidatos" >
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						 até <t:inputCalendar
						renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
						maxlength="10" onkeypress="return formataData(this,event)"
						readonly="#{processoSeletivoVestibular.readOnly}"
						disabled="#{processoSeletivoVestibular.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{processoSeletivoVestibular.obj.fimInscricaoCandidato}"
						id="fimInscricaoCandidato" title="Fim do Período de inscrição de candidatos" >
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
					</td>
				</tr>
				<tr>
					<th>Período de realização das provas:</th>
					<td>de <t:inputCalendar renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" maxlength="10"
						onkeypress="return formataData(this,event)"
						readonly="#{processoSeletivoVestibular.readOnly}"
						disabled="#{processoSeletivoVestibular.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{processoSeletivoVestibular.obj.inicioRealizacaoProva}"
						id="inicioRealizacaoProva" title="Início do Período de realização das provas" >
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						 até <t:inputCalendar
						renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
						maxlength="10" onkeypress="return formataData(this,event)"
						readonly="#{processoSeletivoVestibular.readOnly}"
						disabled="#{processoSeletivoVestibular.readOnly}" popupDateFormat="dd/MM/yyyy"
						value="#{processoSeletivoVestibular.obj.fimRealizacaoProva}"
						id="fimRealizacaoProva" title="Fim do Período de realização das provas" >
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
					</td>
				</tr>
				<tr>
					<th><h:selectBooleanCheckbox id="resultadoFiscal"
						value="#{processoSeletivoVestibular.obj.informaResultadoSelecao}"
						disabled="#{processoSeletivoVestibular.readOnly}" />
						</th>
					<td>Liberar a consulta ao resultado da seleção de fiscais.<ufrn:help>Ao marcar este campo, o resultado da seleção de fiscais será 
						liberado logo após o processamento da seleção. Caso não seja marcado, 
						o resultado poderá ser revisado e, depois de editar e marcar este campo,
						 ser liberado para divulgação.</ufrn:help></td>
				</tr>
			</c:if>
			<tr>
				<th> Estratégia de convocação:
				</th>
				<td>
					<h:selectOneMenu id="classeEstrategiaConvocacao"
						value="#{processoSeletivoVestibular.obj.classeEstrategiaConvocacao}"
						disabled="#{processoSeletivoVestibular.readOnly}">
						<f:selectItem itemValue="" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.estrategiasConvocacaoCombo}" />
					</h:selectOneMenu>
					<ufrn:help>Define as regras que serão utilizadas para a convocação de candidatos aprovados no Vestibular</ufrn:help>
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{processoSeletivoVestibular.confirmButton}"
						action="#{processoSeletivoVestibular.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}"
						action="#{processoSeletivoVestibular.cancelar}" immediate="true" /></td>
				</tr>
			</tfoot>
		</table>
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
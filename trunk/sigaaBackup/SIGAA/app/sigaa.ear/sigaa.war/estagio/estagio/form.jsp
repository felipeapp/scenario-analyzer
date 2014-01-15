<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="estagioMBean"/>
	<a4j:keepAlive beanName="buscaEstagioMBean"/>	
	<h2> <ufrn:subSistema /> &gt; Cadastro de Estágio</h2>
	
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p><br/>		
		<p>Através dessa tela, você poderá cadastrar todas as informações do Estágio referente ao discente selecionado.</p>
	</div>	
	
	<table class="visualizacao" style="width: 90%">
		<caption>Dados da Oferta de Estágio</caption>
		<tr>
			<td colspan="4" class="subFormulario">Dados do Concedente do Estágio</td>
		</tr>
		<tr>
			<th style="width: 30%;">Tipo do Convênio:</th>
			<td colspan="3">
				<h:outputText value="#{estagioMBean.obj.concedente.convenioEstagio.tipoConvenio.descricao}"/>
			</td>
		</tr>	
		<tr>
			<th>CNPJ:</th>
			<td colspan="3">
				<h:outputText value="#{estagioMBean.obj.concedente.pessoa.cpfCnpjFormatado}"/>
			</td>
		</tr>
		<tr>
			<th>Nome:</th>
			<td colspan="3">
				<h:outputText value="#{estagioMBean.obj.concedente.pessoa.nome}"/>																																				
			</td>
		</tr>
		<tr>
			<th>Responsável:</th>
			<td colspan="3">
				<h:outputText value="#{estagioMBean.obj.concedente.responsavel.pessoa.nome}"/>
			</td>
		</tr>
	</table>
	
	<c:set var="discente" value="#{estagioMBean.discente}"/>
	<%@include file="include/_discente.jsp"%>
	
	<c:if test="${!estagioMBean.cadastroAvulso && estagioMBean.obj.supervisor != null}">
		<table class="visualizacao" style="width: 90%">
			<tr>
				<td class="subFormulario" colspan="2">Dados do Supervisor</td>		
			</tr>	
			<tr>
				<th width="30%">CPF do Supervisor:</th>
				<td>
					<h:outputText value="#{estagioMBean.obj.supervisor.cpfCnpjFormatado}"/>																																				
				</td>
			</tr>	
			<tr>
				<th width="30%">Nome do Supervisor:</th>
				<td>
					<h:outputText value="#{estagioMBean.obj.supervisor.nome}"/>																																				
				</td>
			</tr>	
			<tr>
				<th width="30%">E-mail do Supervisor:</th>
				<td>
					<h:outputText value="#{estagioMBean.obj.supervisor.email}"/>																																				
				</td>
			</tr>		
			<tr>
				<th>Descrição das Atividades:</th>
				<td>
					<h:outputText value="#{estagioMBean.obj.descricaoAtividades}"/>
				</td>
			</tr>					
		</table>	
	</c:if>
	<c:if test="${estagioMBean.obj.interesseOferta.oferta.id > 0}">
		<table class="visualizacao" style="width: 90%">
			<tr>
				<td class="subFormulario" colspan="2">Oferta de Estágio</td>		
			</tr>	
			<tr>
				<th width="30%">Titulo:</th>
				<td>
					<h:outputText value="#{estagioMBean.obj.interesseOferta.oferta.titulo}"/>																																				
				</td>
			</tr>	
			<tr>
				<th width="30%">Descrição:</th>
				<td>
					${estagioMBean.obj.interesseOferta.oferta.descricao}																																				
				</td>
			</tr>			
		</table>	
	</c:if>
	<br/>
	
	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Dados do Estágio</caption>	
					
			<c:if test="${!estagioMBean.cadastroAvulso}">
				<tr>
					<th class="obrigatorio">Parecer do Estágio:</th>
					<td colspan="3">
						<a4j:region>
							<h:selectOneMenu value="#{estagioMBean.statusParecer.id}" id="statusParecer"
							 valueChangeListener="#{estagioMBean.carregarStatus }">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
								<f:selectItems value="#{estagioMBean.situacoesAnaliseCombo}"/>
								<a4j:support event="onchange" reRender="form"/>
							</h:selectOneMenu>
						</a4j:region>
					</td>
				</tr>
			</c:if>			
			
			<a4j:region id="obsParecer" rendered="#{!estagioMBean.parecerAprovado && estagioMBean.statusParecer.id > 0}">
				<tr>
					<th class="obrigatorio">Motivo do Parecer:</th>
					<td>
						<h:inputTextarea id="motivoParecer" cols="60" rows="3" value="#{estagioMBean.obj.obsParecerCoordenador}"/>
					</td>
				</tr>
			</a4j:region>
			
			<c:if test="${estagioMBean.parecerAprovado && (estagioMBean.portalCoordenadorGraduacao || estagioMBean.cadastroAvulso || estagioMBean.convenioEstagio)}">
				<tr>
					<th class="obrigatorio">Tipo do Estágio:</th>
					<td colspan="3">
						<h:selectOneMenu value="#{estagioMBean.obj.tipoEstagio.id}" id="tipoEstagio">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{estagioMBean.tipoEstagioCombo}"/>
							<a4j:support event="onchange" reRender="form"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th style="width: 200px;" class="obrigatorio">Carga Horária Semanal:</th>
					<td style="width: 150px;">
						<h:inputText value="#{estagioMBean.obj.cargaHorariaSemanal}" id="cargaHorariaSemanal"
						size="3" maxlength="2" onkeyup="return formatarInteiro(this);" title="Carga Horária Semanal"/> horas
					</td>									
					<th style="width: 150px;">Alterna Teoria e Prática:</th>
					<td>
						<h:selectOneRadio value="#{estagioMBean.obj.alternaTeoriaPratica}" id="alternaTeoriaPratica" layout="lineDirection">
							<f:selectItems value="#{ estagioMBean.simNao}"/>
						</h:selectOneRadio>
					</td>
				</tr>	
				<tr>
					<th class="${ estagioMBean.obj.bolsaObrigatorio ? 'obrigatorio' : '' }">Valor da Bolsa:</th>		
					<td>
						<h:inputText value="#{estagioMBean.obj.valorBolsa}" id="valorBolsa" size="10" maxlength="10" 
							onkeypress="return(formataValor(this, event, 2))" style="text-align: right;">
							<f:converter converterId="convertMoeda"/>
						</h:inputText>							
					</td>
					<th class="${ estagioMBean.obj.bolsaObrigatorio ? 'obrigatorio' : '' }">Valor Aux. Transporte:</th>		
					<td>
						<h:inputText value="#{estagioMBean.obj.valorAuxTransporte}" id="valorAuxTransporte" size="5" maxlength="5" 
							onkeypress="return(formataValor(this, event, 2))" style="text-align: right;">
							<f:converter converterId="convertMoeda"/>
						</h:inputText> ao dia							
					</td>			
				</tr>
				<tr>
					<th class="obrigatorio">Professor  Orientador do Estágio:</th>
					<td colspan="3">
						<a4j:region id="orientadorDocente">
							<h:inputText value="#{estagioMBean.obj.orientador.pessoa.nome}" id="nomeDocente" style="width: 400px;"/>
							<rich:suggestionbox width="400" height="100" for="nomeDocente" id="sbDocente"
								minChars="1" nothingLabel="#{servidor.textoSuggestionBox}"
								suggestionAction="#{servidor.autocompleteDocente}" var="_servidor" fetchValue="#{_servidor.nome}"
								onsubmit="$('form:imgStDocente').style.display='inline';" 
							    oncomplete="$('form:imgStDocente').style.display='none';">
	
								<h:column>
									<h:outputText value="#{_servidor.siape}"/>
								</h:column>
	
								<h:column>
									<h:outputText value="#{_servidor.nome}"/>
								</h:column>
								
			                   <a4j:support event="onselect">
									<f:setPropertyActionListener value="#{_servidor.id}" target="#{estagioMBean.obj.orientador.id}" />
							  </a4j:support>								
								
							</rich:suggestionbox>
							<h:graphicImage id="imgStDocente" style="display:none; overflow: visible;" value="/img/indicator.gif"/>	
						</a4j:region>			
					</td>
				</tr>					
				<tr>
					<td colspan="4" class="subFormulario">Local de Estágio</td>
				</tr>
				<a4j:region>
					<tr>
						<th class="obrigatorio">CPF/CNPJ:</th>
						<td colspan="3">						
							<h:inputText id="campocnpj" onkeypress="return formataCNPJ(this, event, null)"	value="#{estagioMBean.obj.empresaEstagio.cpf_cnpj}" size="19" maxlength="18"
								rendered="#{ estagioMBean.obj.concedente.convenioEstagio.agenteIntegrador }" >
								<f:converter converterId="convertCpf"/>
								<f:param name="type" value="cnpj" />
								<a4j:support actionListener="#{estagioMBean.buscarEmpresaEstagio}" event="onblur" reRender="nomeEmpresa"/>
							</h:inputText>
							<h:outputText value="#{ estagioMBean.obj.empresaEstagio.cpfCnpjFormatado }" rendered="#{ !estagioMBean.obj.concedente.convenioEstagio.agenteIntegrador }"/>											
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Nome:</th>
						<td colspan="3">																																				
							<h:inputText id="nomeEmpresa" value="#{estagioMBean.obj.empresaEstagio.nome}" onkeyup="CAPS(this);" size="60" maxlength="120"
								disabled="#{ !estagioMBean.podeAlterarEmpresaEstagio }" rendered="#{ estagioMBean.obj.concedente.convenioEstagio.agenteIntegrador }"/>
							<h:outputText value="#{ estagioMBean.obj.empresaEstagio.nome }" rendered="#{ !estagioMBean.obj.concedente.convenioEstagio.agenteIntegrador }"/>
						</td>
					</tr>
				</a4j:region>	
				<tr>
					<td colspan="4" class="subFormulario">Horário de Estágio</td>
				</tr>			
				<tr>
					<th class="obrigatorio">Data de Início do Estágio:</th>
					<td>
						<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
									maxlength="10" onkeypress="return formataData(this,event)" value="#{estagioMBean.obj.dataInicio}" id="dataInicio"/> 							
					</td>
					<th>Data de Fim do Estágio:<span class="obrigatorio">&nbsp;</span></th>
					<td>
						<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
									maxlength="10" onkeypress="return formataData(this,event)" value="#{estagioMBean.obj.dataFim}" id="dataFim"/> 							
					</td>
				</tr>	
				<tr>
					<td colspan="4">
						 <%@include file="include/_horario_estagio.jsp" %>
					</td>
				</tr>	
				<tr>
					<td colspan="4" class="subFormulario">Dados do Seguro contra Acidentes Pessoais</td>
				</tr>	
				
				<tr>
					<th class="obrigatorio">CNPJ:</th>
					<td colspan="3">						
						<h:inputText id="cnpjSeguradora" onkeypress="return formataCNPJ(this, event, null)"	
						value="#{estagioMBean.obj.cnpjSeguradora}" size="19" maxlength="18">
							<f:converter converterId="convertCpf"/>
						</h:inputText>											
					</td>
				</tr>			
				<tr>
					<th class="obrigatorio">Seguradora:</th>
					<td colspan="3">																																				
						<h:inputText id="nome" value="#{estagioMBean.obj.seguradora}" onkeyup="CAPS(this);" size="60" maxlength="100"/>
					</td>
				</tr>	
				
				<tr>
					<th class="obrigatorio">Apólice do Seguro:</th>		
					<td>
						<h:inputText value="#{estagioMBean.obj.apoliceSeguro}" id="apoliceSeguro" size="20" maxlength="50"/>
					</td>
					<th class="obrigatorio">Valor Seguro:</th>		
					<td>
						<h:inputText value="#{estagioMBean.obj.valorSeguro}" id="valorSeguro" size="10" maxlength="10" 
							onkeypress="return(formataValor(this, event, 2))" style="text-align: right;">
							<f:converter converterId="convertMoeda"/>
						</h:inputText>							
					</td>			
				</tr>
				
				<c:if test="${estagioMBean.cadastroAvulso || estagioMBean.obj.interesseOferta == null}">
					<tr>
						<td colspan="4" class="subFormulario">Supervisor do Estágio</td>
					</tr>	
					
					<tr>
						<th width="20%" class="obrigatorio">
							CPF do Supervisor: 
						</th>
						<td>						
							<h:inputText id="cpf" onkeypress="return formataCPF(this, event, null)"	value="#{estagioMBean.obj.supervisor.cpf_cnpj}" size="19" maxlength="14" autocomplete="false">
								<f:converter converterId="convertCpf"/>
								<f:param name="type" value="cpf" />
								<a4j:support actionListener="#{estagioMBean.buscarCPF}" event="onchange" reRender="supervisor, supervisorEmail"/>
							</h:inputText>					
							<ufrn:help>O Supervisor será a pessoa que acompanhará o estágio do Discente na Instituição Concedente.</ufrn:help>									
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Nome do Supervisor:</th>
						<td>																																				
							<h:inputText id="supervisor" value="#{estagioMBean.obj.supervisor.nome}" onkeyup="CAPS(this);" 
							size="60" maxlength="80"/>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">E-mail do Supervisor:</th>
						<td>																																				
							<h:inputText id="supervisorEmail" value="#{estagioMBean.obj.supervisor.email}" size="60" maxlength="80"/>
						</td>
					</tr>
					
				</c:if>
								
				<tr>
					<td class="subFormulario obrigatorio" colspan="4">Descrição das Atividades<span class="obrigatorio">&nbsp;</span></td>		
				</tr>
				<tr>
					<td colspan="4" align="center">
						<h:inputTextarea value="#{estagioMBean.obj.descricaoAtividades}" cols="100" rows="8"/>
						<ufrn:help>Descrição das Atividades a serem exercidas pelo Discente no Estágio.</ufrn:help>
					</td>
				</tr>							
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="Cancelar" action="#{estagioMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancel"/>
						<h:commandButton value="Próximo >>" action="#{estagioMBean.proximoPasso}" id="btProximo"/>
					</td>
				</tr>
			</tfoot>													
		</table>				
	</h:form>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	
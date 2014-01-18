<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>
	
	
	<a4j:keepAlive beanName="buscaPadraoParticipanteExtensaoMBean"/>
	
	<%-- Sempre que um novo caso de uso chamar a busca padrão de participantes, colocar os MBens dos casos de uso aqui. --%>
	
	<a4j:keepAlive beanName="realizaInscricaoParticipanteCoordenadorMBean"/>
	<a4j:keepAlive beanName="gerenciarInscritosCursosEEventosExtensaoMBean"/>
	
	<a4j:keepAlive beanName="gerenciarParticipantesExtensaoMBean"/>
	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean"/>
	
	
	<h2>
		<ufrn:subSistema/> > Busca Participantes Extensão > ${buscaPadraoParticipanteExtensaoMBean.operacao}
	</h2>
	
	
	<div class="descricaoOperacao">
		<h:outputText escape="false" value="#{buscaPadraoParticipanteExtensaoMBean.descricaoOperacao}"/>
	</div>
	
	
	<h:form id="formBuscaPadraoParticipantesExtensao">
	
		<table class="formulario" style="width: 80%; margin-bottom: 20px;">
			
			<caption>Filtros da Busca</caption>
			<tbody>
					<tr>
						<td style="width: 2%">
							<h:selectBooleanCheckbox value="#{buscaPadraoParticipanteExtensaoMBean.buscarCPF}" styleClass="noborder" id="checkCPF"/>
						</td>
						<td style="width: 15%">
							CPF:
						</td>
						<td style="width: 83%">
							<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.cpf}" maxlength="11" size="12" 
								onfocus="getEl('formBuscaPadraoParticipantesExtensao:checkCPF').dom.checked = true;"/>
						</td>
					</tr>
					
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{buscaPadraoParticipanteExtensaoMBean.buscarPassaporte}" styleClass="noborder" id="checkPassaporte"/>
						</td>
						<td>Passaporte:</td>
						<td>
							<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.passaporte}" maxlength="20" size="25" 
								onfocus="getEl('formBuscaPadraoParticipantesExtensao:checkPassaporte').dom.checked = true;"/>
						</td>
					</tr>
					
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{buscaPadraoParticipanteExtensaoMBean.buscarNome}" styleClass="noborder" id="checkNome"/>
						</td>
						<td>Nome:</td>
						<td>
							<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.nome}" maxlength="100" size="100" 
								onfocus="getEl('formBuscaPadraoParticipantesExtensao:checkNome').dom.checked = true;"/>
						</td>
					</tr>
				
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{buscaPadraoParticipanteExtensaoMBean.buscarEmail}" styleClass="noborder" id="checkEmail"/>
						</td>
						<td>Email:</td>
						<td>
							<h:inputText value="#{buscaPadraoParticipanteExtensaoMBean.email}" maxlength="100" size="100" 
								onfocus="getEl('formBuscaPadraoParticipantesExtensao:checkEmail').dom.checked = true;"/>
						</td>
					</tr>
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="cmdBucarParticipante" value="Buscar" actionListener="#{buscaPadraoParticipanteExtensaoMBean.buscarParticipante}" />
						<h:commandButton id="cmdCancelar" value=" Cancelar " action="#{buscaPadraoParticipanteExtensaoMBean.cancelarPesquiasParticipanteExtensao}" immediate="true" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	
	
	
		<div class="infoAltRem" style="margin-bottom: 10px;">
			<h:graphicImage value="/img/novo_usuario.gif" style="overflow: visible;" styleClass="noborder" height="16" width="16"/>
			<h:commandLink value="Realizar Novo Cadastro" action="#{buscaPadraoParticipanteExtensaoMBean.preCadastrarParticipante}" />
		
			<h:graphicImage value="/img/seta.gif"/>: Selecionar Participante
			
			<t:div rendered="#{buscaPadraoParticipanteExtensaoMBean.permitirAlterarCadastro}">
				<h:graphicImage value="/img/alterar.gif"/>: Alterar Dados do Participante
			</t:div>
		</div>
	
	
		<c:if test="${buscaPadraoParticipanteExtensaoMBean.qtdCadastros > 0 }">
			<table class="listagem" style="width: 100%;">
				<caption>Participantes de Extensão ( ${buscaPadraoParticipanteExtensaoMBean.qtdCadastros} ) </caption>
				
				<thead>
					<tr>
						<th style="width: 12%;">
							CPF
						</th>
						<th style="width: 12%;">
							Passaporte
						</th>
						<th style="width: 39%;">
							Nome
						</th>
						<th style="width: 30%;">
							Email
						</th>
						<th style="width: 10%;">
							Data de Nascimento
						</th>
						<th style="width: 1%;">
						
						</th>
						<th style="width: 1%;">
						
						</th>
					</tr>
				</thead>
				
				<c:forEach var="cadastro" items="#{buscaPadraoParticipanteExtensaoMBean.cadastros}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
						<td>
							<ufrn:format type="cpf_cnpj" valor="${cadastro.cpf}"/>
						</td>
						<td>
							${cadastro.passaporte}
						</td>
						<td>
							${cadastro.nome}
						</td>
						<td>
							${cadastro.email}
						</td>
						<td>
							${cadastro.dataNascimentoFormatada}
						</td>
						<td>
							<h:commandLink action="#{buscaPadraoParticipanteExtensaoMBean.selecionouParticipante}" title="Selecionar Participante" id="selecionarParticipante">
								<h:graphicImage value="/img/seta.gif"/>
								<f:param name="idParticipanteSelecionado" value="#{cadastro.id}"/>
							</h:commandLink>
						</td>
						<td>
							<h:commandLink action="#{buscaPadraoParticipanteExtensaoMBean.preAlterarCadastroParticipante}" title="Alterar Dados do Participante" id="alteraCadastroParticipante"
								rendered="#{buscaPadraoParticipanteExtensaoMBean.permitirAlterarCadastro}">
								<h:graphicImage value="/img/alterar.gif"/>
								<f:param name="idCadastroParticipanteSelecionado" value="#{cadastro.id}"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		
		</c:if> 
	
	</h:form>


</f:view>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Cadastro de Restrições de Inscrição no Vestibular</h2>
	
	<div class="descricaoOperacao">Este formulário permite definir
	restrições às inscrições para o Vestibular. São dois tipos de
	restrições:
	<ul>
		<li>Exclusiva à: define que poderão se inscrever para o curso
		somente os CPFs indicados. (Ex.: os candidatos aptos para Música -
		Bacharelado);</li>
		<li>Exceto à: define quais CPFs não poderão se inscrever para o
		curso ofertato.</li>
	</ul>
	Selecione um Processo Seletivo, o curso ofertado (as matrizes curriculares são
	listadas de acordo com o cadastro de oferta de vagas), e o tipo de
	restrição. Infome os CPFs para incluir na restrição. Para remover o CPF
	da lista, basta clicar no ícone correspondente.</div>

	<h:form id="form">
		<a4j:keepAlive beanName="restricaoInscricaoVestibular"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Dados da Restrição</caption>
			<tr>
				<th class="obrigatorio">Processo Seletivo:</th>
				<td>
					<h:selectOneMenu id="processoSeletivo" onchange="submit()" 
						disabled="#{restricaoInscricaoVestibular.readOnly}"
						value="#{restricaoInscricaoVestibular.obj.processoSeletivoVestibular.id}"
						valueChangeListener="#{restricaoInscricaoVestibular.processoSeletivoListener}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Curso Ofertado:</th>
				<td>
					<h:selectOneMenu id="primeiraOpcao" 
						disabled="#{restricaoInscricaoVestibular.readOnly}"
						value="#{restricaoInscricaoVestibular.obj.matrizCurricular.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{restricaoInscricaoVestibular.listaMatrizOfertaCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Tipo de Restrição:</th>
				<td>
					<h:selectOneMenu value="#{restricaoInscricaoVestibular.obj.tipoRestricao }" id="tipoRestricao" disabled="#{restricaoInscricaoVestibular.readOnly}">
						<f:selectItems value="#{restricaoInscricaoVestibular.tiposRestricoes}" id="tiposRestricoes" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td>
					<h:inputText value="#{ restricaoInscricaoVestibular.obj.descricao }" id="descricao" size="60" maxlength="120" readonly="#{restricaoInscricaoVestibular.readOnly}"/> 
				</td>
			</tr>
			<tr>
				<th valign="top">Observações:</th>
				<td>
					<h:inputTextarea value="#{ restricaoInscricaoVestibular.obj.observacao }" id="observacoes" rows="4" cols="60" readonly="#{restricaoInscricaoVestibular.readOnly}"
			  			onkeyup="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;" 
						onkeydown="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;">
					</h:inputTextarea>
					<br/>
					Você pode digitar <h:inputText id="observacoes_count" size="3" value="#{250 - fn:length(restricaoInscricaoVestibular.obj.observacao)}" disabled="true" readonly="#{restricaoInscricaoVestibular.readOnly}"/> caracteres.
				</td>
			</tr>
			<c:if test="${not restricaoInscricaoVestibular.readOnly}">
				<tr>
					<th class="obrigatorio">CPF:</th>
					<td>
						<h:inputText
							value="#{ restricaoInscricaoVestibular.cpf }" size="14" 
							onkeypress="formataCPF(this, event, null)" id="txtCPF">
							<f:converter converterId="convertCpf" />
						</h:inputText>
						<h:commandButton value="Incluir CPF" action="#{restricaoInscricaoVestibular.adicionaCPF}" id="adicionaCPF"/>
					</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{restricaoInscricaoVestibular.confirmButton}" action="#{restricaoInscricaoVestibular.cadastrar}" id="cadastrar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{restricaoInscricaoVestibular.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		<br/>
		<c:if test="${not empty restricaoInscricaoVestibular.obj.cpfs}">
			<c:if test="${not restricaoInscricaoVestibular.readOnly}">
				<div class="infoAltRem"  style="width: 100%;">
					<h:graphicImage value="/img/delete.gif"	style="overflow: visible;" />: Remover CPF da Lista
				</div>
			</c:if>
			<table class="listagem" style="width: 80%;">
				<caption>Lista de CPF da Restrição</caption>
				<c:forEach items="#{restricaoInscricaoVestibular.obj.cpfs}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td></td>
						<td><ufrn:format type="cpf_cnpj" valor="${item}" /></td>
						<td width="2%">
							<h:commandLink title="Remover" rendered="#{not restricaoInscricaoVestibular.readOnly}"
								action="#{restricaoInscricaoVestibular.removeCPF}" style="border: 0;">
								<f:param name="cpf" value="#{item}" />
								<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
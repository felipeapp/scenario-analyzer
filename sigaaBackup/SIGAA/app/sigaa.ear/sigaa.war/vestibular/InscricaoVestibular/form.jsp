<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Valida��o das Inscri��es do Vestibular</h2>

	<h:form id="form">
		<a4j:keepAlive beanName="validacaoCandidatoBean"></a4j:keepAlive>
		<table class="visualizacao"  width="80%">
			<caption>Resumo da Inscri��o</caption>
			<tr>
				<th>CPF:</th>
				<td><ufrn:format type="cpf_cnpj"
					valor="${validacaoCandidatoBean.obj.pessoa.cpf_cnpj}"></ufrn:format></td>
			</tr>
			<c:if test="${not empty validacaoCandidatoBean.obj.pessoa.passaporte}">
				<tr>
					<th>Passaporte:</th>
					<td><h:outputText
						value="#{validacaoCandidatoBean.obj.pessoa.passaporte}" /></td>
				</tr>
			</c:if>
			<tr>
				<th>Nome:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.pessoa.nome}" /></td>
			</tr>
			<tr>
				<th>�rea de conhecimento:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[0].curso.areaVestibular.descricao}" /></td>
			</tr>
			<tr>
				<th>Primeira op��o:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[0].curso.municipio}" />
				- <h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[0]}" /></td>
			</tr>
			<tr>
				<th>Segunda op��o:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[1].curso.municipio}" />
				- <h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[1]}" /></td>
			</tr>
			<tr>
				<th>L�ngua estrangeira:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.linguaEstrangeira.denominacao}" /></td>
			</tr>
			<tr>
				<th>Regi�o preferencial de prova:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.regiaoPreferencialProva.denominacao}" /></td>
			</tr>
			<tr>
				<th>Taxa de Inscri��o:</th>
				<td><ufrn:format type="moeda"
					valor="${validacaoCandidatoBean.obj.valorInscricao}" /></td>
			</tr>
			<tr>
				<th>Situa��o da Inscri��o:</th>
				<td>
					<h:outputText value="Validada" rendered="#{validacaoCandidatoBean.obj.validada}" />
					<h:outputText value="N�o Validada" rendered="#{not validacaoCandidatoBean.obj.validada}" />
				</td>
			</tr>
		</table>
		<br/>
		<table class="formulario" width="60%">
			<caption>Valida��o/Invalida��o da Inscri��o</caption>
			<tbody>
				<tr>
					<th valign="top">Observa��o:</th>
					<td>
						<h:inputTextarea value="#{ validacaoCandidatoBean.obj.observacao }" id="observacoes" rows="4" cols="60" readonly="#{validacaoCandidatoBean.readOnly}"
				  			onkeyup="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;" 
							onkeydown="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;">
						</h:inputTextarea>
						<br/>
						Voc� pode digitar <h:inputText id="observacoes_count" size="3" value="#{250 - fn:length(validacaoCandidatoBean.obj.observacao)}" disabled="true" readonly="#{validacaoCandidatoBean.readOnly}"/> caracteres.
					</td>
				</tr>
				<tr>
					<th>Inscri��o Validada:</th>
					<td nowrap="nowrap">
						<h:selectOneRadio value="#{validacaoCandidatoBean.obj.validada}" id="inscricaoValidada" > 
							<f:selectItems value="#{validacaoCandidatoBean.simNao}"  />
						</h:selectOneRadio>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Atualizar Inscri��o" action="#{validacaoCandidatoBean.cadastrar}" id="atualizaInscricao"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{validacaoCandidatoBean.cancelar}" id="cancela" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
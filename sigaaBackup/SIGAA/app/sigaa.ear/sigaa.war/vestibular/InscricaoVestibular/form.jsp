<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Validação das Inscrições do Vestibular</h2>

	<h:form id="form">
		<a4j:keepAlive beanName="validacaoCandidatoBean"></a4j:keepAlive>
		<table class="visualizacao"  width="80%">
			<caption>Resumo da Inscrição</caption>
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
				<th>Área de conhecimento:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[0].curso.areaVestibular.descricao}" /></td>
			</tr>
			<tr>
				<th>Primeira opção:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[0].curso.municipio}" />
				- <h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[0]}" /></td>
			</tr>
			<tr>
				<th>Segunda opção:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[1].curso.municipio}" />
				- <h:outputText
					value="#{validacaoCandidatoBean.obj.opcoesCurso[1]}" /></td>
			</tr>
			<tr>
				<th>Língua estrangeira:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.linguaEstrangeira.denominacao}" /></td>
			</tr>
			<tr>
				<th>Região preferencial de prova:</th>
				<td><h:outputText
					value="#{validacaoCandidatoBean.obj.regiaoPreferencialProva.denominacao}" /></td>
			</tr>
			<tr>
				<th>Taxa de Inscrição:</th>
				<td><ufrn:format type="moeda"
					valor="${validacaoCandidatoBean.obj.valorInscricao}" /></td>
			</tr>
			<tr>
				<th>Situação da Inscrição:</th>
				<td>
					<h:outputText value="Validada" rendered="#{validacaoCandidatoBean.obj.validada}" />
					<h:outputText value="Não Validada" rendered="#{not validacaoCandidatoBean.obj.validada}" />
				</td>
			</tr>
		</table>
		<br/>
		<table class="formulario" width="60%">
			<caption>Validação/Invalidação da Inscrição</caption>
			<tbody>
				<tr>
					<th valign="top">Observação:</th>
					<td>
						<h:inputTextarea value="#{ validacaoCandidatoBean.obj.observacao }" id="observacoes" rows="4" cols="60" readonly="#{validacaoCandidatoBean.readOnly}"
				  			onkeyup="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;" 
							onkeydown="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;">
						</h:inputTextarea>
						<br/>
						Você pode digitar <h:inputText id="observacoes_count" size="3" value="#{250 - fn:length(validacaoCandidatoBean.obj.observacao)}" disabled="true" readonly="#{validacaoCandidatoBean.readOnly}"/> caracteres.
					</td>
				</tr>
				<tr>
					<th>Inscrição Validada:</th>
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
						<h:commandButton value="Atualizar Inscrição" action="#{validacaoCandidatoBean.cadastrar}" id="atualizaInscricao"/> 
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
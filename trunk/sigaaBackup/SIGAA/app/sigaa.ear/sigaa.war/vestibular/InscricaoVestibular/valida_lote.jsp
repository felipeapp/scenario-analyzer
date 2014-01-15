<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Validação das Inscrições do Vestibular em Lote</h2>

	<div class="descricaoOperacao">Este formulário permite validar as inscrições dos candidatos ao
	Vestibular. Para validar as inscrições, informe o número de inscrição do mesmo. Você
	poderá informar uma lista de inscrições a validar. A lista de inscrições deverá ser separada por 
	vírgula, ponto e vírgula ou tabulação. Exemplo: 123, 456, 789, 987, 654, 321.<br/>
	Caso deseje, poderá informar uma observação que será anotada para todas inscrições da lista de validação.</div>
	<h:form id="form">
		<a4j:keepAlive beanName="validacaoCandidatoBean"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Dados da Validação</caption>
			<tr>
				<th class="obrigatorio">Processo Seletivo:</th>
				<td>
					<h:selectOneMenu id="processoSeletivo"
						value="#{validacaoCandidatoBean.obj.processoSeletivo.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th valign="top">Observações:</th>
				<td>
					<h:inputTextarea value="#{ validacaoCandidatoBean.obj.observacao }" id="observacoes" rows="4" cols="60" 
			  			onkeyup="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;" 
						onkeydown="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;">
					</h:inputTextarea>
					<br/>
					Você pode digitar <h:inputText id="observacoes_count" size="3" value="#{250 - fn:length(validacaoCandidatoBean.obj.observacao)}" disabled="true" /> caracteres.
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Lista de Inscrições:</th>
				<td>
					<h:inputTextarea id="listaInscricao" value="#{validacaoCandidatoBean.listaInscricao}" rows="4" cols="60" />
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Inscrições Separados por:</th>
				<td>
					<h:selectOneRadio value="#{validacaoCandidatoBean.separador }" id="separador">
						<f:selectItems value="#{validacaoCandidatoBean.possiveisSeparadores }"  />
					</h:selectOneRadio>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Validar Inscrições" action="#{validacaoCandidatoBean.validaListaInscricao}" id="validaInscricoes"/> 
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
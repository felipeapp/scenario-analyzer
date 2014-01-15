<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Valida��o das Inscri��es do Vestibular em Lote</h2>

	<div class="descricaoOperacao">Este formul�rio permite validar as inscri��es dos candidatos ao
	Vestibular. Para validar as inscri��es, informe o n�mero de inscri��o do mesmo. Voc�
	poder� informar uma lista de inscri��es a validar. A lista de inscri��es dever� ser separada por 
	v�rgula, ponto e v�rgula ou tabula��o. Exemplo: 123, 456, 789, 987, 654, 321.<br/>
	Caso deseje, poder� informar uma observa��o que ser� anotada para todas inscri��es da lista de valida��o.</div>
	<h:form id="form">
		<a4j:keepAlive beanName="validacaoCandidatoBean"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Dados da Valida��o</caption>
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
				<th valign="top">Observa��es:</th>
				<td>
					<h:inputTextarea value="#{ validacaoCandidatoBean.obj.observacao }" id="observacoes" rows="4" cols="60" 
			  			onkeyup="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;" 
						onkeydown="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;">
					</h:inputTextarea>
					<br/>
					Voc� pode digitar <h:inputText id="observacoes_count" size="3" value="#{250 - fn:length(validacaoCandidatoBean.obj.observacao)}" disabled="true" /> caracteres.
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Lista de Inscri��es:</th>
				<td>
					<h:inputTextarea id="listaInscricao" value="#{validacaoCandidatoBean.listaInscricao}" rows="4" cols="60" />
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Inscri��es Separados por:</th>
				<td>
					<h:selectOneRadio value="#{validacaoCandidatoBean.separador }" id="separador">
						<f:selectItems value="#{validacaoCandidatoBean.possiveisSeparadores }"  />
					</h:selectOneRadio>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Validar Inscri��es" action="#{validacaoCandidatoBean.validaListaInscricao}" id="validaInscricoes"/> 
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
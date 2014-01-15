<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.VESTIBULAR} ); %>
<f:view>
	<h2><ufrn:subSistema /> > Cadastro de CPFs de Isentos da Taxa de Inscri��o</h2>

	<div class="descricaoOperacao">Este formul�rio permite incluir/excluir
	pessoas que ter�o isen��o total/parcial da taxa de inscri��o do
	Vestibular. Para incluir candidatos, informe o CPF do mesmo. Voc�
	poder� informar uma lista de CPFs para cadastrar. A lista de CPFs dever� ser separada por 
	v�rgula, ponto e v�rgula ou tabula��o. Exemplo: 123.456.789-01, 987.654.321-10.<br>
	Os CPFs informados
	n�o precisam estar formatados com pontos e tra�o: s�o aceitos tamb�m
	apenas n�meros (ex.: 01398712302). Caso a isen��o n�o seja total,
	informe um valor de taxa de inscri��o a ser pago pelo candidato.</div>
	<h:form id="form">
		<a4j:keepAlive beanName="isencaoTaxaInscricao"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Dados da Isen��o</caption>
			<tr>
				<th class="obrigatorio">Processo Seletivo:</th>
				<td>
					<h:selectOneMenu id="processoSeletivo"
						value="#{isencaoTaxaInscricao.obj.processoSeletivoVestibular.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Isen��o Total da Taxa de Inscri��o:</th>
				<td>
					<h:selectBooleanCheckbox value="#{isencaoTaxaInscricao.obj.isentoTotal}" id="isencaoTotal">
						<a4j:support event="onclick" reRender="form"></a4j:support>
					</h:selectBooleanCheckbox>
				</td>
			</tr>
			<c:if test="${not isencaoTaxaInscricao.obj.isentoTotal}">
				<tr>
					<th class="obrigatorio">Valor da Taxa de Inscri��o a Pagar:</th>
					<td>
						R$ <h:inputText value="#{isencaoTaxaInscricao.obj.valor}" id="valorTaxa" size="8" maxlength="10" style="text-align: right" onkeypress="return(formataValor(this, event, 2))">
							<f:converter converterId="convertMoeda"/>
						</h:inputText>
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="obrigatorio">Tipo de Isento:</th>
				<td>
					<h:selectOneRadio value="#{isencaoTaxaInscricao.obj.tipo}" id="tipoIsento">
						<f:selectItems value="#{isencaoTaxaInscricao.tiposIsento}"/>
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th valign="top">Observa��es:</th>
				<td>
					<h:inputTextarea value="#{ isencaoTaxaInscricao.obj.observacao }" id="observacoes" rows="4" cols="60" 
			  			onkeyup="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;" 
						onkeydown="if (this.value.length > 250) this.value = this.value.substring(0, 250); $('form:observacoes_count').value = 250 - this.value.length;">
					</h:inputTextarea>
					<br/>
					Voc� pode digitar <h:inputText id="observacoes_count" size="3" value="#{250 - fn:length(isencaoTaxaInscricao.obj.observacao)}" disabled="true" /> caracteres.
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Lista de CPF:</th>
				<td>
					<h:inputTextarea id="listaCPF" value="#{isencaoTaxaInscricao.listaCPF}" rows="4" cols="60" />
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">CPFs Separado por:</th>
				<td>
					<h:selectOneRadio value="#{isencaoTaxaInscricao.separador }" id="separador">
						<f:selectItems value="#{isencaoTaxaInscricao.possiveisSeparadores }"  />
					</h:selectOneRadio>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Validar CPFs" action="#{isencaoTaxaInscricao.validaListaCPF}" id="validaCPFs"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{isencaoTaxaInscricao.cancelar}" id="cancela" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
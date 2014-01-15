<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Justificativa de Ausência</h2>

	<div class="descricaoOperacao">
		Altere o conceito do fiscal para o referido Processo Seletivo.
		Caso o fiscal seja considerado INSUFICIENTE, é obrigatório informar o motivo
		nas observações a respeito do fiscal.
    </div>
	<h:form id="form" enctype="multipart/form-data">
		<a4j:keepAlive beanName="conceitoFiscal" />
		<table class="formulario">
			<caption>Conceito do Fiscal</caption>
			<tbody>
			
			<c:if test="${conceitoFiscal.obj.perfilDiscente}">
				<tr>
					<th><b>Fiscal:</b></th>
					<td>${conceitoFiscal.obj.discente.matriculaNome}</td>
				</tr>
				<tr>
					<th><b>Curso:</b></th>
					<td>${conceitoFiscal.obj.discente.curso.descricaoCompleta}</td>
				</tr>
			</c:if>
			<c:if test="${conceitoFiscal.obj.perfilServidor}">
				<tr>
					<th><b>Fiscal:</b></th>
					<td>${conceitoFiscal.obj.servidor.siapeNome}</td>
				</tr>
				<tr>
					<th><b>Unidade:</b></th>
					<td>${conceitoFiscal.obj.servidor.unidade.nome}</td>
				</tr>
			</c:if>
			<tr>
				<th><b>Processo Seletivo:</b></th>
				<td>${conceitoFiscal.obj.processoSeletivoVestibular.nome}</td>
			</tr>
			<tr>
				<th valign="top"><b>Local de Aplicação:</b></th>
				<td>${conceitoFiscal.obj.localAplicacaoProva.nome}</td>
			</tr>
			<tr>
				<th valign="top" width="25%">Conceito:</th>
				<td>
					<h:selectOneMenu value="#{conceitoFiscal.obj.conceito}">
						<f:selectItems value="#{conceitoFiscal.allConceitoCombo}" />
					</h:selectOneMenu>
				</td>	
			</tr>
			<tr>
				<th>Observação:<br/>(Não visualizada pelo fiscal)</th>
				<td>
					<h:inputTextarea value="#{ conceitoFiscal.obj.observacao }" id="observacoes" rows="4" cols="60" 
			  			onkeyup="if (this.value.length > 600) this.value = this.value.substring(0, 600); $('form:observacoes_count').value = 600 - this.value.length;" 
						onkeydown="if (this.value.length > 600) this.value = this.value.substring(0, 600); $('form:observacoes_count').value = 600 - this.value.length;">
					</h:inputTextarea>
					<br/>
					Você pode digitar <h:inputText id="observacoes_count" size="3" value="#{600 - fn:length(conceitoFiscal.obj.observacao)}" disabled="true" /> caracteres.
					</div>
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden	value="#{conceitoFiscal.obj.id}" />
						<h:commandButton value="Alterar"  action="#{conceitoFiscal.cadastrar}" />
						<h:commandButton value="<< Escolher Outro Fiscal"  action="#{conceitoFiscal.buscar}" /> 
						<h:commandButton value="Cancelar" action="#{conceitoFiscal.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

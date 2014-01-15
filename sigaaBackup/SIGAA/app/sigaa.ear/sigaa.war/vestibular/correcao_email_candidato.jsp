<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2 class="tituloPagina"> <ufrn:subSistema /> &gt; Correção de E-Mail de Candidato </h2>

	<h:form id="form">
		<a4j:keepAlive beanName="correcaoEmailCandidatoMBean"></a4j:keepAlive>
		<table class="formulario" width="50%">
			<caption>Dados Pessoais</caption>
			<tbody>
				<tr>
					<th class="rotulo">CPF:</th>
					<td><ufrn:format type="cpf_cnpj" valor="${correcaoEmailCandidatoMBean.obj.cpf_cnpj}"></ufrn:format></td>
				</tr>
				<tr>
					<th class="rotulo">Nome:</th>
					<td><h:outputText id="labelNome" value="#{correcaoEmailCandidatoMBean.obj.nome}"/></td>
				</tr>
				<tr>
					<th class="rotulo">Data de Nascimento:</th>
					<td><h:outputText id="labelDataNasc" value="#{correcaoEmailCandidatoMBean.obj.dataNascimento}"/></td>
				</tr>
				<tr>
					<th class="rotulo">Nome da Mãe:</th>
					<td><h:outputText id="labelNomeMae" value="#{correcaoEmailCandidatoMBean.obj.nomeMae}"/></td>
				</tr>
				<tr>
					<th class="required">E-Mail:</th>
					<td><h:inputText id="inputEmail" value="#{correcaoEmailCandidatoMBean.obj.email}"/></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton id="btnConfirmar" value="Atualizar" action="#{correcaoEmailCandidatoMBean.atualizarEmail}"/>
						<h:commandButton id="btnCancelar" value="Cancelar" action="#{correcaoEmailCandidatoMBean.cancelar}" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br/>
	<div class="obrigatorio"> Campo de preenchimento obrigatório. </div>
	<c:if test="${correcaoEmailCandidatoMBean.exibirPainel }">
		<div id="div-form">
			<div class="ydlg-hd">Identificação do Candidato</div>
			<div class="ydlg-bd">
			<h:form id="cpfForm">
				<a4j:keepAlive beanName="correcaoEmailCandidatoMBean"></a4j:keepAlive>
				<table class="formulario" width="100%" style="border: 0;">
					<caption>Por favor, informe o CPF do Candidato</caption>
					<tr>
						<td colspan="2" style="color: red; font-style: italic; text-align: center;">
						${correcaoEmailCandidatoMBean.erroCPF}</td>
					</tr>
					<tr>
						<th width="50%">CPF:</th>
						<td><h:inputText value="#{correcaoEmailCandidatoMBean.obj.cpf_cnpj}" size="14" maxlength="14"
							converter="convertCpf" id="cpf" onblur="formataCPF(this, event, null)"
							onkeypress="return formataCPF(this, event, null)" >
									<f:converter converterId="convertCpf" />
							</h:inputText>
							</td>
					</tr>
					<tfoot>
						<tr>
							<td colspan="2" align="center">
								<h:commandButton value="Enviar"
									actionListener="#{correcaoEmailCandidatoMBean.submeterCPF}" id="submeterCPF" />
								<h:commandButton value="Cancelar" onclick="#{confirm}"
									action="#{correcaoEmailCandidatoMBean.cancelar}" id="cancelarCPF" immediate="true" />
							</td>
						</tr>
					</tfoot>
				</table>
			</h:form>
			</div>
		</div>
	</c:if>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script>

var PainelCPF = (function() {
	var painel;
	return {
        show : function(){
   	 		painel = new YAHOO.ext.BasicDialog("div-form", {
                modal: true,
                width: 400,
                height: 170,
                shadow: false,
                fixedcenter: true,
                resizable: false,
                closable: false
            });
       	 	painel.show();
        }
	};
})();

<c:if test="${correcaoEmailCandidatoMBean.exibirPainel }">
		PainelCPF.show();
		$('cpfForm:cpf').value = '';
		$('cpfForm:cpf').focus();
</c:if>

</script>
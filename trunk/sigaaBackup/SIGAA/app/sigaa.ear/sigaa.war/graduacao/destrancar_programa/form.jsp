<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title">
		<html:link action="/verPortalDiscente">Portal do Discente</html:link> &gt;
		Destrancar Curso
	</h2>

	<div id="ajuda" class="descricaoOperacao">
			${sf:mensagem('MensagensGraduacao.DESCRICAO_OPERACAO_DESTRANCAR_CURSO')}			
	</div>


	<h:form id="formDestrancarCurso">

		<table class="formulario" width="70%">
			<caption class="formulario">
			Confirmação dos seus dados 
			</caption>
			
			<tr>
				<th class="required">CPF:</th>
				<td>
					<h:inputText value="#{destrancarPrograma.cpf}" size="14" maxlength="14"
							converter="convertCpf" id="cpf" onblur="formataCPF(this, event, null)" />
				</td>
			</tr>

			<tr>
				<th class="required">Identidade:</th>
				<td>
					<h:inputText value="#{destrancarPrograma.identidade}" size="15" maxlength="15" id="identidade"  />
				</td>
			</tr>
			
			<tr>
			<td colspan="2">
				<c:set var="exibirApenasSenha" value="true" scope="request"/>
				<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
			</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar" action="#{destrancarPrograma.destrancar}" id="confirmarOperacao"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{destrancarPrograma.cancelar}" id="cancelarOperacao"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<script type="text/javascript">
	function toggleCPF() {
	//formDestrancarCurso
		if ( getEl('cpfForm:estrangeiro').dom.checked )
			getEl('cpfForm:cpf').dom.disabled = true;
		else
			getEl('cpfForm:cpf').dom.disabled = false;
	}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

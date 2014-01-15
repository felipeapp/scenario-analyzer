<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<h2>Question�rio S�cio Econ�mico</h2>

<f:view>
	<h:form id="form">
	<div class="descricaoOperacao">
		<h4>Prezado Candidato,</h4> <br />
		<p>
			O question�rio a seguir coleta informa��es sobre seu perfil socioeconom�mico, 
			sendo de muita import�ncia para se conhecer as caracter�sticas da popula��o que pretende ingressar nesta institui��o.
		</p>
		<p>
			Suas informa��es n�o influir�o no resultado de seu desempenho como candidato. 
			Entretanto, � fundamental que as respostas sejam verdadeiras, pois contribuir�o com as pol�ticas da ${ configSistema['siglaInstituicao'] }.
		</p>
	</div>
	
	<table class="formulario" width="100%">
		<caption>Responda as perguntas abaixo</caption>
	</table>
	<%@include file="/geral/questionario/_formulario_respostas.jsp" %>
	<table class="formulario" width="100%">
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cancelar" action="#{inscricaoVestibular.cancelar}" id="cancelar" onclick="#{confirm}" />
					<h:commandButton value="Pr�ximo passo >>" id="submeterQSE" action="#{inscricaoVestibular.submeterSocioEconomico}" /> 
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp"%>
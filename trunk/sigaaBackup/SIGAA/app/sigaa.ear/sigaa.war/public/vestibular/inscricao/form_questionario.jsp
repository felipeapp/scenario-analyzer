<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp"%>

<h2>Questionário Sócio Econômico</h2>

<f:view>
	<h:form id="form">
	<div class="descricaoOperacao">
		<h4>Prezado Candidato,</h4> <br />
		<p>
			O questionário a seguir coleta informações sobre seu perfil socioeconomômico, 
			sendo de muita importância para se conhecer as características da população que pretende ingressar nesta instituição.
		</p>
		<p>
			Suas informações não influirão no resultado de seu desempenho como candidato. 
			Entretanto, é fundamental que as respostas sejam verdadeiras, pois contribuirão com as políticas da ${ configSistema['siglaInstituicao'] }.
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
					<h:commandButton value="Próximo passo >>" id="submeterQSE" action="#{inscricaoVestibular.submeterSocioEconomico}" /> 
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Enviar Mensagem aos Participantes do Curso</h2>

<div id="ajuda" class="descricaoOperacao">
	<p>Caro Orientador, </p>
	<p>
	Esta operação tem o intuito de facilitar a comunicação junto ao Participantes do Curso (Discentes e Docentes). <br/>
	Através desta página você poderá enviar uma mensagem para todos os Discentes e/ou Docentes do Curso. Os usuários notificados receberão uma mensagem na Caixa Postal do SIGAA e outra mensagem via Email.
	</p>
</div>

<h:form id="form">
	<table class="formulario" width="100%">
		<caption>Dados da Mensagem</caption>
		<tbody>
			<tr>
				<th width="20%" class="obrigatorio">Informe quem será notificado:</th>
				<td>
					<h:selectOneMenu value="#{notificarParticipantesCurso.tipoNotificacao}" id="tipoNotificacao">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{notificarParticipantesCurso.tiposNotificacaoCombo}"/>					
					</h:selectOneMenu>
					<ufrn:help>Você poderá informar quem será notificado, os Discentes, Docentes ou Todos, informando a opção "Todos", a mensagem será enviada para os Discentes e Docentes Participantes do Curso o qual Coordena.</ufrn:help>
				</td>
			</tr>
			<tr>
				<td class="subFormulario" colspan="2">Conteúdo da Mensagem<span class="obrigatorio">&nbsp;</span></td>
			</tr>
			<tr>			
				<td colspan="2">
					<center>
						<h:inputTextarea id="mensagem" style="width: 90%" rows="9" value="#{notificarParticipantesCurso.obj.mensagem}" />
					</center>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cancelar" action="#{ notificarParticipantesCurso.cancelar }" onclick="#{confirm}"/>
					<h:commandButton title="Enviar Mensagem"  value="Enviar Mensagem" action="#{notificarParticipantesCurso.enviar}" />
				</td>
			</tr>
		</tfoot>	
	</table>
	<br/>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	
</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
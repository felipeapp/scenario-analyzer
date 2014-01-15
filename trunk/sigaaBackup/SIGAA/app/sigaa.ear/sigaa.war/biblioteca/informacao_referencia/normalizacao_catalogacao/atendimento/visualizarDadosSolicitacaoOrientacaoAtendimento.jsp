<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>

<style type="text/css">
	.cancelaBgVisualizacao table tbody {
		background-color: transparent;
	}
</style>

<f:view>

<h2>
	<ufrn:subSistema/> &gt; Solicitação de Orientação de Normalização &gt;  
	<c:choose>
		<c:when test="${solicitacaoOrientacaoMBean.atender || solicitacaoOrientacaoMBean.cancelar}">
			<h:outputText value="#{solicitacaoOrientacaoMBean.confirmButton}"/>
		</c:when>
		<c:otherwise>
			Visualizar
		</c:otherwise>
	</c:choose>
</h2>

<c:if test="${solicitacaoOrientacaoMBean.atender}">
	<div class="descricaoOperacao">
		<p>
			Defina a data e o horário que será reservado ao atendimento da solicitação deste usuário. Se necessário, 
			digite no campo 'comentários' algumas informações que gostaria de repassar ao usuário. Este agendamento 
			será sujeito à aprovação do usuário.
		</p>
	</div>
</c:if>
<c:if test="${solicitacaoOrientacaoMBean.cancelar}">
	<div class="descricaoOperacao">
		<p>
			Informe o motivo do cancelamento da solicitação de orientação. Um e-mail com este motivo será enviado ao usuário. 
		</p>
	</div>
</c:if>

<a4j:keepAlive beanName="solicitacaoOrientacaoMBean"></a4j:keepAlive>

<h:form>

	<table class="visualizacao" width="90%">
			
			<caption>Solicitação de orientação de normalização ${solicitacaoOrientacaoMBean.obj.numeroSolicitacao} </caption>
			
			<c:if test="${not empty solicitacaoOrientacaoMBean.obj.discente}">
				<tr>
					<th width="40%">Solicitante:</th>
					<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.discente}"/></td>
				</tr>
				
				<th>Categoria:</th>
				
				<c:if test="${  ( solicitacaoOrientacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						|| solicitacaoOrientacaoMBean.obj.discente.lato 
						|| solicitacaoOrientacaoMBean.obj.discente.mestrado 
						|| solicitacaoOrientacaoMBean.obj.discente.doutorado}">
					
					<td>Aluno de Pós-Graduação</td>
				</c:if>
				<c:if test="${ ! ( solicitacaoOrientacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						&& !solicitacaoOrientacaoMBean.obj.discente.lato 
						&& !solicitacaoOrientacaoMBean.obj.discente.mestrado 
						&& !solicitacaoOrientacaoMBean.obj.discente.doutorado}">
					<td>Aluno de Graduação</td>
				</c:if>
				
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.discente.curso}"/></td>
				</tr>
				
			</c:if>
			
			<c:if test="${not empty solicitacaoOrientacaoMBean.obj.servidor}">
				<tr>
					<th>Solicitante:</th>
					<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.servidor}"/></td>
				</tr>
				<tr>
					<th>Categoria:</th>
					<td>
						<c:if test="${solicitacaoOrientacaoMBean.obj.servidor.categoria.docente}" >
							Docente
						</c:if>
						<c:if test="${solicitacaoOrientacaoMBean.obj.servidor.categoria.tecnico}" >
							Técnico Administrativo
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Lotação:</th>
					<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.servidor.unidade}"/></td>
				</tr>
			</c:if>
			
			<tr>
				<th>Telefone:</th>
				<td> ${solicitacaoOrientacaoMBean.obj.pessoa.telefone} </td>
			</tr>
			
			<tr>
				<th>Celular:</th>
				<td> ${solicitacaoOrientacaoMBean.obj.pessoa.celular} </td>
			</tr>
			
			<tr>
				<th>Email:</th>
				<td> ${solicitacaoOrientacaoMBean.obj.pessoa.email} </td>
			</tr>
			
			<tr>
				<th>Data da Solicitação:</th>
				<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.dataCadastro}"/></td>
			</tr>
			
			<tr>
				<th>Situação da Solicitação:</th>
				<td>
					<c:if test="${solicitacaoOrientacaoMBean.obj.solicitado}">
						<h:outputText value="#{solicitacaoOrientacaoMBean.obj.situacao.descricao}"/>
					</c:if>
					<c:if test="${solicitacaoOrientacaoMBean.obj.atendido}">
						<h:outputText style="color:grey;" value="#{solicitacaoOrientacaoMBean.obj.situacao.descricao}"/>
					</c:if>
					<c:if test="${solicitacaoOrientacaoMBean.obj.confirmado}">
						<h:outputText style="color:green;"  value="#{solicitacaoOrientacaoMBean.obj.situacao.descricao}"/>
					</c:if>
					<c:if test="${solicitacaoOrientacaoMBean.obj.cancelado}">
						<h:outputText style="color:red;"  value="#{solicitacaoOrientacaoMBean.obj.situacao.descricao}"/>
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.biblioteca.descricao}"/></td>
			</tr>
		
			<tr>
				<th>Comentários do solicitante:</th>
				<td>
					<h:outputText value="#{solicitacaoOrientacaoMBean.obj.comentariosUsuario}" />
				</td>
			</tr>
			
			<tr>
				<th>Turno disponível (solicitante):</th>
				<td>
					<h:outputText value="#{solicitacaoOrientacaoMBean.obj.turnoDisponivel.descricao}" />
				</td>
			</tr>
		
			<c:if test="${solicitacaoOrientacaoMBean.obj.registroAtendimento != null && (solicitacaoOrientacaoMBean.obj.atendido || solicitacaoOrientacaoMBean.obj.confirmado || solicitacaoOrientacaoMBean.obj.cancelado)}">
				<tr>
					<th>Comentários do bibliotecário:</th>
					<td>
						<h:outputText value="#{solicitacaoOrientacaoMBean.obj.comentariosBibliotecario}" />
					</td>
				</tr>
				
				<tr>
					<th>Data/Horário definido:</th>
					<td>
						<h:outputText value="#{solicitacaoOrientacaoMBean.obj.descricaoHorarioAtendimento}" />
					</td>
				</tr>
				
				<tr style="margin-top: 20px;">
					<th style="padding-top: 20px;">Atendida por:</th>
					<td style="padding-top: 20px;">
						${solicitacaoOrientacaoMBean.obj.atendente}
					</td>
				</tr>
				<tr>
					<th>Data do Atendimento:</th>
					<td>
						<ufrn:format type="dataHora" valor="${solicitacaoOrientacaoMBean.obj.dataAtendimento}"/>
					</td>
				</tr>
			</c:if>
			
			<%-- Se o usuário solicitou a orientação de normalização, para atender uma solicitação o bibliotecário deve informar aqui o horário --%>
			
			<c:if test="${solicitacaoOrientacaoMBean.atender}">
				<tr>
					<td colspan="2">
						<table id="tabelaFicha" class="subFormulario" style="width: 100%">
							<caption>Dados para Atendimento</caption>
							<tr class="cancelaBgVisualizacao">
								<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;">Data de atendimento:</th>
								<td>
									<t:inputCalendar value="#{solicitacaoOrientacaoMBean.dataAtendimento}" 
											size="17" maxlength="10" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" 
											renderPopupButtonAsImage="true" id="calDataAtendimento"
											onkeypress="return formataData(this, event)" />
									<ufrn:help>Data na qual deseja marcar o atendimento.</ufrn:help>
								</td>
							</tr>
							<tr>
								<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;">Horário de atendimento:</th>
								<td>
									<h:inputText value="#{solicitacaoOrientacaoMBean.horarioInicioAtendimento}" size="5" maxlength="5" id="txtHorarioInicioAtendimento" onkeypress="return formataHora(this,event);" />
									às
									<h:inputText value="#{solicitacaoOrientacaoMBean.horarioFimAtendimento}" size="5" maxlength="5" id="txtHorarioFimAtendimento" onkeypress="return formataHora(this,event);" />
									<ufrn:help>Horário no qual deseja marcar o atendimento na data definida.</ufrn:help>
								</td>
							</tr>
							<tr>
								<th style="font-weight: normal; padding-right: 13px;">Comentários:</th>
								<td>
									<h:inputTextarea id="txtComentarios" value="#{solicitacaoOrientacaoMBean.obj.comentariosBibliotecario}" cols="45" rows="5" onkeyup="textCounter(this, 'quantidadeCaracteresDigitadosComentarios', 200);" />
									<ufrn:help>Digite aqui quaisquer informações importantes que gostaria de passar ao solicitante.</ufrn:help>
								</td>
							</tr>
							<tr>
								<th style="font-weight: normal;">Caracteres Restantes:</th>
								<td>
									<span id="quantidadeCaracteresDigitadosComentarios">200</span>/200
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${solicitacaoOrientacaoMBean.cancelar}">
				<tr>
					<td colspan="2">
						<table class="subFormulario" style="width: 100%">
							<caption>Motivo do Cancelamento</caption>
							<tr>
								<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;">Motivo:</th>
								<td>
									<h:inputTextarea
											id="txtAreaMotivoCancelamento"
											value="#{solicitacaoOrientacaoMBean.motivoCancelamento}" 
											cols="80" rows="6"
											onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 100);" />
								</td>
							</tr>
							<tr>
								<th style="font-weight: normal;">Caracteres Restantes:</th>
								<td>
									<span id="quantidadeCaracteresDigitados">100</span>/100
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:form id="form">
							<c:if test="${solicitacaoOrientacaoMBean.atender || solicitacaoOrientacaoMBean.confirmar || solicitacaoOrientacaoMBean.cancelar}">
								<h:commandButton
										id="confirmButton"
										value="#{solicitacaoOrientacaoMBean.confirmButton}"
										action="#{solicitacaoOrientacaoMBean.confirmarAtendimento}" />
							</c:if>
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoOrientacaoMBean.verSolicitacoes}" immediate="true" id="cancelar" />
						</h:form>
					</td>
				</tr>
			</tfoot>
	</table>

	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</h:form>

</f:view>

<script language="javascript">
<!--
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}
-->
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
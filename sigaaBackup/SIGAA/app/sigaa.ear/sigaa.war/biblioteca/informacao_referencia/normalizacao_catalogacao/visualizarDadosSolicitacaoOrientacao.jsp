<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>
<style>
	p{
		 text-indent: 2em;
	}
</style>

<f:view>

<h2> <ufrn:subSistema/> &gt; Solicitação de Orientação de Normalização &gt;  
	<c:choose>
		<c:when test="${solicitacaoOrientacaoMBean.confirmar || solicitacaoOrientacaoMBean.cancelar}">
			<h:outputText value="#{solicitacaoOrientacaoMBean.confirmButton}"/>
		</c:when>
		<c:otherwise>
			Visualizar Dados Solicitação
		</c:otherwise>
	</c:choose>
</h2>

<c:if test="${solicitacaoOrientacaoMBean.confirmar}">
	<div class="descricaoOperacao">
		<p><strong>
			Caro usuário, ao aprovar este agendamento você está confirmando que vai comparecer a esta biblioteca na data e horário 
			definidos abaixo, para receber orientação de normalização.
		</strong></p>
	</div>
</c:if>
<c:if test="${solicitacaoOrientacaoMBean.cancelar}">
	<div class="descricaoOperacao">
		<p>
			Caro usuário, informe o motivo do cancelamento do agendamento. Um e-mail com este motivo será enviado ao 
			bibliotecário que lhe atendeu. 
		</p>
	</div>
</c:if>

<a4j:keepAlive beanName="solicitacaoOrientacaoMBean"></a4j:keepAlive>

<%-- Parte onde o usuário visualiza o comprovante da solicitação --%>
<c:if test="${! solicitacaoOrientacaoMBean.obj.atendido}">
	<h:form>
		<table  class="subFormulario" align="center">
			<caption style="text-align: center;">Informação Importante</caption>
			<tr>
			<td width="8%" valign="middle" align="center">
				<html:img page="/img/warning.gif"/>
			</td>
			<td valign="middle" style="text-align: justify">
				Por favor imprima o comprovante clicando no ícone ao lado para maior segurança dessa operação.
			</td>
			<td>
				<table>
					<tr>
						<td align="center">
							<h:commandLink title="Imprimir Comprovante"  target="_blank" action="#{solicitacaoOrientacaoMBean.telaComprovante}" >
					 			<h:graphicImage url="/img/printer_ok.png" />
					 		</h:commandLink>
					 	</td>
					 </tr>
					 <tr>
					 	<td style="font-size: medium;">
					 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{solicitacaoOrientacaoMBean.telaComprovante}"  />
					 	</td>
					 </tr>
				</table>
			</td>
			</tr>
		</table>
	<br/>
	</h:form>
</c:if>

<h:form id="form">
	<table class="visualizacao" width="90%">
			
			<caption>Solicitação de orientação de normalização número ${solicitacaoOrientacaoMBean.obj.numeroSolicitacao} </caption>
			
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
				<th style="padding-top: 20px;">Data da Solicitação:</th>
				<td style="padding-top: 20px;"><h:outputText value="#{solicitacaoOrientacaoMBean.obj.dataCadastro}"/></td>
			</tr>
			
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{solicitacaoOrientacaoMBean.obj.biblioteca.descricao}"/></td>
			</tr>
			
			<tr>
				<th>Turno disponível (solicitante):</th>
				<td>
					<h:outputText value="#{solicitacaoOrientacaoMBean.obj.turnoDisponivel.descricao}" />
				</td>
			</tr>
			
			<tr>
				<th>Comentários do solicitante:</th>
				<td>
					<h:outputText value="#{solicitacaoOrientacaoMBean.obj.comentariosUsuario}" />
				</td>
			</tr>
			
			
			
			
			<tr>
				<th style="padding-top: 20px;">Situação da Solicitação:</th>
				<td style="padding-top: 20px;">
					
					<c:choose>
					
						<c:when test="${solicitacaoOrientacaoMBean.obj.solicitado}">
							<h:outputText value="#{solicitacaoOrientacaoMBean.obj.situacao.descricao}"/>
						</c:when>
						<c:when test="${solicitacaoOrientacaoMBean.obj.atendido}">
							<h:outputText style="color:grey;" value="#{solicitacaoOrientacaoMBean.obj.situacao.descricao}"/>
						</c:when>
						<c:when test="${solicitacaoOrientacaoMBean.obj.confirmado}">
							<h:outputText style="color:green;"  value="#{solicitacaoOrientacaoMBean.obj.situacao.descricao}"/>
						</c:when>
						<c:when test="${solicitacaoOrientacaoMBean.obj.cancelado}">
							<h:outputText style="color:red;"  value="#{solicitacaoOrientacaoMBean.obj.situacao.descricao}"/>
						</c:when>
						
						 <c:otherwise>
						 	<h:outputText value="#{solicitacaoOrientacaoMBean.obj.situacao.descricao}"/>
						 </c:otherwise> 
					</c:choose>
					
				</td>
			</tr>
			
			
			<c:if test="${solicitacaoOrientacaoMBean.obj.registroAtendimento != null && (solicitacaoOrientacaoMBean.obj.atendido || solicitacaoOrientacaoMBean.obj.confirmado || solicitacaoOrientacaoMBean.obj.cancelado)}">
				
				<tr>
					<td colspan="2">
						<div style="width: 60%; margin-left: auto; margin-right: auto; font-weight: bold; background-color: #C8D5EC; text-align: center;">
							<br/> 
							Data e Horário definidos <br/> 
							<h:outputText value="#{solicitacaoOrientacaoMBean.obj.descricaoHorarioAtendimento}" /> 
							<br/><br/>
						</div>
					</td>
				</tr>
				
				<tr>
					<th>Comentários do bibliotecário:</th>
					<td style=" font-style: italic;">
						<h:outputText value="#{solicitacaoOrientacaoMBean.obj.comentariosBibliotecario}" />
					</td>
				</tr>
				
				
				<tr>
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
											cols="50" rows="2"
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
						<c:if test="${solicitacaoOrientacaoMBean.atender || solicitacaoOrientacaoMBean.confirmar || solicitacaoOrientacaoMBean.cancelar}">
							<h:commandButton
									id="confirmButton"
									value="#{solicitacaoOrientacaoMBean.confirmButton}"
									action="#{solicitacaoOrientacaoMBean.finalizar}" />
						</c:if>
						<h:commandButton value="<< Voltar" action="#{solicitacaoOrientacaoMBean.verMinhasSolicitacoes}" id="voltar" immediate="true" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoOrientacaoMBean.cancelar}" id="cancelar" immediate="true" />
					</td>
				</tr>
			</tfoot>
	</table>
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
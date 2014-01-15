<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Solicitação de Bolsa Auxílio </h2>

<f:view>
	<h:form id="form">
		<table class="formulario" width="80%" id="tabela">
		
			<caption>Solicitação de Bolsa Auxílio</caption>
			<tbody>
			
				<%@include file="/sae/BolsaAuxilio/include/_dados_solicitacao.jsp" %>

				<tr>
					<th class="obrigatorio" width="30%"> 
						<b>ATIVIDADES ACADÊMICIAS EM TURNOS CONSECUTIVOS:</b>
					</th>
					<td>
						<h:selectOneRadio value="#{bolsaAuxilioMBean.obj.turnoAtividade}" id="turno">
							<f:selectItem itemLabel="Apenas um Turno" itemValue="TU" />
							<f:selectItem itemLabel="Manhã/Tarde" itemValue="MT" />
							<f:selectItem itemLabel="Tarde/Noite" itemValue="TN" />
							<f:selectItem itemLabel="Manhã/Tarde/Noite" itemValue="MTN" />
						</h:selectOneRadio>	
					</td>
				</tr>
				
				<tr>
					<th class="obrigatorio">
						 <b> MEIO DE TRANSPORTE UTILIZADO PARA DESLOCAMENTO CASA->${ configSistema['siglaInstituicao'] }->CASA: </b>
					</th> 
					<td>
						<h:selectManyCheckbox id="tranportes" value="#{bolsaAuxilioMBean.transportesIds}">	
							<f:selectItems value="#{bolsaAuxilioMBean.meiosTransporte}"/>
						</h:selectManyCheckbox>
					</td>
				</tr>
				
				<!-- ============= RESIDENCIAS -->
				<c:if test="${ bolsaAuxilioMBean.obj.tipoBolsaAuxilio.residenciaGraduacao || bolsaAuxilioMBean.obj.tipoBolsaAuxilio.residenciaPos }">
					<tr>
						<th>
						 	<b>  RESIDÊNCIA: </b>
					 	</th>
						<td>
							<h:selectOneMenu id="residencia" value="#{bolsaAuxilioMBean.obj.residencia.id}" style="width:400px;">
								<f:selectItems value="#{bolsaAuxilioMBean.allResidencias}" />
							</h:selectOneMenu>
							<br />
							(a disponibilidade da residência será analisada pelo SAE)
						</td>
					</tr>
				</c:if>
				
				<tr>
					<th>
						<b> CUSTO MENSAL COM TRANSPORTE R$: </b>
					</th>
					<td>
						<h:inputText id="custoMensal" value="#{bolsaAuxilioMBean.obj.custoMensalTransporte}" maxlength="8" size="8" onkeydown="return(formataValor(this, event, 2))">
							<f:converter converterId="convertMoeda"/>
						</h:inputText>
					</td>
				</tr>		
					
				<!-- ============= JUSTIFICATIVA DE REQUERIMENTO PELO ALUNO  -->
				<tr>
					<td colspan="2">
						<span class="obrigatorio"> <b>JUSTIFICATIVA DE REQUERIMENTOS:</b></span>
					</td>
				<tr>
				<tr>
					<td colspan="2">
						<h:inputTextarea id="justificativa" value="#{bolsaAuxilioMBean.obj.justificativaRequerimento}" cols="118" rows="15"></h:inputTextarea>
					</td>
				</tr>
				
				<!-- ============= PARECER SERVICO SOCIAL - SAE -->
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">
					<tr>
						<td colspan="2">
							<b> PARECER DO SERVIÇO SOCIAL: </b>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<h:inputTextarea  id="parecerServico" value="#{bolsaAuxilioMBean.obj.parecerServicoSocial}" cols="118" rows="15"></h:inputTextarea>
						</td>
					</tr>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">
					<tr>
						<td colspan="4">
						<br/>
							<b> DOCUMENTOS ENTREGUES:  </b> 
							<h:selectManyCheckbox id="documentos"
								value="#{bolsaAuxilioMBean.documentosIds}">	
								<f:selectItems value="#{bolsaAuxilioMBean.documentosEntregues}"/>
							</h:selectManyCheckbox>
						</td>
					</tr>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">	
					<tr>
						<td colspan="4">
						<br/>
							<b> STATUS DO ALUNO PARA ESSA SOLICITAÇÃO: </b>
							<h:selectOneRadio id="statusBolsaSAE" value="#{ bolsaAuxilioMBean.obj.situacaoBolsa.id }">
								<f:selectItems value="#{situacaoBolsaAuxilioMBean.allCombo}" /> 
							</h:selectOneRadio>
						</td>
					</tr>
				</ufrn:checkRole>
								
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						
						<h:commandButton value="#{ bolsaAuxilioMBean.confirmButton }" action="#{bolsaAuxilioMBean.cadastrar}" id="cadastrar" />
						<h:commandButton value="<< Voltar" action="#{bolsaAuxilioMBean.voltar}" id="voltarSolicitação" />
						<h:commandButton value="Cancelar" action="#{bolsaAuxilioMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</center>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
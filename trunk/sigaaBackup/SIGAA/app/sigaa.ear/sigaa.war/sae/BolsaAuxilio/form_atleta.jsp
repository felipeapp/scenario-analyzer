<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Solicitação de Bolsa Auxílio </h2>

<f:view>
	<h:form id="form">
		<table class="formulario" width="80%" id="tabela">
		
			<caption>Solicitação de Bolsa Auxílio</caption>
			<tbody>
			
				<%@include file="/sae/BolsaAuxilio/include/_dados_solicitacao.jsp" %>

				<tr>
					<th class="obrigatorio">
						<b>1. Assinale a modalidade desportiva que pratica: </b>
					</th>
					<td>
						<h:selectOneRadio value="#{ bolsaAuxilioMBean.bolsaAuxiliar.modalidadeEsportiva.id }" id="modalidadeEsportiva">
							<f:selectItems value="#{ modalidadeEsportivaMBean.allCombo }"/>
						</h:selectOneRadio>	
					</td>
				</tr>
				
				<tr>
					<th class="obrigatorio">
						<b>2. Escreva o nome da Federação em que está associado: </b>
					</th>
					<td>
						<h:inputText value="#{ bolsaAuxilioMBean.bolsaAuxiliar.federacao }" id="federacao" size="60" maxlength="80"/>
					</td>
				</tr>
				
				<!-- ============= JUSTIFICATIVA DE REQUERIMENTO PELO ALUNO  -->
				<tr>
					<td colspan="4">
						<br/>
						<span class="obrigatorio"> <b>JUSTIFICATIVA DE REQUERIMENTOS:</b></span>
						<h:inputTextarea id="justificativa" value="#{bolsaAuxilioMBean.obj.justificativaRequerimento}" cols="118" rows="15"></h:inputTextarea>
					</td>
				</tr>
				
				<!-- ============= PARECER SERVICO SOCIAL - SAE -->
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">
				<tr>
					<td colspan="4">
					<br/>
						<b> PARECER DO SERVIÇO SOCIAL: </b>
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
					<td colspan="4" align="center">
						<h:commandButton value="#{ bolsaAuxilioMBean.confirmButton }" action="#{bolsaAuxilioMBean.cadastrar}" id="cadastrar" />
						<h:commandButton value="<< Voltar" action="#{bolsaAuxilioMBean.voltar}" id="voltarEdicao" />
						<h:commandButton value="Cancelar" action="#{bolsaAuxilioMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br> </center>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
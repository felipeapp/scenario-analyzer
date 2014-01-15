<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Solicitação de Bolsa Auxílio </h2>

<f:view>
	<h:form id="form">
		<table class="formulario" width="80%" id="tabela">
		
			<caption>Solicitação de Bolsa Auxílio</caption>
			<tbody>
			
					<%@include file="/sae/BolsaAuxilio/include/_dados_solicitacao.jsp" %>
			
					<tr>
						<th style="padding-left: 10px">
							<b>País de Origem:</b> 
						</th>
						<td width="62%">
							<h:outputText value="#{ bolsaAuxilioMBean.obj.discente.pessoa.pais.nome }" id="pais" />
						</td>
					</tr>
					<tr>
						<th style="padding-left: 10px">
							<b>Nacionalidade:</b> 	
						</th>
						<td>
							<h:outputText value="#{ bolsaAuxilioMBean.obj.discente.pessoa.pais.nacionalidade }" id="nacionalidade" />
						</td>
					</tr>
	
	                <tr>
						<th style="padding-left: 10px">
							<b>Data Nascimento:</b> 	
						</th>
						<td>
							<h:outputText value="#{ bolsaAuxilioMBean.obj.discente.pessoa.dataNascimento }" id="nascimento" />
						</td>
					</tr>
					
	                <tr>
						<th style="padding-left: 10px">
							<b>Nome Pai:</b> 
						</th>
						<td>
							<h:outputText value="#{ bolsaAuxilioMBean.obj.discente.pessoa.nomePai }" id="nomePai" />
						</td>	
					</tr>
					
	                <tr>
						<th style="padding-left: 10px">
							<b>Nome Mãe:</b> 	
						</th>
						<td>
							<h:outputText value="#{ bolsaAuxilioMBean.obj.discente.pessoa.nomeMae }" id="nomeMae" />
						</td>
					</tr>
					
	                <tr>
						<th style="padding-left: 10px">
							<b>Semestre e ano de início do Curso:</b> 
						</th>
						<td>
							<h:outputText value="#{ bolsaAuxilioMBean.obj.discente.anoIngresso }" id="anoIngresso" />.<h:outputText value="#{ bolsaAuxilioMBean.obj.discente.periodoIngresso }" id="periodoIngresso" />	
						</td>
					</tr>
	
	                <tr>
						<th style="padding-left: 10px">
							<b>Semestre e ano de provável conclusão do Curso:</b> 
						</th>
						<td>
							<ufrn:format type="anosemestre" valor="${bolsaAuxilioMBean.obj.discente.prazoConclusao}" />
						</td>
					</tr>
	
	                <tr>
						<th style="padding-left: 10px">
							RNE (Registro Nacional de Estrangeiro): <html:img page="/img/required.gif" style="vertical-align: middle;" />
						</th>
						<td>
							<h:inputText value="#{ bolsaAuxilioMBean.obj.registroNacionalEstrangeiro }" maxlength="50" id="rne" />
						</td>
					</tr>
					
	                <tr>
						<th style="padding-left: 10px">
							Situação Regular: <html:img page="/img/required.gif" style="vertical-align: middle;" />
						</th>
						<td>
							<h:selectOneRadio value="#{bolsaAuxilioMBean.obj.situacaRegular}" id="situacao">
								<f:selectItems value="#{bolsaAuxilioMBean.simNao}" />
							</h:selectOneRadio>
						</td>
					</tr>
					
	                <tr>
						<th style="padding-left: 10px">
							Recebe auxílio/Bolsa do País de Origem?: <html:img page="/img/required.gif" style="vertical-align: middle;" />
						</th>
						<td>
							<h:selectOneRadio value="#{bolsaAuxilioMBean.obj.recebeuAuxilio}" id="recebeuAuxilio">
								<f:selectItems value="#{bolsaAuxilioMBean.simNao}" />
							</h:selectOneRadio>
						</td>
					</tr>

					<!-- ============= JUSTIFICATIVA DE REQUERIMENTO PELO ALUNO  -->
					<tr>
						<td colspan="2">
					<br/>
							<span class="required"> <b>JUSTIFICATIVA DE REQUERIMENTOS:</b></span>
							<h:inputTextarea id="justificativa" value="#{bolsaAuxilioMBean.obj.justificativaRequerimento}" cols="118" rows="15" />
						</td>
					</tr>
		
					<!-- ============= PARECER SERVICO SOCIAL - SAE -->
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">
						<tr>
							<td colspan="2">
							<br/>
								<b> PARECER DO SERVIÇO SOCIAL: </b>
								<h:inputTextarea  id="parecerServico" value="#{bolsaAuxilioMBean.obj.parecerServicoSocial}" cols="118" rows="15" />
							</td>
						</tr>
					</ufrn:checkRole>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">	
						<tr>
							<td colspan="2">
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
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br> 
		</center>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
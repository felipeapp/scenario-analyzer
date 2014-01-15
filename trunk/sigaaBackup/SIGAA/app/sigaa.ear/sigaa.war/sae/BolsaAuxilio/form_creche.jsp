<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Solicita��o de Bolsa Aux�lio </h2>

<f:view>
	<h:form id="form">
		<table class="formulario" width="80%" id="tabela">
		
			<caption>Solicita��o de Bolsa Aux�lio</caption>
			<tbody>
			
				<%@include file="/sae/BolsaAuxilio/include/_dados_solicitacao.jsp" %>
				
				<tr>
					<td style="padding-left: 11px; padding-top: 10px;" colspan="4">
					<br/>
						<span class="obrigatorio"><b>1. ATIVIDADES ACAD�MICIAS EM TURNOS CONSECUTIVOS: </b></span>
						<h:selectOneRadio value="#{bolsaAuxilioMBean.obj.turnoAtividade}" id="turno">
							<f:selectItem itemLabel="Apenas um Turno" itemValue="TU" />
							<f:selectItem itemLabel="Manh�/Tarde" itemValue="MT" />
							<f:selectItem itemLabel="Tarde/Noite" itemValue="TN" />
							<f:selectItem itemLabel="Manh�/Tarde/Noite" itemValue="MTN" />
						</h:selectOneRadio>	
					</td>
				</tr>
				
				<tr>
					<td style="padding-left: 11px; padding-top: 10px;" colspan="4">
					<span class="obrigatorio"><b>2. SITUA��O FAMILIAR: </b></span>
						 <h:selectOneRadio value="#{ bolsaAuxilioMBean.bolsaAuxiliar.solteiro }" id="situacaoFamiliar">
						 	<f:selectItem itemLabel="Casado ou em Uni�o Est�vel e com Filho" itemValue="false" />
						 	<f:selectItem itemLabel="Separado ou Solteiro com Filho" itemValue="true" />
						 	<a4j:support event="onchange" reRender="form" />
						 </h:selectOneRadio> 
					</td>
				</tr>
				
				<tr>
					<td style="padding-left: 20px;" width="25%;"> Trabalho do(a) candidato(a): </td>
					<td> <h:inputText value="#{ bolsaAuxilioMBean.bolsaAuxiliar.trabalhoCandidato }" size="12" 
						 id="trabalhoCandidato"/> </td>
					<td> Sal�rio do(a) candidato(a): </td>
					<td> 
						<h:inputText value="#{ bolsaAuxilioMBean.bolsaAuxiliar.salarioCandidato }" size="14" 
								style="text-align: right" onfocus="javascript:select()" onkeydown="return(formataValor(this, event, 2))"
								maxlength="8" id="salarioCandidato">
							<f:converter converterId="convertMoeda"/>
						</h:inputText>							
					</td>
				</tr>
				
				<tr>
					<td style="padding-left: 20px;"> N�mero de Filhos: </td>
					<td> 
						<h:inputText value="#{ bolsaAuxilioMBean.bolsaAuxiliar.numeroFilhosCandidato }" size="12" style="text-align: right"
								maxlength="1" onkeyup="return formatarInteiro(this);" id="numeroFilhos"/> 
					</td>
					<td> Idade dos Filhos: </td>
					<td> <h:inputText value="#{ bolsaAuxilioMBean.bolsaAuxiliar.idadeFilhosCandidato }" size="14" maxlength="50" id="idadeFilhos"/>
						<ufrn:help>Preencha a idade dos seu(s) filho(s) separando por "," (v�rgula). Ex.: 2,4,10</ufrn:help>
					</td>
				</tr>

				<!-- ============= SOLICITA��O AUXILIO CRECHE CANDIDATOS CASADOS OU UNI�O EST�VEL -->
				<a4j:region id="casado" rendered="#{ not bolsaAuxilioMBean.bolsaAuxiliar.solteiro }">
					<tr>
						<td style="padding-left: 20px;"> Trabalho do C�njuge: </td>
						<td> <h:inputText value="#{ bolsaAuxilioMBean.bolsaAuxiliar.trabalhoConjugeCandidato }" size="12" 
							maxlength="50" id="trabalhoConjuge"/> </td>
						<td> Sal�rio do C�njuge: </td>
						<td> 
							<h:inputText value="#{ bolsaAuxilioMBean.bolsaAuxiliar.salarioConjugeCandidato }" size="14"
									style="text-align: right" onfocus="javascript:select()" onkeydown="return(formataValor(this, event, 2))"
									maxlength="8" id="salario_conjuge">
								<f:converter converterId="convertMoeda"/>
							</h:inputText>							
						</td>						
					</tr>
				</a4j:region>
				
				<!-- ============= SOLICITA��O AUXILIO CRECHE CANDIDATOS SOLTEIROS OU SEPARADOS -->
				<a4j:region id="solteiro" rendered="#{ bolsaAuxilioMBean.bolsaAuxiliar.solteiro }">
					<tr>
						<td style="padding-left: 20px;"> Recebe Pens�o: </td>
						<td> 
							 <h:selectOneRadio value="#{ bolsaAuxilioMBean.bolsaAuxiliar.recebePensao }" id="recebePensao">
							 	<f:selectItems value="#{ bolsaAuxilioMBean.simNao }"/>
							 </h:selectOneRadio> 
						</td>
						<td> Valor da Pens�o: </td>
						<td> 
							<h:inputText value="#{ bolsaAuxilioMBean.bolsaAuxiliar.valorPensao }" size="14"
									style="text-align: right" onfocus="javascript:select()" onkeydown="return(formataValor(this, event, 2))"
									maxlength="8" id="valorPensao">
								<f:converter converterId="convertMoeda"/>
							</h:inputText>
						</td>							
					</tr>
				</a4j:region>

				<tr>
					<td style="padding-left: 11px; padding-top: 10px;" colspan="4">
					<span class="obrigatorio"><b>3. DURANTE SUAS ATIVIDADES ACAD�MICAS SEU(S) FILHO(S) FICAM: </b></span>
						 <h:selectOneRadio value="#{ bolsaAuxilioMBean.bolsaAuxiliar.periodoAtividadeAcademica.id }" id="atividadesAcademicas">
							<f:selectItems value="#{ periodoAtividadeAcademicaMBean.allCombo }" />
						 </h:selectOneRadio> 
					</td>
				</tr>
				
				<!-- ============= JUSTIFICATIVA DE REQUERIMENTO PELO ALUNO  -->
				<tr>
					<td style="padding-left: 10px" colspan="4">
						<br/>
						<span class="obrigatorio"> <b>JUSTIFICATIVA DE REQUERIMENTOS:</b></span>
						<h:inputTextarea id="justificativa" value="#{bolsaAuxilioMBean.obj.justificativaRequerimento}" cols="118" 
							rows="15"></h:inputTextarea>
					</td>
				</tr>
				
				<!-- ============= PARECER SERVICO SOCIAL - SAE -->
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">
				<tr>
					<td style="padding-left: 10px" colspan="4">
					<br/>
						<b> PARECER DO SERVI�O SOCIAL: </b>
						<h:inputTextarea  id="parecerServico" value="#{bolsaAuxilioMBean.obj.parecerServicoSocial}" cols="118" 
							rows="15"></h:inputTextarea>
					</td>
				</tr>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">
				<tr>
					<td colspan="4">
					<br/>
						<b> DOCUMENTOS ENTREGUES:  </b> 
						<h:selectManyCheckbox id="documentos" value="#{bolsaAuxilioMBean.documentosIds}">	
							<f:selectItems value="#{bolsaAuxilioMBean.documentosEntregues}"/>
						</h:selectManyCheckbox>
					</td>
				</tr>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">	
				<tr>
					<td style="padding-left: 10px" colspan="4">
					<br/>
						<b> STATUS DO ALUNO PARA ESSA SOLICITA��O: </b>
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
		class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br> </center>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
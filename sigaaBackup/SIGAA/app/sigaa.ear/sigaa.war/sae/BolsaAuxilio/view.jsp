<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link href="/sigaa/css/ensino/questionarios.css" rel="stylesheet" type="text/css" />

<f:view>
<h2><ufrn:subSistema /> > Formulário de Bolsa Auxílio </h2>

	<div class="descricaoOperacao">
			
			<center>
				<b>Solicitação realizada com sucesso! Imprima esta página como comprovante.</b>
			</center>
			<br/>
			<b> Para concluir o procedimento é necessário que você compareça
			 ao SAE com os documentos abaixo e de acordo com as datas especificadas. </b> <br/>
			 
	 		<ul>
				<li> 1 foto 3x4; </li>
				<li> Carteira de Identidade, CPF e PID (Comprovante de Inscrição em Disciplina)(XEROX); </li>
				<li> Comprovante de residência (água, luz ou telefone) (XEROX); </li>
				<li> Certidão de Nascimento dos dependentes do chefe da família (XEROX); </li>
				<li> Contra-cheque ou carteira profissional do candidato (páginas de identificação pessoal frente e verso e página do contrato de trabalho) (XEROX); </li>
				<li> Comprovante de renda familiar (XEROX); </li>
				<li> Declaração de Bolsista/Estagiário. </li>
			</ul>
		</div>
			
	<%@include file="/geral/questionario/_respostas.jsp" %>
	<br><br>

		<t:newspaperTable value="#{bolsaAuxilioMBean.obj.adesaoCadUnico.listaConfortoFamiliar}" var="confortoFamiliar" newspaperColumns="2" 
				newspaperOrientation="horizontal" rowClasses="linhaPar, linhaImpar"  width="100%">					
	
			<t:column style="text-align: right;">
	         	<h:outputText value="#{confortoFamiliar.item.item}" />
	        </t:column>
	                
	           <t:column>
					<h:selectOneMenu value="#{confortoFamiliar.quantidade}" disabled="true">
						<f:selectItem itemValue="0" itemLabel="Nenhum" />
						<f:selectItem itemValue="1" itemLabel="1" />
						<f:selectItem itemValue="2" itemLabel="2" />
						<f:selectItem itemValue="3" itemLabel="3" />
					</h:selectOneMenu>
	           </t:column>
		</t:newspaperTable>

	<br><br>	
	
	<table class="formulario" width="100%" id="tabela">
			<tr>
				<td style="padding-left: 11px;">
					<b>MATRÍCULA:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.matricula}" id="matricula" />	
				</td>
			</tr>
			
             <tr>
				<td style="padding-left: 11px;">
					<b>DISCENTE:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.nome}" id="discente" />	
				</td>
			</tr>
			
			<tr>
				<td style="padding-left: 11px;">
					<b>CURSO:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.curso.descricao}" id="curso"/>
				</td>
			</tr>
			
			<tr>
				<td style="padding-left: 11px;">
					<b>CEP:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.cep}" id="cep"/></td>
			</tr>
			
			<tr>
				<td style="padding-left: 11px;">
					<b>BAIRRO:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.bairro}" id="bairro"/></td>
			</tr>
			
			<tr>
				<td style="padding-left: 11px;">
					<b>RUA:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.logradouro}" id="rua"/></td>
			</tr>
			
			<tr>
				<td style="padding-left: 11px;">
					<b>NÚMERO:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.numero}" id="numero"/></td>
			</tr>
			
			<tr>
				<td style="padding-left: 11px;">
					<b>CIDADE:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.municipio.nome}" id="cidade"/></td>
			</tr>
			
			<tr>
				<td style="padding-left: 11px;">
					<b>UF:</b> <h:outputText value="#{bolsaAuxilioMBean.discente.pessoa.enderecoContato.unidadeFederativa.sigla}" id="uf" /></td>
			</tr>
			
			<tr>
				<td style="padding-left: 11px;">
				<br>
					<b> TIPO DA BOLSA AUXÍLIO: </b>
					<h:outputText value="#{bolsaAuxilioMBean.obj.tipoBolsaAuxilio.denominacao}" />
				</td>
			</tr>	
				
			<tr>
				<td style="padding-left: 10px">
				<br/>
					<b> ATIVIDADES ACADÊMICAS EM TURNOS CONSECUTIVOS: </b>
					<h:outputText value="#{bolsaAuxilioMBean.obj.descricaoTurnoAtividade}" />
				</td>
			</tr>	
				
				<tr> 
					<td style="padding-left: 10px">
					<br/>
					
						<c:set value="${fn:length(bolsaAuxilioMBean.obj.tipoMeioTransporte)}" var="tamanho" />
						<b> MEIO DE TRANSPORTE UTILIZADO PARA DESLOCAMENTO CASA->${ configSistema['siglaInstituicao'] }->CASA: </b>
						<c:forEach var="itemMeioTransporte" items="#{bolsaAuxilioMBean.obj.tipoMeioTransporte}" varStatus="posicao">
							<c:if test="${tamanho > 0}">
								<c:if test="${posicao.index != tamanho-1}">
									${itemMeioTransporte.meioTransporte}, 
								</c:if>
								<c:if test="${posicao.index == tamanho-1}">
									${itemMeioTransporte.meioTransporte}. 
								</c:if>
							</c:if>							
						</c:forEach>
						
					</td>
				</tr>
				
				<!-- ============= RESIDENCIAS -->
				<c:if test="${bolsaAuxilioMBean.obj.residencia != null}">
					<tr>
						<td style="padding-left: 10px;">
					<br/>
							<b> RESIDÊNCIA: </b> <h:outputText value="#{bolsaAuxilioMBean.obj.residencia.localizacao}" />
						</td>
					</tr>
				</c:if>
				
				<tr>
					<td style="padding-left: 10px">
					<br/>
						<b> CUSTO MENSAL COM TRANSPORTE R$: </b>
						<h:outputText value="#{bolsaAuxilioMBean.obj.custoMensalTransporte}"></h:outputText>
					</td>
				</tr>		
					
				<!-- ============= JUSTIFICATIVA DE REQUERIMENTO PELO ALUNO  -->
				<tr>
					<td style="padding-left: 10px">
				<br/>
						<b> JUSTIFICATIVA DE REQUERIMENTOS: </b> <br>
						<h:inputTextarea readonly="true" id="justificativa" value="#{bolsaAuxilioMBean.obj.justificativaRequerimento}" cols="155" rows="15"></h:inputTextarea>
					</td>
				</tr>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">
				<!-- ============= PARECER SERVICO SOCIAL - SAE -->
				<tr>
					<td style="padding-left: 10px">
					<br/>
						<b> PARECER DO SERVIÇO SOCIAL: </b> <br>
						<h:inputTextarea readonly="true" id="parecerServico" value="#{bolsaAuxilioMBean.obj.parecerServicoSocial}" cols="155" rows="15"></h:inputTextarea>
					</td>
				</tr>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">
				<tr>
					<td style="padding-left: 10px">
					<br/>
						<c:set value="${fn:length(bolsaAuxilioMBean.obj.documentosEntregues)}" var="tamanhoDocumentos"  />
						<b> DOCUMENTOS ENTREGUES: </b>
						<c:forEach var="itemDocumentoEntregue" items="#{bolsaAuxilioMBean.obj.documentosEntregues}" varStatus="posicao">
							<c:if test="${tamanhoDocumentos > 0}">
								<c:if test="${posicao.index != tamanhoDocumentos-1}">
									${itemDocumentoEntregue.descricao}, 
								</c:if>
								<c:if test="${posicao.index == tamanhoDocumentos-1}">
									${itemDocumentoEntregue.descricao}. 
								</c:if>
							</c:if>	
						</c:forEach>
					</td>
				</tr>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR, SigaaPapeis.SAE } %>">
				<tr>
					<td style="padding-left: 10px">
					<br/>
						<b> STATUS DO ALUNO PARA ESSA SOLICITAÇÃO: </b>
							<h:outputText value="#{bolsaAuxilioMBean.obj.situacaoBolsa.denominacao}" />
						</td>
				</tr>
				</ufrn:checkRole>
	</table>
<br>
<div style="text-align: center;">
	<a href="javascript: history.go(-1);"> << Voltar </a>
</div>	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
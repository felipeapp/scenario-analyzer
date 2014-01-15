<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>

<f:view>
<h2> <ufrn:subSistema/> &gt; Solicitação de Normalização &gt;  
	<c:choose>
		<c:when test="${solicitacaoNormalizacaoMBean.atender || solicitacaoNormalizacaoMBean.cancelar}">
			<h:outputText value="#{solicitacaoNormalizacaoMBean.confirmButton}"/>
		</c:when>
		<c:otherwise>
			Visualizar
		</c:otherwise>
	</c:choose>
 </h2>

<%-- <c:if test="${solicitacaoNormalizacaoMBean.validar}">
	<div class="descricaoOperacao">
		<p><strong> Declaro para os devidos fins, que recebi do usuário ${solicitacaoNormalizacaoMBean.obj.solicitante} nesta presente data, o material impresso referente a esta solicitação.</strong> </p>
	</div>
</c:if> --%>

<a4j:keepAlive beanName="solicitacaoNormalizacaoMBean"></a4j:keepAlive>
<a4j:keepAlive beanName="solicitacaoServicoDocumentoMBean"></a4j:keepAlive>

<h:form>

	<table class="visualizacao" width="90%">
			
			<caption>Solicitação de normalização número ${solicitacaoNormalizacaoMBean.obj.numeroSolicitacao}</caption>
			
			<c:if test="${not empty solicitacaoNormalizacaoMBean.obj.discente}">
				<tr>
					<th width="40%">Solicitante:</th>
					<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.discente}"/></td>
				</tr>
				
				<th>Categoria:</th>
				
				<c:if test="${  ( solicitacaoNormalizacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						|| solicitacaoNormalizacaoMBean.obj.discente.lato 
						|| solicitacaoNormalizacaoMBean.obj.discente.mestrado 
						|| solicitacaoNormalizacaoMBean.obj.discente.doutorado}">
					
					<td>Aluno de Pós-Graduação</td>
				</c:if>
				<c:if test="${ ! ( solicitacaoNormalizacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						&& !solicitacaoNormalizacaoMBean.obj.discente.lato 
						&& !solicitacaoNormalizacaoMBean.obj.discente.mestrado 
						&& !solicitacaoNormalizacaoMBean.obj.discente.doutorado}">
					<td>Aluno de Graduação</td>
				</c:if>
				
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.discente.curso}"/></td>
				</tr>
				
			</c:if>
			
			<c:if test="${not empty solicitacaoNormalizacaoMBean.obj.servidor}">
				<tr>
					<th>Solicitante:</th>
					<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.servidor}"/></td>
				</tr>
				<tr>
					<th>Categoria:</th>
					<td>
						<c:if test="${solicitacaoNormalizacaoMBean.obj.servidor.categoria.docente}" >
							Docente
						</c:if>
						<c:if test="${solicitacaoNormalizacaoMBean.obj.servidor.categoria.tecnico}" >
							Técnico Administrativo
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Lotação:</th>
					<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.servidor.unidade}"/></td>
				</tr>
			</c:if>
			
			<tr>
				<th>Telefone:</th>
				<td> ${solicitacaoNormalizacaoMBean.obj.pessoa.telefone} </td>
			</tr>
			
			<tr>
				<th>Celular:</th>
				<td> ${solicitacaoNormalizacaoMBean.obj.pessoa.celular} </td>
			</tr>
			
			<tr>
				<th>Email:</th>
				<td> ${solicitacaoNormalizacaoMBean.obj.pessoa.email} </td>
			</tr>
			
			<tr>
				<th>Data da Solicitação:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.dataCadastro}"/></td>
			</tr>
			
			<tr>
				<th>Situação da Solicitação:</th>
				<td>
					<c:if test="${solicitacaoNormalizacaoMBean.obj.solicitado}">
						<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.situacao.descricao}"/>
					</c:if>
					<%-- <c:if test="${solicitacaoNormalizacaoMBean.obj.validado}">
						<h:outputText style="color:grey;" value="#{solicitacaoNormalizacaoMBean.obj.situacao.descricao}"/>
					</c:if> --%>
					<c:if test="${solicitacaoNormalizacaoMBean.obj.atendido}">
						<h:outputText style="color:green;"  value="#{solicitacaoNormalizacaoMBean.obj.situacao.descricao}"/>
					</c:if>
					<c:if test="${solicitacaoNormalizacaoMBean.obj.cancelado}">
						<h:outputText style="color:red;"  value="#{solicitacaoNormalizacaoMBean.obj.situacao.descricao}"/>
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{solicitacaoNormalizacaoMBean.obj.biblioteca.descricao}"/></td>
			</tr>
			
			<tr>
				<th>Tipo do Documento:</th>
				<td>
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.tipoDocumento.denominacao}"/>
					<c:if test="${solicitacaoNormalizacaoMBean.obj.tipoDocumento.tipoDocumentoOutro}">
								(  ${solicitacaoNormalizacaoMBean.obj.outroTipoDocumento}  )
					</c:if>	
				</td>
			</tr>
			
			<tr>
				<th>Documento Enviado pelo Usuário:</th>
				<td>
					<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${solicitacaoNormalizacaoMBean.obj.idTrabalhoDigitalizado}&key=${ sf:generateArquivoKey(solicitacaoNormalizacaoMBean.obj.idTrabalhoDigitalizado) }">
						<h:graphicImage url="/img/porta_arquivos/icones/doc.png" style="border:none" title="Clique aqui para visualizar o documento em formato digital." /> Visualizar
					</a>
				</td>
			</tr>
			
			<tr>
				<th style="vertical-align:top;">Aspectos a normalizar:</th>
				<td>
					Trabalho no todo: 
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.trabalhoTodo}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
					<br/>
					
					<c:if test="${! solicitacaoNormalizacaoMBean.obj.trabalhoTodo}">
					 
						Referências:
						<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.referencias}">
					      <f:converter converterId="convertSimNao"/>
						</h:outputText>
						<br/>
						
						Citações:
						<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.citacoes}">
					      <f:converter converterId="convertSimNao"/>
						</h:outputText>
						<br/>
						
						Estrutura do Trabalho:
						<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.estruturaDoTrabalho}">
					      <f:converter converterId="convertSimNao"/>
						</h:outputText>
						<br/>
						
						Pré-textuais:
						<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.preTextuais}">
					      <f:converter converterId="convertSimNao"/>
						</h:outputText>
						<br/>
						
						Pró-textuais:
						<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.proTextuais}">
					      <f:converter converterId="convertSimNao"/>
						</h:outputText>
						<br/>
					
					</c:if>
					
					<c:if test="${solicitacaoNormalizacaoMBean.obj.outrosAspectosNormalizacao}">
						Outros Aspectos da Normalização: <h:outputText value="#{solicitacaoNormalizacaoMBean.obj.descricaoOutrosAspectosNormalizacao}"/>
					</c:if> 
					
				</td>
			</tr>
		
			<%-- <tr>
				<th>Autoriza Descarte?</th>
				<td>
					<h:outputText value="#{solicitacaoNormalizacaoMBean.obj.autorizaDescarte}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
				</td>
			</tr> --%>
			
			<%-- <c:if test="${solicitacaoNormalizacaoMBean.obj.validado || solicitacaoNormalizacaoMBean.obj.atendido}">
				<tr>
					<th style="padding-top: 20px;">Validada por:</th>
					<td style="padding-top: 20px;">
						${solicitacaoNormalizacaoMBean.obj.validador}
					</td>
				</tr>
				<tr>
					<th>Data da Validação:</th>
					<td>
						<ufrn:format type="dataHora" valor="${solicitacaoNormalizacaoMBean.obj.dataValidacao}"/>
					</td>
				</tr>
			</c:if> --%>
			
			<c:if test="${solicitacaoNormalizacaoMBean.obj.atendido}">
				<tr style="margin-top: 20px;">
					<th style="padding-top: 20px;">Atendida por:</th>
					<td style="padding-top: 20px;">
						${solicitacaoNormalizacaoMBean.obj.atendente}
					</td>
				</tr>
				<tr>
					<th>Data do Atendimento:</th>
					<td>
						<ufrn:format type="dataHora" valor="${solicitacaoNormalizacaoMBean.obj.dataAtendimento}"/>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${solicitacaoNormalizacaoMBean.cancelar}">
				<tr>
					<td colspan="2">
						<table class="subFormulario" style="width: 100%">
							<caption>Motivo do Cancelamento</caption>
							<tr>
								<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;">Motivo:</th>
								<td>
									<h:inputTextarea
											id="txtAreaMotivoCancelamento"
											value="#{solicitacaoNormalizacaoMBean.motivoCancelamento}" 
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
							<c:if test="${solicitacaoNormalizacaoMBean.atender || solicitacaoNormalizacaoMBean.cancelar}">
								<h:commandButton
										id="confirmButton"
										value="#{solicitacaoNormalizacaoMBean.confirmButton}"
										action="#{solicitacaoNormalizacaoMBean.confirmar}" />
							</c:if>
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoNormalizacaoMBean.verSolicitacoes}" immediate="true" id="cancelar" />
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
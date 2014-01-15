<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean" />
	<a4j:keepAlive beanName="gerenciarParticipantesExtensaoMBean" />
	<a4j:keepAlive beanName="buscaPadraoParticipanteExtensaoMBean" />
	
	<h2><ufrn:subSistema /> &gt; Gerenciar Participantes  &gt; Remover Participante na Atividade </h2>


	<div class="descricaoOperacao">
		<p>Caro Coordenador(a),</p>
		<p>Confirme a remoção do participante da atividade.</p>
		
		<c:if test="${gerenciarParticipantesExtensaoMBean.gerenciandoParticipantesAtividade}">
			<br/><br/>
			<p><strong>IMPORTANTE:</strong> Caso o participante esteja participando de alguma mini atividade associada 
			a essa atividade, sua participação na mini atividade também será cancelada.</p>
		</c:if>
	</div>
	
	<h:form id="formIncluirParticipante">
	
	
		<table class="formulario" style="width: 100%;">
			 <caption> Dados no Novo Participante</caption>
			 
			 <c:if test="${gerenciarParticipantesExtensaoMBean.gerenciandoParticipantesAtividade}">
				 <tr>
					<th width="20%" class="rotulo" >Atividade de Extensão:</th>
					<td>
						<h:outputText value="#{gerenciarParticipantesExtensaoMBean.obj.atividadeExtensao.titulo}" id="atividade" />
					</td>
				 </tr>
				 <tr>
					<th width="20%" class="rotulo">Tipo:</th>
					<td>
						<h:outputText value="#{gerenciarParticipantesExtensaoMBean.obj.atividadeExtensao.tipoAtividadeExtensao.descricao}" id="tipoatividade" />
					</td>
				</tr>
			 </c:if> 
			 
			 <c:if test="${! gerenciarParticipantesExtensaoMBean.gerenciandoParticipantesAtividade}">
				 <tr>
					<th width="20%" class="rotulo" >Mini Atividade de Extensão:</th>
					<td>
						<h:outputText value="#{gerenciarParticipantesExtensaoMBean.obj.subAtividade.titulo}" id="miniatividade" />
					</td>
				 </tr>
				  <tr>
					<th width="20%" class="rotulo">Tipo:</th>
					<td>
						<h:outputText value="#{gerenciarParticipantesExtensaoMBean.obj.subAtividade.tipoSubAtividadeExtensao.descricao}" id="tipoMiniAtividade" />
					</td>
				 </tr>
				 <tr>
					<th width="20%" class="rotulo" >Atividade de Extensão:</th>
					<td>
						<h:outputText value="#{gerenciarParticipantesExtensaoMBean.obj.subAtividade.atividade.titulo}" id="atividade" />
					</td>
				 </tr>
				 <tr>
					<th width="20%" class="rotulo">Tipo:</th>
					<td>
						<h:outputText value="#{gerenciarParticipantesExtensaoMBean.obj.subAtividade.atividade.tipoAtividadeExtensao.descricao}" id="tipoAtividadeMiniAtividade" />
					</td>
				 </tr>
			 </c:if>
			 
			 <tr>
				<th class="required">Participante:</th>
				<td>
					<h:outputText value="#{gerenciarParticipantesExtensaoMBean.obj.cadastroParticipante.identificacaoNome}" />
				</td>
			</tr>
			 
			 <tr>
				<th class="required">Tipo de Participação:</th>
				<td>
					<h:selectOneMenu id="tipoParticipacao" value="#{gerenciarParticipantesExtensaoMBean.obj.tipoParticipacao.id}" 
						disabled="#{true}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --" />
						<f:selectItems value="#{gerenciarParticipantesExtensaoMBean.tiposParticipacaoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			 
			 <tr>
				<th class="required">Frequência de Participação:</th>
				<td>
					<h:outputText value="#{gerenciarParticipantesExtensaoMBean.obj.frequencia}" id="frequencia" /> %
				</td>
			</tr>
			 
			 <tr>
				<th>Autorizar Declaração? 
					<ufrn:help img="/img/ajuda.gif">Autoriza a receber declaração de participação da Ação de Extensão</ufrn:help>
				</th>
				<td><h:selectOneRadio value="#{gerenciarParticipantesExtensaoMBean.obj.autorizacaoDeclaracao}"
							disabled="#{true}" id="declaracao">
						<f:selectItem itemValue="true" itemLabel="SIM" />
						<f:selectItem itemValue="false" itemLabel="NÃO" />
					</h:selectOneRadio>
				</td>
			</tr>
			 
			 <tr>
				<th>Autorizar Certificado? 
					<ufrn:help img="/img/ajuda.gif">Autoriza a receber certificado de participação da Ação de Extensão</ufrn:help>
				</th>
				<td><h:selectOneRadio value="#{gerenciarParticipantesExtensaoMBean.obj.autorizacaoCertificado}"
							disabled="#{true}" id="certificado">
						<f:selectItem itemValue="true" itemLabel="SIM" />
						<f:selectItem itemValue="false" itemLabel="NÃO" />
					</h:selectOneRadio>
				</td>
			</tr>
			 
			 <tr>
			 	<th>Observação no Certificado:
			 	</th>		 
				
				<td>
						<h:inputTextarea id="observacoCertificado" label="Observação no Certificado" 
							value="#{gerenciarParticipantesExtensaoMBean.obj.observacaoCertificado}" rows="2" cols="60"  style="width: 95%;"
						   disabled="true">
					</h:inputTextarea>			
					<br/>
					
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
					
						<h:commandButton id="btRemover"  value="Confirmar Remoção" action="#{gerenciarParticipantesExtensaoMBean.removerParticipanteAtividade}" />
						
						<h:commandButton value="Cancelar" id="btcancelar2" action="#{gerenciarParticipantesExtensaoMBean.telaListaParticipantesParaGerenciar}" immediate="true"/>
						
					</td>
				</tr>
			</tfoot>
							
		</table>
		
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
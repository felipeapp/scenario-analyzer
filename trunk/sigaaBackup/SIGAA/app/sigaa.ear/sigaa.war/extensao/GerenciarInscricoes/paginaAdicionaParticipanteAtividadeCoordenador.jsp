<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean" />
	<a4j:keepAlive beanName="gerenciarParticipantesExtensaoMBean" />
	<a4j:keepAlive beanName="buscaPadraoParticipanteExtensaoMBean" />
	
	<h2><ufrn:subSistema /> &gt; Gerenciar Participantes  &gt; Incluir Participante na Atividade </h2>


	<div class="descricaoOperacao">
		<p>Caro Coordenador(a),</p>
		<p>Essa opera��o permite incluir participantes diretamente nas atividades de extens�o, mesmo que esses participantes 
		n�o tenham realizado a inscri��o na atividade.</p>
		
		<p>O participante receber� um e-mail informando da sua inscri��o e poder� acessar o sistema para 
		     acompanhar as atividades, emitir certificados, entre outras opera��es.</p>
		<br/>
		<p><strong>IMPORTANTE:</strong> Ao utilizar essa op��o <strong>n�o</strong> � criada uma inscri��o para o participante. Caso a atividade exija 
		pagamento de alguma taxa, esse pagamento poder� ser gerenciado pelo sistema, pois o participante n�o se inscreveu.
		Nesse caso, o controle do pagamento deve ser realizado manualmente.
		</p>
		
	</div>
	
	<h:form id="formIncluirParticipante">
	
	
		<table class="formulario" style="width: 100%;">
			 <caption> Dados do Novo Participante</caption>
			 
			 <c:if test="${gerenciarParticipantesExtensaoMBean.gerenciandoParticipantesAtividade}">
				 <tr>
					<th width="20%" class="rotulo" >Atividade de Extens�o:</th>
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
					<th width="20%" class="rotulo" >Mini Atividade de Extens�o:</th>
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
					<th width="20%" class="rotulo" >Atividade de Extens�o:</th>
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
				<th class="required">Tipo de Participa��o:</th>
				<td>
					<h:selectOneMenu id="tipoParticipacao" value="#{gerenciarParticipantesExtensaoMBean.obj.tipoParticipacao.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --" />
						<f:selectItems value="#{gerenciarParticipantesExtensaoMBean.tiposParticipacaoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			 
			 <tr>
				<th class="required">Frequ�ncia de Participa��o:</th>
				<td>
					<h:inputText id="frequencia" label="Frequ�ncia" value="#{gerenciarParticipantesExtensaoMBean.obj.frequencia}"
						maxlength="3" size="4" onkeyup="return formatarInteiro(this)" /> %
				</td>
			</tr>
			 
			 <tr>
				<th>Autorizar Declara��o? 
					<ufrn:help img="/img/ajuda.gif">Autoriza a receber declara��o de participa��o da A��o de Extens�o</ufrn:help>
				</th>
				<td><h:selectOneRadio value="#{gerenciarParticipantesExtensaoMBean.obj.autorizacaoDeclaracao}"
							disabled="#{participanteAcaoExtensao.readOnly}" id="declaracao">
						<f:selectItem itemValue="true" itemLabel="SIM" />
						<f:selectItem itemValue="false" itemLabel="N�O" />
					</h:selectOneRadio>
				</td>
			</tr>
			 
			 <tr>
				<th>Autorizar Certificado? 
					<ufrn:help img="/img/ajuda.gif">Autoriza a receber certificado de participa��o da A��o de Extens�o</ufrn:help>
				</th>
				<td><h:selectOneRadio value="#{gerenciarParticipantesExtensaoMBean.obj.autorizacaoCertificado}"
							disabled="#{participanteAcaoExtensao.readOnly}" id="certificado">
						<f:selectItem itemValue="true" itemLabel="SIM" />
						<f:selectItem itemValue="false" itemLabel="N�O" />
					</h:selectOneRadio>
				</td>
			</tr>
			 
			 <tr>
			 	<th>Observa��o no Certificado:
			 		<ufrn:help> Atributo de texto livre para o coordenador inserir uma informa��o adicional que desejar 
			 	            que seja impressa no certificado, como classifica��o ou premia��o.
	 				</ufrn:help>
			 	</th>		 
				
				<td>
						<h:inputTextarea id="observacoCertificado" label="Observa��o no Certificado" 
							value="#{gerenciarParticipantesExtensaoMBean.obj.observacaoCertificado}" rows="2" cols="60"  style="width: 95%;"
							onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 180);">
					</h:inputTextarea>			
					<br/>
					Caracteres Restantes: <span id="quantidadeCaracteresDigitados">180</span>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
					
						<h:commandButton id="btCadastrar"  value="Adicionar Participante" action="#{gerenciarParticipantesExtensaoMBean.confirmaIncluscaoParticipanteAtividade}" 
							rendered="#{! gerenciarParticipantesExtensaoMBean.alterandoParticipante}"/>
						
						<h:commandButton id="btAlterar"  value="Alterar Participante" action="#{gerenciarParticipantesExtensaoMBean.confirmaAlteracaoParticipante}"
							rendered="#{gerenciarParticipantesExtensaoMBean.alterandoParticipante}" />
								
						<h:commandButton value="Cancelar" id="btcancelar1" action="#{buscaPadraoParticipanteExtensaoMBean.telaBuscaPadraoParticipantesExtensao}" 
							rendered="#{! gerenciarParticipantesExtensaoMBean.alterandoParticipante}"  onclick="#{confirm}" immediate="true"/>
						
						<h:commandButton value="Cancelar" id="btcancelar2" action="#{gerenciarParticipantesExtensaoMBean.telaListaParticipantesParaGerenciar}" 
						 	rendered="#{gerenciarParticipantesExtensaoMBean.alterandoParticipante}" onclick="#{confirm}" immediate="true"/>
						
						
						
					</td>
				</tr>
			</tfoot>
							
		</table>
		
	</h:form>

</f:view>

<script type="text/javascript">
	function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
			
			if (field.value.length > maxlimit){
				field.value = field.value.substring(0, maxlimit);
			}else{ 
				document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
			} 
		}
		
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
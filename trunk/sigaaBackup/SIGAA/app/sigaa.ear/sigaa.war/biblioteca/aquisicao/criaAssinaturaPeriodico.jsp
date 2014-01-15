<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>



<c:if test="${! assinaturaPeriodicoMBean.alteracao}">
	<h2>  <ufrn:subSistema /> &gt; Criar Assinatura de Peri�dicos </h2>
</c:if>

<c:if test="${assinaturaPeriodicoMBean.alteracao}">
	<h2>  <ufrn:subSistema /> &gt; Alterar Assinatura de Peri�dicos </h2>
</c:if>


<div class="descricaoOperacao">

		<c:if test="${! assinaturaPeriodicoMBean.alteracao}">
				<p> P�gina para o cadastro de uma nova assinatura de peri�dicos.</p>
				<p> A partir desse cadastro ser� poss�vel controlar a chegada dos fasc�culos � biblioteca, bem como verificar
				assinaturas que expiraram ou fasc�culos que n�o foram entregues.</p>
				<p> Para o caso de assinaturas de compras, o c�digo da assinatura deve ser o c�digo do assinante que vem junto com os fasc�culo. 
				Para assinaturas de doa��o, o c�digo vai ser gerado automaticamente pelo sistema.</p>
		</c:if>
		
		<c:if test="${assinaturaPeriodicoMBean.alteracao}">
			<p> P�gina para a altera��o de uma assinatura de peri�dicos.</p>
			<p> Para assinaturas com a modalidade de aqui��o <strong>Compra</strong>, alterando-se a data de t�rmino da assinatura pode-se ativ�-la ou desativ�-la, permitindo a inclus�o de novos fasc�culos a ela ou n�o.</p>
		</c:if>

	
</div>


<%-- Mostra mensagens que ocorrem no processamento ajax, porque da maneira normal n�o s�o mostradas --%>
<f:view>

	<h:form id="fromIncluirItem">

		<%-- Fica guardando os itens gerados entre requisicoes  --%>
		<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>
			
	
		<table class="formulario" width="100%">
		
			<caption>
				<c:if test="${! assinaturaPeriodicoMBean.alteracao}"> Nova Assinatura </c:if>
				<c:if test="${assinaturaPeriodicoMBean.alteracao}"> Alterar Assinatura </c:if>
			</caption>

			<tbody>

				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
					<tr>
						<th colspan="2" class="required">Modalidade de Aquisi��o:</th>
						<td colspan="2" >			
							<h:selectOneMenu id="comboBoxModalidadeAquisicao" value="#{assinaturaPeriodicoMBean.obj.modalidadeAquisicao}" 
									valueChangeListener="#{assinaturaPeriodicoMBean.verificaAlteracaoModalidadeAquisicao}" onchange="submit();">
								<f:selectItem itemLabel="Compra" itemValue="#{assinaturaPeriodicoMBean.obj.modalidadeCompra}" />
								<f:selectItem itemLabel="Doa��o" itemValue="#{assinaturaPeriodicoMBean.obj.modalidadeDoacao}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</ufrn:checkRole>
				
				<ufrn:checkNotRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO } %>">
						<tr>
							<th colspan="2" class="required">Modalidade de Aquisi��o:</th>
							<td colspan="2" >			
								<h:selectOneMenu id="comboBoxModalidadeAquisicaoDoacao" value="#{assinaturaPeriodicoMBean.obj.modalidadeAquisicao}" 
										valueChangeListener="#{assinaturaPeriodicoMBean.verificaAlteracaoModalidadeAquisicao}" onchange="submit();">
									<f:selectItem itemLabel="Doa��o" itemValue="#{assinaturaPeriodicoMBean.obj.modalidadeDoacao}" />
								</h:selectOneMenu>
							</td>
						</tr>
					</ufrn:checkRole>
				</ufrn:checkNotRole>
				
				<tr>
					<th colspan="2" class="required">C�digo:</th>
					<td width="30%"> 
						<h:outputText value="#{ assinaturaPeriodicoMBean.obj.codigo}" rendered="#{assinaturaPeriodicoMBean.alteracao}" /> 
						
						<h:inputText id="comboBoxCodigoAssinatura" value="#{ assinaturaPeriodicoMBean.obj.codigo}" size="20" maxlength="20" 
								rendered="#{!assinaturaPeriodicoMBean.alteracao && assinaturaPeriodicoMBean.obj.assinaturaDeCompra
										&& ! assinaturaPeriodicoMBean.obj.assinaturaDeDoacao && !assinaturaPeriodicoMBean.gerarCodigoAssinatuaCompra }" onkeyup="CAPS(this)"/>
										
						<h:outputText value="Gerado automaticamente pelo Sistema" 
								rendered="#{ ( !assinaturaPeriodicoMBean.alteracao && assinaturaPeriodicoMBean.obj.assinaturaDeDoacao ) 
								     || ( ! assinaturaPeriodicoMBean.alteracao && assinaturaPeriodicoMBean.gerarCodigoAssinatuaCompra ) }"/> 
					</td>
					
					<td>
						<h:outputText value="Gerar C�digo ?" rendered="#{!assinaturaPeriodicoMBean.alteracao && assinaturaPeriodicoMBean.obj.assinaturaDeCompra
										&& ! assinaturaPeriodicoMBean.obj.assinaturaDeDoacao}"  />
						<h:selectBooleanCheckbox id="checkBoxGerarCodigoAssinatura" value="#{assinaturaPeriodicoMBean.gerarCodigoAssinatuaCompra}"
							valueChangeListener="#{assinaturaPeriodicoMBean.verificaUsuarioSelecionouGerarCodigo}" onclick="submit();"
								rendered="#{!assinaturaPeriodicoMBean.alteracao && assinaturaPeriodicoMBean.obj.assinaturaDeCompra
										&& ! assinaturaPeriodicoMBean.obj.assinaturaDeDoacao}">
						</h:selectBooleanCheckbox>
						
						<c:if test="${!assinaturaPeriodicoMBean.alteracao && assinaturaPeriodicoMBean.obj.assinaturaDeCompra
										&& ! assinaturaPeriodicoMBean.obj.assinaturaDeDoacao}">
							<ufrn:help>Para assinaturas de compra antigas, cujo c�digo � desconhecido.</ufrn:help>
						</c:if>
					</td>
					
				</tr>
				
				
				<tr>
					<th colspan="2"  class="required">T�tulo da Assinatura:</th>
					<td colspan="2"> 
						<h:inputText id="inputTextTituloAssinatura" value="#{ assinaturaPeriodicoMBean.obj.titulo}" size="30" maxlength="200"  /> 
					</td>
				</tr>
				
				<tr>
					<th colspan="2">ISSN:</th>
					<td colspan="2"> 
						<h:inputText id="inputTextISSNAssinatura" value="#{ assinaturaPeriodicoMBean.obj.issn}" size="20" /> 
					</td>
				</tr>
				
				<tr>	
					<th colspan="2">Data de In�cio da Assinatura:</th>
					<td colspan="2" >	
						<t:inputCalendar id="DataInicioAssinatura" value="#{assinaturaPeriodicoMBean.obj.dataInicioAssinatura}" 
							size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
							renderPopupButtonAsImage="true" title="Data de In�cio da Assinatura"
							onkeypress="return formataData(this, event)"/>
					</td>
				</tr>
				
				
				<tr>
					<th colspan="2">Data de T�rmino da Assinatura:</th> 
					<td colspan="2">
						<t:inputCalendar id="DataTerminoAssinatura" value="#{assinaturaPeriodicoMBean.obj.dataTerminoAssinatura}" 
							size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
							renderPopupButtonAsImage="true" title="Data de T�rmino da Assinatura"
							onkeypress="return formataData(this, event)"/>
					</td>
				</tr>
				
				
				<tr>
					<th colspan="2"  class="required">Unidade de Destino:</th> 
					<td colspan="2" >
						<%-- N�o pode alterar a biblioteca da assintura, a menos que altere a biblioteca de todos os fasc�culos dessa assinatura e isso � feito na parte de transfer�nica n�o por aqui --%>
						<h:selectOneMenu id="comboBoxUnidadeDestinoAssinatura" value="#{assinaturaPeriodicoMBean.obj.unidadeDestino.id}" disabled="#{assinaturaPeriodicoMBean.alteracao}" > 
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{assinaturaPeriodicoMBean.bibliotecasInternas}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				
				<tr>
					<th colspan="2"  class="required">Periodicidade:</th> 
					<td colspan="2" >
						<h:selectOneMenu id="comboBoxPeriodicidadeAssinatura" value="#{assinaturaPeriodicoMBean.obj.frequenciaPeriodicos.id}"> 
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{assinaturaPeriodicoMBean.allFrequenciasAtivasComboBox}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th colspan="2"  class="required">Internacional ? </th> 
					<td colspan="2" >			
						<h:selectOneMenu id="comboBoxInternacional" value="#{assinaturaPeriodicoMBean.obj.internacional}" >
							<f:selectItem itemLabel="N�O" itemValue="false" />
							<f:selectItem itemLabel="SIM" itemValue="true" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				
				
				
				<%-- Daqui para baixo sao informacoes que o aleph chama de padrao aleph, usadas para gerar os dados sequenciais dos itens --%>
				
				
				<tr>
					<th  class="required">N�mero do Primeiro Fasc�culo:</th>
					<td>
						<h:inputText id="inputTextNumeroPrimeiroFasciculo" value="#{ assinaturaPeriodicoMBean.obj.numeroPrimeiroFasciculo}" size="10" maxlength="7"  onkeyup="return formatarInteiro(this);"/>
					</td>
					<th>N�mero do �ltimo Fasc�culo:</th>
					<td>
						<h:inputText id="inputTextNumeroUltimoFasciculo" value="#{ assinaturaPeriodicoMBean.obj.numeroUltimoFasciculo}" size="10" maxlength="7"  onkeyup="return formatarInteiro(this);"/>
					</td>
				</tr>
				
				<tr>
					<th>N�mero do Primeiro Volume:</th>
					<td>
						<h:inputText id="inputTextNumeroPrimeiroVolume" value="#{ assinaturaPeriodicoMBean.obj.numeroPrimeiroVolume}" size="10" maxlength="7"  onkeyup="return formatarInteiro(this);"/>
					</td>
					<th >N�mero do �ltimo Volume:</th>
					<td>
						<h:inputText id="inputTextNumeroUltimoVolume" value="#{ assinaturaPeriodicoMBean.obj.numeroUltimoVolume}" size="10" maxlength="7"  onkeyup="return formatarInteiro(this);"/>
					</td>
				</tr>

			</tbody>

			<tfoot>
				<tr>
					<td colspan="5" align="center">
			
						<h:commandButton id="cmdLinkCriarAssinatura"  value="Criar Assinatura" 
								action="#{assinaturaPeriodicoMBean.criarAssinatura}" 
								rendered="#{! assinaturaPeriodicoMBean.alteracao && ! assinaturaPeriodicoMBean.pesquisaSelecionaAssinaturaTransferenciaFasciculos}" />
						
						<h:commandButton id="cmdLinkCriarAssinaturaTransferenciaPeriodicos" value="Criar Assinatura" 
								action="#{assinaturaPeriodicoMBean.criarAssinaturaTransferenciaPeriodico}" 
								rendered="#{! assinaturaPeriodicoMBean.alteracao && assinaturaPeriodicoMBean.pesquisaSelecionaAssinaturaTransferenciaFasciculos }"/>
						

						<h:commandButton id="cmdLinkAlterarAssinatura" value="Alterar Assinatura" action="#{assinaturaPeriodicoMBean.alterarAssinatura}" rendered="#{assinaturaPeriodicoMBean.alteracao}" />
		
						<h:commandButton value="Cancelar" action="#{assinaturaPeriodicoMBean.telaBuscaAssinaturas}" immediate="true" onclick="#{confirm}" />
						
							
					</td>
				</tr>	
			</tfoot>

		</table>
		
		
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>


	</h:form>
		

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
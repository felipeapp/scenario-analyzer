<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Aprovação da Proposta de Curso</h2>
	<h:form>
		<table class="visualizacao" width="50%">
			<caption>Dados do Curso</caption>
			<tr>
				<th>Nome:</th>
				<td>${ aprovarPropostaLato.curso.nome }</td>
			</tr>
			<tr>
		    	<th>Tipo:</th>
				<td>
					<h:outputText value="#{aprovarPropostaLato.curso.tipoCursoLato.descricao}"/>
				</td>
		   </tr>
		   <tr>
		    	<th>Unidade Responsável:</th>
		    	<td>
					<h:outputText value="#{aprovarPropostaLato.curso.unidade.nome}"/>
				</td>
    		</tr>
    		<tr>
		   		<th>Modalidade de Educação:</th>
			   	<td>
					<h:outputText  value="#{aprovarPropostaLato.curso.modalidadeEducacao.descricao}"/>
			   	</td>
		   </tr>
			<tr>
				<td colspan="2" class="subFormulario">Configuração da GRU para Cobrança de Taxas/Mensalidades do Curso</td>
			</tr>
			<!-- CONFIGURAÇÃO DA GRU PARA PAGAMENTO DE TAXA DE INSCRIÇÃO EM PROCESSOS SELETIVOS -->
			<h:panelGroup rendered="#{ empty aprovarPropostaLato.curso.configuracaoGRUInscricao }">
				<tr>
					<td colspan="2" class="subFormulario">Taxa de Inscrição</td>
				</tr>
				<tr>
					<td colspan="2" style="text-align:center">Este curso não utilizará GRU para cobrança de Taxa de Inscrição em Processos Seletivos</td> 
				</tr>
			</h:panelGroup>
			<h:panelGroup id="panelTaxaInscricao" rendered="#{ not empty aprovarPropostaLato.curso.configuracaoGRUInscricao }">
				<tr>
					<td colspan="2" class="subFormulario">Taxa de Inscrição</td>
				</tr>
				<tr>
					<th>Tipo de GRU:</th>
					<td>
						<h:outputText value="GRU Simples" rendered="#{ aprovarPropostaLato.curso.configuracaoGRUInscricao.gruSimples }" />
						<h:outputText value="GRU Cobrança" rendered="#{ !aprovarPropostaLato.curso.configuracaoGRUInscricao.gruSimples }" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Tipo de Arrecadação:</th>
					<td>
						<h:outputText value="#{ aprovarPropostaLato.curso.configuracaoGRUInscricao.tipoArrecadacao.descricao }" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Código de Recolhimento:</th>
					<td><h:outputText
							value="#{aprovarPropostaLato.curso.configuracaoGRUInscricao.tipoArrecadacao.codigoRecolhimento.codigo} - #{aprovarPropostaLato.curso.configuracaoGRUInscricao.tipoArrecadacao.codigoRecolhimento.descricao}"/>
					</td>
				</tr>
				<tr>
					<th>Unidade Favorecida:</th>
					<td>
						<h:outputText value="#{ aprovarPropostaLato.curso.configuracaoGRUInscricao.unidade.nome }"/>
					</td>
				</tr>
				<c:choose>
					<c:when test="${ aprovarPropostaLato.curso.configuracaoGRUInscricao.gruSimples }">
						<tr>
							<th>Código da Gestão:</th>
							<td>
								${ aprovarPropostaLato.curso.configuracaoGRUInscricao.grupoEmissaoGRU.codigoGestao }
							</td>
						</tr>
						<tr>
							<th>Código da Unidade Gestora:</th>
							<td><h:outputText
									value="#{ aprovarPropostaLato.curso.configuracaoGRUInscricao.grupoEmissaoGRU.codigoUnidadeGestora }" />
							</td>
						</tr>
					</c:when>
					<c:when test="${ !aprovarPropostaLato.curso.configuracaoGRUInscricao.gruSimples }">
						<tr>
							<th>Agência:</th>
							<td><h:outputText
									value="#{ aprovarPropostaLato.curso.configuracaoGRUInscricao.grupoEmissaoGRU.agencia }" />
							</td>
						</tr>
						<tr>
							<th>Código Cedente:</th>
							<td><h:outputText
									value="#{ aprovarPropostaLato.curso.configuracaoGRUInscricao.grupoEmissaoGRU.codigoCedente }" />
							</td>
						</tr>
						<tr>
							<th>Número do Convênio:</th>
							<td><h:outputText
									value="#{ aprovarPropostaLato.curso.configuracaoGRUInscricao.grupoEmissaoGRU.convenio }" />
							</td>
						</tr>
					</c:when>
				</c:choose>
			</h:panelGroup>
			<!-- CONFIGURAÇÃO DA GRU PARA PAGAMENTO DE MENSALIDADES -->
			<h:panelGroup rendered="#{ empty aprovarPropostaLato.curso.configuracaoGRUMensalidade }">
				<tr>
					<td colspan="2" class="subFormulario">Pagamento de Mensalidade</td>
				</tr>
				<tr>
					<td colspan="2" style="text-align:center">Este curso não utilizará GRU para cobrança de Mensalidades</td> 
				</tr>
			</h:panelGroup>
			<h:panelGroup id="panelTaxaMensalidade" rendered="#{ not empty aprovarPropostaLato.curso.configuracaoGRUMensalidade }">
				<tr>
					<td colspan="2" class="subFormulario">Pagamento de Mensalidade</td>
				</tr>
				<tr>
					<th>Tipo de GRU:</th>
					<td>
						<h:outputText value="GRU Simples" rendered="#{ aprovarPropostaLato.curso.configuracaoGRUMensalidade.gruSimples }" />
						<h:outputText value="GRU Cobrança" rendered="#{ !aprovarPropostaLato.curso.configuracaoGRUMensalidade.gruSimples }" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Tipo de Arrecadação:</th>
					<td>
						<h:outputText value="#{aprovarPropostaLato.curso.configuracaoGRUMensalidade.tipoArrecadacao.descricao}" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Código de Recolhimento:</th>
					<td><h:outputText
							value="#{aprovarPropostaLato.curso.configuracaoGRUMensalidade.tipoArrecadacao.codigoRecolhimento.codigo} - #{aprovarPropostaLato.curso.configuracaoGRUMensalidade.tipoArrecadacao.codigoRecolhimento.descricao}"/>
					</td>
				</tr>
				<tr>
					<th>Unidade Favorecida:</th>
					<td>
						<h:outputText value="#{ aprovarPropostaLato.curso.configuracaoGRUMensalidade.unidade.nome }"/>
					</td>
				</tr>
				<c:choose>
					<c:when test="${ aprovarPropostaLato.curso.configuracaoGRUMensalidade.gruSimples }">
						<tr>
							<th>Código da Gestão:</th>
							<td><h:outputText
									value="#{ aprovarPropostaLato.curso.configuracaoGRUMensalidade.grupoEmissaoGRU.codigoGestao }" />
							</td>
						</tr>
						<tr>
							<th>Código da Unidade Gestora:</th>
							<td><h:outputText
									value="#{ aprovarPropostaLato.curso.configuracaoGRUMensalidade.grupoEmissaoGRU.codigoUnidadeGestora }" />
							</td>
						</tr>
					</c:when>
					<c:when test="${ !aprovarPropostaLato.curso.configuracaoGRUMensalidade.gruSimples }">
						<tr>
							<th>Agência:</th>
							<td><h:outputText
									value="#{ aprovarPropostaLato.curso.configuracaoGRUMensalidade.grupoEmissaoGRU.agencia }" />
							</td>
						</tr>
						<tr>
							<th>Código Cedente:</th>
							<td><h:outputText
									value="#{ aprovarPropostaLato.curso.configuracaoGRUMensalidade.grupoEmissaoGRU.codigoCedente }" />
							</td>
						</tr>
						<tr>
							<th>Número do Convênio:</th>
							<td><h:outputText
									value="#{ aprovarPropostaLato.curso.configuracaoGRUMensalidade.grupoEmissaoGRU.convenio }" />
							</td>
						</tr>
					</c:when>
				</c:choose>
			</h:panelGroup>
		</table>
		</br>
		<table class="formulario" width="50%">
			<caption>Informações da Portaria</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Número:</th>
					<td><h:inputText value="#{aprovarPropostaLato.obj.proposta.numeroPortaria}" size="10"
							maxlength="10" onkeyup="formatarInteiro(this);" alt="numero" title="numero"/></td>
				</tr>
				<tr>
					<th class="obrigatorio">Ano:</th>
					<td><h:inputText value="#{aprovarPropostaLato.obj.proposta.anoPortaria}" size="4" 
							maxlength="4" onkeyup="formatarInteiro(this);" alt="ano" title="ano" /></td>
				</tr>
				<tr>
					<th>Data Publicação Portaria:</th>
					<td>
				    	 <t:inputCalendar value="#{aprovarPropostaLato.obj.proposta.dataPublicacaoPortaria}" id="dataInicio" size="10" maxlength="10" 
	   						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	   						renderAsPopup="true" renderPopupButtonAsImage="true" >
	     						<f:converter converterId="convertData"/>
						</t:inputCalendar> 
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Aprovar" action="#{aprovarPropostaLato.aprovar}" />
						<h:commandButton value="Cancelar" action="#{aprovarPropostaLato.carregarPropostasSubmetidas}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<div style="margin-left: 50px;">
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
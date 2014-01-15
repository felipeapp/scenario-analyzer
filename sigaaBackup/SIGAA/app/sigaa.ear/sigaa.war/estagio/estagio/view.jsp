<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="estagioMBean" />
<a4j:keepAlive beanName="buscaEstagioMBean"/>
<h2> <ufrn:subSistema /> &gt; Visualização do Estágio</h2>
<h:form id="form">
	<table class="visualizacao" style="width: 90%">
		<caption>Dados da Oferta de Estágio</caption>
		<tr>
			<td colspan="4" class="subFormulario">Dados do Concedente do Estágio</td>
		</tr>
		<tr>
			<th style="width: 30%;">Tipo do Convênio:</th>
			<td colspan="3">
				<h:outputText value="#{estagioMBean.obj.concedente.convenioEstagio.tipoConvenio.descricao}"/>
			</td>
		</tr>	
		<tr>
			<th>CNPJ:</th>
			<td colspan="3">
				<h:outputText value="#{estagioMBean.obj.concedente.pessoa.cpfCnpjFormatado}"/>
			</td>
		</tr>
		<tr>
			<th>Nome:</th>
			<td colspan="3">
				<h:outputText value="#{estagioMBean.obj.concedente.pessoa.nome}"/>																																				
			</td>
		</tr>
		<tr>
			<th>Responsável:</th>
			<td colspan="3">
				<h:outputText value="#{estagioMBean.obj.concedente.responsavel.pessoa.nome}"/>
			</td>
		</tr>
	</table>
	
	<c:set var="discente" value="#{estagioMBean.obj.discente}"/>
	<%@include file="include/_discente.jsp"%>
	
	<c:if test="${estagioMBean.obj.supervisor != null && estagioMBean.obj.supervisor.id > 0}">
		<table class="visualizacao" style="width: 90%">
			<tr>
				<td class="subFormulario" colspan="2">Dados do Supervisor</td>		
			</tr>	
			<tr>
				<th width="30%">CPF do Supervisor:</th>
				<td>
					<h:outputText value="#{estagioMBean.obj.supervisor.cpfCnpjFormatado}"/>																																				
				</td>
			</tr>	
			<tr>
				<th width="30%">Nome do Supervisor:</th>
				<td>
					<h:outputText value="#{estagioMBean.obj.supervisor.nome}"/>																																				
				</td>
			</tr>			
		</table>	
	</c:if>
	<c:if test="${estagioMBean.obj.interesseOferta.oferta.id > 0}">
		<table class="visualizacao" style="width: 90%">
			<tr>
				<td class="subFormulario" colspan="2">Oferta de Estágio</td>		
			</tr>	
			<tr>
				<th width="30%">Titulo:</th>
				<td>
					<h:outputText value="#{estagioMBean.obj.interesseOferta.oferta.titulo}"/>																																				
				</td>
			</tr>	
			<tr>
				<th width="30%">Descrição:</th>
				<td>
					${estagioMBean.obj.interesseOferta.oferta.descricao}																																				
				</td>
			</tr>			
		</table>	
	</c:if>
	<br/>

		<table class="visualizacao" style="width:  90%;">
			<caption>Dados do Estágio</caption>	
			<tr>
				<th width="30%">Situação do Estágio:</th>
				<td colspan="3">
					${estagioMBean.obj.status.descricao}
				</td>
			</tr>
			<a4j:region id="parecer" rendered="#{!estagioMBean.parecerAprovado}">
				<tr>
					<th style="width: 30%;">Motivo do Parecer:</th>
					<td colspan="3">
						${estagioMBean.obj.obsParecerCoordenador}
					</td>
				</tr>
			</a4j:region>												
			<a4j:region id="dadosEstagio" rendered="#{estagioMBean.parecerAprovado}">						
				<tr>
					<th style="width: 30%;">Tipo do Estágio:</th>
					<td colspan="3">
						${estagioMBean.obj.tipoEstagio.descricao}
					</td>
				</tr>	
				<tr>
					<th>Carga Horária Semanal:</th>
					<td>
						<h:outputText value="#{estagioMBean.obj.cargaHorariaSemanal}"/>
					</td>									
					<th>Alterna Teoria e Prática? :</th>
					<td>
						<ufrn:format type="simnao" valor="${estagioMBean.obj.alternaTeoriaPratica}"></ufrn:format>
					</td>
				</tr>	
				<tr>
					<th>Professor  Orientador do Estágio:</th>
					<td colspan="3">
						${estagioMBean.obj.orientador.pessoa.nome}				
					</td>
				</tr>					
				<tr>
					<th>Início do Estágio:</th>
					<td>
						<ufrn:format type="data" valor="${estagioMBean.obj.dataInicio}"></ufrn:format>
					</td>
					<th>Fim do Estágio:</th>
					<td>
						<ufrn:format type="data" valor="${estagioMBean.obj.dataFim}"></ufrn:format>			
					</td>
				</tr>	
				<tr>
					<th>Horário do Estágio:</th>
					<td colspan="3">
						${estagioMBean.obj.descricaoHorarioEstagio}			
					</td>
				</tr>				
				<tr>
					<th>Valor da Bolsa:</th>		
					<td>
						<ufrn:format type="valor" valor="${estagioMBean.obj.valorBolsa}"></ufrn:format>
					</td>
					<th>Valor Aux. Transporte:</th>		
					<td>
						<ufrn:format type="valor" valor="${estagioMBean.obj.valorAuxTransporte}"></ufrn:format> ao dia
					</td>			
				</tr>
				<tr>
					<th>Descrição das Atividades:</th>
					<td colspan="3">
						<h:outputText value="#{estagioMBean.obj.descricaoAtividades}"/>
					</td>
				</tr>									
				<tr>
					<td colspan="4" class="subFormulario">Dados do Seguro contra Acidentes Pessoais</td>
				</tr>	
				<tr>
					<th>CNPJ:</th>
					<td colspan="3">
						<h:outputText value="#{estagioMBean.obj.cnpjSeguradoraFormatado}"/>
					</td>
				</tr>
				<tr>
					<th>Seguradora:</th>
					<td colspan="3">				
						<c:if test="${not empty estagioMBean.obj.seguradora}">
							${estagioMBean.obj.seguradora}				
						</c:if>
						<c:if test="${empty estagioMBean.obj.seguradora}">
							Não informado.
						</c:if>
					</td>
				</tr>	
				<tr>
					<th>Apólice do Seguro:</th>		
					<td>
						<c:if test="${not empty estagioMBean.obj.apoliceSeguro}">
							${estagioMBean.obj.apoliceSeguro}				
						</c:if>
						<c:if test="${empty estagioMBean.obj.apoliceSeguro}">
							Não informado.
						</c:if>
					</td>
					<th>Valor Seguro:</th>		
					<td>
						<ufrn:format type="valor" valor="${estagioMBean.obj.valorSeguro}"></ufrn:format>
					</td>			
				</tr>
				<c:if test="${!estagioMBean.cadastro && not empty estagioMBean.obj.renovacoes}">
					<tr>
						<td colspan="4">
							<table class="subFormulario" width="100%">
								<caption>Renovações do Estágio</caption>
								<thead>
									<tr>
										<th style="text-align: center;">Data Renovação</th>
										<th style="text-align: center;">Data Anterior</th>
										<th style="text-align: center;" >Data Renovada</th>
										<th style="text-align: left;">Observação</th>
									</tr>	
								</thead>				
								<c:forEach items="#{estagioMBean.obj.renovacoes}" var="renovacao" varStatus="loop">
									<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
										<td style="text-align: center;">
											<ufrn:format type="data" valor="${renovacao.dataCadastro}"/>
										</td>
										<td style="text-align: center;">
											<ufrn:format type="data" valor="${renovacao.dataFimAnterior}"/>
										</td>
										<td style="text-align: center;">
											<ufrn:format type="data" valor="${renovacao.dataRenovacao}"/>
										</td>
										<td style="text-align: left;">${renovacao.observacao}</td>
									</tr>
								</c:forEach>									
							</table>						
						</td>
					</tr>
				</c:if>
			</a4j:region>
			<c:if test="${estagioMBean.cadastro}">	
				<tr>
					<td colspan="4" class="semNegrito">
						<c:set var="exibirApenasSenha" value="true" scope="request" />
						<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
					</td>
				</tr>  				
			</c:if>
			<c:if test="${!estagioMBean.cadastro && estagioMBean.obj.cancelado}">
				<tr>
					<td colspan="4">
						<table class="visualizacao" style="width: 80%;">
							<tr>
								<td class="subFormulario" colspan="2">Dados do Cancelamento</td>
							</tr>
							<tr>
								<th style="width: 30%;">Solicitado Por:</th>
								<td>${estagioMBean.obj.registroSolicitacaoCancelamento.usuario.nome}</td>
							</tr>
							<tr>
								<th style="width: 30%;">Motivo:</th>
								<td>
									<p>${estagioMBean.obj.motivoCancelamento}</p>
								</td>
							</tr>
							<tr>
								<th style="width: 30%;">Data do Cancelamento:</th>
								<td>
									<ufrn:format type="dataHora" valor="${estagioMBean.obj.dataCancelamento}"/>
								</td>								
							</tr>									
						</table>
					</td>
				</tr>			
			</c:if>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="Confirmar" action="#{estagioMBean.cadastrar}" id="btCadastrar" rendered="#{estagioMBean.cadastro}"/>
						<h:commandButton value="<< Voltar" action="#{estagioMBean.telaForm}" id="btVoltar"/>
						<h:commandButton value="Cancelar" action="#{estagioMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancel" rendered="#{estagioMBean.cadastro}"/>
					</td>
				</tr>
			</tfoot>					
		
		</table>
		<style>
			.semNegrito table th { 
				font-weight: normal !important;
				padding-right: 15px !important;
			}
		</style>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
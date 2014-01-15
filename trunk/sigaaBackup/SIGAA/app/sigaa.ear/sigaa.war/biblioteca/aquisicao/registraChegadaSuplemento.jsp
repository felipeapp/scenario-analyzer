<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Registrar Chegada de um Suplemento do Fascículo </h2>

<div class="descricaoOperacao">
	<p> Página para o registro de um novo suplemento de um fascículo. </p>
</div>


<f:view>


	<a4j:keepAlive beanName="registraChegadaFasciculoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>

	<h:form id="formRegistraChegadaPeriodico">
	
		<table class="formulario" style="width: 80%; margin-bottom: 20px;">
			
			<caption>Dados da Assinatura</caption>
			
			<tbody>
				
				<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada != null}">
					
					
					<tr>
						<th style="font-weight:bold; ">
							Código:
						</th>
						<td colspan="3" style="width: 50%">${registraChegadaFasciculoMBean.assinaturaSelecionada.codigo}</td>
					</tr>			
					<tr>
						<th style="font-weight:bold; ">
							Título:
						</th>
						<td colspan="3" style="width: 50%">${registraChegadaFasciculoMBean.assinaturaSelecionada.titulo}</td>
					</tr>
					<tr>
						<th style="font-weight:bold; ">
							Modalidade de Aquisição:
						</th>
						<td colspan="3" style="width: 50%">
							<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeCompra}">
								COMPRA
							</c:if>
							<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeDoacao}">
								DOAÇÃO
							</c:if>
						</td>
					</tr>
					<tr>
						<th style="font-weight:bold; ">
							Unidade de Destino:
						</th>
						<td colspan="3" style="width: 50%">${registraChegadaFasciculoMBean.assinaturaSelecionada.unidadeDestino.descricao}</td>
					</tr>
					<tr>	
						<th  style="font-weight:bold">
							Último Registro:
						</th>
						<td>
							<h:outputText value="#{registraChegadaFasciculoMBean.assinaturaSelecionada.dataUltimaChegadaFasciculo}">
								<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
							</h:outputText>
						</td>
						
					</tr>
					
					<tr>
						<td colspan="6" style="height: 30px:">
						
						</td>
					</tr>
					
				
					
					
					<%-- os dados do fascículo do suplemento --%>
					<tr>
					
						<td colspan="6">
							<table class="subFormulario" style="width: 100%">
								<caption> Fascículo principal do Suplemento: <h:outputText value="#{registraChegadaFasciculoMBean.fasciculoPrincipal.codigoBarras}" /></caption>
								<tr>
									<th>
										Ano Cronológico:
									</th>
									<td>
										<h:outputText value="#{registraChegadaFasciculoMBean.fasciculoPrincipal.anoCronologico}" />
									</td>
									<th>
										Dia/Mês:
									</th>
									<td>
										<h:outputText value="#{registraChegadaFasciculoMBean.fasciculoPrincipal.diaMes}"  />
										
									</td>
									
									<th>
										Ano:
									</th>
									<td>
										<h:outputText value="#{registraChegadaFasciculoMBean.fasciculoPrincipal.ano}"  />
										
									</td>
									<th>
										Volume:
									</th>
									<td>
										<h:outputText value="#{registraChegadaFasciculoMBean.fasciculoPrincipal.volume}"  />
										
									</td>
									
								
									<th>
										Número:
									</th>
									<td>
										<h:outputText value="#{registraChegadaFasciculoMBean.fasciculoPrincipal.numero}"  />
										
									</td>
									<th>
										Edição:
									</th>
									<td>
										<h:outputText value="#{registraChegadaFasciculoMBean.fasciculoPrincipal.edicao}"  />
										
									</td>
								</tr>
								
								<tr>
									<td colspan="10" style="font-weight: bold;">
										<h:outputText value="Suplementos do Fascículo:"  />
									</td>
								</tr>
								
								<%-- Os dados dos suplemento que o fascículo já possui --%>
								
								<c:forEach items="#{registraChegadaFasciculoMBean.suplementosDoFasciculoPrincipal}" var="suplemento" varStatus="status">
									
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<th>
											Código de Barras:
										</th>
										<td>
											<h:outputText value="#{suplemento.codigoBarras}" />
										</td>
										<th>
											Ano Cronológico:
										</th>
										<td>
											<h:outputText value="#{suplemento.anoCronologico}" />
										</td>
										<th>
											Dia/Mês:
										</th>
										<td>
											<h:outputText value="#{suplemento.diaMes}"  />
										</td>
										<th>
											Ano:
										</th>
										<td>
											<h:outputText value="#{suplemento.ano}"  />
											
										</td>
										<th>
											Volume:
										</th>
										<td>
											<h:outputText value="#{suplemento.volume}"  />
											
										</td>
										
									
										<th>
											Número:
										</th>
										<td>
											<h:outputText value="#{suplemento.numero}"  />
											
										</td>
										<th>
											Edição:
										</th>
										<td>
											<h:outputText value="#{suplemento.edicao}"  />
										</td>
									</tr>
								</c:forEach>
								
								<tr>
								
								</tr>
								
							</table>
						</td>
					
						
					</tr>
					
					
					<%--    Os dados do suplemento que o usuário vai digitar agora  --%>
					<tr>
						<td colspan="6">
							<table class="subFormulario" style="width: 100%">
								<caption> Suplemento do Fascículo</caption>
								<tr>
									<th colspan="2">
										Ano Cronológico:
									</th>
									<td>
										<h:inputText id="inputTextAnoCronologicoSuplemento" value="#{registraChegadaFasciculoMBean.anoCronologicoSuplemento}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de um ano cronológico. Exemplo: 2009-2010</ufrn:help>
									</td>
									
									<th colspan="2">
										Dia/Mês:
									</th>
									<td>
										<h:inputText id="inputTextDiaMesSuplemento" value="#{registraChegadaFasciculoMBean.diaMesSuplemento}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de um dia ou mês. Exemplo: 10-20, jan-mar, 15jan-15fev</ufrn:help>
									</td>
									
									<th colspan="2">
										Ano:
									</th>
									<td>
										<h:inputText id="inputTextAnoSuplemento" value="#{registraChegadaFasciculoMBean.anoSuplemento}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de um ano. Exemplo: 10-20</ufrn:help>
									</td>
									
									
								</tr>
								<tr>
									<th colspan="2">
										Volume:
									</th>
									<td>
										<h:inputText id="inputTextVolumeSuplemento" value="#{registraChegadaFasciculoMBean.numeroVolumeSuplemento}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de um volume. Exemplo: 10-20</ufrn:help>
									</td>
								
									<th colspan="2">
										Número:
									</th>
									<td>
										<h:inputText id="inputTextNumeroSuplemento" value="#{registraChegadaFasciculoMBean.numeroFasciculoSuplemento}" size="10"  maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de um número. Exemplo: 10-20</ufrn:help>
									</td>
									<th colspan="2">
										Edição:
									</th>
									<td>
										<h:inputText id="inputTextEdicaoSuplemento" value="#{registraChegadaFasciculoMBean.edicaoSuplemento}" size="12" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fascículos que englobam mais de uma edição. Exemplo: 10-20</ufrn:help>
									</td>
								</tr>
							</table>
						</td>
						
					</tr> 
							
				</c:if>
					
			
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="6">
						
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO } %>">
							<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada != null}">
								<h:commandButton id="cmdRegistraChegadaSuplemento" value="Registrar Chegada" action="#{registraChegadaFasciculoMBean.registrarSuplementoNoFasciculo}" />
							</c:if>
						</ufrn:checkRole>
						
						<h:commandButton value="<< Voltar" action="#{registraChegadaFasciculoMBean.telaRegistraChegadaFasciculo}" immediate="true" />
						
						<h:commandButton value="Cancelar" action="#{assinaturaPeriodicoMBean.telaBuscaAssinaturas}" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		
		<div style="margin-top:20px"></div>
			
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
			
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
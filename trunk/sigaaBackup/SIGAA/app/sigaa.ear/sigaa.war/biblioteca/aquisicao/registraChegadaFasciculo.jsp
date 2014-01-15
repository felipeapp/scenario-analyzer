<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Registrar Chegada de Fasc�culos </h2>

<div class="descricaoOperacao">
	<p> P�gina para inclus�o de um novo fasc�culo. Esse fasc�culo s� aparecer� nas busca do sistema quando
suas informa��es forem completadas pelo setor de Processos T�cnicos.</p>
</div>



<f:view>


	<a4j:keepAlive beanName="registraChegadaFasciculoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>

	<h:form id="formRegistraChegadaPeriodico">
	
	
		<table class="formulario" style="width: 70%; margin-bottom: 20px;">
			
			<caption>Dados da Assinatura</caption>
			
			<tbody>
				
				<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada != null}">
					
					<tr>
						<th style="font-weight:bold; ">
							C�digo:
						</th>
						<td colspan="3" style="width: 50%">${registraChegadaFasciculoMBean.assinaturaSelecionada.codigo}</td>
					</tr>			
					<tr>
						<th style="font-weight:bold; ">
							T�tulo:
						</th>
						<td colspan="3" style="width: 50%">${registraChegadaFasciculoMBean.assinaturaSelecionada.titulo}</td>
					</tr>		
					<tr>
						<th style="font-weight:bold; ">
							ISSN:
						</th>
						<td colspan="3" style="width: 50%">${registraChegadaFasciculoMBean.assinaturaSelecionada.issn}</td>
					</tr>
					<tr>
						<th style="font-weight:bold; ">
							Modalidade de Aquisi��o:
						</th>
						<td colspan="3" style="width: 50%">
							<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeCompra}">
								COMPRA
							</c:if>
							<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeDoacao}">
								DOA��O
							</c:if>
							<c:if test="${! registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeCompra &&  ! registraChegadaFasciculoMBean.assinaturaSelecionada.assinaturaDeDoacao  }">
								INDEFINIDO
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
							�ltimo Registro:
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
					
				
					
					
					<%-- o que o usu�rio vai preencher agora --%>
					<tr>
					
						<td colspan="6">
							<table class="subFormulario" style="width: 100%">
								<caption> Pr�ximo Registro Sugerido</caption>
								<tr>
									<th colspan="2">
										Ano Cronol�gico:
									</th>
									<td>
										<h:inputText id="inputTextAnoCronologico" value="#{registraChegadaFasciculoMBean.anoCronologico}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um ano cronol�gico. Exemplo: 2009-2010</ufrn:help>
									</td>
									<th colspan="2">
										Dia/M�s:
									</th>
									<td>
										<h:inputText id="inputTextDiaMes" value="#{registraChegadaFasciculoMBean.diaMes}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um dia ou m�s. Exemplos: 10-20, jan-fev, 15jan-15fev</ufrn:help>
									</td>
									
									<th colspan="2">
										Ano:
									</th>
									<td>
										<h:inputText id="inputTextAno" value="#{registraChegadaFasciculoMBean.ano}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um ano. Exemplo: 10-20</ufrn:help>
									</td>
									
								</tr>
								<tr>
									<th colspan="2">
										Volume:
									</th>
									<td>
										<h:inputText id="inputTextVolume" value="#{registraChegadaFasciculoMBean.numeroVolume}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um volume. Exemplo: 10-20</ufrn:help>
									</td>
									<th colspan="2">
										N�mero:
									</th>
									<td>
										<h:inputText id="inputTextNumeroFasciculo" value="#{registraChegadaFasciculoMBean.numeroFasciculo}" size="10"  maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um n�mero. Exemplo: 10-20</ufrn:help>
									</td>
									<th colspan="2">
										Edi��o:
									</th>
									<td>
										<h:inputText id="inputTextEdicao" value="#{registraChegadaFasciculoMBean.edicao}" size="12" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de uma edi��o. Exemplo: 10-20</ufrn:help>
									</td>
								</tr>
							</table>
						</td>
					
						
					</tr>
					
					
					<tr>
						<th>Acompanha Suplemento?</th>
						<td colspan="6">
							<h:selectOneMenu id="comboBoxAcompanhaSuplemento" value="#{registraChegadaFasciculoMBean.possuiSuplemento}"
									valueChangeListener="#{registraChegadaFasciculoMBean.verificaSuplemento}" onchange="submit();">
								<f:selectItem itemLabel="N�O" itemValue="false" />
								<f:selectItem itemLabel="SIM" itemValue="true" />
							</h:selectOneMenu>
							<ufrn:help>
								Escolha <strong>SIM</strong> se o suplemento que acompanha o fasc�culo tiver informa��es
								�teis que justifiquem a cria��o de um novo fasc�culo. Caso contr�rio, pode-se acrescentar
								apenas uma nota no fasc�culo principal no momento da cataloga��o.
							</ufrn:help>
						</td>
					</tr>
					
					<c:if test="${registraChegadaFasciculoMBean.possuiSuplemento}">
						<tr>
							<td colspan="6">
								<table class="subFormulario" style="width: 100%">
									<caption> Suplemento do Fasc�culo</caption>
									<tr>
										<th colspan="2">
											Ano Cronol�gico:
										</th>
										<td>
											<h:inputText id="inputTextAnoCronologicoSuplemento" value="#{registraChegadaFasciculoMBean.anoCronologicoSuplemento}" size="10" maxlength="20" />
											<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um ano cronol�gico. Exemplo: 2009-2010</ufrn:help>
										</td>
										
										<th colspan="2">
											M�s:
										</th>
										<td>
											<h:inputText id="inputTextDiaMesSuplemento" value="#{registraChegadaFasciculoMBean.diaMesSuplemento}" size="10" maxlength="20" />
											<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um dia ou m�s. Exemplos: 10-20, jan-mar, 15jan-15fev</ufrn:help>
										</td>
										
										<th colspan="2">
											Ano:
										</th>
										<td>
											<h:inputText id="inputTextAnoSuplemento" value="#{registraChegadaFasciculoMBean.anoSuplemento}" size="10" maxlength="20" />
											<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um ano. Exemplo: 10-20</ufrn:help>
										</td>
										
										
										
									</tr>
									<tr>
										<th colspan="2">
											Volume:
										</th>
										<td>
											<h:inputText id="inputTextVolumeSuplemento" value="#{registraChegadaFasciculoMBean.numeroVolumeSuplemento}" size="10" maxlength="20" />
											<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um volume. Exemplo: 10-20</ufrn:help>
										</td>
										<th colspan="2">
											N�mero:
										</th>
										<td>
											<h:inputText id="inputTextNumeroSuplemento" value="#{registraChegadaFasciculoMBean.numeroFasciculoSuplemento}" size="10"  maxlength="20" />
											<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um n�mero. Exemplo: 10-20</ufrn:help>
										</td>
										<th colspan="2">
											Edi��o:
										</th>
										<td>
											<h:inputText id="inputTextEdicaoSuplemento" value="#{registraChegadaFasciculoMBean.edicaoSuplemento}" size="12" maxlength="20" />
											<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de uma edi��o. Exemplo: 10-20</ufrn:help>
										</td>
									</tr>
								</table>
							</td>
							
						</tr> 
						
					</c:if>
					
						
				</c:if>
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="6">
						
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL } %>">
							<c:if test="${registraChegadaFasciculoMBean.assinaturaSelecionada != null}">
								<h:commandButton id="cmdButaoRegistrarChegada" value="Registrar Chegada" action="#{registraChegadaFasciculoMBean.registraChegadoProximoFasciculo}" />
							</c:if>
						</ufrn:checkRole>
						
						<h:commandButton value="Cancelar" action="#{assinaturaPeriodicoMBean.telaBuscaAssinaturas}" immediate="true" onclick="#{confirm}" />
						
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		
		
		<div class="infoAltRem" style="margin-top: 10px">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
				Registrar Suplemento do Fasc�culo
		</div>
	
		
		<%-- Os fasc�culos que j� foram registrados  --%>
		<table class="listagem" style="width: 100%">
			
			<caption> Fasc�culos Registrados da Assinatura </caption>
			
			<tr>
				<td>
				
					<%-- Os fasc�culos que j� foram registrados  --%>
					
					<table class="listagem" style="width: 100%">
						<caption>Fasc�culos Registrados para a Assinatura  ( ${registraChegadaFasciculoMBean.qtdFasciculosRegistradosDaAssinatura} )</caption>
			
						<c:if test="${registraChegadaFasciculoMBean.qtdFasciculosRegistradosDaAssinatura > 0 }">
							<thead>
								<tr>
									<th style="text-align: left; width: 13%;">C�digo de Barras</th>
									<th style="text-align: center; width: 8%;">Ano Cron.</th>
									<th style="text-align: center; width: 8%;">Dia/M�s</th>
									<th style="text-align: center; width: 5%;">Ano</th>
									<th style="text-align: center; width: 7%;">N�mero</th>
									<th style="text-align: center; width: 7%;">Volume</th>
									<th style="text-align: center; width: 8%;">Edi��o</th>
									<th style="text-align: left; width: 28%;">Usu�rio que realizou o registro</th>
									<th style="text-align: center; width: 15%;">Data/Hora de Registro</th>
									<th style="text-align: center; width: 1%;"> </th>
								</tr>
							</thead>
						</c:if>
							
						<c:forEach var="fasciculo" items="#{registraChegadaFasciculoMBean.fasciculosRegistradosDaAssinatura}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								
								<td style="text-align: left;">${fasciculo.codigoBarras}</td>
								
								<td style="text-align: center;">${fasciculo.anoCronologico}</td>
								
								<td style="text-align: center;">${fasciculo.diaMes}</td>
								
								<td style="text-align: center;">${fasciculo.ano}</td>
								
								<td style="text-align: center;">${fasciculo.numero}</td>
								
								<td style="text-align: center;">${fasciculo.volume}</td>
								
								<td style="text-align: center;">${fasciculo.edicao}</td>
								
								<td style="text-align: left;">${fasciculo.registroCriacao.usuario.nome}</td>
								
								<td style="text-align: center;">  <ufrn:format type="dataHora" valor="${fasciculo.dataCriacao}"> </ufrn:format> </td>
								
								
								<td>
									<c:if test="${! fasciculo.suplemento}">
										<h:commandLink id="cmdLinkRegistrarChegadaSuplemento" action="#{registraChegadaFasciculoMBean.preparaRegistraSuplemento}">
											<h:graphicImage url="/img/seta.gif" style="border:none" title="Registrar Suplemento do Fasc�culo" />
											<f:param name="idFasciculoRegistrado" value="#{fasciculo.id}"></f:param> 
										</h:commandLink>
									</c:if> 
								</td>	
								
							</tr>
							
						</c:forEach>
						
					</table>
					
				</td>
			</tr>
			
			<tr>
				<td>
					<%-- os fasc�culos  que j� est�o no acervo  --%>
					
					<t:div id="divLinkHabilitaVisualizacao" style="text-align: center;">
						<h:commandLink id="cmdLinkHabilitaVisualizacaoExemplares" value="#{registraChegadaFasciculoMBean.textoLinkHabilitarVisualizacaoFasciculos}"
								actionListener="#{registraChegadaFasciculoMBean.habilitaVisualizacaoFasciculosNoAcervo}" >
						</h:commandLink>
					</t:div>
					
					<t:div id="divFasciculosIncluidosAcervo" rendered="#{registraChegadaFasciculoMBean.mostrarFasciculosNoAcervo}">
					
						<table class="listagem" style="width: 100%">
							<caption>Fasc�culos inclu�dos no acervo para a Assinatura  ( ${registraChegadaFasciculoMBean.qtdFasciculosNoAcervoDaAssinatura} )</caption>
				
							<c:if test="${registraChegadaFasciculoMBean.qtdFasciculosNoAcervoDaAssinatura > 0 }">
								<thead>
									<tr>
										<th style="text-align: left; width: 13%;">C�digo de Barras</th>
										<th style="text-align: center; width: 8%;">Ano Cron.</th>
										<th style="text-align: center; width: 8%;">Dia/M�s</th>
										<th style="text-align: center; width: 5%;">Ano</th>
										<th style="text-align: center; width: 7%;">N�mero</th>
										<th style="text-align: center; width: 7%;">Volume</th>
										<th style="text-align: center; width: 8%;">Edi��o</th>
										<th style="text-align: left; width: 28%;">Usu�rio que realizou o registro</th>
										<th style="text-align: center; width: 15%;">Data/Hora de Registro</th>
										<th style="text-align: center; width: 1%;"> </th>
									</tr>
								</thead>
							</c:if>
							
							<c:forEach var="fasciculo" items="#{registraChegadaFasciculoMBean.fasciculosNoAcervoDaAssinatura}" varStatus="status">
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									
									<td style="text-align: left;">${fasciculo.codigoBarras}</td>
									
									<td style="text-align: center;">${fasciculo.anoCronologico}</td>
									
									<td style="text-align: center;">${fasciculo.diaMes}</td>
									
									<td style="text-align: center;">${fasciculo.ano}</td>
									
									<td style="text-align: center;">${fasciculo.numero}</td>
									
									<td style="text-align: center;">${fasciculo.volume}</td>
									
									<td style="text-align: center;">${fasciculo.edicao}</td>
									
									<td style="text-align: left;">${fasciculo.registroCriacao.usuario.nome}</td>
									
									<td style="text-align: center;">  <ufrn:format type="dataHora" valor="${fasciculo.dataCriacao}"> </ufrn:format> </td>
									
									<td>
										<c:if test="${! fasciculo.suplemento}">
											<h:commandLink id="cmdLinkRegistrarChegadaSuplementoFasciculosAcervo" action="#{registraChegadaFasciculoMBean.preparaRegistraSuplemento}">
												<h:graphicImage url="/img/seta.gif" style="border:none" title="Registrar Suplemento do Fasc�culo" />
												<f:param name="idFasciculoRegistrado" value="#{fasciculo.id}"></f:param> 
											</h:commandLink>
										</c:if> 
									</td>	
									
								</tr>
								
							</c:forEach>
						</table>
					
					</t:div>
					
				</td>
			</tr>
			
			
		</table>
		
		
		<div style="margin-top:20px"></div>
			
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
			
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
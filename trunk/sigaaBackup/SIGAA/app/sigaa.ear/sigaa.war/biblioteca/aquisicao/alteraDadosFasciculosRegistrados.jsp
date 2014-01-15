<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Registrar Chegada de Fasc�culos </h2>

<div class="descricaoOperacao">
	<p> Digite os novos dados do Fasc�culo Registrado. </p>
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
					
					<%-- o que o usu�rio vai preencher agora --%>
					<tr>
					
						<td colspan="6">
							<table class="subFormulario" style="width: 100%">
								<caption> Dados do Fasc�culo Registrado</caption>
								<tr>
									<th colspan="2">
										Ano Cronol�gico:
									</th>
									<td>
										<h:inputText value="#{registraChegadaFasciculoMBean.fasciculoAlteracao.anoCronologico}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um ano cronol�gico. Exemplo: 2009-2010</ufrn:help>
									</td>
									
									<th colspan="2">
										Dia/M�s:
									</th>
									<td>
										<h:inputText value="#{registraChegadaFasciculoMBean.fasciculoAlteracao.diaMes}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um dia ou m�s. Exemplos: 10-20, jan-mar, 15jan-20fev</ufrn:help>
									</td>
									
									<th colspan="2">
										Ano:
									</th>
									<td>
										<h:inputText value="#{registraChegadaFasciculoMBean.fasciculoAlteracao.ano}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um ano. Exemplo: 10-20</ufrn:help>
									</td>
									
									
								</tr>
								<tr>
									<th colspan="2">
										Volume:
									</th>
									<td>
										<h:inputText value="#{registraChegadaFasciculoMBean.fasciculoAlteracao.volume}" size="10" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um volume. Exemplo: 10-20</ufrn:help>
									</td>
								
									<th colspan="2">
										N�mero:
									</th>
									<td>
										<h:inputText value="#{registraChegadaFasciculoMBean.fasciculoAlteracao.numero}" size="10"  maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de um n�mero. Exemplo: 10-20</ufrn:help>
									</td>
									<th colspan="2">
										Edi��o:
									</th>
									<td>
										<h:inputText value="#{registraChegadaFasciculoMBean.fasciculoAlteracao.edicao}" size="12" maxlength="20" />
										<ufrn:help>Esse campo pode conter letras para fasc�culos que englobam mais de uma edi��o. Exemplo: 10-20</ufrn:help>
									</td>
								</tr>
							</table>
						</td>
					
						
					</tr>
					
				</c:if>
				
			</tbody>
			<tfoot>
				<tr style="text-align: center">
					<td colspan="10">
						<h:commandButton value=" Alterar " action="#{registraChegadaFasciculoMBean.alterarFasciculoRegistrado}" />
						<h:commandButton value="Cancelar" action="#{registraChegadaFasciculoMBean.telaListaFasciculosRegistrados}" immediate="true" onclick="#{confirm}"/>
						
					</td>
				</tr>
			</tfoot>
			
		</table>
			
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
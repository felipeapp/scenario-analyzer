<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="comunicarMaterialPerdidoMBean" />

	<h:form id="formulario">
		<h2><ufrn:subSistema /> &gt; Comunicar Material Perdido</h2>

		<div class="descricaoOperacao">
			<p>Caro usu�rio,</p>
			<p>Utiliza essa op��o para comunicar a perda de um material que estava em posse de algum usu�rio. 
			A comunica��o de um material perdido no sistema gera um prazo extra que o usu�rio ganha para repor esse material sem que 
			isso implique em puni��es para ele, desde que ele tenha realizada a comunica��o em tempo h�bio.</p>
			<br/>
			<p>A partir do momento em que a perda � comunicada o empr�stimos passa a n�o poder mais ser devolvido pela opera��o normal de devolu��o.
			O empr�timo s� pode ser devolvido a partir dessa opera��o.
			</p>
			<br/>
			<p>Selecione dentre os materiais emprestados pelo usu�rio aquele que ele perdeu.</p>
		</div>



		<%--  Parte onde o usu�rio visualiza o comprovante da devolu��o   --%>
		<c:if test="${comunicarMaterialPerdidoMBean.habilitaComprovanteDevolucao}">
			
				<table  class="subFormulario" align="center" style="width: 70%;">
					<caption style="text-align: center;">Impress�o Comprovante</caption>
					<tr>
						<td width="8%" valign="middle" align="center">
							<html:img page="/img/warning.gif"/>
						</td>
						<td valign="middle" style="text-align: justify">
							Por favor, para uma maior seguran�a imprima o comprovante da devolu��o clicando no link ao lado.
						</td>
						<td>
							<table>
								<tr>
									<td align="center">
								 		<h:graphicImage url="/img/printer_ok.png" />
								 	</td>
								 </tr>
								 <tr>
								 	<td style="font-size: medium;">
								 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{comunicarMaterialPerdidoMBean.geraComprovanteDevolucao}"  />
								 	</td>
								 </tr>
							</table>
						</td>
					</tr>
				</table>
			<br/>
			
		</c:if>


		<%-- Exibe as informa��es do usu�rio. --%>
		<c:set var="_infoUsuarioCirculacao" value="${comunicarMaterialPerdidoMBean.informacaoUsuario}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>

			
		<div class="infoAltRem" style="margin-top: 10px">
			<h:graphicImage value="/img/comments.png" />: Comunicar Material Perdido
			<h:graphicImage value="/img/del_cal.png" />: Devolver Empr�stimo
			<h:graphicImage value="/img/imprimir.gif" />: Imprimir Comprovante da Comunica��o			 
		</div> 

		<div id="divDadosEmprestimos" style="margin-top:30px">
			
			<table class="listagem" >
				
				<%-- Exibe os empr�stios ativos do usu�rio. --%>
				<c:if test="${not empty comunicarMaterialPerdidoMBean.emprestimosAtivos  }">
					
					<caption>Empr�stimos Ativos (${fn:length(comunicarMaterialPerdidoMBean.emprestimosAtivos)})</caption>
					
					<thead>
						<tr>
							<th>C�digo de Barras</th>
							<th>Descri��o do Material</th>
							<th width="100px" style="text-align:center">Data do Empr�stimo</th>
							<th width="100px" style="text-align:center">Prazo Devolu��o</th>
							<th width="20px"></th>
							<th width="20px"></th>
							<th width="20px"></th>
						</tr>
					</thead>
					
					<tbody>
					
							<c:forEach items="#{comunicarMaterialPerdidoMBean.emprestimosAtivos}" var="e" varStatus="status">
		
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									
									<td>
										${e.material.codigoBarras}
									</td>
									
									<td>
										${e.material.informacao}
									</td>
		
									<td style="text-align:center">
										<ufrn:format type="data" valor="${e.dataEmprestimo}" />
									</td>
									
									<td style="text-align:center">
										<ufrn:format type="dataHora" valor="${e.prazo}" />
									</td>
		
									<td>	
										<h:commandLink action="#{comunicarMaterialPerdidoMBean.preComunicar}">
											<h:graphicImage url="/img/comments.png" style="border:none" title="Comunicar Material Perdido" />
											<f:param name="idEmprestimo" value="#{e.id}"/>
										</h:commandLink>
									</td>
									
									<td>
										<%-- S� mostra o bot�o de devolver se o usu�rio comunicou a perda do material --%>	
										<h:commandLink action="#{comunicarMaterialPerdidoMBean.preDevolverEmprestimo}" rendered="#{fn:length(e.prorrogacoes) > 0}">
											<h:graphicImage url="/img/del_cal.png" style="border:none" title="Devolver Empr�stimo" />
											<f:param name="idEmprestimo" value="#{e.id}"/>
										</h:commandLink>
									</td>
									
									<td>	
										<%-- S� pode imprimir se o empr�timo possuir prorroga��es por perda de materal --%>
										<c:if test="${fn:length(e.prorrogacoes) > 0}" > 
											<h:commandLink action="#{comunicarMaterialPerdidoMBean.reImprimirComprovante}">
												<h:graphicImage url="/img/imprimir.gif" style="border:none" title="Imprimir Comprovante da Comunica��o" />
												<f:param name="idEmprestimo" value="#{e.id}"/>
											</h:commandLink>
										</c:if>
									</td>
								</tr>
								
								<c:if test="${fn:length(e.prorrogacoes) > 0}">
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								
										<td colspan="7">
											<table class="subFormulario" style="width: 90%; ">
												<caption style="background-color: #C8D5EC">Comunica��es de Perdas Realizadas para o Empr�timo</caption>
												<tbody  style="background: transparent;">
													<c:forEach items="#{e.prorrogacoes}" var="p">
														<tr>
															<th style="font-weight: bold; width: 15%;">
															Prazo anterior:
															</th>
															<td style="width: 10%;">
																<ufrn:format type="data" valor="${p.dataAnterior}" /> 
															</td>
															<th style="font-weight: bold; width: 20%;">
															Prazo para reposi��o:
															</th>
															<td style="width: 10%;">
																<ufrn:format type="data" valor="${p.dataAtual}" /> 
															</td>
															<td style="width: 35%" ></td>
														</tr>
														<tr>
															<th style="font-weight: bold; width: 10%">
															Justificativa:
															</th>
															<td colspan="5">
																${p.motivo}
															</td>
														</tr>
														<tr>
															<th style="font-weight: bold; width: 15%;">
															Cadastrado por:
															</th>
															<td colspan="5">
																${p.nomeUsuarioCriou}
															</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</td>
									</tr>
								</c:if>
								
							</c:forEach>
					</c:if>
					
					<c:if test="${empty comunicarMaterialPerdidoMBean.emprestimosAtivos }">
							<caption>Lista de Empr�stimos Ativos (0)</caption>	
							<tr><td colspan="7" style="text-align:center;color:#FF0000;">O usu�rio N�o Possui Empr�stimos Ativos</td></tr>	
					</c:if>
					
				</tbody>

				<tfoot>
					<tr>
						<td colspan="7" style="text-align:center;">
							<h:commandButton value="Cancelar" action="#{comunicarMaterialPerdidoMBean.voltaTelaBusca}" onclick="#{confirm}"  immediate="true"/>
						</td>
					</tr>
				</tfoot>
			</table>
		</div>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
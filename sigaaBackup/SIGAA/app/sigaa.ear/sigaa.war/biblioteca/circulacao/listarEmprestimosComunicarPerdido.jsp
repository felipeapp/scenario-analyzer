<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="comunicarMaterialPerdidoMBean" />

	<h:form id="formulario">
		<h2><ufrn:subSistema /> &gt; Comunicar Material Perdido</h2>

		<div class="descricaoOperacao">
			<p>Caro usuário,</p>
			<p>Utiliza essa opção para comunicar a perda de um material que estava em posse de algum usuário. 
			A comunicação de um material perdido no sistema gera um prazo extra que o usuário ganha para repor esse material sem que 
			isso implique em punições para ele, desde que ele tenha realizada a comunicação em tempo hábio.</p>
			<br/>
			<p>A partir do momento em que a perda é comunicada o empréstimos passa a não poder mais ser devolvido pela operação normal de devolução.
			O emprétimo só pode ser devolvido a partir dessa operação.
			</p>
			<br/>
			<p>Selecione dentre os materiais emprestados pelo usuário aquele que ele perdeu.</p>
		</div>



		<%--  Parte onde o usuário visualiza o comprovante da devolução   --%>
		<c:if test="${comunicarMaterialPerdidoMBean.habilitaComprovanteDevolucao}">
			
				<table  class="subFormulario" align="center" style="width: 70%;">
					<caption style="text-align: center;">Impressão Comprovante</caption>
					<tr>
						<td width="8%" valign="middle" align="center">
							<html:img page="/img/warning.gif"/>
						</td>
						<td valign="middle" style="text-align: justify">
							Por favor, para uma maior segurança imprima o comprovante da devolução clicando no link ao lado.
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


		<%-- Exibe as informações do usuário. --%>
		<c:set var="_infoUsuarioCirculacao" value="${comunicarMaterialPerdidoMBean.informacaoUsuario}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>

			
		<div class="infoAltRem" style="margin-top: 10px">
			<h:graphicImage value="/img/comments.png" />: Comunicar Material Perdido
			<h:graphicImage value="/img/del_cal.png" />: Devolver Empréstimo
			<h:graphicImage value="/img/imprimir.gif" />: Imprimir Comprovante da Comunicação			 
		</div> 

		<div id="divDadosEmprestimos" style="margin-top:30px">
			
			<table class="listagem" >
				
				<%-- Exibe os empréstios ativos do usuário. --%>
				<c:if test="${not empty comunicarMaterialPerdidoMBean.emprestimosAtivos  }">
					
					<caption>Empréstimos Ativos (${fn:length(comunicarMaterialPerdidoMBean.emprestimosAtivos)})</caption>
					
					<thead>
						<tr>
							<th>Código de Barras</th>
							<th>Descrição do Material</th>
							<th width="100px" style="text-align:center">Data do Empréstimo</th>
							<th width="100px" style="text-align:center">Prazo Devolução</th>
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
										<%-- Só mostra o botão de devolver se o usuário comunicou a perda do material --%>	
										<h:commandLink action="#{comunicarMaterialPerdidoMBean.preDevolverEmprestimo}" rendered="#{fn:length(e.prorrogacoes) > 0}">
											<h:graphicImage url="/img/del_cal.png" style="border:none" title="Devolver Empréstimo" />
											<f:param name="idEmprestimo" value="#{e.id}"/>
										</h:commandLink>
									</td>
									
									<td>	
										<%-- Só pode imprimir se o emprétimo possuir prorrogações por perda de materal --%>
										<c:if test="${fn:length(e.prorrogacoes) > 0}" > 
											<h:commandLink action="#{comunicarMaterialPerdidoMBean.reImprimirComprovante}">
												<h:graphicImage url="/img/imprimir.gif" style="border:none" title="Imprimir Comprovante da Comunicação" />
												<f:param name="idEmprestimo" value="#{e.id}"/>
											</h:commandLink>
										</c:if>
									</td>
								</tr>
								
								<c:if test="${fn:length(e.prorrogacoes) > 0}">
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								
										<td colspan="7">
											<table class="subFormulario" style="width: 90%; ">
												<caption style="background-color: #C8D5EC">Comunicações de Perdas Realizadas para o Emprétimo</caption>
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
															Prazo para reposição:
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
							<caption>Lista de Empréstimos Ativos (0)</caption>	
							<tr><td colspan="7" style="text-align:center;color:#FF0000;">O usuário Não Possui Empréstimos Ativos</td></tr>	
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
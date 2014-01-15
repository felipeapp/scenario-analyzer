<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Visualização de discente de monitoria</h2>
	<br>
<a4j:keepAlive beanName="consultarMonitor"/>
<h:form id="form">
	
<table class="visualizacao" width="100%">
		<caption class="listagem"> Dados  Monitor </caption>

<tbody>
	
	<tr>
		<th>
			Projeto:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.projetoEnsino.anoTitulo}"/> 
		</td>
	</tr>	

	<tr>
		<th>
			Discente:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.discente.matriculaNome}"/> 
		</td>
	</tr>

	<tr>
		<th>
			Curso:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.discente.curso.descricao}"/> 
		</td>
	</tr>

	<tr>
		<th>
			Tipo de Vínculo:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.tipoVinculo.descricao}"/>
		</td>
	</tr>	
	
	<tr>
		<th>
			Situação:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.situacaoDiscenteMonitoria.descricao}"/>
		</td>
	</tr>	
	
	<tr>
		<th>
			Classificação:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.classificacao}"/>º
		</td>
	</tr>		

	<tr>
		<th>
			Nota da prova escrita:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.notaProva}"/>
		</td>
	</tr>
	<tr>
		<th>
			Nota Final:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.nota}"/>
		</td>
	</tr>		

	<tr>
		<th>
			Data de Entrada:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.dataInicio}">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</h:outputText>
		</td>
	</tr>						


	<tr>
		<th>
			Data de Saída:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.dataFim}">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</h:outputText>
		</td>
	</tr>						

	<tr>
		<th>
			Observações:
		</th>
		<td>		
			<h:outputText value="#{consultarMonitor.obj.observacao}"/> 
		</td>
	</tr>


	<c:if test="${acesso.monitoria}">
		<tr>
			<td class="subFormulario" colspan="2">&nbsp;&nbsp; Dados Bancários </td>
		</tr>
		<tr>
			<th>
				Banco:
			</th>
			<td>		
				<h:outputText value="#{consultarMonitor.obj.banco.codigoNome}"/> 
			</td>
		</tr>
		<tr>
			<th>
				Agência:
			</th>
			<td>		
				<h:outputText value="#{consultarMonitor.obj.agencia}"/> 
			</td>
		</tr>
		<tr>
			<th>
				Conta Corrente:
			</th>
			<td>		
				<h:outputText value="#{consultarMonitor.obj.conta}"/> 
			</td>
		</tr>	
		<c:if test="${consultarMonitor.obj.operacao != null}">
		<tr>
			<th>
				Operação:
			</th>
			<td>		
				<h:outputText value="#{consultarMonitor.obj.operacao}"/> 
			</td>
		</tr>	
		</c:if>
						
	</c:if>


	
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">		
				<caption class="listagem"> Orientações </caption>
					<tr>
						<td>
					<t:dataTable value="#{consultarMonitor.obj.orientacoes}" var="orientacao" align="center" width="100%" rendered="#{consultarMonitor.mostarDocentes}">
							<t:column headerstyle="text-align: left">
								<f:facet name="header" >
									<f:verbatim>Docente</f:verbatim>
								</f:facet>
								<h:outputText value="#{orientacao.equipeDocente.servidor.siapeNome}" />						
							</t:column>
		
							<t:column>
								<f:facet name="header">
									<f:verbatim><center>Início</center></f:verbatim>
								</f:facet>
								<center><h:outputText value="#{orientacao.dataInicio}">						
										<f:convertDateTime pattern="dd/MM/yyyy"/>
								</h:outputText></center>
							</t:column>
		
							<t:column>
								<f:facet name="header">
									<f:verbatim><center>Fim</center></f:verbatim>
								</f:facet>
								<center><h:outputText value="#{orientacao.dataFim}">						
										<f:convertDateTime pattern="dd/MM/yyyy"/>
								</h:outputText></center>
							</t:column>
		

							
				</t:dataTable>
				
				<h:outputText value="Sem orientadores relacionados" rendered="#{empty consultarMonitor.obj.orientacoes}"/>
				
				<h:outputText value="<font color='red'>[Visualização Não Autorizada]</font>" rendered="#{!consultarMonitor.mostarDocentes}" escape="false"/>
				
				 </td>
				</tr>
				</table>
		</td>
	</tr>
	
	
	<tr>
		<td colspan="4">
			<table class="subFormulario" width="100%">	
				<div class="infoAltRem">
				<!-- Realizar filtragem caso o doscente seja o orientador do monitor! -->
					<c:if test="${(consultarMonitor.obj.projetoEnsino.coordenacao.id eq consultarMonitor.servidorUsuario.id) or (acesso.monitoria)}">
					    <h:graphicImage url="/img/monitoria/form_blue.png"/>: Visualizar Relatório Enviado pelo Discente
					</c:if>	
				</div>	
				<caption class="listagem"> Relatórios enviados pelo Discente </caption>
					
					<thead>
						<tr>
							<th><center>Data/Hora Envio</center></th>			
							<th style="text-align: left;">Tipo Relatório</th>			
							<th></th>
						</tr>
					</thead>

					<tr>
						<td>
											<c:set var="relatoriosMonitores"  value="${consultarMonitor.obj.relatoriosMonitor}"/>
											
											<c:if test="${empty relatoriosMonitores}">
										            <tr> <td colspan="6" align="center"> <font color="red">Não há relatórios enviados por este discente!</font> </td></tr>
											</c:if>
								
											<c:if test="${not empty  relatoriosMonitores}">
												<c:forEach items="#{relatoriosMonitores}" var="relMonitor" varStatus="status">
										               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
												
															<td> <center><fmt:formatDate value="${relMonitor.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss"/></center> </td>					
															<td> ${relMonitor.tipoRelatorio.descricao} </td>			
										
																<td>																		
																	<h:commandLink  title="Visualizar" action="#{relatorioMonitor.view}"	style="border: 0;"
																	rendered="#{(consultarMonitor.obj.projetoEnsino.coordenacao.id eq consultarMonitor.servidorUsuario.id) or (acesso.monitoria)}">
																	       <f:param name="id" value="#{relMonitor.id}"/>
																           <h:graphicImage url="/img/monitoria/form_blue.png" />
																	</h:commandLink>
																</td>
														</tr>			
												</c:forEach>		
											</c:if>
	
					 </td>
				</tr>
				</table>
		</td>
	</tr>
	




	<tr>
		<td colspan="4">
			<table class="subFormulario" width="100%">
				<div class="infoAltRem">
					<c:if test="${(consultarMonitor.obj.projetoEnsino.coordenacao.id eq consultarMonitor.servidorUsuario.id) or (acesso.monitoria)}">
					    <h:graphicImage url="/img/view.gif" />: Visualizar Frequência Enviada pelo Discente
					</c:if>
				</div>		
				<caption class="listagem"> Lista de Frequências enviadas pelo Discente </caption>

					<thead>
						<tr>
							<th><center>Período</center></th>			
							<th><center>Data/Hora Envio</center></th>			
							<th><center>Data/Hora Validação</center></th>
							<th style="text-align: left">Situação</th>
							<th style="text-align: right;">Frequência</th>
							<th></th>
						</tr>
					</thead>

					<tr>
						<td>
											<c:set var="atividades"  value="${consultarMonitor.obj.atividadesMonitor}"/>
											
											<c:if test="${empty atividades}">
										            <tr> <td colspan="6" align="center"> <font color="red">Não há frequências enviadas por este discente!</font> </td></tr>
											</c:if>
								
											<c:if test="${not empty  atividades}">
												<c:forEach items="#{atividades}" var="atv" varStatus="status">
										               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
												
															<td><center><fmt:formatNumber value="${atv.mes}" pattern="00"/>/${atv.ano} </center></td>
															<c:choose>
																<c:when test="${not empty atv.dataCadastro}"><td><center><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${atv.dataCadastro}" /></center>  </td></c:when>
																<c:when test="${ empty atv.dataCadastro}"><td><center> - </center>  </td></c:when>
															</c:choose>
															<c:choose>
																<c:when test="${not empty atv.dataValidacaoOrientador}"><td> <center><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${atv.dataValidacaoOrientador}" /></center> </td></c:when>
																<c:when test="${empty atv.dataValidacaoOrientador}"><td> <center> - </center> </td></c:when>
															</c:choose>
															<td> ${atv.validadoOrientador == true ? 'VALIDADO':(not empty atv.dataValidacaoOrientador ? 'NÃO VALIDADO':' - ')} </td>
															<c:choose>
																<c:when test="${atv.validadoOrientador}"><td style=" text-align: right;" > ${atv.frequencia}% </td></c:when>
																<c:when test="${!atv.validadoOrientador}"><td style=" text-align: right;" > - </td></c:when>
																
															</c:choose>
															<td style=" text-align: right;">
																<h:commandLink  title="Visualizar" action="#{atividadeMonitor.visualizarRelatorioMonitor}"	style="border: 0;"
																	rendered="#{(consultarMonitor.obj.projetoEnsino.coordenacao.id eq consultarMonitor.servidorUsuario.id) or (acesso.monitoria)}">
																       <f:param name="id" value="#{atv.id}"/>
														               <h:graphicImage url="/img/view.gif" />
																</h:commandLink>
															</td>
														</tr>			
												</c:forEach>		
											</c:if>
	
					 </td>
				</tr>
				</table>
		</td>
	</tr>

	
	</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<center><input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" /></center>
		</td>
	</tr>
</tfoot>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
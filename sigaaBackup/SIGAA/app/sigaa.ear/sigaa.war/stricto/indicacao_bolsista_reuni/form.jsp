<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Indicação de Bolsista REUNI</h2>

	<h:form id="form">
		<table class="visualizacao" style="width: 80%">
			<caption>Dados do Plano de Trabalho</caption>
			<tr>
				<th> Edital: </th>
				<td>
					<c:choose>
						<c:when test="${not empty indicacaoBolsistaReuni.obj.planoTrabalho.solicitacao.edital.idArquivoEdital}">
							<a href="${ctx}/verArquivo?idArquivo=${indicacaoBolsistaReuni.obj.planoTrabalho.solicitacao.edital.idArquivoEdital}&key=${ sf:generateArquivoKey(indicacaoBolsistaReuni.obj.planoTrabalho.solicitacao.edital.idArquivoEdital) }" target="_blank">
								${ indicacaoBolsistaReuni.obj.planoTrabalho }
							</a> 
						</c:when>
						<c:otherwise>
							${ indicacaoBolsistaReuni.obj.planoTrabalho.solicitacao.edital }
						</c:otherwise>
					</c:choose>													
				</td>
			</tr>	
			<tr>
				<th> Programa de Pós-Graduação: </th>
				<td> <h:outputText value="#{ indicacaoBolsistaReuni.obj.planoTrabalho.solicitacao.programa }"/> </td>
			</tr>
			<c:if test="${indicacaoBolsistaReuni.obj.planoTrabalho.linhaAcao == 1}">
				<tr>
					<th>Componente Curricular:</th>
					<td><h:outputText value="#{indicacaoBolsistaReuni.obj.planoTrabalho.componenteCurricular.codigoNome}" /></td>
				</tr>			
			</c:if>
			<c:if test="${indicacaoBolsistaReuni.obj.planoTrabalho.linhaAcao == 2}">
				<tr>
					<th>Área de Atuação:</th>
					<td> <h:outputText value="#{indicacaoBolsistaReuni.obj.planoTrabalho.areaConhecimento.denominacao}" /> </td>
				</tr>			
			</c:if>
			<tr>
				<th>Discente:</th>
				<td>${indicacaoBolsistaReuni.discente.matricula} - ${indicacaoBolsistaReuni.discente.pessoa.nome}</td>
			</tr>			
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%" id="periodo">
						<caption>Período da Bolsa</caption>
						<tr>
							<th width="50%">Ano/Período:</th>
							<td>
								<h:selectOneMenu id="comboanoPeriodo" value="#{ indicacaoBolsistaReuni.anoPeriodo }" >
									<f:selectItems value="#{ indicacaoBolsistaReuni.periodos}" id="itensPeriodos"/>
								</h:selectOneMenu>							
							</td>
						</tr>
						<tfoot>
							<tr>
								<td colspan="2"><h:commandButton id="add" value="Adicionar Período"
									actionListener="#{indicacaoBolsistaReuni.adicionarPeriodo}" />
								</td>
							</tr>
						</tfoot>						
					</table>
				</td>
			</tr>		
			<tr>
				<td colspan="2">
					<c:if test="${not empty indicacaoBolsistaReuni.obj.periodosIndicacao}">
						<div class="infoAltRem">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
							Remover Período
						</div>
						
						<style>
						   .ano { width: 30px; text-align: right; }
						   .acao { width: 20px; text-align: right; }
						</style>
						
												
						<t:dataTable var="periodo" style="width:50%" styleClass="subFormulario" columnClasses="ano,acao" rowClasses="linhaPar,linhaImpar" value="#{ indicacaoBolsistaReuni.obj.periodosIndicacao }" rowIndexVar="row" id="periodos" width="100%">						
							<f:facet name="caption"><h:outputText value="Períodos da Bolsa do Discente"/></f:facet>
							<center>
								<t:column>
									<h:outputText value="#{periodo.anoPeriodoFormatado}"/>
								</t:column>
								<t:column>
									<h:commandLink action="#{indicacaoBolsistaReuni.removerPeriodo}" onclick="#{confirmDelete}" title="Remover Período" id="remover">
										<h:graphicImage url="/img/delete.gif" />
										<f:param name="indice" value="#{row}"/>
									</h:commandLink>	
								</t:column>
							</center>
						</t:dataTable>
					</c:if>				
				</td>
			</tr>					
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Selecionar outro Discente" action="#{indicacaoBolsistaReuni.informaDiscente}" id="outroDisc" /> 
						<h:commandButton value="#{indicacaoBolsistaReuni.confirmButton}" action="#{indicacaoBolsistaReuni.cadastrar}" id="cadastrar"/>
						<h:commandButton value="<< Voltar" action="#{indicacaoBolsistaReuni.exibirPlanos}" id="voltar"/>  
						<h:commandButton value="Cancelar" action="#{indicacaoBolsistaReuni.cancelar}" immediate="true" onclick="#{confirm}" id="cancelar"/>
					</td> 
				</tr>
			</tfoot>			
		</table>
	</h:form>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

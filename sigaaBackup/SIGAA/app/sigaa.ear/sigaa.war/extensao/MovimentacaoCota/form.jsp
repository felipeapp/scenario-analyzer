<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>
<f:view>

<a4j:keepAlive beanName="movimentacaoCotasExtensao"/>

<h2><ufrn:subSistema /> > Movimentação de Cotas de Bolsas Entre Ações de Extensão</h2>
<h:form id="form">

	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Detalhes da Ação de Extensão
	</div>
	<br/>

	<table class="formulario" width="100%">
		<caption>Movimentar Cotas de Bolsa</caption>
	
		<tbody>
		
			<tr>
				<td>
			
						<table class="subFormulario" width="100%">
								<caption>Movimentação</caption>		
						
								<tr>
									<th width="25%" class="required"> Tipo: </th>
									<td> 
										<h:selectOneRadio id="tipo_movimentacao" value="#{movimentacaoCotasExtensao.obj.tipoMovimentacao}" >
											<f:selectItem itemValue="I" itemLabel="INCLUIR" />
											<f:selectItem itemValue="R" itemLabel="REMOVER" />
										</h:selectOneRadio>
									 </td>
								</tr>
								
								<tr>
									<th width="25%" class="required"> Tipo Cota: </th>
									<td> 
										<h:selectOneMenu id="comboTipoVinculo" value="#{movimentacaoCotasExtensao.obj.tipoCota.id}">
											<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
											<f:selectItems value="#{tipoVinculoDiscenteBean.remuneradosAtivosExtensaoCombo}" />
										</h:selectOneMenu>
									</td>
								</tr>				
						
								<tr>
									<th width="25%" class="required">Nº Bolsas: </th>
									<td> <h:inputText id="numeroBolsas"  value="#{movimentacaoCotasExtensao.obj.qtdCotas}" size="3" onkeyup="formatarInteiro(this)" maxlength="4"/>
										<ufrn:help img="/img/ajuda.gif">Entre com a quantidade de bolsas que serão movimentadas.</ufrn:help> 
									</td>
								</tr>				
				
								<tr>
									<th class="required"> Observações: </th>
									<td> 
									   <h:inputText id="observacoes" value="#{movimentacaoCotasExtensao.obj.observacao }"  size="60"/> 
				   					   <ufrn:help img="/img/ajuda.gif">Motivo da movimentação das cotas de bolsa.</ufrn:help> 
									</td>
								</tr>		
						</table>

				</td>
			</tr>
			
			<tr>
				<td>
						<table class="subFormulario" width="100%">
									<caption>Dados da Ação de Extensão</caption>
		
									<tr>
										<th width="25%">Título:</th>
										<td>
											<b><h:outputText value="#{movimentacaoCotasExtensao.obj.acaoExtensao.anoTitulo}"/></b>
											<input type="hidden" value="${movimentacaoCotasExtensao.obj.acaoExtensao.id}" name="id" id="id"/>
											<h:commandButton image="/img/view.gif" action="#{atividadeExtensao.view}" alt="Visualizar Detalhes da Ação de Extensão" title="Visualizar Detalhes da Ação de Extensão"/>
										</td>
									</tr>
									
									<tr>
										<th>Coordenação:</th>
										<td>
											<b><h:outputText value="#{movimentacaoCotasExtensao.obj.acaoExtensao.coordenacao.pessoa.nome}"/></b>
										</td>
									</tr>
									
									<tr>
										<th>Tipo da Ação:</th>
										<td>
											<b><h:outputText value="#{movimentacaoCotasExtensao.obj.acaoExtensao.tipoAtividadeExtensao.descricao}"/></b>
										</td>
									</tr>

									<tr>
										<th>Nº Bolsas Solicitadas:</th>
										<td>
										 	<b><h:outputText value="#{movimentacaoCotasExtensao.obj.acaoExtensao.bolsasSolicitadas}"/></b>
										 </td>
									</tr>

									<tr>
										<th>Nº Bolsas Concedidas:</th>
										<td>
										 	<b><h:outputText value="#{movimentacaoCotasExtensao.obj.acaoExtensao.bolsasConcedidas}"/></b>
										 </td>
									</tr>
									
									<tr>
										<th>Nº de discente na Ação</th>
										<td>
											<b><h:outputText value="#{movimentacaoCotasExtensao.totalDiscentesAtividade}" /> </b>
										</td>
									</tr>
									
									<tr>
										<th>Nº de discentes voluntários na Ação</th>
										<td>
											<b><h:outputText value="#{movimentacaoCotasExtensao.totalVoluntariosAtividade}" /> </b>
										</td>
									</tr>
									
									<tr>
										<th>Nº de discentes Bolsistas na Ação</th>
										<td>
											<b><h:outputText value="#{movimentacaoCotasExtensao.totalBolsistasAtividade}" /> </b>
										</td>
									</tr>
									
									<tr>		
										<th>Unidade Proponente: </th>
										<td>
											<b><h:outputText value="#{movimentacaoCotasExtensao.obj.acaoExtensao.unidade}"/></b>
										</td>
									</tr>
								
									<tr>
										<th>Situação: </th>
										<td>
											<b><h:outputText value="#{movimentacaoCotasExtensao.obj.acaoExtensao.situacaoProjeto.descricao}"/></b>
										</td>
									</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" >
					<hr/>
						<p style="text-align: center;"><b> Discentes Envolvidos no Projeto </b></p>
					<hr/>
				</td>
			</tr>
			
			<c:set var="discentes" value="#{movimentacaoCotasExtensao.obj.acaoExtensao.discentesSelecionados}"></c:set>
			
			<tr>
				<td colspan="2">
					<c:if test="${empty discentes}">
						<p style="text-align: center;"><font color="red">Não há discentes nesta Ação</font></p>
					</c:if>
					<c:if test="${not empty discentes}">
						<h:dataTable value="#{movimentacaoCotasExtensao.obj.acaoExtensao.discentesSelecionados}" var="dis" width="100%">
							<h:column>
								<f:facet name="header">
									<f:verbatim>Matrícula</f:verbatim>
								</f:facet>
								<h:outputText value="#{dis.discente.matricula}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<f:verbatim>Nome</f:verbatim>
								</f:facet>
								<h:outputText value="#{dis.discente.pessoa.nome}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<f:verbatim>Início</f:verbatim>
								</f:facet>
								<h:outputText value="#{dis.dataInicio}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<f:verbatim>Fim</f:verbatim>
								</f:facet>
								<h:outputText value="#{dis.dataFim}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<f:verbatim>Vínculo</f:verbatim>
								</f:facet>
								<h:outputText value="#{dis.tipoVinculo.descricao}" />
							</h:column>
						</h:dataTable>
					</c:if>
				</td>
			</tr>
			
			
		</tbody>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton action="#{movimentacaoCotasExtensao.cadastrar}" value="Movimentar Cotas" id="btnCadastrar"/>
				<h:commandButton action="#{movimentacaoCotasExtensao.cancelar}" value="Cancelar" id="btnCancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
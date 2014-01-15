<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"/>
<h:form id="form">
	<h2><ufrn:subSistema /> > Relatório Quantitativo de Projetos de Monitoria</h2>

	<table width="70%" class="listagem">
						<caption> Informativo Sintético </caption>
						<tr>
							<td colspan="3" align="center">
									Edital: <h:selectOneMenu value="#{quantMonitoria.idEdital}"
										valueChangeListener="#{quantMonitoria.refreshDados}" onchange="submit()" style="width:400px" >
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
										<f:selectItems value="#{editalMonitoria.allCombo}"/>
									</h:selectOneMenu>
							</td>
						</tr>
						
						
						<tr >
							<td colspan="3" class="subFormulario">
							Projetos
							</td>
						</tr>
						
						
						<tr class="linhaImpar">
							<td> Projetos Cadastrados </td>
							<td width="10%"> <h:outputText value="#{quantMonitoria.cadastrados}"/> </td>
						</tr>


						<tr class="linhaPar">
							<td> Projetos Em Andamento </td>
							<td> <h:outputText value="#{quantMonitoria.emAndamento}"/> </td>
						</tr>




						<tr class="linhaImpar">
							<td> Propostas de projetos cadastradas (projetos em aberto e não enviados aos departamentos)</td>
							<td> <h:outputText value="#{quantMonitoria.abertos}"/> </td>
						</tr>



						
						<tr class="linhaPar">
							<td> Aguardando Autorização Chefe Departamento </td>
							<td> <h:outputText value="#{quantMonitoria.aguardandoAutDepartamentos}"/> </td>
						</tr>



						<tr class="linhaImpar">
							<td> Autorizados pelos departamentos e Aguardando Distribuição para Comissão de Monitoria </td>
							<td> <h:outputText value="#{quantMonitoria.aguardandoDistribuicao}"/> </td>
						</tr>

						<tr class="linhaPar">
							<td> Projetos NÃO Autorizados pelos Chefe de Departamentos </td>
							<td> <h:outputText value="#{quantMonitoria.naoAutorizadosDepartamentos}"/> </td>
						</tr>



						
						<tr class="linhaImpar">
							<td> Projetos distribuídos para Comissão de Monitoria </td>
							<td> <h:outputText value="#{quantMonitoria.aguardandoAvaliacaoPrograd}"/> </td>
						</tr>
						
						
						
						<tr class="linhaPar">
							<td> Avaliados pela comissão de monitoria com Discrepância de Notas </td>
							<td> <h:outputText value="#{quantMonitoria.discrepantes}"/> </td>
						</tr>
						



						<tr class="linhaImpar">
							<td> Recomendados pela comissão de monitoria</td>
							<td> <h:outputText value="#{quantMonitoria.recomendados}"/> </td>
						</tr>



						<tr class="linhaPar">
							<td> NÂO Recomendados pela comissão de monitoria</td>
							<td> <h:outputText value="#{quantMonitoria.naoRecomendados}"/> </td>
						</tr>


						
						
						
						<tr >
							<td colspan="3" class="subFormulario">
							Monitores
							</td>
						</tr>
						
						
						<tr class="linhaPar">
							<td> Total de Monitores Ativos </td>
							<td> <h:outputText value="#{quantMonitoria.monitoresAtivos}"/> </td>
						</tr>


						<tr class="linhaImpar">
							<td> Bolsistas Ativos </td>
							<td> <h:outputText value="#{quantMonitoria.bolsistasAtivos}"/> </td>
						</tr>				



						<tr class="linhaPar">
							<td> Não Remunerados Ativos </td>
							<td> <h:outputText value="#{quantMonitoria.naoRemunerados}"/> </td>
						</tr>		
						
						
						
						<tr >
							<td colspan="3" class="subFormulario">
							Atividades dos Monitores referentes ao período: <b> <h:outputText value="#{quantMonitoria.envioFrequencia.mes}"/> / <h:outputText value="#{quantMonitoria.envioFrequencia.ano}"/> </b>
							</td>
						</tr>
								
						
						<tr class="linhaPar">
							<td> Atividades Enviadas pelos Monitores
							<td> <h:outputText value="#{quantMonitoria.atividadesEnviadas}"/> </td>
						</tr>
						<tr class="linhaImpar">
							<td> Atividades Validadas pelos Orientadores </td>
							<td> <h:outputText value="#{quantMonitoria.atividadesValidadasOrientador}"/> </td>							
						</tr>
					</table>
		</h:form>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	
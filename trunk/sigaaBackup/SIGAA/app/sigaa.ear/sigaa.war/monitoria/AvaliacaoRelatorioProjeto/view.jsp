<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<h:form id="form">
	
	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{relatorioMonitor.create}"/>
	<h:outputText  value="#{relatorioProjetoMonitoria.create}"/>
	<h:outputText  value="#{avalRelatorioProjetoMonitoria.create}"/>

	<h2>Avaliar Relat�rio de Projeto de Ensino</h2>

	<h:messages showDetail="true" showSummary="true"/>

	<table class="tabelaRelatorio" width="100%">
	<caption>Avalia��o do Relat�rio de Projeto de Ensino</caption>
	
	<tr>
		<td>
			<table width="100%" class="subFormulario">
				<caption>1 - IDENTIFICA��O:</caption>
					<tr>
						<th width="25%"><b>Tipo de Relat�rio:</b></th>
						<td><h:outputText value="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.tipoRelatorio.descricao}"/></td>
					</tr>
				
					<tr>
						<th><b>Situa��o do Relat�rio:</b></th>
						<td><h:outputText value="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.status.descricao}"/></td>
					</tr>

					<tr>
						<th><b>Projeto de Ensino:</b></th>
						<td><h:outputText value="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.projetoEnsino.anoTitulo}"/></td>
					</tr>

					<tr>
						<th><b>Situa��o do Projeto:</b></th>
						<td><h:outputText value="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.projetoEnsino.situacaoProjeto.descricao}"/></td>
					</tr>
					
					<tr>
				    	<td colspan="2">
				    		<table width="100%">
				    			<thead>
				    				<tr><th colspan="3">Monitores do projeto</th></tr>
								</thead>
						    	<c:forEach items="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.projetoEnsino.discentesConsolidados}" var="dm">
									<tr>
										<td><h:outputText id="nom" value="<font color='#{(!dm.ativo)?'gray':'black'}'>#{dm.discente.matriculaNome}</font>" escape="false"/></td>
										<td><h:outputText id="tip" value="<font color='#{(!dm.ativo)?'gray':'black'}'>#{dm.tipoVinculo.descricao} </font>" escape="false"/></td>
										<td><h:outputText id="sit" value="<font color='#{(!dm.ativo)?'gray':'black'}'>#{dm.situacaoDiscenteMonitoria.descricao} </font>" escape="false"/></td>
									</tr>
						    	</c:forEach>
						    </table>
					    </td>
					</tr> 
			</table>
		</td>
	</tr>


	<tr>
		<td>
			<table width="100%" class="subFormulario">
				<caption>2 - COM BASE NO PROJETO DE MONITORIA E NO RELAT�RIO DO MONITOR RESPONDA:</caption>
				
					<tr>
						<td width="85%">a) O monitor demonstra conhecer o projeto ao qual est� vinculado?</td>

						<td>
							<b>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.monitorConheceProjeto=='S' ? 'SIM' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.monitorConheceProjeto=='N' ? 'N�O' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.monitorConheceProjeto=='P' ? 'PARCIALMENTE' :''}"/>
							</b>
						</td>
					</tr>					
					
					
					<tr>
						<td width="85%">b) H� coer�ncia entre os objetivos propostas no projeto e as atividades desenvolvidas pelo(s) monitor(es)?</td>
						<td>
							<b>						
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.coerenciaObjetivosAtividades=='S' ? 'SIM' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.coerenciaObjetivosAtividades=='N' ? 'N�O' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.coerenciaObjetivosAtividades=='P' ? 'PARCIALMENTE' :''}"/>
							</b>
						</td>
					</tr>					


					<tr>
						<td width="85%">c) Houve envolvimento do(s) monitor(es) no Semin�rio de Inicia��o � Doc�ncia ( SID ), ou aus�ncia ?</td>
						<td>
							<b>						
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.monitorEnvolvidoSid=='S' ? 'SIM' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.monitorEnvolvidoSid=='N' ? 'N�O' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.monitorEnvolvidoSid=='P' ? 'PARCIALMENTE' :''}"/>
							</b>
						</td>
					</tr>

					<tr>
						<td width="85%">d) O projeto de monitoria propiciou a participa��o efetiva do monitor, contribuindo para a sua forma��o acad�mica?</td>
						<td>
							<b>						
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.projetoContribuiuFormacaoAcademicaMonitor=='S' ? 'SIM' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.projetoContribuiuFormacaoAcademicaMonitor=='N' ? 'N�O' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.projetoContribuiuFormacaoAcademicaMonitor=='P' ? 'PARCIALMENTE' :''}"/>
							</b>
						</td>
					</tr>
			</table>
			
			
			
			<table width="100%" class="subFormulario">
				<caption>3- COM BASE NO PROJETO DE MONITORIA E NO RELAT�RIO DO COORDENADOR E ORIENTADOR(ES) RESPONDA:</caption>
					<tr>
						<td width="85%">a) As estrat�gias tra�adas pelo projeto possibilitam o alcance dos objetivos?</td>

						<td>
							<b>						
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.estrategiasPossibilitamAlcanceObjetivos=='S' ? 'SIM' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.estrategiasPossibilitamAlcanceObjetivos=='N' ? 'N�O' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.estrategiasPossibilitamAlcanceObjetivos=='P' ? 'PARCIALMENTE' :''}"/>
							</b>
						</td>
					</tr>
					
					
					
					<tr>
						<td width="85%">b) As atividades dos monitores est�o de acordo com o que prop�e o programa de monitoria?</td>
						<td>
							<b>						
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.atividadesMonitoresProgramaMonitoria=='S' ? 'SIM' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.atividadesMonitoresProgramaMonitoria=='N' ? 'N�O' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.atividadesMonitoresProgramaMonitoria=='P' ? 'PARCIALMENTE' :''}"/>
							</b>
						</td>
					</tr>
					


					<tr>
						<td width="85%">c) A participa��o dos membros do projeto no SID foi satisfat�ria?</td>
						<td>
							<b>						
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.participacaoMembrosSidSatisfatoria=='S' ? 'SIM' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.participacaoMembrosSidSatisfatoria=='N' ? 'N�O' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.participacaoMembrosSidSatisfatoria=='P' ? 'PARCIALMENTE' :''}"/>
							</b>
						</td>
					</tr>

					<tr>
						<td width="85%">d) As perspectivas propostas para o aprimoramento do projeto justificam a sua renova��o?</td>
						<td><b>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.propostasJustificamRenovacao=='S' ? 'SIM' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.propostasJustificamRenovacao=='N' ? 'N�O' :''}"/>
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.propostasJustificamRenovacao=='P' ? 'PARCIALMENTE' :''}"/>
							</b>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" align="justify">
							e) Parecer:<br/>
							<b><h:outputText value="#{avalRelatorioProjetoMonitoria.obj.parecer}"/></b>
						</td>
					</tr>
					
					
					<tr>
						<td>f) Recomenda a renova��o do projeto?</td>
						<td><b><h:outputText value="#{avalRelatorioProjetoMonitoria.obj.recomendaRenovacao ? 'SIM' : 'N�O'}" /></b></td>
					</tr>
					

					<tr>
						<td>g) Manter quantidade de Bolsas do projeto?</td>
						<td><b><h:outputText value="#{avalRelatorioProjetoMonitoria.obj.mantemQuantidadeBolsas ? 'SIM' : 'N�O'}" /></b></td>
					</tr>
					
					
					
					<tr>
						<td colspan="2">
				
							<table class="listagem" width="100%">	
							<caption>Lista de discentes que tiveram o corte da bolsa recomendado:</caption>
							
								<tr>
									<td>
											<t:dataTable value="#{avalRelatorioProjetoMonitoria.obj.recomendacoesCorteBolsa}" var="corteBolsa" rowClasses="linhaPar,linhaImpar" width="100%" id="corte">
												
												<t:column>
														<h:outputText value="<b>#{corteBolsa.discenteMonitoria.discente.matriculaNome} - <i>#{corteBolsa.discenteMonitoria.tipoVinculo.descricao}</i></b>"  escape="false"/>
	
														<h:outputText value="<div>"  escape="false"/>
															<f:verbatim><label><i>Justifitiva do corte da bolsa:</i></label><br/></f:verbatim>													
															<h:outputText value="#{corteBolsa.justificativa}"/>																	
														<f:verbatim></div></f:verbatim>
												</t:column>
												
											</t:dataTable>
									</td>
								</tr>
								
							    <h:outputText escape="false" value="<tr><td><center><font color=red>Sem recomenda��es para cortes de bolsas neste projeto.</font></center></td></tr>" rendered="#{empty avalRelatorioProjetoMonitoria.obj.recomendacoesCorteBolsa}"/>
				
							</table>		
				 		   </td>
					</tr>
			</table>
		</td>
	</tr>

		

	</table>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
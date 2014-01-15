<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<script type="text/javascript">
							function renovarProjeto(obj){
							
								mqb = $('manter_quantidade_bolsas');

								ld = $('lista_discentes');
								ld.hide();
								
								if(obj.value == 'true'){
									mqb.show();
								}else{
									mqb.hide();										
								}
								
							}

							function manterBolsas(obj){
							
								ld = $('lista_discentes');
								
								if(obj.value == 'false'){
									ld.show();
								}else{
									ld.hide();								
								}
								
							}
							
							
							function cortarBolsa(obj, i) {
							
								var just = $("txaJustificarCorte"+ i) ;
							
								if (obj.checked == false){
									just.style.display = "none";
								}else{
									just.style.display = "";
								}
								
							}
							
	</script>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:form id="form">
	
	<%-- h:outputText  value="#{avalRelatorioProjetoMonitoria.inicializaRelatoriosMonitores}"/ --%>

	<h2><ufrn:subSistema /> > Avaliar Relatório de Projeto de Ensino</h2>

	<h:messages showDetail="true" showSummary="true"/>


	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto
   	    <h:graphicImage value="/img/monitoria/form_green.png" style="overflow: visible;"/>: Visualizar Relatório do Projeto
   	    <br/>
   	    <h:graphicImage value="/img/monitoria/form_blue.png" style="overflow: visible;"/>: Visualizar Relatório Monitor
   	    <h:graphicImage value="/img/monitoria/form_blue_forbidden.png" style="overflow: visible;" />: Relatório não enviado
   	    <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;" />: Visualizar Monitor
   	    <br/>
	</div>
	
	<table class="formulario" width="100%">
	<caption>Avaliar Relatório de Projeto de Ensino</caption>
	
	<tr>
		<td>
			<table width="100%" class="subFormulario">
				<caption>1 - IDENTIFICAÇÃO:</caption>
				<thead>
					<tr>
						<td colspan="7">Tipo de Relatório</td>
					</tr>
				</thead>
					<tr>
						<td colspan="6">
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.tipoRelatorio.descricao}"/>			
						</td>
						
						<td>					
								<h:commandLink  title="Visualizar Relatório" 
										action="#{relatorioProjetoMonitoria.view}" style="border: 0;" 
										rendered="#{not empty avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria}">
										
								      <f:param name="id" value="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.id}"/>
								      <h:graphicImage url="/img/monitoria/form_green.png" />								      
								</h:commandLink>
								
								<h:graphicImage url="/img/monitoria/form_green_forbidden.png" title="Relatório ainda não foi enviado" 
										rendered="#{empty avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria}"/>
						</td>
					</tr>		
				
				
				<thead>
					<tr>
						<td colspan="7">Projeto de Ensino</td>
					</tr>
				</thead>
					<tr>
						<td colspan="6">						
							<h:outputText value="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.projetoEnsino.anoTitulo}"/>
						</td>
						<td>						
								<h:commandLink  title="Visualizar Projeto" action="#{projetoMonitoria.view}" style="border: 0;">
								      <f:param name="id" value="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.projetoEnsino.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
						</td>
					</tr>					
				<thead>
					<tr>
						<td colspan="3">Monitores do projeto</td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
				</thead>
				    <c:forEach items="#{avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.projetoEnsino.discentesConsolidados}" var="dm">
						<tr>
							<td><h:outputText id="nom" value="<font color='#{(!dm.ativo)?'gray':'black'}'>#{dm.discente.matriculaNome}</font>" escape="false"/></td>
							<td><h:outputText id="tip" value="<font color='#{(!dm.ativo)?'gray':'black'}'>#{dm.tipoVinculo.descricao} </font>" escape="false"/></td>
							<td><h:outputText id="sit" value="<font color='#{(!dm.ativo)?'gray':'black'}'>#{dm.situacaoDiscenteMonitoria.descricao} </font>" escape="false"/></td>
							<td>
							    <c:forEach items="#{dm.relatoriosParciais}" var="relp">
											<h:commandLink  title="Ver Relatório Parcial" action="#{relatorioMonitor.view}" 
												style="border: 0;" id="btvrp" rendered="#{ relp.enviado }">
											      <f:param name="id" value="#{relp.id}"/>
											      <h:graphicImage url="/img/monitoria/form_blue.png" />
											</h:commandLink>
								</c:forEach>
								<h:graphicImage url="/img/monitoria/form_blue_forbidden.png" title="Relatório parcial não enviado"
								rendered="#{(!dm.enviouRelatoriosParciais)}"/>
							</td>
							
							<td>
								<c:forEach items="#{dm.relatoriosFinais}" var="relf">
											<h:commandLink  title="Ver Relatório Final" action="#{relatorioMonitor.view}" 
												style="border: 0;" id="btvrf" rendered="#{ relf.enviado }">
											      <f:param name="id" value="#{relf.id}"/>
											      <h:graphicImage url="/img/monitoria/form_blue.png" />
											</h:commandLink>
								</c:forEach>
								<h:graphicImage url="/img/monitoria/form_blue_forbidden.png" title="Relatório final não enviado"
								rendered="#{(!dm.enviouRelatoriosFinais)}"/>		
							</td>
							
							<td>
								<c:forEach items="#{dm.relatoriosDesligamento}" var="reld">
											<h:commandLink  title="Ver Relatório de Desligamento" action="#{relatorioMonitor.view}" 
												style="border: 0;" id="btvrd" rendered="#{ reld.enviado }">
											      <f:param name="id" value="#{reld.id}"/>
											      <h:graphicImage url="/img/monitoria/form_blue.png" />
											</h:commandLink>
								</c:forEach>
								<h:graphicImage url="/img/monitoria/form_blue_forbidden.png" title="Relatório de desligamento não enviado"
								rendered="#{(!dm.enviouRelatoriosDesligamento)}"/>
							</td>
								
							<td>
								<h:commandLink title="Visualizar Monitor" action="#{ consultarMonitor.view }">
								      <f:param name="id" value="#{dm.id}"/>
								      <h:graphicImage url="/img/monitoria/user1_view.png" />
								</h:commandLink>
							</td>
							
						</tr>
				    </c:forEach>
				    
				    <h:outputText escape="false" value="<tr><td><center><font color=red>Atualmente não há monitores ativos neste projeto.</font></center></td></tr>" rendered="#{empty avalRelatorioProjetoMonitoria.obj.relatorioProjetoMonitoria.projetoEnsino.discentesMonitoria}"/>
			</table>
		</td>
	</tr>


	<tr>
		<td>
			<table width="100%" class="subFormulario">
				<caption>2 - COM BASE NO PROJETO DE MONITORIA E NO RELATÓRIO DO MONITOR RESPONDA:</caption>
				
					<tr>
						<td width="85%">a) O monitor demonstra conhecer o projeto ao qual está vinculado?</td>

						<td>
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.monitorConheceProjeto}">
								<f:selectItem itemLabel="Sim" itemValue="S"/>
								<f:selectItem itemLabel="Não" itemValue="N"/>
								<f:selectItem itemLabel="Parcialmente" itemValue="P"/>				
							</h:selectOneMenu>
						
						</td>
					</tr>					
					
					
					<tr>
						<td width="85%">b) Há coerência entre os objetivos propostas no projeto e as atividades desenvolvidas pelo(s) monitor(es)?</td>
						<td>
						
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.coerenciaObjetivosAtividades}">
								<f:selectItem itemLabel="Sim" itemValue="S"/>
								<f:selectItem itemLabel="Não" itemValue="N"/>
								<f:selectItem itemLabel="Parcialmente" itemValue="P"/>				
							</h:selectOneMenu>
						</td>
					</tr>					


					<tr>
						<td width="85%">c) Houve envolvimento do(s) monitor(es) no Seminário de Iniciação à Docência ( SID ), ou ausência ?</td>
						<td>
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.monitorEnvolvidoSid}">
								<f:selectItem itemLabel="Sim" itemValue="S"/>
								<f:selectItem itemLabel="Não" itemValue="N"/>
								<f:selectItem itemLabel="Parcialmente" itemValue="P"/>				
							</h:selectOneMenu>
						</td>
					</tr>

					<tr>
						<td width="85%">d) O projeto de monitoria propiciou a participação efetiva do monitor, contribuindo para a sua formação acadêmica?</td>
						<td>
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.projetoContribuiuFormacaoAcademicaMonitor}">
								<f:selectItem itemLabel="Sim" itemValue="S"/>
								<f:selectItem itemLabel="Não" itemValue="N"/>
								<f:selectItem itemLabel="Parcialmente" itemValue="P"/>				
							</h:selectOneMenu>
						</td>
					</tr>
			</table>
			
			
			
			<table width="100%" class="subFormulario">
				<caption>3- COM BASE NO PROJETO DE MONITORIA E NO RELATÓRIO DO COORDENADOR E ORIENTADOR(ES) RESPONDA:</caption>
					<tr>
						<td width="85%">a) As estratégias traçadas pelo projeto possibilitam o alcance dos objetivos?</td>

						<td>
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.estrategiasPossibilitamAlcanceObjetivos}">
								<f:selectItem itemLabel="Sim" itemValue="S"/>
								<f:selectItem itemLabel="Não" itemValue="N"/>
								<f:selectItem itemLabel="Parcialmente" itemValue="P"/>				
							</h:selectOneMenu>
						
						</td>
					</tr>
					
					
					
					<tr>
						<td width="85%">b) As atividades dos monitores estão de acordo com o que propõe o programa de monitoria?</td>
						<td>
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.atividadesMonitoresProgramaMonitoria}">
								<f:selectItem itemLabel="Sim" itemValue="S"/>
								<f:selectItem itemLabel="Não" itemValue="N"/>
								<f:selectItem itemLabel="Parcialmente" itemValue="P"/>				
							</h:selectOneMenu>
						</td>
					</tr>
					


					<tr>
						<td width="85%">c) A participação dos membros do projeto no SID foi satisfatória?</td>
						<td>
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.participacaoMembrosSidSatisfatoria}">
								<f:selectItem itemLabel="Sim" itemValue="S"/>
								<f:selectItem itemLabel="Não" itemValue="N"/>
								<f:selectItem itemLabel="Parcialmente" itemValue="P"/>				
							</h:selectOneMenu>
						</td>
					</tr>

					<tr>
						<td width="85%">d) As perspectivas propostas para o aprimoramento do projeto justificam a sua renovação?</td>
						<td>
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.propostasJustificamRenovacao}">
								<f:selectItem itemLabel="Sim" itemValue="S"/>
								<f:selectItem itemLabel="Não" itemValue="N"/>
								<f:selectItem itemLabel="Parcialmente" itemValue="P"/>				
							</h:selectOneMenu>
						</td>
					</tr>
					
					<tr>
						<td colspan="2">
							e) Parecer:<br/>
							<h:inputTextarea value="#{avalRelatorioProjetoMonitoria.obj.parecer}" style="width:98%" rows="3"/>
						</td>
					</tr>
					
					
					<tr>
						<td>f) Recomenda a renovação do projeto?</td>
						<td>
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.recomendaRenovacao}" onchange="submit()" valueChangeListener="#{avalRelatorioProjetoMonitoria.changeRecomendaRenovacao}" immediate="true" >
								<f:selectItem itemLabel="Sim" itemValue="true"/>
								<f:selectItem itemLabel="Não" itemValue="false"/>
							</h:selectOneMenu>
						</td>
					</tr>
					

					<tr id="manter_quantidade_bolsas" style="display: ${avalRelatorioProjetoMonitoria.obj.recomendaRenovacao ? '':'none'}">
						<td>g) Manter quantidade de Bolsas do projeto?</td>
						<td>
							<h:selectOneMenu value="#{avalRelatorioProjetoMonitoria.obj.mantemQuantidadeBolsas}" onchange="manterBolsas(this);">
								<f:selectItem itemLabel="Sim" itemValue="true"/>
								<f:selectItem itemLabel="Não" itemValue="false"/>
							</h:selectOneMenu>
						</td>
					</tr>
					
					
					
					<tr id="lista_discentes" style="display: ${(avalRelatorioProjetoMonitoria.obj.recomendaRenovacao and (not avalRelatorioProjetoMonitoria.obj.mantemQuantidadeBolsas)) ? '':'none'}">
						<td colspan="2">
				
							<table class="listagem" width="100%">	
							<caption><font color='red'>Marque</font> os Discentes que <font color='red'>Devem Perder</font> a bolsa de monitoria:</caption>
							
								<tr>
									<td>
									
													<t:dataTable value="#{avalRelatorioProjetoMonitoria.listaMonitoresAtivos}" var="corteBolsa" rowClasses="linhaPar,linhaImpar" width="100%" id="corte">
														
														<t:column>
																	<h:selectBooleanCheckbox id="chkCorteBolsa" value="#{corteBolsa.selecionado}" styleClass="noborder" onclick="javascript:cortarBolsa(this, #{corteBolsa.discente.matricula});" />
														</t:column>
														<t:column>
																<h:outputText value="#{corteBolsa.discente.matriculaNome} - <i>#{corteBolsa.tipoVinculo.descricao}</i>"  escape="false"/>
			
																<h:outputText value="<div id='txaJustificarCorte#{corteBolsa.discente.matricula}' style='display: #{corteBolsa.selecionado ? '':'none' }'>"  escape="false"/>
																	<f:verbatim><label><i>Justificar motivo do corte da bolsa:</i></label><br/></f:verbatim>													
																	<h:inputTextarea value="#{corteBolsa.justificativa}" rows="1" style="width:98%"  readonly="#{avalRelatorioProjetoMonitoria.readOnly}"/>
																	
																<f:verbatim></div></f:verbatim>
														</t:column>
														
													</t:dataTable>
														
									</td>
								</tr>
								
							    <h:outputText escape="false" value="<tr><td><center><font color=red>Atualmente não há monitores ativos neste projeto.</font></center></td></tr>" rendered="#{empty avalRelatorioProjetoMonitoria.monitoresAtivos}"/>
				
							</table>		
				 		   </td>
					</tr>
					
					
					
			</table>
			
			
			
		</td>
	</tr>



	<tfoot>
		<tr>
			<td colspan="2">
			<h:commandButton value="Submeter Avaliação" action="#{avalRelatorioProjetoMonitoria.avaliarProjeto}" 
			onclick="return confirm('Tem certeza que deseja enviar a avaliação do relatório?');"/>
			<h:commandButton value="Cancelar" action="#{avalRelatorioProjetoMonitoria.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>

	</table>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
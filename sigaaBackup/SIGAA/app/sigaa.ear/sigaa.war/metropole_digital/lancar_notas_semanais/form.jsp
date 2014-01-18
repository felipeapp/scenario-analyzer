<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<h2><ufrn:subSistema/> > Lançamento Notas por Período </h2>
<a4j:keepAlive beanName="lancamentoNotasSemanais"/>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>
<style>
	tr.selecionada td { background: #C4D2EB; }
</style>

<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>

<f:view>
	<h:form>
		<h:inputHidden value="#{lancamentoNotasSemanais.turmaEntradaSelecionada.id}"/>
		
		
		<c:if test="${(empty lancamentoNotasSemanais.listaDiscentesTurma) || (empty lancamentoNotasSemanais.listaPeriodosTurma) || (empty lancamentoNotasSemanais.tabelaAcompanhamento) }">
			<center><i> Nenhum discente e/ou período encontrado.</i></center>
		</c:if>
		
		<c:if test="${(not empty lancamentoNotasSemanais.listaDiscentesTurma) && (not empty lancamentoNotasSemanais.listaPeriodosTurma) && (not empty lancamentoNotasSemanais.tabelaAcompanhamento) }">
			
			<div class="descricaoOperacao">
				<p>- Digite as notas de Participação Presencial(PP) e Participação Virtual (PV) utilizando vírgula para separar a casa decimal.</p>
				<p>- Clique em Salvar para gravar as notas inseridas e continuá-las posteriormente.</p>
				<p>- A Participação Total é composta pela média ponderada entre PV e PP, conforme a fórmula a seguir: PT = (PV * 7 + PP *3 )/ 10</p>
			</div>
			
			<p align="center"><h2 align="center">TURMA: ${lancamentoNotasSemanais.turmaEntradaSelecionada.anoReferencia}.${lancamentoNotasSemanais.turmaEntradaSelecionada.periodoReferencia} - ${lancamentoNotasSemanais.turmaEntradaSelecionada.especializacao.descricao} - ${lancamentoNotasSemanais.turmaEntradaSelecionada.cursoTecnico.nome}
			<br />OPÇÃO PÓLO GRUPO: ${lancamentoNotasSemanais.turmaEntradaSelecionada.opcaoPoloGrupo.descricao}</h2></p>
			
			<table class="formulario" style="width: 100%">
				<caption>Lançamento de Notas por Período</caption>
			  	<tbody>
				  	<tr>
				  		<td>
				  		<div style="overflow: auto; width:970px; ">
					  		<rich:dataTable value="#{lancamentoNotasSemanais.tabelaAcompanhamento}" var="tab" id="tabela" rowKeyVar="r" styleClass="listagem" headerClass="" onRowMouseOver="$(this).addClassName('selecionada')"
 onRowMouseOut="$(this).removeClassName('selecionada')">
						  		<rich:column styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}"  style="width: 80px;" colspan="2" >
									<f:facet name="header">
					                  	<h:outputText value="MATRÍCULA" />
					              	</f:facet>
					            	<h:outputText value="#{lancamentoNotasSemanais.listaDiscentesTurma[r].discente.matricula}"/>
								</rich:column>
						
								<rich:column style="width: 200px;"colspan="2" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}"> 
									<f:facet name="header">	
					                  	<h:outputText value="NOME	" />
				              		</f:facet>
					              	<h:outputText value="#{lancamentoNotasSemanais.listaDiscentesTurma[r].discente.pessoa.nome}" />
								</rich:column>
								<rich:column style="width: 20px;"colspan="2" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">
									<f:facet name="header">	
					                  	<h:outputText value="" />
				              		</f:facet>
				              		<strong>
					              	<h:outputText value="PP" style="display:block;margin-bottom:9px;"/>
					              	<h:outputText value="PV" />
					              	</strong>
								</rich:column>
								
								<rich:columns value="#{lancamentoNotasSemanais.listaPeriodosTurma}" var="colunas" index="c" colspan="2" id="colunas" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">
										<f:facet name="header">
						                  	<f:verbatim>										
						                  		<acronym title="PERÍODO ${colunas.numeroPeriodo}: DE ${colunas.diaMesInicioTexto} A ${colunas.diaMesFimTexto} (${colunas.chTotalPeriodo}h)"  style="text-align:center; cursor:pointer;border:none;">
						                  			P<fmt:formatNumber value="${colunas.numeroPeriodo}" pattern="00"/>
						                  			</acronym>
						                  			<br />	
						                  	</f:verbatim>					                  	
						              	</f:facet>
						              	
						              	<c:if test="${lancamentoFrequenciaIMD.dataAtual >= colunas.dataInicio}">
						              			<h:inputText value="#{tab[c].participacaoPresencial}" size="2" maxlength="4" onkeydown="return(formataValorNota(this, event, 1))"  style="text-align:rigth;margin-bottom: 8px;" tabindex="#{c}" onfocus="this.select()">
						              				<f:converter converterId="convertNota"/>	
												</h:inputText>
										</c:if>
										<c:if test="${lancamentoFrequenciaIMD.dataAtual < colunas.dataInicio}">
												<h:inputText value="#{tab[c].participacaoPresencial}" size="2" maxlength="4" onkeydown="return(formataValorNota(this, event, 1))"  style="text-align:rigth;margin-bottom: 8px;" tabindex="#{c}" disabled="true">
						              				<f:converter converterId="convertNota"/>	
												</h:inputText>
										</c:if>
										
										<h:inputText value="#{tab[c].participacaoVirtual}" size="2" maxlength="4" onkeydown="return(formataValorNota(this, event, 1))" style="text-align:left;" tabindex="#{c}" disabled="true">
				              				<f:converter converterId="convertNota"/>
										</h:inputText>					
						        </rich:columns>
							</rich:dataTable>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			
			<div align="center">
				<table style="margin-top: 10;  text-align: center; width: 30%" >
					<tr><td><strong>PP</strong> Participação Presencial</td>		
		  			<td><strong>PV</strong> Participação Virtual</td>
				</table>
			</div>
			
			<div class="opcoes" align="right">
			<table style="width: 100%;">
				<tfoot>
				<tr>
					<td width="70" valign="top" style="float:right; text-align:center; margin:5px 0 3px;">
						<h:commandLink action="#{ lancamentoNotasSemanais.visualizarNotasSemanais }" title="Imprimir">
						 	<h:graphicImage value="/img/consolidacao/printer.png"/>
						</h:commandLink>
					</td>
					
					<td width="70" valign="top" style="float:right; text-align:center; margin:5px 0 3px;"><h:commandButton action="#{lancamentoNotasSemanais.salvar }" image="/img/consolidacao/publicar.png" alt="Salvar e Publicar" title="Salvar e Publicar"/></td>
					
					<td width="70" valign="top"
							style="float: right; text-align: center; margin: 5px 0 3px;"><h:commandButton
								action="#{lancamentoNotasSemanais.carregarIntegracaoPvMoodle }"
								image="/img/projetos/document_into.png"
								alt="Sincronizar Notas PV" title="Sincronizar Notas PV" />
					</td>
						
					<td width="70" valign="top" style="float:right; text-align:center; margin:5px 0 3px;"><h:commandButton action="#{lancamentoNotasSemanais.voltarTelaPortal }" image="/img/consolidacao/nav_left_red.png" alt="Voltar" title="Voltar"/></td>
				</tr>
				<tr>
					<td width="70" valign="top" style="float:right; text-align:center;"><h:commandLink action="#{lancamentoNotasSemanais.visualizarNotasSemanais}" value="Imprimir"/></td>
					<td width="70" valign="top" style="float:right; text-align:center;"><h:commandLink action="#{lancamentoNotasSemanais.salvar}" value="Salvar"/></td>
					<td width="70" valign="top"	style="float: right; text-align: center;"><h:commandLink action="#{lancamentoNotasSemanais.carregarIntegracaoPvMoodle}" value="Sincronizar Notas PV" /></td>
					<td width="70" valign="top" style="float:right; text-align:center;"><h:commandLink action="#{lancamentoNotasSemanais.voltarTelaPortal}" value="Voltar"/></td>
				</tr>
			</tfoot>
			</table>
		</div>
		</c:if>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>
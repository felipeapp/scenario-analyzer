<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="lancamentoNotasDisciplina" />
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>

<script type="text/javascript"
	src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>

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

<script type="text/javascript" src="/sigaa/javascript/metropole_digital/consolidacao.js"></script>

<f:view>
	<h2>
		<ufrn:subSistema />
		> Lançamento Notas por Disciplina
	</h2>
	<h:form>
	
		<div class="descricaoOperacao">
			<p>- Digite as notas de Atividades Executadas(AE) e Prova Escrita (PE) utilizando vírgula para separar a casa decimal.</p>
			<p>- Clique em Sincronizar Notas AE para sincronizar as notas com o Moodle.</p>
			<p>- Clique em Salvar para gravar as notas inseridas e continuá-las posteriormente.</p>
		</div>
	
		<p align="center"><h2 align="center">TURMA: ${lancamentoNotasDisciplina.turmaEntrada.cursoTecnico.nome}
		<br />OPÇÃO PÓLO GRUPO: ${lancamentoNotasDisciplina.turmaEntrada.opcaoPoloGrupo.descricao}<br />DISCIPLINA: ${lancamentoNotasDisciplina.disciplina.nome}</h2></p>
		
		<table class="formulario" style="width: 100%">
			<caption>Lançamento de Notas da Disciplina (${fn:length(lancamentoNotasDisciplina.listaNotasDiscentes)})</caption>
			<tbody>
				<tr>
					<td><rich:dataTable
							value="#{lancamentoNotasDisciplina.listaNotasDiscentes}"
							var="linha" id="tabela" rowKeyVar="r" styleClass="listagem"
							headerClass=""  onRowMouseOver="$(this).addClassName('selecionada')"
 onRowMouseOut="$(this).removeClassName('selecionada')">
							<!-- Cabeçalho -->
							<f:facet name="header">
								<rich:columnGroup>
									<rich:column styleClass="" style="width: 80px;" rowspan="2">
										<h:outputText value="Matrícula" />
									</rich:column>
									<rich:column rowspan="2">
										<h:outputText value="Discente" />
									</rich:column>
									<rich:column style="width: 5%; text-align:center;" colspan="3">
										<h:outputText value="Participação" style="text-align: center"/>
									</rich:column>
									<rich:column style="width: 5%; text-align:center;" rowspan="2">
										<h:outputText value="AE"/>
									</rich:column>
									<rich:column style="width: 5%; text-align:center;" rowspan="2">
										<h:outputText value="PE" />
									</rich:column>
									<rich:column style="width: 5%; text-align:center;" rowspan="2">
										<h:outputText value="Média" />
									</rich:column>
									<rich:column style="width: 5%; text-align:center;" rowspan="2">
										<h:outputText value="CHNF" />
									</rich:column>
									
									<!-- Subcolunas de Participação -->
									<rich:column style="width: 5%; text-align:center;" rowspan="1" breakBefore="true">
										<h:outputText value="PP" />
									</rich:column>
									<rich:column style="width: 5%; text-align:center;" rowspan="1">
										<h:outputText value="PV" />
									</rich:column>
									<rich:column style="width: 5%; text-align:center;" rowspan="1">
										<h:outputText value="PT" />
									</rich:column>
								</rich:columnGroup>
							</f:facet>


<!--						Dados da Tabela -->
							<rich:column style="width: 80px;" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">
								<h:outputText value="#{linha.discente.matricula}" />
							</rich:column>

							<rich:column styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}" > 
								<h:outputText value="#{linha.discente.pessoa.nome}"  />
							</rich:column>
							
							
							<!-- Notas de Participação -->
							<rich:column style="width: 5%; text-align:right" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">
								<h:outputText value="#{linha.participacaoPresencial}" rendered="#{linha.participacaoPresencial !=null}">
									<f:converter converterId="convertNota" />
								</h:outputText>
								<h:outputText value=" -- " rendered="#{linha.participacaoPresencial ==null}" />									
							</rich:column>
							
							<rich:column style="width: 5%; text-align:right;" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">
								<h:outputText value="#{linha.participacaoVirtual}">
									<f:converter converterId="convertNota" />
								</h:outputText>
								<h:outputText value=" -- " rendered="#{linha.participacaoVirtual ==null}" />
							</rich:column>
							
							<rich:column style="width: 5%; text-align:right" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}" >								
								<h:outputText value="#{linha.participacaoTotal.nota}" rendered="#{linha.participacaoVirtual != null && linha.participacaoPresencial != null}">
									<f:converter converterId="convertNota"/>
								</h:outputText>								
								<h:outputText value=" -- " rendered="#{linha.participacaoVirtual == null || linha.participacaoPresencial == null}" />								
							</rich:column>										
							
							
							<!-- Atividades Online -->
							<rich:column style="width: 5%" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">
								<h:inputText value="#{linha.atividadeOnline.nota}" size="2"
 									maxlength="4"
 									onkeypress="return(formataValorNota(this, event, 1))"
									style="text-align: center;">
									<f:attribute name="linha" value="#{r}" />
									<a4j:support event="onchange" reRender="media" />
									<f:converter converterId="convertNota" />
								</h:inputText>
							</rich:column>
							
							<!-- Prova Escrita -->							
							<rich:column style="width: 5%; text-align:right" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">
								<h:inputText value="#{linha.provaEscrita.nota}" size="2"
 									maxlength="4"
 									onkeypress="return(formataValorNota(this, event, 1))"
									style="text-align: center;">
									<f:attribute name="linha" value="#{r}" />
									<a4j:support event="onchange" reRender="media" />
									<f:converter converterId="convertNota" />
								</h:inputText>
							</rich:column>
							
							
							<!-- Média da Disciplina -->
							<rich:column style="width: 5%; text-align:right" id="media" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">									
									<h:outputText
	   									value="#{(linha.participacaoTotal.nota + linha.atividadeOnline.nota +(linha.provaEscrita.nota*2))/4}"  
	   									rendered="#{linha.participacaoVirtual != null && linha.participacaoPresencial != null && linha.atividadeOnline.nota !=null && linha.provaEscrita.nota != null}"> 
	  										<f:converter converterId="convertNota" />
	  								</h:outputText>																																											 
  									<h:outputText value=" -- " rendered="#{linha.participacaoVirtual == null || linha.participacaoPresencial == null || linha.atividadeOnline.nota ==null || linha.provaEscrita.nota == null}" />
 							</rich:column> 
 							
 							<!-- Carga Horária não frequentada -->
 							<rich:column style="width: 5%; text-align:right" styleClass="#{r%2==0? 'linhaPar': 'linhaImpar'}">
 								<h:outputText value="#{linha.chnf}" />
 							</rich:column> 
						</rich:dataTable></td>
				</tr>
			</tbody>
		</table>
		<div align="center">
				<table style="width: 85%">
					<tr align="center">
						<td style="text-align: right;"><strong>PP:</strong> Participação Presencial</td>		
		  				<td style="text-align: center" colspan="2"><strong>PV:</strong> Participação Virtual</td>
		  				<td style="text-align: left;"><strong>PT:</strong> Participação Total</td>		  				
		  			</tr>
		  			<tr>
		  				<td style="text-align: center"><strong>AE:</strong> Atividades Executadas no Moodle</td>
		  				<td style="text-align: center"><strong>PE:</strong> Prova Escrita</td>
		  				<td style="text-align: center"><strong>Média:</strong> Média na Disciplina</td>
		  				<td style="text-align: center"><strong>CHNF:</strong> Carga Horária não Frequentada</td>
		  			</tr>
				</table>				
			</div>

		<div class="opcoes" align="right">
			<table style="width: 100%;">
				<tfoot>
					<tr>
						<td width="70" valign="top"
							style="float: right; text-align: center; margin: 5px 0 3px;">
							<h:commandLink
								action="#{ lancamentoNotasDisciplina.visualizarNotas }"
								title="Imprimir">
								<h:graphicImage value="/img/consolidacao/printer.png" />
							</h:commandLink>
						</td>
						<td width="70" valign="top"
							style="float: right; text-align: center; margin: 5px 0 3px;"><h:commandButton	
								action="#{lancamentoNotasDisciplina.salvar }"
								image="/img/consolidacao/disk_green.png" alt="Salvar"
								title="Salvar e Publicar" /></td>
						<td width="70" valign="top"
							style="float: right; text-align: center; margin: 5px 0 3px;"><h:commandButton
								action="#{lancamentoNotasDisciplina.carregarIntegracaoAeMoodle }"
								image="/img/projetos/document_into.png"
								alt="Sincronizar Notas AE" title="Sincronizar Notas AE" /></td>
						<td width="70" valign="top"
							style="float: right; text-align: center; margin: 5px 0 3px;"><h:commandButton
								action="#{lancamentoNotasDisciplina.voltar}"
								image="/img/consolidacao/nav_left_red.png"
								alt="Voltar" title="Voltar" /></td>
					</tr>
					<tr>
						<td width="70" valign="top"
							style="float: right; text-align: center;"><h:commandLink
								action="#{lancamentoNotasDisciplina.visualizarNotas }"
								value="Imprimir" /></td>
						<td width="70" valign="top"
							style="float: right; text-align: center;"><h:commandLink
								action="#{lancamentoNotasDisciplina.salvar}" value="Salvar" /></td>
						<td width="70" valign="top"
							style="float: right; text-align: center;"><h:commandLink
								action="#{lancamentoNotasDisciplina.carregarIntegracaoAeMoodle}"
								value="Sincronizar Notas AE" /></td>
						<td width="70" valign="top"
							style="float: right; text-align: center;"><h:commandLink
								action="#{lancamentoNotasDisciplina.voltar}"
								value="Voltar" /></td>
					</tr>
				</tfoot>
			</table>
		</div>
		

	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
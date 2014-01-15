<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;  	
	width: 98%;
	overflow:auto;
}
</style>


<script type="text/javascript">
	function acaoRealizada(acaoRealizada) {
		var motivo = document.getElementById("motivoNaoRealizada") ;
		
		if (acaoRealizada.value == 'TRUE') {
			motivo.style.display = "none";
		} else {
			motivo.style.display = "";
		}		
	}
	
	/* Limita o campo ao limite de um inteiro */
	function limiteInteiro(campo){
		var maxNum = 2147483647;
		if(campo.value > maxNum){
			campo.value = campo.value.slice(0, -1);
		}
		return campo;
	}
</script>


<f:view>

<h:outputText value="#{relatorioProjeto.create}"/>
<h:outputText value="#{participanteAcaoExtensao.create}"/>

	<h2><ufrn:subSistema /> > Relatório de Projetos de Extensão</h2>

	<h:form id="form" enctype="multipart/form-data">
		<table class="formulario" width="100%">
		<caption class="listagem">CADASTRO DE ${relatorioProjeto.obj.tipoRelatorio.descricao} DE PROJETOS DE EXTENSÃO</caption>
			
		<tr>
				<th width="25%"><b>Código do Projeto:</b></th>
				<td><h:outputText	value="#{relatorioProjeto.obj.atividade.codigo}" /></td>
		</tr>

		<tr>
				<th><b>Título do Projeto:</b></th>
				<td><h:outputText	value="#{relatorioProjeto.obj.atividade.titulo}"/></td>
		</tr>
		
		<tr>
				<th><b>Unidade Proponente:</b></th>
				<td><h:outputText value="#{relatorioProjeto.obj.atividade.unidade.nome}" /></td>
		</tr>
		
		<tr>
				<th><b>Fontes Financiamento:</b></th>
				<td><h:outputText value="#{relatorioProjeto.obj.atividade.fonteFinanciamentoString}" /></td>
		</tr>

		
		<tr>
				<th>
					<b>Nº Discentes Envolvidos:</b>					
				</th>
				<td>
					<h:outputText value="#{relatorioProjeto.obj.atividade.totalDiscentes}"/>
					&nbsp;
					<ufrn:help img="/img/ajuda.gif">Total e discentes envolvidos na execução do Projeto.</ufrn:help>
				</td>
		</tr>
		


		<tr>
			<th class="required"><b>Esta Ação foi realizada:</b></th>
			<td><h:selectOneRadio value="#{ relatorioProjeto.obj.acaoRealizada }" onclick="javascript:acaoRealizada(this)" id="acaoFoiRealizada">
					<f:selectItem itemLabel="SIM" itemValue="TRUE" />
					<f:selectItem itemLabel="NÃO" itemValue="FALSE" />
				</h:selectOneRadio>
			</td>
		</tr>

		<tr>
			<td  colspan="2" class="subFormulario"> Detalhamento das atividades desenvolvidas:</td>
		</tr>	


		<tr>
			<td colspan="2">
				<div id="motivoNaoRealizada" style="display: ${relatorioProjeto.obj.acaoRealizada == null ? 'none' : (relatorioProjeto.obj.acaoRealizada ? 'none' : '')};">
					<b> Motivo da não realização desta ação: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
					<h:inputTextarea rows="4" style="width:99%" value="#{relatorioProjeto.obj.motivoAcaoNaoRealizada}" rendered="#{not relatorioProjeto.readOnly}" id="motivoAcaoNaoRealizada"/>
					<h:panelGroup rendered="#{relatorioProjeto.readOnly}" id="motivoAcaoNaoRealizadaReadOnly">
						<br/>
						<h:outputText style="width:99%" value="#{relatorioProjeto.obj.motivoAcaoNaoRealizada}"/>
					</h:panelGroup>
				</div>
			</td>	
		</tr>

		<tr>
			<td colspan="2"><b> Existe relação objetiva entre a proposta pedagógica do curso e a proposta do projeto de extensão? Justifique: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioProjeto.obj.relacaoPropostaCurso}" rendered="#{not relatorioProjeto.readOnly}" id="relacaoPropostaCurso"/>
				<h:panelGroup rendered="#{relatorioProjeto.readOnly}" id="relacaoPropostaCursoReadOnly">
					<br/>
					<h:outputText style="width:99%" value="#{relatorioProjeto.obj.relacaoPropostaCurso}"/>
				</h:panelGroup>
			</td>	
		</tr>

		<tr>
			<td colspan="2"><b> Atividades Realizadas: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioProjeto.obj.atividadesRealizadas}" rendered="#{not relatorioProjeto.readOnly}" id="atividades"/>
			<h:panelGroup rendered="#{relatorioProjeto.readOnly}" id="atividadesReadOnly">
				<br/>
				<h:outputText style="width:99%" value="#{relatorioProjeto.obj.atividadesRealizadas}"/>
			</h:panelGroup>	
			</td>
		</tr>

		<tr>
			<td colspan="2"><b> Resultados Obtidos: Qualitativos. </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioProjeto.obj.resultadosQualitativos}" rendered="#{not relatorioProjeto.readOnly}" id="qualitativos"/>
			<h:panelGroup rendered="#{relatorioProjeto.readOnly}" id="qualitativosReadOnly">
				<br/>
				<h:outputText style="width:99%" value="#{relatorioProjeto.obj.resultadosQualitativos}"/>
			</h:panelGroup>
			</td>	
		</tr>


		<tr>
			<td colspan="2"><b> Resultados Obtidos: Quantitativos. </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioProjeto.obj.resultadosQuantitativos}" rendered="#{not relatorioProjeto.readOnly}" id="quantitativos"/>
			<h:panelGroup rendered="#{relatorioProjeto.readOnly}" id="quantitativosReadOnly">
				<br/>
				<h:outputText style="width:99%" value="#{relatorioProjeto.obj.resultadosQuantitativos}"/>
			</h:panelGroup>	
			</td>
		</tr>
		
		<tr>
			<td colspan="2" ><b> Dificuldades Encontradas: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioProjeto.obj.dificuldadesEncontradas}" rendered="#{not relatorioProjeto.readOnly}" id="dificuldades"/>
			<h:panelGroup rendered="#{relatorioProjeto.readOnly}" id="dificuldadesReadOnly">
				<br/>
				<h:outputText style="width:99%" value="#{relatorioProjeto.obj.dificuldadesEncontradas}"/>
			</h:panelGroup>	
			</td>	
		</tr>


		<tr>
			<td colspan="2" ><b> Ajustes Realizados Durante a Execução da Ação: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center;"/>
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioProjeto.obj.ajustesDuranteExecucao}" rendered="#{not relatorioProjeto.readOnly}" id="ajustes"/>
			<h:panelGroup rendered="#{relatorioProjeto.readOnly}" id="ajustesReadOnly">
				<br/>
				<h:outputText style="width:99%" value="#{relatorioProjeto.obj.ajustesDuranteExecucao}"/>
			</h:panelGroup>	
			</td>
		</tr>


		<tr>
			<td colspan="2"><b> Público Estimado:</b>
				<h:outputText value="#{relatorioProjeto.obj.atividade.publicoEstimado}" id="publicoEstimado"/> pessoas
				<ufrn:help img="/img/ajuda.gif">Público estimado informado durante o cadastro da proposta do projeto.</ufrn:help>
			</td>	
		</tr>
		
		<tr>
			<td colspan="2"><b> Público Real Atingido: </b><h:graphicImage  url="/img/required.gif" style="vertical-align: center; margin-right:3px"/>
				<h:panelGroup rendered="#{not relatorioProjeto.readOnly}">
					<h:inputText title="Público real atingido" value="#{relatorioProjeto.obj.publicoRealAtingido}" id="publicoRealAtingido" size="10" maxlength="9" onkeyup="return formatarInteiro(this)"/> pessoas
				</h:panelGroup>
				<h:outputText title="Público real atingido" value="#{relatorioProjeto.obj.publicoRealAtingido}" rendered="#{relatorioProjeto.readOnly}" id="publicoRealAtingidoReadOnly" /> pessoas				
			</td>	
		</tr>
		
		<tr>
			<td  colspan="2" class="subFormulario"> Outras ações realizadas vinculadas ao projeto:</td>
		</tr>	
		<tr>	
			<td colspan="2">
				<t:dataList value="#{relatorioProjeto.outrasAcoes}" var="acao">
						<t:column>
										<f:verbatim><span style="width: 180px; display: block; float: left; "></f:verbatim>
										<h:selectBooleanCheckbox value="#{acao.selecionado}" styleClass="noborder" disabled="#{relatorioProjeto.readOnly}"/>
										<h:outputText value="#{acao.descricao}" />
										<f:verbatim> </span> </f:verbatim>
						</t:column>
				 </t:dataList>
			</td>
		</tr>
		
		<tr>
			<td  colspan="2" class="subFormulario"> Apresentação do projeto em eventos de extensão:</td>
		</tr>	
		<tr>	
			<td colspan="2">
				<t:dataList value="#{ relatorioProjeto.apresentacoes }" var="apresentacao">
						<t:column>
										<f:verbatim><span style="width: 180px; display: block; float: left; "></f:verbatim>
										<h:selectBooleanCheckbox value="#{apresentacao.selecionado}" styleClass="noborder" disabled="#{relatorioProjeto.readOnly}"/>
										<h:outputText value="#{apresentacao.descricao}" />
										<f:verbatim> </span> </f:verbatim>
						</t:column>
				</t:dataList>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> Produção acadêmica gerada:</td>
		</tr>	
		<tr>	
			<td colspan="2">
				<div class="scrollbar" style="margin-left: 5px;">
				</div>
						<t:dataList value="#{ relatorioProjeto.producoesGeradas }" var="prod">
								<t:column>
										<f:verbatim><span style="width: 350px; display: block; float: left; "></f:verbatim>
										<h:selectBooleanCheckbox value="#{prod.selecionado}" styleClass="noborder" disabled="#{relatorioProjeto.readOnly}"/>
										<h:outputText value="#{prod.descricao}" />
										<f:verbatim> </span> </f:verbatim>
								</t:column>
						 </t:dataList>
			</td>
		</tr>
		
		
		
		
		<tr>
			<td colspan="2" class="subFormulario"> Convênios e Contratos:</td>
		</tr>	

		<tr>
			<td align="center" colspan="2">
				<div class="descricaoOperacao">
					Se esta Ação de extensão originou um convênio ou contrato da ${ configSistema['siglaInstituicao'] } com outro órgão, informe o número deles.
				</div>
			</td>
		</tr>

		<tr>
				<th> Nº Convênio: </th>
				<td><h:inputText  id="numeroConvenio" value="#{relatorioProjeto.obj.numeroConvenio}"  readonly="#{relatorioProjeto.readOnly}" size="10" onkeyup="return(formatarInteiro(limiteInteiro(this)))" maxlength="10"/></td>
		</tr>
		<tr>
				<th>Ano Convênio:</th>
				<td><h:inputText  id="anoConvenio" value="#{relatorioProjeto.obj.anoConvenio}"  readonly="#{relatorioProjeto.readOnly}" size="5"  maxlength="4" onkeyup="return(formatarInteiro(this))" /></td>
		</tr>

		<tr>
				<th>Nº Contrato:</th>
				<td><h:inputText  id="numeroContrato" value="#{relatorioProjeto.obj.numeroContrato}"  readonly="#{relatorioProjeto.readOnly}" size="10" onkeyup="return(formatarInteiro(this))" maxlength="10"/></td>
		</tr>
		<tr>
				<th>Ano Contrato:</th>
				<td><h:inputText  id="anoContrato" value="#{relatorioProjeto.obj.anoContrato}"  readonly="#{relatorioProjeto.readOnly}" size="5"  maxlength="4" onkeyup="return(formatarInteiro(this))" /></td>
		</tr>

		<tr>
			<td colspan="2" class="subFormulario"> Detalhamento de utilização dos recursos financeiros </td>
		</tr>	

		<tr>
			<td colspan="2">
				<table class="listagem">
							<tr>
								<td>
									<c:if test="${not empty relatorioProjeto.obj.detalhamentoRecursos}">
										<t:dataTable id="dt" value="#{relatorioProjeto.obj.detalhamentoRecursos}" var="consolidacao"
											 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" 
											 forceIdIndex="true">
														<t:column>
															<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
															<h:outputText value="#{consolidacao.elemento.descricao}" />
														</t:column>

														<t:column>
															<f:facet name="header"><f:verbatim>FAEx (Interno)</f:verbatim></f:facet>
															<f:verbatim>R$ </f:verbatim>
															<h:inputText value="#{consolidacao.faex}"  id="fundo" size="12" maxlength="16" onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioProjeto.readOnly}">
																	<f:converter converterId="convertMoeda"/>
															</h:inputText>
														</t:column>

														<t:column>
															<f:facet name="header"><f:verbatim>Funpec</f:verbatim></f:facet>
															<f:verbatim>R$ </f:verbatim>
															<h:inputText value="#{consolidacao.funpec}" id="fundacao"  size="12" maxlength="16" onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioProjeto.readOnly}">
																	<f:converter converterId="convertMoeda"/>																							
															</h:inputText>
														</t:column>

														<t:column>
															<f:facet name="header"><f:verbatim>Outros (Externo)</f:verbatim></f:facet>
															<f:verbatim>R$ </f:verbatim>
															<h:inputText value="#{consolidacao.outros}" id="outros" size="12" maxlength="16" onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioProjeto.readOnly}" >
																	<f:converter converterId="convertMoeda"/>
															</h:inputText>
														</t:column>
										</t:dataTable>
									</c:if>
								</td>
						</tr>
							
						<c:if test="${empty relatorioProjeto.obj.detalhamentoRecursos}">
							<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
						</c:if>
							
					</table>
			</td>
		</tr>
		
		
		<tr>
			<td colspan="2" class="subFormulario"> Anexar Arquivo com outros detalhes da execução da ação </td>
		</tr>	

		<tr>
			<th  class="required"> Descrição:</th>
			<td>
				<h:inputText  id="descricao" value="#{relatorioProjeto.descricaoArquivo}" size="60" maxlength="90" readonly="#{relatorioProjeto.readOnly}"/>
			</td>
		</tr>
		
		
		<tr>
			<th class="required" width="20%">Arquivo:</th>
			<td>
				<t:inputFileUpload id="uFile" value="#{relatorioProjeto.file}" storage="file" disabled="#{relatorioProjeto.readOnly}" size="50"/>
			</td>
		</tr>

		<tr>
			<td colspan="3">
				<center><h:commandButton	action="#{relatorioProjeto.anexarArquivo}" value="Anexar Arquivo" id="btAnexarArqui" disabled="#{relatorioProjeto.readOnly}"/></center>
			</td>
		</tr>
		
		
		<tr>
			<td colspan="3">
				<div class="infoAltRem">
				    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" styleClass="noborder"/>: Remover Arquivo
				    <h:graphicImage value="/img/view.gif" style="overflow: visible;" styleClass="noborder"/>: Ver Arquivo				    
				</div>
			</td>
		</tr>	
		
		<tr>
			<td colspan="2">
			
				<table class="formulario" style="width: 100%">			
					<thead>
						<tr>
							<td>Descrição Arquivo</td>
							<td width="5%"></td>
							<td width="5%"></td>
						</tr>
					</thead>
				
					<c:forEach items="#{relatorioProjeto.arquivosRelatorio}" var="anexo" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td> ${ anexo.descricao } </td>
							<td>  
								<h:commandLink action="#{relatorioProjeto.removeAnexo}" onclick="#{confirmDelete}">
									<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
									<f:param name="idArquivo" value="#{anexo.idArquivo}"/>
									<f:param name="idArquivoRelatorio" value="#{anexo.id}"/>
								</h:commandLink>
							</td>
							<td>
								<h:commandLink action="#{relatorioProjeto.viewArquivo}" target="_blank">
									<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Ver Arquivo"/>
									<f:param name="idArquivo" value="#{anexo.idArquivo}"/>
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</table> 
			</td>
		</tr>
		
		<tr>
			<td colspan="2">
				<c:if test="${empty relatorioProjeto.arquivosRelatorio}">
					<center><font color='red'>Não há arquivos adicionados</font></center>
				</c:if>
			</td>
		</tr>
		
		
		<tr>
			<td colspan="2" class="subFormulario"> Lista de Participantes do Projeto </td>
		</tr>	
		
			<tr>
				<td colspan="2">
					<div class="infoAltRem">
						<c:if test="${acesso.coordenadorExtensao}">
								<h:commandLink action="#{relatorioAcaoExtensao.alterarListaParticipantes}" style="border: 0;" 
									rendered="#{relatorioProjeto.obj.atividade.aprovadoEmExecucao}" title="Alterar Lista de Participantes">
								        <f:param name="id" value="#{relatorioProjeto.obj.atividade.id}"/>
								        <h:graphicImage url="/img/extensao/user1_add.png" />
								: Alterar Lista de Participantes
								</h:commandLink> 
						</c:if>								
				    	<h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;" styleClass="noborder"/>: Visualizar				    
					</div>
				</td>
			</tr>		
		
		<tr>
			<td colspan="2">
					<table class="listagem">
							<thead>
							<tr>
								<th style="text-align: right;">Nº</th>
								<th style="text-align: center;">CPF</th>
								<th>Nome</th>
								<th>Participação</th>								
								<th style="text-align: center;">Certificado</th>
								<th></th>
							</tr>
							</thead>
				
							<tbody>
								<c:forEach items="#{relatorioProjeto.obj.atividade.participantes}" var="participante" varStatus="status">
										<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
											<td>${status.count}</td>
											<td>${participante.cadastroParticipante.cpf}</td>
											<td>${participante.cadastroParticipante.nome}</td>
											<td>${participante.tipoParticipacao.descricao}</td>			
											<td>${participante.autorizacaoCertificado ? 'SIM' : 'NÃO'}</td>
											<td width="2%">											
												<h:commandLink title="Visualizar" action="#{participanteAcaoExtensao.view}" style="border: 0;">
												        <f:param name="id" value="#{participante.id}"/>
							                   			<h:graphicImage url="/img/extensao/user1_view.png" />
												</h:commandLink>											
											</td>
										</tr>
								</c:forEach>
								
								<c:if test="${empty relatorioProjeto.obj.atividade.participantes}">
									<tr><td colspan="6"><center><i>Não há participantes cadastrados</i></center></td></tr>
								</c:if>				
							</tbody>
					</table>			
			</td>
		</tr>
		
				
		<tfoot>
			<tr>
				<td colspan="2">
					<c:if test="${relatorioProjeto.relatorioFinal && relatorioProjeto.existeRelatorioParcial}">
						<h:commandButton immediate="true" value="Importar Dados do Relatório Parcial" action="#{relatorioProjeto.importarDadosRelatorioParcial}" 
							rendered="#{!relatorioProjeto.readOnly }" />
					</c:if>
						<h:commandButton value="Salvar (Rascunho)" action="#{relatorioAcaoExtensao.salvar}" rendered="#{!relatorioProjeto.readOnly}"/>	
						<h:commandButton value="#{relatorioAcaoExtensao.confirmButton}" action="#{relatorioProjeto.enviar}" rendered="#{!relatorioProjeto.readOnly}" /> 
						<h:commandButton value="#{relatorioProjeto.confirmButton}" action="#{relatorioProjeto.removerRelatorio}" onclick="return confirm('Deseja Remover este Relatório?')" rendered="#{relatorioProjeto.readOnly}"  immediate="true"/>
						<h:commandButton value="Cancelar" action="#{relatorioAcaoExtensao.cancelar}" onclick="#{confirm }" immediate="true" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
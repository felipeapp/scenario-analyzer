<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;  	
	width: 98%;
	overflow:auto;
}
</style>

<f:view>

	<h2><ufrn:subSistema /> > Relatório de Ação Acadêmica Integrada</h2>

	<h:form id="formRelatorioProjetos" enctype="multipart/form-data">
		<h:messages showSummary="true" />		
		
		<table class="formulario" width="100%">
		<caption class="listagem">CADASTRO DE ${relatorioAcaoAssociada.obj.tipoRelatorio.descricao} DE AÇÃO ACADÊMICA INTEGRADA</caption>
			
		<tr>
				<th><b>Título do Projeto:</b></th>
				<td><h:outputText	value="#{relatorioAcaoAssociada.obj.projeto.anoTitulo}"/></td>
		</tr>
		
		<tr>
				<th><b>Unidade Proponente:</b></th>
				<td><h:outputText value="#{relatorioAcaoAssociada.obj.projeto.unidade.nome}" /></td>
		</tr>
		
		<tr>
				<th><b>Fontes Financiamento:</b></th>
				<td><h:outputText value="#{relatorioAcaoAssociada.obj.projeto.fonteFinanciamentoString}" /></td>
		</tr>

		
		<tr>
				<th>
					<b>Nº Discentes Envolvidos:</b>					
				</th>
				<td>
					<h:outputText value="#{relatorioAcaoAssociada.obj.projeto.totalDiscentesEnvovidos}"/>
					&nbsp;
					<ufrn:help img="/img/ajuda.gif">Total de discentes na execução do projeto.</ufrn:help>
				</td>
		</tr>
		

		<tr>
			<td colspan="2" ><b> Existe relação objetiva entre a proposta pedagógica e a proposta do projeto? Justifique: </b><h:graphicImage url="/img/required.gif" />
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoAssociada.obj.relacaoPropostaCurso}" readonly="#{relatorioAcaoAssociada.readOnly}" id="relacaoPropostaCurso"/>
			</td>	
		</tr>


		<tr>
			<td  colspan="2" class="subFormulario"> Detalhamento das atividades desenvolvidas:</td>
		</tr>	


		<tr>
			<td colspan="2" ><b> Atividades Realizadas: </b><h:graphicImage url="/img/required.gif" />
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoAssociada.obj.atividadesRealizadas}" readonly="#{relatorioAcaoAssociada.readOnly}" id="atividades"/>
			</td>	
		</tr>

		<tr>
			<td colspan="2"><b> Resultados Obtidos: Qualitativos. </b><h:graphicImage url="/img/required.gif" />
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoAssociada.obj.resultadosQualitativos}" readonly="#{relatorioAcaoAssociada.readOnly}" id="qualitativos"/>
			</td>	
		</tr>


		<tr>
			<td colspan="2" ><b> Resultados Obtidos: Quantitativos. </b><h:graphicImage url="/img/required.gif" />
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoAssociada.obj.resultadosQuantitativos}" readonly="#{relatorioAcaoAssociada.readOnly}" id="quantitativos"/>
			</td>	
		</tr>
		
		<tr>
			<td colspan="2"  ><b> Dificuldades Encontradas: </b><h:graphicImage url="/img/required.gif" />
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoAssociada.obj.dificuldadesEncontradas}" readonly="#{relatorioAcaoAssociada.readOnly}" id="dificuldades"/>
			</td>	
		</tr>


		<tr>
			<td colspan="2" ><b> Ajustes Realizados Durante do Projeto: </b><h:graphicImage url="/img/required.gif" />
				<h:inputTextarea rows="4" style="width:99%" value="#{relatorioAcaoAssociada.obj.ajustesDuranteExecucao}" readonly="#{relatorioAcaoAssociada.readOnly}" id="ajustes"/>
			</td>	
		</tr>

		<tr>
			<td colspan="2" ><b> Público Real Atingido: </b><h:graphicImage url="/img/required.gif" />
				<h:inputText value="#{relatorioAcaoAssociada.obj.publicoRealAtingido}" readonly="#{relatorioAcaoAssociada.readOnly}" id="publicoRealAtingido" size="10" maxlength="8" onkeyup="return formatarInteiro(this)"/> pessoas
			</td>	
		</tr>
		
		<tr>
			<td  colspan="2" class="subFormulario"> Outras ações realizadas vinculadas ao projeto:</td>
		</tr>	
		<tr>	
			<td colspan="2">
				<t:dataList value="#{relatorioAcaoAssociada.outrasAcoes}" var="acao">
						<t:column>
										<f:verbatim><span style="width: 180px; display: block; float: left; "></f:verbatim>
										<h:selectBooleanCheckbox value="#{acao.selecionado}" styleClass="noborder" disabled="#{relatorioAcaoAssociada.readOnly}"/>
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
				<t:dataList value="#{ relatorioAcaoAssociada.apresentacoes }" var="apresentacao">
						<t:column>
										<f:verbatim><span style="width: 180px; display: block; float: left; "></f:verbatim>
										<h:selectBooleanCheckbox value="#{apresentacao.selecionado}" styleClass="noborder" disabled="#{relatorioAcaoAssociada.readOnly}"/>
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
						<t:dataList value="#{ relatorioAcaoAssociada.producoesGeradas }" var="prod">
								<t:column>
										<f:verbatim><span style="width: 350px; display: block; float: left; "></f:verbatim>
										<h:selectBooleanCheckbox value="#{prod.selecionado}" styleClass="noborder" disabled="#{relatorioAcaoAssociada.readOnly}"/>
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
					Se esta Ação Integrada originou um convênio ou contrato da ${ configSistema['siglaInstituicao'] } com outro órgão, informe o número deles.
				</div>
			</td>
		</tr>

		<tr>
				<th> Nº Convênio: </th>
				<td><h:inputText  id="numeroConvenio" value="#{relatorioAcaoAssociada.obj.numeroConvenio}"  readonly="#{relatorioAcaoAssociada.readOnly}" size="10" onkeyup="return(formatarInteiro(this))" maxlength="8"/></td>
		</tr>
		<tr>
				<th>Ano Convênio:</th>
				<td><h:inputText  id="anoConvenio" value="#{relatorioAcaoAssociada.obj.anoConvenio}"  readonly="#{relatorioAcaoAssociada.readOnly}" size="5"  maxlength="4" onkeyup="return(formatarInteiro(this))" /></td>
		</tr>

		<tr>
				<th>Nº Contrato:</th>
				<td><h:inputText  id="numeroContrato" value="#{relatorioAcaoAssociada.obj.numeroContrato}"  readonly="#{relatorioAcaoAssociada.readOnly}" size="10" onkeyup="return(formatarInteiro(this))" maxlength="8"/></td>
		</tr>
		<tr>
				<th>Ano Contrato:</th>
				<td><h:inputText  id="anoContrato" value="#{relatorioAcaoAssociada.obj.anoContrato}"  readonly="#{relatorioAcaoAssociada.readOnly}" size="5"  maxlength="4" onkeyup="return(formatarInteiro(this))" /></td>
		</tr>

		<tr>
			<td colspan="2" class="subFormulario"> Detalhamento de utilização dos recursos financeiros </td>
		</tr>	

		<tr>
			<td colspan="2">
				<table class="listagem">
							<tr>
								<td>
									<c:if test="${not empty relatorioAcaoAssociada.obj.detalhamentoRecursosProjeto}">
										<t:dataTable id="dt" value="#{relatorioAcaoAssociada.obj.detalhamentoRecursosProjeto}" var="consolidacao"
											 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" 
											 forceIdIndex="true">
														<t:column>
															<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
															<h:outputText value="#{consolidacao.elemento.descricao}" />
														</t:column>

														<t:column style="text-align: right;">
															<f:facet name="header"><f:verbatim><p style="text-align: right;">Interno</p></f:verbatim></f:facet>
															<f:verbatim>R$ </f:verbatim>
															<h:inputText style="text-align: right;" value="#{consolidacao.interno}"  id="fundo" size="12" maxlength="11" onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioAcaoAssociada.readOnly}">
																	<f:converter converterId="convertMoeda"/>
															</h:inputText>
														</t:column>

														<t:column style="text-align: right;">
															<f:facet name="header"><f:verbatim><p style="text-align: right;">Externo</p></f:verbatim></f:facet>
															<f:verbatim>R$ </f:verbatim>
															<h:inputText style="text-align: right;" value="#{consolidacao.externo}" id="fundacao"  size="12" maxlength="11"  onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioAcaoAssociada.readOnly}">
																	<f:converter converterId="convertMoeda"/>																							
															</h:inputText>
														</t:column>

														<t:column style="text-align: right;">
															<f:facet name="header"><f:verbatim><p style="text-align: right;">Outros</p></f:verbatim></f:facet>
															<f:verbatim>R$ </f:verbatim>
															<h:inputText style="text-align: right;" value="#{consolidacao.outros}" id="outros" size="12" maxlength="11" onkeypress="return(formataValor(this, event, 2))" readonly="#{relatorioAcaoAssociada.readOnly}" >
																	<f:converter converterId="convertMoeda"/>
															</h:inputText>
														</t:column>
										</t:dataTable>
									</c:if>
								</td>
						</tr>
							
						<c:if test="${empty relatorioAcaoAssociada.obj.detalhamentoRecursosProjeto}">
							<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
						</c:if>
							
					</table>
			</td>
		</tr>
		
		<tr>
			<td colspan="3">
				<div class="infoAltRem">
			    	<h:graphicImage value="/img/view.gif"style="overflow: visible;" styleClass="noborder"/>: Ver Arquivo	    
			    	<h:graphicImage value="/img/delete.gif"style="overflow: visible;" styleClass="noborder"/>: Remover Arquivo
				</div>
	    	</td>	    
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"> Anexar Arquivo com outros detalhes da execução da ação </td>
		</tr>	
		
		<tr>
			<th  class="required"> Descrição:</th>
			<td>
				<h:inputText  id="descricao" value="#{relatorioAcaoAssociada.descricaoArquivo}" size="60" maxlength="90" readonly="#{relatorioAcaoAssociada.readOnly}"/>
			</td>
		</tr>
		
		<tr>
			<th width="20%">Arquivo:</th>
			<td>
				<t:inputFileUpload id="uFile" value="#{relatorioAcaoAssociada.file}" storage="file" disabled="#{relatorioAcaoAssociada.readOnly}" size="50"/>
			</td>
		</tr>

		<tr>
			<td colspan="3">
				<center><h:commandButton	action="#{relatorioAcaoAssociada.anexarArquivo}" value="Anexar Arquivo" id="btAnexarArqui" disabled="#{relatorioAcaoAssociada.readOnly}"/></center>
			</td>
		</tr>
		
		<tr>
			<td colspan="2">
				<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>
				<input type="hidden" value="0" id="idArquivoRelatorio" name="idArquivoRelatorio"/>
	
				<t:dataTable id="dataTableArq" value="#{relatorioAcaoAssociada.arquivosRelatorio}" var="anexo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					<t:column  width="97%">
						<f:facet name="header"><f:verbatim>Descrição do Arquivo</f:verbatim></f:facet>
						<h:outputText value="#{anexo.descricao}" />
					</t:column>
	
					<t:column>
						<h:commandButton image="/img/delete.gif" action="#{relatorioAcaoAssociada.removeAnexo}"
							title="Remover Arquivo"  alt="Remover Arquivo"   onclick="$(idArquivo).value=#{anexo.idArquivo};$(idArquivoRelatorio).value=#{anexo.id};return confirm('Deseja Remover este Arquivo do Relatório?')" id="remArquivo" />
					</t:column>
					<t:column>
						<h:commandButton image="/img/view.gif" action="#{relatorioAcaoAssociada.viewArquivo}"
							title="Ver Arquivo"  alt="Ver Arquivo"   onclick="$(idArquivo).value=#{anexo.idArquivo};" id="viewArquivo" />
					</t:column>	
				</t:dataTable>
			</td>
		</tr>
		
		
		<tfoot>
			<tr>
				<td colspan="2">
						<h:commandButton value="Salvar (Rascunho)" action="#{relatorioAcaoAssociada.salvar}" rendered="#{!relatorioAcaoAssociada.readOnly}"/>	
						<h:commandButton value="#{relatorioAcaoAssociada.confirmButton}" action="#{relatorioAcaoAssociada.enviar}" rendered="#{!relatorioAcaoAssociada.readOnly}" /> 
						<h:commandButton value="#{relatorioAcaoAssociada.confirmButton}" action="#{relatorioAcaoAssociada.removerRelatorio}" rendered="#{relatorioAcaoAssociada.readOnly}" />
						<h:commandButton value="<< Voltar" action="#{relatorioAcaoAssociada.iniciarCadastroRelatorio}"  />
						<h:commandButton value="Cancelar" action="#{relatorioAcaoAssociada.cancelar}" onclick="#{confirm }" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
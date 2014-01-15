<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;
  	
	width: 98%;
	overflow:auto;
}
</style>


<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Valida��o de Relat�rio</h2>

	<h:form prependId="false">
		
		<table class="formulario" width="100%">
		<caption class="listagem">Valida��o de Relat�rio de Produto de Extens�o</caption>	
		
			<tr>
				<th width="25%"><b> C�digo:</b></th>
				<td>
						<h:outputText	value="#{autorizacaoDepartamento.relatorio.atividade.codigo}"/>
				</td>
			</tr>
	
			<tr>
				<th><b> T�tulo:</b></th>
				<td>
						<h:outputText	value="#{autorizacaoDepartamento.relatorio.atividade.titulo}"/>
				</td>
			</tr>
	
			<tr>
				<th><b> Coordenador(a):</b></th>
				<td>
						<h:outputText	value="#{autorizacaoDepartamento.relatorio.atividade.coordenacao.pessoa.nome}"/>
				</td>
			</tr>
	
	
			<tr>
				<th><b> Tipo de Relat�rio:</b></th>
				<td>
						<h:outputText	value="#{autorizacaoDepartamento.relatorio.tipoRelatorio.descricao}"/>
				</td>
			</tr>
			
			<tr>
				<th><b> P�blico Estimado:</b></th>
				<td>
					<h:outputText value="#{autorizacaoDepartamento.relatorio.atividade.publicoEstimado}" id="publicoEstimado"/>
				</td>	
			</tr>


			<tr>
				<th><b> P�blico Real Atendido:</b></th>
				<td>
					<h:outputText value="#{autorizacaoDepartamento.relatorio.atividade.publicoAtendido}" id="publicoAtendido"/>
				</td>	
			</tr>
						
			<tr>
				<th><b>Esta a��o foi realizada:</b></th>
				<td><h:outputText value="#{autorizacaoDepartamento.relatorio.acaoRealizada ? 'SIM' : (autorizacaoDepartamento.relatorio.acaoRealizada == null ? '-' : 'N�O')}" id="acaoFoiRealizada" /></td>	
			</tr>
		
			<tr>
				<td colspan="2" class="subFormulario"> Detalhamento das atividades desenvolvidas:</td>
			</tr>	

			<c:if test="${autorizacaoDepartamento.relatorio.acaoRealizada != null && !autorizacaoDepartamento.relatorio.acaoRealizada }">
				<tr>
					<td colspan="2" style="text-align: justify;"><b>Motivo da n�o realiza��o desta a��o:</b><br />
						<h:outputText value="#{autorizacaoDepartamento.relatorio.motivoAcaoNaoRealizada}" id="motivoNaoRealizacao" />
					</td>	
				</tr>
			</c:if>
	
			<c:choose>
				<c:when test="${ autorizacaoDepartamento.exibeNovoRelatorio }">

					<tr>
						<td colspan="2"><b> Apresenta��o em Eventos Cient�ficos: </b>
							<h:outputText value="#{autorizacaoDepartamento.relatorio.apresentacaoEventoCientifico}"/> apresenta��es.
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo sobre a apresenta��o: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{autorizacaoDepartamento.relatorio.observacaoApresentacao}"/></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2"><b> Artigos Cient�ficos produzidos a partir da a��o de extens�o: </b>
							<h:outputText value="#{autorizacaoDepartamento.relatorio.artigosEventoCientifico}" /> artigos
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo sobre o Artigo: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{autorizacaoDepartamento.relatorio.observacaoArtigo}" /></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" ><b> Outras produ��es geradas a partir da a��o de Extens�o: </b>
							<h:outputText value="#{autorizacaoDepartamento.relatorio.producoesCientifico}" /> produ��es
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo sobre a Produ��o: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{autorizacaoDepartamento.relatorio.observacaoProducao}" /></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" class="subFormulario">Informa��es do Projeto</td>
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Dificuldades Encontradas: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{autorizacaoDepartamento.relatorio.dificuldadesEncontradas}" /></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Observa��es Gerais: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{autorizacaoDepartamento.relatorio.observacoesGerais}" /></p>
						</td>	
					</tr>
								
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="2"> <b>Atividades Realizadas:</b><br/>
							<h:outputText value="#{autorizacaoDepartamento.relatorio.atividadesRealizadas}" id="atividadesRealizadas"/>
						</td>	
					</tr>
					
					<tr>	
						<td colspan="2"><b>Resultados Obtidos: Qualitativos:</b><br/>
							<h:outputText value="#{autorizacaoDepartamento.relatorio.resultadosQualitativos}" id="resultadosQualitativos"/>				
						</td>
					</tr>
					
					<tr>	
						<td colspan="2"><b>Resultados Obtidos: Quantitativos.</b><br/>
							<h:outputText value="#{autorizacaoDepartamento.relatorio.resultadosQuantitativos}" id="resultadosQuantitativos"/>				
						</td>
					</tr>
					
					<tr>
						<td colspan="2"><b> Dificuldades Encontradas:</b><br/>
							<h:outputText value="#{autorizacaoDepartamento.relatorio.dificuldadesEncontradas}" id="dificuldades"/>
						</td>	
					</tr>
					
					<tr>
						<td colspan="2"><b> Ajustes Realizados Durante a Execu��o da A��o:</b><br/>
							<h:outputText value="#{autorizacaoDepartamento.relatorio.ajustesDuranteExecucao}" id="ajustes"/>
						</td>	
					</tr>
				</c:otherwise>
			</c:choose>
			
			<tr>
				<td colspan="2" class="subFormulario"> Membros da Equipe </td>
			</tr>	
			
			
			<c:if test="${not empty autorizacaoDepartamento.relatorio.atividade.membrosEquipe}">
						<tr>
							<td colspan="2">
					
										<t:dataTable value="#{autorizacaoDepartamento.relatorio.atividade.membrosEquipe}" var="membro" 
											align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbEquipe">
													<t:column>
														<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
														<h:outputText value="#{membro.pessoa.nome}" />
													</t:column>
								
													<t:column>
														<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
														<h:outputText value="#{membro.categoriaMembro.descricao}" />
													</t:column>										
													
													<t:column>
														<f:facet name="header"><f:verbatim>Fun��o</f:verbatim></f:facet>
														<h:outputText value="<font color='red'>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
																		<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
														<h:outputText value="</font>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
													</t:column>										
													
													<t:column>
														<f:facet name="header"><f:verbatim>Departamento</f:verbatim></f:facet>
														<h:outputText value="#{membro.servidor.unidade.sigla}" rendered="#{not empty membro.servidor}" />
													</t:column>
										</t:dataTable>
							</td>
					</tr>
			</c:if>
			
			
			<tr>
					<td colspan="2" class="subFormulario">Lista de Arquivos</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>				
					<t:dataTable id="dataTableArq" value="#{autorizacaoDepartamento.relatorio.arquivos}" var="anexo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
						<t:column  width="97%">
							<h:outputText value="#{anexo.descricao}" />
						</t:column>
		
						<t:column>
							<h:commandButton image="/img/view.gif" action="#{relatorioProjeto.viewArquivo}"
								title="Ver Arquivo"  alt="Ver Arquivo"   onclick="$(idArquivo).value=#{anexo.idArquivo};" id="viewArquivo" />
						</t:column>	
					</t:dataTable>
				</td>
			</tr>
			
			<c:if test="${empty autorizacaoDepartamento.relatorio.arquivos}">
					<tr><td colspan="6" align="center"><font color="red">N�o h� arquivos adicionados ao relat�rio</font> </td></tr>
			</c:if>
			
			<tr>
				<td colspan="2" class="subFormulario"> Detalhamento de utiliza��o dos recursos financeiros </td>
			</tr>	
	
			<tr>
				<td colspan="2">
					<table class="listagem">
						<thead>
										<tr>
											<td>
														<c:if test="${not empty autorizacaoDepartamento.relatorio.detalhamentoRecursos}">
																		<t:dataTable id="dt" value="#{autorizacaoDepartamento.relatorio.detalhamentoRecursos}" var="consolidacao"
																		 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" 
																		 forceIdIndex="true">
																					<t:column>
																						<f:facet name="header"><f:verbatim>Descri��o</f:verbatim></f:facet>
																						<h:outputText value="#{consolidacao.elemento.descricao}" />
																					</t:column>
	
																					<t:column>
																						<f:facet name="header"><f:verbatim>FAEx (Interno)</f:verbatim></f:facet>
																						<f:verbatim>R$ </f:verbatim>
																						<h:outputText value="#{consolidacao.faex}"/>
																					</t:column>
	
																					<t:column>
																						<f:facet name="header"><f:verbatim>Funpec</f:verbatim></f:facet>
																						<f:verbatim>R$ </f:verbatim>
																						<h:outputText value="#{consolidacao.funpec}"/>
																					</t:column>
	
	
																					<t:column>
																						<f:facet name="header"><f:verbatim>Outros (Externo)</f:verbatim></f:facet>
																						<f:verbatim>R$ </f:verbatim>
																						<h:outputText value="#{consolidacao.outros}" />
																					</t:column>
																	</t:dataTable>
														</c:if>
											</td>
									</tr>
								</thead>
								
							<c:if test="${empty autorizacaoDepartamento.relatorio.detalhamentoRecursos}">
								<tr><td colspan="6" align="center"><font color="red">N�o h� itens de despesas cadastrados</font> </td></tr>
							</c:if>
								
						</table>
				</td>
			</tr>	
	
	

			<tr>
				<td colspan="2"><br/><br/></td>
			</tr>	

			
			<tr>
				<td colspan="2" class="subFormulario"> Valida��o do Relat�rio </td>
			</tr>	
	
	
	
			<tr>
					<th class="required">Parecer:</th>
					<td>	
						<h:selectOneMenu id="tipoParecerDepartamento"	value="#{autorizacaoDepartamento.relatorio.tipoParecerDepartamento.id}" 
							onchange="setarRequiredJustificativa();" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA OP��O --"/>
							<f:selectItem itemValue="1" itemLabel="APROVAR"/>
							<f:selectItem itemValue="2" itemLabel="APROVAR COM RECOMENDA��O"/>
							<f:selectItem itemValue="3" itemLabel="N�O APROVAR"/>
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Em caso de n�o aprova��o do relat�rio a justificativa deve ser informada obrigatoriamente.</ufrn:help>
						<br/>
					</td>
			</tr>		
			<tr>			
					<th id="justificativa" valign="top">Justificativa: </th>
					<td>
						<h:inputTextarea  id="parecerDepartamento" value="#{autorizacaoDepartamento.relatorio.parecerDepartamento}" rows="5" style="width: 95%"/>
					</td>
			</tr>
	
			
			<tfoot>
				<tr>
					<td colspan="2">
						<input type="hidden" value="true" name="relatorio" id="relatorio"/>
						<h:commandButton id="confirmar" value="Confirmar Valida��o" action="#{autorizacaoDepartamento.autorizarRelatorio}" /> 
						<h:commandButton id="cancelar" value="Cancelar" action="#{autorizacaoDepartamento.cancelar}" onclick="#{confirm }" />
					</td>
				</tr>
			</tfoot>
	</table>
</h:form>
</f:view>
<script type="text/javascript">

	function setarRequiredJustificativa(){
		if ( $("tipoParecerDepartamento").value != "1"){ 
			$("justificativa").className="required"
		} else {
			$("justificativa").className=""
		}
	}

	window.onload = setarRequiredJustificativa();
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;
  	
	width: 98%;
	overflow:auto;
}
</style>


<f:view>
	<h2>Visualização de Relatório</h2>

	<h:form id="formRelatorioProjetos">
		
		<h3 class="tituloTabelaRelatorio"> RELATÓRIO DE AÇÕES INTEGRADAS </h3>
		<table class="tabelaRelatorio" width="100%">

		<tr>
			<th><b>Projeto:</b></th>
			<td>
					<h:outputText	value="#{relatorioAcaoAssociada.obj.projeto.anoTitulo}"/>
			</td>
		</tr>
		
		<tr>
			<th><b> Área:</b></th>
			<td>
					<h:outputText	value="#{relatorioAcaoAssociada.obj.projeto.areaConhecimentoCnpq.nome}"/>
			</td>
		</tr>

		<tr>
			<th><b> Coordenador(a):</b></th>
			<td>
					<h:outputText	value="#{relatorioAcaoAssociada.obj.projeto.coordenador.pessoa.nome}"/> 
					<c:if test="${relatorioAcaoAssociada.obj.projeto.coordenador.pessoa.email != null }">
						<h:outputText	value=" - #{relatorioAcaoAssociada.obj.projeto.coordenador.pessoa.email}"/>
					</c:if>
			</td>
		</tr>


		<tr>
			<th><b> Tipo de Relatório:</b></th>
			<td>
					<h:outputText	value="#{relatorioAcaoAssociada.obj.tipoRelatorio.descricao}"/>
			</td>
		</tr>
		
		<tr>
			<th><b> Período da Ação:</b></th>
			<td>
				<fmt:formatDate value="${relatorioAcaoAssociada.obj.projeto.dataInicio}" pattern="dd/MM/yyyy" />  
				a 
				<fmt:formatDate value="${relatorioAcaoAssociada.obj.projeto.dataFim}" pattern="dd/MM/yyyy" />
				</td>
		</tr>
		
		<tr>
			<th><b> Público Atingido:</b></th>
			<td>
				<h:outputText value="#{relatorioAcaoAssociada.obj.publicoRealAtingido}" id="publicoRealAtingido"/> pessoas
			</td>	
		</tr>

		<tr>
			<th><b> Data do Cadastro:</b></th>
			<td><fmt:formatDate value="${relatorioAcaoAssociada.obj.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
		</tr>
		
		
		<tr>
			<th><b> Data do Envio:</b></th>
			<td>
				<fmt:formatDate value="${relatorioAcaoAssociada.obj.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss" />
				<h:outputText value="<font color=red>CADASTRO EM ANDAMENTO<font>" rendered="#{relatorioAcaoAssociada.obj.dataEnvio == null}" escape="false" />
			</td>
		</tr>
		
		<tr>
			<th><b> Financiamento Interno:</b></th>
			<td>
					<c:if test="${relatorioAcaoAssociada.obj.projeto.financiamentoExterno}">
						<h:outputText	value="NÃO"/>
					</c:if>
					<c:if test="${!relatorioAcaoAssociada.obj.projeto.financiamentoExterno}">
						<h:outputText	value="SIM"/>
					</c:if>
			</td>
		</tr>
		
		<tr>
			<td  colspan="2"></td>
		</tr>
		
		<tr>
			<td  colspan="2" style="font-weight: bold; background-color: #DEDFE3"> Detalhamento das atividades desenvolvidas:</td>
		</tr>	

		<tr>
			<td colspan="2" style="text-align: justify;"> <b>Existe relação objetiva entre a proposta e a proposta do projeto ? Justifique:</b><br/>
				<h:outputText value="#{relatorioAcaoAssociada.obj.relacaoPropostaCurso}" id="relacaoPropostaCurso"/>
			</td>	
		</tr>
		
		<tr>	
			<td colspan="2"><b>Outras ações realizadas vinculadas ao projeto:</b><br/>
				<t:dataList value="#{relatorioAcaoAssociada.obj.outrasAcoesProjeto}" var="acao">
						<t:column>
								<h:outputText value="#{acao.descricao}, " escape="false" />
						</t:column>
				 </t:dataList>
			</td>
		</tr>
		
		<tr>	
			<td colspan="2"><b>Apresentação do projeto em eventos de extensão:</b><br/>
				<t:dataList value="#{ relatorioAcaoAssociada.obj.apresentacaoProjetos }" var="apresentacao">
						<t:column>
								<h:outputText value="#{apresentacao.descricao}, " escape="false" />
						</t:column>
				</t:dataList>
			</td>
		</tr>
		
		<tr>	
			<td colspan="2"><b> Produção acadêmica gerada:</b><br/>
					<t:dataList value="#{ relatorioAcaoAssociada.obj.producoesGeradas }" var="prod">
							<t:column>
									<h:outputText value="#{prod.descricao}, " escape="false"/>
							</t:column>
					 </t:dataList>
			</td>
		</tr>

		<tr>
			<td colspan="2" style="text-align: justify;"><b> Atividades Realizadas: </b><br/>
				<h:outputText value="#{relatorioAcaoAssociada.obj.atividadesRealizadas}" id="atividades"/>
			</td>	
		</tr>


		<tr>
			<td colspan="2" style="text-align: justify;"><b> Resultados Obtidos: Qualitativos.</b><br/>
				<h:outputText  value="#{relatorioAcaoAssociada.obj.resultadosQualitativos}" id="qualitativos"/>
			</td>	
		</tr>


		<tr>
			<td colspan="2" style="text-align: justify;"><b> Resultados Obtidos: Quantitativos.</b><br/>
				<h:outputText value="#{relatorioAcaoAssociada.obj.resultadosQuantitativos}" id="quantitativos"/>
			</td>	
		</tr>


		<tr>
			<td colspan="2" style="text-align: justify;"><b> Dificuldades Encontradas:</b><br/>
				<h:outputText value="#{relatorioAcaoAssociada.obj.dificuldadesEncontradas}" id="dificuldades"/>
			</td>	
		</tr>


		<tr>
			<td colspan="2" style="text-align: justify;"><b> Ajustes Realizados:</b><br/>
				<h:outputText value="#{relatorioAcaoAssociada.obj.ajustesDuranteExecucao}" id="ajustes"/>
			</td>	
		</tr>
		
		<tr>
			<td  colspan="2"></td>
		</tr>
		
		<tr>
			<td colspan="2" style="font-weight: bold; background-color: #DEDFE3"> Membros da Equipe </td>
		</tr>	
		
		
		<c:if test="${not empty relatorioAcaoAssociada.obj.projeto.equipe}">
					<tr>
						<td colspan="2">
				
									<t:dataTable value="#{relatorioAcaoAssociada.obj.projeto.equipe}" var="membro" 
										align="center" width="100%" styleClass="tabelaRelatorio" rowClasses="linhaPar, linhaImpar" id="tbEquipe">
												<t:column>
													<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
													<h:outputText value="#{membro.pessoa.nome}" />
												</t:column>
							
												<t:column>
													<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
													<h:outputText value="#{membro.categoriaMembro.descricao}" />
												</t:column>										
												
												<t:column>
													<f:facet name="header"><f:verbatim>Função</f:verbatim></f:facet>
													<h:outputText value="<font color='red'>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
																	<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
													<h:outputText value="</font>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
												</t:column>										
												
												<t:column>
													<f:facet name="header"><f:verbatim>Departamento</f:verbatim></f:facet>
													<h:outputText value="#{membro.servidor.unidade.sigla}" rendered="#{not empty membro.servidor}" />
												</t:column>
												<t:column>
													<f:facet name="header">
														<f:verbatim><center>Início</center></f:verbatim>
													</f:facet>
													<h:outputText value="#{membro.dataInicio}" style="text-align: center" />
												</t:column>
					
												<t:column>
													<f:facet name="header">
														<f:verbatim><center>Fim</center></f:verbatim>
													</f:facet>
													<h:outputText value="#{membro.dataFim}" style="text-align: center" />
												</t:column>											
												
									</t:dataTable>
						</td>
				</tr>
		</c:if>
		
		<tr>
			<td  colspan="2"></td>
		</tr>
		
		<tr>
				<td colspan="2" style="font-weight: bold; background-color: #DEDFE3">Lista de Arquivos</td>
		</tr>
		
		<tr>
			<td colspan="2">
				<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>			
				<t:dataTable id="dataTableArq" value="#{relatorioAcaoAssociada.obj.arquivos}" var="anexo" align="center" width="100%" styleClass="tabelaRelatorio" rowClasses="linhaPar, linhaImpar">
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
		
		<c:if test="${empty relatorioAcaoAssociada.obj.arquivos}">
				<tr><td colspan="6" align="center"><font color="red">Não há arquivos adicionados ao relatório</font> </td></tr>
		</c:if>
		
		<tr>
			<td  colspan="2"></td>
		</tr>
		
		<tr>
			<td colspan="2" style="font-weight: bold; background-color: #DEDFE3"> Detalhamento de utilização dos recursos financeiros </td>
		</tr>	

		<tr>
			<td colspan="2">
				<table class="listagem">
					<thead>
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

																				<t:column style="text-align: right">
																					<f:facet name="header"><f:verbatim><p style="text-align: right;">Interno</p></f:verbatim></f:facet>
																					<f:verbatim>R$ </f:verbatim>
																					<h:outputText value="#{consolidacao.interno}"/>
																				</t:column>

																				<t:column style="text-align: right">
																					<f:facet name="header"><f:verbatim><p style="text-align: right;">Externo</p></f:verbatim></f:facet>
																					<f:verbatim>R$ </f:verbatim>
																					<h:outputText value="#{consolidacao.externo}"/>
																				</t:column>


																				<t:column style="text-align: right">
																					<f:facet name="header"><f:verbatim><p style="text-align: right;">Outros</p></f:verbatim></f:facet>
																					<f:verbatim>R$ </f:verbatim>
																					<h:outputText value="#{consolidacao.outros}" />
																				</t:column>
																</t:dataTable>
													</c:if>
										</td>
								</tr>
							</thead>
							
						<c:if test="${empty relatorioAcaoAssociada.obj.detalhamentoRecursosProjeto}">
							<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
						</c:if>
							
					</table>
			</td>
		</tr>
		
		
		<tr>
			<td  colspan="2"></td>
		</tr>
		
<!--		<tr>-->
<!--				<td colspan="2" style="font-weight: bold; background-color: #DEDFE3">Validação do Departamento</td>-->
<!--		</tr>-->
<!---->
<!---->
<!--		<tr>-->
<!--				<th width="20%"><b>Departamento:</b></th>-->
<!--				<td>	-->
<!--					<h:outputText value="#{relatorioAcaoAssociada.obj.projeto.unidade.nome}"/>-->
<!--				</td>-->
<!--		</tr>		-->
<!---->
<!--		<tr>-->
<!--				<th><b>Data Análise:</b></th>-->
<!--				<td>	-->
<!--					<fmt:formatDate value="${relatorioAcaoAssociada.obj.dataValidacaoDepartamento}" pattern="dd/MM/yyyy HH:mm:ss"/>-->
<!--				</td>-->
<!--		</tr>		-->
<!---->
<!--		<tr>-->
<!--				<th><b>Parecer Depto.:</b></th>-->
<!--				<td>	-->
<!--					<h:outputText id="tipoParecerDepto"	value="#{relatorioAcaoAssociada.obj.tipoParecerDepartamento != null ? relatorioAcaoAssociada.obj.tipoParecerDepartamento.descricao : 'NÃO ANALISADO' }"/>-->
<!--				</td>-->
<!--		</tr>		-->
<!--		<tr>			-->
<!--				<th><b>Justificativa: </b></th>-->
<!--				<td style="text-align: justify;">-->
<!--					<h:outputText  id="parecerDepartamento" value="#{relatorioAcaoAssociada.obj.parecerDepartamento}"/>-->
<!--				</td>-->
<!--		</tr>-->


		<tr>
				<td colspan="2" style="font-weight: bold; background-color: #DEDFE3">Validação do Comitê</td>
		</tr>


		<tr>
				<th><b>Data Análise:</b></th>
				<td>	
					<fmt:formatDate value="${relatorioAcaoAssociada.obj.dataValidacaoComite}" pattern="dd/MM/yyyy HH:mm:ss"/>
				</td>
		</tr>		

		<tr>
				<th><b>Parecer Comitê:</b></th>
				<td>	
					<h:outputText id="tipoParecerComite"	value="#{relatorioAcaoAssociada.obj.tipoParecerComite != null ? relatorioAcaoAssociada.obj.tipoParecerComite.descricao : 'NÃO ANALISADO' }"/>
				</td>
		</tr>		
		<tr>			
				<th><b>Justificativa: </b></th>
				<td style="text-align: justify;">
					<h:outputText  id="parecerComite" value="#{relatorioAcaoAssociada.obj.parecerComite}"/>
				</td>
		</tr>

		
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
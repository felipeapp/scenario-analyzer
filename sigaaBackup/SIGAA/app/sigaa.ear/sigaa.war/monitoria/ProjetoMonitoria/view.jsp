<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>

<f:view>
	<h2>VISUALIZAÇÃO DO PROJETO DE ENSINO</h2>

	<h:outputText value="#{avalProjetoMonitoria.create}"/>
	<h:outputText value="#{projetoMonitoria.create}"/>
	
	<c:set var="PROJETO_MONITORIA" 	value="<%= String.valueOf(TipoProjetoEnsino.PROJETO_DE_MONITORIA) %>" 	scope="application"/>
	
<h:form>
	<table class="tabelaRelatorio" width="100%">
		<caption class="listagem"> Dados do Projeto de Ensino </caption>

		<tbody>
			<tr>
				<th width="25%">Título do Projeto:</th>
				<td><h:outputText value="#{projetoMonitoria.obj.titulo}"/></td>
			</tr>
			<tr>
				<th><b> Tipo de Projeto: </b></th>
				<td><h:outputText value="#{projetoMonitoria.obj.tipoProjetoEnsino.descricao}"/></td>
			</tr>
			<tr>
				<th><b> Ano de Referência: </b></th>
				<td><h:outputText value="#{projetoMonitoria.obj.ano}"/> </td>
			</tr>
			<tr>
				<th><b> Data de Inicio: </b></th>
				<td><h:outputText value="#{projetoMonitoria.obj.projeto.dataInicio}"/> </td>
			</tr>
			<tr>
				<th><b> Data de Fim: </b></th>
				<td><h:outputText value="#{projetoMonitoria.obj.projeto.dataFim}"/> </td>
			</tr>
			
			<c:if test="${projetoMonitoria.obj.projetoPAMQEG && empty projetoMonitoria.obj.editalMonitoria}">
				<tr>
					<th><b> Valor do Financiamento: </b></th>
					<td> R$
						<h:outputText value="#{projetoMonitoria.obj.valorFinanciamento}"> 
							<f:convertNumber pattern="###,##0.00"/>
						</h:outputText>					
					</td>
				</tr>
			</c:if>

			<c:if test="${ empty projetoMonitoria.obj.editalMonitoria && ( projetoMonitoria.obj.projetoMonitoria || projetoMonitoria.obj.ambosProjetos)}">
	
					<tr>
						<th><b> Bolsas Solicitadas: </b></th>
						<td><h:outputText value="#{projetoMonitoria.obj.bolsasSolicitadas}"/> </td>
					</tr>
				
				<c:if test="${projetoMonitoria.obj.editalMonitoria.resultadoFinalPublicado}">
					<tr>		
						<th><b> Bolsas Concedidas: </b></th>
						<td><h:outputText value="#{projetoMonitoria.obj.bolsasConcedidas}"/> </td>
					</tr>
	
					<tr>		
						<th>
							<c:set var="NAO_RECOMENDADO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_NAO_RECOMENDADO) %>" scope="application"/>				
							<c:set var="NAO_AUTORIZADO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_NAO_AUTORIZADO_PELOS_DEPARTAMENTOS_ENVOLVIDOS) %>" scope="application"/>								
							<b> Bolsas Não Remuneradas: </b>
						</th>
						<td>
							 <c:if test="${(projetoMonitoria.obj.situacaoProjeto.id == NAO_RECOMENDADO) or (projetoMonitoria.obj.situacaoProjeto.id == NAO_AUTORIAZADO)}">0</c:if>
							 <c:if test="${(projetoMonitoria.obj.situacaoProjeto.id != NAO_RECOMENDADO) and (projetoMonitoria.obj.situacaoProjeto.id != NAO_AUTORIAZADO)}">
							 	<h:outputText value="#{projetoMonitoria.obj.bolsasSolicitadas - projetoMonitoria.obj.bolsasConcedidas}"/> 
							 </c:if>
						</td>
					</tr>
				</c:if>				
					
			</c:if>

			<c:if test="${projetoMonitoria.mostarDocentes}">
				<tr>
					<th><b> Coordenador(a): </b></th>
					<td><h:outputText value="#{projetoMonitoria.obj.projeto.coordenador.pessoa.nome}"/> </td>
				</tr>
				<tr>
					<th><b> E-Mail do Projeto: </b></th>
					<td><h:outputText value="#{projetoMonitoria.obj.projeto.coordenador.pessoa.email}"/> </td>
				</tr>
				
			</c:if>
		
			<c:if test="${ empty projetoMonitoria.obj.editalMonitoria }">
				<tr>		
					<th><b> Edital: </b></th>
					<td><h:outputText value="#{projetoMonitoria.obj.editalMonitoria}"/></td>
				</tr>
			</c:if>

			<tr>		
				<th><b> Centro: </b></th>
				<td><h:outputText value="#{projetoMonitoria.obj.unidade}"/> </td>
			</tr>
			<tr>
				<th><b> Situação: </b></th>
				<td><h:outputText value="#{projetoMonitoria.obj.situacaoProjeto.descricao}"/></td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Detalhes do Projeto</td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Resumo do Projeto: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.resumo}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Justificativa e Diagnóstico: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.justificativa}" escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Objetivos (geral e específico): </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.objetivos}" escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Metodologia de Desenvolvimento do Projeto: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.metodologia}" escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Resultados Esperados: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.resultados}" escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Produtos que resultam da execução do projeto: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.produto}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Avaliação do Desenvolvimento do Projeto: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.avaliacao}" escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Processo Seletivo: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.processoSeletivo}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Referêcias:  Ref. Bibliográficas do projeto, etc.: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.referencias}" escape="false"/> </td>
			</tr>
				
			<tr>
				<td colspan="2"><br/></td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Componentes Curriculares e Planos de Trabalho</td>
			</tr>
			
			<tr>
				<td colspan="2">
		
					<t:dataTable value="#{projetoMonitoria.obj.componentesCurriculares}" var="comp" align="center" width="100%">
						<t:column>
								<f:verbatim><b>Componente Curricular: </b></f:verbatim>
								<h:outputText value="#{comp.disciplina.codigoNome}" />
								<f:verbatim><br/><b>Previsão de Oferta:</b></f:verbatim>
								<h:outputText value="  1º Período Letivo   " rendered="#{comp.semestre1}"/>	
								<h:outputText value="  2º Período Letivo" rendered="#{comp.semestre2}"/>	
								<br/>
								<f:verbatim><b> Carga-horária semanal destinada ao projeto: </b></f:verbatim>
								<p><f:verbatim><i><h:outputText value="#{comp.chDestinadaProjeto}" /></i></f:verbatim></p>
								<f:verbatim><b> Atividades desenvolvidas pelo monitor: </b></f:verbatim>
								<p><f:verbatim><i><h:outputText value="#{comp.planoTrabalho}"  escape="false"/></i></f:verbatim></p>
								<f:verbatim><b> Avaliação do Monitor: </b></f:verbatim>
								<p><f:verbatim><i><h:outputText value="#{comp.avaliacaoMonitor}"  escape="false"/></i></f:verbatim></p>
								
								<f:verbatim></div><hr/></f:verbatim>														
						</t:column>
					</t:dataTable>
					<center>
						<c:if test="${empty projetoMonitoria.obj.componentesCurriculares}">
								<font color="red">Não há componentes curriculares cadastrados</font>
						</c:if>
					</center>				
				</td>
			</tr>
		
			<tr>
				<td colspan="2" class="subFormulario">Docentes Envolvidos no Projeto</td>
			</tr>

			<tr>
				<td colspan="2">
				
					<c:set var="ASSUMIU_MONITORIA" 	value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" 	scope="application"/>
					<c:set var="CONVOCADO" 	value="<%= String.valueOf(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA) %>" 	scope="application"/>

					<t:dataTable value="#{projetoMonitoria.obj.equipeDocentes}" var="docente" width="100%" rendered="#{projetoMonitoria.mostarDocentes}">	
						<t:column>
							<h:outputText value="<font color='#eee'>" rendered="#{! docente.ativo}" escape="false"/>
								<f:facet name="header">
									<f:verbatim>Docente</f:verbatim>
								</f:facet>
								<h:outputText value="#{docente.servidor.siapeNome}" />
						</t:column>
						<t:column>
							<f:facet name="header">
								<f:verbatim>Vínculo</f:verbatim>
							</f:facet>
							<h:outputText value="<font color='red'>COORDENADOR(A) </font>" rendered="#{docente.coordenador}" escape="false" />
							<h:outputText value="ORIENTADOR(A)" rendered="#{!docente.coordenador}" escape="false"/>
						</t:column>
						<t:column style="text-align: center;">
								<f:facet name="header">
									<f:verbatim><center>Data Início</center></f:verbatim>
								</f:facet>
								<h:outputText value="#{docente.dataEntradaProjeto}" />
						</t:column>										
						<t:column style="text-align: center;">
								<f:facet name="header">
									<f:verbatim><center>Data Fim</center></f:verbatim>
								</f:facet>
								<h:outputText value="#{docente.dataSaidaProjeto}" rendered="#{docente.dataSaidaProjeto != null}"/>
								<h:outputText value="#{projetoMonitoria.obj.projeto.dataFim}" rendered="#{docente.dataSaidaProjeto == null}"/>
						</t:column>
					</t:dataTable>					
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Discentes Envolvidos no Projeto</td>
			</tr>

			<tr>
				<td colspan="2">
				
					<c:set var="ASSUMIU_MONITORIA" 	value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" 	scope="application"/>
					<c:set var="CONVOCADO" 	value="<%= String.valueOf(SituacaoDiscenteMonitoria.CONVOCADO_MONITORIA) %>" 	scope="application"/>

					<t:dataTable value="#{projetoMonitoria.obj.discentesMonitoria}" var="discente" width="100%" >	
						<t:column>
								<f:facet name="header">
									<f:verbatim>Discente</f:verbatim>
								</f:facet>
								<h:outputText value="#{discente.discente.discente.matriculaNome}" />
						</t:column>
						<t:column>
								<f:facet name="header">
									<f:verbatim>Vínculo</f:verbatim>
								</f:facet>
								<h:outputText value="#{discente.tipoVinculo.descricao}" />
						</t:column>
						<t:column style="text-align: center;" width="15%">
								<f:facet name="header">
									<f:verbatim><center>Data Início</center></f:verbatim>
								</f:facet>
								<h:outputText value="#{discente.dataInicio}" />
						</t:column>
						<t:column style="text-align: center;" width="15%">
								<f:facet name="header">
									<f:verbatim><center>Data Fim &nbsp;&nbsp;&nbsp; </center></f:verbatim>
								</f:facet>
								<h:outputText value="#{discente.dataFim}" /> 
						</t:column>									
					</t:dataTable>					
				</td>
			</tr>
	
			<c:if test="${not empty projetoMonitoria.obj.orcamentosDetalhados}">
					<tr>
						<td colspan="2">
							<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Orçamento Detalhado</h3>			
							<table class="listagem" width="99%">
								<thead>
								<tr>
									<th>Descrição</th>
									<th style="text-align: right"  width="15%">Licitado</th>
									<th style="text-align: right"  width="15%">Valor Unitário </th>
									<th style="text-align: right"  width="10%">Quant.</th>
									<th style="text-align: right" width="15%">Valor Total </th>
								</tr>
								</thead>

								<tbody>

									<c:if test="${not empty projetoMonitoria.tabelaOrcamentaria}">
									
										<c:set value="${projetoMonitoria.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
										<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">
												
												<tr  style="background: #EFF0FF; font-weight: bold; padding: 2px 0 2px 5px;">
													<td colspan="5">${ tabelaOrc.key.descricao }</td>
												</tr>
														<c:set value="#{tabelaOrc.value.orcamentos}" var="orcamentos" />
														<c:forEach items="#{orcamentos}" var="orcamento" varStatus="status">
															<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
																<td style="padding-left: 20px"> ${orcamento.discriminacao}</td>
																<td style="text-align:right">${orcamento.materialLicitado ? 'Sim' : 'Não'}</td>
																<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorUnitario}" type="currency" />  </td>
																<td align="right">${orcamento.quantidade}</td>
																<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorTotal}" type="currency"/>  </td>
															</tr>
														</c:forEach>

												<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
													<td colspan="3">SUB-TOTAL (${ tabelaOrc.key.descricao})</td>
													<td  align="right">${ tabelaOrc.value.quantidadeTotal }</td>
													<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${ tabelaOrc.value.valorTotalRubrica }" type="currency"/></td>
												</tr>

												<tr>
													<td colspan="5">&nbsp;</td>
												</tr>

										</c:forEach>
									</c:if>

										<c:if test="${empty projetoMonitoria.obj.orcamentosDetalhados}">
											<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
										</c:if>

								</tbody>
							</table>
						</td>
					</tr>	
				</c:if>
			
			<c:if test="${not empty projetoMonitoria.obj.fotos}">
			
				<tr>
					<td colspan="2">
						<div class="infoAltRem">
							<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Fotos	    							    		
						</div>
					</td>
				</tr>
	
				<tr>
					<td colspan="2">
						<input type="hidden" value="0" id="idFotoOriginal" name="idFotoOriginal"/>
						<input type="hidden" value="0" id="idFotoMini" name="idFotoMini"/>
						<input type="hidden" value="0" id="idFotoProjeto" name="idFotoProjeto"/>
	
						<t:dataTable id="dataTableFotos" value="#{projetoMonitoria.fotosProjeto}" var="foto" align="center" 
									 width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	
							<t:column>
								<f:facet name="header"><f:verbatim>Foto</f:verbatim></f:facet>
									<f:verbatim >
										<div class="foto">							
											<h:graphicImage url="/verFoto?idFoto=#{foto.idFotoMini}&key=#{sf:generateArquivoKey(foto.idFotoMini)}" width="70" height="70"/>
										</div>
									</f:verbatim>
							</t:column>
	
							<t:column>
								<f:facet name="header"><f:verbatim>Descrição da Foto</f:verbatim></f:facet>
								<h:outputText value="#{foto.descricao}" />
							</t:column>
	
	
							<t:column width="5%">
								<h:outputLink value="/sigaa/verFoto?idFoto=#{foto.idFotoOriginal}&key=#{sf:generateArquivoKey(foto.idFotoOriginal)}" id="link_verfoto_original_" title="Visualizar foto Original">
									<h:graphicImage url="/img/view.gif" />
								</h:outputLink>						
							</t:column>
					
						</t:dataTable>
					</td>
				</tr>
			</c:if>
			
				<c:if test="${not empty projetoMonitoria.obj.arquivos}">
					<tr>
						<td colspan="2">
							<div class="infoAltRem">
								<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Arquivo	    							    		
							</div>
						</td>		
					</tr>
					
					<tr>
						<td colspan="2">
							<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Arquivos</h3>
							<t:dataTable value="#{projetoMonitoria.obj.arquivos}" var="arquivo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbArquivo">
									<t:column>
										<f:facet name="header">
											<f:verbatim>Descrição Arquivo</f:verbatim></f:facet>
											<h:outputText value="#{arquivo.descricao}" />
									</t:column>			
									<t:column width="5%">
										<h:outputLink value="/sigaa/verProducao?idProducao=#{arquivo.idArquivo}&key=#{ sf:generateArquivoKey(arquivo.idArquivo) }" title="Visualizar Arquivo">
											<h:graphicImage url="/img/view.gif" /> 
										</h:outputLink>
									</t:column>
							</t:dataTable>
						</td>
					</tr>	
				</c:if>
			
			<tr>
				<td colspan="2" class="subFormulario">Ações das quais o projeto faz parte</td>
			</tr>
		
			<tr>
				<td colspan="2">
							
							<c:if test="${projetoMonitoria.obj.projetoAssociado}">
								<table align="center" width="100%" class="listagem" id="tbProjetoAssociado">
									<thead>
										<tr>
											<th>Título</th>
											<th>Tipo</th>
											<th></th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td><h:outputText value="#{projetoMonitoria.obj.projeto.titulo}" /></td>
											<td>AÇÃO ACADÊMICA ASSOCIADA</td>
											<td>
												<h:commandLink title="Visualizar" action="#{ projetoBase.view }" immediate="true">
														<f:param name="id" value="#{projetoMonitoria.obj.projeto.id}" />
														<h:graphicImage value="/img/view.gif" style="overflow: visible;" />
												</h:commandLink>
											</td>
										</tr>										
									</tbody>										
								</table>
							</c:if> 
							
							<c:if test="${not projetoMonitoria.obj.projetoAssociado}">
								<center><font color="red">Este projeto não faz parte de uma ação acadêmica associada</font></center>
							</c:if>
				</td>
			</tr>	
			
			<tr>
				<td colspan="2" class="subFormulario">Lista de Departamentos Envolvidos na Autorização do Projeto</td>
			</tr>
		
			<tr>
				<td colspan="2">
				
				
					<table class="listagem">
					      <thead>
					      	<tr>
								<th>Departamento</th>
					        	<th style="text-align: center">Data/Hora Autorização</th>
					       		<th>Situação</th>
					        </tr>
					      </thead>
					        
						
						<c:if test="${not empty projetoMonitoria.obj.autorizacoesProjeto}">		
							<tbody>
						        <c:forEach items="${projetoMonitoria.obj.autorizacoesProjeto}" var="auto" varStatus="status">
		    		    	       <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
							            <td> ${auto.unidade.nome} </td>
							            <td style="text-align: center"> <fmt:formatDate value="${auto.dataAutorizacao}"  pattern="dd/MM/yyyy HH:mm:ss" var="data"/>${data} <c:if test="${empty auto.dataAutorizacao}">-</c:if></td>
		
			
							            <c:if test="${not empty auto.dataAutorizacao}">
			   								<td> ${(auto.autorizado == true ? 'Autorizado': 'NÃO Autorizado')} </td>
			   							</c:if>
		
							            <c:if test="${empty auto.dataAutorizacao}">
			   								<td> Pendente </td>
			   							</c:if>
		    		    	       		
		        			       </tr>
								</c:forEach>
							</tbody>
						</c:if>
						
						<c:if test="${empty projetoMonitoria.obj.autorizacoesProjeto}">
					        <tbody>
			                    <tr> <td colspan="3" align="center"> <font color="red">Projeto ainda não foi enviado aos departamentos envolvidos!</font> </td></tr>
							</tbody>		
						</c:if>
						
					</table>
					
					<br>
					<br/>
		
						
				</td>
			</tr>
			
			
			
			
			
				<tr>
					<td colspan="2" class="subFormulario">Histórico do Projeto</td>
				</tr>
				
				<tr>
					<td colspan="2">
						<table class="listagem" width="100%" id="dtHistorico">
							<thead>
								<tr>
									<th style="text-align: center">Data/Hora</th>
									<th style="text-align: left">Situação</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{projetoMonitoria.obj.projeto.historicoSituacao}" var="historico" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										
										<td style="text-align: center">
											<h:outputText value="#{historico.data}">
												<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/>
											</h:outputText>
										</td>
										<td><h:outputText value="#{historico.projeto.situacaoProjeto.descricao}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					
						<c:if test="${empty projetoMonitoria.obj.projeto.historicoSituacao}">
							<tr>
								<td colspan="6" align="center"><font color="red">Não há registros sobre o histórico do projeto</font></td>
							</tr>
						</c:if>
					</td>
				</tr>		
		</tbody>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
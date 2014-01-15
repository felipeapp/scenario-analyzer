<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
.button{
   padding:0 .25em 0 .25em;
   width:auto;
   overflow:visible;
}
</style>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h2><ufrn:subSistema /> > Resumo do Projeto de Ensino</h2>

	<div class="descricaoOperacao">	
		<table>
		<tr>
			<td width="5%"><html:img page="/img/warning.gif"/> </td>
			<td style="text-align: justify;">
				<p style="text-align: justify;width:70%px" >
					<b>Aten��o:</b> Considerar que para a submiss�o do projeto de ensino, este deve ser aprovado/homologado pela plen�ria do Departamento ou 
					Unidade Acad�mica Especializada, ao qual o coordenador do projeto de ensino est� vinculado.
				</p>
			</td>
		</tr>	
	</table>
	</div>

<h:form prependId="false">
	<table class="formulario" width="100%">
		<caption class="listagem"> RESUMO DO PROJETO DE ENSINO </caption>
		<tbody>
			<tr>		
				<th width="20%"><b> T�tulo do Projeto: </b></th>
				<td> 
					<h:outputText value="#{projetoMonitoria.obj.titulo}">
						<f:attribute name="lineWrap" value="120"/>
						<f:converter converterId="convertTexto"/>
					</h:outputText> </td>
			</tr>
			
			<tr>
				<th><b> Bolsas Solicitadas: </b></th>
				<td> <h:outputText value="#{projetoMonitoria.obj.bolsasSolicitadas}"/> </td>
			</tr>
			
			<tr>
				<th><b> E-Mail do Projeto: </b></th>
				<td>  <h:outputText value="#{projetoMonitoria.obj.coordenacao.pessoa.email}"/> </td>
			</tr>
			<tr>
				<th><b> Ano Refer�ncia: </b></th>
				<td>  <h:outputText value="#{projetoMonitoria.obj.ano}"/> </td>
			</tr>
			<tr>
				<th><b> Per�odo: </b></th>
				<td>
					<h:outputText id="dataInicio" value="#{projetoMonitoria.obj.projeto.dataInicio}" /> 
					<h:outputText id="ate" rendered="#{not empty projetoMonitoria.obj.projeto.dataFim}" value=" at� "/> 
					<h:outputText id="dataFim" value="#{projetoMonitoria.obj.projeto.dataFim}" />
				</td>
			</tr>
			<c:if test="${ projetoMonitoria.obj.projeto.interno }">
				<tr>		
					<th><b> Edital: </b></th>
					<td> 
					 <h:outputText value="#{projetoMonitoria.obj.editalMonitoria.numeroEdital}"/> (<h:outputText value="#{projetoMonitoria.obj.editalMonitoria.descricao}"/>) 
					</td>
				</tr>
			</c:if>
			<tr>		
				<th><b> Centro: </b></th>
				<td><h:outputText value="#{projetoMonitoria.obj.unidade}"/> </td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Detalhes do projeto</td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Resumo do Projeto: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.resumo}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Justificativa e Diagn�stico: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.justificativa}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Objetivos (geral e espec�fico): </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.objetivos}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Metodologia de Desenvolvimento do Projeto: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.metodologia}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Resultados Esperados: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.resultados}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Produtos que resultam da execu��o do projeto: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.produto}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Avalia��o do Desenvolvimento do Projeto: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.avaliacao}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Processo Seletivo: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.processoSeletivo}"  escape="false"/> </td>
			</tr>
			<tr>
				<td style="text-align: justify;" colspan="2"><b> Refer�cias:  Ref. Bibliogr�ficas do projeto, etc.: </b><br/>
				<h:outputText value="#{projetoMonitoria.obj.referencias}"  escape="false"/> </td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Orientadores e seus Componentes Curriculares</td>
			</tr>
			<tr>
				<td colspan="2">
					<t:dataTable value="#{projetoMonitoria.obj.componentesCurriculares}" var="compCurricular" align="center" width="100%" id="comp">
						<t:column>
							<t:dataTable value="#{compCurricular.docentesComponentes}" var="docentesComponentes" align="center" width="100%" id="ori">
								<t:column>
									<f:verbatim><b> Orientador: </b></f:verbatim><br/>
									<h:outputText value="#{docentesComponentes.equipeDocente.servidor.siapeNome}"/>
									<f:verbatim>
										<h:outputText value="  (Coordenador(a))" rendered="#{docentesComponentes.equipeDocente.coordenador}"/>
									</f:verbatim> <br/><br/>
									<f:verbatim><b>Componente Curricular: </b></f:verbatim>
									<p><h:outputText value="#{compCurricular.disciplina.codigoNome}" /></p> <br/>
									<f:verbatim><b> Carga-hor�ria semanal destinada ao projeto: </b></f:verbatim>
									<p><f:verbatim><i><h:outputText value="#{compCurricular.chDestinadaProjeto}" /></i></f:verbatim></p>
									<f:verbatim><b> Atividades desenvolvidas pelo monitor: </b></f:verbatim>
									<p><f:verbatim><i><h:outputText value="#{compCurricular.planoTrabalho}"  escape="false"/></i></f:verbatim></p>
									<f:verbatim><b> Avalia��o do Monitor: </b></f:verbatim>
									<p><f:verbatim><i><h:outputText value="#{compCurricular.avaliacaoMonitor}"  escape="false"/></i></f:verbatim></p>
									<f:verbatim><br/><hr/></f:verbatim>
								</t:column>
							</t:dataTable>
						</t:column>
					</t:dataTable>
				</td>
			</tr>
			
				<c:if test="${not empty projetoMonitoria.obj.orcamentosDetalhados}">
					<tr>
						<td colspan="2">
									<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Or�amento Detalhado</h3>			
									<table class="listagem" width="99%">
										<thead>
										<tr>
											<th>Descri��o</th>
											<th style="text-align: right"  width="15%">Licitado</th>
											<th style="text-align: right"  width="15%">Valor Unit�rio </th>
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
																		<td align="right">${orcamento.materialLicitado ? 'Sim' : 'N�o'}</td>
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
													<tr><td colspan="6" align="center"><font color="red">N�o h� itens de despesas cadastrados</font> </td></tr>
												</c:if>
		
										</tbody>
								</table>
						
						</td>
					</tr>	
				</c:if>
			
			<c:if test="${!projetoMonitoria.obj.projetoAssociado}">
			
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
								<f:facet name="header"><f:verbatim>Descri��o da Foto</f:verbatim></f:facet>
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
											<f:verbatim>Descri��o Arquivo</f:verbatim></f:facet>
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
			
		</tbody>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<div align="center">			

					<c:if test="${projetoMonitoria.confirmButton != 'Remover'}">
						<h:commandButton value="Finalizar Edi��o e Enviar" action="#{ projetoMonitoria.enviarProjetoPrograd }" styleClass="button" rendered="#{ !projetoBase.membroComiteAlterandoCadastro }" id="btFinalizarEnviar"/>
						<h:commandButton value="Gravar (Rascunho)" action="#{ projetoMonitoria.cadastrar }" styleClass="button" id="btGravar"/>
					</c:if>			
			
					<h:inputHidden id="id" value="#{projetoMonitoria.obj.id}"/>
					<c:if test="${projetoMonitoria.confirmButton == 'Remover'}">
						<h:commandButton value="#{projetoMonitoria.confirmButton}" action="#{projetoMonitoria.remover}" styleClass="button" />
					</c:if>
			
					<c:if test="${projetoMonitoria.confirmButton != 'Remover'}">	
						<h:commandButton value="<< Voltar" action="#{projetoMonitoria.passoAnterior}" styleClass="button" id="btVoltar"/>
					</c:if>
			
			
					<h:commandButton value="Cancelar" action="#{ projetoMonitoria.cancelar }" onclick="#{confirm}" styleClass="button" id="btCancelar"/>
			
				</div>
			</td>
		</tr>
	</tfoot>
</table>


</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
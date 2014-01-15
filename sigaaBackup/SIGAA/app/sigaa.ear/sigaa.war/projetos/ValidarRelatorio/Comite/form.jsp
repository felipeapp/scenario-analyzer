<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;
  	
	width: 98%;
	overflow:auto;
}
</style>

<a4j:keepAlive beanName="validacaoRelatorioBean" />
<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Valida��o de Relat�rio</h2>

	<h:form id="form">
		
		<table class="formulario" width="100%">
		<caption class="listagem">Valida��o de Relat�rio de A��es Acad�micas Integradas</caption>	
		
			<tr>
				<th width="25%" class="rotulo">Ano:</th>
				<td><h:outputText value="#{validacaoRelatorioBean.relatorio.projeto.ano}"/></td>
			</tr>
	
			<tr>
				<th class="rotulo">T�tulo Projeto:</th>
				<td><h:outputText	value="#{validacaoRelatorioBean.relatorio.projeto.titulo}"/></td>
			</tr>
	
			<tr>
				<th class="rotulo">Coordenador(a):</th>
				<td><h:outputText	value="#{validacaoRelatorioBean.relatorio.projeto.coordenador.pessoa.nome}"/></td>
			</tr>
	
			<tr>
				<th class="rotulo">Tipo de Relat�rio:</th>
				<td><h:outputText	value="#{validacaoRelatorioBean.relatorio.tipoRelatorio.descricao}"/></td>
			</tr>
			
			<tr>
				<th><b> P�blico Real Atendido:</b></th>
				<td><h:outputText value="#{validacaoRelatorioBean.relatorio.publicoRealAtingido}" id="publicoAtendido"/> pessoas</td>	
			</tr>
			
			
			<tr>
				<td  colspan="2" class="subFormulario"> Detalhamento das atividades desenvolvidas:</td>
			</tr>	
	
	
			<tr>
				<td colspan="2"><b> Atividades Realizadas: </b><br/>
					<h:outputText value="#{validacaoRelatorioBean.relatorio.atividadesRealizadas}" id="atividades"/>
				</td>	
			</tr>
			
			
			<tr>
				<td colspan="2"> <b>Rela��o entre a proposta pedag�gica do curso e a proposta do projeto de extens�o? Justifique:</b><br/>
					<h:outputText value="#{validacaoRelatorioBean.relatorio.relacaoPropostaCurso}" id="relacaoPropostaCurso"/>
				</td>	
			</tr>
			
			<tr>	
				<td colspan="2"><b>Outras a��es realizadas vinculadas ao projeto:</b><br/>
					<t:dataList value="#{validacaoRelatorioBean.relatorio.outrasAcoesProjeto}" var="acao">
							<t:column>
									<h:outputText value="#{acao.descricao}, " escape="false" />
							</t:column>
					 </t:dataList>
				</td>
			</tr>
			
			<tr>	
				<td colspan="2"><b>Apresenta��o do projeto em eventos de extens�o:</b><br/>
					<t:dataList value="#{ validacaoRelatorioBean.relatorio.apresentacaoProjetos }" var="apresentacao">
							<t:column>
											<h:outputText value="#{apresentacao.descricao}, " escape="false" />
							</t:column>
					</t:dataList>
				</td>
			</tr>
			
			<tr>	
				<td colspan="2"><b> Produ��o acad�mica gerada:</b><br/>
						<t:dataList value="#{ validacaoRelatorioBean.relatorio.producoesGeradas }" var="prod">
								<t:column>
										<h:outputText value="#{prod.descricao}, " escape="false"/>
								</t:column>
						 </t:dataList>
				</td>
			</tr>
	
	
			<tr>
				<td colspan="2"><b> Resultados Obtidos: Qualitativos.</b><br/>
					<h:outputText  value="#{validacaoRelatorioBean.relatorio.resultadosQualitativos}" id="qualitativos"/>
				</td>	
			</tr>
	
	
			<tr>
				<td colspan="2"><b> Resultados Obtidos: Quantitativos.</b><br/>
					<h:outputText value="#{validacaoRelatorioBean.relatorio.resultadosQuantitativos}" id="quantitativos"/>
				</td>	
			</tr>

			<tr>
				<td colspan="2"><b> Dificuldades Encontradas:</b><br/>
					<h:outputText value="#{validacaoRelatorioBean.relatorio.dificuldadesEncontradas}" id="dificuldades"/>
				</td>	
			</tr>
			
			<tr>
				<td colspan="2"><b> Ajustes Realizados Durante a Execu��o da A��o Acad�mica Integrada:</b><br/>
					<h:outputText value="#{validacaoRelatorioBean.relatorio.ajustesDuranteExecucao}" id="ajustes"/>
				</td>	
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario"> Membros da Equipe </td>
			</tr>	
			
			
			<c:if test="${not empty validacaoRelatorioBean.relatorio.projeto.equipe}">
						<tr>
							<td colspan="2">
					
										<t:dataTable value="#{validacaoRelatorioBean.relatorio.projeto.equipe}" var="membro" 
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
					<t:dataTable id="dataTableArq" value="#{validacaoRelatorioBean.relatorio.arquivos}" var="anexo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
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
			
			<c:if test="${empty validacaoRelatorioBean.relatorio.arquivos}">
					<tr><td colspan="6" align="center"><font color="red">N�o h� arquivos adicionados ao relat�rio</font> </td></tr>
			</c:if>
			
			
			<tr>
				<td colspan="2" class="subFormulario"> Detalhamento de utiliza��o dos recursos financeiros </td>
			</tr>	
	
			<tr>
				<td colspan="2">
					<table class="listagem">
							<tr>
								<td>
									<c:if test="${not empty validacaoRelatorioBean.relatorio.detalhamentoRecursosProjeto}">
										<t:dataTable id="dt" value="#{validacaoRelatorioBean.relatorio.detalhamentoRecursosProjeto}" var="consolidacao"
										 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" 
										 forceIdIndex="true">
											<t:column>
												<f:facet name="header"><f:verbatim>Descri��o</f:verbatim></f:facet>
												<h:outputText value="#{consolidacao.elemento.descricao}" />
											</t:column>

											<t:column>
												<f:facet name="header"><f:verbatim>Interno</f:verbatim></f:facet>
												<f:verbatim>R$ </f:verbatim>
												<h:outputText value="#{consolidacao.interno}"/>
											</t:column>

											<t:column>
												<f:facet name="header"><f:verbatim>Externo</f:verbatim></f:facet>
												<f:verbatim>R$ </f:verbatim>
												<h:outputText value="#{consolidacao.externo}"/>
											</t:column>


											<t:column>
												<f:facet name="header"><f:verbatim>Outros</f:verbatim></f:facet>
												<f:verbatim>R$ </f:verbatim>
												<h:outputText value="#{consolidacao.outros}" />
											</t:column>
										</t:dataTable>
									</c:if>
								</td>
						</tr>
								
						<c:if test="${empty validacaoRelatorioBean.relatorio.detalhamentoRecursosProjeto}">
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
						<h:selectOneMenu id="tipoParecerComite" value="#{validacaoRelatorioBean.relatorio.tipoParecerComite.id}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItem itemValue="1" itemLabel="APROVAR"/>
							<f:selectItem itemValue="2" itemLabel="APROVAR COM RECOMENDA��O"/>						
							<f:selectItem itemValue="3" itemLabel="REPROVAR"/>
							<f:selectItem itemValue="4" itemLabel="PROJETO N�O REALIZADO"/>
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Em caso de n�o aprova��o do relat�rio a justificativa deve ser informada obrigatoriamente.</ufrn:help>
						<br/>
					</td>
			</tr>		
			<tr>			
					<th>Justificativa: </th>
					<td>
						<h:inputTextarea  id="parecerComite" value="#{validacaoRelatorioBean.relatorio.parecerComite}" rows="5" style="width: 95%"/>
					</td>
			</tr>
	
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="confirmar" value="Confirmar Valida��o" action="#{validacaoRelatorioBean.validarPorComite}" /> 
						<h:commandButton id="btVoltar" value="<< Voltar" action="#{validacaoRelatorioBean.relatoriosPendenteComite}" /> 
						<h:commandButton id="cancelar" value="Cancelar" action="#{validacaoRelatorioBean.cancelar}" onclick="#{confirm }" />
					</td>
				</tr>
			</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
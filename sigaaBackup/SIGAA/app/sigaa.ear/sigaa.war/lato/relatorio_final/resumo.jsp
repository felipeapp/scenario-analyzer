<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{relatorioFinalLato.create }"></h:outputText>
	<h2><ufrn:subSistema /> &gt; Relatório Final de Curso Lato Sensu</h2>
	

<h:form id="cadastroRelatorio">

		<h:inputHidden value="#{ relatorioFinalLato.obj.curso.id }"/>
		<table class="formulario">
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
					<caption>Resumo da Realização do Curso</caption>
						<tr>
							<th><b>Curso:</b></th>
							<td><h:outputText id="nomeCurso" value="#{ relatorioFinalLato.obj.curso.descricao }"/> </td>
						</tr>
						<tr>
							<th><b>Remunerado:</b></th>
							<td>
								<c:if test="${ relatorioFinalLato.obj.curso.cursoPago }">Sim</c:if>
								<c:if test="${ not relatorioFinalLato.obj.curso.cursoPago }">Não</c:if>
							</td>
						</tr>
						<tr>
							<th><b>Número do Processo:</b></th>
							<td>
								<h:outputText id="numeroProcesso" value="#{ relatorioFinalLato.obj.numeroProcesso }"/>
							</td>
						</tr>
						<tr>
							<th><b>Ano do Processo:</b></th>
							<td>
								<h:outputText id="anoProcesso" value="#{ relatorioFinalLato.obj.anoProcesso }"/>
							</td>
						</tr>
						<tr>
							<th><b>Unidade Responsável:</b></th>
							<td><h:outputText id="unidadeResponsavel" value="#{ relatorioFinalLato.obj.curso.unidade.nome }"/></td>
						</tr>
						<tr>
							<th><b>Centro:</b></th>
							<td><h:outputText id="centro" value="#{ relatorioFinalLato.obj.curso.unidade.gestora.nome }"/></td>
						</tr>
						<tr class="linhaImpar">
							<th valign="top"><b>Coordenador:</b></th>
							<td>
								<table>
									<tr>
										<td colspan="2"><h:outputText id="nomeCoordenador" value="#{ relatorioFinalLato.obj.curso.coordenacao.servidor.pessoa.nome }"/></td>
									</tr>
									<tr>
										<td>Titulação:</td>
										<td><h:outputText id="tituloCoordenador" value="#{ relatorioFinalLato.obj.curso.coordenacao.servidor.formacao.denominacao }"/></td>
									</tr>
									<tr>
										<td>Telefone:</td>
										<td><h:outputText id="foneCoordenador" value="#{ relatorioFinalLato.obj.curso.coordenacao.telefoneContato1 }"/></td>
									</tr>
									<tr>
										<td>Email:</td>
										<td><h:outputText id="mailCoordenador" value="#{ relatorioFinalLato.obj.curso.coordenacao.emailContato }"/></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<th valign="top"><b>Vice-Coordenador:</b></th>
							<td>
								<table>
									<tr>
										<td colspan="2"><h:outputText id="nomeVice" value="#{ relatorioFinalLato.obj.curso.viceCoordenacao.servidor.pessoa.nome }"/></td>
									</tr>
									<tr>
										<td><b>Titulação:</b></td>
										<td><h:outputText id="tituloVice" value="#{ relatorioFinalLato.obj.curso.viceCoordenacao.servidor.formacao.denominacao }"/></td>
									</tr>
									<tr>
										<td><b>Telefone:</b></td>
										<td><h:outputText id="foneVice" value="#{ relatorioFinalLato.obj.curso.viceCoordenacao.telefoneContato1 }"/></td>
									</tr>
									<tr>
										<td><b>Email:</b></td>
										<td><h:outputText id="mailVice" value="#{ relatorioFinalLato.obj.curso.viceCoordenacao.emailContato }"/></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr class="linhaImpar">
							<th><b>Período de realização:</b></th>
							<td>
								<table>
									<tr>
										<th>Previsto:</th>
										<td><h:outputText id="dataIncioPrevista" value="#{ relatorioFinalLato.obj.curso.dataInicio }"/> a <h:outputText id="dataFimPrevista" value="#{ relatorioFinalLato.obj.curso.dataFim }"/></td>
									</tr>
									<tr>
										<th>Realizado:</th>
										<td>
											<h:outputText id="dataInicioRealizada" value="#{ relatorioFinalLato.obj.dataInicioRealizado }"/> a
											<h:outputText id="dataFimRealizada" value="#{ relatorioFinalLato.obj.dataFimRealizado }"/>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<th><b>Portaria de Aprovação:</b></th>
							<td>
								<table>
									<tr>
										<th>Número:</th>
										<td>
											<h:outputText id="numeroPortaria" value="#{ relatorioFinalLato.obj.numeroPortaria }"/> 
										</td>
										<th>Data:</th>
										<td>
											<h:outputText id="dataPortaria" value="#{ relatorioFinalLato.obj.dataPortaria }"/>
										</td>
									</tr>
								</table>				
							</td>
						</tr>
						<tr class="linhaImpar">
							<th><b>Carga Horária:</b></th>
							<td>
								<table>
									<tr>
										<th>Prevista:</th>
										<td><h:outputText id="chPrevista" value="#{ relatorioFinalLato.obj.curso.cargaHoraria }"/> horas</td>
									</tr>
									<tr>
										<th>Realizada:</th>
										<td><h:outputText id="chRealizada" value="#{ relatorioFinalLato.obj.chRealizada }"/></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<th><b>Discentes:</b></th>
							<td>
								<table>
									<tr>
										<th>Inscritos:</th><td><h:outputText id="inscritos" value="#{ relatorioFinalLato.obj.numeroInscritos }"/></td>
										<th>Selecionados:</th><td><h:outputText id="selecionados" value="#{ relatorioFinalLato.obj.numeroSelecionados }"/></td>
										<th>Matriculados:</th><td><h:outputText id="matriculados" value="#{ relatorioFinalLato.obj.numeroMatriculados }"/></td>
										<th>Concluintes:</th><td><h:outputText id="concluintes" value="#{ relatorioFinalLato.obj.numeroConcluintes }"/></td>
									</tr>
									<tr>
										<th>Outra IES:</th><td><h:outputText id="outraIES" value="#{ relatorioFinalLato.obj.numeroOutraIes }"/></td>
										<th>Prof. Ed. Básica:</th><td><h:outputText id="profEdBas" value="#{ relatorioFinalLato.obj.numeroProfEdBasica }"/></td>
										<th>Profissional Liberal:</th><td><h:outputText id="profLiberal" value="#{ relatorioFinalLato.obj.numeroProfissionalLiberal }"/></td>
										<th>Executivos:</th><td><h:outputText id="executivos" value="#{ relatorioFinalLato.obj.numeroExecutivos }"/></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<th><b>Docentes:</b></th>
							<td>
								<table>
									<tr>
										<th>Outras IES:</th>
										<td><h:outputText id="docentesOutraIEs" value="#{ relatorioFinalLato.numeroDocentesOutrasIes }"/></td>
										<th>${ configSistema['siglaInstituicao'] }:</th>
										<td><h:outputText id="docentesUFRN" value="#{ relatorioFinalLato.numeroDocentesUFRN }"/></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
					<caption>Introdução</caption>
					<tr><td>
						<p><h:outputText value="#{ relatorioFinalLato.obj.introducao }"/> </p>
					</td></tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
					<caption>Seleção</caption>
					<tr><td>
						<p><h:outputText value="#{ relatorioFinalLato.obj.selecao }"/> </p>
					</td></tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
					<caption>Disciplinas</caption>
					<tr><td>
						<p><h:outputText value="#{ relatorioFinalLato.obj.disciplinas }"/> </p>
					</td></tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
					<caption>Realização</caption>
					<tr><td>
						<p><h:outputText value="#{ relatorioFinalLato.obj.realizacao }"/> </p>
					</td></tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
					<caption>Meios de Divulgação</caption>
					<tr><td>
						<p><h:outputText value="#{ relatorioFinalLato.obj.meiosDivulgacao }"/> </p>
					</td></tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
					<caption>Instituições Envolvidas</caption>
					<tr><td>
						<p><h:outputText value="#{ relatorioFinalLato.obj.instituicoes }"/> </p>
					</td></tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
					<caption>Conclusão</caption>
					<tr><td>
						<p><h:outputText value="#{ relatorioFinalLato.obj.conclusao }"/> </p>
					</td></tr>
					</table>
				</td>
			</tr>
			
			<c:if test="${not empty relatorioFinalLato.obj.arquivos}">
			<tr>
				<td colspan="2">
					<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Arquivos</h3>
					<t:dataTable value="#{relatorioFinalLato.obj.arquivos}" var="arquivo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbArquivo">
							<t:column>
								<f:facet name="header">
									<f:verbatim>Descrição Arquivo</f:verbatim></f:facet>
									<h:outputText value="#{arquivo.descricao}" />
							</t:column>			
							<t:column width="5%">
								<h:commandLink id="visualizarArquivo" title="Visualizar Arquivo"  action="#{atividadeExtensao.viewArquivo}" immediate="true">
								        <f:param name="idArquivo" value="#{arquivo.idArquivo}"/>
							    		<h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</t:column>
					</t:dataTable>
				</td>
			</tr>	
			</c:if>
			
			<c:if test="${acesso.lato}">
			<tr>
				<td colspan="2" class="subFormulario">Alterar Status do relatório:</td>
			</tr>
			<tr><td></td></tr>
			<tr>
				<td align="center">Status:</td>
				<td>
					<h:selectOneMenu id="statusRelatorioFinalLato" value="#{relatorioFinalLato.obj.status}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{relatorioFinalLato.tiposCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td>Parecer:</td>
				<td>
					<h:inputTextarea value="#{relatorioFinalLato.obj.parecer}" id="parecer" rows="10" cols="110" 
						readonly="#{relatorioFinalLato.readOnly}" />
				</td>
			</tr>
			</c:if>
			<tr><td></td></tr>
			<tr><td></td></tr>
			<tr><td></td></tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="cadastrar" value="#{relatorioFinalLato.confirmButton}"	action="#{relatorioFinalLato.cadastrar}" rendered="#{ acesso.lato || not relatorioFinalLato.view }"/> 
						<h:commandButton value="<< Voltar" id="voltar" action="#{relatorioFinalLato.voltarForm}" rendered="#{ relatorioFinalLato.view }"/>
					</td>
				</tr>
			</tfoot>
		</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

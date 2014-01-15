<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var J = jQuery.noConflict();
</script>

<f:view>

<c:if test="${not planoDocenciaAssistidaMBean.relatorioSemestral}">
	<h2> <ufrn:subSistema /> &gt; Plano de Docência Assistida</h2>
</c:if>
<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	<h2> <ufrn:subSistema /> &gt; Relatório Semestral </h2>
</c:if>	

<a4j:keepAlive beanName="planoDocenciaAssistidaMBean" />
<h:form id="form" enctype="multipart/form-data">

<c:if test="${!planoDocenciaAssistidaMBean.portalPpg}">
<div class="descricaoOperacao">
	<p>
		<b> Caro Aluno, </b>
	</p> 	
	<p>
		Você poderá salvar os dados já cadastrados para posterior edição através da opção 'Salvar'. 
		Entretanto, para efetivar sua solicitação, é necessário selecionar a opção 'Salvar e Submeter', 
		assim não podendo mais ser alterada.
	</p> 	
</div>
</c:if>

<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">
	<c:if test="${planoDocenciaAssistidaMBean.obj.solicitadoAlteracaoRelatorio}">
		<div style="margin : 5px auto 20px auto; margin-bottom:5px; margin-left : auto; width: 90%">
			<span style="color:red;">
				<h3 style="text-align: center; margin-bottom: 10px;"><b>ATENÇÃO</b></h3>
				<p style="text-align: center;">Seu Relatório Semestral foi analisado e encontra-se com o Status: <b>${planoDocenciaAssistidaMBean.obj.descricaoStatus}</b>.</p>
				<c:if test="${not empty planoDocenciaAssistidaMBean.obj.observacao}">
					<br/> Motivo:
					<p style="text-align: justify; text-indent: 20px;">${planoDocenciaAssistidaMBean.obj.observacao}</p>
				</c:if>
				<br/>
			</span>				
		</div>
	</c:if>
</c:if>

<table style="width: 100%;" class="formulario">
	<c:if test="${not planoDocenciaAssistidaMBean.relatorioSemestral}">
		<caption> Dados do Plano de Docência Assistida </caption>
	</c:if>
	<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">
		<caption> Relatório Semestral </caption>
	</c:if>	
	
	<tr>
		<td colspan="2">
			<table class="visualizacao" style="width: 100%; margin: 0; border: 0">
			<tr>
				<td colspan="2" class="subFormulario"> Dados do Aluno de Pós-Graduação</td>
			</tr>
			<tr>
				<th>
					Nome:
				</th>
				<td>
				    ${planoDocenciaAssistidaMBean.obj.discente.matricula} - ${planoDocenciaAssistidaMBean.obj.discente.pessoa.nome}
				</td>		
			</tr>
			<tr>
				<th>
					Programa:
				</th>
				<td>
				    ${planoDocenciaAssistidaMBean.obj.discente.unidade.nome}
				</td>		
			</tr>	
			<tr>
				<th>
					Orientador:
				</th>
				<td>
					<c:if test="${planoDocenciaAssistidaMBean.orientacao != null}">
				    	${planoDocenciaAssistidaMBean.orientacao.descricaoOrientador}
				    </c:if>
				    <c:if test="${planoDocenciaAssistidaMBean.orientacao.descricaoOrientador == null}">
				    	<span style="color: red;">Orientador não informado.</span>
				    </c:if>
				</td>		
			</tr>		
			<tr>
				<th>
					Nível:
				</th>
				<td>
				    ${planoDocenciaAssistidaMBean.obj.discente.nivelDesc}
				</td>		
			</tr>	
			<c:if test="${not empty planoDocenciaAssistidaMBean.obj.periodoIndicacaoBolsa}">
			<tr>
				<th>
					Período da Indicação:
				</th>
				<td>
				    ${planoDocenciaAssistidaMBean.obj.periodoIndicacaoBolsa.anoPeriodoFormatado}
				</td>		
			</tr>			
			</c:if>	
			<c:if test="${empty planoDocenciaAssistidaMBean.obj.periodoIndicacaoBolsa}">
			<tr>
				<th>
					Ano/Período de Referência:
				</th>
				<td>
				    ${planoDocenciaAssistidaMBean.obj.ano}.${planoDocenciaAssistidaMBean.obj.periodo}
				</td>		
			</tr>			
			</c:if>
			<c:if test="${planoDocenciaAssistidaMBean.obj.status > 0}">		
			<tr>
				<th>
					Situação:
				</th>
				<c:choose>
					<c:when test="${planoDocenciaAssistidaMBean.obj.reprovado}">
						<td style="color:red; font-weight: bold;">${planoDocenciaAssistidaMBean.obj.descricaoStatus}</td>
					</c:when>
					<c:when test="${planoDocenciaAssistidaMBean.obj.aprovado}">
						<td style="color:green; font-weight: bold;">${planoDocenciaAssistidaMBean.obj.descricaoStatus}</td>
					</c:when>
					<c:otherwise>
						<td>${planoDocenciaAssistidaMBean.obj.descricaoStatus}</td>
					</c:otherwise>						
				</c:choose>			
			</tr>
			</c:if>	
			<tr>
				<th>
					Modalidade de Bolsa:
				</th>	
				<td>
					<c:choose>
						<c:when test="${planoDocenciaAssistidaMBean.obj.bolsista}">
						<c:if test="${planoDocenciaAssistidaMBean.obj.modalidadeBolsa != null && planoDocenciaAssistidaMBean.obj.modalidadeBolsa.id > 0}">
							${planoDocenciaAssistidaMBean.obj.modalidadeBolsa.descricao}
						</c:if>
						<c:if test="${planoDocenciaAssistidaMBean.obj.modalidadeBolsa == null || planoDocenciaAssistidaMBean.obj.modalidadeBolsa.id <= 0 }">
							<c:if test="${not empty planoDocenciaAssistidaMBean.obj.outraModalidade}">
								${planoDocenciaAssistidaMBean.obj.outraModalidade}
							</c:if>
							<c:if test="${empty planoDocenciaAssistidaMBean.obj.outraModalidade}">
								<span style="color: red;">Modalidade de Bolsa não informada.</span>
							</c:if>
						</c:if>			
						</c:when>
						<c:otherwise>
						Não possui bolsa.
						</c:otherwise>	
					</c:choose>
				</td>		
			</tr>	
			
			<tr>
				<td colspan="2" class="subFormulario">Dados do Componente Curricular</td>
			</tr>	
			<tr>
				<th width="35%">Curso: </th>
				<td>
					<c:if test="${not empty planoDocenciaAssistidaMBean.obj.curso.descricao}">
						${planoDocenciaAssistidaMBean.obj.curso.descricao}
					</c:if>
					<c:if test="${empty planoDocenciaAssistidaMBean.obj.curso.descricao}">
						<span style="color: red;">Curso não informado.</span>
					</c:if>
				</td>
			</tr>			
			<tr>
				<th width="35%">Componente Curricular: </th>
				<td>
					<c:if test="${not empty planoDocenciaAssistidaMBean.obj.componenteCurricular.nome}">
						${planoDocenciaAssistidaMBean.obj.componenteCurricular.nome}
					</c:if>
					<c:if test="${empty planoDocenciaAssistidaMBean.obj.componenteCurricular.nome}">
						<span style="color: red;">Componente Curricular não informado.</span>
					</c:if>
				</td>
			</tr>	
			<tr>
				<th width="35%">Departamento: </th>
				<td colspan="2">
					<c:if test="${not empty planoDocenciaAssistidaMBean.obj.componenteCurricular.unidade.nome}">
						<h:outputText value="#{planoDocenciaAssistidaMBean.obj.componenteCurricular.unidade.nome}"/>					
					</c:if>
					<c:if test="${empty planoDocenciaAssistidaMBean.obj.componenteCurricular.unidade.nome}">
						<span style="color: red;">Departamento não encontrado.</span>
					</c:if>		
				</td>
			</tr>			
			<c:if test="${planoDocenciaAssistidaMBean.obj.componenteCurricular.atividade}">
				<tr>
					<th>Docente:</th>
					<td>
						<c:if test="${not empty planoDocenciaAssistidaMBean.obj.servidor.pessoa.nome}">
							${planoDocenciaAssistidaMBean.obj.servidor.pessoa.nome}
						</c:if>
						<c:if test="${empty planoDocenciaAssistidaMBean.obj.servidor.pessoa.nome}">
							<span style="color: red;">Docente não informado.</span>				
						</c:if>					
					</td>
				</tr>				
			</c:if>

			<tr>
				<td colspan="2">
					<c:if test="${not empty planoDocenciaAssistidaMBean.obj.turmaDocenciaAssistida}">
						<table class="visualizacao">
							<thead>
								<tr>
									<td>Turma(s)</td>
									<td>Docente(s)</td>
									<td style="text-align: center;">Data de Início</td>
									<td style="text-align: center;">Data de Fim</td>
								</tr>
							</thead>
							<c:forEach items="#{planoDocenciaAssistidaMBean.obj.turmaDocenciaAssistida}" var="item" varStatus="loop">
								<tr class="${loop.index % 2 == 0? 'linhaPar': 'linhaImpar' }">
									<td>
										<h:outputText value="#{item.turma.codigo} - #{item.turma.disciplina.nome} (#{item.turma.anoPeriodo})"/>																				
									</td>
									<td>${item.turma.docentesNomes}</td>
									<td style="text-align: center;">
										<h:outputText value="#{item.dataInicio}">
											<f:convertDateTime pattern="dd/MM/yyyy"  />
										</h:outputText> 						
									</td>
									<td style="text-align: center;">
										<h:outputText value="#{item.dataFim}">
											<f:convertDateTime pattern="dd/MM/yyyy"  />
										</h:outputText> 						
									</td>				
								</tr>								
							</c:forEach>
						</table>	
					</c:if>
					<c:if test="${empty planoDocenciaAssistidaMBean.obj.turmaDocenciaAssistida && !planoDocenciaAssistidaMBean.obj.componenteCurricular.atividade}">
						<span style="color: red;">Nenhuma turma informada.</span>
					</c:if>
				</td>
			</tr>	
			<c:if test="${not planoDocenciaAssistidaMBean.relatorioSemestral}">
				<tr>
					<td colspan="2" class="subFormulario">Justificativa para escolha do componente curricular:</td>
				</tr>						 							 					
				<tr>
					<td colspan="2" align="justify"> 
						<c:if test="${not empty planoDocenciaAssistidaMBean.obj.justificativa}">
							<p> ${ planoDocenciaAssistidaMBean.obj.justificativa } </p>			
						</c:if>
						<c:if test="${empty planoDocenciaAssistidaMBean.obj.justificativa}">
							<span style="color: red;">Justificativa não informada.</span>
						</c:if>			
					</td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Objetivos:</td>
				</tr>		
				<tr>
					<td colspan="2" align="justify">
						<c:if test="${not empty planoDocenciaAssistidaMBean.obj.objetivos}">
							<p> ${ planoDocenciaAssistidaMBean.obj.objetivos } </p>			
						</c:if>
						<c:if test="${empty planoDocenciaAssistidaMBean.obj.objetivos}">
							<span style="color: red;">Objetivos não informado.</span>
						</c:if>
					</td>
				</tr>	
				<tr>
					<td colspan="2" class="subFormulario"> Atividade(s):</td>
				</tr>
				<tr>
					<td colspan="2">		
						<c:if test="${not empty planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}">
							<table class="visualizacao">
								<thead>
									<tr>
										<td>Atividade</td>
										<td style="text-align: center;">Data de Início</td>
										<td style="text-align: center;">Data de Fim</td>
										<td style="text-align: center;">Carga Horária</td>
										<td>Frequência</td>
										<td>Como Organizar</td>
										<td>Procedimentos</td>
									</tr>
								</thead>
								<c:forEach items="#{planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}" var="item" varStatus="loop">
									<tr class="${loop.index % 2 == 0? 'linhaPar': 'linhaImpar' }">
										<td>
											<c:if test="${not empty item.outraAtividade}">
												${item.outraAtividade}							
											</c:if>
											<c:if test="${empty item.outraAtividade}">
												${item.formaAtuacao.descricao}							
											</c:if>													
										</td>
										<td style="text-align: center;">
											<h:outputText value="#{item.dataInicio}">
												<f:convertDateTime pattern="dd/MM/yyyy"  />
											</h:outputText> 						
										</td>
										<td style="text-align: center;">
											<h:outputText value="#{item.dataFim}">
												<f:convertDateTime pattern="dd/MM/yyyy"  />
											</h:outputText> 						
										</td>
										<td style="text-align: center;">${item.ch}</td>
										<td>${item.frequenciaAtividade.descricao}</td>
										<td style="text-align: justify;">${item.comoOrganizar}</td>
										<td style="text-align: justify;">${item.procedimentos}</td>
									</tr>								
								</c:forEach>
							</table>		
						</c:if>
						<c:if test="${empty planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}">
							<span style="color: red;">Nenhuma atividade cadastrada.</span>
						</c:if>
					</td>
				</tr>	
			</c:if>	
			</table>	
		</td>
	</tr>

	<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">	
		<tr>
			<td colspan="2" class="subFormulario"> Atividades Realizadas <span class="obrigatorio"> </span> </td>
		</tr>		
		<tr>
			<td colspan="2">	
				<div class="descricaoOperacao">
					<p>Você deverá informar o percentual de realização de cada atividade, seus resultados e dificuldades encontradas.</p>	
				</div>				
				<table class="listagem" style="width: 100%">
					<thead>
						<tr>
							<td>Atividades Propostas no Plano de Atuação</td>
							<td style="text-align: center;">Realização (%)</td>
							<td></td>
						</tr>
					</thead>			
					<c:forEach items="#{planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}" var="item" varStatus="loop">
						<c:if test="${item.prevista}">
							<tr class="${loop.index % 2 == 0? 'linhaPar': 'linhaImpar' }">
								<td>
									<c:if test="${not empty item.outraAtividade}">
										${item.outraAtividade}							
									</c:if>
									<c:if test="${empty item.outraAtividade}">
										${item.formaAtuacao.descricao}							
									</c:if>													
								</td>
								<td style="text-align: center;" colspan="2">
									<h:selectOneMenu value="#{item.percentualRealizado}" id="percentualCombo">
										<f:selectItems value="#{planoDocenciaAssistidaMBean.percentuaisCombo}"/>
									</h:selectOneMenu>					
								</td>													
							</tr>
							<tr>
								<td colspan="3" align="center">
									<table class="formulario" style="width: 90%; margin:  5px;">
										<tr>							
											<td>
												<h4>Resultados Obtidos</h4>
												<h:inputTextarea cols="130" rows="4" id="resultados" value="#{ item.resultadosObtidos }"/>
											</td>	
										</tr>	
										<tr>
											<td>
												<h4>Dificuldades Encontradas</h4>
												<h:inputTextarea cols="130" rows="4" id="dificuldades" value="#{ item.dificuldades }"/>												
											</td>						
										</tr>									
									</table>							
								</td>
							</tr>
						</c:if>					
					</c:forEach>
				</table>		
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"> Atividades não Previstas</td>
		</tr>
		<tr>
			<td colspan="2">
				<a4j:region>
				<a4j:outputPanel id="formAtividades">
					<table style="width: 100%" class="formulario">
						<tr>
							<th class="obrigatorio">Atividade: </th>
							<td>
								<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.atividade.formaAtuacao.id}" onchange="informarAtividade(this);" style="width: 70%" id="atividadeCombo">
									<f:selectItem itemValue="-1" itemLabel="-- Selecione uma Atividade --"/>
									<f:selectItem itemValue="0" itemLabel="OUTRA"/>
									<f:selectItems value="#{planoDocenciaAssistidaMBean.formasAtuacaoCombo}"/>
								</h:selectOneMenu>
							</td>
						</tr>
						<tr id="outraAtividade">
							<th class="obrigatorio">Outra Atividade:</th>
							<td> 
								<h:inputText id="descricaoOutraAtividade" size="60" maxlength="100" value="#{planoDocenciaAssistidaMBean.atividade.outraAtividade}" />
								<ufrn:help>Caso não exista a atividade na lista acima, informe aqui a atividade desejada.</ufrn:help>	
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Data de Início: </th>
							<td>
								<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
										maxlength="10" onkeypress="return formataData(this,event)" value="#{planoDocenciaAssistidaMBean.atividade.dataInicio}" id="datainicioAtividade" />
								
								Data de Fim: <t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy"
										renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return formataData(this,event)"
										value="#{planoDocenciaAssistidaMBean.atividade.dataFim}" id="datafimAtividade" />														 
							</td>
						</tr>				
						<tr>
							<th class="obrigatorio">Frequência da atividade:</th>
							<td>
								<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.atividade.frequenciaAtividade.id}" id="tipoFrequencia">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
									<f:selectItems value="#{planoDocenciaAssistidaMBean.allFrequenciaAtividadeCombo}"/>
								</h:selectOneMenu>				
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Carga Horária: </th>
							<td>
								<h:inputText value="#{planoDocenciaAssistidaMBean.atividade.ch}" id="cargaHoraria" size="5" maxlength="5" 
								onkeyup="return formatarInteiro(this);" style="text-align: right;"/>  (h)
							</td>
						</tr>		
						<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">	
							<tr>
								<th class="obrigatorio">Realização (%): </th>
								<td>
									<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.atividade.percentualRealizado}" id="percentualAtividadeCombo">
										<f:selectItems value="#{planoDocenciaAssistidaMBean.percentuaisCombo}"/>
									</h:selectOneMenu>
								</td>				
							</tr>
						</c:if>			
						<tr>
							<td colspan="2">
								<table width="80%" align="center">				
									<tr>
										<td class="subFormulario" colspan="2">Metodologias da Atividade: <span class="obrigatorio">&nbsp;</span> </td>
									</tr>
									<tr>	
										<td colspan="2">
											<h:inputTextarea cols="100" rows="3" id="comoOrganizar" value="#{ planoDocenciaAssistidaMBean.atividade.comoOrganizar }"/> 
										</td>
									</tr>				
								</table>
							</td>
						</tr>				
						<tr>
							<td colspan="2">
								<table width="80%" align="center">				
									<tr>
										<td class="subFormulario" colspan="2">Como Avaliar a Atividade e Metodologias Empregadas: <span class="obrigatorio">&nbsp;</span> </td>
									</tr>
									<tr>	
										<td colspan="2">
											<h:inputTextarea cols="100" rows="3" id="procedimentos" value="#{ planoDocenciaAssistidaMBean.atividade.procedimentos }"/><ufrn:help>Análises ou Sínteses qualitativas e/ou quantitativas.</ufrn:help>
										</td>
									</tr>				
								</table>
							</td>
						</tr>
						<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">	
							<tr>
								<td colspan="2">
									<table width="80%" align="center">				
										<tr>
											<td class="subFormulario" colspan="2">Resultados Obtidos: <span class="obrigatorio">&nbsp;</span> </td>
										</tr>
										<tr>	
											<td colspan="2">
												<h:inputTextarea cols="100" rows="3" id="resultadosObtidosAtividade" value="#{ planoDocenciaAssistidaMBean.atividade.resultadosObtidos }"/>
											</td>
										</tr>				
									</table>
								</td>
							</tr>	
							<tr>
								<td colspan="2">
									<table width="80%" align="center">				
										<tr>
											<td class="subFormulario" colspan="2">Dificuldades Encontradas: <span class="obrigatorio">&nbsp;</span> </td>
										</tr>
										<tr>	
											<td colspan="2">
												<h:inputTextarea cols="100" rows="3" id="dificuldadesAtividade" value="#{ planoDocenciaAssistidaMBean.atividade.dificuldades }"/>
											</td>
										</tr>				
									</table>
								</td>
							</tr>			
						</c:if>
						<tfoot>
							<tr>
								<td colspan="2">
									<a4j:commandButton value="Salvar Atividade" actionListener="#{planoDocenciaAssistidaMBean.addAtividade}" reRender="listaAtividades, atividadeCombo, formAtividades" id="btAddAtividade"/>
									<rich:spacer width="10"/>
						            <a4j:status>
						                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
						            </a4j:status>	
								</td>
							</tr>
						</tfoot>					
					</table>
				</a4j:outputPanel>
				</a4j:region>	
			</td>
		</tr>	
		<tr>
			<td colspan="2">
				<a4j:region>	
				<a4j:outputPanel id="listaAtividades">
					<c:if test="${not empty planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}">						
						<div class="infoAltRem">
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Atividade							
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
							Remover Atividade
						</div>
					</c:if>
					
					<style>
						.data {
							text-align: center !important;
						}
						.atividade {
						   text-align: left !important;
						}
						.ch {
						   text-align: right !important;
						}							
					</style>
											
					<t:dataTable value="#{planoDocenciaAssistidaMBean.atividadesNaoPrevistas}" var="_atividade" style="width: 100%;" 
						styleClass="listagem" id="listagemAtividades" rowClasses="linhaPar, linhaImpar" rowIndexVar="row" rendered="#{not empty planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}">
						<t:column headerstyleClass="atividade" styleClass="atividade">
							<f:facet name="header"><f:verbatim>Atividade</f:verbatim></f:facet>
							<h:outputText value="#{_atividade.outraAtividade}"/>										
							<h:outputText value="#{_atividade.formaAtuacao.descricao}"/>																			
						</t:column>
						<t:column headerstyleClass="data" styleClass="data">
								<f:facet name="header"><f:verbatim>Data de Início</f:verbatim></f:facet>
								<h:outputText value="#{_atividade.dataInicio}"/>
						</t:column>
						<t:column headerstyleClass="data" styleClass="data">
								<f:facet name="header"><f:verbatim>Data de Fim</f:verbatim></f:facet>
								<h:outputText value="#{_atividade.dataFim}"/>						
						</t:column>
						<t:column headerstyleClass="ch" styleClass="ch">
								<f:facet name="header"><f:verbatim>Carga Horária (h)</f:verbatim></f:facet>
								<h:outputText value="#{_atividade.ch}"/>									
						</t:column>
						<t:column>
							<a4j:commandLink actionListener="#{planoDocenciaAssistidaMBean.alterarAtividade}" 
							oncomplete="informarAtividade(document.getElementById('form:atividadeCombo'));" 
							reRender="formAtividades" title="Alterar Atividade" id="linkAlterarAtv"
							 rendered="#{!planoDocenciaAssistidaMBean.relatorioSemestral || !_atividade.prevista}">
									<h:graphicImage value="/img/alterar.gif"/>
									<f:param name="indice" value="#{row}"/>
									<f:param name="id" value="#{_atividade.id}"/>
							</a4j:commandLink>								
						</t:column>											
						<t:column>
							<a4j:commandLink actionListener="#{planoDocenciaAssistidaMBean.removerAtividade}" 
							reRender="listaAtividades" title="Remover Atividade" id="linkRemoveAtv"
							rendered="#{!planoDocenciaAssistidaMBean.relatorioSemestral || !_atividade.prevista}">
									<h:graphicImage value="/img/delete.gif"/>
									<f:param name="indice" value="#{row}"/>
							</a4j:commandLink>								
						</t:column>	
					</t:dataTable>
				</a4j:outputPanel>
				</a4j:region>
			</td>
		</tr>	
		<tr>
			<td colspan="2" class="subFormulario">Anexar Arquivo:</td>
		</tr>		
		<tr>
			<td colspan="2" style="text-align: center;">
			
				<c:if test="${not empty planoDocenciaAssistidaMBean.obj.idArquivo}">								
					<html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${planoDocenciaAssistidaMBean.obj.idArquivo}">
						<img src="/shared/img/icones/download.png " title="Visualizar Arquivo Anexado" /> <br/> Visualizar Arquivo Anexado
					</html:link>
					<br/>
					<div class="descricaoOperacao">
						<p>Caso deseje susbtituir o arquivo existente, informe o novo arquivo abaixo:</p>
					</div> 					
				</c:if>				
			
				Selecione o Arquivo: <t:inputFileUpload value="#{ planoDocenciaAssistidaMBean.arquivo }" id="anexo" size="100"/>
				<ufrn:help>Selecione o arquivo a ser anexo ao Relatório. Tamanho Máximo ${planoDocenciaAssistidaMBean.tamanhoMaxArquivo}MB.</ufrn:help>			
			</td>
		</tr>			
		<tr>
			<td colspan="2" class="subFormulario"> Análise da Contribuição para Formação Docente:</td>
		</tr>
		<tr>
			<td colspan="2" style="text-align: center;">
				<h:inputTextarea cols="100" rows="4" id="analise" value="#{ planoDocenciaAssistidaMBean.obj.analise }"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"> Críticas e Sugestões:</td>
		</tr>
		<tr>
			<td colspan="2" style="text-align: center;">
				<h:inputTextarea cols="100" rows="4" id="sugestoes" value="#{ planoDocenciaAssistidaMBean.obj.sugestoes }"/>
			</td>
		</tr>			
	</c:if>	
	<tfoot>
		<tr>
			<td colspan="2">
				<c:if test="${not planoDocenciaAssistidaMBean.relatorioSemestral}">
					<h:commandButton value="Salvar" action="#{planoDocenciaAssistidaMBean.salvar}" id="btDeSalvar"/>
					<c:if test="${!planoDocenciaAssistidaMBean.portalPpg}">
						<h:commandButton value="Salvar e Submeter" action="#{planoDocenciaAssistidaMBean.submeter}" id="btDeSalvarSubmeter"/>
					</c:if>
					<h:commandButton value="<< Voltar" action="#{planoDocenciaAssistidaMBean.redirectForm}" id="btParaVoltar"/>
				</c:if>
				<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">
					<h:commandButton value="Salvar" action="#{planoDocenciaAssistidaMBean.salvarRelatorioSemestral}" id="botaoUsadoSalvar"/>
					<h:commandButton value="Salvar e Submeter" action="#{planoDocenciaAssistidaMBean.emitirRelatorioSemestral}" id="botaoUsadoParaSalvarSubmeter"/>
				</c:if>
				<h:commandButton value="Cancelar" action="#{planoDocenciaAssistidaMBean.cancelarGeral}" onclick="#{confirm}" immediate="true" id="cancelOperation"/>
			</td>
		</tr>
	</tfoot>	
</table>

<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">	
	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
</c:if>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


<script>
function habilitarDetalhes(id) {
	var linha = 'linha_'+ id;
	if ( J('#'+linha).css('display') == 'none' ) {
		J('#'+linha).css('display', 'inline');
		J('#img1_'+id).css('display', 'none');			
		J('#img2_'+id).css('display', 'inline');
		J('#link_'+id).attr('title', 'Ocutar Detalhes');				
	} else {
		J('#'+linha).css('display', 'none');
		J('#img2_'+id).css('display', 'none');			
		J('#img1_'+id).css('display', 'inline');
		J('#link_'+id).attr('title', 'Visualizar Detalhes');
	}
}

function informarAtividade(obj){
	if (obj.value == '0') {
		$('outraAtividade').show();
	} else {
		$('outraAtividade').hide();
	}	
}
informarAtividade(document.getElementById("form:atividadeCombo"));	
</script>
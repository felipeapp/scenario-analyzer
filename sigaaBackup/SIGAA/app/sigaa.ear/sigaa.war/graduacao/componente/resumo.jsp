<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/sigaa" prefix="sigaa" %>
<c:if test="${empty param.ajaxRequest }">
<%@include file="/WEB-INF/jsp/include/head_dialog.jsp"%>
</c:if>

<style>
<!--
table.visualizacao td {text-align: left;}
table.visualizacao th {font-weight: bold;}
-->
</style>
<f:view>
<c:if test="${empty param.ajaxRequest }">
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema/> &gt; Cadastrar Componente Curricular &gt; Resumo</h2>
</c:if>

	<h:form id="form">
		<table class="visualizacao" style="width:100%">
			<caption>Dados Gerais do Componente Curricular</caption>
			<tr>
				<th>Tipo do Componente Curricular: </th>
				<td><h:outputText value="#{componenteCurricular.obj.tipoComponente.descricao}" /></td>
			</tr>
			<c:if test="${ componenteCurricular.obj.passivelTipoAtividade }">
				<tr>
					<th>Tipo de ${componenteCurricular.obj.atividade ? 'Atividade' : 'Disciplina'}:</th>
					<td><h:outputText value="#{componenteCurricular.obj.tipoAtividade.descricao}" /></td>
				</tr>
				<tr>
					<th>Forma de Participação:</th>
					<td><h:outputText value="#{componenteCurricular.obj.formaParticipacao.descricao}" /></td>
				</tr>				
			</c:if>
			<tr>
				<th>Unidade Responsável:</th>
				<td><h:outputText value="#{componenteCurricular.obj.unidade.nome}" /></td>
			</tr>
			<c:if test="${not empty componenteCurricular.obj.curso.id and componenteCurricular.obj.curso.id > 0}">
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{componenteCurricular.obj.curso.descricao}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.obj.curso.id == 0 and componenteCurricular.obj.cursoNovo != null}">
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{componenteCurricular.obj.cursoNovo}" /> (Curso Novo)</td>
				</tr>
			</c:if>
			<tr>
				<th width="30%">Código:</th>
				<td><h:outputText value="#{componenteCurricular.obj.codigo}" /></td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.nome}" /></td>
			</tr>
			<tr>
				<th>Ativo:</th>
				<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.ativo}" /></td>
			</tr>
			<c:if test="${componenteCurricular.exibeCargaHorariaTotal}">
				<c:if test="${componenteCurricular.exibeCrTeorico}">
					<tr>
						<th>Créditos Teóricos:</th>
						<td><h:outputText value="#{componenteCurricular.obj.detalhes.crAula}" /> crs. (${componenteCurricular.obj.detalhes.chAula} h.) </td>
					</tr>
				</c:if>
				<c:if test="${componenteCurricular.exibeCrPratico}">
					<tr>
						<th>Créditos Práticos:</th>
						<td><h:outputText value="#{componenteCurricular.obj.detalhes.crLaboratorio}" /> crs. (${componenteCurricular.obj.detalhes.chLaboratorio} h.)</td>
					</tr>
				</c:if>
				<c:if test="${componenteCurricular.exibeCrEad}">
					<tr>
						<th>Créditos Ead:</th>
						<td><h:outputText value="#{componenteCurricular.obj.detalhes.crEad}" /> crs. (${componenteCurricular.obj.detalhes.chEad} h.)</td>
					</tr>
				</c:if>
				<c:if test="${componenteCurricular.exibeChTeorico}">
					<tr>
						<th>Carga Horária Teórica:</th>
						<td><h:outputText value="#{componenteCurricular.obj.detalhes.chAula}" /> h.</td>
					</tr>
				</c:if>
				<c:if test="${componenteCurricular.exibeChEad}">
					<tr>
						<th>Carga Horária de Ead:</th>
						<td><h:outputText value="#{componenteCurricular.obj.detalhes.chEad}" /> h.</td>
					</tr>
				</c:if>
				<c:if test="${componenteCurricular.exibeChPratico}">
					<tr>
						<th>Carga Horária Prática:</th>
						<td><h:outputText value="#{componenteCurricular.obj.detalhes.chLaboratorio}" /> h.</td>
					</tr>
				</c:if>
				<c:if test="${componenteCurricular.exibeChNaoAula}">
					<tr>
						<th>Carga Horária de Não Aula:</th>
						<td><h:outputText value="#{componenteCurricular.obj.detalhes.chNaoAula}" /> h.</td>
					</tr>
				</c:if>
				<c:if test="${componenteCurricular.exibeChDedicadaDocente}">
					<tr>
						<th>Carga Horária Dedicada do Docente:</th>
						<td><h:outputText value="#{componenteCurricular.obj.detalhes.chDedicadaDocente}" /> h.</td>
					</tr>
				</c:if>
			</c:if>
			<tr>
				<th>Carga Horária Total:</th>
				<td>
				<h:outputText value="#{componenteCurricular.obj.chTotal}" /> h.</td>
			</tr>
			<c:if test="${not acesso.programaStricto}">
				<tr>
					<th>Pré-Requisitos:</th>
					<td>
						<h:outputText value="#{componenteCurricular.preRequisitoForm}" />
					</td>
				</tr>
				<tr>
					<th>Co-Requisitos:</th>
					<td>
						<h:outputText value="#{componenteCurricular.coRequisitoForm}" />
					</td>
				</tr>
				<tr>
					<th>Equivalências:</th>
					<td>
						<h:outputText value="#{componenteCurricular.equivalenciaForm}" />
					</td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeAtivarComponente}">
				<tr>
					<th>Excluir da Avaliação Institucional:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.excluirAvaliacaoInstitucional}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.obj.atividade && !componenteCurricular.tecnico}">
				<tr>
					<th>Aceita Criar Turma:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.detalhes.atividadeAceitaTurma}" /></td>
				</tr>			
			</c:if>			
			<c:if test="${componenteCurricular.exibeMatriculavelOnLine}">
				<tr>
					<th>Matriculável On-Line:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.matriculavel}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeFlexibilidadeHorario}">
				<tr>
					<th>Horário Flexível da Turma:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.permiteHorarioFlexivel}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeHorarioDocenteFlexivel}">
				<tr>
					<th>Horário Flexível do Docente:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.permiteHorarioDocenteFlexivel}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibePrecisaNota}">
				<tr>
					<th>Obrigatoriedade de ${ (sessionScope.nivel == 'S') ? 'Conceito' : 'Nota Final' }:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.necessitaMediaFinal}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeTurmaSemSolicitacao}">
				<tr>
					<th>Pode Criar Turma Sem Solicitação:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.turmasSemSolicitacao}" /></td>
				</tr>			
			</c:if>
			<c:if test="${componenteCurricular.exibeNecessitaOrientador}">
				<tr>
					<th>Necessita de Orientador:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.temOrientador}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeProibeAproveitamento}">
				<tr>
					<th>Proíbe Aproveitamento:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.detalhes.proibeAproveitamento}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeSubTurma}">
				<tr>
					<th>Permitir Criar subturmas desse componente curricular:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.aceitaSubturma}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeHorarioTurma}">
				<tr>
					<th>Exige Horário:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.exigeHorarioEmTurmas}" /></td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.obj.permiteCriarTurma}" >
				<tr>
					<th>Núm. Máximo de Docentes na Turma:</th>
					<td>
						<h:outputText value="#{componenteCurricular.obj.numMaxDocentes}"/>								
					</td>
				</tr>
			</c:if>
			<tr>
				<th width="295px;"><b>Modalidade de Educação:</b></th>
				<td><h:outputText value="#{componenteCurricular.obj.modalidadeEducacao.descricao}" />
			</tr>
			<c:if test="${componenteCurricular.exibeChCompartilhada}">
				<tr>
					<th>Permite CH Compartilhada entre Docentes:</th>
					<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.detalhes.permiteChCompartilhada}" /></td>
				</tr>
			</c:if>
			<tr>
				<th valign="top">Quantidade máxima de matrículas:</th>
				<td><h:outputText value="#{componenteCurricular.obj.qtdMaximaMatriculas}" /></td>
			</tr>
			<tr>
				<th valign="top">Quantidade de Avaliações:</th>
				<td><h:outputText value="#{componenteCurricular.obj.numUnidades}" /></td>
			</tr>
			<c:if test="${not componenteCurricular.obj.bloco }">
				<tr>
					<th valign="top">Ementa/Descrição:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.ementa}" /></td>
				</tr>
				<c:if test="${componenteCurricular.exibeBibliografia}">
					<tr>
						<th valign="top">Referências:</th>
						<td><h:outputText value="#{componenteCurricular.obj.bibliografia}" /></td>
					</tr>
				</c:if>
			</c:if>
			<!-- dados do bloco -->
			<c:if test="${componenteCurricular.obj.bloco }">
				<tr>
					<td colspan="2">
					<table class="subFormulario" width="100%">
						<caption>Subunidades do Bloco</caption>
						<thead>
							<c:if test="${ not componenteCurricular.tecnico }">
								<td width="7%" style="text-align: right;">Cr</td>
							</c:if>
							<td width="7%" style="text-align: right;">Ch</td>
							<td width="10%">Tipo</td>
							<td>Código</td>
							<td>Nome</td>
						</thead>
						<c:set var="legenda" value="false" />
						<c:forEach items="#{componenteCurricular.obj.subUnidades}" var="subUnidade" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<c:if test="${ not componenteCurricular.tecnico }">
									<td style="text-align: right;">
										<c:if test="${subUnidade.disciplina}">${subUnidade.detalhes.crTotal} crs.</c:if>
									</td>
								</c:if>
								<td style="text-align: right;">
									${subUnidade.detalhes.chTotal}h
								</td>
								<td>${subUnidade.tipoComponente.descricao}</td>
								<td>
									<c:if test="${not empty subUnidade.codigo}">${subUnidade.codigo}</c:if>
									<c:if test="${empty subUnidade.codigo}">
										A DEFINIR<B><SUP>*</SUP></B>
										<c:set var="legenda" value="true" />
									</c:if>
								</td>
								<td>${subUnidade.nome}</td>
							</tr>
						</c:forEach>
					</table>
					<c:if test="${legenda}">
						<b>*</b> o código da subunidade será definido automaticamente.
					</c:if>
					</td>
				</tr>
			</c:if>
			<c:if test="${empty param.ajaxRequest }">
				
				<tr>
					<td colspan="2">
						<c:set var="exibirApenasSenha" value="true" scope="request"/>
						<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
					</td>
				</tr>
				<tfoot>
					<tr>
						<td colspan="2" style="text-align: center">
							<h:commandButton id="cadastrar" value="#{componenteCurricular.confirmButton}"
								action="#{componenteCurricular.cadastrarComponente}" />

							<c:if test="${componenteCurricular.confirmButton != 'Remover' }">
								<h:commandButton id="tipoComponente" value="<< Tipo do Componente Curricular"
									action="#{componenteCurricular.voltarDadosGerais}" />
								<h:commandButton id="dadosGerais" value="<< Dados Gerais"
								action="#{componenteCurricular.submeterTipoComponente}" />
							 	<c:if test="${componenteCurricular.obj.bloco }">
									<h:commandButton id="bloco" value="<< Bloco" action="#{componenteCurricular.voltarBloco}" />
								</c:if>
							</c:if>

							<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}"
								action="#{componenteCurricular.cancelar}" />
						</td>
					</tr>
				</tfoot>
			</c:if>
		</table>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

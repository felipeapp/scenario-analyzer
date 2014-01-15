<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/sigaa" prefix="sigaa"  %>

<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<script type="text/javascript" src="/shared/javascript/paineis/painel_generico.js"></script>
<link href="/sigaa/css/ensino/busca_turma.css" rel="stylesheet" type="text/css" />

<c:set var="reabrir" value="if (!confirm('Deseja realmente reabrir a turma?')) return false" scope="request" />
<c:set var="fechar" value="if (!confirm('Deseja realmente fechar a turma?')) return false" scope="request" />
<a4j:keepAlive beanName="buscaTurmaBean"/>
<f:view>
	<ufrn:subSistema teste="portalDiscente">
		<%@include file="/portais/discente/menu_discente.jsp" %>
	</ufrn:subSistema>
	<ufrn:subSistema teste="portalDocente">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</ufrn:subSistema>
	<ufrn:subSistema teste="portalCoordenador">
		<%@include file="/graduacao/menu_coordenador.jsp" %>
	</ufrn:subSistema>
	<c:if test="${acesso.tutorEad}">
		<%@include file="/portais/tutor/menu_tutor.jsp" %>
	</c:if>
	<c:if test="${acesso.coordenadorPolo}">
		<%@include file="/portais/cpolo/menu_cpolo.jsp" %>
	</c:if>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	
	<h2><ufrn:subSistema /> &gt; Consulta Geral de Turmas</h2>
	<h:form id="form">
	
		<c:if test="${param['gestor']}">
			<h:outputText value="#{buscaTurmaBean.reabrirTurmaGestorLato}" />
		</c:if>
		<table class="formulario" width="90%">
			<caption>Informe os critérios de busca das turmas </caption>
			<tbody>
				<c:if test="${ !buscaTurmaBean.turmasEAD }">
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroNivel}" id="checkNivel" styleClass="noborder" /></td>
					<td><label for="checkNivel" onclick="$('form:checkNivel').checked = !$('form:checkNivel').checked;">Nível:</label></td>
					<td><h:selectOneMenu value="#{buscaTurmaBean.nivelTurma}" style="width:40%;" onfocus="$('form:checkNivel').checked = true;" 
								id="selectNivelTurma">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{buscaTurmaBean.niveisCombo}" />
							<a4j:support event="onchange" reRender="form" />
						</h:selectOneMenu>
					</td>
				</tr>
				</c:if>
				<tr>
					<td width="1px;"><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroAnoPeriodo}" id="checkAnoPeriodo" styleClass="noborder" /></td>
					<td width="20%"><label for="checkAnoPeriodo" onclick="$('form:checkAnoPeriodo').checked = !$('form:checkAnoPeriodo').checked;">
							Ano-Período:</label></td>
					<td><h:inputText value="#{buscaTurmaBean.anoTurma}" onfocus="$('form:checkAnoPeriodo').checked = true;" size="4" maxlength="4" 
								id="inputAno" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" /> .
						<h:inputText value="#{buscaTurmaBean.periodoTurma}" onfocus="$('form:checkAnoPeriodo').checked = true;" size="1" maxlength="1" 
								id="inputPeriodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroUnidade}" id="checkUnidade" styleClass="noborder" /></td>
					<td><label for="checkDepartamento" onclick="$('form:checkUnidade').checked = !$('form:checkUnidade').checked;">
							Unidade:</label></td>
					<td><h:selectOneMenu value="#{buscaTurmaBean.unidade.id}" style="width:95%;" onfocus="$('form:checkUnidade').checked = true;" 
								id="selectUnidade" disabled="#{buscaTurmaBean.buscaTurmasEAJ}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.allDetentorasComponentesCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroCodigo}" id="checkCodigo" styleClass="noborder"/></td>
					<td><label for="checkCodigo" onclick="$('form:checkCodigo').checked = !$('form:checkCodigo').checked;">
							Código do componente:</label></td>
					<td><h:inputText value="#{buscaTurmaBean.codigoDisciplina}" size="10" maxlength="9" onfocus="$('form:checkCodigo').checked = true;" 
							onkeyup="CAPS(this)" id="inputCodDisciplina" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroCodigoTurma}" id="checkCodigoTurma" styleClass="noborder" /></td>
					<td><label for="checkCodigoTurma" onclick="$('form:checkCodigoTurma').checked = !$('form:checkCodigoTurma').checked;">
							Código da turma:</label></td>
					<td><h:inputText value="#{buscaTurmaBean.codigoTurma}" size="4" maxlength="3" onfocus="$('form:checkCodigoTurma').checked = true;" 
							id="inputCodTurma" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroLocal}" id="checkLocal" styleClass="noborder" /></td>
					<td><label for="checkLocal" onclick="$('form:checkLocal').checked = !$('form:checkLocal').checked;">Local:</label></td>
					<td><h:inputText value="#{buscaTurmaBean.local}" size="20" maxlength="20" onfocus="$('form:checkLocal').checked = true;" id="inputLocal" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroHorario}" id="checkHorario" styleClass="noborder" /></td>
					<td><label for="checkHorario" onclick="$('form:checkHorario').checked = !$('form:checkHorario').checked;">Horário:</label></td>
					<td><h:inputText value="#{buscaTurmaBean.turmaHorario}" size="20" maxlength="20" onfocus="$('form:checkHorario').checked = true;"  onkeyup="CAPS(this)" id="inputHorario" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroDisciplina}" id="checkDisciplina" styleClass="noborder" /></td>
					<td><label for="checkDisciplina" onclick="$('form:checkDisciplina').checked = !$('form:checkDisciplina').checked;">
							Nome do componente:</label></td>
					<td><h:inputText value="#{buscaTurmaBean.nomeDisciplina}" maxlength="60" onfocus="$('form:checkDisciplina').checked = true;" style="width: 95%;" 
							id="inputNomeDisciplina" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroDocente}" id="checkDocente" styleClass="noborder" /></td>
					<td> <label for="checkDocente" onclick="$('form:checkDocente').checked = !$('form:checkDocente').checked;">
							Nome do docente</label>:</td>
					<td><h:inputText value="#{buscaTurmaBean.nomeDocente}" maxlength="60" onfocus="$('form:checkDocente').checked = true;" style="width: 95%;" 
							id="inputNomeDocente" /></td>
				</tr>
				
				<a4j:region id="regiaoCurso" rendered="#{!buscaTurmaBean.stricto}">
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroCurso}" id="checkCurso" styleClass="noborder" /></td>
					<td><label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;"> Ofertadas ao curso:</label></td>
					<td>
						<h:selectOneMenu value="#{ buscaTurmaBean.curso.id }" onchange="$('form:checkCurso').checked = true;" style="width: 95%;" id="selectCurso">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ buscaTurmaBean.allCursoNivelAtualCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
				</a4j:region>
				<c:if test="${ buscaTurmaBean.turmasEAD }">
					<tr>
						<td><h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroPolo}" id="checkPolo" styleClass="noborder" /></td>
						<td> <label for="checkPolo" onclick="$('form:checkPolo').checked = !$('form:checkPolo').checked;">Pólo</label>:</td>
						<td>
							<h:selectOneMenu value="#{ buscaTurmaBean.polo.id }" onchange="$('form:checkPolo').checked = true;" style="width: 40%;" 
									id="selectPolo">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{ tutorOrientador.polos }" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroSituacao}" id="checkSituacao" styleClass="noborder" /></td>
					<td> <label for="checkSituacao" onclick="$('form:checkSituacao').checked = !$('form:checkSituacao').checked;">
							Situação</label>:</td>
					<td>
						<h:selectOneMenu value="#{buscaTurmaBean.situacaoTurma}" onchange="$('form:checkSituacao').checked = true;" 
								style="width: 40%;" id="selectSituacaoTurma">
							<f:selectItem itemLabel="TODAS" itemValue="-1" />
							<f:selectItems value="#{situacaoTurma.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroTipo}" id="checkTipo" styleClass="noborder" /></td>
					<td> <label for="checkTipo" onclick="$('form:checkTipo').checked = !$('form:checkTipo').checked;">Tipo</label>:</td>
					<td>
						<h:selectOneMenu value="#{buscaTurmaBean.tipoTurma}" onchange="$('form:checkTipo').checked = true;" style="width: 40%;" 
								id="selectTipoTurma">
							<f:selectItem itemLabel="TODAS" itemValue="0" />
							<f:selectItems value="#{buscaTurmaBean.tiposTurmaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroConvenio}" id="checkConvenio" styleClass="noborder" /></td>
					<td colspan="2"> 
						<label for="checkConvenio" onclick="$('form:checkConvenio').checked = !$('form:checkConvenio').checked;">
							Somente turmas vinculadas ao Convênio Probásica</label>
					</td>
				</tr>
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroRelatorio}" id="checkRel" styleClass="noborder" /></td>
					<td colspan="2"> 
						<label for="checkRel" onclick="$('form:checkRel').checked = !$('form:checkRel').checked;">
							Exibir resultado da consulta em formato de relatório</label>
					</td>
				</tr>
				<c:if test="${ (acesso.secretarioDepartamento ) }">
					<tr>	
						<td> <h:selectBooleanCheckbox value="#{buscaTurmaBean.filtroDiarioTurma}" id="checkTurma" styleClass="noborder" /></td>
						<td colspan="2"> 
							<label for="checkTurma" onclick="$('form:checkTurma').checked = !$('form:checkTurma').checked;">
								Emitir Diário de Classe</label>
						</td>
					</tr>
				</c:if>
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaTurmaBean.ordenarBusca}" id="checkOrdenacao" styleClass="noborder" /></td>
					<td> <label for="checkOrdenacao" onclick="$('form:checkOrdenacao').checked = !$('form:checkOrdenacao').checked;">Ordenar por</label>: </td>
					<td>
						<h:selectOneMenu value="#{buscaTurmaBean.ordenarPor}" onchange="$('form:checkOrdenacao').checked = true;"
								style="width: 40%;" id="selectOpcaoOrdenacao">							
							<f:selectItems value="#{buscaTurmaBean.allOpcoesOrdenacaoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<input type="hidden" name="turmasEAD" id="hdnTurmasEAD" value="${buscaTurmaBean.turmasEAD}" />
						<h:commandButton action="#{buscaTurmaBean.buscarGeral}" value="Buscar" id="buttonBuscar" />
						<h:commandButton action="#{buscaTurmaBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="buttonCancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br />

		<c:if test="${not empty buscaTurmaBean.resultadosBusca}">

			<div class="infoAltRem">
				<c:choose>
					<c:when test="${buscaTurmaBean.filtroDiarioTurma}">
						<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Emitir Diário de Classe									 
					</c:when>
				<c:otherwise>
					<h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" style="overflow: visible;" />: Visualizar Menu
				</c:otherwise>
				</c:choose>
			</div>

			<table class="listagem" id="lista-turmas">
				<caption>Turmas Encontradas (${ fn:length(buscaTurmaBean.resultadosBusca) })</caption>
				<thead>
					<tr>
					<td width="8%" style="text-align: center;">Ano Período</td>
					<td width="7%"></td>
					<td id="colDocente">Docente(s)</td>
					<td width="8%">Tipo</td>
					<td width="8%">Situação</td>
					<td id="colHorario">Horário</td>
					<td width="10%">Local</td>
					<td style="text-align: right;">Mat./Cap.</td>
					<td width="2px"></td>
					</tr>
				</thead>
				<tbody>
				<c:set var="disciplinaAtual" value="0" />

				<c:forEach items="#{buscaTurmaBean.resultadosBusca}" var="t" varStatus="s">

					<c:if test="${ disciplinaAtual != t.disciplina.id}">
						<c:set var="disciplinaAtual" value="${t.disciplina.id}" />
						<tr class="destaque"><td colspan="17" style="font-variant: small-caps;" style="text-align: left;">
							${t.disciplina.descricaoResumida} <small>(${t.disciplina.nivelDesc})</small>
						</td></tr>
					</c:if>
						<tr class="${s.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="font-size: xx-small">
							<td style="text-align: center;">${t.ano}.${t.periodo}</td>
							<td align="center">
								<a href="javascript:void(0);" onclick="PainelTurma.show(${t.id});" title="Ver Detalhes dessa turma" 
										style="font-weight: normal">
									Turma ${t.codigo}
									<c:if test="${not empty t.especializacao}"> (${t.especializacao.descricao}) </c:if>
								</a>
							</td>
							<td id="colDocente">${empty t.docentesNomesCh ? "A DEFINIR" : t.docentesNomesCh}</td>
							<td>${t.tipoString}</td>
							<td>${t.situacaoTurma.descricao}</td>
							<c:set var="posDescricaoHorario" value="${fn:indexOf(t.descricaoHorario,' ')}"/>
							<td id="colHorario">${t.descricaoHorario}
							</td>
							<td>${t.polo == null ? t.local : t.polo.cidade.nomeUF}</td>
							<td style="text-align: right;"> ${t.qtdMatriculados}/${t.capacidadeAluno} alunos</td>
							<c:choose>
								<c:when test="${buscaTurmaBean.filtroDiarioTurma}">
									<td>
										<h:commandLink action="#{diarioClasse.gerarDiarioClasse}" title="Emitir Diário de Classe">
											<h:graphicImage url="/img/seta.gif"/>
											<f:param name="id" value="#{t.id}"/>
										</h:commandLink>					
									</td>
								</c:when>
							<c:otherwise>
								<td><img src="${ctx}/img/biblioteca/emprestimos_ativos.png" onclick="exibirOpcoes(${t.id});" style="cursor: pointer" title="Visualizar Menu"/></td>
							</c:otherwise>
							</c:choose>
						</tr>
 
			<tr class="${s.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="display: none" id="trOpcoes${t.id}">
			<td colspan="9">
            <c:set var="bgCor" value="${ s.index % 2 == 0 ? '#F9FBFD' : '#EDF1F8' }" />
             
			<ul class="listaOpcoes">
			
			<sigaa:permissaoOperarTurma operacao="alterar" turma="${t}">
				<li id="Alterar">
					<h:commandLink styleClass="noborder" title="Alterar" action="#{buscaTurmaBean.atualizar}"  id="sltAlterar">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Alterar
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="duplicarTurma" turma="${t}">
				<li id="duplicarTurma">
					<h:commandLink styleClass="noborder" title="Duplicar Turma" action="#{buscaTurmaBean.duplicarTurma}"  id="sltDuplicarTurma">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Duplicar a Turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="cadastrarNoticia" turma="${t}">
				<li id="btnNoticia">
					<h:commandLink styleClass="noborder" title="Casdatrar notícia para a turma" id="sltbtnNoticia"
							action="#{noticiaTurmaSelecionavelBean.selecionarTurma}">
						<f:param name="id" value="#{t.id}" />
						Cadastrar Notícia
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>

			<sigaa:permissaoOperarTurma operacao="consolidar" turma="${t}">
				<li  id="btnConsolidar">
					<h:commandLink styleClass="noborder" title="Consolidar Turma" action="#{buscaTurmaBean.consolidarTurma}" id="sltbtnConsolidar">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Consolidar Turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>

			<sigaa:permissaoOperarTurma operacao="gerarPlanilhaNotas" turma="${t}">
				<li  id="btnPlanilhaDeNotas">
					<h:commandLink styleClass="noborder" title="Planilha de Notas" actionListener="#{notasExcel.iniciar}" id="sltbtnPlanilhaDeNotas">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Planilha de Notas
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="gerarDiarioTurma" turma="${t}">
				<li id="btnDiarioTurma">
					<h:commandLink styleClass="noborder" title="Diário de turma" action="#{relatorioConsolidacao.diarioTurma}" id="sltbtnDiarioTurma">
						<f:param name="idTurma" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Diário de Turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="gerarDiarioClasse" turma="${t}">	
				<li id="btnGerarDiarioClasse">
					<h:commandLink styleClass="noborder" title="Diário de turma" action="#{diarioClasse.gerarDiarioClasse}" id="sltbtnGerarDiarioClasse">
						<f:param name="id" value="#{t.id}" />
						Diário de turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="gerarListaFrequencia" turma="${t}">	
				<li id="btnGerarListaFrequencia">
					<h:commandLink styleClass="noborder" title="Lista de Frequência" action="#{turmaVirtual.visualizaListaPresenca}" id="sltbtnGerarListaFrequencia">
						<f:param name="idTurma" value="#{t.id}" />
						Lista de Frequência
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
						
			<sigaa:permissaoOperarTurma operacao="fecharTurma" turma="${t}">	
				<li id="btnfecharTurma">
					<h:commandLink styleClass="noborder" title="Fechar Turma" action="#{buscaTurmaBean.fecharTurma}" onclick="#{fechar}" id="sltbtnfecharTurma">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Fechar Turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="listarAlunos" turma="${t}">
				<li id="btnSelecionarTurma">
					<h:commandLink action="#{buscaTurmaBean.selecionaTurma }" title="Listar Alunos" id="sltbtnSelecionarTurma">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Listar Alunos
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="listarAlunosImpressao" turma="${t}">
			<li  id="btnListarAlunosImpressao">
				<h:commandLink  action="#{buscaTurmaBean.listarAlunosImpressao }" title="Listar Alunos para Impressão" id="sltbtnListarAlunosImpressao">
					<f:param name="id" value="#{t.id}" />
					<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
					Listar Alunos para Impressão
				</h:commandLink>
			</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="listarNotasAlunos" turma="${t}">
			<li id="btnNotasDiscente">
				<h:commandLink styleClass="noborder" title="Notas dos alunos" action="#{relatorioConsolidacao.notasDiscente}" id="sltbtnNotasDiscente">
					<f:param name="idTurma" value="#{t.id}" />
					<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
					Notas dos alunos
				</h:commandLink>
			</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="gerarNotasTutores" turma="${t}">
				<li id="btnNotasTutoresPorDisciplina">
					<h:commandLink styleClass="noborder" title="Notas dos tutores" action="#{relatorioConsolidacao.notasTutoresPorDisciplina}" id="sltbtnNotasTutoresPorDisciplina">
						<f:param name="idTurma" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Notas dos tutores
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="exibeProcessamentoMatricula" turma="${t}">
				<li id="btnResultadoProcessamentoMatricula">
					<a href="#" onclick="window.open('${ctx}/relatorioProcessamento?idTurma=${ t.id }','resultado','height=480,width=730,resizable=yes,scrollbars=yes');">Processamento da Matrícula</a> 
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="exibeProcessamentoRematricula" turma="${t}">
				<li id="btnResultadoProcessamentoRematricula">
					<a href="#" onclick="window.open('${ctx}/relatorioProcessamento?idTurma=${ t.id }&rematricula=true','resultado','height=480,width=730,resizable=yes,scrollbars=yes');">Processamento da Rematrícula</a>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="reabrirTurma" turma="${t}">
				<li id="btnReabrir">
					<h:commandLink styleClass="noborder" title="Reabrir Turma" action="#{buscaTurmaBean.reabrirTurma}" onclick="#{reabrir}">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{buscaTurmaBean.turmasEAD}" />
						Reabrir Turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="removerTurma" turma="${t}">
				<li id="btnRemover">
					<h:commandLink styleClass="noborder" title="Remover" action="#{buscaTurmaBean.preRemover}" id="sltbtnRemover">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Remover
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="alterarStatusMatriculaTurma" turma="${t}">
				<li id="btnAlterarStatusMatricula">
					<h:commandLink styleClass="noborder" title="Alterar Status de Matrícula" action="#{alteracaoStatusMatricula.selecionarAlterarSituacaoMatricula}">
						<f:param name="id" value="#{t.id}" />
						Alterar Status de Matrícula
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>			
			
			<sigaa:permissaoOperarTurma operacao="visualizarTurma" turma="${t}">
				<li id="btnView">
					<h:commandLink styleClass="noborder" title="Visualizar Turma" action="#{buscaTurmaBean.view}" id="sltbtnView">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
						Visualizar Turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="visualizarTurmaVirtual" turma="${t}">
				<li id="btnTurmaVirtual">
					<h:commandLink styleClass="noborder" title="Visualizar Turma Virtual" action="#{turmaVirtual.entrar}" id="sltbtnTurmaVirtual" rendered="#{!t.subTurma}">
						<f:param name="idTurma" value="#{t.id}" />
						Visualizar Turma Virtual
					</h:commandLink>
					<h:commandLink styleClass="noborder" title="Visualizar Turma Virtual" action="#{turmaVirtual.entrar}" id="sltbtnTurmaVirtualSubTurma" rendered="#{t.subTurma}">
						<f:param name="idTurma" value="#{t.turmaAgrupadora.id}" />
						Visualizar Turma Virtual
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>

			<sigaa:permissaoOperarTurma operacao="enviarEmail" turma="${t}">
				<li id="btnEnviarEmail">
					<h:commandLink styleClass="noborder" title="Enviar Email" action="#{envioEmailTurma.selecionarTurma}" id="enviarEmail">
						<f:param name="id" value="#{t.id}" />
						Enviar Email
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="verAgenda" turma="${t}">
				<li id="btnVerAgenda">
					<h:outputLink value="/sigaa/ensino/turma/view_calendario.jsf"
						target="popupWindow" 
						styleClass="noborder" title="Ver Agenda da Turma" id="verAgenda">
						<f:param name="idTurma" value="#{t.id}" />
						Ver Agenda da Turma
			    	</h:outputLink> 
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="ajustarTurma" turma="${t}">
				<li id="btnAjustarTurma">
					<h:commandLink styleClass="noborder" title="Ajustar Turma" action="#{ajustarTurmaMBean.iniciar}"  id="sltAjustarTurma">
						<f:param name="id" value="#{t.id}" />
						Ajustar Turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<li style="clear: both; float: none; background-image: none;"></li>
			</ul>
			
			</td>
		</tr>
		
		</c:forEach>

	</tbody>
	</table>
	</c:if>
</h:form>

</f:view>
<script type="text/javascript">
<!--
	function exibirPainel(id) {
		PainelDetalhar.show('/sigaa/geral/turma/painelOpcoes.jsf?id='+id, 'Opções', 300, 300);
	}
	
	function exibirOpcoes(idDiscente){
		var linha = 'trOpcoes'+ idDiscente;
		$(linha).toggle();
	}
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
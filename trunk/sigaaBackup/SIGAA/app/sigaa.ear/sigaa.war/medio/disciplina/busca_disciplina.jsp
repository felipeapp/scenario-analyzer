<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/sigaa" prefix="sigaa"  %>

<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<script type="text/javascript" src="/shared/javascript/paineis/painel_generico.js"></script>
<link href="/sigaa/css/ensino/busca_turma.css" rel="stylesheet" type="text/css" />

<c:set var="reabrir" value="if (!confirm('Deseja realmente reabrir a turma?')) return false" scope="request" />
<c:set var="fechar" value="if (!confirm('Deseja realmente fechar a turma?')) return false" scope="request" />
<a4j:keepAlive beanName="buscaDisciplinaMedio"/>
<f:view>
	<ufrn:subSistema teste="portalDiscente">
		<%@include file="/portais/discente/medio/menu_discente_medio.jsp" %>
	</ufrn:subSistema>
	
	<h2><ufrn:subSistema /> &gt; Consulta Geral de Disciplinas</h2>
	<h:form id="form">
	
		<table class="formulario" width="90%">
			<caption>Informe os critérios de busca das Disciplinas </caption>
			<tbody>
				<tr>
					<td width="1px;"><h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroAnoPeriodo}" id="checkAnoPeriodo" styleClass="noborder" /></td>
					<td width="20%">
						<label for="checkAnoPeriodo" onclick="$('form:checkAnoPeriodo').checked = !$('form:checkAnoPeriodo').checked;">Ano:</label>
					</td>
					<td>
						<h:inputText value="#{buscaDisciplinaMedio.anoTurma}" onfocus="$('form:checkAnoPeriodo').checked = true;" size="4" maxlength="4" 
							id="inputAno" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" /> 
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroUnidade}" id="checkUnidade" styleClass="noborder" /></td>
					<td>
						<label for="checkDepartamento" onclick="$('form:checkUnidade').checked = !$('form:checkUnidade').checked;">Unidade:</label>
					</td>
					<td>
						<h:selectOneMenu value="#{buscaDisciplinaMedio.unidade.id}" style="width:95%;" onfocus="$('form:checkUnidade').checked = true;" 
								id="selectUnidade">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.allDetentorasComponentesCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroCodigoTurma}" id="checkCodigoTurma" styleClass="noborder" /></td>
					<td><label for="checkCodigoTurma" onclick="$('form:checkCodigoTurma').checked = !$('form:checkCodigoTurma').checked;">
							Código da turma:</label></td>
					<td><h:inputText value="#{buscaDisciplinaMedio.codigoTurma}" size="4" maxlength="3" onfocus="$('form:checkCodigoTurma').checked = true;" 
							id="inputCodTurma" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroLocal}" id="checkLocal" styleClass="noborder" /></td>
					<td><label for="checkLocal" onclick="$('form:checkLocal').checked = !$('form:checkLocal').checked;">Local:</label></td>
					<td><h:inputText value="#{buscaDisciplinaMedio.local}" size="20" maxlength="20" onfocus="$('form:checkLocal').checked = true;" id="inputLocal" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroHorario}" id="checkHorario" styleClass="noborder" /></td>
					<td><label for="checkHorario" onclick="$('form:checkHorario').checked = !$('form:checkHorario').checked;">Horário:</label></td>
					<td><h:inputText value="#{buscaDisciplinaMedio.turmaHorario}" size="20" maxlength="20" onfocus="$('form:checkHorario').checked = true;"  onkeyup="CAPS(this)" id="inputHorario" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroDisciplina}" id="checkDisciplina" styleClass="noborder" /></td>
					<td><label for="checkDisciplina" onclick="$('form:checkDisciplina').checked = !$('form:checkDisciplina').checked;">
							Nome da Disciplina:</label></td>
					<td><h:inputText value="#{buscaDisciplinaMedio.nomeDisciplina}" maxlength="60" onfocus="$('form:checkDisciplina').checked = true;" style="width: 95%;" 
							id="inputNomeDisciplina" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroDocente}" id="checkDocente" styleClass="noborder" /></td>
					<td> <label for="checkDocente" onclick="$('form:checkDocente').checked = !$('form:checkDocente').checked;">
							Nome do docente</label>:</td>
					<td><h:inputText value="#{buscaDisciplinaMedio.nomeDocente}" maxlength="60" onfocus="$('form:checkDocente').checked = true;" style="width: 95%;" 
							id="inputNomeDocente" /></td>
				</tr>
				
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroCurso}" id="checkCurso" styleClass="noborder" /></td>
					<td> <label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">Curso:</label></td>
					<td>
						<a4j:region>
						<h:selectOneMenu value="#{ buscaDisciplinaMedio.curso.id }" style="width: 95%;" 
								valueChangeListener="#{buscaDisciplinaMedio.carregarSeriesByCurso }" id="selectCurso">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ cursoMedio.allCombo }" />
							<a4j:support event="onchange" oncomplete="$('form:checkCurso').checked = true;" reRender="selectSerie, $('form:checkCurso').checked = true;" />
							</h:selectOneMenu>
							<a4j:status>
					                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
				            </a4j:status>
						</a4j:region>
					</td>
				</tr>
				
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroSerie}" id="checkSerie" styleClass="noborder" /></td>
					<td> <label for="checkSerie" onclick="$('form:checkSerie').checked = !$('form:checkSerie').checked;">Série:</label></td>
					<td>
						<h:selectOneMenu value="#{ buscaDisciplinaMedio.serie.id }" style="width: 75%;" id="selectSerie" onchange="$('form:checkSerie').checked = true;" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CURSO --" />
							<f:selectItems value="#{ buscaDisciplinaMedio.seriesByCurso }" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroSituacao}" id="checkSituacao" styleClass="noborder" /></td>
					<td> <label for="checkSituacao" onclick="$('form:checkSituacao').checked = !$('form:checkSituacao').checked;">
							Situação</label>:</td>
					<td>
						<h:selectOneMenu value="#{buscaDisciplinaMedio.situacaoTurma}" onchange="$('form:checkSituacao').checked = true;" 
								style="width: 40%;" id="selectSituacaoTurma">
							<f:selectItem itemLabel="TODAS" itemValue="-1" />
							<f:selectItems value="#{situacaoTurma.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroTipo}" id="checkTipo" styleClass="noborder" /></td>
					<td> <label for="checkTipo" onclick="$('form:checkTipo').checked = !$('form:checkTipo').checked;">Tipo</label>:</td>
					<td>
						<h:selectOneMenu value="#{buscaDisciplinaMedio.tipoTurma}" onchange="$('form:checkTipo').checked = true;" style="width: 40%;" 
								id="selectTipoTurma">
							<f:selectItem itemLabel="TODAS" itemValue="0" />
							<f:selectItems value="#{buscaDisciplinaMedio.tiposTurmaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroRelatorio}" id="checkRel" styleClass="noborder" /></td>
					<td colspan="2"> 
						<label for="checkRel" onclick="$('form:checkRel').checked = !$('form:checkRel').checked;">
							Exibir resultado da consulta em formato de relatório</label>
					</td>
				</tr>
				<c:if test="${ (acesso.secretarioDepartamento ) }">
					<tr>	
						<td> <h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.filtroDiarioTurma}" id="checkTurma" styleClass="noborder" /></td>
						<td colspan="2"> 
							<label for="checkTurma" onclick="$('form:checkTurma').checked = !$('form:checkTurma').checked;">
								Emitir Diário de Classe</label>
						</td>
					</tr>
				</c:if>
				<tr>
					<td> <h:selectBooleanCheckbox value="#{buscaDisciplinaMedio.ordenarBusca}" id="checkOrdenacao" styleClass="noborder" /></td>
					<td> <label for="checkOrdenacao" onclick="$('form:checkOrdenacao').checked = !$('form:checkOrdenacao').checked;">Ordenar por</label>: </td>
					<td>
						<h:selectOneMenu value="#{buscaDisciplinaMedio.ordenarPor}" onchange="$('form:checkOrdenacao').checked = true;"
								style="width: 40%;" id="selectOpcaoOrdenacao">							
							<f:selectItems value="#{buscaDisciplinaMedio.allOpcoesOrdenacaoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<input type="hidden" name="turmasEAD" id="hdnTurmasEAD" value="${buscaDisciplinaMedio.turmasEAD}" />
						<h:commandButton action="#{buscaDisciplinaMedio.buscarGeral}" value="Buscar" id="buttonBuscar" />
						<h:commandButton action="#{buscaDisciplinaMedio.cancelar}" value="Cancelar" onclick="#{confirm}" id="buttonCancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br />

		<c:if test="${not empty buscaDisciplinaMedio.resultadosBusca}">

			<div class="infoAltRem">
				<c:choose>
					<c:when test="${buscaDisciplinaMedio.filtroDiarioTurma}">
						<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Emitir Diário de Classe									 
					</c:when>
				<c:otherwise>
					<h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" style="overflow: visible;" />: Visualizar Menu
				</c:otherwise>
				</c:choose>
			</div>

			<table class="listagem" id="lista-turmas">
				<caption>Disciplinas Encontradas (${ fn:length(buscaDisciplinaMedio.resultadosBusca) })</caption>
				<thead>
					<tr>
					<td width="8%" style="text-align: center;">Ano</td>
					<td width="8%">Série</td>
					<td width="7%">Turma</td>
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

				<c:forEach items="#{buscaDisciplinaMedio.resultadosBusca}" var="t" varStatus="s">

					<c:if test="${ disciplinaAtual != t.disciplina.id}">
						<c:set var="disciplinaAtual" value="${t.disciplina.id}" />
						<tr class="destaque"><td colspan="17" style="font-variant: small-caps;" style="text-align: left;">
							${t.disciplina.nome} <small>(${t.disciplina.nivelDesc})</small>
						</td></tr>
					</c:if>
						<tr class="${s.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="font-size: xx-small">
							<td style="text-align: center;">${t.ano}</td>
							<td>${t.turmaSerie.serie.descricaoCompleta}</td>
							<td align="left">
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
							<td>${t.local}</td>
							<td style="text-align: right;"> ${t.qtdMatriculados}/${t.capacidadeAluno} alunos</td>
							<c:choose>
								<c:when test="${buscaDisciplinaMedio.filtroDiarioTurma}">
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
					<h:commandLink styleClass="noborder" title="Alterar" action="#{buscaDisciplinaMedio.atualizar}"  id="sltAlterar">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaDisciplinaMedio.turmasEAD }" />
						Alterar
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

			<sigaa:permissaoOperarTurma operacao="gerarDiarioClasse" turma="${t}">	
				<li id="btnGerarDiarioClasse">
					<h:commandLink styleClass="noborder" title="Diário de turma" action="#{diarioClasseMedio.gerarDiarioClasse}" id="sltbtnGerarDiarioClasse">
						<f:param name="id" value="#{t.id}" />
						Diário de classe
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="fecharTurma" turma="${t}">	
				<li id="btnfecharTurma">
					<h:commandLink styleClass="noborder" title="Fechar Turma" action="#{buscaDisciplinaMedio.fecharTurma}" onclick="#{fechar}" id="sltbtnfecharTurma">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaDisciplinaMedio.turmasEAD }" />
						Fechar Turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="listarAlunos" turma="${t}">
				<li id="btnSelecionarTurma">
					<h:commandLink action="#{turmaMedio.listaAlunosMatriculados }" title="Listar Alunos" id="sltbtnSelecionarTurma">
						<f:param name="id" value="#{t.id}" />
						<f:param name="idTurmaSerie" value="#{t.turmaSerie.id}"/>
						Listar Alunos
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="listarAlunosImpressao" turma="${t}">
			<li  id="btnListarAlunosImpressao">
				<h:commandLink  action="#{buscaDisciplinaMedio.listarAlunosImpressao }" title="Listar Alunos para Impressão" id="sltbtnListarAlunosImpressao">
					<f:param name="id" value="#{t.id}" />
					<f:param name="turmasEAD" value="#{ buscaDisciplinaMedio.turmasEAD }" />
					Listar Alunos para Impressão
				</h:commandLink>
			</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="reabrirTurma" turma="${t}">
				<li id="btnReabrir">
					<h:commandLink styleClass="noborder" title="Reabrir Turma" action="#{buscaDisciplinaMedio.reabrirTurma}" onclick="#{reabrir}">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{buscaDisciplinaMedio.turmasEAD}" />
						Reabrir Turma
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
					<h:commandLink styleClass="noborder" title="Visualizar Turma" action="#{buscaDisciplinaMedio.view}" id="sltbtnView">
						<f:param name="id" value="#{t.id}" />
						<f:param name="turmasEAD" value="#{ buscaDisciplinaMedio.turmasEAD }" />
						Visualizar Turma
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>
			
			<sigaa:permissaoOperarTurma operacao="visualizarTurmaVirtual" turma="${t}">
				<li id="btnTurmaVirtual">
					<h:commandLink styleClass="noborder" title="Visualizar Turma Virtual" action="#{turmaVirtual.entrar}" id="sltbtnTurmaVirtual">
						<f:param name="idTurma" value="#{t.id}" />
						Visualizar Turma Virtual
					</h:commandLink>
				</li>
			</sigaa:permissaoOperarTurma>

			<sigaa:permissaoOperarTurma operacao="enviarEmail" turma="${t}">
				<li id="btnEnviarEmail">
					<h:commandLink styleClass="noborder" title="Enviar Email" action="#{noticiaTurmaSelecionavelBean.selecionarTurma}" id="enviarEmail">
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
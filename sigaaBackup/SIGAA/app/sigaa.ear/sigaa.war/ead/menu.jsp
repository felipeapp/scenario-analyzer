<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2>Secretaria de Ensino a Dist�ncia</h2>

<h:form>
		<input type="hidden" name="aba" id="aba"/>

		<div id="operacoes-subsistema"  class="reduzido">

			<div id="pessoas" class="aba">
				<ul>
					<li> Pessoas
						<ul>
							<li> <h:commandLink value="Cadastrar Dados Pessoais" action="#{dadosPessoais.preCadastrarEad}" onclick="setAba('pessoas')"/></li>
							<li> <h:commandLink value="Buscar/Alterar" action="#{dadosPessoais.listar}" onclick="setAba('pessoas')"/></li>							
						</ul>
					</li>
					<li> Tutores Presenciais
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{tutorOrientador.preCadastrar}" onclick="setAba('pessoas')"/> </li>
							<li> <h:commandLink value="Listar/Alterar" action="#{tutorOrientador.listar}" onclick="setAba('pessoas')"/></li>							
							<li> <a href="${ctx}/ead/TutorOrientador/logarComo.jsf?aba=pessoas">Logar como Tutor </a> </li>
							<li> <a href="${ctx}/ead/TutorOrientador/logarComoDiscente.jsf?aba=pessoas">Logar como Discente </a> </li>						
						</ul>
					</li>
					<li> Tutores � Dist�ncia
						<ul>
							<li> <h:commandLink value="Cadastrar" action="#{tutorDistancia.preCadastrar}" onclick="setAba('pessoas')"/> </li>
							<li> <h:commandLink value="Listar/Alterar" action="#{tutorDistancia.listar}" onclick="setAba('pessoas')"/></li>							
							<li> <a href="${ctx}/ead/TutorDistancia/logarComo.jsf?aba=pessoas">Logar como Tutor </a> </li>
						</ul>
					</li>
					<li> Coordenadores de Polo
						<ul>
							<li>
								<h:commandLink value="Cadastrar" action="#{coordenacaoPolo.preCadastrar}" onclick="setAba('pessoas')"/>
							</li>
							<li><a href="${ctx}/ead/CoordenacaoPolo/lista.jsf?aba=pessoas">Alterar/Remover</a></li>
							<li><a href="${ctx}/ead/CoordenacaoPolo/lista.jsf?aba=pessoas">Cadastrar Usu�rio</a></li>
							<li><a href="${ctx}/ead/CoordenacaoPolo/logarComo.jsf?aba=pessoas">Logar como Coord. P�lo </a> </li>
						</ul>
					</li>
					<c:if test="${tutorOrientador.permiteTutoria}">
						<li> Associar Alunos a Tutor
							<ul>
								<li> <h:commandLink value="Cadastrar Tutoria" action="#{tutoriaAluno.iniciar}" onclick="setAba('pessoas')"/></li>
								<li> <h:commandLink value="Buscar/Alterar" action="#{tutoriaAluno.listar}" onclick="setAba('pessoas')"/></li>							
							</ul>
						</li>
					</c:if>
					</ul>
			</div>

			<div id="alunos" class="aba">
				<ul>
					<li> <h:commandLink action="#{consolidacaoIndividual.iniciar}" value="Consolida��o Individual" onclick="setAba('alunos')"/> </li>
					<li> <h:commandLink action="#{alteracaoStatusMatricula.iniciarCancelamentoMatricula }" value="Cancelar Matr�culas em Turmas" onclick="setAba('alunos')"/> </li>					

					<li> <h:commandLink action="#{historicoDiscente.iniciar}" value="Consultar Dados do Aluno" onclick="setAba('alunos')"> <f:param name="eadAluno" value="true"/> </h:commandLink> </li>
					
										
					<li> Documentos
						<ul>
							<li> <h:commandLink	action="#{ historico.buscarDiscenteEad }"	value="Emitir Hist�rico" onclick="setAba('alunos')"/> </li>
							<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" onclick="setAba('alunos')"/> </li>
						</ul>
					</li>
				</ul>
			</div>

			<div id="avaliacao" class="aba">
				<ul>
					<li>Metodologia de Avalia��o
					<ul>
						<li><h:commandLink action="#{ metodologiaAvaliacaoEad.listar }" value="Gerenciar Metodologias" onclick="setAba('avaliacao')"/></li>
					</ul>
					</li>
					<li><h:commandLink action="#{ habilitarAvaliacao.iniciar }" value="Habilitar Avalia��o" onclick="setAba('avaliacao')"/></li>
				</ul>
			</div>
			
			<div id="coordenacao" class="aba">
				<ul>
					<li>Calend�rio
						<ul>
						<li><h:commandLink value="Alterar Calend�rio" action="#{ calendario.iniciarEAD}" onclick="setAba('coordenacao')"/></li>
						</ul>
					</li>
					<li>Matr�culas
						<ul>
						<li><h:commandLink value="Matricular Aluno EAD" action="#{ matriculaGraduacao.iniciarMatriculaEAD}" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink id="matriculaGraduacaoiniciarMatriculaFerias" action="#{matriculaGraduacao.iniciarMatriculaFerias}" value="Matricular Aluno Em Turma de F�rias" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Matricular Alunos em Lote" action="#{ loteMatriculas.iniciarMatriculas}" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Cancelar Matr�cula de Aluno EAD" action="#{ alteracaoStatusMatricula.iniciarCancelamentoMatricula}" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Orientar Trancamentos de Matr�cula" action="#{ atenderTrancamentoMatricula.iniciarAtendimentoSolicitacaoEad }" onclick="setAba('coordenacao')"/></li>
						</ul>
					</li>
					<li>Atividades
						<ul>
						<li><h:commandLink value="Matricular" action="#{ registroAtividade.iniciarMatricula}" onclick="setAba('coordenacao')"><f:param name="ead" value="true"/></h:commandLink></li>
						<li><h:commandLink value="Consolidar" action="#{ registroAtividade.iniciarConsolidacao}" onclick="setAba('coordenacao')"><f:param name="ead" value="true"/></h:commandLink></li>
						<li><h:commandLink value="Validar" action="#{ registroAtividade.iniciarValidacao}" onclick="setAba('coordenacao')"><f:param name="ead" value="true"/></h:commandLink></li>
						<li><h:commandLink value="Excluir" action="#{ registroAtividade.iniciarExclusao}" onclick="setAba('coordenacao')"><f:param name="ead" value="true"/></h:commandLink></li>
						<li><h:commandLink value="Solicitar Cadastro de Atividade" action="#{opCoordenadorGeralEad.solicitarCadastroAtividade}" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Minhas Solicita��es de Cadastro de Atividade" action="#{opCoordenadorGeralEad.verMinhasSolicitacoes}" onclick="setAba('coordenacao')"/></li>
						</ul>
					</li>
					<li>Aluno
						<ul>
						<li><h:commandLink value="Atualizar Dados Pessoais" action="#{ alteracaoDadosDiscente.iniciar}" onclick="setAba('coordenacao')"/></li>
						<li> <h:commandLink action="#{ alterarDadosUsuarioAluno.iniciar }" value="Alterar Dados do Usu�rio do Aluno" onclick="setAba('coordenacao')"/> </li>
						<li><h:commandLink value="Emitir Atestado de Matr�cula" action="#{ atestadoMatricula.iniciarEad }" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Emitir Hist�rico" action="#{ historico.buscarDiscenteEad }" onclick="setAba('coordenacao')"/></li>
						</ul>
					</li>
					<li> Turmas
						<ul>
							<li>
								<h:commandLink action="#{ turmaGraduacaoBean.cadastroTurmaEad }" value="Cadastrar" onclick="setAba('coordenacao')"/>
							</li>
							<li>
								<h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Consultar" onclick="setAba('coordenacao')">
									<f:param value="true" name="turmasEAD"/>
								</h:commandLink>
							</li>
							<li>
								<h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Emitir Listas de Presenca" onclick="setAba('coordenacao')">
									<f:param value="true" name="turmasEAD"/>
								</h:commandLink>
							</li>
							<li>
								<h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Emitir Di�rio de Turma" onclick="setAba('coordenacao')">
									<f:param value="true" name="turmasEAD"/>
								</h:commandLink>
							</li>
							<!-- 
							<li>
								
								<h:commandLink action="#{ relatorioAlunosPolo.buscarNotasInseridasPorTutor }" value="Acompanhamento da Inser��o de Notas Por Tutor" onclick="setAba('coordenacao')">
									<f:param value="true" name="turmasEAD"/>
								</h:commandLink>
								
								 <a href="${ctx}/ead/CoordenacaoPolo/buscar_notas_tutor.jsf?aba=coordenacao">Acompanhamento da Inser��o de Notas Por Tutor</a></li>
							</li>
							 -->
							<li>
								<h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Notas por disciplina" onclick="setAba('coordenacao')">
									<f:param value="true" name="turmasEAD"/>
								</h:commandLink>
							</li>
							<li>
								<f:param value="true" name="turmasEAD"/>
									<h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Notas das semanas/tutores" onclick="setAba('coordenacao')">
								</h:commandLink>
							</li>
						</ul>
					</li>
					<li> Itens de um programa
						<ul>
							<li>
								<h:commandLink action="#{ itemProgramaMBean.cadastro }" value="Cadastrar" onclick="setAba('coordenacao')"/>
							</li>							
						</ul>
						<ul>
							<li>
								<h:commandLink action="#{ itemProgramaMBean.consultar }" value="Consultar" onclick="setAba('coordenacao')"/>
							</li>							
						</ul>
					</li>
					<li>Relat�rios
						<ul>
						<li><h:commandLink value="Alunos Ativos no Curso" action="#{ opCoordenadorGeralEad.relatorioAlunosAtivosCurso }"/></li>
						<li><h:commandLink value="Alunos pendentes de Matr�cula" action="#{ opCoordenadorGeralEad.relatorioAlunosPendenteMatricula }" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Matr�cula On-line n�o atentidas" action="#{ opCoordenadorGeralEad.relatorioMatriculasNaoAtendidas }" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Trancamentos no Semestre" action="#{ opCoordenadorGeralEad.relatorioTrancamentos }" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Turmas Consolidadas" action="#{ opCoordenadorGeralEad.relatorioTurmasConsolidadas }" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Turmas Ofertadas ao Curso" action="#{ opCoordenadorGeralEad.iniciarRelatorioListaTurmasOfertadasCurso }" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Reprova��es por Disciplinas" action="#{ relatorioDisciplinasReprovacoesBean.iniciar }" onclick="setAba('coordenacao')"/></li>
						<li><h:commandLink value="Alunos Concluintes" action="#{opCoordenadorGeralEad.relatorioAlunosConcluintes}" onclick="setAba('coordenacao')"/></li>
						<li><a href="${ctx}/graduacao/relatorios/discente/seleciona_eleicao.jsf?aba=coordenacao">Lista de alunos para elei��o</a></li>
						<li><a href="${ctx}/graduacao/relatorios/discente/seleciona_tipo_saida.jsf?aba=coordenacao">Lista de alunos por tipo de sa�da</a></li>
						<li><h:commandLink value="Docentes por Semestre" action="#{relatorioDocenteDisciplinasMBean.iniciar}" onclick="setAba('coordenacao')"/></li>
						</ul>
					</li>
					<li>Consultas
						<ul>
						<li><h:commandLink action="#{ componenteCurricular.popularBuscaGeral }" value="Componentes Curriculares" onclick="setAba('coordenacao')"/></li>
						<li>
							<h:commandLink action="#{buscaTurmaBean.popularBuscaGeral}" value="Turmas" onclick="setAba('coordenacao')">
								<f:param value="true" name="turmasEAD"/>
							</h:commandLink>
						</li>
						<li><a href="${ctx}/graduacao/curriculo/lista.jsf?aba=coordenacao">Estruturas Curriculares</a></li>
						<li><a href="${ctx}/graduacao/habilitacao/lista.jsf?aba=coordenacao">Habilita��es</a></li>
						<li><a href="${ctx}/graduacao/matriz_curricular/lista.jsf?aba=coordenacao">Matrizes Curriculares</a></li>
						<li><a href="${ctx}/graduacao/curso/lista.jsf?aba=coordenacao">Cursos</a></li>
						</ul>
					</li>					
				</ul>

			</div>

			<div id="consultas" class="aba">

				<ul>
				
					<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.COORDENADOR_GERAL_EAD } %>">
					<li> Docentes
						<ul>
							<li>
								<h:commandLink action="#{docenteExterno.preCadastrar}" value="Cadastrar Docente Externo" onclick="setAba('administracao')"/> 
							</li>
							<li> <ufrn:link action="administracao/docente_externo/lista.jsf"> Cadastrar Usu�rio Para Docente Externo</ufrn:link> </li>
						</ul>
					</li>
					</ufrn:checkRole> 
				
				
					<li> Turmas
						<ul>
							<li>
								<h:commandLink action="#{ turmaGraduacaoBean.cadastroTurmaEad }" value="Cadastrar" onclick="setAba('consultas')"/>
							</li>
							<li>
								<h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Consultar" onclick="setAba('consultas')">
									<f:param value="true" name="turmasEAD"/>
								</h:commandLink>
							</li>
							<li>
								<h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Emitir Listas de Presen�a" onclick="setAba('consultas')">
									<f:param value="true" name="turmasEAD"/>
								</h:commandLink>
							</li>
							<li>
								<h:commandLink action="#{ listaPresencaLoteMBean.iniciar }" value="Emitir Listas de Presen�a em Lote" onclick="setAba('consultas')"/>
							</li>
							<li>
								<h:commandLink action="#{ cargaHorariaEadMBean.listar }" value="Cadastrar CH Dedicada no Ensino EAD" onclick="setAba('consultas')"
									id="cadastrarCHDedicadaEAD">
								</h:commandLink>
							</li>
						</ul>
					</li>

					<li> Cadastro de Cidades P�los
						<ul>
							<li><a href="${ pageContext.request.contextPath }/ead/Polo/form.jsf?aba=consultas">Cadastrar</a></li>
							<li><a href="${ pageContext.request.contextPath }/ead/Polo/lista.jsf?aba=consultas">Alterar/Remover</a></li>
						</ul>
					</li>

				</ul>

				<%@include file="/graduacao/menus/consultas.jsp"%>

			</div>

			<div id="relatorios" class="aba">
				
				<ul>
					<li><a href="${ctx}/ead/relatorios/relatorio_horario_form.jsf?aba=relatorios">Hor�rios de Tutores e Discentes</a></li>
					<li><a href="${ctx}/ead/relatorios/relatorio_alunos_polo_form.jsf?aba=relatorios">Alunos por P�lo/Curso</a></li>
					<li><a href="${ctx}/ead/relatorios/exportar_alunos_form.jsf?aba=relatorios">Exportar alunos em TXT</a></li>
					<li><h:commandLink action="#{extrairDadosEadSql.selecionarTabelasSedis}" value="Extrair Dados de EAD (Gradua��o) em SQL" onclick="setAba('relatorios')" /></li>
					<li><h:commandLink action="#{extrairDadosEadSql.selecionarTabelasMetropoleDigital}" value="Extrair Dados da Metr�pole Digital em SQL" onclick="setAba('relatorios')"></h:commandLink></li>
					
					<li><h:commandLink action="#{relatorioAlunosEad.selecionarCursos}" value="Relat�rio de Alunos por Turma" onclick="setAba('relatorios')" /></li>
				</ul>
			</div>

		</div>
</h:form>
<c:set var="hideSubsistema" value="true" />

</f:view>
<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        	abas.addTab('pessoas', "Tutoria");
        	abas.addTab('alunos', "Alunos");
        	abas.addTab('coordenacao', "Coordena��o");
			abas.addTab('avaliacao', "Avalia��o");
			abas.addTab('consultas', "Gradua��o");
			abas.addTab('relatorios', "Relat�rios");
			<c:if test="${sessionScope.aba != null && sessionScope.aba != ''}">
		    	abas.activate('${sessionScope.aba}');
		    </c:if>
		    <c:if test="${sessionScope.aba == null}">
				abas.activate('pessoas');
			</c:if>
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAba(aba) {
	document.getElementById('aba').value = aba;
}
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<c:remove var="aba" scope="session"/>
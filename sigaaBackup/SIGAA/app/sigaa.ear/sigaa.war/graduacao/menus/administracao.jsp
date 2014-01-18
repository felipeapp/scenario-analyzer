	<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<ul>
	<li> Operações Administrativas
		<ul>
<%-- 	<li> <h:commandLink action="#{parametros.iniciarPermissoes}" value="Permissões" onclick="setAba('administracao')"/> </li> --%>
		<li> <h:commandLink action="#{parametros.iniciarDAE}" value="Parâmetros do Sistema" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{userBean.iniciarLogarComo}" value="Logar como Outro Usuário" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{calendario.iniciarDAE}" value="Calendário Universitário" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{jubilamentoMBean.iniciar}" value="Cancelamento de Alunos em Lote"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{alteracaoDataColacao.buscarDiscente}" value="Alterar Dados de Saída do Aluno"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{integralizacaoAlunoMigrado.buscarDiscente}" value="Integralizar Créditos de Aluno Migrado"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{mudancaCurricular.iniciarMudancaColetiva}" value="Mudança Coletiva de Currículo"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{mudancaColetivaMatrizCurricular.iniciarMudancaColetivaMatriz}" value="Mudança Coletiva de Matriz Curricular"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{alteracaoStatusDiscente.iniciar}" value="Alterar Status de Aluno"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink	action="#{consultaSolicitacoes.iniciar}" value="Anular Solicitações de Matrículas" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink	action="#{cadastroOfertaVagasCurso.iniciarGraduacao}" value="Cadastrar Oferta de Cursos para Processos Seletivos" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{discenteGraduacao.iniciarAcrescimoPerfilInicial}" value="Alterar Perfil Inicial do Aluno"  onclick="setAba('administracao')"/> </li>	
		<li> <h:commandLink action="#{fechamentoCompulsorioAtividades.iniciar}" value="Fechamento Compulsório de Atividades"  onclick="setAba('administracao')"/> </li>	
		</ul>
	</li>
	<li>Coordenadores de Curso
		<ul>
		<li> <h:commandLink action="#{coordenacaoCurso.iniciar}" value="Identificar Coordenador" onclick="setAba('administracao')" id="identificarCoordenador"/> </li>
		<li> <h:commandLink action="#{coordenacaoCurso.iniciarSubstituicao}" value="Alterar/Substituir/Cancelar Coordenador" onclick="setAba('administracao')" id="alterarSubstituirCancelarCoord"/> </li>
		<li> <h:commandLink action="#{coordenacaoCurso.listar}" value="Listar Coordenadores de Curso" onclick="setAba('administracao')" id="listarCoordenadoresDeCurso"/> </li>
		</ul>
	</li>
	<li> Secretaria de Departamento
		<ul>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarDepartamento}" value="Identificar Secretário" onclick="setAba('administracao')"/></li>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoDepartamento}" value="Substituir Secretário" onclick="setAba('administracao')"/> </li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_departamento.jsf?aba=administracao"> Listar Secretários </a></li>
		</ul>
	<li> Secretaria de Centro
		<ul>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarCentro}" value="Identificar Secretário" onclick="setAba('administracao')"/></li>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoCentro}" value="Substituir Secretário" onclick="setAba('administracao')"/> </li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_centro.jsf?aba=administracao"> Listar Secretários </a></li>
		</ul>
	<li>Secretaria de Coordenação de Curso
		<ul>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarCoordenacao}" value="Identificar Secretário" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoCoordenacao}" value="Substituir Secretário" onclick="setAba('administracao')"/> </li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_curso.jsf?aba=administracao"> Listar Secretários </a></li>
		</ul>
	</li>
	<li>Secretaria de Unidade Acadêmica Especializada
		<ul>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarUnidadeEspecializada}" value="Identificar Secretário" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoUnidadeEspecializada}" value="Substituir Secretário" onclick="setAba('administracao')"/> </li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_unidade_especializada.jsf?aba=administracao"> Listar Secretários </a></li>
		</ul>
	</li>
	
	<li>Docentes
		<ul>
		<li> <h:commandLink action="#{docenteExterno.preCadastrar}" value="Cadastrar Docente Externo" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{docenteExterno.iniciarAlterar}" value="Consultar Docentes Externos"  onclick="setAba('administracao')"/></li>
		<li> <ufrn:link action="administracao/docente_externo/lista.jsf" roles="<%=new int[] {SigaaPapeis.DAE} %>"> Cadastrar Usuário Para Docente Externo</ufrn:link> </li>
		<li> <a href="${ctx}/ead/pessoa/lista.jsf?aba=pessoas">Alterar Dados Pessoais </a> </li>
		</ul>
	</li>
	<li>Relatórios
		<ul>
		<li> <h:commandLink action="#{relatorioDiscente.gerarRelatorioListaAlunoConcluidoCreditoPendente}" value="Lista de Alunos Concluídos com Créditos Pendentes" onclick="setAba('administracao')"/> </li>
		</ul>
	</li>
	<li> Cadastramento e Reconvocações do Vestibular
		<ul>
		<li> <h:commandLink action="#{resumoConvocacaoVestibularMBean.iniciar}" value="Consultar Resumo de Convocações" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{cadastramentoDiscente.iniciar}" value="Gerenciar Cadastramento de Discentes" onclick="setAba('administracao')"/> </li>
		<!-- <li> <h:commandLink action="#{convocacaoVestibular.iniciar}" value="Convocação de Candidatos para Vagas Remanescentes" onclick="setAba('administracao')"/> </li>--> 
		<li> <h:commandLink action="#{convocacaoVagasRemanescentesVestibularMBean.iniciar}" value="Convocação de Candidatos para Vagas Remanescentes" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{documentosDiscentesConvocadosMBean.iniciarImpressaoDocumentos}" value="Gerar Documentos para Cadastramentos e Reconvocações" onclick="setAba('administracao')"/></li>
		<li> <h:commandLink action="#{estornoConvocacaoVestibularMBean.iniciar}" value="Estornar Cadastramento/Cancelamento de Convocação" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{convocacaoVagasRemanescentesVestibularMBean.efetivarCadastramento}" value="Efetivar o Cadastramento de Discentes PRÉ-CADASTRADO" onclick="setAba('administracao')"/> </li>
		<!-- <li> <h:commandLink action="#{convocacaoVagasRemanescentesVestibularMBean.encerrarCadastramento}" value="Encerrar o Cadastramento" onclick="setAba('administracao')"/> </li> -->
		</ul>
	</li>
	<li>Notificações Acadêmicas
		<ul>
		<li> <h:commandLink action="#{notificacaoAcademica.iniciar}" value="Notificações Acadêmicas"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{notificacaoAcademica.iniciarIndividual}" value="Notificação Individual"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{notificacaoAcademica.acompanhar}" value="Acompanhar Notificações Acadêmicas"  onclick="setAba('administracao')"/> </li>
		</ul>
	</li>
</ul>
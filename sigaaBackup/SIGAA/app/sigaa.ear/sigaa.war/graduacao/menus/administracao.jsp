	<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<ul>
	<li> Opera��es Administrativas
		<ul>
<%-- 	<li> <h:commandLink action="#{parametros.iniciarPermissoes}" value="Permiss�es" onclick="setAba('administracao')"/> </li> --%>
		<li> <h:commandLink action="#{parametros.iniciarDAE}" value="Par�metros do Sistema" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{userBean.iniciarLogarComo}" value="Logar como Outro Usu�rio" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{calendario.iniciarDAE}" value="Calend�rio Universit�rio" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{jubilamentoMBean.iniciar}" value="Cancelamento de Alunos em Lote"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{alteracaoDataColacao.buscarDiscente}" value="Alterar Dados de Sa�da do Aluno"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{integralizacaoAlunoMigrado.buscarDiscente}" value="Integralizar Cr�ditos de Aluno Migrado"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{mudancaCurricular.iniciarMudancaColetiva}" value="Mudan�a Coletiva de Curr�culo"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{mudancaColetivaMatrizCurricular.iniciarMudancaColetivaMatriz}" value="Mudan�a Coletiva de Matriz Curricular"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{alteracaoStatusDiscente.iniciar}" value="Alterar Status de Aluno"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink	action="#{consultaSolicitacoes.iniciar}" value="Anular Solicita��es de Matr�culas" onclick="setAba('dae-programa')"/> </li>
		<li> <h:commandLink	action="#{cadastroOfertaVagasCurso.iniciarGraduacao}" value="Cadastrar Oferta de Cursos para Processos Seletivos" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{discenteGraduacao.iniciarAcrescimoPerfilInicial}" value="Alterar Perfil Inicial do Aluno"  onclick="setAba('administracao')"/> </li>	
		<li> <h:commandLink action="#{fechamentoCompulsorioAtividades.iniciar}" value="Fechamento Compuls�rio de Atividades"  onclick="setAba('administracao')"/> </li>	
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
		<li> <h:commandLink action="#{secretariaUnidade.iniciarDepartamento}" value="Identificar Secret�rio" onclick="setAba('administracao')"/></li>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoDepartamento}" value="Substituir Secret�rio" onclick="setAba('administracao')"/> </li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_departamento.jsf?aba=administracao"> Listar Secret�rios </a></li>
		</ul>
	<li> Secretaria de Centro
		<ul>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarCentro}" value="Identificar Secret�rio" onclick="setAba('administracao')"/></li>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoCentro}" value="Substituir Secret�rio" onclick="setAba('administracao')"/> </li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_centro.jsf?aba=administracao"> Listar Secret�rios </a></li>
		</ul>
	<li>Secretaria de Coordena��o de Curso
		<ul>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarCoordenacao}" value="Identificar Secret�rio" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoCoordenacao}" value="Substituir Secret�rio" onclick="setAba('administracao')"/> </li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_curso.jsf?aba=administracao"> Listar Secret�rios </a></li>
		</ul>
	</li>
	<li>Secretaria de Unidade Acad�mica Especializada
		<ul>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarUnidadeEspecializada}" value="Identificar Secret�rio" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoUnidadeEspecializada}" value="Substituir Secret�rio" onclick="setAba('administracao')"/> </li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_unidade_especializada.jsf?aba=administracao"> Listar Secret�rios </a></li>
		</ul>
	</li>
	
	<li>Docentes
		<ul>
		<li> <h:commandLink action="#{docenteExterno.preCadastrar}" value="Cadastrar Docente Externo" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{docenteExterno.iniciarAlterar}" value="Consultar Docentes Externos"  onclick="setAba('administracao')"/></li>
		<li> <ufrn:link action="administracao/docente_externo/lista.jsf" roles="<%=new int[] {SigaaPapeis.DAE} %>"> Cadastrar Usu�rio Para Docente Externo</ufrn:link> </li>
		<li> <a href="${ctx}/ead/pessoa/lista.jsf?aba=pessoas">Alterar Dados Pessoais </a> </li>
		</ul>
	</li>
	<li>Relat�rios
		<ul>
		<li> <h:commandLink action="#{relatorioDiscente.gerarRelatorioListaAlunoConcluidoCreditoPendente}" value="Lista de Alunos Conclu�dos com Cr�ditos Pendentes" onclick="setAba('administracao')"/> </li>
		</ul>
	</li>
	<li> Cadastramento e Reconvoca��es do Vestibular
		<ul>
		<li> <h:commandLink action="#{resumoConvocacaoVestibularMBean.iniciar}" value="Consultar Resumo de Convoca��es" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{cadastramentoDiscente.iniciar}" value="Gerenciar Cadastramento de Discentes" onclick="setAba('administracao')"/> </li>
		<!-- <li> <h:commandLink action="#{convocacaoVestibular.iniciar}" value="Convoca��o de Candidatos para Vagas Remanescentes" onclick="setAba('administracao')"/> </li>--> 
		<li> <h:commandLink action="#{convocacaoVagasRemanescentesVestibularMBean.iniciar}" value="Convoca��o de Candidatos para Vagas Remanescentes" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{documentosDiscentesConvocadosMBean.iniciarImpressaoDocumentos}" value="Gerar Documentos para Cadastramentos e Reconvoca��es" onclick="setAba('administracao')"/></li>
		<li> <h:commandLink action="#{estornoConvocacaoVestibularMBean.iniciar}" value="Estornar Cadastramento/Cancelamento de Convoca��o" onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{convocacaoVagasRemanescentesVestibularMBean.efetivarCadastramento}" value="Efetivar o Cadastramento de Discentes PR�-CADASTRADO" onclick="setAba('administracao')"/> </li>
		<!-- <li> <h:commandLink action="#{convocacaoVagasRemanescentesVestibularMBean.encerrarCadastramento}" value="Encerrar o Cadastramento" onclick="setAba('administracao')"/> </li> -->
		</ul>
	</li>
	<li>Notifica��es Acad�micas
		<ul>
		<li> <h:commandLink action="#{notificacaoAcademica.iniciar}" value="Notifica��es Acad�micas"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{notificacaoAcademica.iniciarIndividual}" value="Notifica��o Individual"  onclick="setAba('administracao')"/> </li>
		<li> <h:commandLink action="#{notificacaoAcademica.acompanhar}" value="Acompanhar Notifica��es Acad�micas"  onclick="setAba('administracao')"/> </li>
		</ul>
	</li>
</ul>
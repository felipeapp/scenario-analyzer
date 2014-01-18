
<ul>
	<li> Aluno
		<ul>
			<li> <h:commandLink action="#{discenteGraduacao.popular}" value="Cadastrar Discente" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{ alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{historicoDiscente.iniciar}" value="Consultar Dados do Aluno" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{ observacaoDiscente.iniciar}" value="Editar Observações do Discente" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{ alterarFormaIngresso.iniciar}" value="Alterar Forma de Ingresso" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{aproveitamento.iniciarAproveitamento}" value="Aproveitar Componente Curricular" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{aproveitamento.iniciarCancelamento}" value="Excluir Aproveitamento" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{movimentacaoAluno.iniciarCancelamentoPrograma }" value="Cancelar Programa" onclick="setAba('cdp-cadastros')" /> </li>
			<li> <h:commandLink action="#{calculosDiscente.iniciar}" value="Cálculos de Discente"  onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink	action="#{matriculaGraduacao.iniciarCompulsoria }" value="Matrícula Compulsória" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matrícula" onclick="setAba('cdp-cadastros')"/></li>
			<li> <h:commandLink action="#{ historico.buscarDiscente }" value="Emitir Histórico" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink	action="#{consultaSolicitacoes.iniciar}" value="Consultar Solicitações de Matrículas" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink	action="#{alteracaoStatusMatricula.iniciar }" value="Alterar Status de Matrículas em Turmas e Aproveitamentos" onclick="setAba('dae-programa')" /> </li>
			<li> <h:commandLink action="#{ mergeDadosDiscenteMBean.iniciar }" onclick="setAba('cdp-cadastros')" value="Unificar Dados de Discentes"/> </li>				
		</ul>
	</li>
	<li> Operações Administrativas
		<ul>
			<li> <h:commandLink action="#{mudancaCurricular.iniciarMudancaColetiva}" value="Mudança de Currículo Coletiva"  onclick="setAba('cdp-cadastros')"/> </li>
		</ul>
	</li>
	<li>Curso
		<ul>
		<li> <h:commandLink value="Cadastrar" action="#{cursoGrad.preCadastrar}" onclick="setAba('cdp-cadastros')"/></li>
		<li> <a href="${ctx}/graduacao/curso/lista.jsf?aba=cdp-cadastros">Buscar/Alterar </a></li>
		</ul>
	</li>
	<li>Habilitação
		<ul>
		<li> <h:commandLink value="Cadastrar" action="#{habilitacaoGrad.preCadastrar}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <a href="${ctx}/graduacao/habilitacao/lista.jsf?aba=cdp-cadastros"> Buscar/Alterar</a> </li>
		</ul>
	</li>
	<li>Ênfase
		<ul>
		<li> <h:commandLink value="Cadastrar Somente a Ênfase" action="#{enfase.preCadastrarSimples}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <h:commandLink value="Listar/Alterar" action="#{enfase.listar}" onclick="setAba('cdp-cadastros')"/> </li>
		</ul>
	</li>
	<li>Matriz Curricular
		<ul>
		<li> <h:commandLink value="Cadastrar" action="#{matrizCurricular.preCadastrar}" onclick="setAba('cdp-cadastros')"/></li>
		<li> <h:commandLink value="Listar/Alterar" action="#{matrizCurricular.listar}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <h:commandLink value="Cadastrar matriz com ênfase, reaproveitando dados" action="#{enfase.preCadastrar}" onclick="setAba('cdp-cadastros')"/> </li>
		</ul>
	</li>
	<li>Componentes Curriculares
		<ul>
		<li> <h:commandLink value="Análise de Solicitações" action="#{autorizacaoComponente.iniciar}" onclick="setAba('cdp-cadastros')"/></li>		
		<li> <h:commandLink value="Ativar/Inativar Equivalências" action="#{componenteCurricular.listar}" onclick="setAba('cdp-cadastros')"/> </li>		
		<li> <h:commandLink value="Listar/Alterar" action="#{componenteCurricular.listar}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <h:commandLink value="Cadastrar" action="#{componenteCurricular.preCadastrar}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <h:commandLink value="Componentes com Expressões Inválidas" action="#{verificacaoExpressoesComponentesBean.listarInvalidos}" onclick="setAba('cdp-cadastros')"/></li>
		<li> <h:commandLink value="Inativar Componentes de Departamento" action="#{inativarComponentesDepartamentoMBean.iniciar}" onclick="setAba('cdp-cadastros')"/> </li>		
		</ul>
	</li>

	<li>Estrutura Curricular
		<ul>
		<li> <h:commandLink value="Cadastrar" action="#{curriculo.preCadastrar}" onclick="setAba('cdp-cadastros')"/></li>
		<li> <h:commandLink value="Listar/Alterar" action="#{curriculo.preListar}" onclick="setAba('cdp-cadastros')" id="botaoListarEstruturaCurricular"/> </li> 
		<li><h:commandLink value="Grupos de Optativas" action="#{grupoOptativasMBean.iniciar}"/></li>
		<li> <h:commandLink	action="#{recalculosMBean.iniciar}" value="Recálculo de Currículos" onclick="setAba('administracao')"/> </li>
		</ul>
	</li>

	<li> Equivalências Específicas
		<ul>
			<li> <h:commandLink value="Cadastrar" action="#{equivalenciaEspecificaMBean.preCadastrar}" onclick="setAba('cdp-cadastros')" id="botaoCadastrarEquivEspecifica"/> </li>
			<li> <h:commandLink value="Listar/Alterar" action="#{equivalenciaEspecificaMBean.listar}" onclick="setAba('cdp-cadastros')" id="botaoListarEquivEspecifica"/> </li>
		</ul>
	</li>

	<li> Programa de Educação Tutorial (PET)
		<ul>
			<li> <h:commandLink value="Cadastrar Grupo" action="#{petBean.preCadastrar}" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <a href="${ctx}/prodocente/atividades/Pet/lista.jsf?aba=cdp-cadastros"> Buscar/Alterar</a> </li>
		</ul>
	</li>
	<li>Turno
		<ul>
		<li> <a href="${ctx}/administracao/cadastro/Turno/form.jsf?aba=cdp-cadastros">Cadastrar </a></li>
		<li> <a href="${ctx}/administracao/cadastro/Turno/lista.jsf?aba=cdp-cadastros">Listar</a></li>
		</ul>
	</li>
	<li> Tutorias PET
		<ul>
			<li> <h:commandLink value="Cadastrar" action="#{tutoriaPet.preCadastrar}" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink value="Buscar/Alterar" action="#{tutoriaPet.listar}" onclick="setAba('cdp-cadastros')"/> </li>
		</ul>
	</li>

	<li>Forma de Participação do Aluno
		<ul>
		<li> <a	href="${ctx}/administracao/cadastro/ModalidadeEducacao/form.jsf?aba=cdp-cadastros">Cadastrar </a></li>
		<li> <a	href="${ctx}/administracao/cadastro/ModalidadeEducacao/lista.jsf?aba=cdp-cadastros">Listar</a></li>
		</ul>
	</li>
	<li>Modalidade
		<ul>
		<li> <a href="${ctx}/administracao/cadastro/GrauAcademico/form.jsf?aba=cdp-cadastros">Cadastrar </a></li>
		<li> <a	href="${ctx}/administracao/cadastro/GrauAcademico/lista.jsf?aba=cdp-cadastros">Listar/Alterar </a></li>
		</ul>
	</li>
	<li>Municípios
		<ul>
		<li> <a href="${ctx}/administracao/cadastro/Municipio/form.jsf?aba=cdp-cadastros">Cadastrar </a></li>
		<li> <a	href="${ctx}/administracao/cadastro/Municipio/lista.jsf?aba=cdp-cadastros">Listar </a></li>
		</ul>
	</li>
	<li>Reconhecimento
		<ul>
		<li> <h:commandLink value="Cadastrar" action="#{reconhecimento.preCadastrar}" onclick="setAba('cdp-cadastros')"/></li>
		<li> <h:commandLink value="Listar/Alterar" action="#{reconhecimento.listar}" onclick="setAba('cdp-cadastros')"/></li>
		</ul>
	</li>
	<li> Pólos de um Curso
		<ul>
			<li><a href="${ pageContext.request.contextPath }/ead/Polo/form.jsf?aba=cdp-cadastros">Cadastrar</a></li>
			<li><a href="${ pageContext.request.contextPath }/ead/Polo/lista.jsf?aba=cdp-cadastros">Buscar/Alterar</a></li>
		</ul>
	</li>
	<li> Índices Acadêmicos
		<ul>
			<li><a href="${ pageContext.request.contextPath }/ensino/IndiceAcademico/form.jsf?aba=cdp-cadastros">Cadastrar</a></li>
			<li><a href="${ pageContext.request.contextPath }/ensino/IndiceAcademico/lista.jsf?aba=cdp-cadastros">Buscar/Alterar</a></li>
		</ul>
	</li>
	
	<br/>
	<br/>
</ul>

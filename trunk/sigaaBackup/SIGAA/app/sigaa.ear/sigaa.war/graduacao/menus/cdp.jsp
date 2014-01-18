
<ul>
	<li> Aluno
		<ul>
			<li> <h:commandLink action="#{discenteGraduacao.popular}" value="Cadastrar Discente" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{ alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{historicoDiscente.iniciar}" value="Consultar Dados do Aluno" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{ observacaoDiscente.iniciar}" value="Editar Observa��es do Discente" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{ alterarFormaIngresso.iniciar}" value="Alterar Forma de Ingresso" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{aproveitamento.iniciarAproveitamento}" value="Aproveitar Componente Curricular" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{aproveitamento.iniciarCancelamento}" value="Excluir Aproveitamento" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{movimentacaoAluno.iniciarCancelamentoPrograma }" value="Cancelar Programa" onclick="setAba('cdp-cadastros')" /> </li>
			<li> <h:commandLink action="#{calculosDiscente.iniciar}" value="C�lculos de Discente"  onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink	action="#{matriculaGraduacao.iniciarCompulsoria }" value="Matr�cula Compuls�ria" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" onclick="setAba('cdp-cadastros')"/></li>
			<li> <h:commandLink action="#{ historico.buscarDiscente }" value="Emitir Hist�rico" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink	action="#{consultaSolicitacoes.iniciar}" value="Consultar Solicita��es de Matr�culas" onclick="setAba('cdp-cadastros')"/> </li>
			<li> <h:commandLink	action="#{alteracaoStatusMatricula.iniciar }" value="Alterar Status de Matr�culas em Turmas e Aproveitamentos" onclick="setAba('dae-programa')" /> </li>
			<li> <h:commandLink action="#{ mergeDadosDiscenteMBean.iniciar }" onclick="setAba('cdp-cadastros')" value="Unificar Dados de Discentes"/> </li>				
		</ul>
	</li>
	<li> Opera��es Administrativas
		<ul>
			<li> <h:commandLink action="#{mudancaCurricular.iniciarMudancaColetiva}" value="Mudan�a de Curr�culo Coletiva"  onclick="setAba('cdp-cadastros')"/> </li>
		</ul>
	</li>
	<li>Curso
		<ul>
		<li> <h:commandLink value="Cadastrar" action="#{cursoGrad.preCadastrar}" onclick="setAba('cdp-cadastros')"/></li>
		<li> <a href="${ctx}/graduacao/curso/lista.jsf?aba=cdp-cadastros">Buscar/Alterar </a></li>
		</ul>
	</li>
	<li>Habilita��o
		<ul>
		<li> <h:commandLink value="Cadastrar" action="#{habilitacaoGrad.preCadastrar}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <a href="${ctx}/graduacao/habilitacao/lista.jsf?aba=cdp-cadastros"> Buscar/Alterar</a> </li>
		</ul>
	</li>
	<li>�nfase
		<ul>
		<li> <h:commandLink value="Cadastrar Somente a �nfase" action="#{enfase.preCadastrarSimples}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <h:commandLink value="Listar/Alterar" action="#{enfase.listar}" onclick="setAba('cdp-cadastros')"/> </li>
		</ul>
	</li>
	<li>Matriz Curricular
		<ul>
		<li> <h:commandLink value="Cadastrar" action="#{matrizCurricular.preCadastrar}" onclick="setAba('cdp-cadastros')"/></li>
		<li> <h:commandLink value="Listar/Alterar" action="#{matrizCurricular.listar}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <h:commandLink value="Cadastrar matriz com �nfase, reaproveitando dados" action="#{enfase.preCadastrar}" onclick="setAba('cdp-cadastros')"/> </li>
		</ul>
	</li>
	<li>Componentes Curriculares
		<ul>
		<li> <h:commandLink value="An�lise de Solicita��es" action="#{autorizacaoComponente.iniciar}" onclick="setAba('cdp-cadastros')"/></li>		
		<li> <h:commandLink value="Ativar/Inativar Equival�ncias" action="#{componenteCurricular.listar}" onclick="setAba('cdp-cadastros')"/> </li>		
		<li> <h:commandLink value="Listar/Alterar" action="#{componenteCurricular.listar}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <h:commandLink value="Cadastrar" action="#{componenteCurricular.preCadastrar}" onclick="setAba('cdp-cadastros')"/> </li>
		<li> <h:commandLink value="Componentes com Express�es Inv�lidas" action="#{verificacaoExpressoesComponentesBean.listarInvalidos}" onclick="setAba('cdp-cadastros')"/></li>
		<li> <h:commandLink value="Inativar Componentes de Departamento" action="#{inativarComponentesDepartamentoMBean.iniciar}" onclick="setAba('cdp-cadastros')"/> </li>		
		</ul>
	</li>

	<li>Estrutura Curricular
		<ul>
		<li> <h:commandLink value="Cadastrar" action="#{curriculo.preCadastrar}" onclick="setAba('cdp-cadastros')"/></li>
		<li> <h:commandLink value="Listar/Alterar" action="#{curriculo.preListar}" onclick="setAba('cdp-cadastros')" id="botaoListarEstruturaCurricular"/> </li> 
		<li><h:commandLink value="Grupos de Optativas" action="#{grupoOptativasMBean.iniciar}"/></li>
		<li> <h:commandLink	action="#{recalculosMBean.iniciar}" value="Rec�lculo de Curr�culos" onclick="setAba('administracao')"/> </li>
		</ul>
	</li>

	<li> Equival�ncias Espec�ficas
		<ul>
			<li> <h:commandLink value="Cadastrar" action="#{equivalenciaEspecificaMBean.preCadastrar}" onclick="setAba('cdp-cadastros')" id="botaoCadastrarEquivEspecifica"/> </li>
			<li> <h:commandLink value="Listar/Alterar" action="#{equivalenciaEspecificaMBean.listar}" onclick="setAba('cdp-cadastros')" id="botaoListarEquivEspecifica"/> </li>
		</ul>
	</li>

	<li> Programa de Educa��o Tutorial (PET)
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

	<li>Forma de Participa��o do Aluno
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
	<li>Munic�pios
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
	<li> P�los de um Curso
		<ul>
			<li><a href="${ pageContext.request.contextPath }/ead/Polo/form.jsf?aba=cdp-cadastros">Cadastrar</a></li>
			<li><a href="${ pageContext.request.contextPath }/ead/Polo/lista.jsf?aba=cdp-cadastros">Buscar/Alterar</a></li>
		</ul>
	</li>
	<li> �ndices Acad�micos
		<ul>
			<li><a href="${ pageContext.request.contextPath }/ensino/IndiceAcademico/form.jsf?aba=cdp-cadastros">Cadastrar</a></li>
			<li><a href="${ pageContext.request.contextPath }/ensino/IndiceAcademico/lista.jsf?aba=cdp-cadastros">Buscar/Alterar</a></li>
		</ul>
	</li>
	
	<br/>
	<br/>
</ul>

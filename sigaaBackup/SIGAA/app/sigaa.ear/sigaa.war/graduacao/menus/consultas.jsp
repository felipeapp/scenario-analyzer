
<ul>
	<li> Consultas Gerais
		<ul>
		<li> <h:commandLink action="#{historicoDiscente.iniciar}" value="Alunos" onclick="setAba('consultas')"/> </li>
		<li> <h:commandLink action="#{ buscaAvancadaDiscenteMBean.iniciar}" value="Consulta Geral de Discentes"/></li>
		<li> <h:commandLink action="#{ componenteCurricular.popularBuscaGeral }" value="Componentes Curriculares" onclick="setAba('consultas')"/> </li>
		<li> <h:commandLink action="#{ cursoGrad.listar }" value="Cursos" onclick="setAba('consultas')"/> </li>		
		<li> <h:commandLink value="Estruturas Curriculares" action="#{curriculo.preListar}" onclick="setAba('consultas')" id="consultaEstruturaCurricular"/> </li>
		<li> <a	href="${ctx}/administracao/cadastro/GrauAcademico/lista.jsf?aba=consultas">Graus Acadêmicos </a></li>
		<li> <h:commandLink action="#{ habilitacaoGrad.listar }" value="Habilitações" onclick="setAba('consultas')"/> </li>
		<li> <h:commandLink action="#{ matrizCurricular.listar }" value="Matrizes Curriculares" onclick="setAba('consultas')"/> </li>		
		<li> <a	href="${ctx}/administracao/cadastro/ModalidadeEducacao/lista.jsf?aba=consultas">Modalidades de Educação</a></li>
		<li> <h:commandLink action="#{ municipio.buscar }" value="Municípios" onclick="setAba('consultas')"/></li>
		<li> <h:commandLink action="#{ cadastroOfertaVagasCurso.listarGraduacao }" onclick="setAba('consultas')" value="Oferta de Vagas em Cursos"/> </li>
		<li> <h:commandLink action="#{ orientacaoAtividade.iniciarBusca }" value="Orientação de Atividades" onclick="setAba('consultas')"/> </li>
		<li> <a href="${ctx}/graduacao/reconhecimento/lista.jsf?aba=consultas">Reconhecimentos</a></li>		
		<c:if test="${servidor.permissaoConsultarServidor}"> <li> <h:commandLink action="#{ servidor.listar }" value="Servidores" onclick="setAba('consultas')"/> </li> </c:if>		
		<li> <h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Turmas" onclick="setAba('consultas')"/> </li>
		<li> <a href="${ctx}/administracao/cadastro/Turno/lista.jsf?aba=consultas">Turnos</a></li>
		<li> <h:commandLink action="#{ indiceAcademicoMBean.buscarIndicesDiscente }" onclick="setAba('consultas')" value="Índices Acadêmicos do Aluno"/> </li>
		</ul>
	</li>
</ul>
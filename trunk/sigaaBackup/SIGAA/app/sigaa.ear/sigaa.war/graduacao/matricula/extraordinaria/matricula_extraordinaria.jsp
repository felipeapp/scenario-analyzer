<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<style>
	span.periodo {
		color: #292;
		font-weight: bold;
	}

	descricaoOperacao p{
		line-height: 1.25em;
		margin: 8px 10px;
	}
	
	b.enfase { color: #922; }
</style>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2>
		<ufrn:subSistema /> &gt; Matrícula ${matriculaExtraordinaria.ferias ?'em Turma de Férias':''} Extraordinária
	</h2>

	<div class="descricaoOperacao">
		<h4>Caro(a) Aluno(a),</h4> <br />
		<p>
			A matrícula extraordinária tem a finalidade de preencher as vagas remanescentes nas turmas ofertadas para o período 
			${matriculaExtraordinaria.ferias ? 'de férias vigente' : 'letivo atual' }.
			O período dessa matrícula estende-se de
			<span class="periodo">
				<c:choose>
				<c:when test="${matriculaExtraordinaria.ferias}">
					<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaExtraordinariaFerias}"/>
			 		a <b><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaExtraordinariaFerias}"/></b>
				</c:when>
				<c:otherwise>
					<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaExtraordinaria}"/>
			 		a <b><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaExtraordinaria}"/></b>
				</c:otherwise>	
				</c:choose>
		 	</span>.
		</p>
		<p>
			Lembramos que, assim como na matrícula regular, a escolha das turmas está sujeita às regras do
			<a href="${linkPublico.urlDownloadPublico}/regulamento_dos_cursos_de_graduacao.pdf" target="_blank">	Regulamento dos Cursos de Graduação	</a>
			tais como a verificação de pré-requisitos e co-requisitos, as matrículas em componentes
			equivalentes, bem como o choque de horários com as turmas já matriculadas. Vale ressaltar ainda que essa operação permite somente a matrícula
			em componentes curriculares que formam turmas, que permitem matrícula on-line pelo próprio aluno e que possuem turmas abertas 
			<b style="color: #922;">com vagas remanescentes</b> no 
			<c:if test="${not matriculaExtraordinaria.ferias}">ano-semestre	de ${calendarioAcademico.anoPeriodo}</c:if>
			<c:if test="${matriculaExtraordinaria.ferias}">período de férias ${calendarioAcademico.anoPeriodoFeriasVigente}</c:if>.
		</p>
		<p>
			Atenção para algumas particularidades da matrícula extraordinária:
			<ol>
				<li>O aluno pode apenas adicionar novas matrículas. Não é possível excluir ou cancelar turmas onde já está matriculado.</li>
				<li>Só é possível tentar se matricular em 01 (uma) turma de cada vez. Isto exclui a possibilidade de fazer matrícula extraordinária em
     			componentes curriculares do tipo BLOCO ou em componentes curriculares que sejam mutuamente correquisitos 
     			(um é correquisito do outro, e vice-versa).</li>
				<li>O processamento da matrícula <b class="enfase">ocorrerá imediatamente</b> após sua confirmação da escolha da turma. 
				Caso passe em todas as verificações, você <b class="enfase">estará automaticamente MATRICULADO</b> na turma que selecionar.
				Isso significa que você não poderá desistir de cursar o componente curricular a não ser que efetue um <b class="enfase">TRANCAMENTO</b>, posteriormente;</li>
				<li>O critério para a ocupação das vagas é a <b class="enfase">ordem de chegada</b>, ou seja, quem confirmar a escolha da turma
				e passar em todas as verificações primeiro, ocupará a vaga. Dessa forma, confirmar a escolha da turma <b class="enfase">NÃO garante</b> que a
				matrícula será efetuada, o aluno deve confirmar no histórico se a matrícula foi feita, pois existe a possibilidade pequena que um outro aluno esteja efetuando
     			um procedimento similar ao mesmo tempo, tenha confirmado a escolha na mesma turma instantes antes e ocupe a vaga primeiro.</li>
     			<li>Como o preenchimento das vagas remanescentes é por ordem de chegada, tanto as prioridades (os nivelados antes dos atrasados,
     			etc.) quanto as reservas por curso <b class="enfase">NÃO SÃO LEVADAS EM CONTA</b> na matrícula extraordinária. Isto apenas repete o que já acontecia
     			anteriormente, nos pedidos extraordinários enviados em papel ("matrícula fora de prazo").</li>
     			<li>O aluno <b class="enfase">RECEBERÁ FALTAS</b> para todos os dias de aula anteriores a matrícula que o professor já lançou frequência.</li>
			</ol>
		</p>
		<p>
			Utilize o formulário abaixo para buscar por turmas que ainda possuem vagas remanescentes. Da lista de
			resultados da busca, selecione uma turma para tentar se matricular clicando no ícone correspondente.
		</p>
	</div>

	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Buscar Turmas com Vagas Remanescentes</caption>
			<tbody>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkCodigo" value="#{matriculaExtraordinaria.boolDadosBusca[0]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkCodigo">Código do Componente:</label></td>
					<td><h:inputText size="10" maxlength="8" value="#{matriculaExtraordinaria.dadosBuscaTurma.disciplina.codigo }"
						onfocus="marcaCheckBox('form:checkCodigo')" onkeyup="CAPS(this)" id="txtCodigo"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkNome" value="#{matriculaExtraordinaria.boolDadosBusca[1]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkNome">Nome do Componente:</label></td>
					<td><h:inputText size="60" maxlength="100" value="#{matriculaExtraordinaria.dadosBuscaTurma.disciplina.nome }"
						onfocus="marcaCheckBox('form:checkNome')" onkeyup="CAPS(this)" id="txtNome"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkHorario" value="#{matriculaExtraordinaria.boolDadosBusca[2]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkHorario">Horário:</label></td>
					<td><h:inputText size="10" maxlength="20" value="#{matriculaExtraordinaria.dadosBuscaTurma.descricaoHorario}"
						onfocus="marcaCheckBox('form:checkHorario')" onkeyup="CAPS(this)" id="txtHorario"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkNomeDocente" value="#{matriculaExtraordinaria.boolDadosBusca[3]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkNomeDocente">Nome do Docente:</label></td>
					<td><h:inputText size="60" maxlength="100" value="#{matriculaExtraordinaria.dadosBuscaTurma.nomesDocentes}"
						onfocus="marcaCheckBox('form:checkNomeDocente')" onkeyup="CAPS(this)" id="txtNomeDocente"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkUnidade" value="#{matriculaExtraordinaria.boolDadosBusca[4]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkUnidade">Unidade Responsável:</label></td>
					<td><h:selectOneMenu style="width: 400px"
						value="#{matriculaExtraordinaria.dadosBuscaTurma.disciplina.unidade.id}"
						onfocus="marcaCheckBox('form:checkUnidade')" id="comboDepartamento">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.allDeptosEscolasCombo}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{matriculaExtraordinaria.buscarTurmasVagasRemanescentes}" id="buscar"/>
						<h:commandButton value="Cancelar" action="#{matriculaExtraordinaria.cancelar}" onclick="#{confirm}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<c:if test="${empty matriculaExtraordinaria.resultadoTurmasBuscadas}">
		<br/><center style="font-style: italic;">
			Informe critérios para refinar a busca de turmas abertas.
			</center>
		</c:if>
		<c:if test="${not empty matriculaExtraordinaria.resultadoTurmasBuscadas}">
			<br/>
			<center>
				<div class="infoAltRem">
					<img src="/sigaa/img/graduacao/matriculas/zoom.png" style="overflow: visible;">: Ver detalhes da turma
					<img src="/sigaa/img/seta.gif" style="overflow: visible;">: Selecionar turma
				</div>
			</center>

			<%-- TURMAS ENCONTRADAS --%>
			<table class="subFormulario" id="lista-turmas-extra">
				<caption>Turmas Encontradas (${ fn:length(matriculaExtraordinaria.resultadoTurmasBuscadas) })</caption>

				<thead>
					<tr>
						<th> </th>
						<th> Turma </th>
						<th> Docente(s) </th>
						<th> Tipo </th>
						<th> Horário </th>
						<th> Local </th>
						<th> Capacidade </th>
						<th> Vagas </th>
						<th> </th>
					</tr>
				</thead>
			
			<tbody>
				<c:if test="${not empty matriculaExtraordinaria.resultadoTurmasBuscadas}">
					<c:set var="disciplinaAtual" value="0" />
					<c:forEach items="#{matriculaExtraordinaria.resultadoTurmasBuscadas}" var="turma" varStatus="s">
		
						<%-- Componente Curricular --%>
						<c:if test="${ disciplinaAtual != turma.disciplina.id}">
							<c:set var="disciplinaAtual" value="${turma.disciplina.id}" />
							<tr class="disciplina" >
							<td colspan="9" style="font-variant: small-caps;">
								<a href="javascript:void(0);" onclick="PainelComponente.show(${turma.disciplina.id});"
									title="Ver Detalhes do Componente Curricular">
								${turma.disciplina.codigo} - ${turma.disciplina.detalhes.nome}
								</a>
							</td></tr>
						</c:if>
		
						<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small" id="turma_${turma.id}TR">
							<td width="2%">
								<a href="javascript:void(0);" onclick="PainelTurma.show(${turma.id});" title="Ver Detalhes dessa turma">
									<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt="Ver detalhes da turma" class="noborder" />
								</a>
							</td>
							<td width="8%">Turma ${turma.codigo}</td>
							<td>${turma.docentesNomes}</td>
							<td width="10%">${turma.tipoString}</td>
							<td width="10%">${turma.descricaoHorario}</td>
							<td width="10%">${turma.local}</td>
							<td width="10%">${turma.capacidadeAluno} alunos</td>
							<td width="10%">${turma.qtdVagasDisponiveis} vagas</td>
							<td width="2%">
								<h:commandLink id="selecionarTurma" title="Selecionar turma" 
										action="#{matriculaExtraordinaria.selecionarTurma}">
									<f:param name="idTurma" value="#{turma.id}"/>
									<h:graphicImage url="/img/seta.gif" alt="Selecionar turma" title="Selecionar turma"/>
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</c:if>
			</tbody>
		</table>
		</c:if>

	</h:form>



</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

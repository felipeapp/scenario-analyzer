<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<f:view>
	<c:set value="outras_turmas" var="pagina"></c:set>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<div class="descricaoOperacao">
	Utilize o formulário abaixo para buscar por turmas abertas. Da lista de
	Resultados da Busca, selecione uma ou mais turmas da lista abaixo e
	confirme a seleção através do botão <b>Adicionar Turmas</b>, localizado
	no final desta página.</div>
	<%@ include file="/graduacao/matricula/cabecalho_botoes_superiores.jsp"%>
	<%@ include file="_info_discente.jsp"%>


	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Buscar Turmas Abertas</caption>
			<tbody>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkCodigo" value="#{matriculaGraduacao.boolDadosBusca[0]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkCodigo">Código do Componente:</label></td>
					<td><h:inputText size="10" maxlength="9" value="#{matriculaGraduacao.dadosBuscaTurma.disciplina.codigo }"
						onfocus="marcaCheckBox('form:checkCodigo')" onkeyup="CAPS(this)" id="txtCodigo"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkNome" value="#{matriculaGraduacao.boolDadosBusca[1]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkNome">Nome do Componente:</label></td>
					<td><h:inputText size="60" maxlength="100" value="#{matriculaGraduacao.dadosBuscaTurma.disciplina.nome }"
						onfocus="marcaCheckBox('form:checkNome')" onkeyup="CAPS(this);" id="txtNome"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkHorario" value="#{matriculaGraduacao.boolDadosBusca[2]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkHorario">Horário:</label></td>
					<td><h:inputText size="10" maxlength="20" value="#{matriculaGraduacao.dadosBuscaTurma.descricaoHorario}"
						onchange="marcaCheckBox('form:checkHorario')" onkeyup="CAPS(this)" id="txtHorario"/></td>
				</tr>
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox id="checkNomeDocente" value="#{matriculaGraduacao.boolDadosBusca[3]}" styleClass="noborder" />
					</td>
					<td><label for="form:checkNomeDocente">Nome do Docente:</label></td>
					<td><h:inputText size="60" maxlength="100" value="#{matriculaGraduacao.dadosBuscaTurma.nomesDocentes}"
						onchange="marcaCheckBox('form:checkNomeDocente')" onkeyup="CAPS(this)" id="txtNomeDocente"/></td>
				</tr>
				<c:if test="${!matriculaGraduacao.alunoEspecial}">
					<c:if test="${!matriculaGraduacao.discente.stricto}">
					<tr>
						<td width="5%">
							<h:selectBooleanCheckbox id="checkUnidade" value="#{matriculaGraduacao.boolDadosBusca[4]}" styleClass="noborder" />
						</td>
						<td><label for="form:checkUnidade">Unidade Responsável:</label></td>
						<td><h:selectOneMenu style="width: 400px"
							value="#{matriculaGraduacao.dadosBuscaTurma.disciplina.unidade.id}"
							onchange="marcaCheckBox('form:checkUnidade')" id="comboDepartamento">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{unidade.allDeptosEscolasCoordCursosCombo}" />
						</h:selectOneMenu></td>
					</tr>
					</c:if>
					<c:if test="${matriculaGraduacao.discente.stricto}">
					<tr>
						<td width="5%">
							<h:selectBooleanCheckbox id="checkAnoPeriodo" value="#{matriculaGraduacao.boolDadosBusca[5]}" styleClass="noborder" />
						</td>
						<td><label for="form:checkAnoPeriodo">Ano-Período:</label></td>
						<td>
							<h:inputText size="4" maxlength="4" value="#{matriculaGraduacao.dadosBuscaTurma.ano}"
							onchange="marcaCheckBox('form:checkAnoPeriodo')" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="txtAno"/> -
							<h:inputText size="1" maxlength="1" value="#{matriculaGraduacao.dadosBuscaTurma.periodo}"
							onchange="marcaCheckBox('form:checkAnoPeriodo')" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" id="txtperiodo"/>
						</td>
					</tr>
					</c:if>
					<c:if test="${matriculaGraduacao.discente.stricto and acesso.ppg}">
					<tr>
						<td width="5%">
							<h:selectBooleanCheckbox id="checkUnidade" value="#{matriculaGraduacao.boolDadosBusca[4]}" styleClass="noborder" />
						</td>
						<td><label for="form:checkUnidade">Unidade Responsável:</label></td>
						<td><h:selectOneMenu style="width: 400px" 
							value="#{matriculaGraduacao.dadosBuscaTurma.disciplina.unidade.id}"
							onchange="marcaCheckBox('form:checkUnidade')" id="comboDepartamento">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{unidade.allProgramaPosCombo}" />
						</h:selectOneMenu></td>
					</tr>
					</c:if>
					<c:if test="${matriculaGraduacao.discente.stricto and !acesso.ppg}">
					<tr>
					
						<c:if test="${(matriculaGraduacao.permiteMatriculaRegularEmOutrosProgramas)}">
						<td width="5%">
							<h:selectBooleanCheckbox id="checkUnidade" value="#{matriculaGraduacao.boolDadosBusca[4]}" styleClass="noborder" />
						</td>
						<td><label for="form:checkUnidade">Unidade Responsável:</label></td>
						<td><h:selectOneMenu style="width: 400px" 
							value="#{matriculaGraduacao.dadosBuscaTurma.disciplina.unidade.id}"
							onchange="marcaCheckBox('form:checkUnidade')" id="comboDepartamento">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{unidade.allProgramaPosCombo}" />
						</h:selectOneMenu></td>
						</c:if>
						
						<c:if test="${!(matriculaGraduacao.permiteMatriculaRegularEmOutrosProgramas)}">
						<td width="5%">
						</td>
						<td class="label">Unidade Responsável:</td>
						<td>
							${matriculaGraduacao.dadosBuscaTurma.disciplina.unidade.nome}
						</td>
						</c:if>
						
					</tr>
					</c:if>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{matriculaGraduacao.buscarOutrasTurmas}" id="buscar"/></td>
				</tr>
			</tfoot>
		</table>
		<c:if test="${empty resultadoTurmasBuscadas}">
			<br><center style="font-style: italic;">
				Informe critérios para refinar a busca de turmas abertas.
				</center>
		</c:if>
		<c:if test="${not empty resultadoTurmasBuscadas}">
			<br>

			<div class="infoAltRem">
				<img src="/sigaa/img/graduacao/matriculas/zoom.png">: Ver detalhes da turma
				<img src="/sigaa/img/graduacao/matriculas/matricula_tem_reservas.png">: Possui vagas reservadas para seu curso
				<img src="/sigaa/img/graduacao/matriculas/matricula_negada.png">: Não é permitida a matrícula do discente na turma
			</div>

			<%-- TURMAS ENCONTRADAS --%>
			<table class="listagem" id="lista-turmas-extra">
			<caption>Turmas Abertas Encontradas
			<c:if test="${matriculaGraduacao.discente.stricto and matriculaGraduacao.primeiraTurmaBusca != null}">
				de ${matriculaGraduacao.primeiraTurmaBusca.anoPeriodo}
			</c:if>
			</caption>

			<thead>
			<tr>
				<th colspan="3"> </th>
				<th> Turma </th>
				<th> Docente(s) </th>
				<th> Tipo </th>
				<th> ${matriculaGraduacao.matriculaEAD ? '' : 'Horário'} </th>
				<th> Local </th>
				<th> ${matriculaGraduacao.matriculaEAD ? '' : 'Capacidade'} </th>
			</tr>
			</thead>
			
			<tbody>
			<c:if test="${not empty resultadoTurmasBuscadas}">
			<c:set var="disciplinaAtual" value="0" />
			<c:forEach items="#{resultadoTurmasBuscadas}" var="turma" varStatus="s">

				<%-- Componente Curricular --%>
				<c:if test="${ disciplinaAtual != turma.disciplina.id}">
					<c:set var="disciplinaAtual" value="${turma.disciplina.id}" />
					<tr class="disciplina" >
						<td colspan="9" style="font-variant: small-caps;">
							<c:if test="${!turma.podeMatricular}">
								<ufrn:help img="/img/graduacao/matriculas/matricula_negada.png" width="400">
									<li>O discente já se encontra matriculado nesse componente (ou equivalentes)</li>
								</ufrn:help>
							</c:if>
							<a href="javascript:void(0);" onclick="PainelComponente.show(${turma.disciplina.id});"  style="${turma.podeMatricular?'':'color: #666'}"
								title="Ver Detalhes do Componente Curricular">
							${turma.disciplina.codigo} - ${turma.disciplina.detalhes.nome}
							</a>
						</td>
					</tr>
				</c:if>

				<c:set value="turma_${turma.id}CHK" var="idCheckbox"></c:set>
				<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small" id="turma_${turma.id}TR">
					<td width="2%">
						<c:if test="${not empty turma.reservas}">
							<ufrn:help img="/img/graduacao/matriculas/matricula_tem_reservas.png">
								<li>Essa turma possui vagas reservadas para seu curso</li>
							</ufrn:help>
						</c:if>
					</td>
					<td width="2%">
						<a href="javascript:void(0);" onclick="PainelTurma.show(${turma.id});" title="Ver Detalhes dessa turma">
							<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt="Ver detalhes da turma" class="noborder" />
						</a>
					</td>
					<td width="2%">
						<c:if test="${turma.selecionada}">
							<input type="checkbox" name="selecaoTurmas" onclick="markCheck(this)" value="${turma.id}" id="${idCheckbox}" class="noborder"
							${ turma.selecionada ? "checked=\"checked\" ": " " } disabled="true" />
						</c:if>
						<c:if test="${!turma.selecionada && turma.podeMatricular}">
							<input type="checkbox" name="selecaoTurmas" onclick="markCheck(this)" value="${turma.id}" id="${idCheckbox}" class="noborder"
							${ turma.selecionada ? "checked=\"checked\" ": " " } />
						</c:if>
					</td>
					<td width="8%"><label for="${idCheckbox}">Turma ${turma.codigo}</label></td>
					<td><label for="${idCheckbox}">${turma.docentesNomes}</label></td>
					<td width="10%"><label for="${idCheckbox}">${turma.tipoString}</label></td>
					<td width="10%"><label for="${idCheckbox}">${turma.descricaoHorario}</label></td>
					<td width="10%"><label for="${idCheckbox}">${turma.local}</label></td>
					<td width="10%">
						<c:if test="${not matriculaGraduacao.matriculaEAD}">
							<label for="${idCheckbox}">${turma.capacidadeAluno} alunos</label>
						</c:if>
					</td>
				</tr>
			</c:forEach>
			<tfoot>
				<tr>
					<td colspan="9" align="center">
						<div class="botoes confirmacao">
							<h:commandButton title="Adicionar as turmas selecionadas à solicitação de matrícula" 
								image="/img/graduacao/matriculas/adicionar.gif"
								action="#{matriculaGraduacao.selecionarTurmas}" id="cmdTurmas"/><br />
							<h:commandLink value="Adicionar Turmas" action="#{matriculaGraduacao.selecionarTurmas}" id="adicionarTurmas"></h:commandLink>
						</div>
					</td>
				</tr>
			</tfoot>
			</c:if>
		</table>
		</c:if>

	</h:form>



</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

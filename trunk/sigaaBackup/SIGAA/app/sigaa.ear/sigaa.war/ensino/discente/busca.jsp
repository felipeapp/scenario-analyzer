<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Busca de Discentes</h2>

<f:view>

<h:form id="busca">

<div class="descricaoOperacao">
Para utilizar a busca avançada, escolha um ou mais critérios entre os listados abaixo e clique no botão "Buscar".
Se o nível de ensino for selecionado, mais critérios de busca aparecerão para o nível escolhido. Para gerar um
relatório com o resultado da busca, marque a opção "Trazer informações em formato de relatório".
</div>

<table class="formulario" width="90%">
	<caption>Opções de Busca </caption>
	<tbody>
		<tr>
			<td><h:selectBooleanCheckbox id="matricula" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaMatricula }" /></td>
			<td>Matrícula:</td>
			<td><h:inputText id="inputMatricula" value="#{ buscaAvancadaDiscenteMBean.parametros.matricula }" size="10" maxlength="15" onfocus="Field.check('busca:matricula')" onkeyup="return formatarInteiro(this);" /></td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="nome" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaNome }"/></td>
			<td>Nome:</td>
			<td><h:inputText id="inputNome" value="#{ buscaAvancadaDiscenteMBean.parametros.nome }" size="50" onfocus="Field.check('busca:nome')"/></td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="idade" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaIdade }"/></td>
			<td>Idade:</td>
			<td>de <h:inputText id="inputIdadeDe" value="#{ buscaAvancadaDiscenteMBean.parametros.idadeDe }" size="2" maxlength="3" onfocus="Field.check('busca:idade')" onkeyup="return formatarInteiro(this);"/> até
			<h:inputText id="inputIdadeAte" value="#{ buscaAvancadaDiscenteMBean.parametros.idadeAte }" size="2" maxlength="3" onfocus="Field.check('busca:idade')" onkeyup="return formatarInteiro(this);" /> anos</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="sexo" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaSexo }"/></td>
			<td>Sexo:</td>
			<td><h:selectOneMenu id="selectSexo" value="#{ buscaAvancadaDiscenteMBean.parametros.sexo }" onfocus="Field.check('busca:sexo')">
				<f:selectItem itemLabel="Ambos" itemValue=""/>
				<f:selectItems value="#{ buscaAvancadaDiscenteMBean.mascFem }"/>
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="tipo" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaTipo }"/></td>
			<td>Tipo:</td>
			<td>
				<h:selectOneMenu id="selectTipo" value="#{ buscaAvancadaDiscenteMBean.parametros.tipo }" onfocus="Field.check('busca:tipo')">
					<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
					<f:selectItem itemLabel="Regular" itemValue="1"/>
					<f:selectItem itemLabel="Especial" itemValue="2"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<td>
				<c:if test="${ fn:length(buscaAvancadaDiscenteMBean.niveisCombo) > 1 }">
					<h:selectBooleanCheckbox id="nivel" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaNivel }"/>
				</c:if>
				<c:if test="${ fn:length(buscaAvancadaDiscenteMBean.niveisCombo) <= 1 }">
					<h:inputHidden id="hiddenRestricoesNivel" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaNivel }"/>
				</c:if>
			</td>
			<td>Nível de Ensino:</td>
			<td>
				<c:if test="${ fn:length(buscaAvancadaDiscenteMBean.niveisCombo) > 1 }">
					<h:selectOneMenu id="niveis" value="#{ buscaAvancadaDiscenteMBean.parametros.nivel }" onchange="selecionaNivel()" onfocus="Field.check('busca:nivel')">
						<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
						<f:selectItems value="#{ buscaAvancadaDiscenteMBean.niveisCombo }"/>
					</h:selectOneMenu>
				</c:if>
				<c:if test="${ fn:length(buscaAvancadaDiscenteMBean.niveisCombo) <= 1 }">
					<h:inputHidden id="niveis" value="#{ buscaAvancadaDiscenteMBean.parametros.nivel }"/>
					${ buscaAvancadaDiscenteMBean.parametros.nivelDesc }
				</c:if> 
			</td>
		</tr>
 		<tr>
			<td width="20"><h:selectBooleanCheckbox id="status" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaStatus }"/></td>
			<td width="180">Status:</td>
			<td>
				<h:selectOneMenu id="selectStatus" value="#{ buscaAvancadaDiscenteMBean.parametros.status }" onfocus="Field.check('busca:status')">
					<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
					<f:selectItems value="#{ statusDiscente.allCombo }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
		<tr>
			<td width="20"><h:selectBooleanCheckbox id="formaIngresso" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaFormaIngresso }"/></td>
			<td width="180">Forma de Ingresso:</td>
			<td>
				<h:selectOneMenu id="selectFormaIngresso" value="#{ buscaAvancadaDiscenteMBean.parametros.formaIngresso.id }" onfocus="Field.check('busca:formaIngresso')">
					<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
					<f:selectItems value="#{ buscaAvancadaDiscenteMBean.formasIngresso }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="anoIngresso" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaAnoIngresso }"/></td>
			<td>Ano Ingresso:</td>
			<td><h:inputText id="inputAnoIngresso" value="#{ buscaAvancadaDiscenteMBean.parametros.anoIngresso }" size="4" maxlength="4" onfocus="Field.check('busca:anoIngresso')" onkeyup="return formatarInteiro(this);" /></td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="periodoIngresso" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPeriodoIngresso }"/></td>
			<td>Período Ingresso:</td>
			<td><h:inputText id="inputPeriodoIngresso" value="#{ buscaAvancadaDiscenteMBean.parametros.periodoIngresso }" size="1" maxlength="1" onfocus="Field.check('busca:periodoIngresso')" onkeyup="return formatarInteiro(this);"/></td>
		</tr>
		<tr>
			<td width="20"><h:selectBooleanCheckbox id="indicesAcademicos" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaIndiceAcademico }"/></td>
			<td width="180">Índices Acadêmicos:</td>
			<td>
				<h:inputText id="inputIndicesAcademicos" value="#{ buscaAvancadaDiscenteMBean.parametros.indicesAcademicos }" onfocus="Field.check('busca:indicesAcademicos')" size="50"/>
				<ufrn:help>
					Utilize os operadores >, <, >=, <= e = e as siglas dos índices acadêmicos para compará-los com valores. É possível ainda utilizar
					os operadores lógicos E e OU para comparar mais de um índice. Utilize parênteses para separar os termos da comparação. 
					Exemplo: (MC > 8 OU IRA >= 7.5) E IEA > 7  
				</ufrn:help>
			</td>
		</tr>
		<tr>
			<td width="20"><h:selectBooleanCheckbox id="tipoSaida" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaTipoSaida }"/></td>
			<td width="180">Tipo de Saída:</td>
			<td>
				<h:selectOneMenu id="selectTipoSaida" value="#{ buscaAvancadaDiscenteMBean.parametros.tipoSaida.id }" onfocus="Field.check('busca:tipoSaida')">
					<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
					<f:selectItems value="#{ buscaAvancadaDiscenteMBean.tiposSaida }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="anoSaida" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaAnoSaida }"/></td>
			<td>Ano Saída:</td>
			<td><h:inputText id="inputAnoSaida" value="#{ buscaAvancadaDiscenteMBean.parametros.anoSaida }" size="4" maxlength="4" onfocus="Field.check('busca:anoSaida')" onkeyup="return formatarInteiro(this);"/></td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="periodoSaida" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPeriodoSaida }"/></td>
			<td>Período Saída:</td>
			<td><h:inputText id="inputPeriodoSaida" value="#{ buscaAvancadaDiscenteMBean.parametros.periodoSaida }" size="1" maxlength="1" onfocus="Field.check('busca:periodoSaida')" onkeyup="return formatarInteiro(this);"/></td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="matriculadoEm" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaMatriculadosEm }"/></td>
			<td>Matriculados em:</td>
			<td><h:inputText id="inputMatriculadoEm" value="#{ buscaAvancadaDiscenteMBean.parametros.matriculadoEmAno }" size="4" maxlength="4" onfocus="Field.check('busca:matriculadoEm')" onkeyup="return formatarInteiro(this);" /> - <h:inputText id="matPeriodo" value="#{ buscaAvancadaDiscenteMBean.parametros.matriculadoEmPeriodo }" size="1" maxlength="1" onfocus="Field.check('busca:matriculadoEm')" onkeyup="return formatarInteiro(this);"/> </td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="naoMatriculadoEm" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaNaoMatriculadosEm }"/></td>
			<td>Não matriculados em:</td>
			<td><h:inputText id="inputNaoMatriculadoEm" value="#{ buscaAvancadaDiscenteMBean.parametros.naoMatriculadoEmAno }" size="4" maxlength="4" onfocus="Field.check('busca:naoMatriculadoEm')" onkeyup="return formatarInteiro(this);"/> - <h:inputText id="naoMatPeriodo" value="#{ buscaAvancadaDiscenteMBean.parametros.naoMatriculadoEmPeriodo }" size="1" maxlength="1" onfocus="Field.check('busca:naoMatriculadoEm')" onkeyup="return formatarInteiro(this);" /> </td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="trancadoPeriodo" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaTrancadosNoPeriodo }"/></td>
			<td>Trancados em:</td>
			<td><h:inputText id="inputTrancadoPeriodo" value="#{ buscaAvancadaDiscenteMBean.parametros.trancadoNoAno }" size="4" maxlength="4" onfocus="Field.check('busca:trancadoPeriodo')" onkeyup="return formatarInteiro(this);" /> - <h:inputText id="trancPeriodo" value="#{ buscaAvancadaDiscenteMBean.parametros.trancadoNoPeriodo }" size="1" maxlength="1" onfocus="Field.check('busca:trancadoPeriodo')" onkeyup="return formatarInteiro(this);" /></td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaEstado }" id="estado"/></td>
			<td>Estado:</td>
			<td>
				<h:selectOneMenu id="selectEstado" value="#{ buscaAvancadaDiscenteMBean.parametros.estado.id }" onfocus="Field.check('busca:estado')">
					<a4j:support event="onchange" reRender="cidades"/>
					<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
					<f:selectItems value="#{ buscaAvancadaDiscenteMBean.estados }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="cidade" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaCidade }"/></td>
			<td>Cidade:</td>
			<td>
				<h:selectOneMenu id="cidades" value="#{ buscaAvancadaDiscenteMBean.parametros.cidade.id }" onfocus="Field.check('busca:cidade')">
					<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
					<f:selectItems value="#{ buscaAvancadaDiscenteMBean.cidades }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="tipoNecessidadeEspecial" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaTipoNecessidadeEspecial }"/></td>
			<td>Necessidade Especial:</td>
			<td>
				<h:selectOneMenu id="tiposNecessidadeEspecial" value="#{ buscaAvancadaDiscenteMBean.parametros.tipoNecessidadeEspecial.id }" onfocus="Field.check('busca:tipoNecessidadeEspecial')">
					<f:selectItem itemLabel="Todas" itemValue="0"/>
					<f:selectItems value="#{ tipoNecessidadeEspecialMBean.allCombo }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="relatorio" value="#{ buscaAvancadaDiscenteMBean.relatorio }"/> </td>
			<td colspan="2"><b>Trazer informações em formato de relatório</b></td>
		</tr>
		<tr>
			<td><h:selectBooleanCheckbox id="csv" value="#{ buscaAvancadaDiscenteMBean.gerarCSV }"/> </td>
			<td colspan="2">
				<b>Gerar CSV</b>
				<ufrn:help>Esse formato pode ser aberto como uma planilha em programas como o Excel.</ufrn:help>
			</td>
		</tr>
		<tr><td colspan="3">
			<table class="subFormulario" id="tblGraduacao" width="100%">
				<caption>Graduação</caption>
				<tr>
					<td width="20"><h:selectBooleanCheckbox id="centro" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaCentro }"/> </td>
					<td width="180">Centro/Unidade Especializada:</td>
					<td>
						<h:selectOneMenu id="selectCentro" value="#{ buscaAvancadaDiscenteMBean.parametros.centro.id }" onfocus="Field.check('busca:centro')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ unidade.allCentrosEscolasCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="curso" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaCursoGraduacao }"/> </td>
					<td>Curso:</td>
					<td>
						<h:selectOneMenu id="selectCurso" value="#{ buscaAvancadaDiscenteMBean.parametros.cursoGraduacao.id }" style="width: 450px" onfocus="Field.check('busca:curso')">
							<a4j:support event="onchange" reRender="matrizCurricular"/>
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ curso.allCursoGraduacaoCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="matriz" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaMatriz }"/> </td>
					<td>Matriz Curricular:</td>
					<td>
						<h:selectOneMenu id="matrizCurricular" value="#{ buscaAvancadaDiscenteMBean.parametros.matrizCurricular.id }" onfocus="Field.check('busca:matriz')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ buscaAvancadaDiscenteMBean.matrizesCurso }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="curriculo" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaCurriculo }"/></td>
					<td>Currículo:</td>
					<td><h:inputText id="inputCurriculo" value="#{ buscaAvancadaDiscenteMBean.parametros.curriculo.codigo }" size="5" maxlength="5" onfocus="Field.check('busca:curriculo')"/></td>
				</tr>			
				<tr>
					<td><h:selectBooleanCheckbox id="noPeriodo" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaNoPeriodo }"/></td>
					<td>No período:</td>
					<td><h:inputText id="inputNoPeriodo" value="#{ buscaAvancadaDiscenteMBean.parametros.noPeriodo }" size="2" maxlength="2" onfocus="Field.check('busca:noPeriodo')" onkeyup="return formatarInteiro(this);" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="turno" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaTurno }"/> </td>
					<td>Turno:</td>
					<td>
						<h:selectOneMenu id="selectTurno" value="#{ buscaAvancadaDiscenteMBean.parametros.turno.id }" onfocus="Field.check('busca:turno')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ turno.allAtivosCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="modalidade" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaModalidade }"/> </td>
					<td>Modalidade:</td>
					<td>
						<h:selectOneMenu id="selectModalidade" value="#{ buscaAvancadaDiscenteMBean.parametros.modalidade.id }" onfocus="Field.check('busca:modalidade')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ modalidadeEducacao.allCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="prazoMaximo" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPrazoMaximo }"/> </td>
					<td>Prazo Máximo de Conclusão:</td>
					<td> <h:inputText id="inputPrazoMaximo" value="#{ buscaAvancadaDiscenteMBean.parametros.prazoMaximoAno }" size="4" maxlength="4" onfocus="Field.check('busca:prazoMaximo')" onkeyup="return formatarInteiro(this);" /> - <h:inputText value="#{ buscaAvancadaDiscenteMBean.parametros.prazoMaximoPeriodo }" size="1" maxlength="1" onfocus="Field.check('busca:prazoMaximo')" onkeyup="return formatarInteiro(this);" /> </td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaConvenio }" id="convenio"/> </td>
					<td>Convênio:</td>
					<td>
						<h:selectOneMenu id="selectConvenio" value="#{ buscaAvancadaDiscenteMBean.parametros.convenio.id }" onfocus="Field.check('busca:convenio')">
							<f:selectItem itemLabel="NENHUM" itemValue="0"/>
							<f:selectItems value="#{ buscaAvancadaDiscenteMBean.convenios }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPolo }" id="polo"/> </td>
					<td>Pólo:</td>
					<td>
						<h:selectOneMenu id="selectPolo" value="#{ buscaAvancadaDiscenteMBean.parametros.polo.id }" onfocus="Field.check('busca:polo')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ poloBean.allCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaParticipacaoEnade }" id="enad"/> </td>
					<td>Participação ENADE:</td>
					<td>
						<h:selectOneMenu id="selectENAD" value="#{ buscaAvancadaDiscenteMBean.parametros.participacaoEnade.id }" onfocus="Field.check('busca:enad')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ participacaoDiscenteEnade.allTiposParticipacaoCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox 
						value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaOpcaoAprovacao }" id="opcaoAprovacao"/> </td>
					<td>Aprovado na Opção (Vestibular):</td>
					<td>
						<h:inputText id="inputOpcaoAprovacao" value="#{ buscaAvancadaDiscenteMBean.parametros.opcaoAprovacao }" 
						size="2" maxlength="1" onfocus="Field.check('busca:opcaoAprovacao')" 
						onkeyup="return formatarInteiro(this);"/>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaDesconsiderarApostilamentos }" id="apostilamentos"/></td>
					<td colspan="2">Desconsiderar Apostilamentos</td>
				</tr>
			</table>
		</td></tr>
		<tr><td colspan="3">
			<table class="subFormulario" id="tblStricto" width="100%">
				<caption>Pós-Graduação Stricto Sensu:</caption>
				<tr>
					<td width="20"><h:selectBooleanCheckbox id="programa" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPrograma }"/> </td>
					<td width="180">Programa:</td>
					<td>
						<h:selectOneMenu id="selectPrograma" value="#{ buscaAvancadaDiscenteMBean.parametros.programa.id }" style="width: 450px" onfocus="Field.check('busca:programa')">
							<a4j:support event="onchange" reRender="selectCurriculoStricto" />
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ unidade.allProgramaPosCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<td><h:selectBooleanCheckbox id="curriculoStricto" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaCurriculoStricto }"/></td>
					<td>Currículo:</td>
					<td>
						<h:selectOneMenu id="selectCurriculoStricto" value="#{ buscaAvancadaDiscenteMBean.parametros.curriculo.id }" style="width: 450px" onfocus="Field.check('busca:curriculoStricto')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ buscaAvancadaDiscenteMBean.curriculosStricto }"/>
						</h:selectOneMenu>
					</td>					
				</tr>							
				
				<tr>
					<td><h:selectBooleanCheckbox id="previsaoQualificacao" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPrevisaoQualificacao }"/> </td>
					<td colspan="2">Com previsão de qualificação (alunos que concluíram as disciplinas e devem fazer a qualificação)</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="previsaoDefesa" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPrevisaoDefesa }"/> </td>
					<td colspan="2">Com previsão de defesa (alunos concluintes, que cumpriram todas as obrigações e devem defender a dissertação/tese)</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="prazoEsgotado" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPrazoEsgotado }"/> </td>
					<td colspan="2">Com prazo esgotado</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="prazoEsgotadoEm" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPrazoASerEsgotado }"/> </td>
					<td>Com prazo a ser esgotado em:</td>
					<td>
						<t:inputCalendar value="#{buscaAvancadaDiscenteMBean.parametros.prazoEsgotadoAte}" size="10" 
							maxlength="10" onkeypress="return formataData(this,event)" popupDateFormat="dd/MM/yyyy" 
							onchange="Field.check('busca:prazoEsgotadoEm')" id="calendarPrazoEsgotado" 
							renderAsPopup="true" renderPopupButtonAsImage="true" >
						</t:inputCalendar>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="prazoProrrogado" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaPrazoProrrogado }"/> </td>
					<td colspan="2">Com prazo prorrogado</td>
				</tr>
				<%-- <tr>
					<td><h:selectBooleanCheckbox value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaDiscentesDesligados }"/> </td>
					<td>Discentes desligados</td>
					<td></td>
				</tr> --%>
				<tr>
					<td><h:selectBooleanCheckbox id="discentesDiplomasHomologados" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaDiscentesHomologados }"/> </td>
					<td colspan="2">Discentes com diplomas homologados pela PPg e ainda não formados</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="discentesNaoHomologados" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaDiscentesDefenderamNaoHomologados }"/> </td>
					<td colspan="2">Discentes que defenderam mas não foram homologados</td>
				</tr>
			</table>
		</td></tr>
		<tr><td colspan="3">
			<table class="subFormulario" id="tblLato" width="100%">
				<caption>Pós-Graduação Lato Sensu</caption>
				<tr>
					<td width="20"><h:selectBooleanCheckbox id="cursoLato" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaCursoLato }"/> </td>
					<td width="180">Curso:</td>
					<td>
						<h:selectOneMenu id="selectCursoLato" value="#{ buscaAvancadaDiscenteMBean.parametros.cursoLato.id }" style="width: 450px" onfocus="Field.check('busca:cursoLato')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ curso.allCursoEspecializacaoCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</table>
		</td></tr>
		<tr><td colspan="3">
			<table class="subFormulario" id="tblTecnico" width="100%">
				<caption>Técnico</caption>
				<tr>
					<td>
						<c:choose>
							<c:when test="${ fn:length(buscaAvancadaDiscenteMBean.allEscolaCombo) > 1 }">
								<h:selectBooleanCheckbox id="escola" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaEscola }"/> </td>
							</c:when>
							<c:otherwise>
							<c:if test="${ fn:length(buscaAvancadaDiscenteMBean.allEscolaCombo) <= 1 }">
								<h:inputHidden id="hiddenRestricoesEscola" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaEscola }"/>
							</c:if>
							</c:otherwise>
						</c:choose>
					</td>
					<td width="180">Escola:</td>
					<td>
						<c:choose>
						<c:when test="${ fn:length(buscaAvancadaDiscenteMBean.allEscolaCombo) > 1 }">
							<h:selectOneMenu id="selectEscola" value="#{ buscaAvancadaDiscenteMBean.parametros.escola.id }" style="width: 450px" onfocus="Field.check('busca:escola')">
								<a4j:support event="onchange" reRender="turmaEntrada,especialidade"/>
								<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
								<f:selectItems value="#{ buscaAvancadaDiscenteMBean.allEscolaCombo }"/>
							</h:selectOneMenu>
						</c:when>
						<c:otherwise>
							<h:inputHidden id="escolas" value="#{ buscaAvancadaDiscenteMBean.parametros.escola.id }"/>
							${ buscaAvancadaDiscenteMBean.parametros.escola.nome }
						</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="cursoTecnico" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaCursoTecnico }"/> </td>
					<td>Curso:</td>
					<td>
						<h:selectOneMenu id="selectCursoTecnico" value="#{ buscaAvancadaDiscenteMBean.parametros.cursoTecnico.id }" style="width: 450px" onfocus="Field.check('busca:cursoTecnico')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ curso.allCursoTecnicoCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="turma" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaTurmaEntrada }"/> </td>
					<td>Turma de Entrada:</td>
					<td>
						<h:selectOneMenu id="turmaEntrada" value="#{ buscaAvancadaDiscenteMBean.parametros.turmaEntrada.id }" onfocus="Field.check('busca:turma')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ buscaAvancadaDiscenteMBean.turmasEntrada }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="especialidadeChk" value="#{ buscaAvancadaDiscenteMBean.restricoes.buscaEspecialidade }"/> </td>
					<td>Especialidade:</td>
					<td>
						<h:selectOneMenu id="especialidade" value="#{ buscaAvancadaDiscenteMBean.parametros.especialidade.id }" onfocus="Field.check('busca:especialidadeChk')">
							<f:selectItem itemLabel="Selecione uma opção" itemValue="0"/>
							<f:selectItems value="#{ buscaAvancadaDiscenteMBean.especialidades }"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</table>
		</td></tr>
	</tbody>
	<tfoot>
		<tr><td colspan="3">
			<h:commandButton value="Buscar" action="#{ buscaAvancadaDiscenteMBean.buscar }" id="busca"/>
			<h:commandButton value="Cancelar" action="#{ buscaAvancadaDiscenteMBean.cancelar }" id="cancelar" onclick="#{confirm}"/>
		</td></tr>
	</tfoot>
</table>

<br/>
<c:if test="${ not empty buscaAvancadaDiscenteMBean.discentesEncontrados }">

<div class="infoAltRem">
	<img src="${ctx}/img/view.gif"/>: Ver Histórico
</div>

<table class="listagem">
	<caption>Discentes Encontrados: ${ fn:length(buscaAvancadaDiscenteMBean.discentesEncontrados) }</caption>
	<thead>
		<tr>
			<th style="text-align: center;">Matrícula</th>
			<th>Nome</th>
			<c:if test="${buscaAvancadaDiscenteMBean.nomeCurso}">
				<th>Curso</th>
			</c:if>
			<th>Status</th>
			<th></th>
		</tr>
	</thead>
	<c:forEach items="#{ buscaAvancadaDiscenteMBean.discentesEncontrados }" var="discente" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" style="font-size: x-small">
		
			<td style="text-align: center;">${ discente.matricula }</td>
			<td>${ discente.nome }</td>
			<c:if test="${buscaAvancadaDiscenteMBean.nomeCurso}">
				<td>${ discente.curso.nomeCursoStricto }</td>
			</c:if>
			<td>${ discente.statusString }</td>
			<td>
				<h:commandLink action="#{ buscaAvancadaDiscenteMBean.detalhesDiscente }">
					<h:graphicImage value="/img/view.gif" alt="Ver Histórico" title="Ver Histórico"/>
					<f:param name="id" value="#{ discente.id }"/>
				</h:commandLink>
			</td>
		
		</tr>
	</c:forEach>
</table>
</c:if>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
<c:if test="${ fn:length(buscaAvancadaDiscenteMBean.niveisCombo) > 1 }">
	getEl('busca:nivel').on('click', function() {
		var elem = getEl('busca:nivel');
		if (!elem.dom.checked) {
			getEl('busca:niveis').dom.selectedIndex = 0;
			selecionaNivel(getEl('busca:niveis').dom);
		}
	});
</c:if>

function selecionaNivel(elem) {

	if (elem == undefined)
		elem = $('busca:niveis').value;
	
	var escolhido = elem;
	if (escolhido == 'G') {
		$('tblGraduacao').show();
		$('tblStricto').hide();
		$('tblLato').hide();
		$('tblTecnico').hide();
	} else if (escolhido == 'E' || escolhido == 'D' || escolhido == 'S') {
		$('tblGraduacao').hide();
		$('tblStricto').show();
		$('tblLato').hide();
		$('tblTecnico').hide();	
	} else if (escolhido == 'T') {
		$('tblGraduacao').hide();
		$('tblStricto').hide();
		$('tblLato').hide();
		$('tblTecnico').show();	
	} else if (escolhido == 'L') {	
		$('tblGraduacao').hide();
		$('tblStricto').hide();
		$('tblLato').show();
		$('tblTecnico').hide();
	} else {
		$('tblGraduacao').hide();
		$('tblStricto').hide();
		$('tblLato').hide();
		$('tblTecnico').hide();
	}
	
}
<c:if test="${ fn:length(buscaAvancadaDiscenteMBean.niveisCombo) > 1 }">
	selecionaNivel();
</c:if>
<c:if test="${ fn:length(buscaAvancadaDiscenteMBean.niveisCombo) <= 1 }">
	selecionaNivel('${ buscaAvancadaDiscenteMBean.parametros.nivel }');
</c:if>
</script>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
function adicionarReserva(event) {
	var obj =  event.target ? event.target : event.srcElement;
	var tr = getEl(obj.parentNode.parentNode.parentNode);
	var hidden = tr.getChildrenByClassName('idReserva')[0];
	var text = tr.getChildrenByClassName('vagasReserva')[0];
	
	var valorText = 0
	if( text != null )
		valorText = text.dom.value;
	
	getEl('idReservaCurso').dom.value = hidden.dom.value;
	getEl('qtdVagas').dom.value = valorText;
}

function dataTableSelectOneRadio(radio) {
    var id = radio.name.substring(radio.name.lastIndexOf(':'));
    var el = radio.form.elements;
    for (var i = 0; i < el.length; i++) {
        if (el[i].name.substring(el[i].name.lastIndexOf(':')) == id) {
            el[i].checked = false;
        }
    }
    radio.checked = true;
}


function validaInputVagas(){
	var inputs = document.getElementsByClassName('inputVagas')
	for (var i = 0; i < inputs.length; i++) {
		if( inputs[i].value == "" ){
			alert("Informe a quantidade de vagas que devem ser reservadas.");
			return false;
		}
	}
}
</script>

<style>
	table.subFormulario tr.turmaAgrupadora td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	.left { text-align: left !important; }
	.right { text-align: right !important; }
	.center { text-align: center !important; }
	.inputVagas { text-align: right !important; }
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>


<f:view>
	<c:if test="${acesso.chefeDepartamento and not (turmaGraduacaoBean.portalCoordenadorGraduacao or turmaGraduacaoBean.portalCoordenadorStricto )}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema /> &gt; Cadastro de Turmas &gt; Dados Gerais </h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Nesta tela você deve entrar com os dados gerais da turma.
			As datas de início e fim das turmas não podem ser alteradas e são preenchidas automaticamente com as datas de início e fim do período letivo,
			caso as datas não sejam preenchidas entre em contato com o ${ configSistema['siglaUnidadeGestoraGraduacao'] }. 	
		</p>
		<c:if test="${turmaGraduacaoBean.obj.disciplina.aceitaSubturma }">
			<p><b>Atenção! Este é um componente que permite a criação de subturmas. Porém, para que isso aconteça, é
			 necessário que as turmas criadas tenham algum horário em comum. </b></p>
		</c:if>
	</div>

	<h:form id="cadastroTurma">
	
	<br/>
	<table class="formulario" style="width: 100%">
		<caption class="formulario">Dados da Turma </caption>
		<tr>
			<td colspan="2" class="subFormulario">Dados do Componente Curricular</td>
		</tr>
		<tr>
			<th width="30%"><b>Componente Curricular:</b></th>
			<td>
				<h:outputText value="#{turmaGraduacaoBean.obj.disciplina.codigo}" /> - <h:outputText value="#{turmaGraduacaoBean.obj.disciplina.detalhes.nome}" />
			</td>
		</tr>
		<tr>
			<th><b>Tipo do Componente:</b></th>
			<td><h:outputText value="#{turmaGraduacaoBean.obj.disciplina.tipoComponente.descricao}" /></td>
		</tr>
		<c:if test="${turmaGraduacaoBean.obj.disciplina.atividade}">
			<tr>
				<th><b>Tipo de Atividade:</b></th>
				<td><h:outputText value="#{turmaGraduacaoBean.obj.disciplina.tipoAtividade.descricao}" /></td>
			</tr>
		</c:if>
		<c:if test="${turmaGraduacaoBean.obj.disciplina.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva}">
			<tr>
				<th><b>Total de Créditos:</b></th>
				<td><h:outputText value="#{turmaGraduacaoBean.obj.disciplina.detalhes.crTotal}" /> crs. (${turmaGraduacaoBean.obj.disciplina.detalhes.chTotal} h)</td>
			</tr>
		</c:if>
		<c:if test="${not (componenteCurricular.obj.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva)}">
			<tr>
				<th><b>CH Total:</b></th>
				<td><h:outputText value="#{turmaGraduacaoBean.obj.disciplina.detalhes.chTotal}" /> h</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" class="subFormulario">Dados Gerais da Turma</td>
		</tr>
		<c:if test="${ turmaGraduacaoBean.selecionarCurso}">
			<tr>
				<th class="required">Curso</th>
				<td>
					<h:selectOneMenu id="curso" onchange="submit()" valueChangeListener="#{turmaGraduacaoBean.selecionarCurso}"
						 value="#{turmaGraduacaoBean.obj.curso.id}" disabled="#{!turmaGraduacaoBean.passivelEdicao}">
						<f:selectItem itemValue="0" itemLabel="-- NENHUM --" />
						<f:selectItems value="#{cursoGrad.allProbasicaCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</c:if>
		<c:if test="${not empty turmaGraduacaoBean.solicitacao || turmaGraduacaoBean.obj.id != 0}">
			<tr>
				<th><b>Tipo da Turma:</b></th>
				<td>
					<h:outputText value="#{ turmaGraduacaoBean.obj.tipoString }"  />
				</td>
			</tr>
		</c:if>
		<c:if test="${empty turmaGraduacaoBean.solicitacao && turmaGraduacaoBean.obj.id == 0}">
			<tr>
				<th class="required">Tipo da Turma:</th>
				<td>
					<a4j:region>
					<h:selectOneMenu id="tipoTurma" value="#{turmaGraduacaoBean.obj.tipo}" valueChangeListener="#{turmaGraduacaoBean.atualizarAnoPeriodo}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{turmaGraduacaoBean.tiposTurmaCombo}"/>
						<a4j:support event="onchange" onsubmit="true" reRender="panelPeriodo" />
					</h:selectOneMenu>
					</a4j:region>
				</td>
			</tr>
		</c:if>
		<tr>
			<th><b>Modalidade:</b></th>
			<td>
				<c:choose>
					<c:when test="${ !turmaGraduacaoBean.turmaEad }">Presencial</c:when>
					<c:otherwise>A Distância</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<th class="${ empty turmaGraduacaoBean.solicitacao && turmaGraduacaoBean.obj.id == 0 ? 'required' : 'rotulo' }" >Ano-Período:</th>
			<td>
				<c:choose>
					<c:when test="${ empty turmaGraduacaoBean.solicitacao && turmaGraduacaoBean.obj.id == 0}">
						<a4j:region>
							<h:panelGroup id="panelPeriodo"> 
							<h:inputText value="#{turmaGraduacaoBean.obj.ano}" maxlength="4" size="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }">
								<a4j:support event="onblur" reRender="dataInicioTurma, dataFimTurma" actionListener="#{turmaGraduacaoBean.atualizarDataInicioFim}" />
							</h:inputText>
							-
							<h:inputText value="#{turmaGraduacaoBean.obj.periodo}" maxlength="1" size="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }">
								<a4j:support event="onblur" reRender="dataInicioTurma, dataFimTurma" actionListener="#{turmaGraduacaoBean.atualizarDataInicioFim}" />
							</h:inputText>
							</h:panelGroup>
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
						</a4j:region>
					</c:when>
					<c:otherwise>
						<h:outputText value="#{turmaGraduacaoBean.obj.ano}" />-<h:outputText value="#{turmaGraduacaoBean.obj.periodo}" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:if test="${(turmaGraduacaoBean.obj.campusObrigatorio || acesso.dae) && !turmaGraduacaoBean.turmaEad}">
			<tr>
			<th class="${ (turmaGraduacaoBean.obj.campusObrigatorio && turmaGraduacaoBean.obj.aberta) ? 'required' : '' }">Campus:</th>
			<td>
				<h:selectOneMenu id="campus" value="#{turmaGraduacaoBean.obj.campus.id}" disabled="#{!turmaGraduacaoBean.obj.aberta}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
					<f:selectItems value="#{campusIes.allCampusCombo}"/>
				</h:selectOneMenu>
			</td>
			</tr>
		</c:if>
		<c:if test="${ turmaGraduacaoBean.turmaEad }">
			<c:if test="${ turmaGraduacaoBean.obj.id == 0 }" >
				<tr>
					<th>
						<a4j:region>
						<h:selectBooleanCheckbox value="#{turmaGraduacaoBean.turmaUnicaCursoEad}" id="chkTurmaUnicaEad">
							<a4j:support event="onclick" reRender="labelCursoPolo,panelCursoPolo" />
						</h:selectBooleanCheckbox>
						</a4j:region>
					 </th>
					<td>
						Criar uma única turma para o curso
						<ufrn:help>Desmarque esta opção caso deseje especificar para quais Pólos serão criadas as turmas.</ufrn:help>
					</td>
				</tr>
			</c:if>
			<tr>
				<th valign="top" class="required">
					<h:panelGroup id="labelCursoPolo">
						<h:outputText value="Curso:" rendered="#{ turmaGraduacaoBean.turmaUnicaCursoEad }" />
						<h:outputText value="Pólo:" rendered="#{not turmaGraduacaoBean.turmaUnicaCursoEad }" />
					</h:panelGroup> 
				</th>
				<td>
					<h:panelGroup id="panelCursoPolo">
						<h:selectOneMenu value="#{ turmaGraduacaoBean.obj.curso.id }" id="selectCurso"
							rendered="#{ turmaGraduacaoBean.turmaUnicaCursoEad }">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{ curso.allCursosGraduacaoEADCombo }"/>
						</h:selectOneMenu>
						<h:selectManyCheckbox value="#{ turmaGraduacaoBean.polosEscolhidos }" style="height: 80px; width: 200px"
							rendered="#{ !turmaGraduacaoBean.turmaUnicaCursoEad && turmaGraduacaoBean.obj.id == 0 }" layout="pageDirection" id="checkManyPolos">
							<f:selectItems value="#{ turmaGraduacaoBean.polos }"/>
						</h:selectManyCheckbox>
						<h:selectOneMenu value="#{ turmaGraduacaoBean.obj.polo.id }" 
							rendered="#{ !turmaGraduacaoBean.turmaUnicaCursoEad && turmaGraduacaoBean.obj.id != 0 }" id="selectPolos">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{ turmaGraduacaoBean.polos }"/>
						</h:selectOneMenu>
					</h:panelGroup>
				</td>
			</tr>
		</c:if>
		<c:if test="${ turmaGraduacaoBean.editarCodigoTurma}">
			<tr>
				<th class="required">Código da Turma: </th>
				<td>
					<h:inputText id="codigoTurma2" value="#{ turmaGraduacaoBean.obj.codigo }" size="4" 
						disabled="#{(!turmaGraduacaoBean.passivelEdicao || turmaGraduacaoBean.matriculada) && !turmaGraduacaoBean.turmaEad}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${!turmaGraduacaoBean.editarCodigoTurma && not empty turmaGraduacaoBean.obj.codigo}">
			<tr>
				<th class="rotulo">Código da Turma: </th>
				<td>
					<h:outputText value="#{ turmaGraduacaoBean.obj.codigo }"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${ !turmaGraduacaoBean.turmaEad }">
			<tr>
				<th class="required">Capacidade de Alunos:</th>
				<td>
					<c:if test="${!(turmaGraduacaoBean.solicitacao.turmaEnsinoIndividual && !turmaGraduacaoBean.solicitacao.geraTurmaRegular)}">
						<h:inputText id="vagas" value="#{ turmaGraduacaoBean.obj.capacidadeAluno }"  size="4" maxlength="3" 
						disabled="#{!turmaGraduacaoBean.passivelEdicao}"  
						converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
						<c:if test="${ turmaGraduacaoBean.matriculada }">
							<span style="font-size: 0.9em; color: #888; padding: 2px 5px;">
								(existem ${ turmaGraduacaoBean.obj.qtdMatriculados } alunos associados a esta turma)
							</span>
						</c:if>
					</c:if>

					<c:if test="${turmaGraduacaoBean.solicitacao.turmaEnsinoIndividual && !turmaGraduacaoBean.solicitacao.geraTurmaRegular}">
						<h:outputText value="#{turmaGraduacaoBean.obj.capacidadeAluno}"/>
					</c:if>
				</td>
			</tr>
			<c:if test="${turmaGraduacaoBean.defineQuantidadeSubturmas}">
				<tr>
					<th class="required">Quantidade de Subturmas a Criar:</th>
					<td>
						<h:inputText id="qtdSubTurma" value="#{ turmaGraduacaoBean.quantidadeSubturmas }" maxlength="2" size="3" 
						converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
						<ufrn:help>
							É possível criar várias subturmas no atendimento de uma única solicitação. 
							Caso deseje criar mais de uma subTurma, informe a quantidade de subturmas a ser criada.
						</ufrn:help>
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="required">Local:</th>
				<td>
					<h:inputText id="local" value="#{ turmaGraduacaoBean.obj.local }" maxlength="50" size="50" disabled="#{!turmaGraduacaoBean.obj.aberta}"/>
				</td>
			</tr>
			<c:if test="${turmaGraduacaoBean.turmaStricto}">
				<tr>
					<th>Observações ao Aluno: 
					<ufrn:help img="/img/ajuda.gif" width="320">Utilize este campo para colocar observações para o aluno realizar a matrícula.
					Por exemplo: nas disciplinas de tópicos especiais pode ser adicionado a informação de qual será o assunto abordado, estas observações aparecerão no histórico ao lado do nome da disciplina.</ufrn:help>
					<br/>
					<span style="font-size: xx-small"></span>
					</th>
					<td>
						<h:inputTextarea id="observacoes" value="#{ turmaGraduacaoBean.obj.observacao }" rows="2" cols="50" disabled="#{!turmaGraduacaoBean.obj.aberta && turmaGraduacaoBean.portalCoordenadorStricto}"/>
					</td>
				</tr>
			</c:if> 
			<c:if test="${not empty turmaGraduacaoBean.solicitacao.motivo && turmaGraduacaoBean.obj.turmaFerias}">
				<tr>
					<th>Motivo da Solicitação:</th>
					<td>
						<h:outputText id="motivo" value="#{ turmaGraduacaoBean.solicitacao.motivo }"/>
					</td>
				</tr>
			</c:if>
		</c:if>
		<c:if test="${not ( turmaGraduacaoBean.obj.graduacao ) }">
			<tr>
				<th class="required">Início:</th>
				<td>
					<t:inputCalendar value="#{turmaGraduacaoBean.obj.dataInicio}" id="dataInicioTurma"
						disabled="#{ !((turmaGraduacaoBean.passivelEdicao && turmaGraduacaoBean.obj.disciplina.modulo) || turmaGraduacaoBean.turmaEad || acesso.dae || acesso.ppg
						                || (acesso.chefeDepartamento && turmaGraduacaoBean.obj.turmaFerias)) }"
			            size="10" maxlength="10" 
					    onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
					    renderAsPopup="true" renderPopupButtonAsImage="true" >
				    	<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="required">Fim:<br/>
				</th>
				<td>
					<t:inputCalendar value="#{turmaGraduacaoBean.obj.dataFim}" id="dataFimTurma"
						disabled="#{ !((turmaGraduacaoBean.passivelEdicao && turmaGraduacaoBean.obj.disciplina.modulo) || turmaGraduacaoBean.turmaEad || acesso.dae || acesso.ppg
					                || (acesso.chefeDepartamento && turmaGraduacaoBean.obj.turmaFerias)) }"size="10" maxlength="10" 
					    onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
					    renderAsPopup="true" renderPopupButtonAsImage="true" >
				    	<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
	</c:if>	
	<c:if test="${not ( turmaGraduacaoBean.solicitacao.turmaEnsinoIndividual ) }">
	<c:if test="${ not turmaGraduacaoBean.turmaEad }">
		<c:if test="${not empty turmaGraduacaoBean.outrasSolicitacoes}">
		<tr>
			<td colspan="2">
			<div class="infoAltRem">
				<h:graphicImage value="/img/cronograma/adicionar.gif" style="overflow: visible;" />: Adicionar Reserva
			</div>
			<table class="subFormulario" width="100%">
			<caption>Outras Solicitações do Mesmo Componente/Horário (${fn:length(turmaGraduacaoBean.outrasSolicitacoes)})</caption>
				<tr>
					<td colspan="2">

						<table class="listagem"  width="99%">
							<thead>
								<tr>
									<td>Curso</td>
									<td>Turno</td>
									<td>Grau Acadêmico</td>
									<td>Habilitação/Ênfase</td>
									<td>Vagas Solicitadas</td>
									<td width="2%" colspan="2"></td>
								</tr>
							</thead>

							<tbody>

							<input type="hidden" name="idReservaCurso" id="idReservaCurso" />
							<input type="hidden" name="qtdVagas" id="qtdVagas" />

							<c:forEach var="solicitacao" items="#{turmaGraduacaoBean.outrasSolicitacoes}" varStatus="status">

								<c:forEach items="#{solicitacao.reservas}" var="reserva" varStatus="statusReserva">

									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
										<td>${reserva.matrizCurricular.curso.descricao}</td>
										<td>${reserva.matrizCurricular.turno.descricao}</td>
										<td>${reserva.matrizCurricular.grauAcademico.descricao}</td>
										<td>
											${reserva.matrizCurricular.habilitacao.nome}
											${reserva.matrizCurricular.enfase.nome}
										</td>
										<td>${reserva.vagasSolicitadas}</td>
										<td>
											<%--  <h:inputText value="#{reserva.vagasAtendidas}" size="4" styleClass="vagasReservadas"/> --%>
											<c:if test="${reserva.id > 0}">
												<input type="text" size="4" class="vagasReserva" value="${reserva.vagasReservadas}">
											</c:if>
										</td>
										<td valign="middle" >
											<c:if test="${empty reserva.turma}">
												<input type="hidden" name="id" value="${reserva.id}" class="idReserva"/>
												<h:commandLink action="#{turmaGraduacaoBean.adicionarReserva}" onclick="adicionarReserva(event)" id="lnkAdicinoarReserva">
													<f:param name="idSolicitacao" value="#{reserva.idSolicitacao}"/> 
													<h:graphicImage url="/img/cronograma/adicionar.gif" title="Adicionar Reserva" />
												</h:commandLink>
											</c:if>
										</td>
									</tr>

								</c:forEach>
							</c:forEach>

							</tbody>
						</table>

					</td>
				</tr>
			</table>
			</td>
		</tr>
		</c:if>

		<c:if test="${ not empty turmaGraduacaoBean.obj.reservas }">
			<tr>
				<td colspan="2" class="subFormulario">Reservas de Vagas</td>
			</tr>
			<tr>
				<td colspan="2">
					<c:set var="podeRemover" value="false" />
					<c:forEach items="#{ turmaGraduacaoBean.obj.reservas }" var="reserva">
						<c:if test="${reserva.podeRemover && reserva.matrizCurricular != null}">
							<c:set var="podeRemover" value="true" />
						</c:if>		
					</c:forEach>
					<c:if test="${podeRemover}">
						<div class="infoAltRem">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Reservas
						</div>
					</c:if>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<thead>
						<tr>
							<th style="text-align: left;"><h:outputText value="Reservas Contempladas" /></th>
							<th colspan="3">
								<table width="100%">
									<tr>
										<th style="text-align: center;" colspan="3"><h:outputText value="Vagas"/></th>
									</tr>
									<tr>	
										<th style="text-align: right; width: 10%"><h:outputText value="Solicitadas"/></th>
										<th style="text-align: right; width: 10%"><h:outputText value="Reservadas"/></th>
										<th style="text-align: right; width: 10%"><h:outputText value="Ingressantes"/></th>
									</tr>
								</table>
							</th>
							<th style="text-align: center; width: 2%"></th>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="#{ turmaGraduacaoBean.obj.reservas }" var="reserva" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align: left;"><h:outputText value="#{ reserva.descricao }" id="desc"/></td>
						<td align="right" width="10%"><h:outputText value="#{reserva.vagasSolicitadas}" id="vagasSol"/></td>
						<td align="right" style="text-align: right;" width="10%">
							<c:choose>
								<c:when test="${turmaGraduacaoBean.adicionarOutrasReservas || (not empty turmaGraduacaoBean.solicitacao && turmaGraduacaoBean.obj.id == 0) || turmaGraduacaoBean.passivelEdicao}">
								  	<h:inputText value="#{reserva.vagasReservadas}" size="4" maxlength="4" id="vagasReservadas" onkeyup="return formatarInteiro(this);" title="Vagas Reservadas" styleClass="inputVagas"/>
								</c:when>
								<c:otherwise>
   							    	<h:outputText value="#{reserva.vagasReservadas}" id="vagasReservadasOut"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td align="right" width="10%"><h:outputText value="#{reserva.vagasReservadasIngressantes}" id="vagasSolIngressantes"/></td>
						<td style="text-align: center; width: 2%">
							<c:if test="${(not empty turmaGraduacaoBean.solicitacao && turmaGraduacaoBean.obj.id == 0) || turmaGraduacaoBean.adicionarOutrasReservas}">
								<h:commandLink actionListener="#{ turmaGraduacaoBean.removerReservaCurso }" id="linkRemover" rendered="#{reserva.podeRemover}">
									<f:param value="#{ reserva.id }" name="idMatriz" id="paramReserva"/>
									<f:param value="#{ reserva.idSolicitacao }" name="idSolicitacao" id="paramIdSolicitacao"/>
									<h:graphicImage url="/img/delete.gif" title="Remover reservas" id="imgDelete" />
								</h:commandLink>
							</c:if>
						</td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				</td>
			</tr>
		</c:if>

		<c:if test="${ empty turmaGraduacaoBean.obj.reservas }">
			<tr>
				<td colspan="2" style="text-align: center;">
					<font color="red"><i><strong>Não há reservas para esta turma.</strong></i></font>
				</td>
			</tr>
		</c:if>

	</c:if> <%-- <c:if test="${ not turmaGraduacaoBean.turmaEad }"> --%>
	</c:if> <%-- <c:if test="${not ( turmaGraduacaoBean.solicitacao.turmaEnsinoIndividual ) }"> --%>
	
			<tr>
				<td colspan="2" >
					<a4j:outputPanel id="painelSubTurmas">
					<c:if test="${ not empty turmaGraduacaoBean.obj.disciplina.aceitaSubturma && not empty turmaGraduacaoBean.turmasAgrupadoras}">
						<table width="100%" class="visualizacao">
							<caption>Selecione a Turma Agrupadora desta Turma</caption>
							<thead>
							<tr>
								<th width="5%">&nbsp;</th>
								<th width="5%"  style="text-align: right;padding-left: 20px">Código</th>
								<th width="20%" style="text-align: right;padding-left: 20px">Horário</th>
								<th width="5%"  style="text-align: right;padding-left: 20px">Capacidade</th>
								<th width="50%" style="padding-left: 20px">Docentes</th>
							</tr>
							</thead>
							<tbody>
							<c:set var="agrupadoraAnterior" value="0"/>
							<c:set var="novoCodigo" value="1"/>
							<c:forEach var="agrupadora" items="${turmaGraduacaoBean.turmasAgrupadoras}" varStatus="status">
								<c:if test="${agrupadoraAnterior != agrupadora.id}">
									<tr>
									<td colspan="5" class="subFormulario">
										<input type="radio" name="turmaAgrupadoraSelecionada" 
										value="${agrupadora.id}" 
										${(turmaGraduacaoBean.obj.turmaAgrupadora.id == agrupadora.id) ? 'checked="checked"' : ''}>
										Turma Agrupadora: ${agrupadora.codigo}
									</td>
									</tr>
									<c:set var="agrupadoraAnterior" value="${agrupadora.id}"/>
									<c:set var="novoCodigo" value="${novoCodigo + 1}"/>
								</c:if>
								<c:forEach var="subturma" items="${agrupadora.subturmas}">
									<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
										<th width="5%">&nbsp;</th>
										<td style="text-align: right;padding-left: 20px">${subturma.codigo}</td>
										<td style="text-align: right;padding-left: 20px">${subturma.descricaoHorario}</td>
										<td style="text-align: right;padding-left: 20px">${subturma.capacidadeAluno}</td>
										<td style="padding-left: 20px">${subturma.docentesNomesCh}</td>
									</tr>	
								</c:forEach>
							</c:forEach>
								<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									<td colspan="5" class="subFormulario">
										<input type="radio" name="turmaAgrupadoraSelecionada" value="0" ${(turmaGraduacaoBean.obj.turmaAgrupadora.id == agrupadora.id) ? 'checked="checked"' : ''}>
										Nova Turma Agrupadora
										<ufrn:help>Será atribuído um novo código para a turma, quando o cadastro for finalizado, como, por exemplo, ${novoCodigo}A.</ufrn:help>
									</td>
								</tr>
							</tbody>
						</table>
					</c:if>
					</a4j:outputPanel>
				</td>
			</tr>
		<c:if test="${turmaGraduacaoBean.solicitacao.turmaEnsinoIndividual || turmaGraduacaoBean.solicitacao.turmaFerias}">
			<tr>
			<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Discentes Solicitantes da ${turmaGraduacaoBean.solicitacao.tipoString}
					<c:if test="${turmaGraduacaoBean.solicitacao.turmaEnsinoIndividual}">
						<ufrn:help img="/img/ajuda.gif">Estes discentes serão matriculados automáticamente na turma criada</ufrn:help>
					</c:if>
					</caption>
					
					<thead>
					<tr>
						<td>Curso</td>
						<td>Discentes</td>
					</tr>
					</thead>
				
					<c:forEach var="discenteSolicitacao" items="#{turmaGraduacaoBean.discentes}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td> ${discenteSolicitacao.discenteGraduacao.curso.descricao} </td>
						<td> ${discenteSolicitacao.discenteGraduacao} </td>
					</tr>
					</c:forEach>
					
				</table>
			</td>
			</tr>
		</c:if>
	
	
		<tfoot>
			<tr>
				<td colspan="2">
					<c:if test="${!turmaGraduacaoBean.atendimentoSolicitacao && turmaGraduacaoBean.obj.id == 0}">
						<h:commandButton value="<< Selecionar Outro Componente" action="#{ turmaGraduacaoBean.formSelecaoComponente }" id="voltar"/>
					</c:if>
					
					<c:if test="${ turmaGraduacaoBean.confirmButton eq 'Alterar'}">
						<h:commandButton value="<< Selecionar Outra Turma" action="#{ buscaTurmaBean.telaBuscaGeral }" id="voltarAlteraTurma"/>
					</c:if>
					
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaGraduacaoBean.cancelar }" id="cancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{ turmaGraduacaoBean.submeterDadosGerais }" id="proximoPasso" onclick="return validaInputVagas()"/>
				</td>
			</tr>
		</tfoot>
		</table>
	</h:form>
	
	<br>
	<center><h:graphicImage url="/img/required.gif" style="vertical-align: top;" /><span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
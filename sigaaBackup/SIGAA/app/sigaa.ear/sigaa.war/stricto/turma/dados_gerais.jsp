<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

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
</script>

<style>
	table.subFormulario tr.turmaAgrupadora td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	.left { text-align: left; }
	.rigth { text-align: right; }
	.center { text-align: center; }
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>

<a4j:keepAlive beanName="buscaTurmaBean"/>
<a4j:keepAlive beanName="turmaStrictoSensuBean"/>

<f:view>
	<c:if test="${acesso.chefeDepartamento and not (turmaStrictoSensuBean.portalCoordenadorGraduacao or turmaStrictoSensuBean.portalCoordenadorStricto )}">
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
		<c:if test="${turmaStrictoSensuBean.obj.disciplina.aceitaSubturma }">
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
			<th width="40%"><b>Componente Curricular:</b></th>
			<td>
				<h:outputText value="#{turmaStrictoSensuBean.obj.disciplina.codigo}" /> - <h:outputText value="#{turmaStrictoSensuBean.obj.disciplina.detalhes.nome}" />
			</td>
		</tr>
		<tr>
			<th><b>Tipo do Componente:</b></th>
			<td><h:outputText value="#{turmaStrictoSensuBean.obj.disciplina.tipoComponente.descricao}" /></td>
		</tr>
		<c:if test="${turmaStrictoSensuBean.obj.disciplina.atividade}">
			<tr>
				<th><b>Tipo de Atividade:</b></th>
				<td><h:outputText value="#{turmaStrictoSensuBean.obj.disciplina.tipoAtividade.descricao}" /></td>
			</tr>
		</c:if>
		<c:if test="${turmaStrictoSensuBean.obj.disciplina.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva}">
			<tr>
				<th><b>Cr Total:</th>
				<td><h:outputText value="#{turmaStrictoSensuBean.obj.disciplina.detalhes.crTotal}" /> crs. (${turmaStrictoSensuBean.obj.disciplina.detalhes.chTotal} h)</td>
			</tr>
		</c:if>
		<c:if test="${not (componenteCurricular.obj.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva)}">
			<tr>
				<th><b>CH Total:</b></th>
				<td><h:outputText value="#{turmaStrictoSensuBean.obj.disciplina.detalhes.chTotal}" /> h</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" class="subFormulario">Dados Gerais da Turma</td>
		</tr>
		<tr>
			<th><b>Tipo da Turma:</b></th>
			<td>
				<h:outputText value="#{ turmaStrictoSensuBean.obj.tipoString }"  />
			</td>
		</tr>
		<tr>
			<th><b>Modalidade:</b></th>
			<td>
				<c:choose>
					<c:when test="${ !turmaStrictoSensuBean.turmaEad }">Presencial</c:when>
					<c:otherwise>A Distância</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<th class="required">Ano-Período:</th>
			<td>
				<c:choose>
					<c:when test="${ turmaStrictoSensuBean.obj.id == 0}">
						<a4j:region>
							<h:inputText value="#{turmaStrictoSensuBean.obj.ano}" maxlength="4" size="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }">
								<a4j:support event="onchange" reRender="painelSubTurmas, dataInicioTurma, dataFimTurma" actionListener="#{turmaStrictoSensuBean.atualizarDataInicioFim}" />
							</h:inputText>
							-
							<h:inputText value="#{turmaStrictoSensuBean.obj.periodo}" maxlength="1" size="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }">
								<a4j:support event="onchange" reRender="painelSubTurmas, dataInicioTurma, dataFimTurma" actionListener="#{turmaStrictoSensuBean.atualizarDataInicioFim}" />
							</h:inputText>
							
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
							
						</a4j:region>
					</c:when>
					<c:otherwise>
						<h:outputText value="#{turmaStrictoSensuBean.obj.ano}" />-<h:outputText value="#{turmaStrictoSensuBean.obj.periodo}" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:if test="${turmaStrictoSensuBean.obj.campusObrigatorio || acesso.dae}">
			<tr>
			<th class="${ (turmaStrictoSensuBean.obj.campusObrigatorio && turmaStrictoSensuBean.obj.aberta) ? 'required' : '' }">Campus:</th>
			<td>
				<h:selectOneMenu id="campus" value="#{turmaStrictoSensuBean.obj.campus.id}" disabled="#{!turmaStrictoSensuBean.obj.aberta}">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- "  />
					<f:selectItems value="#{campusIes.allCampusCombo}"/>
				</h:selectOneMenu>
			</td>
			</tr>
		</c:if>
		<c:if test="${ turmaStrictoSensuBean.editarCodigoTurma}">
			<tr>
				<th class="required">Código da Turma: </th>
				<td>
					<h:inputText id="codigo" value="#{ turmaStrictoSensuBean.obj.codigo }" size="4" disabled="#{(!turmaStrictoSensuBean.passivelEdicao || turmaStrictoSensuBean.matriculada) && !turmaStrictoSensuBean.turmaEad}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${ !turmaStrictoSensuBean.turmaEad }">
			<tr>
				<th class="required">Capacidade de Alunos:</th>
				<td>
					<h:inputText id="vagas" value="#{ turmaStrictoSensuBean.obj.capacidadeAluno }"  size="4" maxlength="3" 
					disabled="#{!(turmaStrictoSensuBean.passivelEdicao || turmaStrictoSensuBean.adicionarOutrasReservas)}"  
					onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>
			<tr>
				<th class="required">Local:</th>
				<td>
					<h:inputText id="local" value="#{ turmaStrictoSensuBean.obj.local }" maxlength="50" size="50" disabled="#{!turmaStrictoSensuBean.obj.aberta}"/>
				</td>
			</tr>
			<c:if test="${turmaStrictoSensuBean.turmaStricto}">
				<tr>
					<th>Observações ao Aluno: 
					<ufrn:help img="/img/ajuda.gif" width="320">Utilize este campo para colocar observações para o aluno realizar a matricula
					Por exemplo: nas disciplinas de tópicos especiais pode ser adicionado a informação de qual será o assunto abordado, estas observações aparecerão no histórico ao lado do nome da disciplina.</ufrn:help>
					<br/>
					<span style="font-size: xx-small"></span>
					</th>
					<td>
						<h:inputTextarea id="observacoes" value="#{ turmaStrictoSensuBean.obj.observacao }" rows="2" cols="50" disabled="#{!turmaStrictoSensuBean.obj.aberta && turmaStrictoSensuBean.portalCoordenadorStricto}"/>
					</td>
				</tr>
			</c:if> 
		</c:if>
		<tr>
			<th class="required">Início:</th>
			<td>
				<t:inputCalendar value="#{turmaStrictoSensuBean.obj.dataInicio}"  renderAsPopup="true" size="10" maxlength="10" id="dataInicioTurma"
				popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)" popupDateFormat="dd/MM/yyyy"
				disabled="#{ !((turmaStrictoSensuBean.passivelEdicao && turmaStrictoSensuBean.obj.disciplina.modulo) || turmaStrictoSensuBean.turmaEad || acesso.dae || acesso.ppg
				                || (acesso.chefeDepartamento && turmaStrictoSensuBean.obj.turmaFerias)) }">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required">Fim:<br/>
			</th>
			<td>
				<t:inputCalendar value="#{turmaStrictoSensuBean.obj.dataFim}"  renderAsPopup="true" size="10" maxlength="10" id="dataFimTurma"
				popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)" popupDateFormat="dd/MM/yyyy"
				disabled="#{ !((turmaStrictoSensuBean.passivelEdicao && turmaStrictoSensuBean.obj.disciplina.modulo) || turmaStrictoSensuBean.turmaEad || acesso.dae || acesso.ppg
				                || (acesso.chefeDepartamento && turmaStrictoSensuBean.obj.turmaFerias)) }">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		
		<c:if test="${ not empty turmaStrictoSensuBean.obj.reservas }">
			<tr>
				<td colspan="2" class="subFormulario">Reservas Contempladas</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
				<tr><td align="center">
					<t:dataTable width="100%" var="reserva" value="#{ turmaStrictoSensuBean.obj.reservas }" 
						rowClasses="linhaPar, linhaImpar"
						columnClasses="left, right, right, center"
						id="datatableReservas" forceIdIndex="true">

						<t:column id="col1">
							<f:facet name="header"><f:verbatim><center>Reservas Contempladas</center></f:verbatim></f:facet>
							<h:outputText value="#{ reserva.descricao }" id="desc"/>
						</t:column>

						<t:column id="col4">
							<f:facet name="header"><f:verbatim>Vagas Solicitadas</f:verbatim></f:facet>
							<h:outputText value="#{reserva.vagasSolicitadas}" id="vagasSol"/>
						</t:column>

						<t:column id="col2">
							<f:facet name="header"><f:verbatim>Vagas Reservadas</f:verbatim></f:facet>
							  <c:if test="${turmaStrictoSensuBean.adicionarOutrasReservas || (not empty turmaStrictoSensuBean.solicitacao && turmaStrictoSensuBean.obj.id == 0) || turmaStrictoSensuBean.passivelEdicao}">
							  <h:inputText value="#{reserva.vagasReservadas}" size="4" id="vagasReservadas" onkeyup="return formatarInteiro(this);" disabled="#{reserva.id == 0}"/><!-- reserva.id só será 0(zero) se for turma de ferias -->
							  </c:if>
							  <c:if test="${!(turmaStrictoSensuBean.adicionarOutrasReservas || (not empty turmaStrictoSensuBean.solicitacao && turmaStrictoSensuBean.obj.id == 0) || turmaStrictoSensuBean.passivelEdicao)}">
							  <h:outputText value="#{reserva.vagasReservadas}" id="vagasReservadasOut"/>
							  </c:if>
						</t:column>

						<t:column id="col3">
							<f:facet name="header"></f:facet>
							<c:if test="${(not empty turmaStrictoSensuBean.solicitacao && turmaStrictoSensuBean.obj.id == 0) || turmaStrictoSensuBean.adicionarOutrasReservas}">
								<h:commandLink actionListener="#{ turmaStrictoSensuBean.removerReservaCurso }" id="linkRemover" rendered="#{reserva.podeRemover && reserva.matrizCurricular != null}">
									<f:param value="#{ reserva.id }" name="idMatriz" id="paramReserva"/>
									<f:param value="#{ reserva.idSolicitacao }" name="idSolicitacao" id="paramIdSolicitacao"/>
									<h:graphicImage url="/img/delete.gif" title="Retirar reservas" id="imgDelete" />
								</h:commandLink>
							</c:if>
						</t:column>

					</t:dataTable>
				</td></tr>
				</table>
				</td>
			</tr>
		</c:if>

		<c:if test="${ turmaStrictoSensuBean.obj.graduacao && !(turmaGraduacaoBean.obj.distancia || turmaGraduacaoBean.solicitacao.turmaEnsinoIndividual) }">
		<c:if test="${ empty turmaStrictoSensuBean.obj.reservas }">
			<tr>
				<td colspan="2" style="text-align: center;">
					<font color="red"><i><strong>Não há reservas para esta turma.</strong></i></font>
				</td>
			</tr>
		</c:if>
		</c:if>

		<c:if test="${ not empty turmaStrictoSensuBean.obj.disciplina.aceitaSubturma && not empty turmaStrictoSensuBean.turmasAgrupadoras}">
			<tr>
				<td colspan="2" class="subFormulario">Grupo de subturmas deste componente</td>
			</tr>
			<tr>
				<td colspan="2" >
				<a4j:outputPanel id="painelSubTurmas">
					<table width="100%">
						<thead>
						<tr>
							<th width="10%"></th>
							<th width="15%">Código</th>
							<th width="15%">Horário</th>
							<th width="15%">Capacidade</th>
							<th width="35%">Docentes</th>
						</tr>
						</thead>
						
						<tbody>
						<c:forEach var="agrupadora" items="${turmaStrictoSensuBean.turmasAgrupadoras}">
						<tr class="turmaAgrupadora">
							<td><input type="radio" name="turmaAgrupadoraSelecionada" 
							value="${agrupadora.id}" ${(turmaStrictoSensuBean.obj.turmaAgrupadora.id == agrupadora.id) ? 'checked="checked"' : ''}></td>
							<td style="text-align: left;">${agrupadora.codigo}</td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
							<c:forEach var="subturma" items="${agrupadora.subturmas}">
								<tr>
									<td></td>
									<td style="text-align: left;">&nbsp;&nbsp;&nbsp;&nbsp;${subturma.codigo}</td>
									<td>${subturma.descricaoHorario}</td>
									<td>${subturma.capacidadeAluno}</td>
									<td>${subturma.docentesNomesCh}</td>
								</tr>	
							</c:forEach>
						</c:forEach>
						<tr  class="turmaAgrupadora">
							<td><input type="radio" name="turmaAgrupadoraSelecionada" value="0"></td>
							<td colspan="4">Nenhuma (Não será subturma)</td>
						</tr>
						</tbody>
					</table>
				</a4j:outputPanel>
			</td>
			</tr>
		</c:if>
		<tfoot>
			<tr>
				<td colspan="2">
					<c:if test="${turmaStrictoSensuBean.obj.id == 0}">
						<h:commandButton value="<< Selecionar Outro Componente" action="#{ turmaStrictoSensuBean.formSelecaoComponente }" id="voltar"/>
					</c:if>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaStrictoSensuBean.cancelar }" id="cancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{ turmaStrictoSensuBean.submeterDadosGerais }" id="proximoPasso"/>
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
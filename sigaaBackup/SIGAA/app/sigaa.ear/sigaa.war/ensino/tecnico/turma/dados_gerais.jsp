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


<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Cadastro de Turmas &gt; Dados Gerais </h2>

	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<p>Nesta tela voc� deve entrar com os dados gerais da turma.
			As datas de in�cio e fim das turmas n�o podem ser alteradas e s�o preenchidas automaticamente com as datas de in�cio e fim do per�odo letivo,
			caso as datas n�o sejam preenchidas entre em contato com o ${ configSistema['siglaUnidadeGestoraGraduacao'] }. 	
		</p>
		<c:if test="${turmaTecnicoBean.obj.disciplina.aceitaSubturma }">
			<p><b>Aten��o! Este � um componente que permite a cria��o de subturmas. Por�m, para que isso aconte�a, �
			 necess�rio que as turmas criadas tenham algum hor�rio em comum. </b></p>
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
				<h:outputText value="#{turmaTecnicoBean.obj.disciplina.codigo}" /> - <h:outputText value="#{turmaTecnicoBean.obj.disciplina.detalhes.nome}" />
			</td>
		</tr>
		<tr>
			<th><b>Tipo do Componente:</b></th>
			<td><h:outputText value="#{turmaTecnicoBean.obj.disciplina.tipoComponente.descricao}" /></td>
		</tr>
		<c:if test="${turmaTecnicoBean.obj.disciplina.atividade}">
			<tr>
				<th><b>Tipo de Atividade:</b></th>
				<td><h:outputText value="#{turmaTecnicoBean.obj.disciplina.tipoAtividade.descricao}" /></td>
			</tr>
		</c:if>
		<c:if test="${turmaTecnicoBean.obj.disciplina.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva}">
			<tr>
				<th><b>Cr Total:</th>
				<td><h:outputText value="#{turmaTecnicoBean.obj.disciplina.detalhes.crTotal}" /> crs. (${turmaTecnicoBean.obj.disciplina.detalhes.chTotal} h)</td>
			</tr>
		</c:if>
		<c:if test="${not (componenteCurricular.obj.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva)}">
			<tr>
				<th><b>CH Total:</b></th>
				<td><h:outputText value="#{turmaTecnicoBean.obj.disciplina.detalhes.chTotal}" /> h</td>
			</tr>
		</c:if>
		<tr>
			<td colspan="2" class="subFormulario">Dados Gerais da Turma</td>
		</tr>
		<tr>
			<th><b>Tipo da Turma:</b></th>
			<td>
				<h:outputText value="#{ turmaTecnicoBean.obj.tipoString }"  />
			</td>
		</tr>
		<tr>
			<th><b>Modalidade:</b></th>
			<td>
				<c:choose>
					<c:when test="${ !turmaTecnicoBean.turmaEad }">Presencial</c:when>
					<c:otherwise>A Dist�ncia</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:if test="${especializacaoTurma.utilizadoPelaGestora && not empty especializacaoTurma.especializacoesUnidadeGestoraCombo}">
			<tr>
				<th>Especialidade da Turma de Entrada: </th>
				<td>
					<h:selectOneMenu id="especializacaoEntrada" value="#{turmaTecnicoBean.obj.especializacao.id}">
						<f:selectItem itemValue="0" itemLabel="-- TODAS --"/>
						<f:selectItems value="#{especializacaoTurma.especializacoesUnidadeGestoraCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</c:if>
		<tr>
			<th class="required">Ano-Per�odo:</th>
			<td>
				<c:choose>
					<c:when test="${ turmaTecnicoBean.obj.id == 0}">
						<a4j:region>
							<h:inputText value="#{turmaTecnicoBean.obj.ano}" maxlength="4" size="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }">
								<a4j:support event="onblur" reRender="painelSubTurmas, dataInicioTurma, dataFimTurma" actionListener="#{turmaTecnicoBean.atualizarDataInicioFim}" />
							</h:inputText>
							-
							<h:inputText value="#{turmaTecnicoBean.obj.periodo}" maxlength="1" size="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }">
								<a4j:support event="onblur" reRender="painelSubTurmas, dataInicioTurma, dataFimTurma" actionListener="#{turmaTecnicoBean.atualizarDataInicioFim}" />
							</h:inputText>
							
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
							
						</a4j:region>
					</c:when>
					<c:otherwise>
						<h:outputText value="#{turmaTecnicoBean.obj.ano}" />-<h:outputText value="#{turmaTecnicoBean.obj.periodo}" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:if test="${turmaTecnicoBean.obj.campusObrigatorio || acesso.dae}">
			<tr>
			<th class="${ (turmaTecnicoBean.obj.campusObrigatorio && turmaTecnicoBean.obj.aberta) ? 'required' : '' }">Campus:</th>
			<td>
				<h:selectOneMenu id="campus" value="#{turmaTecnicoBean.obj.campus.id}" disabled="#{!turmaTecnicoBean.obj.aberta}">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- "  />
					<f:selectItems value="#{campusIes.allCampusCombo}"/>
				</h:selectOneMenu>
			</td>
			</tr>
		</c:if>
		<c:if test="${ turmaTecnicoBean.editarCodigoTurma}">
			<tr>
				<th class="required">C�digo da Turma: </th>
				<td>
					<h:inputText id="codigo" value="#{ turmaTecnicoBean.obj.codigo }" size="4" disabled="#{(!turmaTecnicoBean.passivelEdicao || turmaTecnicoBean.matriculada) && !turmaTecnicoBean.turmaEad}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${ !turmaTecnicoBean.turmaEad }">
			<tr>
				<th class="required">Capacidade de Alunos:</th>
				<td>
					<h:inputText id="vagas" value="#{ turmaTecnicoBean.obj.capacidadeAluno }"  size="4" maxlength="3" 
					disabled="#{!(turmaTecnicoBean.passivelEdicao || turmaTecnicoBean.adicionarOutrasReservas)}"  
					onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>
			<tr>
				<th class="required">Local:</th>
				<td>
					<h:inputText id="local" value="#{ turmaTecnicoBean.obj.local }" maxlength="50" size="50" disabled="#{!turmaTecnicoBean.obj.aberta}"/>
				</td>
			</tr>
		</c:if>
		<tr>
			<th>Observa��es ao Aluno: 
			<ufrn:help img="/img/ajuda.gif" width="320">Utilize este campo para colocar observa��es para o aluno realizar a matricula
			Por exemplo: nas disciplinas de t�picos especiais pode ser adicionado a informa��o de qual ser� o assunto abordado, estas observa��es aparecer�o no hist�rico ao lado do nome da disciplina.</ufrn:help>
			<br/>
			<span style="font-size: xx-small"></span>
			</th>
			<td>
				<h:inputTextarea id="observacoes" value="#{ turmaTecnicoBean.obj.observacao }" rows="2" cols="50" disabled="#{!turmaTecnicoBean.obj.aberta && turmaTecnicoBean.portalCoordenadorStricto}"/>
			</td>
		</tr>
		<tr>
			<th class="required">In�cio:</th>
			<td>
				<t:inputCalendar value="#{turmaTecnicoBean.obj.dataInicio}"  renderAsPopup="true" size="10" maxlength="10" id="dataInicioTurma"
				popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
				popupDateFormat="dd/MM/yyyy" disabled="#{ !turmaTecnicoBean.passivelEdicao }">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required">Fim:<br/>
			</th>
			<td>
				<t:inputCalendar value="#{turmaTecnicoBean.obj.dataFim}"  renderAsPopup="true" size="10" maxlength="10" id="dataFimTurma"
				popupTodayString="Hoje" renderPopupButtonAsImage="true" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
				popupDateFormat="dd/MM/yyyy" disabled="#{ !turmaTecnicoBean.passivelEdicao }">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<c:if test="${ not empty turmaTecnicoBean.obj.reservas }">
			<tr>
				<td colspan="2" class="subFormulario">Reservas Contempladas</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
				<tr><td align="center">
					<t:dataTable width="100%" var="reserva" value="#{ turmaTecnicoBean.obj.reservas }" 
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
							  <c:if test="${turmaTecnicoBean.adicionarOutrasReservas || (not empty turmaTecnicoBean.solicitacao && turmaTecnicoBean.obj.id == 0) || turmaTecnicoBean.passivelEdicao}">
							  <h:inputText value="#{reserva.vagasReservadas}" size="4" id="vagasReservadas" onkeyup="return formatarInteiro(this);" disabled="#{reserva.id == 0}"/><!-- reserva.id s� ser� 0(zero) se for turma de ferias -->
							  </c:if>
							  <c:if test="${!(turmaTecnicoBean.adicionarOutrasReservas || (not empty turmaTecnicoBean.solicitacao && turmaTecnicoBean.obj.id == 0) || turmaTecnicoBean.passivelEdicao)}">
							  <h:outputText value="#{reserva.vagasReservadas}" id="vagasReservadasOut"/>
							  </c:if>
						</t:column>

						<t:column id="col3">
							<f:facet name="header"></f:facet>
							<c:if test="${(not empty turmaTecnicoBean.solicitacao && turmaTecnicoBean.obj.id == 0) || turmaTecnicoBean.adicionarOutrasReservas}">
								<h:commandLink actionListener="#{ turmaTecnicoBean.removerReservaCurso }" id="linkRemover" rendered="#{reserva.podeRemover && reserva.matrizCurricular != null}">
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

		<c:if test="${ empty turmaTecnicoBean.obj.reservas }">
			<tr>
				<td colspan="2" style="text-align: center;">
					<font color="red"><i><strong>N�o h� reservas para esta turma.</strong></i></font>
				</td>
			</tr>
		</c:if>

		<c:if test="${ not empty turmaTecnicoBean.obj.disciplina.aceitaSubturma && not empty turmaTecnicoBean.turmasAgrupadoras}">
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
							<th width="15%">C�digo</th>
							<th width="15%">Hor�rio</th>
							<th width="15%">Capacidade</th>
							<th width="35%">Docentes</th>
						</tr>
						</thead>
						
						<tbody>
						<c:forEach var="agrupadora" items="${turmaTecnicoBean.turmasAgrupadoras}">
						<tr class="turmaAgrupadora">
							<td><input type="radio" name="turmaAgrupadoraSelecionada" 
							value="${agrupadora.id}" ${(turmaTecnicoBean.obj.turmaAgrupadora.id == agrupadora.id) ? 'checked="checked"' : ''}></td>
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
							<td colspan="4">Nenhuma (N�o ser� subturma)</td>
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
					<c:if test="${turmaTecnicoBean.obj.id == 0}">
						<h:commandButton value="<< Selecionar Outro Componente" action="#{ turmaTecnicoBean.formSelecaoComponente }" id="voltar"/>
					</c:if>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaTecnicoBean.cancelar }" id="cancelar"/>
					<h:commandButton value="Pr�ximo Passo >>" action="#{ turmaTecnicoBean.submeterDadosGerais }" id="proximoPasso"/>
				</td>
			</tr>
		</tfoot>
		</table>
	</h:form>
	
	<br>
	<center><h:graphicImage url="/img/required.gif" style="vertical-align: top;" /><span
		class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>